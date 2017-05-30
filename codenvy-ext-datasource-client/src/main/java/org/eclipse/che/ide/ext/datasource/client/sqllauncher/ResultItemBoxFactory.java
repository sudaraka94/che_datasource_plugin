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
package org.eclipse.che.ide.ext.datasource.client.sqllauncher;

/**
 * Factory for {@link ResultItemBox}.
 * 
 * @author "Mickaël Leduque"
 */
public interface ResultItemBoxFactory {
    /**
     * Creates an instance of {@link ResultItemBox}.
     * 
     * @return a {@link ResultItemBox}
     */
    ResultItemBox createResultItemBox(RequestResultHeader header);
}
