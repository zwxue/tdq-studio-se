/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.osgi.framework.Bundle;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
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
public class DatePatternFreqIndicatorImpl extends FrequencyIndicatorImpl implements DatePatternFreqIndicator {

    private DatePatternRetriever dateRetriever;

    private boolean isDelimtedFile = false;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected DatePatternFreqIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
        Bundle bundle = Platform.getBundle("org.talend.dataquality.matching"); //$NON-NLS-1$
        URL url = bundle.getResource("PatternsNameAndRegularExpressions.txt"); //$NON-NLS-1$
        String filepath = null;
        try {
            filepath = FileLocator.toFileURL(url).getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(filepath);
        dateRetriever.initModel2Regex(file);

        // MOD qiongli 2011-11-15 TDQ-3864,judge if it is file connection.
        MetadataColumn mdColumn = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(this.getAnalyzedElement());
        if (mdColumn != null) {
            Connection Connection = ConnectionHelper.getTdDataProvider(mdColumn);
            if (Connection != null && SwitchHelpers.DELIMITEDFILECONNECTION_SWITCH.doSwitch(Connection) != null) {
                isDelimtedFile = true;
            }

        }

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
            // MOD qiongli 2011-11-11 TDQ-3864,format the date for file connection.
            if (data instanceof Date && isDelimtedFile) {
                MetadataColumn mdColumn = SwitchHelpers.METADATA_COLUMN_SWITCH.doSwitch(this.getAnalyzedElement());
                String pattern = mdColumn.getPattern();
                if (pattern != null) {
                    pattern = StringUtils.replace(pattern, "\"", StringUtils.EMPTY);
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    data = sdf.format((Date) data);
                }
            }
            dateRetriever.handle(String.valueOf(data));
        }
        boolean returnValue = super.handle(data);
        this.mustStoreRow = false;
        return returnValue;
    }

    @Override
    public Double getFrequency(Object dataValue) {
        if (this.count.compareTo(0L) == 0) {
            return Double.NaN;
        } else

        if (dataValue instanceof ModelMatcher) {
            ModelMatcher dataMatcher = (ModelMatcher) dataValue;
            return ((double) dataMatcher.getScore()) / this.getCount().longValue();
        }
        return super.getFrequency(dataValue);
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

    public List<ModelMatcher> getModelMatcherList() {
        return dateRetriever.getModelMatchers();
    }

    /**
     * return List for ModelMatcher which Score more than 1.
     */
    public List<Object> getRealModelMatcherList() {
        List<Object> realModelMatcherList = new ArrayList<Object>();
        for (ModelMatcher matcher : dateRetriever.getModelMatchers()) {
            if (matcher.getScore() > 0) {
                realModelMatcherList.add(matcher);
            }
        }
        return realModelMatcherList;
    }

    public String getModel(Object matcher) {
        if (matcher instanceof ModelMatcher) {
            return ((ModelMatcher) matcher).getModel();
        } else {
            return null;
        }
    }

    public int getScore(Object matcher) {
        if (matcher instanceof ModelMatcher) {
            return ((ModelMatcher) matcher).getScore();
        } else {
            return -1;
        }
    }

    /**
     * 
     * DOC zshen Comment method "getRegex".
     * 
     * @param model the model of matcher.
     * @return if can find corresponding to matcher return it's the Regex of matcher else return null;
     */
    public String getRegex(String model) {
        return this.dateRetriever.getRegex(model);
    }

} // DatePatternFreqIndicatorImpl
