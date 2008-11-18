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
package org.talend.dq.indicators.preview.table;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.format.StringFormatUtil;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartDataEntity {

    private String label;

    private String value;

    private Indicator indicator;

    private String percent;

    private Boolean outOfRange = null;

    private String range;

    private boolean labelNull = false;

    public ChartDataEntity() {

    }

    public ChartDataEntity(Indicator indicator, String label, String value) {
        this(indicator, label, value, null, false);
    }

    public ChartDataEntity(Indicator indicator, String label, String value, String percent, boolean labelNull) {
        this.label = label;
        this.value = value;
        this.percent = percent;
        this.labelNull = labelNull;
        this.indicator = indicator;
    }

    public boolean isOutOfRange() {
        return isOutOfRange(null);
    }

    /**
     * DOC Zqin Comment method "isOutOfRange".
     * 
     * @return
     */
    public boolean isOutOfRange(String inputValue) {
        outOfRange = false;

        if (inputValue == null) {
            inputValue = getValue();
        }

        boolean canContinue = value != null && percent != null && indicator != null;

        if (canContinue) {
            IndicatorEnum indicatorEnum = IndicatorEnum.findIndicatorEnum(indicator.eClass());

            switch (indicatorEnum) {
            case ModeIndicatorEnum:
                String expectedValue = IndicatorHelper.getExpectedValue(indicator);
                if (expectedValue != null) {

                    Boolean ignoreCaseOption = IndicatorHelper.ignoreCaseOption(indicator);

                    outOfRange = !StringUtils.equals(value, expectedValue);

                    if (ignoreCaseOption) {
                        outOfRange = !(ignoreCaseOption && StringUtils.equalsIgnoreCase(value, expectedValue));
                    }
                }
                break;
            default:

                if (inputValue.equals(getValue())) {
                    outOfRange = checkRange(value, getDefinedRange());
                } else if (inputValue.equals(getPersent())) {
                    outOfRange = checkRange(percent, getDefinedPercentRange());
                }

            }
        }

        return outOfRange;
    }

    public Double[] getDefinedRange() {
        String[] threshold = IndicatorHelper.getDataThreshold(indicator);

        if (threshold == null) {
            threshold = IndicatorHelper.getIndicatorThreshold(indicator);
        }

        if (threshold != null) {
            Double[] returnDB = new Double[threshold.length];

            for (int i = 0; i < threshold.length; i++) {
                returnDB[i] = Double.valueOf(StringFormatUtil.format(threshold[i], StringFormatUtil.NUMBER).toString());
            }

            return returnDB;
        }

        return null;
    }

    private Double[] getDefinedPercentRange() {
        String[] threshold = IndicatorHelper.getIndicatorThresholdInPercent(indicator);

        if (threshold != null) {
            Double[] returnDB = new Double[threshold.length];

            for (int i = 0; i < threshold.length; i++) {
                returnDB[i] = new Double(threshold[i]);
            }

            return returnDB;
        }

        return null;
    }

    /**
     * DOC Zqin Comment method "checkRange".
     * 
     * @param currentValue
     * @param threshold
     * @return
     */
    private boolean checkRange(String currentValue, Double[] threshold) {

        if (threshold != null) {
            Double min = threshold[0];
            Double max = threshold[1];

            // handle min and max
            Double dValue = currentValue != null ? Double.valueOf(currentValue) : Double.NaN;
            if (min == null || Double.isNaN(min)) {
                min = Double.NEGATIVE_INFINITY;
            }

            if (max == null || Double.isNaN(max)) {
                max = Double.POSITIVE_INFINITY;
            }

            if (dValue < min || dValue > max) {
                range = " [" + min + "," + max + "]";
                return true;
            }
        }

        return false;
    }

    /**
     * DOC Zqin Comment method "getRangeAsString".
     * 
     * @return retrun the message when indicator value out the defined range.
     */
    public String getRangeAsString() {

        StringBuilder msg = new StringBuilder();

        if (outOfRange != null && indicator != null && outOfRange) {
            IndicatorEnum indicatorEnum = IndicatorEnum.findIndicatorEnum(indicator.eClass());

            if (indicatorEnum == IndicatorEnum.ModeIndicatorEnum) {
                msg.append("This value differs from the expected value: \"" + IndicatorHelper.getExpectedValue(indicator) + "\"");
            } else if (indicatorEnum == IndicatorEnum.BoxIIndicatorEnum) {
                msg.append("This value is outside the expected data's thresholds: " + range);
            } else {
                msg.append("This value is outside the expected indicator's thresholds: " + range);
            }
        }

        return msg.length() == 0 ? null : msg.toString();
    }

    /**
     * Getter for labelNull.
     * 
     * @return true if the given label represents a null value
     */
    public boolean isLabelNull() {
        return this.labelNull;
    }

    /**
     * Sets the labelNull.
     * 
     * @param labelNull set to true if the label represents the null value
     */
    public void setLabelNull(boolean labelNull) {
        this.labelNull = labelNull;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return StringFormatUtil.format(value, StringFormatUtil.NUMBER).toString();
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPersent() {
        if (percent == null) {
            return String.valueOf(Double.NaN);
        }
        return StringFormatUtil.format(percent, StringFormatUtil.PERCENT).toString();
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }
}
