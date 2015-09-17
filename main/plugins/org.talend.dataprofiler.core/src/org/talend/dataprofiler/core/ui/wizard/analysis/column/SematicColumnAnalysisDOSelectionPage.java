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


/**
 * DOC klliu class global comment. Detailled comment,2011-02-16 feature 15387
 */
public class SematicColumnAnalysisDOSelectionPage extends ColumnAnalysisDOSelectionPage {

    public SematicColumnAnalysisDOSelectionPage() {
        super();
        setPageComplete(false);
    }

    /**
     * 
     * DOC talend Comment method "updateCompleteState".
     */
    @Override
    void updateCompleteState() {
        setPageComplete(false);
    }
}
