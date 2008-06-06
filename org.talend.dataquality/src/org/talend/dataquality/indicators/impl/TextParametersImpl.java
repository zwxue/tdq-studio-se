/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MatchingAlgorithm;
import org.talend.dataquality.indicators.TextParameters;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Text Parameters</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.TextParametersImpl#isUseBlank <em>Use Blank</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.impl.TextParametersImpl#getMatchingAlgorithm <em>Matching Algorithm</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.impl.TextParametersImpl#isIgnoreCase <em>Ignore Case</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.impl.TextParametersImpl#isUseNulls <em>Use Nulls</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TextParametersImpl extends EObjectImpl implements TextParameters {
    /**
     * The default value of the '{@link #isUseBlank() <em>Use Blank</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUseBlank()
     * @generated
     * @ordered
     */
    protected static final boolean USE_BLANK_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isUseBlank() <em>Use Blank</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUseBlank()
     * @generated
     * @ordered
     */
    protected boolean useBlank = USE_BLANK_EDEFAULT;

    /**
     * The default value of the '{@link #getMatchingAlgorithm() <em>Matching Algorithm</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMatchingAlgorithm()
     * @generated
     * @ordered
     */
    protected static final MatchingAlgorithm MATCHING_ALGORITHM_EDEFAULT = MatchingAlgorithm.EXACT;

    /**
     * The cached value of the '{@link #getMatchingAlgorithm() <em>Matching Algorithm</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMatchingAlgorithm()
     * @generated
     * @ordered
     */
    protected MatchingAlgorithm matchingAlgorithm = MATCHING_ALGORITHM_EDEFAULT;

    /**
     * The default value of the '{@link #isIgnoreCase() <em>Ignore Case</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIgnoreCase()
     * @generated
     * @ordered
     */
    protected static final boolean IGNORE_CASE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIgnoreCase() <em>Ignore Case</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIgnoreCase()
     * @generated
     * @ordered
     */
    protected boolean ignoreCase = IGNORE_CASE_EDEFAULT;

    /**
     * The default value of the '{@link #isUseNulls() <em>Use Nulls</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUseNulls()
     * @generated
     * @ordered
     */
    protected static final boolean USE_NULLS_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isUseNulls() <em>Use Nulls</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUseNulls()
     * @generated
     * @ordered
     */
    protected boolean useNulls = USE_NULLS_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TextParametersImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.TEXT_PARAMETERS;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isUseBlank() {
        return useBlank;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUseBlank(boolean newUseBlank) {
        boolean oldUseBlank = useBlank;
        useBlank = newUseBlank;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.TEXT_PARAMETERS__USE_BLANK, oldUseBlank, useBlank));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MatchingAlgorithm getMatchingAlgorithm() {
        return matchingAlgorithm;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMatchingAlgorithm(MatchingAlgorithm newMatchingAlgorithm) {
        MatchingAlgorithm oldMatchingAlgorithm = matchingAlgorithm;
        matchingAlgorithm = newMatchingAlgorithm == null ? MATCHING_ALGORITHM_EDEFAULT : newMatchingAlgorithm;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.TEXT_PARAMETERS__MATCHING_ALGORITHM, oldMatchingAlgorithm, matchingAlgorithm));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIgnoreCase(boolean newIgnoreCase) {
        boolean oldIgnoreCase = ignoreCase;
        ignoreCase = newIgnoreCase;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.TEXT_PARAMETERS__IGNORE_CASE, oldIgnoreCase, ignoreCase));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isUseNulls() {
        return useNulls;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUseNulls(boolean newUseNulls) {
        boolean oldUseNulls = useNulls;
        useNulls = newUseNulls;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.TEXT_PARAMETERS__USE_NULLS, oldUseNulls, useNulls));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case IndicatorsPackage.TEXT_PARAMETERS__USE_BLANK:
                return isUseBlank() ? Boolean.TRUE : Boolean.FALSE;
            case IndicatorsPackage.TEXT_PARAMETERS__MATCHING_ALGORITHM:
                return getMatchingAlgorithm();
            case IndicatorsPackage.TEXT_PARAMETERS__IGNORE_CASE:
                return isIgnoreCase() ? Boolean.TRUE : Boolean.FALSE;
            case IndicatorsPackage.TEXT_PARAMETERS__USE_NULLS:
                return isUseNulls() ? Boolean.TRUE : Boolean.FALSE;
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
            case IndicatorsPackage.TEXT_PARAMETERS__USE_BLANK:
                setUseBlank(((Boolean)newValue).booleanValue());
                return;
            case IndicatorsPackage.TEXT_PARAMETERS__MATCHING_ALGORITHM:
                setMatchingAlgorithm((MatchingAlgorithm)newValue);
                return;
            case IndicatorsPackage.TEXT_PARAMETERS__IGNORE_CASE:
                setIgnoreCase(((Boolean)newValue).booleanValue());
                return;
            case IndicatorsPackage.TEXT_PARAMETERS__USE_NULLS:
                setUseNulls(((Boolean)newValue).booleanValue());
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
            case IndicatorsPackage.TEXT_PARAMETERS__USE_BLANK:
                setUseBlank(USE_BLANK_EDEFAULT);
                return;
            case IndicatorsPackage.TEXT_PARAMETERS__MATCHING_ALGORITHM:
                setMatchingAlgorithm(MATCHING_ALGORITHM_EDEFAULT);
                return;
            case IndicatorsPackage.TEXT_PARAMETERS__IGNORE_CASE:
                setIgnoreCase(IGNORE_CASE_EDEFAULT);
                return;
            case IndicatorsPackage.TEXT_PARAMETERS__USE_NULLS:
                setUseNulls(USE_NULLS_EDEFAULT);
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
            case IndicatorsPackage.TEXT_PARAMETERS__USE_BLANK:
                return useBlank != USE_BLANK_EDEFAULT;
            case IndicatorsPackage.TEXT_PARAMETERS__MATCHING_ALGORITHM:
                return matchingAlgorithm != MATCHING_ALGORITHM_EDEFAULT;
            case IndicatorsPackage.TEXT_PARAMETERS__IGNORE_CASE:
                return ignoreCase != IGNORE_CASE_EDEFAULT;
            case IndicatorsPackage.TEXT_PARAMETERS__USE_NULLS:
                return useNulls != USE_NULLS_EDEFAULT;
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
        result.append(" (useBlank: ");
        result.append(useBlank);
        result.append(", matchingAlgorithm: ");
        result.append(matchingAlgorithm);
        result.append(", ignoreCase: ");
        result.append(ignoreCase);
        result.append(", useNulls: ");
        result.append(useNulls);
        result.append(')');
        return result.toString();
    }

} //TextParametersImpl
