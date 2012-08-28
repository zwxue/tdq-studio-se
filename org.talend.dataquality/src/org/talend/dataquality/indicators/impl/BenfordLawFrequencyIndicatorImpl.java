/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.HashMap;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.BenfordLawFrequencyIndicator;
import org.talend.dataquality.indicators.IndicatorsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Benford Law Frequency Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * * This Class provide the function to compute a group of data by Benford's Law and output the leading digits with its
 * distribution in this dataset. Then the user can use it to compare with the standard, to detect possible cases of
 * Fraud.
 * 
 * </p>
 * 
 * @generated
 */
public class BenfordLawFrequencyIndicatorImpl extends FrequencyIndicatorImpl implements BenfordLawFrequencyIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected BenfordLawFrequencyIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.BENFORD_LAW_FREQUENCY_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> count the occur times for 0~9 at the beginning of every numbers. <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public boolean handle(Object data) {
        if (this.valueToFreq == null) {
            this.valueToFreq = new HashMap<Object, Long>();
        }
        this.count++;

        if (data == null) {
            return true;
        }

        Integer leadDigit = Integer.valueOf(String.valueOf(data).substring(0, 1));

        // increment frequency of leading digit in data
        Long c = this.valueToFreq.get(leadDigit);
        if (c == null) {
            // add value to map
            this.valueToFreq.put(leadDigit, 1L);
            c = 1L;
        } else {
            // already exists: increment number of occurences
            c++;
            this.valueToFreq.put(leadDigit, c);
        }

        return true;
    }

} // BenfordLawFrequencyIndicatorImpl
