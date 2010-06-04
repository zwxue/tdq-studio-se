/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.expressions;

import orgomg.cwm.objectmodel.core.Expression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Td Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.expressions.TdExpression#getVersion <em>Version</em>}</li>
 *   <li>{@link org.talend.dataquality.expressions.TdExpression#getModificationDate <em>Modification Date</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.expressions.ExpressionsPackage#getTdExpression()
 * @model
 * @generated
 */
public interface TdExpression extends Expression {
    /**
     * Returns the value of the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Version</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Version</em>' attribute.
     * @see #setVersion(String)
     * @see org.talend.dataquality.expressions.ExpressionsPackage#getTdExpression_Version()
     * @model dataType="orgomg.cwm.objectmodel.core.String"
     * @generated
     */
    String getVersion();

    /**
     * Sets the value of the '{@link org.talend.dataquality.expressions.TdExpression#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see #getVersion()
     * @generated
     */
    void setVersion(String value);

    /**
     * Returns the value of the '<em><b>Modification Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Modification Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Modification Date</em>' attribute.
     * @see #setModificationDate(String)
     * @see org.talend.dataquality.expressions.ExpressionsPackage#getTdExpression_ModificationDate()
     * @model dataType="orgomg.cwm.objectmodel.core.Time"
     * @generated
     */
    String getModificationDate();

    /**
     * Sets the value of the '{@link org.talend.dataquality.expressions.TdExpression#getModificationDate <em>Modification Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Modification Date</em>' attribute.
     * @see #getModificationDate()
     * @generated
     */
    void setModificationDate(String value);

} // TdExpression
