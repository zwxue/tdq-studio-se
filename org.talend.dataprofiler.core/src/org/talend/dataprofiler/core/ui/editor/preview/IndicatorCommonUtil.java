// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import java.awt.Color;
import java.util.Set;

import org.talend.dataprofiler.core.ui.editor.preview.ext.FrequencyExt;
import org.talend.dataprofiler.core.ui.editor.preview.ext.PatternMatchingExt;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
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
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.UpperQuartileIndicator;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class IndicatorCommonUtil {

    // Color Constants
    private static final Color COLOR_ROW_COUNT = Color.RED;

    private static final Color COLOR_NULL_COUNT = Color.GRAY;

    private static final Color COLOR_DISTIN_CTCOUNT = Color.YELLOW;

    private static final Color COLOR_UNIQUE_COUNT = Color.BLUE;

    private static final Color COLOR_DUPLICATE_COUNT = Color.CYAN;

    private static final Color COLOR_BLANKCOUNT = Color.GREEN;

    private static final Color COLOR_MIN_LENGTH = Color.MAGENTA;

    private static final Color COLOR_MAX_LENGTH = Color.ORANGE;

    private static final Color COLOR_AVERAGE_LENGTH = Color.PINK;

    public IndicatorCommonUtil() {

    }

    public static void compositeIndicatorMap(IndicatorUnit indicatorUnit) {

        Color tempColor = null;
        Object tempObject = null;

        IndicatorEnum type = indicatorUnit.getType();
        Indicator indicator = indicatorUnit.getIndicator();

        if (!indicator.getInstantiatedExpressions().isEmpty()) {

            switch (type) {
            case RowCountIndicatorEnum:
                tempColor = COLOR_ROW_COUNT;
                tempObject = ((RowCountIndicator) indicator).getCount();
                break;

            case NullCountIndicatorEnum:
                tempColor = COLOR_NULL_COUNT;
                tempObject = ((NullCountIndicator) indicator).getNullCount();
                break;

            case DistinctCountIndicatorEnum:
                tempColor = COLOR_DISTIN_CTCOUNT;
                tempObject = ((DistinctCountIndicator) indicator).getDistinctValueCount();
                break;

            case UniqueIndicatorEnum:
                tempColor = COLOR_UNIQUE_COUNT;
                tempObject = (((UniqueCountIndicator) indicator).getUniqueValueCount());
                break;

            case DuplicateCountIndicatorEnum:
                tempColor = COLOR_DUPLICATE_COUNT;
                tempObject = ((DuplicateCountIndicator) indicator).getDuplicateValueCount();
                break;

            case BlankCountIndicatorEnum:
                tempColor = COLOR_BLANKCOUNT;
                tempObject = ((BlankCountIndicator) indicator).getBlankCount();
                break;

            case MinLengthIndicatorEnum:
                tempColor = COLOR_MIN_LENGTH;
                tempObject = ((MinLengthIndicator) indicator).getLength();
                break;

            case MaxLengthIndicatorEnum:
                tempColor = COLOR_MAX_LENGTH;
                tempObject = ((MaxLengthIndicator) indicator).getLength();
                break;

            case AverageLengthIndicatorEnum:
                tempColor = COLOR_AVERAGE_LENGTH;
                tempObject = ((AverageLengthIndicator) indicator).getAverageLength();
                break;

            case FrequencyIndicatorEnum:
            case LowFrequencyIndicatorEnum:
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
                    frequencyExt[i].setValue(frequency.getCount(o));
                    frequencyExt[i].setFrequency(frequency.getFrequency(o));
                    i++;
                }

                tempColor = null;
                tempObject = frequencyExt;
                break;

            case MeanIndicatorEnum:
                tempColor = null;
                tempObject = ((MeanIndicator) indicator).getMean();
                break;

            case MedianIndicatorEnum:
                tempColor = null;
                tempObject = ((MedianIndicator) indicator).getMedian();
                break;

            case MinValueIndicatorEnum:
                tempColor = null;
                tempObject = ((MinValueIndicator) indicator).getValue();
                break;

            case MaxValueIndicatorEnum:
                tempColor = null;
                tempObject = ((MaxValueIndicator) indicator).getValue();
                break;

            case LowerQuartileIndicatorEnum:
                tempColor = null;
                tempObject = ((LowerQuartileIndicator) indicator).getValue();
                break;

            case UpperQuartileIndicatorEnum:
                tempColor = null;
                tempObject = ((UpperQuartileIndicator) indicator).getValue();
                break;

            case RegexpMatchingIndicatorEnum:
            case SqlPatternMatchingIndicatorEnum:
                tempColor = null;
                PatternMatchingExt patternExt = new PatternMatchingExt();
                patternExt.setMatchingValueCount(((PatternMatchingIndicator) indicator).getMatchingValueCount());
                patternExt.setNotMatchingValueCount(((PatternMatchingIndicator) indicator).getNotMatchingValueCount());
                tempObject = patternExt;
                break;
            case ModeIndicatorEnum:
                tempColor = null;
                tempObject = ((ModeIndicator) indicator).getMode();

            default:

            }

            indicatorUnit.setExcuted(true);
            indicatorUnit.setColor(tempColor);
            indicatorUnit.setValue(tempObject);
        }
    }

}
