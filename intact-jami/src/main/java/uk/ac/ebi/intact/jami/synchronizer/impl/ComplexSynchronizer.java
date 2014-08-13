package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousExactComplexComparator;
import psidev.psi.mi.jami.utils.comparator.participant.UnambiguousModelledParticipantComparator;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ComplexMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleStatus;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * Default synchronizer for complexes
 *
 * NOTE: when we want to persist cooperative effects, we would remove the transcient property in the IntActConmplex
 * and uncomment the prepareCooperativeEffects
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class ComplexSynchronizer extends InteractorSynchronizerTemplate<Complex, IntactComplex>{

    private CollectionComparator<ModelledParticipant> participantsComparator;
    private ComplexExperimentBCSynchronizer experimentBCSynchronizer;

    public ComplexSynchronizer(SynchronizerContext context) {
        super(context, IntactComplex.class);
        this.participantsComparator = new CollectionComparator<ModelledParticipant>(new UnambiguousModelledParticipantComparator());
        this.experimentBCSynchronizer = new ComplexExperimentBCSynchronizer(context);
    }

    @Override
    protected IntactComplex postFilter(Complex term, Collection<IntactComplex> results) {
        Collection<IntactComplex> filteredResults = new ArrayList<IntactComplex>(results.size());
        for (IntactComplex complex : results){
            // we accept empty participants when finding complexes
            if (term.getParticipants().isEmpty()){
                filteredResults.add(complex);
            }
            // same participants
            else if (this.participantsComparator.compare(term.getParticipants(), complex.getParticipants()) == 0){
                filteredResults.add(complex);
            }
        }

        if (filteredResults.size() == 1){
            return filteredResults.iterator().next();
        }
        else{
            return null;
        }
    }

    @Override
    protected Collection<IntactComplex> findByOtherProperties(Complex term, IntactCvTerm existingType, IntactOrganism existingOrganism) {
        Query query;
        if (existingOrganism == null){
            query = getEntityManager().createQuery("select i from IntactComplex i " +
                    "join i.interactorType as t " +
                    "where i.organism is null " +
                    "and size(i.participants) =:participantSize " +
                    "and t.ac = :typeAc");
            query.setParameter("typeAc", existingType.getAc());
            query.setParameter("participantSize", term.getParticipants().size());
        }
        else{
            query = getEntityManager().createQuery("select i from "+getIntactClass().getSimpleName()+" i " +
                    "join i.interactorType as t " +
                    "join i.organism as o " +
                    "where o.ac = :orgAc " +
                    "and size(i.participants) =:participantSize " +
                    "and t.ac = :typeAc");
            query.setParameter("orgAc", existingOrganism.getAc());
            query.setParameter("participantSize", term.getParticipants().size());
            query.setParameter("typeAc", existingType.getAc());
        }
        return query.getResultList();
    }

    @Override
    protected void initialisePersistedObjectMap() {
        super.setPersistedObjects(new TreeMap<Complex, IntactComplex>(new UnambiguousExactComplexComparator()));
        super.setConvertedObjects(new IdentityMap());
    }

    @Override
    public void synchronizeProperties(IntactComplex intactComplex) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactComplex);
        // prepare evidence type
        prepareEvidenceType(intactComplex, true);
        // prepare interaction evidences
        //prepareInteractionEvidences(intactComplex);
        // then check confidences
        prepareConfidences(intactComplex, true);
        // then check parameters
        prepareParameters(intactComplex, true);
        // then check participants
        prepareParticipants(intactComplex, true);
        // then check cooperative effects
        //prepareCooperativeEffects(intactComplex);
        // prepare status
        prepareStatusAndCurators(intactComplex, true);
        // prepare lifecycle
        prepareLifeCycleEvents(intactComplex, true);
        // then prepare experiment for backward compatibility
        prepareExperiments(intactComplex, true);
    }

    @Override
    public void convertPersistableProperties(IntactComplex intactComplex) throws FinderException, PersisterException, SynchronizerException {
        super.convertPersistableProperties(intactComplex);
        // prepare evidence type
        prepareEvidenceType(intactComplex, false);
        // prepare interaction evidences
        //prepareInteractionEvidences(intactComplex);
        // then check confidences
        prepareConfidences(intactComplex, false);
        // then check parameters
        prepareParameters(intactComplex, false);
        // then check participants
        prepareParticipants(intactComplex, false);
        // then check cooperative effects
        //prepareCooperativeEffects(intactComplex);
        // prepare status
        prepareStatusAndCurators(intactComplex, false);
        // prepare lifecycle
        prepareLifeCycleEvents(intactComplex, false);
        // then prepare experiment for backward compatibility
        prepareExperiments(intactComplex, false);
    }

    @Override
    protected void prepareAnnotations(IntactComplex intactInteractor, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteractor.areAnnotationsInitialized()){
            if (AnnotationUtils.collectFirstAnnotationWithTopic(intactInteractor.getAnnotations(), null, "curated-complex") == null){
                intactInteractor.getAnnotations().add(new InteractorAnnotation(IntactUtils.createMITopic("curated-complex", null)));
            }
        }
        super.prepareAnnotations(intactInteractor, enableSynchronization);
    }

    protected void prepareEvidenceType(IntactComplex intactComplex, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

       if (intactComplex.getEvidenceType() != null){
           intactComplex.setEvidenceType(enableSynchronization ?
                   getContext().getDatabaseSynchronizer().synchronize(intactComplex.getEvidenceType(), true) :
                   getContext().getDatabaseSynchronizer().convertToPersistentObject(intactComplex.getEvidenceType()));
       }
    }

    /*protected void prepareInteractionEvidences(IntactComplex intactComplex) throws PersisterException, FinderException, SynchronizerException {
        if (intactComplex.areInteractionEvidencesInitialized()){
            Collection<InteractionEvidence> evidencesToPersist = new ArrayList<InteractionEvidence>(intactComplex.getInteractionEvidences());
            for (InteractionEvidence interaction : evidencesToPersist){
                // do not persist or merge interaction evidences
                InteractionEvidence persistetnInter = getContext().getInteractionSynchronizer().synchronize(interaction, false);
                // we have a different instance because needed to be synchronized
                if (persistetnInter != interaction){
                    intactComplex.getInteractionEvidences().remove(interaction);
                    intactComplex.getInteractionEvidences().add(persistetnInter);
                }
            }
        }
    }*/

    protected void prepareStatusAndCurators(IntactComplex intactComplex, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        // first the status
        CvTerm status = intactComplex.getStatus().toCvTerm();
        intactComplex.setCvStatus(enableSynchronization ?
                getContext().getLifecycleStatusSynchronizer().synchronize(status, true) :
                getContext().getLifecycleStatusSynchronizer().convertToPersistentObject(status));

        // then curator
        User curator = intactComplex.getCurrentOwner();
        // do not persist user if not there
        if (curator != null){
            intactComplex.setCurrentOwner(enableSynchronization ?
                    getContext().getUserReadOnlySynchronizer().synchronize(curator, false) :
                    getContext().getUserReadOnlySynchronizer().convertToPersistentObject(curator));
        }

        // then reviewer
        User reviewer = intactComplex.getCurrentReviewer();
        if (reviewer != null){
            intactComplex.setCurrentReviewer(enableSynchronization ?
                    getContext().getUserReadOnlySynchronizer().synchronize(reviewer, false) :
                    getContext().getUserReadOnlySynchronizer().convertToPersistentObject(reviewer));
        }
    }

    protected void prepareLifeCycleEvents(IntactComplex intactComplex, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactComplex.areLifeCycleEventsInitialized()){
            List<LifeCycleEvent> eventsToPersist = new ArrayList<LifeCycleEvent>(intactComplex.getLifecycleEvents());
            for (LifeCycleEvent event : eventsToPersist){
                // do not persist or merge events because of cascades
                LifeCycleEvent evt = enableSynchronization ?
                        getContext().getComplexLifecycleSynchronizer().synchronize(event, false) :
                        getContext().getComplexLifecycleSynchronizer().convertToPersistentObject(event);
                // we have a different instance because needed to be synchronized
                if (evt != event){
                    intactComplex.getLifecycleEvents().add(intactComplex.getLifecycleEvents().indexOf(event), evt);
                    intactComplex.getLifecycleEvents().remove(event);
                }
            }
        }
    }

    protected void prepareCooperativeEffects(IntactComplex intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactInteraction.areCooperativeEffectsInitialized()){
            Collection<CooperativeEffect> parametersToPersist = new ArrayList<CooperativeEffect>(intactInteraction.getCooperativeEffects());
            for (CooperativeEffect param : parametersToPersist){
                // do not persist or merge parameters because of cascades
                CooperativeEffect expParam = enableSynchronization ?
                        getContext().getCooperativeEffectSynchronizer().synchronize(param, false) :
                        getContext().getCooperativeEffectSynchronizer().convertToPersistentObject(param);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    intactInteraction.getCooperativeEffects().remove(param);
                    intactInteraction.getCooperativeEffects().add(expParam);
                }
            }
        }
    }

    protected void prepareParticipants(IntactComplex intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParticipantsInitialized()){
            Collection<ModelledParticipant> participantsToPersist = new ArrayList<ModelledParticipant>(intactInteraction.getParticipants());
            for (ModelledParticipant participant : participantsToPersist){
                // reinit parent
                participant.setInteraction(intactInteraction);
                // do not persist or merge participants because of cascades
                ModelledParticipant expPart = enableSynchronization ?
                        (ModelledParticipant) getContext().getModelledParticipantSynchronizer().synchronize(participant, false) :
                        (ModelledParticipant) getContext().getModelledParticipantSynchronizer().convertToPersistentObject(participant);
                // we have a different instance because needed to be synchronized
                if (expPart != participant){
                    intactInteraction.getParticipants().remove(participant);
                    intactInteraction.addParticipant(expPart);
                }
            }
        }
    }

    protected void prepareExperiments(IntactComplex intactComplex, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactComplex.areExperimentsInitialized()){

            Collection<Experiment> experimentsToPersist = new ArrayList<Experiment>(intactComplex.getExperiments());
            for (Experiment exp : experimentsToPersist){
                // synchronize publication if not done yet
                if (exp.getPublication() != null){
                    exp.getPublication().getExperiments().clear();
                    Publication syncPub = enableSynchronization ?
                            getContext().getPublicationSynchronizer().synchronize(exp.getPublication(), true) :
                            getContext().getPublicationSynchronizer().convertToPersistentObject(exp.getPublication());
                    // we have a different instance because needed to be synchronized
                    if (syncPub != exp.getPublication()){
                        exp.setPublication(syncPub);
                    }
                }
                // synchronize experiment
                Experiment expPar = enableSynchronization ?
                        this.experimentBCSynchronizer.synchronize(exp, true) :
                        this.experimentBCSynchronizer.convertToPersistentObject(exp);
                // we have a different instance because needed to be synchronized
                if (expPar != exp){
                    intactComplex.getExperiments().remove(exp);
                    intactComplex.getExperiments().add(expPar);
                }
            }
        }
    }

    protected void prepareParameters(IntactComplex intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParametersInitialized()){
            Collection<ModelledParameter> parametersToPersist = new ArrayList<ModelledParameter>(intactInteraction.getModelledParameters());
            for (ModelledParameter param : parametersToPersist){
                // do not persist or merge parameters because of cascades
                ModelledParameter expPar = enableSynchronization ?
                        getContext().getComplexParameterSynchronizer().synchronize(param, false) :
                        getContext().getComplexParameterSynchronizer().convertToPersistentObject(param);
                // we have a different instance because needed to be synchronized
                if (expPar != param){
                    intactInteraction.getModelledParameters().remove(param);
                    intactInteraction.getModelledParameters().add(expPar);
                }
            }
        }
    }

    protected void prepareConfidences(IntactComplex intactInteraction, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteraction.areConfidencesInitialized()){
            List<ModelledConfidence> confsToPersist = new ArrayList<ModelledConfidence>(intactInteraction.getModelledConfidences());
            for (ModelledConfidence confidence : confsToPersist){
                // do not persist or merge confidences because of cascades
                ModelledConfidence expConf = enableSynchronization ?
                        getContext().getComplexConfidenceSynchronizer().synchronize(confidence, false) :
                        getContext().getComplexConfidenceSynchronizer().convertToPersistentObject(confidence);
                // we have a different instance because needed to be synchronized
                if (expConf != confidence){
                    intactInteraction.getModelledConfidences().remove(confidence);
                    intactInteraction.getModelledConfidences().add(expConf);
                }
            }
        }
    }

    @Override
    protected void prepareAndSynchronizeShortLabel(IntactComplex intactInteraction) {
        // first initialise shortlabel if not done
        if (intactInteraction.getShortName() == null){
            intactInteraction.setShortName(IntactUtils.generateAutomaticComplexShortlabelFor(intactInteraction, IntactUtils.MAX_SHORT_LABEL_LEN));
        }

        super.prepareAndSynchronizeShortLabel(intactInteraction);
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ComplexMergerEnrichOnly());
    }

    @Override
    protected IntactComplex instantiateNewPersistentInstance(Complex object, Class<? extends IntactComplex> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactComplex newInteractor = new IntactComplex(object.getShortName());
        InteractorCloner.copyAndOverrideComplexProperties(object, newInteractor, false, false);
        return newInteractor;
    }

    @Override
    public void deleteRelatedProperties(IntactComplex intactParticipant){
        for (Object f : intactParticipant.getParticipants()){
            getContext().getModelledParticipantSynchronizer().delete((ModelledParticipant)f);
        }
        intactParticipant.getParticipants().clear();
    }
}
