// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.wizards;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class ConnectionWizard extends Wizard implements INewWizard {

    private static Logger log = Logger.getLogger(ConnectionWizard.class);

    private ConnectionTypePage connectionTypePage;
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO Auto-generated method stub

    }

    /*
     * According to the first connection type, go to different wizard: database, file, mdm
     */
    @Override
    public void addPages() {
        // add the first page to select the connection type
        connectionTypePage = new ConnectionTypePage("");
        addPage(connectionTypePage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     */
    @Override
    public boolean canFinish() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls(Composite pageContainer) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#dispose()
     */
    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getContainer()
     */
    @Override
    public IWizardContainer getContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getDefaultPageImage()
     */
    @Override
    public Image getDefaultPageImage() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getDialogSettings()
     */
    @Override
    public IDialogSettings getDialogSettings() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getPage(java.lang.String)
     */
    @Override
    public IWizardPage getPage(String pageName) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getPageCount()
     */
    @Override
    public int getPageCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getPages()
     */
    @Override
    public IWizardPage[] getPages() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getPreviousPage(IWizardPage page) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getStartingPage()
     */
    @Override
    public IWizardPage getStartingPage() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getTitleBarColor()
     */
    @Override
    public RGB getTitleBarColor() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#getWindowTitle()
     */
    @Override
    public String getWindowTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#isHelpAvailable()
     */
    @Override
    public boolean isHelpAvailable() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#needsPreviousAndNextButtons()
     */
    @Override
    public boolean needsPreviousAndNextButtons() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#needsProgressMonitor()
     */
    @Override
    public boolean needsProgressMonitor() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#performCancel()
     */
    @Override
    public boolean performCancel() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        RepositoryNode node = null;
        // switch (this.connectionTypePage.getConnectionType()) {
        // case 0:// mdm
        // node = (RepositoryNode) RepositoryNodeHelper.getMetadataFolderNode(EResourceConstant.MDM_CONNECTIONS);
        // if (PluginChecker.isMDMPluginLoaded()
        // && GlobalServiceRegister.getDefault().isServiceRegistered(IMDMProviderService.class)) {
        // IMDMProviderService service = (IMDMProviderService) GlobalServiceRegister.getDefault().getService(
        // IMDMProviderService.class);
        // if (service != null) {
        // service.newWizard(PlatformUI.getWorkbench(), true, node, getExistingNames());
        // }
        // }
        // break;
        // case 1:// db
        // node = (RepositoryNode) RepositoryNodeHelper.getMetadataFolderNode(EResourceConstant.DB_CONNECTIONS);
        // new DatabaseWizard(PlatformUI.getWorkbench(), true, node, getExistingNames());
        // break;
        // case 2: // file
        // new DelimitedFileWizard(PlatformUI.getWorkbench(), true, node, getExistingNames());
        // break;
        // }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#setContainer(org.eclipse.jface.wizard.IWizardContainer)
     */
    @Override
    public void setContainer(IWizardContainer wizardContainer) {
        // TODO Auto-generated method stub

    }

}
