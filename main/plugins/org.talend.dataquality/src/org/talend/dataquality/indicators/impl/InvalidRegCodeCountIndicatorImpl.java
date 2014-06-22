/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.InvalidRegCodeCountIndicator;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Invalid Reg Code Count Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.InvalidRegCodeCountIndicatorImpl#getInvalidRegCount <em>Invalid Reg Count</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InvalidRegCodeCountIndicatorImpl extends IndicatorImpl implements InvalidRegCodeCountIndicator {
    /**
     * The default value of the '{@link #getInvalidRegCount() <em>Invalid Reg Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInvalidRegCount()
     * @generated
     * @ordered
     */
    protected static final Long INVALID_REG_COUNT_EDEFAULT = new Long(0L);

    /**
     * The cached value of the '{@link #getInvalidRegCount() <em>Invalid Reg Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInvalidRegCount()
     * @generated
     * @ordered
     */
    protected Long invalidRegCount = INVALID_REG_COUNT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected InvalidRegCodeCountIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.INVALID_REG_CODE_COUNT_INDICATOR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Long getInvalidRegCount() {
        return invalidRegCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInvalidRegCount(Long newInvalidRegCount) {
        Long oldInvalidRegCount = invalidRegCount;
        invalidRegCount = newInvalidRegCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.INVALID_REG_CODE_COUNT_INDICATOR__INVALID_REG_COUNT, oldInvalidRegCount, invalidRegCount));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Set<Object> getInvalidRegValues() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case IndicatorsPackage.INVALID_REG_CODE_COUNT_INDICATOR__INVALID_REG_COUNT:
                return getInvalidRegCount();
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
            case IndicatorsPackage.INVALID_REG_CODE_COUNT_INDICATOR__INVALID_REG_COUNT:
                setInvalidRegCount((Long)newValue);
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
            case IndicatorsPackage.INVALID_REG_CODE_COUNT_INDICATOR__INVALID_REG_COUNT:
                setInvalidRegCount(INVALID_REG_COUNT_EDEFAULT);
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
            case IndicatorsPackage.INVALID_REG_CODE_COUNT_INDICATOR__INVALID_REG_COUNT:
                return INVALID_REG_COUNT_EDEFAULT == null ? invalidRegCount != null : !INVALID_REG_COUNT_EDEFAULT.equals(invalidRegCount);
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
        result.append(" (invalidRegCount: ");
        result.append(invalidRegCount);
        result.append(')');
        return result.toString();
    }

    @Override
    public boolean handle(Object data) {
        this.mustStoreRow = false;
        super.handle(data);
        if (data == null) {
            return false;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        // IndicatorParameters indParameters = this.getParameters();
        // TextParameters textParameters = indParameters == null ? null : indParameters.getTextParameter();
        // String country = Locale.getDefault().getCountry();
        // if (textParameters != null) {
        // country = textParameters.getCountryCode();
        // }
        // PhoneNumber phhoneNum = phoneUtil.parseAndKeepRawInput(data.toString(), country);
        // String regionCodeForNumber = phoneUtil.getRegionCodeForNumber(phhoneNum);
        Set<String> supportedCountries = phoneUtil.getSupportedCountries();
        if (data == null || (data != null && !supportedCountries.contains(data.toString().toUpperCase()))) {
            this.invalidRegCount++;
            this.mustStoreRow = true;
        }

        return true;
    }

    @Override
    public boolean reset() {
        this.invalidRegCount = INVALID_REG_COUNT_EDEFAULT;
        return super.reset();
    }

    @Override
    public Long getIntegerValue() {
        return this.getInvalidRegCount();
    }

} //InvalidRegCodeCountIndicatorImpl
