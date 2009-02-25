// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.ResourceHelper;
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

    // ~ADD mzhao 2009-02-05
    public static final int DATE_FROM = 0;

    public static final int DATE_TO = 1;

    // ~
    /**
     * The report types.
     */
    public static enum ReportType {
        BASIC("Basic", "/reports/column/report_01.jrxml"), //$NON-NLS-1$//$NON-NLS-2$
        EVOLUTION("Evolution", "/reports/column/report_02.jrxml"), //$NON-NLS-1$//$NON-NLS-2$
        USER_MADE("User specified", null); // for the user to set his own file path//$NON-NLS-1$

        private String label;

        /**
         * Getter for label.
         * 
         * @return the label
         */
        public String getLabel() {
            return this.label;
        }

        /**
         * Getter for jrxmlFilename.
         * 
         * @return the jrxmlFilename
         */
        public String getJrxmlFilename() {
            return this.jrxmlFilename;
        }

        private String jrxmlFilename;

        /**
         * Sets the jrxmlFilename.
         * 
         * @param jrxmlFilename the jrxmlFilename to set
         */
        public void setJrxmlFilename(String jrxmlFilename) {
            this.jrxmlFilename = jrxmlFilename;
        }

        ReportType(String lab, String filename) {
            this.label = lab;
            this.jrxmlFilename = filename;
        }

        public static List<String> getLabels() {
            List<String> list = new ArrayList<String>();
            for (ReportType t : ReportType.values()) {
                list.add(t.getLabel());
            }
            return list;
        }

        /**
         * DOC qzhang Comment method "getReportType".
         * 
         * @param text
         * @return
         */
        public static ReportType getReportType(String text) {
            for (ReportType reptype : values()) {
                if (reptype.label.equals(text)) {
                    return reptype;
                }
            }
            return BASIC;
        }

    }

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
        boolean removed = true;
        for (Analysis analysis : analyses) {
            if (!report.removeAnalysis(analysis)) {
                removed = false;
            }
        }
        return removed;
    }

    /**
     * Method "addAnalyses".
     * 
     * @param analyses a collection of analyses.
     * @param report a report (must not be null)
     * @return true if the analysis list changed as a result of the call.
     */
    public static boolean addAnalyses(Collection<Analysis> analyses, TdReport report) {
        boolean added = true;
        for (Analysis analysis : analyses) {
            if (!report.addAnalysis(analysis)) {
                added = false;
            }
        }
        return added;
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

    /**
     * Method "setReportType".
     * 
     * MOD mzhao 2009-02-16
     * 
     * @param report the report object to update
     * @param reportType the report type to set
     * @param jrxmlFullPath the full path to the jxrxml file (can be null when the type of report is different from the
     * USER_DEFINED)
     * @return true if everything is set correctly, false otherwise.
     */
    public static boolean setReportType(TdReport report, Analysis analysis, ReportType reportType, String jrxmlFullPath) {
        boolean ok = true;
        switch (reportType) {
        case BASIC:
            report.setReportType(reportType.getLabel(), null, analysis);
            break;
        case EVOLUTION:
            report.setReportType(reportType.getLabel(), null, analysis);
            break;
        case USER_MADE:
            report.setReportType(reportType.getLabel(), jrxmlFullPath, analysis);
            if (StringUtils.isBlank(jrxmlFullPath)) {
                // do not log an error here
                ok = false;
            }
            break;
        default:
            ok = false;
            break;
        }
        return ok;
    }

    /**
     * 
     * DOC mzhao Set analysis filter date from.
     * 
     * @param report
     * @param dateText
     * @return
     */
    public static boolean setAnalysisFilterDate(TdReport report, String dateText, int datePos) {
        boolean ok = true;
        Date date = null;
        String pattern = "MM/dd/yyyy"; //$NON-NLS-1$
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            date = sdf.parse(dateText);
        } catch (ParseException e) {
            ok = false;
        }
        if (datePos == DATE_FROM) {
            report.setDateFrom(date);
        } else {
            report.setDateTo(date);
        }
        sdf = null;
        return ok;
    }

    /**
     * 
     * DOC mzhao Comment method "getReportType".
     * 
     * @param report
     * @param analysis
     * @return
     */
    public static String getReportType(TdReport report, Analysis analysis) {
        String reportType = null;
        EList<AnalysisMap> anMaps = report.getAnalysisMap();
        for (AnalysisMap anMap : anMaps) {
            if (ResourceHelper.areSame(analysis, anMap.getAnalysis())) {
                reportType = anMap.getReportType();
                break;
            }
        }
        return reportType;
    }

    public static String getReportJrxml(TdReport report, Analysis analysis) {
        String jrxmlSource = null;
        EList<AnalysisMap> anMaps = report.getAnalysisMap();
        for (AnalysisMap anMap : anMaps) {
            if (ResourceHelper.areSame(analysis, anMap.getAnalysis())) {
                jrxmlSource = anMap.getJrxmlSource();
                break;
            }
        }
        return jrxmlSource;
    }
}
