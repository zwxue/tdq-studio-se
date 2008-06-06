/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.sql;

import java.util.Date;

import org.eclipse.emf.common.util.EList;

import org.talend.dataquality.indicators.Indicator;

import orgomg.cwm.objectmodel.core.Expression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sql Indicator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.sql.SqlIndicator#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.sql.SqlIndicator#getLastModificationDate <em>Last Modification Date</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.sql.IndicatorSqlPackage#getSqlIndicator()
 * @model
 * @generated
 */
public interface SqlIndicator extends Indicator {
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
     * @see org.talend.dataquality.indicators.sql.IndicatorSqlPackage#getSqlIndicator_CreationDate()
     * @model
     * @generated
     */
    Date getCreationDate();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.sql.SqlIndicator#getCreationDate <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Creation Date</em>' attribute.
     * @see #getCreationDate()
     * @generated
     */
    void setCreationDate(Date value);

    /**
     * Returns the value of the '<em><b>Last Modification Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Last Modification Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Last Modification Date</em>' attribute.
     * @see #setLastModificationDate(Date)
     * @see org.talend.dataquality.indicators.sql.IndicatorSqlPackage#getSqlIndicator_LastModificationDate()
     * @model
     * @generated
     */
    Date getLastModificationDate();

    /**
     * Sets the value of the '{@link org.talend.dataquality.indicators.sql.SqlIndicator#getLastModificationDate <em>Last Modification Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Last Modification Date</em>' attribute.
     * @see #getLastModificationDate()
     * @generated
     */
    void setLastModificationDate(Date value);

} // SqlIndicator
