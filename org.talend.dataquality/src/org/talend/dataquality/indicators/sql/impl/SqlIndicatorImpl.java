/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.sql.impl;

import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.talend.dataquality.indicators.impl.IndicatorImpl;

import org.talend.dataquality.indicators.sql.IndicatorSqlPackage;
import org.talend.dataquality.indicators.sql.SqlIndicator;

import orgomg.cwm.objectmodel.core.Expression;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sql Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.sql.impl.SqlIndicatorImpl#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.sql.impl.SqlIndicatorImpl#getLastModificationDate <em>Last Modification Date</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SqlIndicatorImpl extends IndicatorImpl implements SqlIndicator {
    /**
     * The default value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCreationDate()
     * @generated
     * @ordered
     */
    protected static final Date CREATION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCreationDate()
     * @generated
     * @ordered
     */
    protected Date creationDate = CREATION_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getLastModificationDate() <em>Last Modification Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastModificationDate()
     * @generated
     * @ordered
     */
    protected static final Date LAST_MODIFICATION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLastModificationDate() <em>Last Modification Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastModificationDate()
     * @generated
     * @ordered
     */
    protected Date lastModificationDate = LAST_MODIFICATION_DATE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SqlIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorSqlPackage.Literals.SQL_INDICATOR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCreationDate(Date newCreationDate) {
        Date oldCreationDate = creationDate;
        creationDate = newCreationDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorSqlPackage.SQL_INDICATOR__CREATION_DATE, oldCreationDate, creationDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLastModificationDate(Date newLastModificationDate) {
        Date oldLastModificationDate = lastModificationDate;
        lastModificationDate = newLastModificationDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorSqlPackage.SQL_INDICATOR__LAST_MODIFICATION_DATE, oldLastModificationDate, lastModificationDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case IndicatorSqlPackage.SQL_INDICATOR__CREATION_DATE:
                return getCreationDate();
            case IndicatorSqlPackage.SQL_INDICATOR__LAST_MODIFICATION_DATE:
                return getLastModificationDate();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case IndicatorSqlPackage.SQL_INDICATOR__CREATION_DATE:
                setCreationDate((Date)newValue);
                return;
            case IndicatorSqlPackage.SQL_INDICATOR__LAST_MODIFICATION_DATE:
                setLastModificationDate((Date)newValue);
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
            case IndicatorSqlPackage.SQL_INDICATOR__CREATION_DATE:
                setCreationDate(CREATION_DATE_EDEFAULT);
                return;
            case IndicatorSqlPackage.SQL_INDICATOR__LAST_MODIFICATION_DATE:
                setLastModificationDate(LAST_MODIFICATION_DATE_EDEFAULT);
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
            case IndicatorSqlPackage.SQL_INDICATOR__CREATION_DATE:
                return CREATION_DATE_EDEFAULT == null ? creationDate != null : !CREATION_DATE_EDEFAULT.equals(creationDate);
            case IndicatorSqlPackage.SQL_INDICATOR__LAST_MODIFICATION_DATE:
                return LAST_MODIFICATION_DATE_EDEFAULT == null ? lastModificationDate != null : !LAST_MODIFICATION_DATE_EDEFAULT.equals(lastModificationDate);
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
        result.append(" (creationDate: ");
        result.append(creationDate);
        result.append(", lastModificationDate: ");
        result.append(lastModificationDate);
        result.append(')');
        return result.toString();
    }

} //SqlIndicatorImpl
