/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MinLengthWithNullIndicator;
import org.talend.dataquality.indicators.TextParameters;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Min Length With Null Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class MinLengthWithNullIndicatorImpl extends MinLengthIndicatorImpl implements MinLengthWithNullIndicator {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MinLengthWithNullIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.MIN_LENGTH_WITH_NULL_INDICATOR;
    }

    @Override
    public IndicatorParameters getParameters() {
        parameters = super.getParameters();
        if (parameters == null) {
            parameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        }
        TextParameters textParameters = parameters.getTextParameter();
        if (textParameters == null) {
            textParameters = IndicatorsFactory.eINSTANCE.createTextParameters();
        }
        textParameters.setUseNulls(true);
        textParameters.setUseBlank(false);
        parameters.setTextParameter(textParameters);
        return parameters;
    }

} //MinLengthWithNullIndicatorImpl
