package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactXref;

/**
 * Utility class for intact classes and properties
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */

public class IntactUtils {

    public static final int MAX_SHORT_LABEL_LEN = 256;
    public static final int MAX_FULL_NAME_LEN = 1000;
    public static final int MAX_DESCRIPTION_LEN = 4000;
    public static final int MAX_ALIAS_NAME_LEN = 256;

    public static final String CV_LOCAL_SEQ = "cv_local_seq";

    public static final String DATABASE_OBJCLASS="uk.ac.ebi.intact.model.CvDatabase";
    public static final String QUALIFIER_OBJCLASS="uk.ac.ebi.intact.model.CvXrefQualifier";
    public static final String TOPIC_OBJCLASS="uk.ac.ebi.intact.model.CvTopic";

    public static IntactCvTerm createMIDatabase(String name, String MI){
        return createIntactMITerm(name, MI, DATABASE_OBJCLASS);
    }

    public static IntactCvTerm createMIQualifier(String name, String MI){
        return createIntactMITerm(name, MI, QUALIFIER_OBJCLASS);
    }

    public static IntactCvTerm createMITopic(String name, String MI){
        return createIntactMITerm(name, MI, TOPIC_OBJCLASS);
    }

    public static IntactCvTerm createIntactMITerm(String name, String MI, String objclass){
        if (MI != null){
            return new IntactCvTerm(name, new IntactXref(new IntactCvTerm(CvTerm.PSI_MI, null, CvTerm.PSI_MI_MI, DATABASE_OBJCLASS), MI, new IntactCvTerm(Xref.IDENTITY, null, Xref.IDENTITY_MI, QUALIFIER_OBJCLASS)), objclass);
        }
        else {
            return new IntactCvTerm(name, (String)null, (String)null, objclass);
        }
    }
}