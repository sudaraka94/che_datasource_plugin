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

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.ide.ext.datasource.shared.DriversDTO;
import org.eclipse.che.ide.ext.datasource.shared.ServicePaths;

/**
 * A service that lists all available JDBC drivers.
 * 
 * @author "Mickaël Leduque"
 */
@Path(ServicePaths.DATABASE_TYPES_PATH)
@Singleton
public class AvailableDriversService {

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(AvailableDriversService.class);
    // try to load all supported drivers
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOG.debug("postgresql driver not present");
            LOG.trace("postgresql driver not present", e);
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.debug("MySQL driver not present");
            LOG.trace("MySQL driver not present", e);
        }
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            LOG.debug("Oracle driver not present");
            LOG.trace("Oracle driver not present", e);
        }
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.debug("JTDS driver not present");
            LOG.trace("JTDS driver not present", e);
        }
        try {
            Class.forName("com.nuodb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.debug("NuoDB driver not present");
            LOG.trace("NuoDB driver not present", e);
        }
    }

    /**
     * Lists all supported JDBC drivers that are present in the classloader.
     * 
     * @return the list of drivers
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public String getAvailableDatabaseDrivers() {
        final Enumeration<Driver> loadedDrivers = DriverManager.getDrivers();
        final List<String> drivers = new ArrayList<>();
        while (loadedDrivers.hasMoreElements()) {
            Driver driver = loadedDrivers.nextElement();
            drivers.add(driver.getClass().getCanonicalName());
        }
        final DriversDTO driversDTO = DtoFactory.getInstance().createDto(DriversDTO.class).withDrivers(drivers);
        final String msg = DtoFactory.getInstance().toJson(driversDTO);
        LOG.debug(msg);
        return msg;
    }
}
