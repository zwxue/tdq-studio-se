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
package org.talend.dataprofiler.core.ui.wizard.analysis;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dq.analysis.parameters.ConnectionAnalysisParameter;
import org.talend.dq.analysis.parameters.ConnectionParameter;

/**
 * @author zqin
 * 
 */
public class CreateNewAnalysisWizard extends Wizard {

    private boolean creation;

    private AnalysisType type;

    private IWizard wizard;

    private NewWizardSelectionPage mainPage;

    private WizardPage[] otherPages;

    public CreateNewAnalysisWizard(boolean creation, AnalysisType type) {
        this.creation = creation;
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        if (type == null) {
            wizard = mainPage.getSelectedWizard();
            wizard.setContainer(getContainer());

            return true;
        } else {
            return wizard.performFinish();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {

        setWindowTitle("Create New Analysis");
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));

        if (creation) {
            if (type == null) {

                AbstractAnalysisWizardPage.setConnectionParams(new ConnectionParameter());
                mainPage = new NewWizardSelectionPage();
                addPage(mainPage);
            } else {

                ConnectionAnalysisParameter analysisParameter = new ConnectionAnalysisParameter();
                AbstractAnalysisWizardPage.setConnectionParams(analysisParameter);
                if (type == AnalysisType.MULTIPLE_COLUMN) {
                    analysisParameter.setAnalysisTypeName(AnalysisType.MULTIPLE_COLUMN.getLiteral());
                    wizard = WizardFactory.createColumnWizard();
                    wizard.addPages();
                } else if (type == AnalysisType.CONNECTION) {
                    analysisParameter.setAnalysisTypeName(type.getLiteral());
                    wizard = WizardFactory.createConnectionWizard(false);
                    wizard.addPages();
                }

                if (wizard != null) {
                    IWizardPage[] pages = wizard.getPages();
                    for (IWizardPage page : pages) {
                        addPage(page);
                    }
                }

                if (getOtherPages() != null) {
                    for (IWizardPage page : getOtherPages()) {
                        addPage(page);
                    }
                }
            }
        }
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
