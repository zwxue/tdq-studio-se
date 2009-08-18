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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.ReportsFactory;
import org.talend.dataquality.reports.TdReport;
import org.talend.utils.dates.DateUtils;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;
import orgomg.cwm.objectmodel.core.TaggedValue;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public final class ReportHelper {

    private static Logger log = Logger.getLogger(ReportHelper.class);

    public static final String DOT_MARK = "."; //$NON-NLS-1$

    // ~ADD mzhao 2009-02-05

    public static final String BASIC = "Basic"; // TODO externalize

    public static final String EVOLUTION = "Evolution"; // TODO externalize

    public static final String USER_DEFINED = "User defined"; // TODO externalize

    private static final TypedProperties PROPS = PropertiesLoader
            .getProperties(ReportHelper.class, "predefined_jrxml.properties");

    /**
     * The report types.
     * 
     * MOD mzhao 2009-02-19 MOD mzhao 2009-04-15 jrxml template files changed its location.
     */
    public static enum ReportType {

        // Analysis: multi column
        BASIC_MUTICOLUMN(BASIC, PROPS.getProperty("BASIC_MUTICOLUMN")), //$NON-NLS-1$
        EVOLUTION_MUTICOLUMN(EVOLUTION, PROPS.getProperty("EVOLUTION_MUTICOLUMN")), //$NON-NLS-1$
        // Analysis: connection
        BASIC_CONNECTION(BASIC, PROPS.getProperty("BASIC_CONNECTION")), //$NON-NLS-1$
        EVOLUTION_CONNECTION(EVOLUTION, PROPS.getProperty("EVOLUTION_CONNECTION")), //$NON-NLS-1$
        // EVOLUTION_CONNECTION(BASIC, "/reports/column/report_04.jrxml"),
        // Analysis: schema
        BASIC_SCHEMA(BASIC, PROPS.getProperty("BASIC_SCHEMA")), //$NON-NLS-1$
        EVOLUTION_SCHEMA(EVOLUTION, PROPS.getProperty("EVOLUTION_SCHEMA")), //$NON-NLS-1$
        // TODO assign type to specific jrxml
        // Analysis: catalog
        BASIC_CATALOG(BASIC, PROPS.getProperty("BASIC_CATALOG")), //$NON-NLS-1$
        EVOLUTION_CATALOG(EVOLUTION, PROPS.getProperty("EVOLUTION_CATALOG")), //$NON-NLS-1$
        // Analysis: table
        BASIC_TABLE(BASIC, PROPS.getProperty("BASIC_TABLE")), //$NON-NLS-1$
        EVOLUTION_TABLE(EVOLUTION, PROPS.getProperty("EVOLUTION_TABLE")), //$NON-NLS-1$
        // Analysis: column comparison
        BASIC_COLUMNS_COMPARISON(BASIC, PROPS.getProperty("BASIC_COLUMNS_COMPARISON")), //$NON-NLS-1$
        EVOLUTION_COLUMNS_COMPARISON(EVOLUTION, PROPS.getProperty("EVOLUTION_COLUMNS_COMPARISON")), //$NON-NLS-1$

        // Analysis: column correation
        BASIC_COLUMNS_CORRELATION(BASIC, PROPS.getProperty("BASIC_COLUMNS_CORRELATION")), //$NON-NLS-1$
        EVOLUTION_COLUMNS_CORRELATION(EVOLUTION, PROPS.getProperty("EVOLUTION_COLUMNS_CORRELATION")), //$NON-NLS-1$

        USER_MADE(USER_DEFINED, null); // for the user to set his own file path

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
                if (!list.contains(t.getLabel())) {
                    list.add(t.getLabel());
                }
            }
            return list;
        }

        /**
         * DOC qzhang Comment method "getReportType". MOD mzhao 2009-02-19
         * 
         * @param text
         * @return
         */
        public static ReportType getReportType(AnalysisType anaType, String text) {
            if (USER_MADE.getLabel().equals(text)) {
                return USER_MADE;
            }
            // Multi column
            if (anaType.getName().equals(AnalysisType.MULTIPLE_COLUMN.getName())) {
                if (BASIC_MUTICOLUMN.getLabel().equals(text)) {
                    return BASIC_MUTICOLUMN;
                } else if (EVOLUTION_MUTICOLUMN.getLabel().equals(text)) {
                    return EVOLUTION_MUTICOLUMN;
                }
            } else if (anaType.getName().equals(AnalysisType.CONNECTION.getName())) {
                if (BASIC_CONNECTION.getLabel().equals(text)) {
                    return BASIC_CONNECTION;
                } else if (EVOLUTION_CONNECTION.getLabel().equals(text)) {
                    return EVOLUTION_CONNECTION;
                }
            } else if (anaType.getName().equals(AnalysisType.SCHEMA.getName())) {
                if (BASIC_SCHEMA.getLabel().equals(text)) {
                    return BASIC_SCHEMA;
                } else if (EVOLUTION_SCHEMA.getLabel().equals(text)) {
                    return EVOLUTION_SCHEMA;
                }
            } else if (anaType.getName().equals(AnalysisType.CATALOG.getName())) {
                if (BASIC_CATALOG.getLabel().equals(text)) {
                    return BASIC_CATALOG;
                } else if (EVOLUTION_CATALOG.getLabel().equals(text)) {
                    return EVOLUTION_CATALOG;
                }
            } else if (anaType.getName().equals(AnalysisType.TABLE.getName())) {
                if (BASIC_TABLE.getLabel().equals(text)) {
                    return BASIC_TABLE;
                } else if (EVOLUTION_TABLE.getLabel().equals(text)) {
                    return EVOLUTION_TABLE;
                }
            } else if (anaType.getName().equals(AnalysisType.COLUMNS_COMPARISON.getName())) {
                if (BASIC_COLUMNS_COMPARISON.getLabel().equals(text)) {
                    return BASIC_COLUMNS_COMPARISON;
                } else if (EVOLUTION_COLUMNS_COMPARISON.getLabel().equals(text)) {
                    return EVOLUTION_COLUMNS_COMPARISON;
                }
            } else if (anaType.getName().equals(AnalysisType.COLUMN_CORRELATION.getName())) {
                if (BASIC_COLUMNS_CORRELATION.getLabel().equals(text)) {
                    return BASIC_COLUMNS_CORRELATION;
                } else if (EVOLUTION_COLUMNS_CORRELATION.getLabel().equals(text)) {
                    return EVOLUTION_COLUMNS_CORRELATION;
                }
            }

            return null;
        }

        public static ReportType getReportType(Analysis ana, String text) {
            AnalysisType at = AnalysisHelper.getAnalysisType(ana);
            return getReportType(at, text);
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
        reportType = reportType == null ? ReportHelper.ReportType.USER_MADE : reportType;
        switch (reportType) {
        case USER_MADE:
            report.setReportType(reportType.getLabel(), jrxmlFullPath, analysis);
            if (StringUtils.isBlank(jrxmlFullPath)) {
                // do not log an error here
                ok = false;
            }
            break;
        default:
            report.setReportType(reportType.getLabel(), null, analysis);
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
     * @throws ParseException
     */
    public static void setAnalysisFilterDateFrom(TdReport report, String dateText) {
        if (dateText == null || dateText.trim().equals("")) {
            return;
        }
        try {
            report.setDateFrom(DateUtils.parse(DateUtils.PATTERN_1, dateText));
        } catch (ParseException e) {
            log.error(e, e);
        }
    }

    /**
     * 
     * DOC mzhao Comment method "setAnalysisFilterDateFrom".
     * 
     * @param report
     * @param dateText
     * @return
     * @return
     * @throws ParseException
     */
    public static void setAnalysisFilterDateTo(TdReport report, String dateText) {
        if (dateText == null || dateText.trim().equals("")) {
            return;
        }
        try {
            report.setDateTo(DateUtils.parse(DateUtils.PATTERN_1, dateText));
        } catch (ParseException e) {
            log.error(e, e);
        }
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

    /**
     * DOC bZhou Comment method "setOutputType".
     * 
     * @param outputType
     * @param report
     * @return
     */
    public static boolean setOutputType(String outputType, Report report) {
        return TaggedValueHelper.setTaggedValue(report, TaggedValueHelper.OUTPUT_TYPE_TAG, outputType);
    }

    /**
     * DOC bZhou Comment method "getOutputType".
     * 
     * @param tag
     * @param element
     * @return
     */
    public static String getOutputType(Report report) {
        TaggedValue taggedValue = TaggedValueHelper.getTaggedValue(TaggedValueHelper.OUTPUT_TYPE_TAG, report.getTaggedValue());
        if (taggedValue == null) {
            return "pdf";
        }
        return taggedValue.getValue();
    }

    /**
     * DOC bZhou Comment method "setSingleGenReport".
     * 
     * @param single
     * @param report
     * @return
     */
    public static boolean setSingleGenReport(Boolean single, Report report) {
        String statusStr = String.valueOf(single);
        return TaggedValueHelper.setTaggedValue(report, TaggedValueHelper.GEN_SINGLE_REPORT, statusStr);
    }

    /**
     * DOC bZhou Comment method "getSingleGenReport".
     * 
     * @param report
     * @return
     */
    public static Boolean getSingleGenReport(Report report) {
        TaggedValue taggedValue = TaggedValueHelper.getTaggedValue(TaggedValueHelper.GEN_SINGLE_REPORT, report.getTaggedValue());
        if (taggedValue == null) {
            return false;
        }
        return Boolean.valueOf(taggedValue.getValue());
    }

    /**
     * DOC bZhou Comment method "setOutputFileName".
     * 
     * @param fileName
     * @param report
     * @return
     */
    public static boolean setOutputFileName(String fileName, Report report) {
        return TaggedValueHelper.setTaggedValue(report, TaggedValueHelper.OUTPUT_FILENAME_TAG, fileName);
    }

    /**
     * DOC bZhou Comment method "getOutputFileName".
     * 
     * @param report
     * @return
     */
    public static String getOutputFileName(Report report) {
        TaggedValue taggedValue = TaggedValueHelper
                .getTaggedValue(TaggedValueHelper.OUTPUT_FILENAME_TAG, report.getTaggedValue());
        if (taggedValue == null) {
            return "";
        }

        return taggedValue.getValue();
    }

    /**
     * DOC bZhou Comment method "setOutputFolderName".
     * 
     * @param folderName
     * @param report
     * @return
     */
    public static boolean setOutputFolderName(String folderName, Report report) {
        return TaggedValueHelper.setTaggedValue(report, TaggedValueHelper.OUTPUT_FOLDER_TAG, folderName);
    }

    /**
     * DOC bZhou Comment method "getOutputFolderName".
     * 
     * @param report
     * @return
     */
    public static String getOutputFolderNameAssinged(Report report) {
        TaggedValue taggedValue = TaggedValueHelper.getTaggedValue(TaggedValueHelper.OUTPUT_FOLDER_TAG, report.getTaggedValue());
        if (taggedValue == null) {
            return "";
        }

        return taggedValue.getValue();
    }

    /**
     * DOC xqliu Comment method "getOutputName".
     * 
     * @param reportContainer
     * @param simpleName
     * @return
     */
    public static String getOutputFolderNameDefault(IFolder reportContainer, String simpleName) {
        return ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toOSString()
                + reportContainer.getFolder("." + simpleName).getFullPath().toOSString();
    }

    /**
     * DOC xqliu Comment method "getOutputFolder".
     * 
     * @param reportFile
     * @return
     */
    public static IFolder getOutputFolder(IFile reportFile) {
        IFolder reportContainer = (IFolder) reportFile.getParent();
        String fileName = reportFile.getName();
        String simpleName = null;
        int indexOf = fileName.indexOf(DOT_MARK);
        if (indexOf != -1) {
            simpleName = fileName.substring(0, indexOf);
        } else {
            log.error("The current report file name: " + reportFile.getFullPath() + " is a illegal name."); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
        return reportContainer.getFolder(DOT_MARK + simpleName);
    }

}
