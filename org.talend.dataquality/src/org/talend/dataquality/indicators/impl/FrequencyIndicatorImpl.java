/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.IndicatorsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Frequency Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#getUniqueValues <em>Unique Values</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#getDistinctValueCount <em>Distinct Value Count</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#getUniqueValueCount <em>Unique Value Count</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#getDuplicateValueCount <em>Duplicate Value Count</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#getValueToFreq <em>Value To Freq</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FrequencyIndicatorImpl extends IndicatorImpl implements FrequencyIndicator {

    private static Logger log = Logger.getLogger(FrequencyIndicatorImpl.class);

    private static final DecimalFormat F4_DIGIT = new DecimalFormat("0000");

    private static final DecimalFormat F2_DIGIT = new DecimalFormat("00");

    /**
     * The cached value of the '{@link #getUniqueValues() <em>Unique Values</em>}' attribute list.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see #getUniqueValues()
     * @generated
     * @ordered
     */
    protected EList<Object> uniqueValues;

    /**
     * The default value of the '{@link #getDistinctValueCount() <em>Distinct Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDistinctValueCount()
     * @generated
     * @ordered
     */
    protected static final Long DISTINCT_VALUE_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDistinctValueCount() <em>Distinct Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDistinctValueCount()
     * @generated
     * @ordered
     */
    protected Long distinctValueCount = DISTINCT_VALUE_COUNT_EDEFAULT;

    /**
     * The default value of the '{@link #getUniqueValueCount() <em>Unique Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getUniqueValueCount()
     * @generated
     * @ordered
     */
    protected static final Long UNIQUE_VALUE_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUniqueValueCount() <em>Unique Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getUniqueValueCount()
     * @generated
     * @ordered
     */
    protected Long uniqueValueCount = UNIQUE_VALUE_COUNT_EDEFAULT;

    /**
     * The default value of the '{@link #getDuplicateValueCount() <em>Duplicate Value Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDuplicateValueCount()
     * @generated
     * @ordered
     */
    protected static final Long DUPLICATE_VALUE_COUNT_EDEFAULT = null;

    /**
     * The default value of the '{@link #getValueToFreq() <em>Value To Freq</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getValueToFreq()
     * @generated NOT
     * @ordered
     */
    protected static final HashMap<Object, Long> VALUE_TO_FREQ_EDEFAULT = new HashMap<Object, Long>();

    /**
     * The cached value of the '{@link #getValueToFreq() <em>Value To Freq</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValueToFreq()
     * @generated
     * @ordered
     */
    protected HashMap<Object, Long> valueToFreq = VALUE_TO_FREQ_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected FrequencyIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.FREQUENCY_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EList<Object> getUniqueValues() {
        if (uniqueValues == null) {
            uniqueValues = new EDataTypeUniqueEList<Object>(Object.class, this, IndicatorsPackage.FREQUENCY_INDICATOR__UNIQUE_VALUES);
        }
        return uniqueValues;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Long getCount(Object dataValue) {
        if (dataValue == OTHER) {
            long counted = 0L;
            for (Object val : valueToFreq.keySet()) {
                Long freq = this.valueToFreq.get(val);
                counted = (freq == null) ? counted : counted + freq;
            }
            return (count != null && count > 0) ? count - counted : 0L;
        }
        Long freq = this.valueToFreq.get(dataValue);
        return (freq == null) ? 0L : freq;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Double getFrequency(Object dataValue) {
        if (this.count.compareTo(0L) == 0) {
            return Double.NaN;
        }
        return ((double) getCount(dataValue)) / this.count.longValue();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Set<Object> getDistinctValues() {
        if (!distinctComputed) {
            computeDistinctValues();
        }
        return distinctValues;
    }

    /**
     * ADDED scorreia Method "computeDistinctValues" updates the distinctValues field and set distinctComputed field to
     * true.
     */
    private void computeDistinctValues() {
        this.distinctValues = this.valueToFreq.keySet();
        this.setDistinctValueCount(Long.valueOf(distinctValues.size()));
        distinctComputed = true;
    }

    private Set<Object> distinctValues = null;

    /**
     * @generated
     */
    protected Long getDistinctValueCountGen() {
        return distinctValueCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Long getDistinctValueCount() {
        if (!distinctComputed) {
            computeDistinctValues();
        }
        return getDistinctValueCountGen();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setDistinctValueCount(Long newDistinctValueCount) {
        Long oldDistinctValueCount = distinctValueCount;
        distinctValueCount = newDistinctValueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.FREQUENCY_INDICATOR__DISTINCT_VALUE_COUNT, oldDistinctValueCount, distinctValueCount));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Long getUniqueValueCount() {
        return uniqueValueCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setUniqueValueCount(Long newUniqueValueCount) {
        Long oldUniqueValueCount = uniqueValueCount;
        uniqueValueCount = newUniqueValueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.FREQUENCY_INDICATOR__UNIQUE_VALUE_COUNT, oldUniqueValueCount, uniqueValueCount));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Long getDuplicateValueCount() {
        return count - getUniqueValueCount();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public HashMap<Object, Long> getValueToFreqGen() {
        return valueToFreq;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.FrequencyIndicator#getValueToFreq() @generated NOT
     */
    public HashMap<Object, Long> getValueToFreq() {
        if (valueToFreq == VALUE_TO_FREQ_EDEFAULT) {
            valueToFreq = new HashMap<Object, Long>();
        }
        return getValueToFreqGen();
    }

    public void setValueToFreq(HashMap<Object, Long> newValueToFreq) {
        this.distinctComputed = false;
        this.setValueToFreqGen(newValueToFreq);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setValueToFreqGen(HashMap<Object, Long> newValueToFreq) {
        HashMap<Object, Long> oldValueToFreq = valueToFreq;
        valueToFreq = newValueToFreq;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.FREQUENCY_INDICATOR__VALUE_TO_FREQ, oldValueToFreq, valueToFreq));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case IndicatorsPackage.FREQUENCY_INDICATOR__UNIQUE_VALUES:
                return getUniqueValues();
            case IndicatorsPackage.FREQUENCY_INDICATOR__DISTINCT_VALUE_COUNT:
                return getDistinctValueCount();
            case IndicatorsPackage.FREQUENCY_INDICATOR__UNIQUE_VALUE_COUNT:
                return getUniqueValueCount();
            case IndicatorsPackage.FREQUENCY_INDICATOR__DUPLICATE_VALUE_COUNT:
                return getDuplicateValueCount();
            case IndicatorsPackage.FREQUENCY_INDICATOR__VALUE_TO_FREQ:
                return getValueToFreq();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case IndicatorsPackage.FREQUENCY_INDICATOR__UNIQUE_VALUES:
                getUniqueValues().clear();
                getUniqueValues().addAll((Collection<? extends Object>)newValue);
                return;
            case IndicatorsPackage.FREQUENCY_INDICATOR__DISTINCT_VALUE_COUNT:
                setDistinctValueCount((Long)newValue);
                return;
            case IndicatorsPackage.FREQUENCY_INDICATOR__UNIQUE_VALUE_COUNT:
                setUniqueValueCount((Long)newValue);
                return;
            case IndicatorsPackage.FREQUENCY_INDICATOR__VALUE_TO_FREQ:
                setValueToFreq((HashMap<Object, Long>)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case IndicatorsPackage.FREQUENCY_INDICATOR__UNIQUE_VALUES:
                getUniqueValues().clear();
                return;
            case IndicatorsPackage.FREQUENCY_INDICATOR__DISTINCT_VALUE_COUNT:
                setDistinctValueCount(DISTINCT_VALUE_COUNT_EDEFAULT);
                return;
            case IndicatorsPackage.FREQUENCY_INDICATOR__UNIQUE_VALUE_COUNT:
                setUniqueValueCount(UNIQUE_VALUE_COUNT_EDEFAULT);
                return;
            case IndicatorsPackage.FREQUENCY_INDICATOR__VALUE_TO_FREQ:
                setValueToFreq(VALUE_TO_FREQ_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case IndicatorsPackage.FREQUENCY_INDICATOR__UNIQUE_VALUES:
                return uniqueValues != null && !uniqueValues.isEmpty();
            case IndicatorsPackage.FREQUENCY_INDICATOR__DISTINCT_VALUE_COUNT:
                return DISTINCT_VALUE_COUNT_EDEFAULT == null ? distinctValueCount != null : !DISTINCT_VALUE_COUNT_EDEFAULT.equals(distinctValueCount);
            case IndicatorsPackage.FREQUENCY_INDICATOR__UNIQUE_VALUE_COUNT:
                return UNIQUE_VALUE_COUNT_EDEFAULT == null ? uniqueValueCount != null : !UNIQUE_VALUE_COUNT_EDEFAULT.equals(uniqueValueCount);
            case IndicatorsPackage.FREQUENCY_INDICATOR__DUPLICATE_VALUE_COUNT:
                return DUPLICATE_VALUE_COUNT_EDEFAULT == null ? getDuplicateValueCount() != null : !DUPLICATE_VALUE_COUNT_EDEFAULT.equals(getDuplicateValueCount());
            case IndicatorsPackage.FREQUENCY_INDICATOR__VALUE_TO_FREQ:
                return VALUE_TO_FREQ_EDEFAULT == null ? valueToFreq != null : !VALUE_TO_FREQ_EDEFAULT.equals(valueToFreq);
        }
        return super.eIsSet(featureID);
    }

    /**
     * true is distinct value count is computed
     */
    private boolean distinctComputed = false;

    @Override
    public boolean handle(Object data) {
        super.handle(data);
        Long freq = getValueToFreq().get(data);
        if (freq == null) { // new data
            freq = 0L;
            this.getUniqueValues().add(data);
            this.uniqueValueCount++;
        } else { // data not new
            this.getUniqueValues().remove(data);
            if (freq.compareTo(1L) == 0) { // decrement when data is seen twice
                this.uniqueValueCount--;
            }
        }
        freq++;
        // TODO scorreia compute distinct values ?
        valueToFreq.put(data, freq);
        return freq > 0;

    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(this.getName());
        Set<Object> keySet = this.valueToFreq.keySet();
        for (Object object : keySet) {
            buf.append(object);
            buf.append(": ");
            buf.append(this.valueToFreq.get(object));
            buf.append("\n");
        }
        // TODO scorreia could compute average frequency (sum values/# keys)
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#storeSqlResults(java.lang.Object[])
     * 
     * ADDED scorreia 2008-04-30 storeSqlResults(List<Object[]> objects)
     */
    @SuppressWarnings("fallthrough")
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        // handle case when frequencies are computed on dates.
        int nbColumns = 2;
        if (hasDateGrainParameter()) {
            DateGrain dategrain = getParameters().getDateParameters().getDateAggregationType();
            switch (dategrain) {
            case DAY:
                nbColumns++;
                // no break
            case WEEK:
                nbColumns++;
                // no break
            case MONTH:
                nbColumns++;
                // no break
            case QUARTER:
                nbColumns++;
                // no break
            case YEAR:
                break;
            default:
                break;
            }
        }
        // handle case when no row is returned because there is no value.
        if (objects.isEmpty()) {

            if (log.isInfoEnabled()) {
                log.info("Query for frequency table did not return any result. "
                        + "Check the options of this indicator. Bins must contains some data.");
            }
            this.setValueToFreq(new HashMap<Object, Long>());
            return true;
        } // else we got some values
        if (!checkResults(objects, nbColumns)) {
            return false;
        }
        HashMap<Object, Long> mapVal2Freq = new HashMap<Object, Long>();
        boolean debug = log.isDebugEnabled();
        StringBuffer matrix = debug ? new StringBuffer() : null;
        for (Object[] value2freq : objects) {
            if (value2freq.length != nbColumns) {
                log.error("Problem with result for Frequency indicator");
                return false;
            }
            Object value = getValueFields(value2freq);
            Long freq = Long.valueOf(String.valueOf(value2freq[nbColumns - 1]));
            mapVal2Freq.put(value, freq);
            if (debug) {
                matrix.append("\n").append("\"").append(value).append("\"").append(",").append(freq);
            }
        }
        if (debug) {
            log.debug(matrix);
        }

        this.setValueToFreq(mapVal2Freq);
        return true;
    }

    private boolean hasDateGrainParameter() {
        return getParameters() != null && getParameters().getDateParameters() != null
                && getParameters().getDateParameters().getDateAggregationType() != null;
    }

    /**
     * DOC scorreia Comment method "getValueFields".
     * 
     * @param value2freq
     * @return
     */
    protected Object getValueFields(Object[] value2freq) {
        int nbFields = value2freq.length;
        if (nbFields == 2) {
            return value2freq[0];
        }
        // else
        // several columns exists (for date fields)
        // y, q , m, w, d
        StringBuffer buf = new StringBuffer();

        if (nbFields == 3) { // year and quarter
            Object year = value2freq[0];
            buf.append(format4digit(year));
            Object quarter = value2freq[1];
            buf.append(String.valueOf(quarter));
            return buf.toString();
        }

        if (nbFields == 4) { // year, quarter and month
            buf.append(format4digit(value2freq[0]));
            buf.append(format2digit(value2freq[2]));
            return buf.toString();
        }

        if (nbFields == 5) { // year, quarter, month, week
            Object year = value2freq[0];
            buf.append(format4digit(year));
            Object month = String.valueOf(value2freq[2]);
            buf.append(format2digit(month));
            String week = String.valueOf(value2freq[3]);
            buf.append(format2digit(week));
            return buf.toString();
        }

        if (nbFields == 6) { // year, quarter, month, week and day
            Object year = value2freq[0];
            buf.append(format4digit(year));
            Object month = String.valueOf(value2freq[2]);
            buf.append(format2digit(month));
            String day = String.valueOf(value2freq[4]);
            buf.append(format2digit(day));
            return buf.toString();
        }

        return null;
    }

   
    /**
     * DOC scorreia Comment method "format2digit".
     * 
     * @param month
     * @return
     */
    private String format2digit(Object month) {
        if (month == null) {
            return "00";
        }
        // else
        String str = String.valueOf(month);
        return F2_DIGIT.format(Integer.valueOf(str));
    }

    /**
     * DOC scorreia Comment method "format4digit".
     * 
     * @param year
     * @return
     */
    private String format4digit(Object year) {
        if (year == null) {
            return "0000";
        }
        // else
        String str = String.valueOf(year);
        return F4_DIGIT.format(Integer.valueOf(str));
    }

} // FrequencyIndicatorImpl
