/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators;

import java.math.BigInteger;
import java.util.Set;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Duplicate Count Indicator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.DuplicateCountIndicator#getDuplicateValueCount <em>Duplicate Value Count</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.IndicatorsPackage#getDuplicateCountIndicator()
 * @model
 * @generated
 */
public interface DuplicateCountIndicator extends Indicator {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model kind="operation" dataType="org.talend.dataquality.indicators.JavaSet"
     * @generated
     */
    Set<Object> getDuplicateValues();

    /**
     * Returns the value of the '<em><b>Duplicate Value Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Duplicate Value Count</em>' attribute.
     * @see #setDuplicateValueCount(Long)
     * @see org.talend.dataquality.indicators.IndicatorsPackage#getDuplicateCountIndicator_DuplicateValueCount()
     * @model
     * @generated
     */
    Long getDuplicateValueCount();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.DuplicateCountIndicator#getDuplicateValueCount <em>Duplicate Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Duplicate Value Count</em>' attribute.
     * @see #getDuplicateValueCount()
     * @generated
     */
    void setDuplicateValueCount(Long value);
} // DuplicateCountIndicator
