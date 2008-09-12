/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.indicators.IndicatorValueType;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.PatternMatchingIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Pattern Matching Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.talend.dataquality.indicators.impl.PatternMatchingIndicatorImpl#getMatchingValueCount <em>Matching
 * Value Count</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.PatternMatchingIndicatorImpl#getNotMatchingValueCount <em>Not
 * Matching Value Count</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class PatternMatchingIndicatorImpl extends IndicatorImpl implements PatternMatchingIndicator {

    /**
     * The default value of the '{@link #getMatchingValueCount() <em>Matching Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMatchingValueCount()
     * @generated
     * @ordered
     */
    protected static final Long MATCHING_VALUE_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMatchingValueCount() <em>Matching Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getMatchingValueCount()
     * @generated
     * @ordered
     */
    protected Long matchingValueCount = MATCHING_VALUE_COUNT_EDEFAULT;

    /**
     * The default value of the '{@link #getNotMatchingValueCount() <em>Not Matching Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getNotMatchingValueCount()
     * @generated
     * @ordered
     */
    protected static final Long NOT_MATCHING_VALUE_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNotMatchingValueCount() <em>Not Matching Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getNotMatchingValueCount()
     * @generated
     * @ordered
     */
    protected Long notMatchingValueCount = NOT_MATCHING_VALUE_COUNT_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected PatternMatchingIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.PATTERN_MATCHING_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Long getMatchingValueCount() {
        return matchingValueCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setMatchingValueCount(Long newMatchingValueCount) {
        Long oldMatchingValueCount = matchingValueCount;
        matchingValueCount = newMatchingValueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    IndicatorsPackage.PATTERN_MATCHING_INDICATOR__MATCHING_VALUE_COUNT, oldMatchingValueCount, matchingValueCount));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Long getNotMatchingValueCount() {
        return notMatchingValueCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setNotMatchingValueCount(Long newNotMatchingValueCount) {
        Long oldNotMatchingValueCount = notMatchingValueCount;
        notMatchingValueCount = newNotMatchingValueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    IndicatorsPackage.PATTERN_MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT, oldNotMatchingValueCount,
                    notMatchingValueCount));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case IndicatorsPackage.PATTERN_MATCHING_INDICATOR__MATCHING_VALUE_COUNT:
            return getMatchingValueCount();
        case IndicatorsPackage.PATTERN_MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT:
            return getNotMatchingValueCount();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case IndicatorsPackage.PATTERN_MATCHING_INDICATOR__MATCHING_VALUE_COUNT:
            setMatchingValueCount((Long) newValue);
            return;
        case IndicatorsPackage.PATTERN_MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT:
            setNotMatchingValueCount((Long) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case IndicatorsPackage.PATTERN_MATCHING_INDICATOR__MATCHING_VALUE_COUNT:
            setMatchingValueCount(MATCHING_VALUE_COUNT_EDEFAULT);
            return;
        case IndicatorsPackage.PATTERN_MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT:
            setNotMatchingValueCount(NOT_MATCHING_VALUE_COUNT_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case IndicatorsPackage.PATTERN_MATCHING_INDICATOR__MATCHING_VALUE_COUNT:
            return MATCHING_VALUE_COUNT_EDEFAULT == null ? matchingValueCount != null : !MATCHING_VALUE_COUNT_EDEFAULT
                    .equals(matchingValueCount);
        case IndicatorsPackage.PATTERN_MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT:
            return NOT_MATCHING_VALUE_COUNT_EDEFAULT == null ? notMatchingValueCount != null : !NOT_MATCHING_VALUE_COUNT_EDEFAULT
                    .equals(notMatchingValueCount);
        }
        return super.eIsSet(featureID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#storeSqlResults(java.util.List)
     * 
     * ADDED scorreia 2008-06-06 storeSqlResults(List<Object[]> objects)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        if (!checkResults(objects, 2)) {
            return false;
        }
        Long match = Long.valueOf(String.valueOf(objects.get(0)[0]));
        Long total = Long.valueOf(String.valueOf(objects.get(0)[1]));
        if (match != null) {
            this.setMatchingValueCount(match);
        }
        if (total != null) {
            this.setCount(total);
        }
        if (total != null && match != null) {
            this.setNotMatchingValueCount(total - match);
        }
        return true;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (matchingValueCount: ");
        result.append(matchingValueCount);
        result.append(", notMatchingValueCount: ");
        result.append(notMatchingValueCount);
        result.append(')');
        return result.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#getIntegerValue()
     */
    @Override
    public Long getIntegerValue() {
        return this.getMatchingValueCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#getValueType()
     */
    @Override
    public IndicatorValueType getValueType() {
        return IndicatorValueType.INTEGER_VALUE;
    }

} // PatternMatchingIndicatorImpl
