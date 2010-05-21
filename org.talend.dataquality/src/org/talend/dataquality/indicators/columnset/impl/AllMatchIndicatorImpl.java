/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.columnset.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MatchingIndicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.columnset.AllMatchIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>All Match Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.AllMatchIndicatorImpl#getMatchingValueCount <em>Matching Value Count</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.AllMatchIndicatorImpl#getNotMatchingValueCount <em>Not Matching Value Count</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AllMatchIndicatorImpl extends ColumnSetMultiValueIndicatorImpl implements AllMatchIndicator {

    private EList<RegexpMatchingIndicator> compositeRegexMatchingIndicators = new BasicEList<RegexpMatchingIndicator>();
    /**
     * The default value of the '{@link #getMatchingValueCount() <em>Matching Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMatchingValueCount()
     * @generated
     * @ordered
     */
    protected static final Long MATCHING_VALUE_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMatchingValueCount() <em>Matching Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMatchingValueCount()
     * @generated
     * @ordered
     */
    protected Long matchingValueCount = MATCHING_VALUE_COUNT_EDEFAULT;

    /**
     * The default value of the '{@link #getNotMatchingValueCount() <em>Not Matching Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNotMatchingValueCount()
     * @generated
     * @ordered
     */
    protected static final Long NOT_MATCHING_VALUE_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNotMatchingValueCount() <em>Not Matching Value Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNotMatchingValueCount()
     * @generated
     * @ordered
     */
    protected Long notMatchingValueCount = NOT_MATCHING_VALUE_COUNT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AllMatchIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ColumnsetPackage.Literals.ALL_MATCH_INDICATOR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Long getMatchingValueCount() {
        return matchingValueCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMatchingValueCount(Long newMatchingValueCount) {
        Long oldMatchingValueCount = matchingValueCount;
        matchingValueCount = newMatchingValueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT, oldMatchingValueCount, matchingValueCount));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Long getNotMatchingValueCount() {
        return notMatchingValueCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNotMatchingValueCount(Long newNotMatchingValueCount) {
        Long oldNotMatchingValueCount = notMatchingValueCount;
        notMatchingValueCount = newNotMatchingValueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ColumnsetPackage.ALL_MATCH_INDICATOR__NOT_MATCHING_VALUE_COUNT, oldNotMatchingValueCount, notMatchingValueCount));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public EList<RegexpMatchingIndicator> getCompositeRegexMatchingIndicators() {
        return compositeRegexMatchingIndicators;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT:
                return getMatchingValueCount();
            case ColumnsetPackage.ALL_MATCH_INDICATOR__NOT_MATCHING_VALUE_COUNT:
                return getNotMatchingValueCount();
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
            case ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT:
                setMatchingValueCount((Long)newValue);
                return;
            case ColumnsetPackage.ALL_MATCH_INDICATOR__NOT_MATCHING_VALUE_COUNT:
                setNotMatchingValueCount((Long)newValue);
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
            case ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT:
                setMatchingValueCount(MATCHING_VALUE_COUNT_EDEFAULT);
                return;
            case ColumnsetPackage.ALL_MATCH_INDICATOR__NOT_MATCHING_VALUE_COUNT:
                setNotMatchingValueCount(NOT_MATCHING_VALUE_COUNT_EDEFAULT);
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
            case ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT:
                return MATCHING_VALUE_COUNT_EDEFAULT == null ? matchingValueCount != null : !MATCHING_VALUE_COUNT_EDEFAULT.equals(matchingValueCount);
            case ColumnsetPackage.ALL_MATCH_INDICATOR__NOT_MATCHING_VALUE_COUNT:
                return NOT_MATCHING_VALUE_COUNT_EDEFAULT == null ? notMatchingValueCount != null : !NOT_MATCHING_VALUE_COUNT_EDEFAULT.equals(notMatchingValueCount);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
        if (baseClass == MatchingIndicator.class) {
            switch (derivedFeatureID) {
                case ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT: return IndicatorsPackage.MATCHING_INDICATOR__MATCHING_VALUE_COUNT;
                case ColumnsetPackage.ALL_MATCH_INDICATOR__NOT_MATCHING_VALUE_COUNT: return IndicatorsPackage.MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT;
                default: return -1;
            }
        }
        if (baseClass == PatternMatchingIndicator.class) {
            switch (derivedFeatureID) {
                default: return -1;
            }
        }
        if (baseClass == RegexpMatchingIndicator.class) {
            switch (derivedFeatureID) {
                default: return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
        if (baseClass == MatchingIndicator.class) {
            switch (baseFeatureID) {
                case IndicatorsPackage.MATCHING_INDICATOR__MATCHING_VALUE_COUNT: return ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT;
                case IndicatorsPackage.MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT: return ColumnsetPackage.ALL_MATCH_INDICATOR__NOT_MATCHING_VALUE_COUNT;
                default: return -1;
            }
        }
        if (baseClass == PatternMatchingIndicator.class) {
            switch (baseFeatureID) {
                default: return -1;
            }
        }
        if (baseClass == RegexpMatchingIndicator.class) {
            switch (baseFeatureID) {
                default: return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
        result.append(" (matchingValueCount: ");
        result.append(matchingValueCount);
        result.append(", notMatchingValueCount: ");
        result.append(notMatchingValueCount);
        result.append(')');
        return result.toString();
    }

} //AllMatchIndicatorImpl
