/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.util;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IllegalLabelFormatException extends Exception {

    private static final String LABEL_MSG = "Short label with invalid format: ";

    public IllegalLabelFormatException(String label) {
        super(LABEL_MSG +label);
    }

    public IllegalLabelFormatException(String label, Throwable cause) {
        super(LABEL_MSG +label, cause);
    }

    public IllegalLabelFormatException(String label, String message) {
        super(message+" ("+label+")");
    }

    public IllegalLabelFormatException(String label, String message, Throwable cause) {
        super(message+" ("+label+")", cause);
    }
}