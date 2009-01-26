/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.rules.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.talend.dataquality.rules.RulesPackage;
import org.talend.dataquality.rules.WhereRule;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Where Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.rules.impl.WhereRuleImpl#getWhereExpression <em>Where Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WhereRuleImpl extends SpecifiedDQRuleImpl implements WhereRule {
    /**
     * The default value of the '{@link #getWhereExpression() <em>Where Expression</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWhereExpression()
     * @generated
     * @ordered
     */
    protected static final String WHERE_EXPRESSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getWhereExpression() <em>Where Expression</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWhereExpression()
     * @generated
     * @ordered
     */
    protected String whereExpression = WHERE_EXPRESSION_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected WhereRuleImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RulesPackage.Literals.WHERE_RULE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getWhereExpression() {
        return whereExpression;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setWhereExpression(String newWhereExpression) {
        String oldWhereExpression = whereExpression;
        whereExpression = newWhereExpression;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RulesPackage.WHERE_RULE__WHERE_EXPRESSION, oldWhereExpression, whereExpression));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case RulesPackage.WHERE_RULE__WHERE_EXPRESSION:
                return getWhereExpression();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case RulesPackage.WHERE_RULE__WHERE_EXPRESSION:
                setWhereExpression((String)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case RulesPackage.WHERE_RULE__WHERE_EXPRESSION:
                setWhereExpression(WHERE_EXPRESSION_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case RulesPackage.WHERE_RULE__WHERE_EXPRESSION:
                return WHERE_EXPRESSION_EDEFAULT == null ? whereExpression != null : !WHERE_EXPRESSION_EDEFAULT.equals(whereExpression);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (whereExpression: ");
        result.append(whereExpression);
        result.append(')');
        return result.toString();
    }

} //WhereRuleImpl
