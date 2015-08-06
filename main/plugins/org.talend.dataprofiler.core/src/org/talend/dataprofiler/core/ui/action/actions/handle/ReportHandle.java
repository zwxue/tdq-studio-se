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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.Property;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.repository.model.IRepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ReportHandle extends EMFResourceHandle {

    /**
     * DOC bZhou DuplicateReportHandle constructor comment.
     */
    ReportHandle(Property propety) {
        super(propety);
    }

    ReportHandle(IRepositoryNode node) {
        super(node);
    }

    @Override
    protected ModelElement update(ModelElement oldObject, ModelElement newObject) {
        TdReport report = (TdReport) super.update(oldObject, newObject);

        // MOD klliu 2011-06-21 bug 21812 "duplicate report" have some issue
        report.getAnalysisMap().clear();
        EList<AnalysisMap> analysisMap = ((TdReport) oldObject).getAnalysisMap();
        for (AnalysisMap analysiM : analysisMap) {
            Analysis analysis = analysiM.getAnalysis();
            DependenciesHandler.getInstance().setDependencyOn(report, analysis);
            report.addAnalysis(analysis);
            report.setReportType(analysiM.getReportType(), analysiM.getJrxmlSource(), analysis);
        }
        // ~

        return report;
    }
}
