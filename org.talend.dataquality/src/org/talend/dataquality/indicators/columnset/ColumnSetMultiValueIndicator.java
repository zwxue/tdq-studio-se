/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.columnset;

import java.util.List;

import org.eclipse.emf.common.util.EList;

import org.talend.dataquality.indicators.Indicator;

import orgomg.cwm.resource.relational.Column;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Column Set Multi Value Indicator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator#getAnalyzedColumns <em>Analyzed Columns</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator#getListRows <em>List Rows</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator#getNumericFunctions <em>Numeric Functions</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator#getNominalColumns <em>Nominal Columns</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator#getNumericColumns <em>Numeric Columns</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#getColumnSetMultiValueIndicator()
 * @model
 * @generated
 */
public interface ColumnSetMultiValueIndicator extends Indicator {
    /**
     * Returns the value of the '<em><b>Analyzed Columns</b></em>' reference list.
     * The list contents are of type {@link orgomg.cwm.resource.relational.Column}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Analyzed Columns</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Analyzed Columns</em>' reference list.
     * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#getColumnSetMultiValueIndicator_AnalyzedColumns()
     * @model
     * @generated
     */
    EList<Column> getAnalyzedColumns();

    /**
     * Returns the value of the '<em><b>List Rows</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>List Rows</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>List Rows</em>' attribute.
     * @see #setListRows(List)
     * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#getColumnSetMultiValueIndicator_ListRows()
     * @model dataType="org.talend.dataquality.indicators.ObjectArray"
     * @generated
     */
    List<Object[]> getListRows();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator#getListRows <em>List Rows</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>List Rows</em>' attribute.
     * @see #getListRows()
     * @generated
     */
    void setListRows(List<Object[]> value);

    /**
     * Returns the value of the '<em><b>Numeric Functions</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Numeric Functions</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Numeric Functions</em>' attribute list.
     * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#getColumnSetMultiValueIndicator_NumericFunctions()
     * @model
     * @generated
     */
    EList<String> getNumericFunctions();

    /**
     * Returns the value of the '<em><b>Nominal Columns</b></em>' reference list.
     * The list contents are of type {@link orgomg.cwm.resource.relational.Column}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Nominal Columns</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Nominal Columns</em>' reference list.
     * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#getColumnSetMultiValueIndicator_NominalColumns()
     * @model transient="true" changeable="false" volatile="true"
     * @generated
     */
    EList<Column> getNominalColumns();

    /**
     * Returns the value of the '<em><b>Numeric Columns</b></em>' reference list.
     * The list contents are of type {@link orgomg.cwm.resource.relational.Column}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Numeric Columns</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Numeric Columns</em>' reference list.
     * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#getColumnSetMultiValueIndicator_NumericColumns()
     * @model transient="true" changeable="false" volatile="true"
     * @generated
     */
    EList<Column> getNumericColumns();

} // ColumnSetMultiValueIndicator
