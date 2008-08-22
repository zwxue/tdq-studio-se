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
package org.talend.dataprofiler.core.ui.wizard.indicator;

import org.eclipse.emf.common.util.EList;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.BinsDesignerParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.DataThresholdsParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.ExpectedValueParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.NumbericNominalParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TextLengthParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TextParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TimeSlicesParameter;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.domain.RealNumberValue;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.TextParameters;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ParamCompareFactory {

    public static boolean compare(IndicatorParameters indicatorParam, AbstractIndicatorParameter formParam) {
        boolean same = true;
        TextParameters textParameters = indicatorParam.getTextParameter();
        DateParameters dateParameters = indicatorParam.getDateParameters();

        switch (formParam.getFormEnum()) {
        case BinsDesignerForm:
            BinsDesignerParameter binsParam = (BinsDesignerParameter) formParam;

            Domain newDomain = binsParam.getUserDomian();
            Domain oldDomain = indicatorParam.getBins();

            if (oldDomain != null) {
                EList<RangeRestriction> ranges = oldDomain.getRanges();
                EList<RangeRestriction> ranges2 = newDomain.getRanges();
                if (ranges.size() != ranges2.size()) {
                    same = false;
                }
                for (int i = 0; i < ranges2.size() && same; i++) {
                    RangeRestriction d2 = ranges2.get(i);
                    RangeRestriction d1 = ranges.get(i);
                    double v1 = ((RealNumberValue) d1.getLowerValue()).getValue();
                    double v2 = ((RealNumberValue) d2.getLowerValue()).getValue();
                    if (v1 != v2) {
                        same = false;
                        break;
                    }
                    v1 = ((RealNumberValue) d1.getUpperValue()).getValue();
                    v2 = ((RealNumberValue) d2.getUpperValue()).getValue();
                    if (v1 != v2) {
                        same = false;
                        break;
                    }
                }
            } else {
                same = false;
            }

            if (indicatorParam.getTopN() != binsParam.getNumOfShown()) {
                same = false;
            }

            break;
        case TextLengthForm:
            TextLengthParameter lengthParam = (TextLengthParameter) formParam;

            if (textParameters == null || textParameters.isUseBlank() != lengthParam.isUseBlank()
                    || textParameters.isUseNulls() != lengthParam.isUseNull()) {
                same = false;
            }
            break;
        case TextParametersForm:
            TextParameter textParam = (TextParameter) formParam;
            if (textParameters == null || textParameters.isIgnoreCase() != textParam.isIngoreCase()
                    || indicatorParam.getTopN() != textParam.getNumOfShown()) {
                same = false;
            }
            break;
        case DataThresholdsForm:
            same = checkDomain(formParam, indicatorParam.getDataValidDomain());
            break;
        case IndicatorThresholdsForm:
            same = checkDomain(formParam, indicatorParam.getIndicatorValidDomain());
            break;
        case TimeSlicesForm:
            TimeSlicesParameter timeParam = (TimeSlicesParameter) formParam;

            DateGrain dateGrain = DateGrain.get(timeParam.getDataUnit());

            if (dateParameters == null || dateGrain.compareTo(dateParameters.getDateAggregationType()) != 0
                    || timeParam.getNumOfShown() != indicatorParam.getTopN()) {

                same = false;
            }

            break;
        case NumbericNominalForm:
            NumbericNominalParameter numbParam = (NumbericNominalParameter) formParam;
            same = indicatorParam.getTopN() == numbParam.getNumberOfShown();

            break;

        case ExpectedValueForm:
            ExpectedValueParameter expParam = (ExpectedValueParameter) formParam;
            String oldValue = IndicatorHelper.getExpectedValue(indicatorParam);
            String newValue = expParam.getExpectedValue();
            if (oldValue != null && newValue != null) {
                same = newValue.equals(oldValue);
            } else {
                same = false;
            }

            break;
        default:
        }

        return same;
    }

    /**
     * DOC zqin Comment method "checkDomain".
     * 
     * @param formParam
     * @param same
     * @param validDomain
     * @return
     */
    private static boolean checkDomain(AbstractIndicatorParameter formParam, Domain validDomain) {
        boolean result = true;

        DataThresholdsParameter dataParam = (DataThresholdsParameter) formParam;
        String min = dataParam.getMinThreshold();
        String max = dataParam.getMaxThreshold();
        if (validDomain == null) {
            result = false;
        } else {
            RangeRestriction range = validDomain.getRanges().get(0);

            if (!min.equals(DomainHelper.getMinValue(range)) || !max.equals(DomainHelper.getMaxValue(range))) {
                result = false;
            }
        }
        return result;
    }
}
