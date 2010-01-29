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
public interface DatePatternFreqIndicator extends PatternFreqIndicator {

    public List<ModelMatcher> getModelMatcherList();

    public List<Object> getRealModelMatcherList();

    public String getModel(Object matcher);

    public int getScore(Object matcher);
} // DatePatternFreqIndicator
