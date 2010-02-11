/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators;

import java.util.List;

import org.talend.dataquality.matching.date.pattern.ModelMatcher;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Date Pattern Freq Indicator</b></em>'. <!--
 * end-user-doc -->
 * 
 * 
 * @see org.talend.dataquality.indicators.IndicatorsPackage#getDatePatternFreqIndicator()
 * @model
 * @generated
 */
public interface DatePatternFreqIndicator extends FrequencyIndicator {

    public List<ModelMatcher> getModelMatcherList();

    /**
     * return List for ModelMatcher which Score more than 1.
     */
    public List<Object> getRealModelMatcherList();

    public String getModel(Object matcher);

    public int getScore(Object matcher);

    /**
     * 
     * DOC zshen Comment method "getRegex".
     * 
     * @param model the model of matcher.
     * @return if can find corresponding to matcher return it's the Regex of matcher else return null;
     */
    public String getRegex(String model);
} // DatePatternFreqIndicator
