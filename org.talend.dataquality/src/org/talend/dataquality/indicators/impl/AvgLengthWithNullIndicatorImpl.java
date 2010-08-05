/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.AvgLengthWithNullIndicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.TextParameters;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Avg Length With Null Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class AvgLengthWithNullIndicatorImpl extends AverageLengthIndicatorImpl implements AvgLengthWithNullIndicator {

    protected static final Long BLANK_COUNT_EDEFAULT = new Long(0L);

    protected Long blankCount = BLANK_COUNT_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected AvgLengthWithNullIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.AVG_LENGTH_WITH_NULL_INDICATOR;
    }

    @Override
    public boolean storeSqlResults(List<Object[]> objects) {

        if (!checkResults(objects, 2)) {
            return false;
        }

        // http://www.talendforge.org/bugs/view.php?id=4783
        // Oracle treats empty strings as null values
        Object lCount = objects.get(0)[1];
        if (lCount == null) {
            this.setCount(null);
        } else {
            String c = String.valueOf(lCount);
            this.setCount(Long.valueOf(c));
        }

        Object lSum = objects.get(0)[0];
        if (lSum == null) {
            this.setSumLength(null);
        } else {
            String s = String.valueOf(lSum);
            this.setSumLength(Double.valueOf(s));
        }

        return true;
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

    @Override
    public boolean handle(Object data) {
        boolean ok = super.handle(data);
        if (data != null) {
            String str = (String) data;
            if (0 == str.length()) {
                this.count--;
            }
        }
        return ok;
    }

    @Override
    public boolean reset() {
        this.sumLength = SUM_LENGTH_EDEFAULT;
        this.blankCount = BLANK_COUNT_EDEFAULT;
        return super.reset();
    }
} // AvgLengthWithNullIndicatorImpl
