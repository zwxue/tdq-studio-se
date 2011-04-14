/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Distinct Count Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.DistinctCountIndicatorImpl#getDistinctValueCount <em>Distinct Value Count</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DistinctCountIndicatorImpl extends IndicatorImpl implements DistinctCountIndicator {

    /**
     * The default value of the '{@link #getDistinctValueCount() <em>Distinct Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDistinctValueCount()
     * @generated
     * @ordered
     */
    protected static final Long DISTINCT_VALUE_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDistinctValueCount() <em>Distinct Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDistinctValueCount()
     * @generated
     * @ordered
     */
    protected Long distinctValueCount = DISTINCT_VALUE_COUNT_EDEFAULT;

    private Set<Object> distinctObjects = new HashSet<Object>();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected DistinctCountIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.DISTINCT_COUNT_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Set<Object> getDistinctValues() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case IndicatorsPackage.DISTINCT_COUNT_INDICATOR__DISTINCT_VALUE_COUNT:
                return getDistinctValueCount();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case IndicatorsPackage.DISTINCT_COUNT_INDICATOR__DISTINCT_VALUE_COUNT:
                setDistinctValueCount((Long)newValue);
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
            case IndicatorsPackage.DISTINCT_COUNT_INDICATOR__DISTINCT_VALUE_COUNT:
                setDistinctValueCount(DISTINCT_VALUE_COUNT_EDEFAULT);
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
            case IndicatorsPackage.DISTINCT_COUNT_INDICATOR__DISTINCT_VALUE_COUNT:
                return DISTINCT_VALUE_COUNT_EDEFAULT == null ? distinctValueCount != null : !DISTINCT_VALUE_COUNT_EDEFAULT.equals(distinctValueCount);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(this.getName());
        result.append("= ");
        result.append(distinctValueCount);
        return result.toString();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Long getDistinctValueCount() {
        return distinctValueCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setDistinctValueCount(Long newDistinctValueCount) {
        Long oldDistinctValueCount = distinctValueCount;
        distinctValueCount = newDistinctValueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.DISTINCT_COUNT_INDICATOR__DISTINCT_VALUE_COUNT, oldDistinctValueCount, distinctValueCount));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#storeSqlResults(java.lang.Object[])
     * 
     * ADDED scorreia 2008-04-30 storeSqlResults(List<Object[]> objects)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        if (!super.checkResults(objects, 1)) {
            return false;
        }
        // MOD gdbu 2011-4-14 bug : 18975
        this.setDistinctValueCount(getLongFromObject(objects.get(0)[0]));
        // ~18975
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#getIntegerValue()
     * 
     * ADDED scorreia 2008-05-12 getIntegerValue()
     */
    @Override
    public Long getIntegerValue() {
        return this.getDistinctValueCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#handle(java.lang.Object)
     * 
     * ADDED scorreia 2009-04-17 handle(Object data)
     */
    @Override
    public boolean handle(Object data) {
        this.mustStoreRow = false;
        super.handle(data);
        if (data != null) {
            if (this.distinctObjects.add(data)) {
                this.mustStoreRow = true;
            }
        }
        return true;
    }

    @Override
    public boolean finalizeComputation() {
        this.setDistinctValueCount(Long.valueOf(distinctObjects.size()));
        return super.finalizeComputation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#reset()
     */
    @Override
    public boolean reset() {
        this.distinctObjects.clear();
        this.distinctValueCount = DISTINCT_VALUE_COUNT_EDEFAULT;
        return super.reset();
    }

    @Override
    public Expression getInstantiatedExpressions(String language) {
        return super.getInstantiatedExpressions(language);
    }

} // DistinctCountIndicatorImpl
