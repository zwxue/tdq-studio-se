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
import org.talend.dataquality.indicators.MaxLengthWithBlankIndicator;
import org.talend.dataquality.indicators.TextParameters;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Max Length With Blank Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class MaxLengthWithBlankIndicatorImpl extends MaxLengthIndicatorImpl implements MaxLengthWithBlankIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected MaxLengthWithBlankIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.MAX_LENGTH_WITH_BLANK_INDICATOR;
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
        textParameters.setUseNulls(false);
        textParameters.setUseBlank(true);
        parameters.setTextParameter(textParameters);
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
        // handle case when length is not set although data is not null (this means that data is blank)
        if (length == LENGTH_EDEFAULT && data != null) {
            length = 0L;
        }
        return ok;
    }
} // MaxLengthWithBlankIndicatorImpl
