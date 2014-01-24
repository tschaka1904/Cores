package uk.ac.ebi.intact.jami.finder;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;
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

public class IntactAnnotationsSynchronizer implements IntactDbSynchronizer<Annotation>{

    private IntactDbSynchronizer<CvTerm> topicSynchronizer;
    private EntityManager entityManager;
    private Class<? extends AbstractIntactAnnotation> annotationClass;

    public IntactAnnotationsSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactAnnotation> annotationClass){
        if (entityManager == null){
            throw new IllegalArgumentException("Annotation synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (annotationClass == null){
            throw new IllegalArgumentException("Annotation synchronizer needs a non null annotation class");
        }
        this.annotationClass = annotationClass;
        this.topicSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
    }

    public Annotation find(Annotation object) throws FinderException {
        return null;
    }

    public Annotation persist(Annotation object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactAnnotation) object);
        this.entityManager.persist(object);
        return object;
    }

    public void synchronizeProperties(Annotation object) throws FinderException, PersisterException, SynchronizerException {
         synchronizeProperties((AbstractIntactAnnotation)object);
    }

    public Annotation synchronize(Annotation object) throws FinderException, PersisterException, SynchronizerException {
        if (!object.getClass().isAssignableFrom(this.annotationClass)){
            AbstractIntactAnnotation newAnnotation = null;
            try {
                newAnnotation = this.annotationClass.getConstructor(CvTerm.class, String.class).newInstance(object.getTopic(), object.getValue());
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.annotationClass, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.annotationClass, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.annotationClass, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.annotationClass, e);
            }

            // synchronize properties
            synchronizeProperties(newAnnotation);
            this.entityManager.persist(newAnnotation);
            return newAnnotation;
        }
        else{
            AbstractIntactAnnotation intactType = (AbstractIntactAnnotation)object;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                // synchronize properties
                synchronizeProperties(intactType);
                // merge
                return this.entityManager.merge(intactType);
            }
            else{
                // synchronize properties
                synchronizeProperties(intactType);
                return intactType;
            }
        }
    }

    public void clearCache() {
        this.topicSynchronizer.clearCache();
    }

    protected void synchronizeProperties(AbstractIntactAnnotation object) throws PersisterException, SynchronizerException {
        CvTerm type = object.getTopic();
        try {
            object.setTopic(topicSynchronizer.synchronize(type));
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the annotation because could not synchronize its annotation topic.");
        }
        // check annotation value
        if (object.getValue() != null && object.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
            object.setValue(object.getValue().substring(0,IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }
}
