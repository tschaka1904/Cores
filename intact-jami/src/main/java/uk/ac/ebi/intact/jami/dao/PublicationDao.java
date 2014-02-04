package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.CurationDepth;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;

import java.util.Collection;

/**
 * Publication DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public interface PublicationDao extends IntactBaseDao<IntactPublication> {
    public IntactPublication getByAc(String ac);

    public IntactPublication getByPubmedId(String value);

    public IntactPublication getByDOI(String value);

    public Collection<IntactPublication> getByTitle(String value);

    public Collection<IntactPublication> getByTitleLike(String value);

    public Collection<IntactPublication> getByJournal(String value);

    public Collection<IntactPublication> getByJournalLike(String value);

    public Collection<IntactPublication> getByPublicationDate(String value);

    public Collection<IntactPublication> getByAuthor(String author);

    public Collection<IntactPublication> getByAuthorLike(String author);

    public Collection<IntactPublication> getByXref(String primaryId);

    public Collection<IntactPublication> getByXrefLike(String primaryId);

    public Collection<IntactPublication> getByXref(String dbName, String dbMI, String primaryId);

    public Collection<IntactPublication> getByXrefLike(String dbName, String dbMI, String primaryId);

    public Collection<IntactPublication> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactPublication> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI);

    public Collection<IntactPublication> getByAnnotationTopic(String topicName, String topicMI);

    public Collection<IntactPublication> getByAnnotationTopicAndValue(String topicName, String topicMI, String value);

    public Collection<IntactPublication> getByCurationDepth(CurationDepth depth, int firs, int max);
}
