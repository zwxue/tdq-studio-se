/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorValueType;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MinValueIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Min Value Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class MinValueIndicatorImpl extends ValueIndicatorImpl implements MinValueIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected MinValueIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.MIN_VALUE_INDICATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.ValueIndicatorImpl#getValueType()
     * 
     * @generated NOT
     */
    @Override
    public IndicatorValueType getValueType() {
        // MOD scorreia handle date: bug 5938
        if (isDateValue()) {
            return IndicatorValueType.DATE_VALUE;
        }

        return IndicatorValueType.REAL_VALUE;
    }

    @Override
    public boolean handle(Object data) {
        boolean ok = super.handle(data);
        if (isLess(data)) {
            this.value = String.valueOf(data);
        }
        return ok;
    }

    /**
     * DOC scorreia Comment method "isLess".
     * 
     * @param data
     * @return
     */
    private boolean isLess(Object data) {
        // FIXME scorreia implement me
        throw new UnsupportedOperationException("Don't know how to compare this data yet " + data);
    }

    @Override
    public boolean reset() {
        this.value = VALUE_EDEFAULT;
        return super.reset();
    }
    
    
} // MinValueIndicatorImpl
