/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.PatternFreqIndicator;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.utils.string.AsciiUtils;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Pattern Freq Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class PatternFreqIndicatorImpl extends FrequencyIndicatorImpl implements PatternFreqIndicator {

    private String charsToReplace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞß0123456789";

    private String replacementChars = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999";

    // Mark replace parameter whether has been setting by the user.
    private boolean hasBeanCustomized = false;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected PatternFreqIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.PATTERN_FREQ_INDICATOR;
    }

    /**
     * add by zshen for feature 12919 to convertCharacter when save data into analysisDataSet with java engin
     */
    @Override
    public String convertCharacters(String data) {
        return AsciiUtils.replaceCharacters(String.valueOf(data), this.charsToReplace, this.replacementChars);
    }

    @Override
    public boolean prepare() {
        // MOD xqliu 2009-06-29 bug 7068
        final TextParameters textParameter = this.getParameters() == null ? null : this.getParameters().getTextParameter();
        // ~
        if (textParameter != null) {
            hasBeanCustomized = true;
            // TDQ-10044: fix when the user didn't set the replace and charactersToReplace, use the default value(only
            // for jave engine)
            String replacementCharacters = textParameter.getReplacementCharacters();
            if (!StringUtils.isBlank(replacementCharacters)) {
                this.replacementChars = replacementCharacters;
            }

            String charactersToReplace = textParameter.getCharactersToReplace();
            if (!StringUtils.isBlank(charactersToReplace)) {
                this.charsToReplace = charactersToReplace;
            }
            // TDQ-10044~

        }
        return super.prepare();
    }

    @Override
    public boolean handle(Object data) {
        if (data == null) {
            return super.handle(data);
        } else {
            String parsedData = convertCharacters(String.valueOf(data));
            return super.handle(parsedData);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#specialName(java.lang.Object)
     */
    @Override
    protected String getFrequencyLabel(Object name) {
        return convertCharacters(name.toString());
    }

    /**
     * Getter for hasBeanCustomized.
     * 
     * @return the hasBeanCustomized
     */
    protected boolean isBeanCustomized() {
        return hasBeanCustomized;
    }

} // PatternFreqIndicatorImpl
