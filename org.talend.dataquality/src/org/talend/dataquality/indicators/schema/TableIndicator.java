/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.schema;

import org.talend.dataquality.indicators.CompositeIndicator;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Table Indicator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.schema.TableIndicator#getRowCount <em>Row Count</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.schema.SchemaPackage#getTableIndicator()
 * @model
 * @generated
 */
public interface TableIndicator extends CompositeIndicator {
    /**
     * Returns the value of the '<em><b>Row Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Row Count</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Row Count</em>' attribute.
     * @see #setRowCount(long)
     * @see org.talend.dataquality.indicators.schema.SchemaPackage#getTableIndicator_RowCount()
     * @model
     * @generated
     */
    long getRowCount();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.schema.TableIndicator#getRowCount <em>Row Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Row Count</em>' attribute.
     * @see #getRowCount()
     * @generated
     */
    void setRowCount(long value);

} // TableIndicator
