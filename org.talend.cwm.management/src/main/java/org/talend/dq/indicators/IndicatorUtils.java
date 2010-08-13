// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.indicators;

import org.apache.log4j.Logger;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.AvgLengthWithBlankIndicator;
import org.talend.dataquality.indicators.AvgLengthWithBlankNullIndicator;
import org.talend.dataquality.indicators.AvgLengthWithNullIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.DefValueCountIndicator;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.LowerQuartileIndicator;
import org.talend.dataquality.indicators.MaxLengthIndicator;
import org.talend.dataquality.indicators.MaxLengthWithBlankIndicator;
import org.talend.dataquality.indicators.MaxLengthWithBlankNullIndicator;
import org.talend.dataquality.indicators.MaxLengthWithNullIndicator;
import org.talend.dataquality.indicators.MaxValueIndicator;
import org.talend.dataquality.indicators.MeanIndicator;
import org.talend.dataquality.indicators.MedianIndicator;
import org.talend.dataquality.indicators.MinLengthIndicator;
import org.talend.dataquality.indicators.MinLengthWithBlankIndicator;
import org.talend.dataquality.indicators.MinLengthWithBlankNullIndicator;
import org.talend.dataquality.indicators.MinLengthWithNullIndicator;
import org.talend.dataquality.indicators.MinValueIndicator;
import org.talend.dataquality.indicators.ModeIndicator;
import org.talend.dataquality.indicators.NullCountIndicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.RangeIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.UpperQuartileIndicator;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.indicators.ext.PatternMatchingExt;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class IndicatorUtils {

    private static Logger log = Logger.getLogger(IndicatorUtils.class);

    /**
     * 
     * DOC qiongli: get indcator value for indicator which has threshold.
     * 
     * @param indicator
     * @return
     */
    public static Object getIndicatorValue(Indicator indicator) {

        Object object = null;

        IndicatorEnum type = IndicatorEnum.findIndicatorEnum(indicator.eClass());

        try {
            if (type == IndicatorEnum.RangeIndicatorEnum || type == IndicatorEnum.IQRIndicatorEnum) {
                object = ((RangeIndicator) indicator).getRange();

            } else if (indicator.isComputed()) {

                switch (type) {
                case RowCountIndicatorEnum:
                    object = ((RowCountIndicator) indicator).getCount();
                    break;

                case NullCountIndicatorEnum:
                    object = ((NullCountIndicator) indicator).getNullCount();
                    break;

                case DistinctCountIndicatorEnum:
                    object = ((DistinctCountIndicator) indicator).getDistinctValueCount();
                    break;

                case UniqueIndicatorEnum:
                    object = (((UniqueCountIndicator) indicator).getUniqueValueCount());
                    break;

                case DuplicateCountIndicatorEnum:
                    object = ((DuplicateCountIndicator) indicator).getDuplicateValueCount();
                    break;

                case BlankCountIndicatorEnum:
                    object = ((BlankCountIndicator) indicator).getBlankCount();
                    break;

                case DefValueCountIndicatorEnum:
                    object = ((DefValueCountIndicator) indicator).getDefaultValCount();
                    break;

                case MinLengthIndicatorEnum:
                    object = ((MinLengthIndicator) indicator).getLength();
                    break;

                case MinLengthWithNullIndicatorEnum:
                    object = ((MinLengthWithNullIndicator) indicator).getLength();
                    break;

                case MinLengthWithBlankIndicatorEnum:
                    object = ((MinLengthWithBlankIndicator) indicator).getLength();
                    break;

                case MinLengthWithBlankNullIndicatorEnum:
                    object = ((MinLengthWithBlankNullIndicator) indicator).getLength();
                    break;

                case MaxLengthIndicatorEnum:
                    object = ((MaxLengthIndicator) indicator).getLength();
                    break;

                case MaxLengthWithNullIndicatorEnum:
                    object = ((MaxLengthWithNullIndicator) indicator).getLength();
                    break;

                case MaxLengthWithBlankIndicatorEnum:
                    object = ((MaxLengthWithBlankIndicator) indicator).getLength();
                    break;

                case MaxLengthWithBlankNullIndicatorEnum:
                    object = ((MaxLengthWithBlankNullIndicator) indicator).getLength();
                    break;

                case AverageLengthIndicatorEnum:
                    object = ((AverageLengthIndicator) indicator).getAverageLength();
                    break;

                case AverageLengthWithNullIndicatorEnum:
                    object = ((AvgLengthWithNullIndicator) indicator).getAverageLength();
                    break;

                case AverageLengthWithBlankIndicatorEnum:
                    object = ((AvgLengthWithBlankIndicator) indicator).getAverageLength();
                    break;

                case AverageLengthWithNullBlankIndicatorEnum:
                    object = ((AvgLengthWithBlankNullIndicator) indicator).getAverageLength();
                    break;

                case MeanIndicatorEnum:
                    object = ((MeanIndicator) indicator).getMean();
                    break;

                case MedianIndicatorEnum:
                    object = ((MedianIndicator) indicator).getMedian();
                    break;

                case MinValueIndicatorEnum:
                    object = ((MinValueIndicator) indicator).getValue();
                    break;

                case MaxValueIndicatorEnum:
                    object = ((MaxValueIndicator) indicator).getValue();
                    break;

                case LowerQuartileIndicatorEnum:
                    object = ((LowerQuartileIndicator) indicator).getValue();
                    break;

                case UpperQuartileIndicatorEnum:
                    object = ((UpperQuartileIndicator) indicator).getValue();
                    break;

                case RegexpMatchingIndicatorEnum:
                case SqlPatternMatchingIndicatorEnum:
                case AllMatchIndicatorEnum:
                    object = handleMatchingValue(indicator);
                    break;

                case ModeIndicatorEnum:
                    object = ((ModeIndicator) indicator).getMode();
                    break;

                default:

                }

                if (object == null || "null".equalsIgnoreCase(object.toString())) { //$NON-NLS-1$
                    indicator.setComputed(false);
                }
            }
        } catch (Exception e) {
            log.error(e, e);
            if (indicator != null) {
                indicator.setComputed(false);
            }
        }
        return object;
    }

    /**
     * DOC qiongli Comment method "handleMatchingValue".
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
}
