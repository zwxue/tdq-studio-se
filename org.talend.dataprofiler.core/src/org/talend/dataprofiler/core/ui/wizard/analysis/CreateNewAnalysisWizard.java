// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.analysis;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * @author zqin
 * 
 */
public class CreateNewAnalysisWizard extends Wizard {

    private IWizard wizard;

    private NewWizardSelectionPage mainPage;

    private WizardPage[] otherPages;

    private FolderProvider currentFolderProvider;

    public FolderProvider getCurrentFolderProvider() {
        return currentFolderProvider;
    }

    public void setCurrentFolderProvider(FolderProvider currentFolderProvider) {
        this.currentFolderProvider = currentFolderProvider;
    }

    public CreateNewAnalysisWizard() {
        setWindowTitle(DefaultMessagesImpl.getString("CreateNewAnalysisWizard.createNewAnalysis")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        wizard = mainPage.getSelectedWizard();
        wizard.setContainer(getContainer());
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {

        mainPage = new NewWizardSelectionPage();
        addPage(mainPage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    @Override
    public boolean canFinish() {
        if (mainPage != null) {
            return mainPage.isCanFinishEarly();
        }

        return super.canFinish();
    }

    public WizardPage[] getOtherPages() {
        return otherPages;
    }

    public void setOtherPages(WizardPage[] otherPages) {
        this.otherPages = otherPages;
    }
}
