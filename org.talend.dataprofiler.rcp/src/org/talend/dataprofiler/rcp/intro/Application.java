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
package org.talend.dataprofiler.rcp.intro;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.tweaklets.Tweaklets;
import org.eclipse.ui.internal.tweaklets.WorkbenchImplementation;
import org.talend.commons.exception.BusinessException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.license.LicenseManagement;
import org.talend.dataprofiler.core.license.LicenseWizard;
import org.talend.dataprofiler.core.license.LicenseWizardDialog;
import org.talend.dataprofiler.rcp.i18n.Messages;
import org.talend.dataprofiler.rcp.intro.linksbar.Workbench3xImplementation4CoolBar;
import org.talend.repository.registeruser.RegisterManagement;
import org.talend.repository.ui.wizards.register.RegisterWizard;
import org.talend.repository.ui.wizards.register.RegisterWizardPage1;

/**
 * This class controls all aspects of the application's execution.
 */
@SuppressWarnings("restriction") //$NON-NLS-1$
public class Application implements IApplication {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    public Object start(IApplicationContext context) {
        Display display = PlatformUI.createDisplay();
        Shell shell = new Shell(display, SWT.ON_TOP);
        try {
            openLicenseAndRegister(shell);
        } catch (BusinessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            if (!CorePlugin.getDefault().isRepositoryInitialized()) {
                CorePlugin.getDefault().initProxyRepository();
            }

            Tweaklets.setDefault(WorkbenchImplementation.KEY, new Workbench3xImplementation4CoolBar());

            int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
            if (returnCode == PlatformUI.RETURN_RESTART) {
                return IApplication.EXIT_RESTART;
            }
            return IApplication.EXIT_OK;
        } finally {
            display.dispose();
        }
    }

    private void openLicenseAndRegister(Shell shell) throws BusinessException {
        IBrandingService brandingService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                IBrandingService.class);
        if (!LicenseManagement.isLicenseValidated()) {
            LicenseWizard licenseWizard = new LicenseWizard();
            LicenseWizardDialog dialog = new LicenseWizardDialog(shell, licenseWizard);
            dialog.setTitle(Messages.getString("LicenseWizard.windowTitle")); //$NON-NLS-1$
            if (dialog.open() == WizardDialog.OK) {
                LicenseManagement.acceptLicense();

            } else {
                // FIXME this code must be removed. We must not exit the application but display a pop-up to ask for a
                // new license
                System.exit(0);
            }
        }

        if (brandingService.getBrandingConfiguration().isUseProductRegistration()) {
            if (!RegisterManagement.getInstance().isProductRegistered()) {
                RegisterWizard registerWizard = new RegisterWizard();
                RegisterWizardPage1 dialog = new RegisterWizardPage1(shell, registerWizard);
                dialog.open();
            }
        }

    }



    public boolean licenceAccept(Shell shell) {
        if (!LicenseManagement.isLicenseValidated()) {
            LicenseWizard licenseWizard = new LicenseWizard();
            LicenseWizardDialog dialog = new LicenseWizardDialog(shell, licenseWizard);
            dialog.setTitle(Messages.getString("Application.license")); //$NON-NLS-1$
            if (dialog.open() == WizardDialog.OK) {
                LicenseManagement.acceptLicense();
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null) {
            return;
        }
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {

            public void run() {
                if (!display.isDisposed()) {
                    workbench.close();
                }
            }
        });
    }

}
