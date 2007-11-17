package uk.ac.ebi.intact.core.persister.standard;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Institution;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InstitutionPersisterTest extends AbstractPersisterTest
{

    @Before
    public void before() throws Exception {
        beginTransaction();
    }

    @After
    public void after() throws Exception {
        commitTransaction();
    }

    @Test
    public void persistInstitution_default_withHelper() throws Exception {

        Institution institution = getMockBuilder().createInstitution(Institution.MINT_REF, Institution.MINT);
        PersisterHelper.saveOrUpdate(institution);

        beginTransaction();
        Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);

        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());

        commitTransaction();
    }

    @Test
    public void persistInstitution_default() throws Exception {

        Institution institution = getMockBuilder().createInstitution(Institution.MINT_REF, Institution.MINT);
        InstitutionPersister institutionPersister = InstitutionPersister.getInstance();
        institutionPersister.saveOrUpdate(institution);
        institutionPersister.commit();

        beginTransaction();
        Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);

        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());

        commitTransaction();
    }

    @Test
    public void persistInstitution_annotations() throws Exception {

        Institution institution = getMockBuilder().createInstitution(Institution.MINT_REF, Institution.MINT);
        institution.getAnnotations().add(getMockBuilder().createAnnotation("nowhere", CvTopic.CONTACT_EMAIL_MI_REF, CvTopic.CONTACT_EMAIL));

        InstitutionPersister institutionPersister = InstitutionPersister.getInstance();
        institutionPersister.saveOrUpdate(institution);
        institutionPersister.commit();

        beginTransaction();
        Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);

        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());
        Assert.assertEquals(1, reloadedInstitution.getAnnotations().size());

        commitTransaction();
    }

    @Test
    public void persistInstitution_syncWithDatabase() throws Exception {

        Institution institution = getMockBuilder().createInstitution(Institution.MINT_REF, Institution.MINT);
        InstitutionPersister institutionPersister = InstitutionPersister.getInstance();
        institutionPersister.saveOrUpdate(institution);
        institutionPersister.commit();

        beginTransaction();
        Institution reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);
        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());
        commitTransaction();
        
        PersisterContext.getInstance().clear();
        beginTransaction();

        institution = getMockBuilder().createInstitution(Institution.MINT_REF, "mint2");
        institutionPersister = InstitutionPersister.getInstance();
        institutionPersister.saveOrUpdate(institution);
        institutionPersister.commit();

        beginTransaction();
        reloadedInstitution = getDaoFactory().getInstitutionDao().getByXref(Institution.MINT_REF);
        Assert.assertNotNull(reloadedInstitution);
        Assert.assertEquals(1, reloadedInstitution.getXrefs().size());
        Assert.assertEquals(0, reloadedInstitution.getAliases().size());
        commitTransaction();
    }

}