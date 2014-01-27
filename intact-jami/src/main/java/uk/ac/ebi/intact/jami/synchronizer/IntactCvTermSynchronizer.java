package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.clone.CvTermCloner;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.IntactContext;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.sequence.SequenceManager;
import uk.ac.ebi.intact.jami.utils.IntactCvTermComparator;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Default synchronizer for cv terms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class IntactCvTermSynchronizer implements IntactDbSynchronizer<CvTerm> {

    private EntityManager entityManager;
    private String objClass;
    private Map<CvTerm, CvTerm> persistedObjects;

    private IntactDbSynchronizer<Alias> aliasSynchronizer;
    private IntactDbSynchronizer<Annotation> annotationSynchronizer;
    private IntactDbSynchronizer<Xref> xrefSynchronizer;

    private static final Log log = LogFactory.getLog(IntactCvTermSynchronizer.class);

    public IntactCvTermSynchronizer(EntityManager entityManager){
        if (entityManager == null){
            throw new IllegalArgumentException("A Cv Term synchronizer needs a non null entity manager");
        }
        this.entityManager = entityManager;
        this.objClass = null;
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<CvTerm, CvTerm>(new IntactCvTermComparator());
        this.aliasSynchronizer = new IntactAliasSynchronizer(this.entityManager, CvTermAlias.class);
        this.annotationSynchronizer = new IntactAnnotationsSynchronizer(this.entityManager, CvTermAnnotation.class);
        this.xrefSynchronizer = new IntactXrefSynchronizer(this.entityManager, CvTermXref.class);
    }

    public IntactCvTermSynchronizer(EntityManager entityManager, String objClass){
        this(entityManager);
        this.objClass = objClass;
    }

    public IntactCvTermSynchronizer(EntityManager entityManager, IntactDbSynchronizer<Alias> aliasSynchronizer,
                                    IntactDbSynchronizer<Annotation> annotationSynchronizer, IntactDbSynchronizer<Xref> xrefSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("A Cv Term synchronizer needs a non null entity manager");
        }
        this.entityManager = entityManager;
        this.objClass = null;
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<CvTerm, CvTerm>(new IntactCvTermComparator());
        this.aliasSynchronizer = aliasSynchronizer != null ? aliasSynchronizer : new IntactAliasSynchronizer(this.entityManager, CvTermAlias.class);
        this.annotationSynchronizer = annotationSynchronizer != null ? annotationSynchronizer : new IntactAnnotationsSynchronizer(this.entityManager, CvTermAnnotation.class);
        this.xrefSynchronizer = xrefSynchronizer != null ? xrefSynchronizer : new IntactXrefSynchronizer(this.entityManager, CvTermXref.class);
    }

    public IntactCvTermSynchronizer(EntityManager entityManager, String objClass,
                                    IntactDbSynchronizer<Alias> aliasSynchronizer, IntactDbSynchronizer<Annotation> annotationSynchronizer,
                                    IntactDbSynchronizer<Xref> xrefSynchronizer){
        this(entityManager, aliasSynchronizer, annotationSynchronizer, xrefSynchronizer);
        this.objClass = objClass;
    }

    public CvTerm find(CvTerm term) throws FinderException {
        return find(term, this.objClass);
    }

    public CvTerm find(CvTerm term, String objClass) throws FinderException {
        Query query;
        if (term == null){
            return null;
        }
        else if (this.persistedObjects.containsKey(term)){
            return this.persistedObjects.get(term);
        }
        else if (term.getMIIdentifier() != null){
            query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                    "join cv.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", term.getMIIdentifier());
            if (objClass != null){
                query.setParameter("objclass", objClass);
            }
        }
        else if (term.getMODIdentifier() != null){
            query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                    "join cv.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimod " +
                    "and x.id = :mod"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimod", CvTerm.PSI_MOD);
            query.setParameter("mod", term.getMODIdentifier());
            if (objClass != null){
                query.setParameter("objclass", objClass);
            }
        }
        else if (term.getPARIdentifier() != null){
            query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                    "join cv.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psipar " +
                    "and x.id = :par"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psipar", CvTerm.PSI_PAR);
            query.setParameter("par", term.getPARIdentifier());
            if (objClass != null){
                query.setParameter("objclass", objClass);
            }
        }
        else if (!term.getIdentifiers().isEmpty()){
            boolean foundSeveral = false;
            for (Xref ref : term.getIdentifiers()){
                query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :db " +
                        "and x.id = :id"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("db", ref.getDatabase().getShortName());
                query.setParameter("id", ref.getId());
                if (objClass != null){
                    query.setParameter("objclass", objClass);
                }

                Collection<CvTerm> cvs = query.getResultList();
                if (cvs.size() == 1){
                    return cvs.iterator().next();
                }
                else if (cvs.size() > 1){
                    foundSeveral = true;
                }
            }
            if (foundSeveral){
                throw new FinderException("The cv "+term.toString() + " has some identifiers that can match several terms in the database and we cannot determine which one is valid.");
            }
            else{
                query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                        "where cv.shortName = :name"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
                query.setParameter("name", term.getShortName().trim().toLowerCase());
                if (objClass != null){
                    query.setParameter("objclass", objClass);
                }
            }
        }
        else{
            query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                    "where cv.shortName = :name"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
            query.setParameter("name", term.getShortName().trim().toLowerCase());
            if (objClass != null){
                query.setParameter("objclass", objClass);
            }
        }
        return (CvTerm) query.getSingleResult();
    }

    public CvTerm persist(CvTerm object) throws FinderException, PersisterException, SynchronizerException {
        // only persist if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        this.persistedObjects.put(object, object);

        IntactCvTerm intactCv = (IntactCvTerm)object;
        // synchronize properties
        synchronizeProperties(intactCv);

        // persist the cv
        this.entityManager.persist(intactCv);

        return intactCv;
    }

    public void synchronizeProperties(CvTerm object) throws FinderException, PersisterException, SynchronizerException {
         synchronizeProperties((IntactCvTerm)object);
    }

    public CvTerm synchronize(CvTerm cv, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
         return synchronize(cv, this.objClass, persist, merge);
    }

    public void clearCache() {
        this.persistedObjects.clear();
    }

    protected CvTerm synchronize(CvTerm cv, String objClass, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (this.persistedObjects.containsKey(cv)){
            return this.persistedObjects.get(cv);
        }

        if (!(cv instanceof IntactCvTerm)){
            IntactCvTerm newCv = new IntactCvTerm(cv.getShortName());
            CvTermCloner.copyAndOverrideCvTermProperties(cv, newCv);
            newCv.setObjClass(objClass);

            CvTerm retrievedCv = findOrPersist(newCv, objClass, persist);
            this.persistedObjects.put(retrievedCv, retrievedCv);

            return retrievedCv;
        }
        else{
            IntactCvTerm intactType = (IntactCvTerm)cv;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                // first synchronize properties before merging
                synchronizeProperties(intactType);
                // merge
                if (merge){
                    CvTerm newTopic = this.entityManager.merge(intactType);
                    this.persistedObjects.put(newTopic, newTopic);
                    return newTopic;
                }
                else{
                    this.persistedObjects.put(intactType, intactType);
                    return intactType;
                }
            }
            // retrieve and or persist transient instance
            else if (intactType.getAc() == null){
                // retrieves or persist cv
                CvTerm newTopic = findOrPersist(intactType, objClass, persist);
                this.persistedObjects.put(newTopic, newTopic);
                return newTopic;
            }
            else{
                // only synchronize properties
                synchronizeProperties(cv);
                this.persistedObjects.put(cv, cv);
                return cv;
            }
        }
    }

    protected void synchronizeProperties(IntactCvTerm intactCv) throws FinderException, PersisterException, SynchronizerException {
        // first set objclass
        initialiseObjClass(intactCv);
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactCv);
        // then check full name
        prepareFullName(intactCv);
        // then check def
        prepareDefinition(intactCv);
        // then check aliases
        prepareAliases(intactCv);
        // then check annotations
        prepareAnnotations(intactCv);
        // set identifier for backward compatibility
        initialiseIdentifier(intactCv);
        // then check xrefs
        prepareXrefs(intactCv);
    }

    protected void initialiseIdentifier(IntactCvTerm intactCv) throws SynchronizerException {
        // if xrefs have been initialised, some identifiers may have changed
        if (intactCv.areXrefsInitialized()){
            // first look at PSI-MI
            if (intactCv.getMIIdentifier() != null){
                intactCv.setIdentifier(intactCv.getMIIdentifier());
            }
            // then MOD identifier
            else if (intactCv.getMODIdentifier() != null){
                intactCv.setIdentifier(intactCv.getMODIdentifier());
            }
            // then PAR identifier
            else if (intactCv.getPARIdentifier() != null){
                intactCv.setIdentifier(intactCv.getPARIdentifier());
            }
            // then first identifier
            else if (!intactCv.getIdentifiers().isEmpty()){
                intactCv.setIdentifier(intactCv.getIdentifiers().iterator().next().getId());
            }
            // then generate automatic identifier
            else{
                final IntactContext context = ApplicationContextProvider.getBean(IntactContext.class);
                String prefix = "IA";
                Source institution = null;
                if (context != null){
                    prefix = context.getConfig().getLocalCvPrefix();
                    institution = context.getConfig().getDefaultInstitution();
                }
                if (institution != null){
                    SequenceManager seqManager = ApplicationContextProvider.getBean(SequenceManager.class);
                    if (seqManager == null){
                        throw new SynchronizerException("The Cv identifier listener needs a sequence manager to automatically generate a cv identifier for backward compatibility. No sequence manager bean " +
                                "was found in the spring context.");
                    }
                    seqManager.createSequenceIfNotExists(IntactUtils.CV_LOCAL_SEQ, 1);
                    String nextIntegerAsString = String.valueOf(seqManager.getNextValueForSequence(IntactUtils.CV_LOCAL_SEQ));
                    String identifier = prefix+":" + StringUtils.leftPad(nextIntegerAsString, 4, "0");
                    // set identifier
                    intactCv.setIdentifier(identifier);
                    // add xref
                    intactCv.getIdentifiers().add(new CvTermXref(IntactUtils.createMIDatabase(institution.getShortName(), institution.getMIIdentifier()), identifier, IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI)));
                }
            }
        }
    }

    protected void prepareDefinition(IntactCvTerm intactCv) {
        if (intactCv.getDefinition() == null){
            AnnotationUtils.removeAllAnnotationsWithTopic(intactCv.getAnnotations(), null, "definition");
        }
        else{
            // truncate if necessary
            if (IntactUtils.MAX_DESCRIPTION_LEN < intactCv.getDefinition().length()){
                log.warn("Cv term definition too long: "+intactCv.getDefinition()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
                intactCv.setDefinition(intactCv.getDefinition().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
            }
            Annotation def = AnnotationUtils.collectFirstAnnotationWithTopic(intactCv.getAnnotations(), null, "definition");
            if (def != null){
                if (!intactCv.getDefinition().equalsIgnoreCase(def.getValue())){
                    def.setValue(intactCv.getDefinition());
                }
            }
            else{
                intactCv.getAnnotations().add(new CvTermAnnotation(IntactUtils.createMITopic("definition", null), intactCv.getDefinition()));
            }
        }
    }

    protected void prepareXrefs(IntactCvTerm intactCv) throws FinderException, PersisterException, SynchronizerException {
        if (intactCv.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactCv.getPersistentXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref cvXref = this.xrefSynchronizer.synchronize(xref, false, false);
                // we have a different instance because needed to be synchronized
                if (cvXref != xref){
                    intactCv.getPersistentXrefs().remove(xref);
                    intactCv.getPersistentXrefs().add(cvXref);
                }
            }
        }
    }

    protected void prepareAnnotations(IntactCvTerm intactCv) throws FinderException, PersisterException, SynchronizerException {
        if (intactCv.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactCv.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation cvAnnotation = this.annotationSynchronizer.synchronize(annotation, false, false);
                // we have a different instance because needed to be synchronized
                if (cvAnnotation != annotation){
                    intactCv.getAnnotations().remove(annotation);
                    intactCv.getAnnotations().add(cvAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(IntactCvTerm intactCv) throws FinderException, PersisterException, SynchronizerException {
        if (intactCv.areSynonymsInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactCv.getSynonyms());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias cvAlias = this.aliasSynchronizer.synchronize(alias, false, false);
                // we have a different instance because needed to be synchronized
                if (cvAlias != alias){
                    intactCv.getSynonyms().remove(alias);
                    intactCv.getSynonyms().add(cvAlias);
                }
            }
        }
    }

    protected void prepareFullName(IntactCvTerm intactCv) {
        // truncate if necessary
        if (intactCv.getFullName() != null && IntactUtils.MAX_FULL_NAME_LEN < intactCv.getFullName().length()){
            log.warn("Cv term fullName too long: "+intactCv.getFullName()+", will be truncated to "+ IntactUtils.MAX_FULL_NAME_LEN+" characters.");
            intactCv.setFullName(intactCv.getFullName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void prepareAndSynchronizeShortLabel(CvTerm intactCv) {
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactCv.getShortName().length()){
            log.warn("Cv term shortLabel too long: "+intactCv.getShortName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactCv.setShortName(intactCv.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
        // check if short name already exist, if yes, synchronize
        Query query = this.entityManager.createQuery("select cv from IntactCvTerm cv " +
                "where cv.shortName = :name"+(this.objClass != null ? " and cv.objClass = :objclass" : ""));
        query.setParameter("name", intactCv.getShortName().trim().toLowerCase());
        if (this.objClass != null){
            query.setParameter("objclass", this.objClass);
        }
        List<IntactCvTerm> existingCvs = query.getResultList();
        if (!existingCvs.isEmpty()){
            int max = 1;
            for (IntactCvTerm cv : existingCvs){
                String name = cv.getShortName();
                if (name.contains("-")){
                    String strSuffix = name.substring(name .lastIndexOf("-") + 1, name.length());
                    Matcher matcher = IntactUtils.decimalPattern.matcher(strSuffix);

                    if (matcher.matches()){
                        max = Math.max(max, Integer.parseInt(matcher.group()));
                    }
                }
            }
            String maxString = Integer.toString(max);
            // retruncate if necessary
            if (IntactUtils.MAX_SHORT_LABEL_LEN < intactCv.getShortName().length()+maxString.length()+1){
                log.warn("Cv term shortLabel too long: "+intactCv.getShortName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
                intactCv.setShortName(intactCv.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN-(maxString.length()+1))
                 +"-"+maxString);
            }
            else{
                intactCv.setShortName(intactCv.getShortName()+"-"+maxString);
            }
        }
    }

    protected void initialiseObjClass(IntactCvTerm intactCv) {
        if (this.objClass != null){
            intactCv.setObjClass(this.objClass);
        }
    }

    protected CvTerm findOrPersist(CvTerm cvType, String objClass, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        CvTerm existingInstance = find(cvType, objClass);
        if (existingInstance != null){
            return existingInstance;
        }
        else{
            // synchronize before persisting
            synchronizeProperties(cvType);
            if (persist){
                this.entityManager.persist(cvType);
            }
            return cvType;
        }
    }
}
