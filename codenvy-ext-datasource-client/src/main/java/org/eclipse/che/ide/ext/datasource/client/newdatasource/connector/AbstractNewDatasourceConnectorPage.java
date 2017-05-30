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
package org.eclipse.che.ide.ext.datasource.client.newdatasource.connector;

import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.Notification.Type;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.wizard.AbstractWizardPage;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.datasource.client.DatasourceClientService;
import org.eclipse.che.ide.ext.datasource.client.newdatasource.NewDatasourceWizardMessages;
import org.eclipse.che.ide.ext.datasource.shared.ConnectionTestResultDTO;
import org.eclipse.che.ide.ext.datasource.shared.ConnectionTestResultDTO.Status;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseConfigurationDTO;
import org.eclipse.che.ide.ext.datasource.shared.DatabaseType;
import org.eclipse.che.ide.ext.datasource.shared.TextDTO;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.StringUnmarshaller;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.validation.constraints.NotNull;

public abstract class AbstractNewDatasourceConnectorPage extends AbstractWizardPage<DatabaseConfigurationDTO>
        implements AbstractNewDatasourceConnectorView.ActionDelegate {

    private final AbstractNewDatasourceConnectorView view;
    private final DatasourceClientService            service;
    private final NotificationManager                notificationManager;
    private final DtoFactory                         dtoFactory;
    private final NewDatasourceWizardMessages        messages;

    public AbstractNewDatasourceConnectorPage(@NotNull final AbstractNewDatasourceConnectorView view,
                                              @NotNull final DatasourceClientService service,
                                              @NotNull final NotificationManager notificationManager,
                                              @NotNull final DtoFactory dtoFactory,
                                              @NotNull final NewDatasourceWizardMessages messages) {
        super();
        view.setDelegate(this);
        this.service = service;
        this.view = view;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        this.messages = messages;
    }


    @Override
    public void init(DatabaseConfigurationDTO dataObject) {
        super.init(dataObject);

        dataObject.setDatabaseType(getDatabaseType());
        dataObject.setPort(getDefaultPort());
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(getView());
    }

    public AbstractNewDatasourceConnectorView getView() {
        return view;
    }

    /**
     * Returns the currently configured database.
     *
     * @return the database
     */
    protected abstract DatabaseConfigurationDTO getConfiguredDatabase();

    @Override
    public void onClickTestConnectionButton() {
        if (getView().isPasswordFieldDirty()) {
            try {
                service.encryptText(getView().getPassword(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                    @Override
                    protected void onSuccess(final String encryptedText) {
                        TextDTO encryptedTextDTO = dtoFactory.createDtoFromJson(encryptedText, TextDTO.class);
                        getView().setEncryptedPassword(encryptedTextDTO.getValue(), false);
                        doOnClickTestConnectionButton();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(DefaultNewDatasourceConnectorViewImpl.class, exception);
                    }
                });
            } catch (RequestException e2) {
                Log.error(DefaultNewDatasourceConnectorViewImpl.class, e2);
            }
        }
        else {
            doOnClickTestConnectionButton();
        }

    }

    public void doOnClickTestConnectionButton() {
        DatabaseConfigurationDTO configuration = getConfiguredDatabase();

        final Notification connectingNotification = new Notification(messages.startConnectionTest(),
                                                                     Notification.Status.PROGRESS);
        notificationManager.showNotification(connectingNotification);

        try {
            service.testDatabaseConnectivity(configuration, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    final ConnectionTestResultDTO testResult = dtoFactory.createDtoFromJson(result, ConnectionTestResultDTO.class);
                    if (Status.SUCCESS.equals(testResult.getTestResult())) {
                        getView().onTestConnectionSuccess();
                        connectingNotification.setMessage(messages.connectionTestSuccessNotification());
                        connectingNotification.setStatus(Notification.Status.FINISHED);
                    } else {
                        getView().onTestConnectionFailure(messages.connectionTestFailureSuccessMessage() + " "
                                                          + testResult.getFailureMessage());
                        connectingNotification.setMessage(messages.connectionTestFailureSuccessNotification());
                        connectingNotification.setType(Type.ERROR);
                        connectingNotification.setStatus(Notification.Status.FINISHED);
                    }
                }

                @Override
                protected void onFailure(final Throwable exception) {
                    getView().onTestConnectionFailure(messages.connectionTestFailureSuccessMessage());
                    connectingNotification.setMessage(messages.connectionTestFailureSuccessNotification());
                    connectingNotification.setType(Type.ERROR);
                    connectingNotification.setStatus(Notification.Status.FINISHED);
                }
            }

                   );
        } catch (final RequestException e) {
            Log.debug(AbstractNewDatasourceConnectorPage.class, e.getMessage());
            getView().onTestConnectionFailure(messages.connectionTestFailureSuccessMessage());
            connectingNotification.setMessage(messages.connectionTestFailureSuccessNotification());
            connectingNotification.setType(Type.ERROR);
            connectingNotification.setStatus(Notification.Status.FINISHED);
        }
    }

    @Override
    public void databaseNameChanged(String name) {
        dataObject.setDatabaseName(name);
        updateDelegate.updateControls();
    }

    @Override
    public void userNameChanged(String name) {
        dataObject.setUsername(name);
        updateDelegate.updateControls();
    }

    @Override
    public void passwordChanged(String password) {
        dataObject.setPassword(password);
        updateDelegate.updateControls();
    }

    public abstract Integer getDefaultPort();

    public abstract DatabaseType getDatabaseType();
}
