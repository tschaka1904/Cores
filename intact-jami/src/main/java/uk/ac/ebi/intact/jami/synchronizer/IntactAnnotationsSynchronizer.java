package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for annotations
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactAnnotationsSynchronizer<A extends AbstractIntactAnnotation> extends AbstractIntactDbSynchronizer<Annotation, A> {

    private IntactDbSynchronizer<CvTerm, IntactCvTerm> topicSynchronizer;

    private static final Log log = LogFactory.getLog(IntactAnnotationsSynchronizer.class);

    public IntactAnnotationsSynchronizer(EntityManager entityManager, Class<? extends A> annotationClass){
        super(entityManager, annotationClass);
    }

    public A find(Annotation object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(A object) throws FinderException, PersisterException, SynchronizerException {
        // topic first
        CvTerm topic = object.getTopic();
        object.setTopic(getTopicSynchronizer().synchronize(topic, true));

        // check annotation value
        if (object.getValue() != null && object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            log.warn("Annotation value too long: "+object.getValue()+", will be truncated to "+ IntactUtils.MAX_DESCRIPTION_LEN+" characters.");
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }

    public void clearCache() {
        clearCache(this.topicSynchronizer);
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getTopicSynchronizer() {
        if (this.topicSynchronizer == null){
            this.topicSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.TOPIC_OBJCLASS);

            if (getIntactClass().isAssignableFrom(CvTermAnnotation.class)){
                ((IntactCvTermSynchronizer)this.topicSynchronizer).setAnnotationSynchronizer((IntactDbSynchronizer<Annotation, CvTermAnnotation>) this);
            }
        }
        return topicSynchronizer;
    }

    public void setTopicSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> topicSynchronizer) {
        this.topicSynchronizer = topicSynchronizer;
    }

    @Override
    protected Object extractIdentifier(A object) {
        return object.getAc();
    }

    @Override
    protected A instantiateNewPersistentInstance(Annotation object, Class<? extends A> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return  intactClass.getConstructor(CvTerm.class, String.class).newInstance(object.getTopic(), object.getValue());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<Annotation, A>(this));
    }
}
