// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprofiler.rcp.register;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.update.core.SiteManager;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.service.IBrandingService;
import org.talend.repository.registeruser.proxy.RegisterUserPortTypeProxy;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class RegisterManagement {

    private static final int REGISTRATION_MAX_TRIES = 6;

    // REGISTRATION_DONE = 1 : registration OK
    private static final double REGISTRATION_DONE = 2;

    /**
     * DOC qzhang Comment method "isProductRegistered".
     * 
     * @return
     */
    public static boolean isProductRegistered() {
        initPreferenceStore();
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        if ((prefStore.getInt("REGISTRATION_TRIES") > 1) && ((prefStore.getInt("REGISTRATION_DONE") != 1))) { //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        }
        return true;
    }

    /**
     * DOC qzhang Comment method "initPreferenceStore".
     */
    private static void initPreferenceStore() {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        if (prefStore.getDefaultInt("REGISTRATION_TRIES") == 0) { //$NON-NLS-1$
            prefStore.setDefault("REGISTRATION_TRIES", REGISTRATION_MAX_TRIES); //$NON-NLS-1$
        }
        if (prefStore.getDefaultInt("REGISTRATION_DONE") == 0) { //$NON-NLS-1$
            prefStore.setDefault("REGISTRATION_DONE", REGISTRATION_DONE); //$NON-NLS-1$
        }
    }

    /**
     * DOC qzhang Comment method "register".
     * 
     * @param email
     * @param country
     * @param isProxyEnabled
     * @param proxyHost
     * @param proxyPort
     * @param designerVersion
     * @param projectLanguage
     * @param osName
     * @param osVersion
     * @param javaVersion
     * @param totalMemory
     * @param memRAM
     * @param nbProc
     * @return
     */
    public static boolean register(String email, String country, boolean isProxyEnabled, String proxyHost, String proxyPort,
            String designerVersion, String projectLanguage, String osName, String osVersion, String javaVersion,
            long totalMemory, Long memRAM, int nbProc) {
        BigInteger result = BigInteger.valueOf(-1);
        // if proxy is enabled
        if (isProxyEnabled) {
            // get parameter and put them in System.properties.
            System.setProperty("http.proxyHost", proxyHost); //$NON-NLS-1$
            System.setProperty("http.proxyPort", proxyPort); //$NON-NLS-1$

            // override automatic update parameters
            if (proxyPort != null && proxyPort.trim().equals("")) { //$NON-NLS-1$
                proxyPort = null;
            }
            SiteManager.setHttpProxyInfo(true, proxyHost, proxyPort);
        }
        RegisterUserPortTypeProxy proxy = new RegisterUserPortTypeProxy();
        proxy.setEndpoint("http://www.talend.com/TalendRegisterWS/registerws.php"); //$NON-NLS-1$
        try {
            IBrandingService brandingService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                    IBrandingService.class);
            result = proxy.registerUserWithAllUserInformationsAndReturnId(email, country, designerVersion, brandingService
                    .getShortProductName(), projectLanguage, osName, osVersion, javaVersion, totalMemory + "", memRAM + "", //$NON-NLS-1$ //$NON-NLS-2$
                    nbProc + ""); //$NON-NLS-1$
            if (result.signum() > 0) {
                PlatformUI.getPreferenceStore().setValue("REGISTRATION_DONE", 1); //$NON-NLS-1$
                validateRegistration(brandingService.getAcronym(), result.longValue());
                // PreferenceManipulator prefManipulator = new
                // PreferenceManipulator(CorePlugin.getDefault().getPreferenceStore());
                // // prefManipulator.addUser(email);
                // // prefManipulator.setLastUser(email);
                //
                // // Create a default connection:
                // if (prefManipulator.readConnections().isEmpty()) {
                // ConnectionBean recup = ConnectionBean.getDefaultConnectionBean();
                // recup.setUser(email);
                // recup.setComplete(true);
                // prefManipulator.addConnection(recup);
                // }

            }
        } catch (RemoteException e) {
            decrementTry();
        }
        return result.signum() > 0;
    }

    public static void validateRegistration(String acronym, long registNumber) {
        URL registURL = null;
        try {
            registURL = new URL("http://www.talend.com/designer_post_reg.php?prd=" + acronym + "&cid=" + registNumber);
            PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(registURL);
        } catch (PartInitException e) {
            // if no default browser (like on linux), try to open directly with firefox.
            try {
                Runtime.getRuntime().exec("firefox " + registURL.toString());
            } catch (IOException e2) {
                if (PlatformUI.getWorkbench().getBrowserSupport().isInternalWebBrowserAvailable()) {
                    IWebBrowser browser;
                    try {
                        browser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser("registrationId");
                        browser.openURL(registURL);
                    } catch (PartInitException e1) {
                        ExceptionHandler.process(e);
                    }
                } else {
                    ExceptionHandler.process(e);
                }
            }
        } catch (MalformedURLException e) {
            ExceptionHandler.process(e);
        }
    }

    /**
     * DOC qzhang Comment method "decrementTry".
     */
    public static void decrementTry() {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        prefStore.setValue("REGISTRATION_TRIES", prefStore.getInt("REGISTRATION_TRIES") - 1); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
