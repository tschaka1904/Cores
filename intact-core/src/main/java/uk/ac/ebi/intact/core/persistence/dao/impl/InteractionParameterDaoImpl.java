/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.InteractionParameterDao;
import uk.ac.ebi.intact.model.InteractionParameter;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Data Access Object for Interaction parameter.
 *
 * @author Julie Bourbeillon (julie.bourbeillon@labri.fr)
 * @version $Id$
 * @since 1.9.0
 */
@Repository
@Transactional(readOnly = true)
public class InteractionParameterDaoImpl extends IntactObjectDaoImpl<InteractionParameter> implements InteractionParameterDao {

    public InteractionParameterDaoImpl() {
        super(InteractionParameter.class);
    }

    public InteractionParameterDaoImpl( EntityManager entityManager, IntactSession intactSession ) {
        super( InteractionParameter.class, entityManager, intactSession );
    }

    public List<InteractionParameter> getByInteractionAc( String interactionAc ) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "interaction" )
                .add( Restrictions.idEq(interactionAc) ).list();
    }
}
