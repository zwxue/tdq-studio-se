/**
 * // ============================================================================
 * //
 * // Copyright (C) 2006-2009 Talend Inc. - www.talend.com
 * //
 * // This source code is available under agreement available at
 * // %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * //
 * // You should have received a copy of the agreement
 * // along with this program; if not, write to Talend SA
 * // 9 rue Pages 92150 Suresnes, France
 * //
 * // ============================================================================
 * 
 *
 * $Id$
 */
package org.talend.dataquality.rules;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Where Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This kind of rule contains a "where" clause which may be used to compute user defined indicators. 
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.rules.WhereRule#getWhereExpression <em>Where Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.rules.RulesPackage#getWhereRule()
 * @model
 * @generated
 */
public interface WhereRule extends SpecifiedDQRule {
    /**
     * Returns the value of the '<em><b>Where Expression</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Where Expression</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Where Expression</em>' attribute.
     * @see #setWhereExpression(String)
     * @see org.talend.dataquality.rules.RulesPackage#getWhereRule_WhereExpression()
     * @model
     * @generated
     */
    String getWhereExpression();

    /**
     * Sets the value of the '{@link org.talend.dataquality.rules.WhereRule#getWhereExpression <em>Where Expression</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Where Expression</em>' attribute.
     * @see #getWhereExpression()
     * @generated
     */
    void setWhereExpression(String value);

} // WhereRule
