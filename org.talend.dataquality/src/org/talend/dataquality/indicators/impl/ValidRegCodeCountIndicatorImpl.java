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
import org.talend.dataquality.indicators.ValidRegCodeCountIndicator;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Valid Reg Code Count Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.ValidRegCodeCountIndicatorImpl#getValidRegCount <em>Valid Reg Count</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ValidRegCodeCountIndicatorImpl extends IndicatorImpl implements ValidRegCodeCountIndicator {
    /**
     * The default value of the '{@link #getValidRegCount() <em>Valid Reg Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValidRegCount()
     * @generated
     * @ordered
     */
    protected static final Long VALID_REG_COUNT_EDEFAULT = new Long(0L);

    /**
     * The cached value of the '{@link #getValidRegCount() <em>Valid Reg Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValidRegCount()
     * @generated
     * @ordered
     */
    protected Long validRegCount = VALID_REG_COUNT_EDEFAULT;

    // private Set<Object> validRegPhoneObjects = new HashSet<Object>();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ValidRegCodeCountIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.VALID_REG_CODE_COUNT_INDICATOR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Long getValidRegCount() {
        return validRegCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setValidRegCount(Long newValidRegCount) {
        Long oldValidRegCount = validRegCount;
        validRegCount = newValidRegCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.VALID_REG_CODE_COUNT_INDICATOR__VALID_REG_COUNT, oldValidRegCount, validRegCount));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Set<Object> getValidRegValues() {
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
            case IndicatorsPackage.VALID_REG_CODE_COUNT_INDICATOR__VALID_REG_COUNT:
                return getValidRegCount();
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
            case IndicatorsPackage.VALID_REG_CODE_COUNT_INDICATOR__VALID_REG_COUNT:
                setValidRegCount((Long)newValue);
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
            case IndicatorsPackage.VALID_REG_CODE_COUNT_INDICATOR__VALID_REG_COUNT:
                setValidRegCount(VALID_REG_COUNT_EDEFAULT);
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
            case IndicatorsPackage.VALID_REG_CODE_COUNT_INDICATOR__VALID_REG_COUNT:
                return VALID_REG_COUNT_EDEFAULT == null ? validRegCount != null : !VALID_REG_COUNT_EDEFAULT.equals(validRegCount);
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
        result.append(" (validRegCount: ");
        result.append(validRegCount);
        result.append(')');
        return result.toString();
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#handle(java.lang.Object)
     */
    @Override
    public boolean handle(Object data) {

        this.mustStoreRow = false;
        super.handle(data);
        if (data == null) {
            return false;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        /*
         * IndicatorParameters indParameters = this.getParameters(); TextParameters textParameters = indParameters ==
         * null ? null : indParameters.getTextParameter(); String country = Locale.getDefault().getCountry(); if
         * (textParameters != null) { country = textParameters.getCountryCode(); } PhoneNumber phhoneNum =
         * phoneUtil.parse(data.toString(), country); if (phoneUtil.isValidNumber(phhoneNum)) { String
         * regionCodeForNumber = phoneUtil.getRegionCodeForNumber(phhoneNum); Set<String> supportedCountries =
         * phoneUtil.getSupportedCountries(); if (regionCodeForNumber != null &&
         * supportedCountries.contains(regionCodeForNumber.toUpperCase())) { super.handle(data); }
         * 
         * }
         */
        Set<String> supportedCountries = phoneUtil.getSupportedCountries();
        if (data != null && supportedCountries.contains(data.toString().toUpperCase())) {
            this.validRegCount++;
            this.mustStoreRow = true;
        }

        return true;
    }

    @Override
    public boolean reset() {
        this.validRegCount = VALID_REG_COUNT_EDEFAULT;
        return super.reset();
    }

    @Override
    public Long getIntegerValue() {
        return this.getValidRegCount();
    }

} //ValidRegCodeCountIndicatorImpl
