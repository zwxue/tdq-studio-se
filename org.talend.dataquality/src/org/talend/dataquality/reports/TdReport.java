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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean addAnalysis(Analysis analysis);
} // TdReport
