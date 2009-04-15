/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.sql;

import org.talend.dataquality.indicators.Indicator;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User Def Indicator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Indicator defined by the user. 
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.sql.UserDefIndicator#getUserCount <em>User Count</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.sql.IndicatorSqlPackage#getUserDefIndicator()
 * @model
 * @generated
 */
public interface UserDefIndicator extends Indicator {

    /**
     * Returns the value of the '<em><b>User Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The count value associated to this user defined indicator
     * <!-- end-model-doc -->
     * @return the value of the '<em>User Count</em>' attribute.
     * @see #setUserCount(Long)
     * @see org.talend.dataquality.indicators.sql.IndicatorSqlPackage#getUserDefIndicator_UserCount()
     * @model
     * @generated
     */
    Long getUserCount();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.sql.UserDefIndicator#getUserCount <em>User Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>User Count</em>' attribute.
     * @see #getUserCount()
     * @generated
     */
    void setUserCount(Long value);
} // UserDefIndicator
