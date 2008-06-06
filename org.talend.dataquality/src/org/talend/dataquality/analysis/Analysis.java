/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.analysis;

import java.util.Date;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.talend.dataquality.indicators.Indicator;
import orgomg.cwmx.analysis.informationreporting.ReportGroup;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Analysis</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.analysis.Analysis#getContext <em>Context</em>}</li>
 *   <li>{@link org.talend.dataquality.analysis.Analysis#getResults <em>Results</em>}</li>
 *   <li>{@link org.talend.dataquality.analysis.Analysis#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.talend.dataquality.analysis.Analysis#getCreationDate <em>Creation Date</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.analysis.AnalysisPackage#getAnalysis()
 * @model
 * @generated
 */
public interface Analysis extends ReportGroup {
    /**
     * Returns the value of the '<em><b>Context</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Context</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Context</em>' containment reference.
     * @see #setContext(AnalysisContext)
     * @see org.talend.dataquality.analysis.AnalysisPackage#getAnalysis_Context()
     * @model containment="true"
     * @generated
     */
    AnalysisContext getContext();

    /**
     * Sets the value of the '{@link org.talend.dataquality.analysis.Analysis#getContext <em>Context</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Context</em>' containment reference.
     * @see #getContext()
     * @generated
     */
    void setContext(AnalysisContext value);

    /**
     * Returns the value of the '<em><b>Results</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link org.talend.dataquality.analysis.AnalysisResult#getAnalysis <em>Analysis</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Results</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Results</em>' containment reference.
     * @see #setResults(AnalysisResult)
     * @see org.talend.dataquality.analysis.AnalysisPackage#getAnalysis_Results()
     * @see org.talend.dataquality.analysis.AnalysisResult#getAnalysis
     * @model opposite="analysis" containment="true"
     * @generated
     */
    AnalysisResult getResults();

    /**
     * Sets the value of the '{@link org.talend.dataquality.analysis.Analysis#getResults <em>Results</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Results</em>' containment reference.
     * @see #getResults()
     * @generated
     */
    void setResults(AnalysisResult value);

    /**
     * Returns the value of the '<em><b>Parameters</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' containment reference.
     * @see #setParameters(AnalysisParameters)
     * @see org.talend.dataquality.analysis.AnalysisPackage#getAnalysis_Parameters()
     * @model containment="true"
     * @generated
     */
    AnalysisParameters getParameters();

    /**
     * Sets the value of the '{@link org.talend.dataquality.analysis.Analysis#getParameters <em>Parameters</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parameters</em>' containment reference.
     * @see #getParameters()
     * @generated
     */
    void setParameters(AnalysisParameters value);

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
     * @see org.talend.dataquality.analysis.AnalysisPackage#getAnalysis_CreationDate()
     * @model
     * @generated
     */
    Date getCreationDate();

    /**
     * Sets the value of the '{@link org.talend.dataquality.analysis.Analysis#getCreationDate <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Creation Date</em>' attribute.
     * @see #getCreationDate()
     * @generated
     */
    void setCreationDate(Date value);

} // Analysis
