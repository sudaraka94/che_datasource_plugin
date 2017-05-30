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
package org.eclipse.che.ide.ext.datasource.server;

import java.sql.Connection;
import java.sql.SQLException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.datasource.shared.ConnectionTestResultDTO;
import org.eclipse.che.ide.ext.datasource.shared.ConnectionTestResultDTO.Status;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import org.eclipse.che.ide.ext.datasource.shared.ServicePaths;
import org.eclipse.che.ide.ext.datasource.shared.exception.DatabaseDefinitionException;
import com.google.common.base.Throwables;
import com.google.inject.Inject;

/**
 * Service for connection tests on datasources.
 * 
 * @author "Mickaël Leduque"
 */
@Path(ServicePaths.TEST_DATABASE_CONNECTIVITY_PATH)
public class TestConnectionService {

    /** The logger. */
    private static final Logger         LOG = LoggerFactory.getLogger(TestConnectionService.class);

    /** the provider for JDBC connections. */
    private final JdbcConnectionFactory jdbcConnectionFactory;

    @Inject
    public TestConnectionService(final JdbcConnectionFactory jdbcConnectionFactory) {
        this.jdbcConnectionFactory = jdbcConnectionFactory;
    }

    /**
     * Tests a datasource configuration by opening a connection.
     * 
     * @param databaseConfig the datasource configuration
     * @return true iff the connection was successfully created
     * @throws DatabaseDefinitionException is the datasource is not correctly defined
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public String testDatabaseConnectivity(final DatabaseConfigurationDTO databaseConfig) throws DatabaseDefinitionException {
        if (databaseConfig == null) {
            throw new DatabaseDefinitionException("Database definition is 'null'");
        }

        final ConnectionTestResultDTO testResult = DtoFactory.getInstance().createDto(ConnectionTestResultDTO.class);

        try (final Connection connection = this.jdbcConnectionFactory.getDatabaseConnection(databaseConfig)) {
            if (connection != null) {
                testResult.setTestResult(Status.SUCCESS);
            } else {
                testResult.setTestResult(Status.FAILURE);
                // no message
            }
        } catch (final SQLException e) {
            LOG.debug("Connection test failed ; error messages : {} | {}", e.getMessage());
            if (LOG.isTraceEnabled()) {
                LOG.trace("Connection test failed ; exception : {}", Throwables.getStackTraceAsString(e));
            }
            testResult.withTestResult(Status.FAILURE).withFailureMessage(e.getLocalizedMessage());
        } catch (DatabaseDefinitionException e) {
            testResult.withTestResult(Status.FAILURE).withFailureMessage(e.getLocalizedMessage());
        }
        return DtoFactory.getInstance().toJson(testResult);
    }
}
