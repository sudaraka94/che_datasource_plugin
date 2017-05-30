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
package org.eclipse.che.ide.ext.datasource.shared;

import org.eclipse.che.dto.shared.DTO;

@DTO
public interface ConnectionTestResultDTO {

    /**
     * The test result, success or failure.
     * 
     * @return true for success, false for failure.
     */
    Status getTestResult();

    String getFailureMessage();

    void setTestResult(Status result);

    void setFailureMessage(String message);

    ConnectionTestResultDTO withTestResult(Status result);

    ConnectionTestResultDTO withFailureMessage(String message);

    enum Status {
        SUCCESS,
        FAILURE
    }
}
