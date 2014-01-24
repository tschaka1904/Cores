package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import psidev.psi.mi.jami.utils.collection.AbstractCollectionWrapper;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.listener.InteractionExperimentListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Intact implementation of interaction evidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@Entity
@Table(name = "ia_interaction")
@EntityListeners(value = {InteractionExperimentListener.class})
public class IntactInteractionEvidence extends AbstractIntactPrimaryObject implements InteractionEvidence{
    private Xref imexId;
    private Experiment experiment;
    private String availability;
    private Collection<Parameter> parameters;
    private boolean isInferred = false;
    private Collection<Confidence> confidences;
    private boolean isNegative;

    private Collection<VariableParameterValueSet> variableParameterValueSets;
    private String shortName;
    private Checksum rigid;
    private InteractionChecksumList checksums;
    private InteractionIdentifierList identifiers;
    private InteractionXrefList xrefs;
    private Collection<Annotation> annotations;
    private CvTerm interactionType;
    private Collection<ParticipantEvidence> participants;

    private PersistentXrefList persistentXrefs;
    private Collection<Experiment> experiments;

    public IntactInteractionEvidence(){
    }

    public IntactInteractionEvidence(String shortName){
        this.shortName = shortName;
    }

    public IntactInteractionEvidence(String shortName, CvTerm type){
        this(shortName);
        this.interactionType = type;
    }

    @Column(name = "shortlabel", nullable = false, unique = true)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String name) {
        this.shortName = name;
    }

    @Transient
    public String getRigid() {
        // initialise checksums if not done
        getChecksums();
        return this.rigid != null ? this.rigid.getValue() : null;
    }

    public void setRigid(String rigid) {
        Collection<Checksum> checksums = getChecksums();
        if (rigid != null){
            CvTerm rigidMethod = IntactUtils.createMITopic(Checksum.RIGID, null);
            // first remove old rigid
            if (this.rigid != null){
                checksums.remove(this.rigid);
            }
            this.rigid = new InteractionChecksum(rigidMethod, rigid);
            checksums.add(this.rigid);
        }
        // remove all smiles if the collection is not empty
        else if (!checksums.isEmpty()) {
            ChecksumUtils.removeAllChecksumWithMethod(checksums, Checksum.RIGID_MI, Checksum.RIGID);
            this.rigid = null;
        }
    }

    @Transient
    public Collection<Xref> getIdentifiers() {
        if (identifiers == null){
            initialiseXrefs();
        }
        return this.identifiers;
    }

    @Transient
    public Collection<Xref> getXrefs() {
        if (xrefs == null){
            initialiseXrefs();
        }
        return this.xrefs;
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractionChecksum.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractionChecksum.class)
    public Collection<Checksum> getChecksums() {
        if (checksums == null){
            initialiseChecksums();
        }
        return this.checksums;
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractionAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractionAnnotation.class)
    public Collection<Annotation> getAnnotations() {
        if (annotations == null){
            initialiseAnnotations();
        }
        return this.annotations;
    }

    @Transient
    public Date getUpdatedDate() {
        return getUpdated();
    }

    public void setUpdatedDate(Date updated) {
        setUpdated(updated);
    }

    @Transient
    public Date getCreatedDate() {
        return getCreated();
    }

    public void setCreatedDate(Date created) {
        setCreated(created);
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "interactiontype_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getInteractionType() {
        return this.interactionType;
    }

    public void setInteractionType(CvTerm term) {
        this.interactionType = term;
    }

    @OneToMany( mappedBy = "interaction", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = IntactParticipantEvidence.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactParticipantEvidence.class)
    public Collection<ParticipantEvidence> getParticipants() {
        if (participants == null){
            initialiseParticipants();
        }
        return participants;
    }

    public boolean addParticipant(ParticipantEvidence part) {
        if (part == null){
            return false;
        }
        if (getParticipants().add(part)){
            part.setInteraction(this);
            return true;
        }
        return false;
    }

    public boolean removeParticipant(ParticipantEvidence part) {
        if (part == null){
            return false;
        }
        if (getParticipants().remove(part)){
            part.setInteraction(null);
            return true;
        }
        return false;
    }

    public boolean addAllParticipants(Collection<? extends ParticipantEvidence> participants) {
        if (participants == null){
            return false;
        }

        boolean added = false;
        for (ParticipantEvidence p : participants){
            if (addParticipant(p)){
                added = true;
            }
        }
        return added;
    }

    public boolean removeAllParticipants(Collection<? extends ParticipantEvidence> participants) {
        if (participants == null){
            return false;
        }

        boolean removed = false;
        for (ParticipantEvidence p : participants){
            if (removeParticipant(p)){
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public String toString() {
        return (shortName != null ? shortName+", " : "") + (interactionType != null ? interactionType.toString() : "");
    }

    @Transient
    public String getImexId() {
        // initialise xrefs if not done yet
        getXrefs();
        return this.imexId != null ? this.imexId.getId() : null;
    }

    public void assignImexId(String identifier) {
        // add new imex if not null
        if (identifier != null){
            Collection<Xref> interactionXrefs = getXrefs();
            CvTerm imexDatabase = IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI);
            CvTerm imexPrimaryQualifier = IntactUtils.createMIQualifier(Xref.IMEX_PRIMARY, Xref.IMEX_PRIMARY_MI);
            // first remove old doi if not null
            if (this.imexId != null){
                interactionXrefs.remove(this.imexId);
            }
            this.imexId = new InteractionXref(imexDatabase, identifier, imexPrimaryQualifier);
            interactionXrefs.add(this.imexId);
        }
        else {
            throw new IllegalArgumentException("The imex id has to be non null.");
        }
    }

    @ManyToOne(targetEntity = IntactExperiment.class)
    @JoinColumn( name = "experiment_ac", referencedColumnName = "ac")
    @Target(IntactExperiment.class)
    public Experiment getExperiment() {
        return this.experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public void setExperimentAndAddInteractionEvidence(Experiment experiment) {
        if (this.experiment != null){
            this.experiment.removeInteractionEvidence(this);
        }

        if (experiment != null){
            experiment.addInteractionEvidence(this);
        }
    }

    @ManyToMany(targetEntity=IntactVariableParameterValueSet.class)
    @JoinTable(
            name="ia_interaction2variable_parameters",
            joinColumns=@JoinColumn(name="interaction_ac"),
            inverseJoinColumns=@JoinColumn(name="variable_set_id")
    )
    @Target(IntactVariableParameterValueSet.class)
    public Collection<VariableParameterValueSet> getVariableParameterValues() {

        if (variableParameterValueSets == null){
            initialiseVariableParameterValueSets();
        }
        return this.variableParameterValueSets;
    }

    @OneToMany( mappedBy = "parent", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = InteractionEvidenceConfidence.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractionEvidenceConfidence.class)
    public Collection<Confidence> getConfidences() {
        if (confidences == null){
            initialiseExperimentalConfidences();
        }
        return this.confidences;
    }

    @Transient
    public String getAvailability() {
        return this.availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public boolean isNegative() {
        return this.isNegative;
    }

    public void setNegative(boolean negative) {
        this.isNegative = negative;
    }

    @OneToMany( mappedBy = "parent", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = InteractionEvidenceParameter.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractionEvidenceParameter.class)
    public Collection<Parameter> getParameters() {
        if (parameters == null){
            initialiseExperimentalParameters();
        }
        return this.parameters;
    }

    @Transient
    public boolean isInferred() {
        return this.isInferred;
    }

    public void setInferred(boolean inferred) {
        this.isInferred = inferred;
    }

    @ManyToMany(targetEntity = IntactExperiment.class)
    @JoinTable(
            name = "ia_int2exp",
            joinColumns = {@JoinColumn( name = "interaction_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "experiment_ac" )}
    )
    @Target(IntactExperiment.class)
    @Deprecated
    /**
     * @deprecated see getExperiment instead. Only kept for backward compatibility with intact core
     */
    public Collection<Experiment> getExperiments() {
        if (experiments == null){
            experiments = new ArrayList<Experiment>();
        }
        return experiments;
    }

    protected void processAddedChecksumEvent(Checksum added) {
        if (rigid == null && ChecksumUtils.doesChecksumHaveMethod(added, Checksum.RIGID_MI, Checksum.RIGID)){
            // the rigid is not set, we can set the rigid
            rigid = added;
        }
    }

    protected void processRemovedChecksumEvent(Checksum removed) {
        if (rigid == removed){
            rigid = ChecksumUtils.collectFirstChecksumWithMethod(getChecksums(), Checksum.RIGID_MI, Checksum.RIGID);
        }
    }

    protected void clearPropertiesLinkedToChecksums() {
        rigid = null;
    }

    protected void initialiseExperimentalConfidences(){
        this.confidences = new ArrayList<Confidence>();
    }

    protected void initialiseVariableParameterValueSets(){
        this.variableParameterValueSets = new ArrayList<VariableParameterValueSet>();
    }

    protected void initialiseExperimentalParameters(){
        this.parameters = new ArrayList<Parameter>();
    }

    protected void processAddedXrefEvent(Xref added) {

        // the added identifier is imex and the current imex is not set
        if (imexId == null && XrefUtils.isXrefFromDatabase(added, Xref.IMEX_MI, Xref.IMEX)){
            // the added xref is imex-primary
            if (XrefUtils.doesXrefHaveQualifier(added, Xref.IMEX_PRIMARY_MI, Xref.IMEX_PRIMARY)){
                imexId = added;
            }
        }
    }

    protected void processRemovedXrefEvent(Xref removed) {
        // the removed identifier is pubmed
        if (imexId != null && imexId.equals(removed)){
            imexId = null;
        }
    }

    private void clearPropertiesLinkedToXrefs() {
        imexId = null;
    }

    private void initialiseAnnotations(){
        this.annotations = new ArrayList<Annotation>();
    }

    private void initialiseParticipants(){
        this.participants = new ArrayList<ParticipantEvidence>();
    }

    private void initialiseChecksums(){
        this.checksums = new InteractionChecksumList(null);
        for (Checksum check : this.checksums){
            processAddedChecksumEvent(check);
        }
    }

    private void setChecksums(Collection<Checksum> checksums) {
        this.checksums = new InteractionChecksumList(checksums);
    }

    private void initialiseXrefs(){
        this.identifiers = new InteractionIdentifierList();
        this.xrefs = new InteractionXrefList();
        if (this.persistentXrefs != null){
            for (Xref ref : this.persistentXrefs){
                if (XrefUtils.isXrefAnIdentifier(ref)){
                    this.identifiers.addOnly(ref);
                }
                else{
                    this.xrefs.addOnly(ref);
                    processAddedXrefEvent(ref);
                }
            }
        }
        else{
            this.persistentXrefs = new PersistentXrefList(null);
        }
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractionXref.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractionXref.class)
    private Collection<Xref> getPersistentXrefs() {
        if (persistentXrefs == null){
            persistentXrefs = new PersistentXrefList(null);
        }
        return persistentXrefs;
    }

    private void setPersistentXrefs(Collection<Xref> persistentXrefs) {
        if (persistentXrefs instanceof PersistentXrefList){
            this.persistentXrefs = (PersistentXrefList)persistentXrefs;
        }
        else{
            this.persistentXrefs = new PersistentXrefList(persistentXrefs);
        }
    }

    private void setAnnotations(Collection<Annotation> annotations) {
        this.annotations = annotations;
    }

    private void setExperiments(Collection<Experiment> experiments) {
        this.experiments = experiments;
    }

    private void setVariableParameterValues(Collection<VariableParameterValueSet> variableParameterValueSets) {
        this.variableParameterValueSets = variableParameterValueSets;
    }

    private void setConfidences(Collection<Confidence> confidences) {
        this.confidences = confidences;
    }

    private void setParameters(Collection<Parameter> parameters) {
        this.parameters = parameters;
    }

    private void setParticipants(Collection<ParticipantEvidence> participants) {
        this.participants = participants;
    }

    /**
     * Experimental interaction identifier list
     */
    private class InteractionIdentifierList extends AbstractListHavingProperties<Xref> {
        public InteractionIdentifierList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {
            persistentXrefs.add(added);
        }

        @Override
        protected void processRemovedObjectEvent(Xref removed) {
            persistentXrefs.remove(removed);
        }

        @Override
        protected void clearProperties() {
            persistentXrefs.retainAll(getXrefs());

        }
    }

    /**
     * Experimental interaction Xref list
     */
    private class InteractionXrefList extends AbstractListHavingProperties<Xref> {
        public InteractionXrefList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {

            processAddedXrefEvent(added);
            persistentXrefs.add(added);
        }

        @Override
        protected void processRemovedObjectEvent(Xref removed) {
            processRemovedXrefEvent(removed);
            persistentXrefs.remove(removed);
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToXrefs();
            persistentXrefs.retainAll(getIdentifiers());
        }
    }

    private class InteractionChecksumList extends AbstractCollectionWrapper<Checksum> {
        public InteractionChecksumList(Collection<Checksum> checksums){
            super(checksums);
        }

        @Override
        public boolean add(Checksum xref) {
            if(super.add(xref)){
                processAddedChecksumEvent(xref);
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (super.remove(o)){
                processRemovedChecksumEvent((Checksum) o);
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean hasChanged = false;
            for (Object annot : c){
                if (remove(annot)){
                    hasChanged = true;
                }
            }
            return hasChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            List<Checksum> existingObject = new ArrayList<Checksum>(this);

            boolean removed = false;
            for (Checksum o : existingObject){
                if (!c.contains(o)){
                    if (remove(o)){
                        removed = true;
                    }
                }
            }

            return removed;
        }

        @Override
        public void clear() {
            super.clear();
            clearPropertiesLinkedToChecksums();
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Checksum added) {
            return false;
        }

        @Override
        protected Checksum processOrWrapElementToAdd(Checksum added) {
            return added;
        }
    }
    private class PersistentXrefList extends AbstractCollectionWrapper<Xref> {

        public PersistentXrefList(Collection<Xref> persistentBag){
            super(persistentBag);
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Xref added) {
            if (!(added instanceof InteractionXref)){
                return true;
            }
            else{
                InteractionXref termXref = (InteractionXref)added;
                if (termXref.getParent() != null && termXref.getParent() != this){
                    return true;
                }
            }
            return false;
        }

        @Override
        protected Xref processOrWrapElementToAdd(Xref added) {
            return new InteractionXref(added.getDatabase(), added.getId(), added.getVersion(), added.getQualifier());
        }
    }

}
