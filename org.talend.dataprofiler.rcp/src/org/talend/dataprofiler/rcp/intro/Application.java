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

import java.util.HashMap;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.User;
import org.talend.core.model.properties.impl.PropertiesFactoryImpl;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.license.LicenseManagement;
import org.talend.dataprofiler.core.license.LicenseWizard;
import org.talend.dataprofiler.core.license.LicenseWizardDialog;
import org.talend.dataprofiler.rcp.i18n.Messages;
import org.talend.dataprofiler.rcp.intro.linksbar.Workbench3xImplementation4CoolBar;
import org.talend.repository.localprovider.model.LocalRepositoryFactory;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.registeruser.RegisterManagement;
import org.talend.repository.ui.wizards.register.RegisterWizard;
import org.talend.repository.ui.wizards.register.RegisterWizardPage1;
import org.talend.repository.utils.ProjectHelper;
import org.talend.repository.utils.XmiResourceManager;
import org.talend.resource.ResourceManager;

/**
 * This class controls all aspects of the application's execution.
 */
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
            initProxyRepository();

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

    /**
     * 
     * DOC zshen Comment method "initProxyRepository".
     * 
     */
    private void initProxyRepository() {
        Project project = null;
        ProxyRepositoryFactory proxyRepository = ProxyRepositoryFactory.getInstance();
        proxyRepository.setRepositoryFactoryFromProvider(LocalRepositoryFactory.getInstance());
        try {
            proxyRepository.checkAvailability();
            proxyRepository.initialize();

            XmiResourceManager xmiResourceManager = new XmiResourceManager();
            IProject rootProject = ResourceManager.getRootProject();
            if (rootProject.getFile(FileConstants.LOCAL_PROJECT_FILENAME).exists()) {
                project = new Project(xmiResourceManager.loadProject(rootProject));
            } else {
                User user = PropertiesFactoryImpl.eINSTANCE.createUser();
                user.setLogin("talend@talend.com");
                user.setPassword("talend@talend.com".getBytes());
                String projectName = ResourceManager.getRootProjectName();
                String projectDesc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName).getComment();
                Project projectInfor = ProjectHelper.createProject(projectName, projectDesc, ECodeLanguage.JAVA.getName(), user);

                // MOD zshen create project by proxyRepository
                checkFileName(projectInfor.getLabel(), RepositoryConstants.PROJECT_PATTERN);

                project = proxyRepository.getRepositoryFactoryFromProvider().createProject(projectInfor);

            }

            if (project != null) {
                initRepositoryContext(project);
            }

        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }

    }

    private void initRepositoryContext(Project project) {
        RepositoryContext repositoryContext = new RepositoryContext();
        repositoryContext.setUser(project.getAuthor());
        repositoryContext.setClearPassword(project.getLabel());
        repositoryContext.setProject(project);
        repositoryContext.setFields(new HashMap<String, String>());
        repositoryContext.getFields().put(IProxyRepositoryFactory.BRANCH_SELECTION + "_" + project.getTechnicalLabel(), "");
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        ctx.putProperty(Context.REPOSITORY_CONTEXT_KEY, repositoryContext);
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

    /**
     * 
     * DOC zshen Comment method "checkFileName".
     * 
     * @param fileName
     * @param pattern
     * 
     * copy the method from ProxyRepositoryFactory to avoid tos migeration.
     */
    private void checkFileName(String fileName, String pattern) {
        if (!Pattern.matches(pattern, fileName)) {
            throw new IllegalArgumentException(Messages.getString(
                    "ProxyRepositoryFactory.illegalArgumentException.labelNotMatchPattern", new String[] { fileName, pattern })); //$NON-NLS-1$
        }
    }
}
