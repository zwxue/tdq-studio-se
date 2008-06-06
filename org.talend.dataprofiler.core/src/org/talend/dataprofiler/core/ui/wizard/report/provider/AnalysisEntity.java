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
package org.talend.dataprofiler.core.ui.wizard.report.provider;

import org.talend.dataquality.analysis.Analysis;


/**
 * DOC zqin  class global comment. Detailled comment
 */
public class AnalysisEntity {
    
    private Analysis analysis;

    public AnalysisEntity(Analysis analysis) {
        this.analysis = analysis;
    }
    
    /**
     * Getter for analysisName.
     * @return the analysisName
     */
    public String getAnalysisName() {
        return this.analysis.getName();
    }

    
    /**
     * Getter for analysis.
     * @return the analysis
     */
    public Analysis getAnalysis() {
        return this.analysis;
    }

}
