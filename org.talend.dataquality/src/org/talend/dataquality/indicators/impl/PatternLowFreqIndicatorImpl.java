/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.utils.string.AsciiUtils;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Pattern Low Freq Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class PatternLowFreqIndicatorImpl extends LowFrequencyIndicatorImpl implements PatternLowFreqIndicator {
    
    private String charsToReplace = "abcdefghijklmnopqrstuvwxyzçâêîôûéèùïöüABCDEFGHIJKLMNOPQRSTUVWXYZÇÂÊÎÔÛÉÈÙÏÖÜ0123456789";

    private String replacementChars = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999";

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected PatternLowFreqIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.PATTERN_LOW_FREQ_INDICATOR;
    }

    @Override
    public boolean prepare() {
        final TextParameters textParameter = this.getParameters().getTextParameter();
        if (textParameter != null) {
            this.replacementChars = textParameter.getReplacementCharacters();
            this.charsToReplace = textParameter.getCharactersToReplace();
        }
        return super.prepare();
    }

    @Override
    public boolean handle(Object data) {
        if (data == null) {
            return super.handle(data);
        } else {
            String parsedData = AsciiUtils.replaceCharacters(String.valueOf(data), this.charsToReplace, this.replacementChars);
            return super.handle(parsedData);
        }
    }

} //PatternLowFreqIndicatorImpl
