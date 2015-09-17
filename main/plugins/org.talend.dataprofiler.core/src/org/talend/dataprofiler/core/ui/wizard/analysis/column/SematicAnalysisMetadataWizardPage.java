// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class SematicAnalysisMetadataWizardPage extends AnalysisMetadataWizardPage {

    public SematicAnalysisMetadataWizardPage() {
        super();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#fireEvent()
     */
    @Override
    protected void fireEvent() {
        super.fireEvent();
    }

    @Override
    public boolean canFlipToNextPage() {
        return true;
    }
}
