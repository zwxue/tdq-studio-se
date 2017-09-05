// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.nodes;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * Repost's analysis RepositoryNode.
 */
public class ReportAnalysisRepNode extends DQRepositoryNode {

    private Report report;

    private Analysis analysis;

    public Report getReport() {
        return this.report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Analysis getAnalysis() {
        return this.analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    /**
     * DOC xqliu ReportAnalysisRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public ReportAnalysisRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type,
            org.talend.core.model.general.Project inWhichProject) {
        super(object, parent, type, inWhichProject);
    }

    @Override
    public String getLabel() {
        if (this.getAnalysis() != null) {
            return this.getAnalysis().getName();
        }
        return super.getLabel();
    }

    /**
     * return the report's Item object.
     * 
     * @return
     */
    public TDQReportItem getReportItem() {
        return (TDQReportItem) this.getParent().getParent().getObject().getProperty().getItem();
    }
}
