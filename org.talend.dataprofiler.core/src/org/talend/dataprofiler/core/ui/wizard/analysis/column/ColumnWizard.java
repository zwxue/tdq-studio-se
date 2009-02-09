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
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import org.eclipse.jface.wizard.WizardPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dq.analysis.parameters.AnalysisParameter;

/**
 * @author zqin
 * 
 */
public class ColumnWizard extends AbstractAnalysisWizard {

    private AnalysisParameter parameter;

    private WizardPage[] extenalPages;

    public WizardPage[] getExtenalPages() {
        if (extenalPages == null) {
            return new WizardPage[0];
        }
        return extenalPages;
    }

    public void setExtenalPages(WizardPage[] extenalPages) {
        this.extenalPages = extenalPages;
    }

    public ColumnWizard(AnalysisParameter parameter) {
        super(parameter);
        this.parameter = parameter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(new AnalysisMetadataWizardPage());

        for (WizardPage page : getExtenalPages()) {
            addPage(page);
        }
    }

    @Override
    protected void fillAnalysisEditorParam() {
        this.analysisName = parameter.getAnalysisName();
        this.analysisType = parameter.getAnalysisType();
        this.folderResource = parameter.getFolderProvider().getFolderResource();
    }
}
