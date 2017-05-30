/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.datasource.shared.exception;

/**
 * Exception for datasource explore errors.
 * 
 * @author "Mickaël Leduque"
 */
public class ExploreException extends Exception {

    /** UID for serialization */
    private static final long serialVersionUID = 1L;

    public ExploreException() {
    }

    public ExploreException(final String message) {
        super(message);
    }

    public ExploreException(final Throwable cause) {
        super(cause);
    }

    public ExploreException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
