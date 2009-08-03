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

import java.util.Set;

import org.talend.dataprofiler.core.ui.editor.preview.ext.FrequencyExt;
import org.talend.dataprofiler.core.ui.editor.preview.ext.PatternMatchingExt;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
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
import org.talend.dataquality.indicators.sql.WhereRuleIndicator;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public final class IndicatorCommonUtil {

    private IndicatorCommonUtil() {

    }

    public static void getIndicatorValue(IndicatorUnit indicatorUnit) {

        Object tempObject = null;

        IndicatorEnum type = indicatorUnit.getType();
        Indicator indicator = indicatorUnit.getIndicator();

        if (type == IndicatorEnum.RangeIndicatorEnum || type == IndicatorEnum.IQRIndicatorEnum) {
            indicatorUnit.setValue(((RangeIndicator) indicator).getRange());
        } else if (indicatorUnit.isExcuted()) {

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
            case SoundexIndicatorEnum:
            case SoundexLowIndicatorEnum:
                FrequencyIndicator frequency = (FrequencyIndicator) indicator;
                Set<Object> valueSet = frequency.getDistinctValues();
                if (valueSet == null) {
                    break;
                }

                FrequencyExt[] frequencyExt = new FrequencyExt[valueSet.size()];

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

                tempObject = frequencyExt;
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
                PatternMatchingExt patternExt = new PatternMatchingExt();
                patternExt.setMatchingValueCount(((PatternMatchingIndicator) indicator).getMatchingValueCount());
                patternExt.setNotMatchingValueCount(((PatternMatchingIndicator) indicator).getNotMatchingValueCount());
                tempObject = patternExt;
                break;

            case ModeIndicatorEnum:
                tempObject = ((ModeIndicator) indicator).getMode();
                break;

            case UserDefinedIndicatorEnum:
                tempObject = indicator.getCount();
                break;

            default:

            }

            indicatorUnit.setValue(tempObject);
        }
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
