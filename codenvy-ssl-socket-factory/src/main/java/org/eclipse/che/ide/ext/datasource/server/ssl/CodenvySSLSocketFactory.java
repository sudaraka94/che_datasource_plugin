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
package org.eclipse.che.ide.ext.datasource.server.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CodenvySSLSocketFactory extends SSLSocketFactory {
    private static final Logger                                        LOG                  =
                                                                                              LoggerFactory.getLogger(CodenvySSLSocketFactory.class);

    public static ThreadLocal<CodenvySSLSocketFactoryKeyStoreSettings> keystore             =
                                                                                              new ThreadLocal<CodenvySSLSocketFactoryKeyStoreSettings>();

    protected ThreadLocal<SSLSocketFactory>                            wrappedSocketFactory = new ThreadLocal<SSLSocketFactory>();

    public static SSLSocketFactory                                     defaultSocketFactory;

    protected void reloadIfNeeded() {
        if (keystore.get() != null) {
            CodenvySSLSocketFactoryKeyStoreSettings keystoreConfig = keystore.get();
            keystore.set(null);
            wrappedSocketFactory.set(getSSLSocketFactoryDefaultOrConfigured(keystoreConfig));
        }
        if (wrappedSocketFactory.get() == null) {
            wrappedSocketFactory.set(getDefaultSSLSocketFactory());
        }
    }

    protected SSLSocketFactory getDefaultSSLSocketFactory() {
        try {
            if (defaultSocketFactory == null) {
                defaultSocketFactory = SSLContext.getDefault().getSocketFactory();
            }
            return defaultSocketFactory;
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Couldn't set default socket factory", e);
        }
    }

    protected SSLSocketFactory getSSLSocketFactoryDefaultOrConfigured(CodenvySSLSocketFactoryKeyStoreSettings keystoreConfig) {
        TrustManagerFactory tmf = null;
        KeyManagerFactory kmf = null;

        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        } catch (NoSuchAlgorithmException nsae) {
            LOG.error("An error occured while setting up custom SSL Socket factory. Falling back the the default one", nsae);
            return getDefaultSSLSocketFactory();
        }

        if (keystoreConfig.getKeyStoreContent() != null && keystoreConfig.getKeyStoreContent().length > 0) {
            char[] password = (keystoreConfig.getKeyStorePassword() == null) ? new char[0]
                : keystoreConfig.getKeyStorePassword().toCharArray();
            try (InputStream fis = new ByteArrayInputStream(keystoreConfig.getKeyStoreContent())) {
                KeyStore ks = KeyStore.getInstance("JKS");
                ks.load(fis, password);
                kmf.init(ks, password);
            } catch (Exception e) {
                LOG.error("An error occured while setting up custom SSL Socket factory. Falling back the the default one", e);
                return getDefaultSSLSocketFactory();
            }
        }


        if (keystoreConfig.getTrustStoreContent() != null && keystoreConfig.getTrustStoreContent().length > 0) {
            LOG.info("Initializing truststore from file");
            char[] password = (keystoreConfig.getTrustStorePassword() == null) ? new char[0]
                : keystoreConfig.getTrustStorePassword().toCharArray();
            try (InputStream fis = new ByteArrayInputStream(keystoreConfig.getTrustStoreContent())) {
                KeyStore ks = KeyStore.getInstance("JKS");
                ks.load(fis, password);
                tmf.init(ks);
            } catch (Exception e) {
                LOG.error("An error occured while setting up custom SSL Socket factory. Falling back the the default one", e);
                return getDefaultSSLSocketFactory();
            }
        }

        SSLContext sslContext = null;

        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(isNullOrEmpty(keystoreConfig.getKeyStoreContent()) ? null : kmf.getKeyManagers(),
                            isNullOrEmpty(keystoreConfig.tsContent) ?
                                new X509TrustManager[]{new X509TrustManager() {
                                    @Override
                                    public void checkClientTrusted(X509Certificate[] chain,
                                                                   String authType) {
                                        // return without complaint
                                    }

                                    @Override
                                    public void checkServerTrusted(X509Certificate[] chain,
                                                                   String authType) throws CertificateException {
                                        // return without complaint
                                    }

                                    @Override
                                    public X509Certificate[] getAcceptedIssuers() {
                                        return null;
                                    }
                                }
                                } : tmf.getTrustManagers(), null);

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            LOG.error("An error occured while setting up custom SSL Socket factory. Falling back the the default one", e);
            return (javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory
                                                                                 .getDefault();
        }
    }

    @Override
    public Socket createSocket() throws IOException {
        reloadIfNeeded();
        return wrappedSocketFactory.get().createSocket();
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        reloadIfNeeded();
        return wrappedSocketFactory.get().createSocket(host, port);
    }

    @Override
    public String[] getDefaultCipherSuites() {
        reloadIfNeeded();
        return wrappedSocketFactory.get().getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        reloadIfNeeded();
        return wrappedSocketFactory.get().getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        reloadIfNeeded();
        return wrappedSocketFactory.get().createSocket(s, host, port, autoClose);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        reloadIfNeeded();
        return wrappedSocketFactory.get().createSocket(host, port, localHost, localPort);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        reloadIfNeeded();
        return wrappedSocketFactory.get().createSocket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        reloadIfNeeded();
        return wrappedSocketFactory.get().createSocket(address, port, localAddress, localPort);
    }

    public static boolean isNullOrEmpty(byte[] byteArray) {
        return byteArray == null || byteArray.length <= 0;
    }

}
