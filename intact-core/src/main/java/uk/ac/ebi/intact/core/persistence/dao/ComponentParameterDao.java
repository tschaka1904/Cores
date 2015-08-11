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
package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.model.ComponentParameter;
import uk.ac.ebi.intact.annotation.Mockable;

import java.util.List;

/**
 * To access the confidence. 
 *
 * @author Julie Bourbeillon (julie.bourbeillon@labri.fr)
 * @version $Id$
 * @since 1.8.0
 */
@Mockable
public interface ComponentParameterDao  extends IntactObjectDao<ComponentParameter>{

    List<ComponentParameter> getByComponentAc( String componentAc );
}
