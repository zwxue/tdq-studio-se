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
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dq.analysis.parameters.ConnectionAnalysisParameter;

/**
 * @author zqin
 * 
 */
public class ColumnWizard extends AbstractAnalysisWizard {

    private AnalysisMetadataWizardPage metadataPage;

    /**
     * 
     */
    public ColumnWizard() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {

        metadataPage = new AnalysisMetadataWizardPage();

        addPage(metadataPage);
    }

    @Override
    protected void fillAnalysisEditorParam() {
        ConnectionAnalysisParameter parameters = (ConnectionAnalysisParameter) getAnalysisParameter();
        this.analysisName = parameters.getAnalysisName();
        this.analysisType = parameters.getAnalysisType();
        this.folderResource = parameters.getFolderProvider().getFolderResource();
    }

}
