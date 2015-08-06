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
package org.talend.dq.analysis.parameters;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class ParameterFactory {

    /**
     * DOC bZhou ParameterFactory constructor comment.
     */
    private ParameterFactory() {
    }

    /**
     * DOC bZhou Comment method "createAnalysisParameter".
     * 
     * @return
     */
    public static AnalysisParameter createAnalysisParameter() {
        return new AnalysisParameter();
    }

    /**
     * DOC bZhou Comment method "createAnalysisFilterParameter".
     * 
     * @return
     */
    public static AnalysisFilterParameter createAnalysisFilterParameter() {
        return new AnalysisFilterParameter();
    }

    /**
     * DOC bZhou Comment method "createAnalysisLabelParameter".
     * 
     * @return
     */
    public static AnalysisLabelParameter createAnalysisLabelParameter() {
        return new AnalysisLabelParameter();
    }

    /**
     * DOC bZhou Comment method "createAnalysisFuncationParameter".
     * 
     * @return
     */
    public static FuncationDependencyParameter createAnalysisFuncationParameter() {
        return new FuncationDependencyParameter();
    }

    /**
     * DOC bZhou Comment method "createNamedAnalysisParameter".
     * 
     * @return
     */
    public static NamedColumnSetAnalysisParameter createNamedAnalysisParameter() {
        return new NamedColumnSetAnalysisParameter();
    }

    /**
     * DOC bZhou Comment method "createPackagesAnalysisParameter".
     * 
     * @return
     */
    public static PackagesAnalyisParameter createPackagesAnalysisParameter() {
        return new PackagesAnalyisParameter();
    }

    /**
     * DOC bZhou Comment method "createDBConnectionParameter".
     * 
     * @return
     */
    public static DBConnectionParameter createDBConnectionParameter() {
        return new DBConnectionParameter();
    }

    /**
     * DOC bZhou Comment method "createDQRulesParameter".
     * 
     * @return
     */
    public static DQRulesParameter createDQRulesParameter() {
        return new DQRulesParameter();
    }

    /**
     * DOC bZhou Comment method "createPatternParameter".
     * 
     * @return
     */
    public static PatternParameter createPatternParameter() {
        return new PatternParameter();
    }

    /**
     * DOC bZhou Comment method "createReportParameter".
     * 
     * @return
     */
    public static ReportParameter createReportParameter() {
        return new ReportParameter();
    }

    /**
     * DOC bZhou Comment method "creatIndicatorParameter".
     * 
     * @return
     */
    public static UDIndicatorParameter creatIndicatorParameter() {
        return new UDIndicatorParameter();
    }
}
