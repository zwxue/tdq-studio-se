/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.SoundexLowFreqIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Soundex Low Freq Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class SoundexLowFreqIndicatorImpl extends SoundexFreqIndicatorImpl implements SoundexLowFreqIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected SoundexLowFreqIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.SOUNDEX_LOW_FREQ_INDICATOR;
    }

    @Override
    public boolean handle(Object data) {
        this.mustStoreRow = true;
        return super.handle(data);
    }

} // SoundexLowFreqIndicatorImpl
