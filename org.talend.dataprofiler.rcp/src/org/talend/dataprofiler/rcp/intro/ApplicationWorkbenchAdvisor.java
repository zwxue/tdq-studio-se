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

import java.lang.management.ManagementFactory;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchAdvisor;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.repository.registeruser.RegisterManagement;
import org.talend.repository.ui.wizards.register.RegisterWizard;
import org.talend.repository.ui.wizards.register.RegisterWizardDialog;

/**
 * DOC rli class global comment. Detailled comment <br/>
 * 
 */
public class ApplicationWorkbenchAdvisor extends IDEWorkbenchAdvisor {

    private static Logger log = Logger.getLogger(ApplicationWorkbenchAdvisor.class);

    private static final String PERSPECTIVE_ID = "org.talend.dataprofiler.DataProfilingPerspective"; //$NON-NLS-1$

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    public void initialize(IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        configurer.setSaveAndRestore(true);
        IPreferenceStore apiStore = PlatformUI.getPreferenceStore();
        apiStore.setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
        // PTODO qzhang close the Sql editor, when start up the RCP.
        apiStore.setValue(IWorkbenchPreferenceConstants.CLOSE_EDITORS_ON_EXIT, true);
    }

    public String getInitialWindowPerspectiveId() {
        return PERSPECTIVE_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.internal.ide.application.IDEWorkbenchAdvisor#postStartup()
     */
    @Override
    public void postStartup() {
        super.postStartup();
        // Start Web Service Registration
        try {
            if (!RegisterManagement.getInstance().isProductRegistered()) {
                RegisterWizard registerWizard = new RegisterWizard();
                Shell shell = getWorkbenchConfigurer().getWorkbench().getActiveWorkbenchWindow().getShell();
                WizardDialog dialog = new RegisterWizardDialog(shell, registerWizard);
                dialog.setTitle(""); //$NON-NLS-1$
                if (dialog.open() == WizardDialog.OK) {
                    String projectLanguage = "java"; //$NON-NLS-1$
                    // OS
                    String osName = System.getProperty("os.name"); //$NON-NLS-1$
                    String osVersion = System.getProperty("os.version"); //$NON-NLS-1$

                    // Java version
                    String javaVersion = System.getProperty("java.version"); //$NON-NLS-1$

                    // Java Memory
                    long totalMemory = Runtime.getRuntime().totalMemory();

                    // RAM
                    com.sun.management.OperatingSystemMXBean composantSystem = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                            .getOperatingSystemMXBean();
                    Long memRAM = new Long(composantSystem.getTotalPhysicalMemorySize() / 1024);

                    // CPU
                    int nbProc = Runtime.getRuntime().availableProcessors();

                    RegisterManagement.getInstance().register(registerWizard.getEmail(), registerWizard.getCountry(), registerWizard
                            .isProxyEnabled(), registerWizard.getProxyHost(), registerWizard.getProxyPort(), CorePlugin
                            .getDefault().getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION).toString(),
                            projectLanguage, osName, osVersion, javaVersion, totalMemory, memRAM, nbProc);
                } else {
                    RegisterManagement.decrementTry();
                }
            }
        } catch (Exception e) {
            log.error("Problem during the registration wizard:" + e.getLocalizedMessage(), e); //$NON-NLS-1$
        }
    }
}
