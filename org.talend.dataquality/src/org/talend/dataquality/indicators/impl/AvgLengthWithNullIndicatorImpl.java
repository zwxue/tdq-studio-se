/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.AvgLengthWithNullIndicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Avg Length With Null Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class AvgLengthWithNullIndicatorImpl extends AverageLengthIndicatorImpl implements AvgLengthWithNullIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected AvgLengthWithNullIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.AVG_LENGTH_WITH_NULL_INDICATOR;
    }

    @Override
    public IndicatorParameters getParameters() {
        parameters = super.getParameters();
        parameters.getTextParameter().setUseNulls(true);
        parameters.getTextParameter().setUseBlank(false);
        return parameters;

    }

    @Override
    public boolean handle(Object data) {
        // override super.handle(data);

        if (data == null) {
            nullCount++;
            count++; // count all rows
        } else {
            String str = (String) data;
            sumLength += str.length();
            if (str.length() > 0) {
                count++; // count all rows
            }
        }
        return true;
    }

} // AvgLengthWithNullIndicatorImpl
