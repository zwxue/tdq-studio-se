/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.reports;

import java.util.Date;
import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.analysis.Analysis;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Td Report</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getPresentationParams <em>Presentation Params</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getLastExecutionDate <em>Last Execution Date</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getAnalysisMap <em>Analysis Map</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getInputJrxml <em>Input Jrxml</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getOutputReportFolder <em>Output Report Folder</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getReportType <em>Report Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.reports.ReportsPackage#getTdReport()
 * @model
 * @generated
 */
public interface TdReport extends Report {

    /**
     * Returns the value of the '<em><b>Presentation Params</b></em>' containment reference list.
     * The list contents are of type {@link org.talend.dataquality.reports.PresentationParameter}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Presentation Params</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Presentation Params</em>' containment reference list.
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_PresentationParams()
     * @model containment="true"
     * @generated
     */
    EList<PresentationParameter> getPresentationParams();

    /**
     * Returns the value of the '<em><b>Creation Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Creation Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Creation Date</em>' attribute.
     * @see #setCreationDate(Date)
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_CreationDate()
     * @model
     * @generated
     */
    Date getCreationDate();

    /**
     * Sets the value of the '{@link org.talend.dataquality.reports.TdReport#getCreationDate <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Creation Date</em>' attribute.
     * @see #getCreationDate()
     * @generated
     */
    void setCreationDate(Date value);

    /**
     * Returns the value of the '<em><b>Last Execution Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The date of the last computation of the report. 
     * <!-- end-model-doc -->
     * @return the value of the '<em>Last Execution Date</em>' attribute.
     * @see #setLastExecutionDate(Date)
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_LastExecutionDate()
     * @model
     * @generated
     */
    Date getLastExecutionDate();

    /**
     * Sets the value of the '{@link org.talend.dataquality.reports.TdReport#getLastExecutionDate <em>Last Execution Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Last Execution Date</em>' attribute.
     * @see #getLastExecutionDate()
     * @generated
     */
    void setLastExecutionDate(Date value);

    /**
     * Returns the value of the '<em><b>Analysis Map</b></em>' containment reference list.
     * The list contents are of type {@link org.talend.dataquality.reports.AnalysisMap}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Analysis Map</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Analysis Map</em>' containment reference list.
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_AnalysisMap()
     * @model containment="true"
     * @generated
     */
    EList<AnalysisMap> getAnalysisMap();

    /**
     * Returns the value of the '<em><b>Input Jrxml</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The input jasper report xml file.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Input Jrxml</em>' attribute.
     * @see #setInputJrxml(String)
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_InputJrxml()
     * @model
     * @generated
     */
    String getInputJrxml();

    /**
     * Sets the value of the '{@link org.talend.dataquality.reports.TdReport#getInputJrxml <em>Input Jrxml</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Input Jrxml</em>' attribute.
     * @see #getInputJrxml()
     * @generated
     */
    void setInputJrxml(String value);

    /**
     * Returns the value of the '<em><b>Output Report Folder</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The folder where the reports generated by jasper will be archived.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Output Report Folder</em>' attribute.
     * @see #setOutputReportFolder(String)
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_OutputReportFolder()
     * @model
     * @generated
     */
    String getOutputReportFolder();

    /**
     * Sets the value of the '{@link org.talend.dataquality.reports.TdReport#getOutputReportFolder <em>Output Report Folder</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Output Report Folder</em>' attribute.
     * @see #getOutputReportFolder()
     * @generated
     */
    void setOutputReportFolder(String value);

    /**
     * Returns the value of the '<em><b>Report Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The type of report. Types of report can be "snapshot" or "temporal" for the moment. 
     * <!-- end-model-doc -->
     * @return the value of the '<em>Report Type</em>' attribute.
     * @see #setReportType(String)
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_ReportType()
     * @model
     * @generated
     */
    String getReportType();

    /**
     * Sets the value of the '{@link org.talend.dataquality.reports.TdReport#getReportType <em>Report Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Report Type</em>' attribute.
     * @see #getReportType()
     * @generated
     */
    void setReportType(String value);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean addAnalysis(Analysis analysis);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean removeAnalysis(Analysis analysis);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model mustRefreshDataType="orgomg.cwm.objectmodel.core.Boolean"
     * @generated
     */
    boolean setMustRefresh(Analysis analysis, boolean mustRefresh);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model mustRefreshDataType="orgomg.cwm.objectmodel.core.Boolean"
     * @generated
     */
    boolean addAnalysis(Analysis analysis, boolean mustRefresh);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean mustRefresh(Analysis analysis);
} // TdReport
