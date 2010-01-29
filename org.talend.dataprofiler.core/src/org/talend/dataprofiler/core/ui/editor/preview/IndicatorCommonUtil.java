// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.preview;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.talend.dataprofiler.core.ui.editor.preview.ext.FrequencyExt;
import org.talend.dataprofiler.core.ui.editor.preview.ext.PatternMatchingExt;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.DatePatternFreqIndicator;
import org.talend.dataquality.indicators.DefValueCountIndicator;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.LowerQuartileIndicator;
import org.talend.dataquality.indicators.MaxLengthIndicator;
import org.talend.dataquality.indicators.MaxValueIndicator;
import org.talend.dataquality.indicators.MeanIndicator;
import org.talend.dataquality.indicators.MedianIndicator;
import org.talend.dataquality.indicators.MinLengthIndicator;
import org.talend.dataquality.indicators.MinValueIndicator;
import org.talend.dataquality.indicators.ModeIndicator;
import org.talend.dataquality.indicators.NullCountIndicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.RangeIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.SoundexFreqIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.UpperQuartileIndicator;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dataquality.indicators.sql.WhereRuleIndicator;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public final class IndicatorCommonUtil {

    private static Logger log = Logger.getLogger(IndicatorCommonUtil.class);

    private IndicatorCommonUtil() {

    }

    public static void getIndicatorValue(IndicatorUnit indicatorUnit) {

        Object tempObject = null;

        IndicatorEnum type = indicatorUnit.getType();
        Indicator indicator = indicatorUnit.getIndicator();

        try {
            if (type == IndicatorEnum.RangeIndicatorEnum || type == IndicatorEnum.IQRIndicatorEnum) {
                indicatorUnit.setValue(((RangeIndicator) indicator).getRange());
                ((RangeIndicator) indicator).setComputed(true);
            } else if (indicatorUnit.isExcuted()) {

                // log.warn("now getting the value of indicator [" + indicator.getName() + "]");

                switch (type) {
                case RowCountIndicatorEnum:
                    tempObject = ((RowCountIndicator) indicator).getCount();
                    break;

                case NullCountIndicatorEnum:
                    tempObject = ((NullCountIndicator) indicator).getNullCount();
                    break;

                case DistinctCountIndicatorEnum:
                    tempObject = ((DistinctCountIndicator) indicator).getDistinctValueCount();
                    break;

                case UniqueIndicatorEnum:
                    tempObject = (((UniqueCountIndicator) indicator).getUniqueValueCount());
                    break;

                case DuplicateCountIndicatorEnum:
                    tempObject = ((DuplicateCountIndicator) indicator).getDuplicateValueCount();
                    break;

                case BlankCountIndicatorEnum:
                    tempObject = ((BlankCountIndicator) indicator).getBlankCount();
                    break;

                case DefValueCountIndicatorEnum:
                    tempObject = ((DefValueCountIndicator) indicator).getDefaultValCount();
                    break;

                case MinLengthIndicatorEnum:
                    tempObject = ((MinLengthIndicator) indicator).getLength();
                    break;

                case MaxLengthIndicatorEnum:
                    tempObject = ((MaxLengthIndicator) indicator).getLength();
                    break;

                case AverageLengthIndicatorEnum:
                    tempObject = ((AverageLengthIndicator) indicator).getAverageLength();
                    break;

                case FrequencyIndicatorEnum:
                case LowFrequencyIndicatorEnum:
                case PatternFreqIndicatorEnum:
                case PatternLowFreqIndicatorEnum:
                case DatePatternFreqIndicatorEnum:
                case SoundexIndicatorEnum:
                case SoundexLowIndicatorEnum:
                    tempObject = handleFrequency(indicator);
                    break;

                case MeanIndicatorEnum:
                    tempObject = ((MeanIndicator) indicator).getMean();
                    break;

                case MedianIndicatorEnum:
                    tempObject = ((MedianIndicator) indicator).getMedian();
                    break;

                case MinValueIndicatorEnum:
                    tempObject = ((MinValueIndicator) indicator).getValue();
                    break;

                case MaxValueIndicatorEnum:
                    tempObject = ((MaxValueIndicator) indicator).getValue();
                    break;

                case LowerQuartileIndicatorEnum:
                    tempObject = ((LowerQuartileIndicator) indicator).getValue();
                    break;

                case UpperQuartileIndicatorEnum:
                    tempObject = ((UpperQuartileIndicator) indicator).getValue();
                    break;

                case RegexpMatchingIndicatorEnum:
                case SqlPatternMatchingIndicatorEnum:
                    tempObject = handleMatchingValue(indicator);
                    break;

                case ModeIndicatorEnum:
                    tempObject = ((ModeIndicator) indicator).getMode();
                    break;

                case UserDefinedIndicatorEnum:
                    tempObject = handleUDIValue(indicator);
                    break;

                default:

                }

                if (tempObject == null || "null".equalsIgnoreCase(tempObject.toString())) { //$NON-NLS-1$
                    indicator.setComputed(false);
                }

                indicatorUnit.setValue(tempObject);
            }
        } catch (Exception e) {
            indicator.setComputed(false);
        }
    }

    /**
     * DOC xqliu Comment method "handleFrequency".
     * 
     * @param indicator
     * @return
     */
    private static Object handleFrequency(Indicator indicator) {
        FrequencyExt[] frequencyExt = null;
        if (UDIHelper.isUDI(indicator)) {
            UserDefIndicator udi = (UserDefIndicator) indicator;
            Set<Object> valueSet = udi.getDistinctValues();
            if (valueSet == null) {
                return null;
            }

            frequencyExt = new FrequencyExt[valueSet.size()];

            int i = 0;
            for (Object o : valueSet) {
                frequencyExt[i] = new FrequencyExt();
                frequencyExt[i].setKey(o);
                frequencyExt[i].setValue(udi.getCount(o));
                frequencyExt[i].setFrequency(udi.getFrequency(o));
                i++;
            }
        } else if (IndicatorEnum.DatePatternFreqIndicatorEnum.getIndicatorType().isInstance(indicator)) {
            DatePatternFreqIndicator datePatternFrequency = (DatePatternFreqIndicator) indicator;
            List<Object> modelMatchers = datePatternFrequency.getRealModelMatcherList();
            frequencyExt = new FrequencyExt[modelMatchers.size()];
            int i = 0;
            for (Object patternMatcher : modelMatchers) {

                frequencyExt[i] = new FrequencyExt();
                frequencyExt[i].setKey(datePatternFrequency.getModel(patternMatcher));
                frequencyExt[i].setValue(Long.valueOf(String.valueOf(datePatternFrequency.getScore(patternMatcher))));
                frequencyExt[i].setFrequency(datePatternFrequency.getFrequency(patternMatcher));
                i++;
            }

        } else {
            FrequencyIndicator frequency = (FrequencyIndicator) indicator;
            Set<Object> valueSet = frequency.getDistinctValues();
            if (valueSet == null) {
                return null;
            }

            frequencyExt = new FrequencyExt[valueSet.size()];

            int i = 0;
            for (Object o : valueSet) {
                frequencyExt[i] = new FrequencyExt();
                frequencyExt[i].setKey(o);
                if (IndicatorsPackage.eINSTANCE.getSoundexFreqIndicator().equals(frequency.eClass())
                        || IndicatorsPackage.eINSTANCE.getSoundexLowFreqIndicator().equals(frequency.eClass())) {
                    // MOD scorreia 2009-03-23 display distinct count when working with Soundex
                    frequencyExt[i].setValue(((SoundexFreqIndicator) frequency).getDistinctCount(o));
                } else {
                    frequencyExt[i].setValue(frequency.getCount(o));
                }
                frequencyExt[i].setFrequency(frequency.getFrequency(o));
                i++;
            }
        }

        return frequencyExt;
    }

    /**
     * DOC xqliu Comment method "handleMatchingValue".
     * 
     * @param indicator
     * @return
     */
    private static Object handleMatchingValue(Indicator indicator) {
        Object tempObject;
        PatternMatchingExt patternExt = new PatternMatchingExt();
        if (UDIHelper.isUDI(indicator)) {
            patternExt.setMatchingValueCount(((UserDefIndicator) indicator).getMatchingValueCount());
            patternExt.setNotMatchingValueCount(((UserDefIndicator) indicator).getNotMatchingValueCount());
        } else {
            patternExt.setMatchingValueCount(((PatternMatchingIndicator) indicator).getMatchingValueCount());
            patternExt.setNotMatchingValueCount(((PatternMatchingIndicator) indicator).getNotMatchingValueCount());
        }
        tempObject = patternExt;
        return tempObject;
    }

    /**
     * DOC xqliu Comment method "handleUDIValue".
     * 
     * @param indicator
     * @return
     */
    private static Object handleUDIValue(Indicator indicator) {
        Object object = null;
        if (UDIHelper.isCount(indicator)) {
            object = ((UserDefIndicator) indicator).getUserCount();
        } else if (UDIHelper.isFrequency(indicator)) {
            object = handleFrequency(indicator);
        } else if (UDIHelper.isMatching(indicator)) {
            object = handleMatchingValue(indicator);
        } else if (UDIHelper.isRealValue(indicator)) {
            object = ((UserDefIndicator) indicator).getRealValue();
        }
        return object;
    }

    public static void getIndicatorValue(TableIndicatorUnit indicatorUnit) {
        Object tempObject = null;
        IndicatorEnum type = indicatorUnit.getType();
        Indicator indicator = indicatorUnit.getIndicator();
        if (indicatorUnit.isExcuted()) {
            switch (type) {
            case RowCountIndicatorEnum:
                tempObject = ((RowCountIndicator) indicator).getCount();
                break;
            case WhereRuleIndicatorEnum:
                Long userCount = ((WhereRuleIndicator) indicator).getUserCount();
                tempObject = userCount == null ? 0 : userCount;
            default:
            }
            indicatorUnit.setValue(tempObject);
        }
    }
}
