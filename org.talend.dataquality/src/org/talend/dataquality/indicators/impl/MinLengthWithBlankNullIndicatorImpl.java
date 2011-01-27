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
import org.talend.dataquality.indicators.MinLengthWithBlankNullIndicator;
import org.talend.dataquality.indicators.TextParameters;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Min Length With Blank Null Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class MinLengthWithBlankNullIndicatorImpl extends MinLengthIndicatorImpl implements MinLengthWithBlankNullIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected MinLengthWithBlankNullIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.MIN_LENGTH_WITH_BLANK_NULL_INDICATOR;
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
            textParameters.setUseBlank(true);
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
        if (data != null) {
            String str = (String) data;
            if (0 == str.trim().length()) {
                length = new Long(0);
            }
        } else {
            length = new Long(0);
        }
        return ok;
    }
} // MinLengthWithBlankNullIndicatorImpl
