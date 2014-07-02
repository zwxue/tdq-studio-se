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
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
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
 *   <li>{@link org.talend.dataquality.reports.TdReport#getAnalysisMap <em>Analysis Map</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getOutputReportFolder <em>Output Report Folder</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getExecInformations <em>Exec Informations</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getDateFrom <em>Date From</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getDateTo <em>Date To</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getLogo <em>Logo</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getInputJrxml <em>Input Jrxml</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getReportType <em>Report Type</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getContext <em>Context</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.TdReport#getDefaultContext <em>Default Context</em>}</li>
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
     * Returns the value of the '<em><b>Exec Informations</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Exec Informations</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Exec Informations</em>' containment reference.
     * @see #setExecInformations(ExecutionInformations)
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_ExecInformations()
     * @model containment="true"
     * @generated
     */
    ExecutionInformations getExecInformations();

    /**
     * Sets the value of the '{@link org.talend.dataquality.reports.TdReport#getExecInformations <em>Exec Informations</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Exec Informations</em>' containment reference.
     * @see #getExecInformations()
     * @generated
     */
    void setExecInformations(ExecutionInformations value);

    /**
     * Returns the value of the '<em><b>Date From</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Date From</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Date From</em>' attribute.
     * @see #setDateFrom(String)
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_DateFrom()
     * @model
     * @generated
     */
	String getDateFrom();

				/**
     * Sets the value of the '{@link org.talend.dataquality.reports.TdReport#getDateFrom <em>Date From</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Date From</em>' attribute.
     * @see #getDateFrom()
     * @generated
     */
    void setDateFrom(String value);

                /**
     * Returns the value of the '<em><b>Date To</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Date To</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Date To</em>' attribute.
     * @see #setDateTo(String)
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_DateTo()
     * @model
     * @generated
     */
	String getDateTo();

				/**
     * Sets the value of the '{@link org.talend.dataquality.reports.TdReport#getDateTo <em>Date To</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Date To</em>' attribute.
     * @see #getDateTo()
     * @generated
     */
    void setDateTo(String value);

                /**
     * Returns the value of the '<em><b>Logo</b></em>' attribute.
     * The default value is <code>""</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Logo</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Logo</em>' attribute.
     * @see #setLogo(String)
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_Logo()
     * @model default=""
     * @generated
     */
    String getLogo();

                /**
     * Sets the value of the '{@link org.talend.dataquality.reports.TdReport#getLogo <em>Logo</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Logo</em>' attribute.
     * @see #getLogo()
     * @generated
     */
    void setLogo(String value);

                /**
     * Returns the value of the '<em><b>Input Jrxml</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Jrxml</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * @deprecated This field has been removed to in ananysisMap.
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
     * Returns the value of the '<em><b>Report Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Report Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * @deprecated This field has been replaced by reportType in ananysisMap.
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
     * Returns the value of the '<em><b>Context</b></em>' containment reference list.
     * The list contents are of type {@link org.talend.designer.core.model.utils.emf.talendfile.ContextType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Context</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Context</em>' containment reference list.
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_Context()
     * @model containment="true"
     * @generated
     */
    EList<ContextType> getContext();

                /**
     * Returns the value of the '<em><b>Default Context</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Default Context</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Default Context</em>' attribute.
     * @see #setDefaultContext(String)
     * @see org.talend.dataquality.reports.ReportsPackage#getTdReport_DefaultContext()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     * @generated
     */
    String getDefaultContext();

                /**
     * Sets the value of the '{@link org.talend.dataquality.reports.TdReport#getDefaultContext <em>Default Context</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Default Context</em>' attribute.
     * @see #getDefaultContext()
     * @generated
     */
    void setDefaultContext(String value);

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

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    PresentationParameter setReportPresentationParam(String type, String formula);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    void setReportType(String reportType, String jrxmlSource, Analysis analysis);
} // TdReport
