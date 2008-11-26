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
package org.talend.dataprofiler.core.ui.wizard.analysis.connection;

import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;

/**
 * @author zqin
 * 
 */
public class ConnectionAnalysisWizard extends AnalysisFilterWizard {

    protected ConnAnalysisDPSelectionPage dpSelectionPage;

    /**
     * 
     */
    public ConnectionAnalysisWizard(AnalysisFilterParameter parameter) {
        super(parameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {

        addPage(new AnalysisMetadataWizardPage());

        if (anaFilterParameter.getTdDataProvider() == null) {
            dpSelectionPage = new ConnAnalysisDPSelectionPage();
            addPage(dpSelectionPage);
        }

        anaFilterPage = new ConnAnalysisFilterPage();
        addPage(anaFilterPage);
    }
}
