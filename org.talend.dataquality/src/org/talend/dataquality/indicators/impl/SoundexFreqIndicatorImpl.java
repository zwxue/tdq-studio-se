/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.language.Soundex;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.commons.utils.SpecialValueDisplay;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.SoundexFreqIndicator;
import org.talend.utils.collections.MapValueSorter;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Soundex Freq Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.SoundexFreqIndicatorImpl#getValueToDistinctFreq <em>Value To Distinct Freq</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SoundexFreqIndicatorImpl extends FrequencyIndicatorImpl implements SoundexFreqIndicator {

    /**
     * The default value of the '{@link #getValueToDistinctFreq() <em>Value To Distinct Freq</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getValueToDistinctFreq()
     * @generated
     * @ordered
     */
    protected static final HashMap<Object, Long> VALUE_TO_DISTINCT_FREQ_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getValueToDistinctFreq() <em>Value To Distinct Freq</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getValueToDistinctFreq()
     * @generated
     * @ordered
     */
    protected HashMap<Object, Long> valueToDistinctFreq = VALUE_TO_DISTINCT_FREQ_EDEFAULT;

    private static Logger log = Logger.getLogger(SoundexFreqIndicatorImpl.class);

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    protected SoundexFreqIndicatorImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return IndicatorsPackage.Literals.SOUNDEX_FREQ_INDICATOR;
	}

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    public HashMap<Object, Long> getValueToDistinctFreq() {
		return valueToDistinctFreq;
	}

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    public void setValueToDistinctFreq(HashMap<Object, Long> newValueToDistinctFreq) {
		HashMap<Object, Long> oldValueToDistinctFreq = valueToDistinctFreq;
		valueToDistinctFreq = newValueToDistinctFreq;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.SOUNDEX_FREQ_INDICATOR__VALUE_TO_DISTINCT_FREQ, oldValueToDistinctFreq, valueToDistinctFreq));
	}

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT getDistinctCount(Object dataValue)
     */
    public Long getDistinctCount(Object dataValue) {
        // MOD xqliu 2009-07-01 bug 7068
        Long freq = this.valueToDistinctFreq == null || this.valueToDistinctFreq.size() ==0 ? null : this.valueToDistinctFreq.get(dataValue);
        // ~
        return (freq == null) ? 0L : freq;

    }

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case IndicatorsPackage.SOUNDEX_FREQ_INDICATOR__VALUE_TO_DISTINCT_FREQ:
				return getValueToDistinctFreq();
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
			case IndicatorsPackage.SOUNDEX_FREQ_INDICATOR__VALUE_TO_DISTINCT_FREQ:
				setValueToDistinctFreq((HashMap<Object, Long>)newValue);
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
			case IndicatorsPackage.SOUNDEX_FREQ_INDICATOR__VALUE_TO_DISTINCT_FREQ:
				setValueToDistinctFreq(VALUE_TO_DISTINCT_FREQ_EDEFAULT);
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
			case IndicatorsPackage.SOUNDEX_FREQ_INDICATOR__VALUE_TO_DISTINCT_FREQ:
				return VALUE_TO_DISTINCT_FREQ_EDEFAULT == null ? valueToDistinctFreq != null : !VALUE_TO_DISTINCT_FREQ_EDEFAULT.equals(valueToDistinctFreq);
		}
		return super.eIsSet(featureID);
	}

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (valueToDistinctFreq: ");
		result.append(valueToDistinctFreq);
		result.append(')');
		return result.toString();
	}

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#storeSqlResults(java.util.List)
     * 
     * MOD scorreia 2009-02-09 storeSqlResults(List<Object[]> objects)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        // handle case when no row is returned because there is no value.
        if (objects.isEmpty()) {
            if (log.isInfoEnabled()) {
                log
                        .info("Query for soundex frequency table did not return any result. "
                                + "Check the options of this indicator.");
            }
            this.setValueToFreq(new HashMap<Object, Long>());
            return true;
        } // else we got some values

        final int nbColumns = 4;
        // columns of result set are: value, soundex, count(*) , count(distinct)
        if (!checkResults(objects, nbColumns)) {
            return false;
        }

        // --- count the distinct records
        HashMap<Object, Long> mapVal2DistinctFreq = new HashMap<Object, Long>();
        HashMap<Object, Long> mapVal2Freq = new HashMap<Object, Long>();
        boolean debug = log.isDebugEnabled();
        StringBuffer matrix = debug ? new StringBuffer() : null;
        for (Object[] value2freq : objects) {
            if (value2freq.length != nbColumns) {
                log.error("Problem with result for Frequency indicator");
                return false;
            }
            Object value = getValueFields(value2freq);
            Long freq = Long.valueOf(String.valueOf(value2freq[nbColumns - 2]));
            mapVal2Freq.put(value, freq);
            Long distinctFreq = Long.valueOf(String.valueOf(value2freq[nbColumns - 1]));
            mapVal2DistinctFreq.put(value, distinctFreq);
            if (debug) {
                matrix.append("\n").append("\"").append(value).append("\"").append(",").append(freq);
            }
        }
        if (debug) {
            log.debug(matrix);
        }

        this.setValueToFreq(mapVal2Freq);
        this.setValueToDistinctFreq(mapVal2DistinctFreq);
        
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#getValueFields(java.lang.Object[])
     */
    @Override
    protected Object getValueFields(Object[] value2freq) {
        assert (value2freq.length == 4);
        return value2freq[0];
    }

    @Override
    public boolean handle(Object data) {
        boolean returnValue = super.handle(data);
        this.mustStoreRow = false;
        return returnValue;
    }

    /*
     * Run as JavaEngine.Set ValueToFreq and ValueToDistinctFreq
     * Add by qiongli 2010-6-22,bug 13654
     */

    protected void soundexForJavaEngine(){
		Iterator<Object> iterator = this.getValueToFreq().keySet().iterator();
		Soundex sd = new Soundex();
		HashMap<Object, Long> disctinctVfMap = new HashMap<Object, Long>();
		List<String[]> valueToFreqLs = new ArrayList<String[]>();
		
		while (iterator.hasNext()) {
			String array[] = new String[3];
			Object obj = iterator.next();
			if (obj == null) {
				array[0] = SpecialValueDisplay.NULL_FIELD;// col value
				array[2] = String.valueOf(0);// distinct count
			} else {
				array[0] = obj.toString();
				array[2] = String.valueOf(1);
			}
			try {
				array[1] = sd.soundex(array[0]);// soundex value
			} catch (IllegalArgumentException ex) {
				log.warn("Soundex algorithm do not support the charactors: "
						+ array[0]);
				continue;
			}
			valueToFreqLs.add(array);
		}
		
		String foreArray[] = null;
		String afterArray[] = null;
		HashMap<Object, Long> vfMap = new HashMap<Object, Long>();
		// remove duplicate key of valueToFreqLs, and get total count every key for valueToFreq.
		for (int i = 0; i < valueToFreqLs.size(); i++) {
			foreArray = (String[]) valueToFreqLs.get(i);
			vfMap.put(foreArray[0], valueToFreq.get(foreArray[0]));
			for (int j = i + 1; j < valueToFreqLs.size(); j++) {
				afterArray = (String[]) valueToFreqLs.get(j);
				if (afterArray[1].equals(foreArray[1])) {
					foreArray[2] = Long.valueOf(foreArray[2]).intValue() + 1+ "";
					valueToFreqLs.remove(afterArray);
					j--;
					Long newLong = Long.valueOf(vfMap.get(foreArray[0])
							.longValue()+ valueToFreq.get(afterArray[0]).longValue());
					vfMap.remove(foreArray[0]);
					//same as function max in sql engine
					if(foreArray[0].compareTo(afterArray[0])<0)
						foreArray[0]=afterArray[0];
					vfMap.put(foreArray[0], newLong);
				}
			}
			disctinctVfMap.put(foreArray[0], Long.valueOf(foreArray[2]));
		}
		setValueToFreq(vfMap);
		setValueToDistinctFreq(disctinctVfMap);
    }
    /*
     * (non-Javadoc)
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#finalizeComputation()
     */
    @Override
    public boolean finalizeComputation() {
        final int topN = (parameters != null) ? parameters.getTopN() : PluginConstant.DEFAULT_TOP_N;
        soundexForJavaEngine();
		MapValueSorter mvs = new MapValueSorter();
		List<Object> ls = mvs.sortMap(this.valueToDistinctFreq, false);
		List<Object> mostDistinctFrequent = getOrderElements(ls, topN, false);
        HashMap<Object, Long> map = new HashMap<Object, Long>();
        for (Object object : mostDistinctFrequent) {
            map.put(object, valueToFreq.get(object));
        }
        this.setValueToFreq(map);
        return true;
    }
    
    /*
     * ADD qiongli 2010-6-22,bug 13654
     * For java engine:Order by valueToDistinctFreq ,then order by valueToFreq
     */
    
    protected List<Object> getOrderElements(final List<Object> sortedMap, int bottomN,boolean asc) {
		List<Object> frequentLs = new ArrayList<Object>();
		int i = 0;
		Object obj1 = null;
		Object obj2 = null;
		Object temp = null;
		for (int m = 0; m < sortedMap.size(); m++) {
			obj1 = sortedMap.get(m);
			for (int n = m + 1; n < sortedMap.size(); n++) {
				obj2 = sortedMap.get(n);
				if (this.valueToDistinctFreq.get(obj1).equals(
						this.valueToDistinctFreq.get(obj2))) {
					if (!asc && valueToFreq.get(obj1) < valueToFreq.get(obj2)
							|| asc
							&& valueToFreq.get(obj1) > valueToFreq.get(obj2)) {
						temp = obj1;
						obj1 = obj2;
						obj2 = temp;
						sortedMap.set(n, temp);
						sortedMap.set(m, obj1);
					}
				}
			}
			frequentLs.add(obj1);
			i++;
			if (i == bottomN) {
				break;
			}
		}
		return frequentLs;
    }



} // SoundexFreqIndicatorImpl
