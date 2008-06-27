// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.service.IBrandingService;
import org.talend.dataprofiler.rcp.i18n.Messages;

/**
 * Wizard for the creation of a new project. <br/> $Id: RegisterWizard.java 1 2006-09-29 17:06:40 +0000 (ven., 29 sept.
 * 2006) nrousseau $
 */
public class RegisterWizard extends Wizard {

    /** Main page. */
    private RegisterWizardPage mainPage;

    private String email;

    private String country;

    private boolean proxyEnabled = false;

    private String proxyHost = ""; //$NON-NLS-1$

    private String proxyPort = ""; //$NON-NLS-1$

    /**
     * Constructs a new RegisterWizard.
     * 
     * @param author Project author.
     * @param server
     * @param password
     * @param port2
     */
    public RegisterWizard() {
        super();
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        mainPage = new RegisterWizardPage();
        addPage(mainPage);
        IBrandingService brandingService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                IBrandingService.class);
        setWindowTitle(Messages.getString("RegisterWizard.windowTitle", brandingService.getFullProductName())); //$NON-NLS-1$
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REGISTER_WIZ));
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        this.email = mainPage.getEmail();
        this.country = mainPage.getCountry();
        this.proxyEnabled = mainPage.getEnableHttpProxy().getEnabled();
        this.proxyHost = mainPage.getHttpProxyHostText().getText();
        this.proxyPort = mainPage.getHttpProxyPortText().getText();

        return true;
    }

    /**
     * Getter for country.
     * 
     * @return the country
     */
    public String getCountry() {
        return this.country;
    }

    /**
     * Sets the country.
     * 
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Getter for email.
     * 
     * @return the email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email.
     * 
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the proxyEnabled
     */
    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    /**
     * @param proxyEnabled the proxyEnabled to set
     */
    public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    /**
     * @return the proxyHost
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * @param proxyHost the proxyHost to set
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * @return the proxyPort
     */
    public String getProxyPort() {
        return proxyPort;
    }

    /**
     * @param proxyPort the proxyPort to set
     */
    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

}
