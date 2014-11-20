/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateFormatUtils;
import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.WeekFrequencyIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Week Frequency Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class WeekFrequencyIndicatorImpl extends FrequencyIndicatorImpl implements WeekFrequencyIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected WeekFrequencyIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.WEEK_FREQUENCY_INDICATOR;
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
            int weekOfYear = getWeekOfYear(date);
            String format = null;
            if (weekOfYear < 10) {
                format = DateFormatUtils.format((Date) data, datePattern + "0" + getWeekOfYear(date));
            } else {
                format = DateFormatUtils.format((Date) data, datePattern + getWeekOfYear(date));
            }
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
        // 'setFirstDayOfWeek(int)' and 'setMinimalDaysInFirstWeek(Locale)' will set by these 2 default parametes.
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        cal.setTime(date);
        int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
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

} // WeekFrequencyIndicatorImpl
