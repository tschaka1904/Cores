package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.ExperimentEnricher;
import psidev.psi.mi.jami.enricher.InteractionEvidenceEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEnricher;
import psidev.psi.mi.jami.enricher.impl.FullInteractionEvidenceUpdater;
import psidev.psi.mi.jami.enricher.listener.InteractionEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.model.InteractionEvidence;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;

/**
 * Interaction evidence merger based on the jami interaction evidence enricher.
 * It will override properties loaded from the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class IntactInteractionEvidenceMergerOverride extends IntactDbMergerOverride<InteractionEvidence,IntactInteractionEvidence> implements InteractionEvidenceEnricher {

    public IntactInteractionEvidenceMergerOverride(){
        super(new FullInteractionEvidenceUpdater());
    }

    protected IntactInteractionEvidenceMergerOverride(InteractionEvidenceEnricher interactorEnricher){
        super(interactorEnricher);
    }

    @Override
    protected InteractionEvidenceEnricher getBasicEnricher() {
        return (InteractionEvidenceEnricher)super.getBasicEnricher();
    }

    public ExperimentEnricher getExperimentEnricher() {
        return getBasicEnricher().getExperimentEnricher();
    }

    public void setExperimentEnricher(ExperimentEnricher experimentEnricher) {
        getBasicEnricher().setExperimentEnricher(experimentEnricher);
    }

    public ParticipantEnricher<ParticipantEvidence, FeatureEvidence> getParticipantEnricher() {
        return getBasicEnricher().getParticipantEnricher();
    }

    public void setParticipantEnricher(ParticipantEnricher<ParticipantEvidence, FeatureEvidence> participantEnricher) {
        getBasicEnricher().setParticipantEnricher(participantEnricher);
    }

    public void setCvTermEnricher(CvTermEnricher<CvTerm> cvTermEnricher) {
        getBasicEnricher().setCvTermEnricher(cvTermEnricher);
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return getBasicEnricher().getCvTermEnricher();
    }

    public InteractionEnricherListener<InteractionEvidence> getInteractionEnricherListener() {
        return getBasicEnricher().getInteractionEnricherListener();
    }

    public void setInteractionEnricherListener(InteractionEnricherListener<InteractionEvidence> listener) {
        getBasicEnricher().setInteractionEnricherListener(listener);
    }

    @Override
    public IntactInteractionEvidence merge(IntactInteractionEvidence int1, IntactInteractionEvidence int2) {
        // reset parent to source parent
        int2.setExperiment(int1.getExperiment());

        return super.merge(int1, int2);
    }
}

