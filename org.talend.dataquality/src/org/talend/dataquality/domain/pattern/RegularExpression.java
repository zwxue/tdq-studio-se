/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.domain.pattern;

import orgomg.cwm.objectmodel.core.Expression;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Regular Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.domain.pattern.RegularExpression#getExpression <em>Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.domain.pattern.PatternPackage#getRegularExpression()
 * @model
 * @generated
 */
public interface RegularExpression extends PatternComponent {

    /**
     * Returns the value of the '<em><b>Expression</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Expression</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Expression</em>' containment reference.
     * @see #setExpression(Expression)
     * @see org.talend.dataquality.domain.pattern.PatternPackage#getRegularExpression_Expression()
     * @model containment="true"
     * @generated
     */
    Expression getExpression();

    /**
     * Sets the value of the '{@link org.talend.dataquality.domain.pattern.RegularExpression#getExpression <em>Expression</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Expression</em>' containment reference.
     * @see #getExpression()
     * @generated
     */
    void setExpression(Expression value);
} // RegularExpression
