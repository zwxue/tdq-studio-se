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
package org.talend.dataquality.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.ReportsFactory;
import org.talend.dataquality.reports.TdReport;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public final class ReportHelper {

    private ReportHelper() {
    }

    /**
     * Method "getAnalyses".
     * 
     * @param report
     * @return a list of analyses or an empty list. Do not use this list to add analysis to the report.
     */
    public static List<Analysis> getAnalyses(TdReport report) {
        List<Analysis> analyses = new ArrayList<Analysis>();
        EList<RenderedObject> components = report.getComponent();
        for (RenderedObject renderedObject : components) {
            Analysis analysis = DataqualitySwitchHelper.ANALYSIS_SWITCH.doSwitch(renderedObject);
            if (analysis != null) {
                analyses.add(analysis);
            }
        }
        return analyses;
    }

    /**
     * Method "removeAnalyses".
     * 
     * @param report
     * @param analyses analyses to remove from the report
     * @return true if the analyses list of the report changed as a result of the call.
     */
    public static boolean removeAnalyses(TdReport report, Collection<Analysis> analyses) {
        return report.getComponent().removeAll(analyses);
    }

    /**
     * Method "addAnalyses".
     * 
     * @param analyses a collection of analyses.
     * @param report a report (must not be null)
     * @return true if the analysis list changed as a result of the call.
     */
    public static boolean addAnalyses(Collection<Analysis> analyses, TdReport report) {
        return report.getComponent().addAll(analyses);
    }

    public static TdReport createReport(String name) {
        TdReport report = ReportsFactory.eINSTANCE.createTdReport();
        report.setName(name);
        return report;
    }

    /**
     * Method "mustRefreshAllAnalyses".
     * 
     * @param report
     * @param refresh true if all analyses must be refreshed. False means that no analysis will be refreshed.
     */
    public static void mustRefreshAllAnalyses(TdReport report, boolean refresh) {
        EList<AnalysisMap> analysisMap = report.getAnalysisMap();
        for (AnalysisMap map : analysisMap) {
            map.setMustRefresh(refresh);
        }
    }

    /**
     * Method "getExecutionInformations" returns the execution informations of the given report. If none existed, they
     * are created and stored in the report.
     * 
     * @param report a report
     * @return the existing execution informations
     */
    public static ExecutionInformations getExecutionInformations(TdReport report) {
        ExecutionInformations execInformations = report.getExecInformations();
        if (execInformations == null) {
            execInformations = AnalysisFactory.eINSTANCE.createExecutionInformations();
            report.setExecInformations(execInformations);
        }
        return execInformations;
    }
}
