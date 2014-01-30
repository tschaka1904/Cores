package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.impl.FullPolymerUpdater;
import psidev.psi.mi.jami.model.Polymer;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;
import uk.ac.ebi.intact.jami.synchronizer.IntactPolymerSynchronizer;

/**
 * Interactor merger based on the jami interactor enricher.
 * It will override properties of object in database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class IntactPolymerMergerOverride extends IntactInteractorBaseMergerOverride<Polymer, IntactPolymer> {

    public IntactPolymerMergerOverride(IntactPolymerSynchronizer intactSynchronizer){
        super(new FullPolymerUpdater<Polymer>(intactSynchronizer));
    }
}
