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
package org.talend.dataprofiler.core.model.nodes.foldernode;

import java.util.List;

import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;

/**
 * This folder container will store all the analyses related with special report.
 */
public class AnalysesContainerFolder extends AbstractFolderNode {

    /**
     * DOC rli AnalysesFolder constructor comment.
     * 
     * @param name
     */
    public AnalysesContainerFolder(TdReport report) {
        super("Analyses");
        this.setParent(report);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.model.nodes.foldernode.AbstractFolderNode#loadChildren()
     */
    @Override
    public void loadChildren() {
        List<Analysis> analyses = ReportHelper.getAnalyses((TdReport) getParent());
        this.setChildren(analyses.toArray());
    }

}
