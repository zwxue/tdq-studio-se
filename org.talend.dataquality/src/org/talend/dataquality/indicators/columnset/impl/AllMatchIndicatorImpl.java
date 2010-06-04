/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.columnset.impl;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MatchingIndicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.columnset.AllMatchIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>All Match Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.AllMatchIndicatorImpl#getMatchingValueCount <em>Matching Value Count</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.AllMatchIndicatorImpl#getNotMatchingValueCount <em>Not Matching Value Count</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.AllMatchIndicatorImpl#getCompositeRegexMatchingIndicators <em>Composite Regex Matching Indicators</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AllMatchIndicatorImpl extends ColumnSetMultiValueIndicatorImpl implements AllMatchIndicator {

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
     * The cached value of the '{@link #getCompositeRegexMatchingIndicators() <em>Composite Regex Matching Indicators</em>}' containment reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getCompositeRegexMatchingIndicators()
     * @generated
     * @ordered
     */
    protected EList<RegexpMatchingIndicator> compositeRegexMatchingIndicators;

    private List<String>[] patterns;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected AllMatchIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ColumnsetPackage.Literals.ALL_MATCH_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Long getMatchingValueCount() {
        return matchingValueCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setMatchingValueCount(Long newMatchingValueCount) {
        Long oldMatchingValueCount = matchingValueCount;
        matchingValueCount = newMatchingValueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT, oldMatchingValueCount, matchingValueCount));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Long getNotMatchingValueCount() {
        return notMatchingValueCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
     * @generated
     */
    public EList<RegexpMatchingIndicator> getCompositeRegexMatchingIndicators() {
        if (compositeRegexMatchingIndicators == null) {
            compositeRegexMatchingIndicators = new EObjectContainmentEList<RegexpMatchingIndicator>(RegexpMatchingIndicator.class, this, ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS);
        }
        return compositeRegexMatchingIndicators;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS:
                return ((InternalEList<?>)getCompositeRegexMatchingIndicators()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT:
                return getMatchingValueCount();
            case ColumnsetPackage.ALL_MATCH_INDICATOR__NOT_MATCHING_VALUE_COUNT:
                return getNotMatchingValueCount();
            case ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS:
                return getCompositeRegexMatchingIndicators();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT:
                setMatchingValueCount((Long)newValue);
                return;
            case ColumnsetPackage.ALL_MATCH_INDICATOR__NOT_MATCHING_VALUE_COUNT:
                setNotMatchingValueCount((Long)newValue);
                return;
            case ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS:
                getCompositeRegexMatchingIndicators().clear();
                getCompositeRegexMatchingIndicators().addAll((Collection<? extends RegexpMatchingIndicator>)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
            case ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS:
                getCompositeRegexMatchingIndicators().clear();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ColumnsetPackage.ALL_MATCH_INDICATOR__MATCHING_VALUE_COUNT:
                return MATCHING_VALUE_COUNT_EDEFAULT == null ? matchingValueCount != null : !MATCHING_VALUE_COUNT_EDEFAULT.equals(matchingValueCount);
            case ColumnsetPackage.ALL_MATCH_INDICATOR__NOT_MATCHING_VALUE_COUNT:
                return NOT_MATCHING_VALUE_COUNT_EDEFAULT == null ? notMatchingValueCount != null : !NOT_MATCHING_VALUE_COUNT_EDEFAULT.equals(notMatchingValueCount);
            case ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS:
                return compositeRegexMatchingIndicators != null && !compositeRegexMatchingIndicators.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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

    private boolean computeCounts(List<Object[]> objects) {
        boolean ok = true;
        Long matchCount = 0L;
        for (Object[] row : objects) {
            boolean found = false;
            for (int i = 0; i < row.length - 1; i++) {
                for (String regex : patterns[i]) {
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(String.valueOf(row[i]));
                    if (!m.find()) {
                        found = false;
                        break;
                    }
                    found = true;
                }
                if (false == found)
                    break;
            }
            if (found) {
                Long val = Long.valueOf(String.valueOf(row[row.length - 1]));
                matchCount += val;
            }
        }
        setMatchingValueCount(matchCount);
        setNotMatchingValueCount(getCount() - matchCount);
        return super.handle(objects);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#storeSqlResults(java.util.List)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        boolean ok = super.storeSqlResults(objects);
        computeCounts(objects);
        return ok;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.columnset.AllMatchIndicator#setPatterns(java.util.List)
     */
    public void setPatterns(List<String>[] patterns) {
        // TODO Auto-generated method stub
        this.patterns = patterns;
    }
} // AllMatchIndicatorImpl
