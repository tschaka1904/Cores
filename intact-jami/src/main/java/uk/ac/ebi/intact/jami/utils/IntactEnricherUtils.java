package uk.ac.ebi.intact.jami.utils;

import org.apache.commons.collections.CollectionUtils;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.comparator.alias.DefaultAliasComparator;
import psidev.psi.mi.jami.utils.comparator.annotation.DefaultAnnotationComparator;
import psidev.psi.mi.jami.utils.comparator.confidence.DefaultConfidenceComparator;
import psidev.psi.mi.jami.utils.comparator.cv.DefaultCvTermComparator;
import psidev.psi.mi.jami.utils.comparator.experiment.DefaultVariableParameterComparator;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousExactInteractorComparator;
import psidev.psi.mi.jami.utils.comparator.parameter.DefaultParameterComparator;
import psidev.psi.mi.jami.utils.comparator.xref.DefaultXrefComparator;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.util.*;

/**
 * Enricher utils for intact
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02/02/15</pre>
 */

public class IntactEnricherUtils {

    /**
     * Method which will synchronize annotations that are not present in the annotations to enrich, only in the enriched annotations.
     * It will refresh the enriched annotations to have fully initialised annotations before enrichment
     * @param annotationsToEnrich : the collection of annotations to be enriched
     * @param enrichedAnnotations : the collections of annotations fully enriched
     * @param annotationSynchronizer : the annotation sycnrhonizer to be used
     * @return the synchronized annotations which will be added to the annotations to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static void synchronizeAnnotationsToEnrich(Collection<Annotation> annotationsToEnrich,
                                                                        Collection<Annotation> enrichedAnnotations,
                                                                        AnnotationSynchronizer annotationSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<Annotation> annotationsToBeAdded = CollectionUtils.subtract(enrichedAnnotations, annotationsToEnrich);
        if (!annotationsToBeAdded.isEmpty()){
            // filter annotations to be added in case we have mix of topics with MI identifier and with shortlabel only
            Iterator<Annotation> annotIterator = annotationsToBeAdded.iterator();
            while (annotIterator.hasNext()){
                Annotation toBeAdded = annotIterator.next();
                for (Annotation existingAnnot : annotationsToEnrich){
                    if (DefaultAnnotationComparator.areEquals(toBeAdded, existingAnnot)){
                        annotIterator.remove();
                        break;
                    }
                }
            }
        }

        List<Annotation> synchronizedAnnots = new ArrayList<Annotation>(annotationsToBeAdded.size());
        for (Annotation annotation : annotationsToBeAdded){
            // do not persist or merge annotations because of cascades
            Annotation expAnnotation = (Annotation)annotationSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            synchronizedAnnots.add(expAnnotation);
        }

        // remove annotations which have been synchronized from original object
        enrichedAnnotations.removeAll(annotationsToBeAdded);
        enrichedAnnotations.addAll(synchronizedAnnots);
    }

    /**
     * Method which will synchronize aliases that are not present in the aliases to enrich, only in the enriched aliases.
     * It will refresh the enriched aliases to have fully initialised aliases before enrichment
     * @param aliasesToEnrich : the collection of aliases to be enriched
     * @param enrichedAliases : the collections of aliases fully enriched
     * @param aliasSynchronizer : the aliases synchronizer to be used
     * @return the synchronized aliases which will be added to the aliases to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static void synchronizeAliasesToEnrich(Collection<Alias> aliasesToEnrich,
                                                      Collection<Alias> enrichedAliases,
                                                      AliasSynchronizer aliasSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<Alias> aliasesToBeAdded = CollectionUtils.subtract(enrichedAliases, aliasesToEnrich);
        if (!aliasesToBeAdded.isEmpty()){
            // filter aliases to be added in case we have mix of types with MI identifier and with shortlabel only
            Iterator<Alias> aliasIterator = aliasesToBeAdded.iterator();
            while (aliasIterator.hasNext()){
                Alias toBeAdded = aliasIterator.next();
                for (Alias existingAlias : aliasesToEnrich){
                    if (DefaultAliasComparator.areEquals(toBeAdded, existingAlias)){
                        aliasIterator.remove();
                        break;
                    }
                }
            }
        }

        List<Alias> synchronizedAliases = new ArrayList<Alias>(aliasesToBeAdded.size());
        for (Alias annotation : aliasesToBeAdded){
            // do not persist or merge aliases because of cascades
            Alias expAlias = (Alias)aliasSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            synchronizedAliases.add(expAlias);
        }

        // remove annotations which have been synchronized from original object
        enrichedAliases.removeAll(aliasesToBeAdded);
        enrichedAliases.addAll(synchronizedAliases);
    }

    /**
     * Method which will synchronize variable parameters that are not present in the variable parameters to enrich, only in the enriched variable parameters.
     * It will refresh the enriched variable parameters to have fully initialised variable parameters before enrichment
     * @param parametersToEnrich : the collection of variable parameters to be enriched
     * @param enrichedParameters : the collections of variable parameters fully enriched
     * @param vPSynchronizer : the variable parameters synchronizer to be used
     * @return the synchronized variable parameters which will be added to the variable parameters to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static void synchronizeVariableParametersToEnrich(Collection<VariableParameter> parametersToEnrich,
                                                  Collection<VariableParameter> enrichedParameters,
                                                  IntactDbSynchronizer vPSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<VariableParameter> paramsToBweAdded = CollectionUtils.subtract(enrichedParameters, parametersToEnrich);
        if (!paramsToBweAdded.isEmpty()){
            // filter variable parameters to be added in case we have mix of types with MI identifier and with shortlabel only
            Iterator<VariableParameter> paramIterator = paramsToBweAdded.iterator();
            while (paramIterator.hasNext()){
                VariableParameter toBeAdded = paramIterator.next();
                for (VariableParameter existingAlias : parametersToEnrich){
                    if (DefaultVariableParameterComparator.areEquals(toBeAdded, existingAlias)){
                        paramIterator.remove();
                        break;
                    }
                }
            }
        }

        List<VariableParameter> synchronizedParameters = new ArrayList<VariableParameter>(paramsToBweAdded.size());
        for (VariableParameter annotation : paramsToBweAdded){
            // do not persist or merge variable parameters because of cascades
            VariableParameter cpExp = (VariableParameter)vPSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            synchronizedParameters.add(cpExp);
        }

        // remove variable parameters which have been synchronized from original object
        enrichedParameters.removeAll(paramsToBweAdded);
        enrichedParameters.addAll(synchronizedParameters);
    }

    /**
     * Method which will synchronize cv parents that are not present in the aliases to enrich, only in the enriched cv parents.
     * It will refresh the enriched cv parents to have fully initialised cv parents before enrichment
     * @param parentsToEnrich : the collection of cv parents to be enriched
     * @param enrichedParents : the collections of cv parents fully enriched
     * @param cvSynchronizer : the cv parents synchronizer to be used
     * @return the synchronized cv parents which will be added to the cv parents to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <C extends CvTerm> void synchronizeCvsToEnrich(Collection<C> parentsToEnrich,
                                              Collection<C> enrichedParents,
                                              IntactDbSynchronizer cvSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<C> cvsToBeAdded = CollectionUtils.subtract(enrichedParents, parentsToEnrich);
        if (!cvsToBeAdded.isEmpty()){
            // filter cv parents to be added in case we have mix of types with MI identifier and with shortlabel only
            Iterator<C> cvIterator = cvsToBeAdded.iterator();
            while (cvIterator.hasNext()){
                C toBeAdded = cvIterator.next();
                for (C existingCv : parentsToEnrich){
                    if (DefaultCvTermComparator.areEquals(toBeAdded, existingCv)){
                        cvIterator.remove();
                        break;
                    }
                }
            }
        }

        List<C> synchronizedCvs = new ArrayList<C>(cvsToBeAdded.size());
        for (C cv : cvsToBeAdded){
            // do not persist or merge cv parents because of cascades
            C expCv = (C)cvSynchronizer.synchronize(cv, true);
            // we have a different instance because needed to be synchronized
            synchronizedCvs.add(expCv);
        }

        // remove cv parents which have been synchronized from original object
        enrichedParents.removeAll(cvsToBeAdded);
        enrichedParents.addAll(synchronizedCvs);
    }

    /**
     * Method which will synchronize xrefs that are not present in the xrefs to enrich
     * @param xrefsToEnrich : the collection of xrefs to be enriched
     * @param enrichedXrefs : the collections of xrefs fully enriched
     * @param xrefSynchronizer : the xref sycnrhonizer to be used
     * @return the synchronized xrefs which will be added to the xrefs to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static void synchronizeXrefsToEnrich(Collection<Xref> xrefsToEnrich,
                                                            Collection<Xref> enrichedXrefs,
                                                            XrefSynchronizer xrefSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<Xref> xrefsToBeAdded = CollectionUtils.subtract(enrichedXrefs, xrefsToEnrich);
        if (!xrefsToBeAdded.isEmpty()){
            // filter xrefs to be added in case we have mix of database/qualifier with MI identifier and with shortlabel only
            Iterator<Xref> refIterator = xrefsToBeAdded.iterator();
            while (refIterator.hasNext()){
                Xref toBeAdded = refIterator.next();
                for (Xref existingXref : xrefsToEnrich){
                    if (DefaultXrefComparator.areEquals(toBeAdded, existingXref)){
                        refIterator.remove();
                        break;
                    }
                }
            }
        }

        List<Xref> synchronizedXrefs = new ArrayList<Xref>(xrefsToBeAdded.size());
        for (Xref ref : xrefsToBeAdded){
            // do not persist or merge xrefs because of cascades
            Xref expRef = (Xref)xrefSynchronizer.synchronize(ref, false);
            // we have a different instance because needed to be synchronized
            synchronizedXrefs.add(expRef);
        }

        // remove xrefs which have been synchronized from original object
        enrichedXrefs.removeAll(xrefsToBeAdded);
        enrichedXrefs.addAll(synchronizedXrefs);
    }

    /**
     * Method which will synchronize interactors that are not present in the interactors to enrich
     * @param interactorsToEnrich : the collection of interactors to be enriched
     * @param enrichedInteractors : the collections of interactors fully enriched
     * @param interactorSynchronizer : the interactors sycnrhonizer to be used
     * @return the synchronized interactors which will be added to the xrefs to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static void synchronizeInteractorsToEnrich(Collection<Interactor> interactorsToEnrich,
                                                Collection<Interactor> enrichedInteractors,
                                                IntactDbSynchronizer interactorSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<Interactor> interactorsToBeAdded = CollectionUtils.subtract(enrichedInteractors, interactorsToEnrich);
        if (!interactorsToBeAdded.isEmpty()){
            // filter interactors to be added in case we have mix of database/qualifier with MI identifier and with shortlabel only
            Iterator<Interactor> refIterator = interactorsToBeAdded.iterator();
            while (refIterator.hasNext()){
                Interactor toBeAdded = refIterator.next();
                for (Interactor existingXref : interactorsToEnrich){
                    if (UnambiguousExactInteractorComparator.areEquals(toBeAdded, existingXref)){
                        refIterator.remove();
                        break;
                    }
                }
            }
        }

        List<Interactor> synchronizedInteractors = new ArrayList<Interactor>(interactorsToBeAdded.size());
        for (Interactor ref : interactorsToBeAdded){
            // do not persist or merge interactors because of cascades
            Interactor expRef = (Interactor)interactorSynchronizer.synchronize(ref, true);
            // we have a different instance because needed to be synchronized
            synchronizedInteractors.add(expRef);
        }

        // remove interactors which have been synchronized from original object
        enrichedInteractors.removeAll(interactorsToBeAdded);
        enrichedInteractors.addAll(synchronizedInteractors);
    }

    /**
     * Method which will synchronize confidences that are not present in the confidences to enrich
     * @param confidencesToEnrich : the collection of confidences to be enriched
     * @param enrichedConfidences : the collections of confidences fully enriched
     * @param confidenceSynchronizer : the confidences synchronizer to be used
     * @return the synchronized confidences which will be added to the confidences to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <C extends Confidence> void synchronizeConfidencesToEnrich(Collection<C> confidencesToEnrich,
                                                            Collection<C> enrichedConfidences,
                                                            ConfidenceSynchronizer confidenceSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<C> confidencesToBeAdded = CollectionUtils.subtract(enrichedConfidences, confidencesToEnrich);
        if (!confidencesToBeAdded.isEmpty()){
            // filter confidences to be added in case we have mix of type with MI identifier and with shortlabel only
            Iterator<C> refIterator = confidencesToBeAdded.iterator();
            while (refIterator.hasNext()){
                C toBeAdded = refIterator.next();
                for (C exististingConf : confidencesToEnrich){
                    if (DefaultConfidenceComparator.areEquals(toBeAdded, exististingConf)){
                        refIterator.remove();
                        break;
                    }
                }
            }
        }

        List<C> synchronizedConfidences = new ArrayList<C>(confidencesToBeAdded.size());
        for (C conf : confidencesToBeAdded){
            // do not persist or merge confidences because of cascades
            C expConf = (C)confidenceSynchronizer.synchronize(conf, false);
            // we have a different instance because needed to be synchronized
            synchronizedConfidences.add(expConf);
        }

        // remove confidences which have been synchronized from original object
        enrichedConfidences.removeAll(confidencesToBeAdded);
        enrichedConfidences.addAll(synchronizedConfidences);
    }

    /**
     * Method which will synchronize parameters that are not present in the parameters to enrich
     * @param parametersToEnrich : the collection of parameters to be enriched
     * @param enrichedParameters : the collections of parameters fully enriched
     * @param parameterSynchronizer : the parameters synchronizer to be used
     * @return the synchronized parameters which will be added to the parameters to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <C extends Parameter> void synchronizeParametersToEnrich(Collection<C> parametersToEnrich,
                                                                                Collection<C> enrichedParameters,
                                                                                ParameterSynchronizer parameterSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<C> parametersToBeAdded = CollectionUtils.subtract(enrichedParameters, parametersToEnrich);
        if (!parametersToBeAdded.isEmpty()){
            // filter parameters to be added in case we have mix of type/unit with MI identifier and with shortlabel only
            Iterator<C> paramIterator = parametersToBeAdded.iterator();
            while (paramIterator.hasNext()){
                C toBeAdded = paramIterator.next();
                for (C exististingParam : parametersToEnrich){
                    if (DefaultParameterComparator.areEquals(toBeAdded, exististingParam)){
                        paramIterator.remove();
                        break;
                    }
                }
            }
        }

        List<C> synchronizedParameters = new ArrayList<C>(parametersToBeAdded.size());
        for (C para : parametersToBeAdded){
            // do not persist or merge parameters because of cascades
            C expConf = (C)parameterSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            synchronizedParameters.add(expConf);
        }

        // remove confidences which have been synchronized from original object
        enrichedParameters.removeAll(parametersToBeAdded);
        enrichedParameters.addAll(synchronizedParameters);
    }

    /**
     * Method which will synchronize participants that are not present in the participants to enrich
     * @param participantsToEnrich : the collection of participants to be enriched
     * @param enrichedParticipants : the collections of participants fully enriched
     * @param participantSynchronizer : the participants synchronizer to be used
     * @return the synchronized participants which will be added to the participants to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <C extends Participant> void synchronizeParticipantsToEnrich(Collection<C> participantsToEnrich,
                                                                                    Collection<C> enrichedParticipants,
                                                                                    ParticipantSynchronizer participantSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<C> participantsToBeAdded = CollectionUtils.subtract(enrichedParticipants, participantsToEnrich);

        List<C> synchronizedParticipants = new ArrayList<C>(participantsToBeAdded.size());
        for (C para : participantsToBeAdded){
            // do not persist or merge participants because of cascades
            C expParticipant = (C)participantSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            synchronizedParticipants.add(expParticipant);
        }

        // remove participants which have been synchronized from original object
        enrichedParticipants.removeAll(participantsToBeAdded);
        enrichedParticipants.addAll(synchronizedParticipants);
    }

    /**
     * Method which will synchronize interactions that are not present in the interactions to enrich
     * @param interactionsToEnrich : the collection of interactions to be enriched
     * @param enrichedInteractions : the collections of interactions fully enriched
     * @param interactionSynchronizer : the interactions synchronizer to be used
     * @return the synchronized interactions which will be added to the interactions to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static void synchronizeInteractionsToEnrich(Collection<InteractionEvidence> interactionsToEnrich,
                                                                                        Collection<InteractionEvidence> enrichedInteractions,
                                                                                        IntactDbSynchronizer interactionSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<InteractionEvidence> interactionsToBeAdded = CollectionUtils.subtract(enrichedInteractions, interactionsToEnrich);

        List<InteractionEvidence> synchronizedInteractions = new ArrayList<InteractionEvidence>(interactionsToBeAdded.size());
        for (InteractionEvidence para : interactionsToBeAdded){
            // do not persist or merge interactions because of cascades
            InteractionEvidence expInteraction = (InteractionEvidence)interactionSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            synchronizedInteractions.add(expInteraction);
        }

        // remove interactions which have been synchronized from original object
        enrichedInteractions.removeAll(interactionsToBeAdded);
        enrichedInteractions.addAll(synchronizedInteractions);
    }

    /**
     * Method which will synchronize experiments that are not present in the experiments to enrich
     * @param experimentsToEnrich : the collection of experiments to be enriched
     * @param enrichedExperiments : the collections of experiments fully enriched
     * @param experimentSynchronizer : the experiments synchronizer to be used
     * @return the synchronized experiments which will be added to the experiments to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static void synchronizeExperimentsToEnrich(Collection<Experiment> experimentsToEnrich,
                                                       Collection<Experiment> enrichedExperiments,
                                                       IntactDbSynchronizer experimentSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<Experiment> experimentsToBeAdded = CollectionUtils.subtract(enrichedExperiments, experimentsToEnrich);

        List<Experiment> synchronizedInteractions = new ArrayList<Experiment>(experimentsToBeAdded.size());
        for (Experiment para : experimentsToBeAdded){
            // do not persist or merge experiments because of cascades
            Experiment expInteraction = (Experiment)experimentSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            synchronizedInteractions.add(expInteraction);
        }

        // remove experiments which have been synchronized from original object
        enrichedExperiments.removeAll(experimentsToBeAdded);
        enrichedExperiments.addAll(synchronizedInteractions);
    }

    /**
     * Method which will synchronize ranges that are not present in the ranges to enrich
     * @param rangesToEnrich : the collection of ranges to be enriched
     * @param enrichedRanges : the collections of ranges fully enriched
     * @param rangeSynchronizer : the ranges synchronizer to be used
     * @return the synchronized ranges which will be added to the ranges to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static void synchronizeRangesToEnrich(Collection<Range> rangesToEnrich,
                                                                                  Collection<Range> enrichedRanges,
                                                                                  IntactDbSynchronizer rangeSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<Range> rangesToBeAdded = CollectionUtils.subtract(enrichedRanges, rangesToEnrich);

        List<Range> synchronizedRanges = new ArrayList<Range>(rangesToBeAdded.size());
        for (Range para : rangesToBeAdded){
            // do not persist or merge ranges because of cascades
            Range expInteraction = (Range)rangeSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            synchronizedRanges.add(expInteraction);
        }

        // remove ranges which have been synchronized from original object
        enrichedRanges.removeAll(rangesToBeAdded);
        enrichedRanges.addAll(synchronizedRanges);
    }

    /**
     * Method which will synchronize features that are not present in the features to enrich
     * @param featuresToEnrich : the collection of features to be enriched
     * @param enrichedFeatures : the collections of features fully enriched
     * @param featureSynchronizer : the feature synchronizer to be used
     * @return the synchronized features which will be added to the features to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <F extends Feature> void synchronizeFeaturesToEnrich(Collection<F> featuresToEnrich,
                                                 Collection<F> enrichedFeatures,
                                                 IntactDbSynchronizer featureSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<F> featuresToBeAdded = CollectionUtils.subtract(enrichedFeatures, featuresToEnrich);

        List<F> synchronizedfeatures = new ArrayList<F>(featuresToBeAdded.size());
        for (F para : featuresToBeAdded){
            // do not persist or merge ranges because of cascades
            F expInteraction = (F)featureSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            synchronizedfeatures.add(expInteraction);
        }

        // remove ranges which have been synchronized from original object
        enrichedFeatures.removeAll(featuresToBeAdded);
        enrichedFeatures.addAll(synchronizedfeatures);
    }

    /**
     * Method which will synchronize causal relationships that are not present in the causal relationships to enrich
     * @param crToEnrich : the collection of causal relationships to be enriched
     * @param enrichedCr : the collections of causal relationships fully enriched
     * @param crSynchronizer : the causal relationships synchronizer to be used
     * @return the synchronized causal relationships which will be added to the causal relationships to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <F extends CausalRelationship> void synchronizeCausalRelationshipsToEnrich(Collection<F> crToEnrich,
                                                                       Collection<F> enrichedCr,
                                                                       IntactDbSynchronizer crSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<F> crToBeAdded = CollectionUtils.subtract(enrichedCr, crToEnrich);

        List<F> synchronizedCr = new ArrayList<F>(crToBeAdded.size());
        for (F para : crToBeAdded){
            // do not persist or merge causal relationships because of cascades
            F expInteraction = (F)crSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            synchronizedCr.add(expInteraction);
        }

        // remove causal relationships which have been synchronized from original object
        enrichedCr.removeAll(crToBeAdded);
        enrichedCr.addAll(synchronizedCr);
    }

    /**
     * Method which will synchronize lifecycle that are not present in the lifecycle to enrich
     * @param lcToEnrich : the collection of lifecycle to be enriched
     * @param enrichedLs : the collections of lifecycle fully enriched
     * @param lcSynchronizer : the lifecycle synchronizer to be used
     * @return the synchronized lifecycle which will be added to the lifecycle to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <C extends LifeCycleEvent> void synchronizeLifeCycleEventsToEnrich(Collection<C> lcToEnrich,
                                                                                        Collection<C> enrichedLs,
                                                                                        IntactDbSynchronizer lcSynchronizer) throws PersisterException, FinderException, SynchronizerException {
        Collection<C> lcToBeAdded = CollectionUtils.subtract(enrichedLs, lcToEnrich);

        List<C> synchronizedLc = new ArrayList<C>(lcToBeAdded.size());
        for (C para : lcToBeAdded){
            // do not persist or merge lifecycle because of cascades
            C expLc = (C)lcSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            synchronizedLc.add(expLc);
        }

        // remove lifecycle which have been synchronized from original object
        enrichedLs.removeAll(lcToBeAdded);
        enrichedLs.addAll(synchronizedLc);
    }
}