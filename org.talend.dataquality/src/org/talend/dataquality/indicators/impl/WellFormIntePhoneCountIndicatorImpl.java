/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.Locale;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.dataquality.indicators.WellFormIntePhoneCountIndicator;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Well Form Inte Phone Count Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.WellFormIntePhoneCountIndicatorImpl#getWellFormIntePhoneCount <em>Well Form Inte Phone Count</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WellFormIntePhoneCountIndicatorImpl extends IndicatorImpl implements WellFormIntePhoneCountIndicator {

    protected final String PLUS_SIGN = "+";//$NON-NLS-1$
    /**
     * The default value of the '{@link #getWellFormIntePhoneCount() <em>Well Form Inte Phone Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWellFormIntePhoneCount()
     * @generated
     * @ordered
     */
    protected static final Long WELL_FORM_INTE_PHONE_COUNT_EDEFAULT = new Long(0L);

    /**
     * The cached value of the '{@link #getWellFormIntePhoneCount() <em>Well Form Inte Phone Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWellFormIntePhoneCount()
     * @generated
     * @ordered
     */
    protected Long wellFormIntePhoneCount = WELL_FORM_INTE_PHONE_COUNT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected WellFormIntePhoneCountIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.WELL_FORM_INTE_PHONE_COUNT_INDICATOR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Long getWellFormIntePhoneCount() {
        return wellFormIntePhoneCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setWellFormIntePhoneCount(Long newWellFormIntePhoneCount) {
        Long oldWellFormIntePhoneCount = wellFormIntePhoneCount;
        wellFormIntePhoneCount = newWellFormIntePhoneCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.WELL_FORM_INTE_PHONE_COUNT_INDICATOR__WELL_FORM_INTE_PHONE_COUNT, oldWellFormIntePhoneCount, wellFormIntePhoneCount));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Set<Object> getWellFormIntePhoneValues() {
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
            case IndicatorsPackage.WELL_FORM_INTE_PHONE_COUNT_INDICATOR__WELL_FORM_INTE_PHONE_COUNT:
                return getWellFormIntePhoneCount();
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
            case IndicatorsPackage.WELL_FORM_INTE_PHONE_COUNT_INDICATOR__WELL_FORM_INTE_PHONE_COUNT:
                setWellFormIntePhoneCount((Long)newValue);
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
            case IndicatorsPackage.WELL_FORM_INTE_PHONE_COUNT_INDICATOR__WELL_FORM_INTE_PHONE_COUNT:
                setWellFormIntePhoneCount(WELL_FORM_INTE_PHONE_COUNT_EDEFAULT);
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
            case IndicatorsPackage.WELL_FORM_INTE_PHONE_COUNT_INDICATOR__WELL_FORM_INTE_PHONE_COUNT:
                return WELL_FORM_INTE_PHONE_COUNT_EDEFAULT == null ? wellFormIntePhoneCount != null : !WELL_FORM_INTE_PHONE_COUNT_EDEFAULT.equals(wellFormIntePhoneCount);
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
        result.append(" (wellFormIntePhoneCount: ");
        result.append(wellFormIntePhoneCount);
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
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            IndicatorParameters indParameters = this.getParameters();
            TextParameters textParameters = indParameters == null ? null : indParameters.getTextParameter();
            String country = Locale.getDefault().getCountry();
            if (textParameters != null) {
                country = textParameters.getCountryCode();
            }
            PhoneNumber phoneNumeber = phoneUtil.parse(data.toString(), country);
            String nationalSignificantNumber = PhoneNumberUtil.getNationalSignificantNumber(phoneNumeber);
            int countryCode = phoneNumeber.getCountryCode();
            if (data.toString().startsWith(PLUS_SIGN + countryCode)
                    && data.toString().equals(PLUS_SIGN + countryCode + PluginConstant.SPACE_STRING + nationalSignificantNumber)) {
                wellFormIntePhoneCount++;
                this.mustStoreRow = true;
            }

        } catch (NumberParseException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean reset() {
        this.wellFormIntePhoneCount = WELL_FORM_INTE_PHONE_COUNT_EDEFAULT;
        return super.reset();
    }

    @Override
    public Long getIntegerValue() {
        return this.getWellFormIntePhoneCount();
    }

} //WellFormIntePhoneCountIndicatorImpl
