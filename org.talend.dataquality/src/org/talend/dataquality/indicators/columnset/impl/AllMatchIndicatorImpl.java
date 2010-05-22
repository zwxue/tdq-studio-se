/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.columnset.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
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
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.AllMatchIndicatorImpl#getCompositeRegexMatchingIndicators <em>Composite Regex Matching Indicators</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AllMatchIndicatorImpl extends ColumnSetMultiValueIndicatorImpl implements AllMatchIndicator {

    /**
     * The cached value of the '{@link #getCompositeRegexMatchingIndicators() <em>Composite Regex Matching Indicators</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCompositeRegexMatchingIndicators()
     * @generated
     * @ordered
     */
    protected EList<RegexpMatchingIndicator> compositeRegexMatchingIndicators;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    public EList<RegexpMatchingIndicator> getCompositeRegexMatchingIndicators() {
        if (compositeRegexMatchingIndicators == null) {
            compositeRegexMatchingIndicators = new EObjectContainmentEList<RegexpMatchingIndicator>(RegexpMatchingIndicator.class, this, ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS);
        }
        return compositeRegexMatchingIndicators;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS:
                return getCompositeRegexMatchingIndicators();
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
            case ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS:
                getCompositeRegexMatchingIndicators().clear();
                getCompositeRegexMatchingIndicators().addAll((Collection<? extends RegexpMatchingIndicator>)newValue);
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
            case ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS:
                getCompositeRegexMatchingIndicators().clear();
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
            case ColumnsetPackage.ALL_MATCH_INDICATOR__COMPOSITE_REGEX_MATCHING_INDICATORS:
                return compositeRegexMatchingIndicators != null && !compositeRegexMatchingIndicators.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //AllMatchIndicatorImpl
