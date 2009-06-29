/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorValueType;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MaxValueIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Max Value Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class MaxValueIndicatorImpl extends ValueIndicatorImpl implements MaxValueIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected MaxValueIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.MAX_VALUE_INDICATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.ValueIndicatorImpl#getValueType()
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
        if (isGreater(data)) {
            this.value = String.valueOf(data);
        }
        return ok;
    }

    /**
     * DOC scorreia Comment method "isGreater".
     * 
     * @param data
     * @return
     */
    private boolean isGreater(Object data) {
        // MOD xqliu 2009-06-29 bug 7068
        try {
            double thisValue = Double.valueOf(this.value);
            double dataValue = Double.valueOf(data.toString());
            return thisValue < dataValue;
        } catch (Exception e) {
            return false;
        }
    }

    
} // MaxValueIndicatorImpl
