/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.jami.lifecycle.status;

import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.jami.lifecycle.IllegalTransitionException;
import uk.ac.ebi.intact.jami.lifecycle.LifecycleEventListener;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEventType;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleStatus;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;

/**
 */
@Component
public class ReadyForReleaseStatus extends GlobalStatus {

    public ReadyForReleaseStatus() {
        setLifecycleStatus(LifeCycleStatus.READY_FOR_RELEASE);
    }

    /**
     * The publication is made public.
     *
     * @param releasable the releasable
     * @param releaseId mandatory release process ID
     */
    public void release(Releasable releasable, String releaseId) {
        if (canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition ready for release to released cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }
        enfoceMandatory(releaseId);
        changeStatus(releasable, LifeCycleStatus.RELEASED, LifeCycleEventType.RELEASED, releaseId);

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireReleased( releasable );
        }
    }

    public void putOnHold(Releasable releasable, String reason) {
        if (canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition ready for release to accepted on hold cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }
        enfoceMandatory(reason);

        releasable.onHold(reason);
        changeStatus(releasable, LifeCycleStatus.ACCEPTED_ON_HOLD, LifeCycleEventType.PUT_ON_HOLD, reason);

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.firePutOnHold( releasable );
        }
    }
}