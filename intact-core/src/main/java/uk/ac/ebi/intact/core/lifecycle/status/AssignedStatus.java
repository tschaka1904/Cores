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
package uk.ac.ebi.intact.core.lifecycle.status;

import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.core.lifecycle.LifecycleEventListener;
import uk.ac.ebi.intact.core.lifecycle.LifecycleTransition;
import uk.ac.ebi.intact.model.CvLifecycleEventType;
import uk.ac.ebi.intact.model.CvPublicationStatusType;
import uk.ac.ebi.intact.model.Publication;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class AssignedStatus extends GlobalStatus {

    public AssignedStatus() {
        setStatusType( CvPublicationStatusType.ASSIGNED );
    }

    /**
     * The curator starts to work on a specific publication.
     *
     * @param publication the publication
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.ASSIGNED, toStatus = CvPublicationStatusType.CURATION_IN_PROGRESS)
    public void startCuration(Publication publication) {
        changeStatus(publication, CvPublicationStatusType.CURATION_IN_PROGRESS, CvLifecycleEventType.CURATION_STARTED, "");

        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireCurationInProgress( publication );
        }
    }

    /**
     * The curator decides not to work on the publication.
     *
     * @param publication the publication
     * @param reason a mandatory reason
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.ASSIGNED, toStatus = CvPublicationStatusType.RESERVED)
    public void unassign(Publication publication, String reason) {
        enfoceMandatory(reason);
        changeStatus(publication, CvPublicationStatusType.RESERVED, CvLifecycleEventType.ASSIGNMENT_DECLINED, reason);

        // notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireAssignentDeclined( publication );
        }
    }
}
