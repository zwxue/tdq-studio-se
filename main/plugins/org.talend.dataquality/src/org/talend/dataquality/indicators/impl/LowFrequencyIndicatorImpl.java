/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.LowFrequencyIndicator;
import org.talend.utils.collections.MapValueSorter;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Low Frequency Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class LowFrequencyIndicatorImpl extends FrequencyIndicatorImpl implements LowFrequencyIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected LowFrequencyIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.LOW_FREQUENCY_INDICATOR;
    }

    @Override
    protected List<Object> getReducedValues(int n) {
        return new MapValueSorter().getLessFrequent(getMapForFreq(), n);
    }

    @Override
    public boolean handle(Object data) {
        // MOD qiongli 2012-1-4 TDQ-4320,make the result is same as sql engine.
        if (data != null && data.toString().trim().equals(PluginConstant.EMPTY_STRING)) {
            data = PluginConstant.SPACE_STRING;
        }
        return super.handle(data);
    }

} // LowFrequencyIndicatorImpl
