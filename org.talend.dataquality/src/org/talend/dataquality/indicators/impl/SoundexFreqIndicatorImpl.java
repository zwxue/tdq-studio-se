/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.SoundexFreqIndicator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Soundex Freq Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class SoundexFreqIndicatorImpl extends FrequencyIndicatorImpl implements SoundexFreqIndicator {
    private static Logger log = Logger.getLogger(SoundexFreqIndicatorImpl.class);
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SoundexFreqIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.SOUNDEX_FREQ_INDICATOR;
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
                log.info("Query for frequency table did not return any result. "
                        + "Check the options of this indicator. Bins must contains some data.");
            }
            this.setValueToFreq(new HashMap<Object, Long>());
            return true;
        } // else we got some values

        final int nbColumns = 3;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#getValueFields(java.lang.Object[])
     */
    @Override
    protected Object getValueFields(Object[] value2freq) {
        int nbFields = value2freq.length;
        assert (nbFields == 3);
        return value2freq[0];
    }
    
    

} //SoundexFreqIndicatorImpl
