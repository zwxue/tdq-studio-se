/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MinLengthIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Min Length Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class MinLengthIndicatorImpl extends LengthIndicatorImpl implements MinLengthIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected MinLengthIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.MIN_LENGTH_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public boolean handle(Object data) {
        mustStoreRow = true;
        boolean ok = super.handle(data);
        if (data != null) {
            String str = (String) data;
            if (str.length() > 0 && (length == LENGTH_EDEFAULT || length.intValue() > str.length())) {
                length = Long.valueOf(str.length());
            }
        }
        // TODO scorreia handle null data
        return ok;
    }

} // MinLengthIndicatorImpl
