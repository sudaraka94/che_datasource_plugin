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
package org.eclipse.che.ide.ext.datasource.client.common.interaction;

/**
 * Callback called when the user clicks on "cancel" in the confirmation window.
 * 
 * @author "Mickaël Leduque"
 */
public interface CancelCallback {

    /** Action called when the user click on cancel. */
    void cancelled();

}
