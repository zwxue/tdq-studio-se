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

    protected String range;

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
        if (value != null) {
            return StringFormatUtil.format(value, StringFormatUtil.NUMBER).toString();
        } else {
            return null;
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPersent() {
        if (percent != null) {
            return StringFormatUtil.format(percent, StringFormatUtil.PERCENT).toString();
        } else {
            return null;
        }
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

    /**
     * DOC Zqin Comment method "isOutOfRange".
     * 
     * @return
     */
    public boolean isOutOfRange(String inputValue) {
        outOfRange = false;

        if (inputValue == null || indicator == null) {
            return false;
        }

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

            outOfRange = checkRange(inputValue);
        }

        return outOfRange;
    }

    protected Double[] getDefinedRange(String inString) {
        boolean flag = inString.indexOf('%') > 0;
        String[] threshold = IndicatorHelper.getDataThreshold(indicator);

        if (threshold == null) {
            if (flag) {
                threshold = IndicatorHelper.getIndicatorThresholdInPercent(indicator);
            } else {
                threshold = IndicatorHelper.getIndicatorThreshold(indicator);
            }
        }

        if (threshold != null) {
            Double[] returnDB = new Double[threshold.length];

            for (int i = 0; i < threshold.length; i++) {
                returnDB[i] = StringFormatUtil.formatDouble(threshold[i]);
            }

            return returnDB;
        }

        return null;
    }

    private boolean checkRange(String inString) {

        Double[] definedRange = getDefinedRange(inString);
        if (definedRange != null) {
            Double min = definedRange[0];
            Double max = definedRange[1];

            // handle min and max
            Double dValue = inString != null ? StringFormatUtil.parseDouble(inString) : Double.NaN;
            if (min == null || Double.isNaN(min)) {
                min = Double.NEGATIVE_INFINITY;
            }

            if (max == null || Double.isNaN(max)) {
                max = Double.POSITIVE_INFINITY;
            }

            if (dValue < min || dValue > max) {
                if (inString.indexOf('%') > 0) {
                    range = "[" + StringFormatUtil.formatPersent(min / 100) + "," + StringFormatUtil.formatPersent(max / 100)
                            + "]";
                } else {
                    range = "[" + min + "," + max + "]";
                }

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

        if (indicator != null) {
            IndicatorEnum indicatorEnum = IndicatorEnum.findIndicatorEnum(indicator.eClass());

            if (indicatorEnum == IndicatorEnum.ModeIndicatorEnum) {
                msg.append("This value differs from the expected value: \"" + IndicatorHelper.getExpectedValue(indicator) + "\"");
            } else if (indicatorEnum == IndicatorEnum.BoxIIndicatorEnum) {
                if (isOutOfRange(getValue())) {
                    msg.append("This value is outside the expected data's thresholds: " + range);
                }
            } else {
                if (isOutOfRange(getValue())) {
                    msg.append("This value is outside the expected indicator's thresholds: " + range);
                    msg.append("\n");
                }
                if (isOutOfRange(getPersent())) {
                    msg.append("This value is outside the expected indicator's thresholds in percent: " + range);
                }
            }
        }

        return msg.length() == 0 ? null : msg.toString();
    }
}
