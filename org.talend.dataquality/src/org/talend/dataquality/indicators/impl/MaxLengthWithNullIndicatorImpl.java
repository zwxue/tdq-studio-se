/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MaxLengthWithNullIndicator;
import org.talend.dataquality.indicators.TextParameters;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Max Length With Null Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class MaxLengthWithNullIndicatorImpl extends MaxLengthIndicatorImpl implements MaxLengthWithNullIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected MaxLengthWithNullIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.MAX_LENGTH_WITH_NULL_INDICATOR;
    }

    @Override
    public IndicatorParameters getParameters() {
        // MOD yyi 2010-12-23 17740:enable thresholds
        if (parameters != null) {
            TextParameters textParameters = parameters.getTextParameter();
            if (textParameters == null) {
                textParameters = IndicatorsFactory.eINSTANCE.createTextParameters();
            }
            textParameters.setUseNulls(true);
            textParameters.setUseBlank(false);
            parameters.setTextParameter(textParameters);
        }
        return parameters;
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
        // set max length to 0 when it is not already set and data is null
        if (length == LENGTH_EDEFAULT && data == null) {
            length = new Long(0);
        }
        return ok;
    }
} // MaxLengthWithNullIndicatorImpl
