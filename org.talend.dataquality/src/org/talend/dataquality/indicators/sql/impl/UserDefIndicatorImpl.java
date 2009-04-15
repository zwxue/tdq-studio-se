/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.sql.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.indicators.impl.IndicatorImpl;
import org.talend.dataquality.indicators.sql.IndicatorSqlPackage;
import org.talend.dataquality.indicators.sql.UserDefIndicator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>User Def Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.sql.impl.UserDefIndicatorImpl#getUserCount <em>User Count</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UserDefIndicatorImpl extends IndicatorImpl implements UserDefIndicator {
    /**
     * The default value of the '{@link #getUserCount() <em>User Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUserCount()
     * @generated
     * @ordered
     */
    protected static final Long USER_COUNT_EDEFAULT = null;
    /**
     * The cached value of the '{@link #getUserCount() <em>User Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUserCount()
     * @generated
     * @ordered
     */
    protected Long userCount = USER_COUNT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected UserDefIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorSqlPackage.Literals.USER_DEF_INDICATOR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Long getUserCount() {
        return userCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUserCount(Long newUserCount) {
        Long oldUserCount = userCount;
        userCount = newUserCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorSqlPackage.USER_DEF_INDICATOR__USER_COUNT, oldUserCount, userCount));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case IndicatorSqlPackage.USER_DEF_INDICATOR__USER_COUNT:
                return getUserCount();
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
            case IndicatorSqlPackage.USER_DEF_INDICATOR__USER_COUNT:
                setUserCount((Long)newValue);
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
            case IndicatorSqlPackage.USER_DEF_INDICATOR__USER_COUNT:
                setUserCount(USER_COUNT_EDEFAULT);
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
            case IndicatorSqlPackage.USER_DEF_INDICATOR__USER_COUNT:
                return USER_COUNT_EDEFAULT == null ? userCount != null : !USER_COUNT_EDEFAULT.equals(userCount);
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
        result.append(" (userCount: ");
        result.append(userCount);
        result.append(')');
        return result.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#getIntegerValue()
     * 
     * @generated NOT getIntegerValue() returns the user count value
     */
    @Override
    public Long getIntegerValue() {
        return this.getUserCount();
    }

    
} //UserDefIndicatorImpl
