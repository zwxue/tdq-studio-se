/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.WeekLowFrequencyIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Week Low Frequency Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class WeekLowFrequencyIndicatorImpl extends FrequencyIndicatorImpl implements WeekLowFrequencyIndicator {

    private final String weekSign = "w"; //$NON-NLS-1$ 

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected WeekLowFrequencyIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.WEEK_LOW_FREQUENCY_INDICATOR;
    }

    @Override
    public IndicatorParameters getParameters() {
        parameters = super.getParameters();
        if (parameters == null) {
            parameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        }
        DateParameters dateParameters = parameters.getDateParameters();
        if (dateParameters == null) {
            dateParameters = IndicatorsFactory.eINSTANCE.createDateParameters();
        }
        dateParameters.setDateAggregationType(DateGrain.WEEK);
        parameters.setDateParameters(dateParameters);
        return parameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#handle(java.lang.Object)
     */
    @Override
    public boolean handle(Object data) {
        if (data == null) {
            return super.handle(data);
        }

        if (data instanceof Date) {
            Date date = (Date) data;
            String format = DateFormatUtils.format((Date) data, datePattern + getWeekOfYear(date));
            return super.handle(format);
        }
        return super.handle(data);
    }

    /**
     * 
     * get week of year,make this indicator running result same as SQL engine, so minus 1.
     * 
     * @param date
     * @return
     */
    private int getWeekOfYear(Date date) {
        String weekStr = DateFormatUtils.format(date, weekSign);
        int weekOfYear = Integer.parseInt(weekStr);
        if (weekOfYear > 0) {
            weekOfYear--;
        }
        return weekOfYear;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#reset()
     */
    @Override
    public boolean reset() {
        boolean flag = super.reset();
        datePattern = "yyyyMM"; //$NON-NLS-1$ 
        return flag;
    }

} // WeekLowFrequencyIndicatorImpl
