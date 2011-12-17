// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.util.List;

import org.talend.core.model.properties.Property;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.ReportHelper;
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
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.action.actions.duplicate.DuplicateEMFResourceHandle#update(orgomg.cwm.objectmodel
     * .core.ModelElement, orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected ModelElement update(ModelElement oldObject, ModelElement newObject) {
        newObject = super.update(oldObject, newObject);

        // MOD klliu 2011-06-21 bug 21812 "duplicate report" have some issue
        TdReport report = (TdReport) newObject;
        report.getAnalysisMap().clear();
        List<Analysis> anaLs = ReportHelper.getAnalyses((TdReport) oldObject);
        for (Analysis analysis : anaLs) {
            DependenciesHandler.getInstance().setDependencyOn(report, analysis);
            ((TdReport) newObject).addAnalysis(analysis);
        }
        // ~

        return newObject;
    }
}
