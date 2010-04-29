/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.analysis;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Analyzed Data Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The data analyzed by an indicator.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.analysis.AnalyzedDataSet#getDataCount <em>Data Count</em>}</li>
 *   <li>{@link org.talend.dataquality.analysis.AnalyzedDataSet#getRecordSize <em>Record Size</em>}</li>
 *   <li>{@link org.talend.dataquality.analysis.AnalyzedDataSet#getData <em>Data</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.analysis.AnalysisPackage#getAnalyzedDataSet()
 * @model
 * @generated
 */
public interface AnalyzedDataSet extends EObject {
    /**
     * Returns the value of the '<em><b>Data Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The number of data which are concerned by the indicator's result. This number may be greater than the size of the records stored in this analyzed data set. The reason is that not all record may be stored here.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Data Count</em>' attribute.
     * @see #setDataCount(int)
     * @see org.talend.dataquality.analysis.AnalysisPackage#getAnalyzedDataSet_DataCount()
     * @model
     * @generated
     */
    int getDataCount();

    /**
     * Sets the value of the '{@link org.talend.dataquality.analysis.AnalyzedDataSet#getDataCount <em>Data Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Data Count</em>' attribute.
     * @see #getDataCount()
     * @generated
     */
    void setDataCount(int value);

    /**
     * Returns the value of the '<em><b>Record Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The size of the record (e.g. the number of analyzed columns)
     * <!-- end-model-doc -->
     * @return the value of the '<em>Record Size</em>' attribute.
     * @see #setRecordSize(int)
     * @see org.talend.dataquality.analysis.AnalysisPackage#getAnalyzedDataSet_RecordSize()
     * @model
     * @generated
     */
    int getRecordSize();

    /**
     * Sets the value of the '{@link org.talend.dataquality.analysis.AnalyzedDataSet#getRecordSize <em>Record Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Record Size</em>' attribute.
     * @see #getRecordSize()
     * @generated
     */
    void setRecordSize(int value);

    /**
     * Returns the value of the '<em><b>Data</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * the analyzed data.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Data</em>' attribute.
     * @see #setData(List)
     * @see org.talend.dataquality.analysis.AnalysisPackage#getAnalyzedDataSet_Data()
     * @model dataType="org.talend.dataquality.indicators.ObjectArray"
     * @generated
     */
    List<Object[]> getData();

    /**
     * Sets the value of the '{@link org.talend.dataquality.analysis.AnalyzedDataSet#getData <em>Data</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Data</em>' attribute.
     * @see #getData()
     * @generated
     */
    void setData(List<Object[]> value);

} // AnalyzedDataSet
