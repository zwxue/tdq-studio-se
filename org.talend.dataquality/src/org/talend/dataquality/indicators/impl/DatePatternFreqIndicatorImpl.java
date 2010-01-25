/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.DatePatternFreqIndicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.matching.date.pattern.DatePatternRetriever;
import org.talend.dataquality.matching.date.pattern.ModelMatcher;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Date Pattern Freq Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class DatePatternFreqIndicatorImpl extends PatternFreqIndicatorImpl implements DatePatternFreqIndicator {

    private DatePatternRetriever dateRetriever;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected DatePatternFreqIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.DATE_PATTERN_FREQ_INDICATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.PatternFreqIndicatorImpl#prepare()
     */
    @Override
    public boolean prepare() {
        dateRetriever = new DatePatternRetriever();
        return super.prepare();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.PatternFreqIndicatorImpl#handle(java.lang.Object)
     */
    @Override
    public boolean handle(Object data) {
        if (data != null) {
            dateRetriever.handle(data.toString());
        }

        return super.handle(data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.FrequencyIndicatorImpl#storeSqlResults(java.util.List)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        List<ModelMatcher> modelMatchers = dateRetriever.getModelMatchers();
        ModelMatcher[] matchersArray = modelMatchers.toArray(new ModelMatcher[modelMatchers.size()]);
        ArrayList<Object[]> objects2 = new ArrayList<Object[]>();
        objects2.add(matchersArray);
        return super.storeSqlResults(objects2);
    }
} // DatePatternFreqIndicatorImpl
