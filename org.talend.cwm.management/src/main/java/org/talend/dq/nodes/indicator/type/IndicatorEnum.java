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
package org.talend.dq.nodes.indicator.type;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;

/**
 * @author rli
 * 
 */
public enum IndicatorEnum {
    SqlPatternMatchingIndicatorEnum(
                                    IndicatorsPackage.SQL_PATTERN_MATCHING_INDICATOR,
                                    IndicatorsPackage.Literals.SQL_PATTERN_MATCHING_INDICATOR,
                                    "sql like matching",
                                    null),
    RegexpMatchingIndicatorEnum(
                                IndicatorsPackage.REGEXP_MATCHING_INDICATOR,
                                IndicatorsPackage.Literals.REGEXP_MATCHING_INDICATOR,
                                "regexp matching",
                                null),
    RowCountIndicatorEnum(
                          IndicatorsPackage.ROW_COUNT_INDICATOR,
                          IndicatorsPackage.Literals.ROW_COUNT_INDICATOR,
                          "row count",
                          null),
    NullCountIndicatorEnum(
                           IndicatorsPackage.NULL_COUNT_INDICATOR,
                           IndicatorsPackage.Literals.NULL_COUNT_INDICATOR,
                           "null count",
                           null),
    DistinctCountIndicatorEnum(
                               IndicatorsPackage.DISTINCT_COUNT_INDICATOR,
                               IndicatorsPackage.Literals.DISTINCT_COUNT_INDICATOR,
                               "distinct count",
                               null),
    UniqueIndicatorEnum(
                        IndicatorsPackage.UNIQUE_COUNT_INDICATOR,
                        IndicatorsPackage.Literals.UNIQUE_COUNT_INDICATOR,
                        "unique count",
                        null),
    DuplicateCountIndicatorEnum(
                                IndicatorsPackage.DUPLICATE_COUNT_INDICATOR,
                                IndicatorsPackage.Literals.DUPLICATE_COUNT_INDICATOR,
                                "duplicate count",
                                null),
    BlankCountIndicatorEnum(
                            IndicatorsPackage.BLANK_COUNT_INDICATOR,
                            IndicatorsPackage.Literals.BLANK_COUNT_INDICATOR,
                            "blank count",
                            null),

    MinLengthIndicatorEnum(
                           IndicatorsPackage.MIN_LENGTH_INDICATOR,
                           IndicatorsPackage.Literals.MIN_LENGTH_INDICATOR,
                           "min length",
                           null),
    MaxLengthIndicatorEnum(
                           IndicatorsPackage.MAX_LENGTH_INDICATOR,
                           IndicatorsPackage.Literals.MAX_LENGTH_INDICATOR,
                           "max length",
                           null),

    AverageLengthIndicatorEnum(
                               IndicatorsPackage.AVERAGE_LENGTH_INDICATOR,
                               IndicatorsPackage.Literals.AVERAGE_LENGTH_INDICATOR,
                               "average length",
                               null),

    ModeIndicatorEnum(IndicatorsPackage.MODE_INDICATOR, IndicatorsPackage.Literals.MODE_INDICATOR, "mode", null),
    FrequencyIndicatorEnum(
                           IndicatorsPackage.FREQUENCY_INDICATOR,
                           IndicatorsPackage.Literals.FREQUENCY_INDICATOR,
                           "frequency table",
                           null),
    LowFrequencyIndicatorEnum(
                              IndicatorsPackage.LOW_FREQUENCY_INDICATOR,
                              IndicatorsPackage.Literals.LOW_FREQUENCY_INDICATOR,
                              "low frequency table",
                              null),
    PatternFreqIndicatorEnum(
                             IndicatorsPackage.PATTERN_FREQ_INDICATOR,
                             IndicatorsPackage.Literals.PATTERN_FREQ_INDICATOR,
                             "pattern frequency table",
                             null),
    PatternLowFreqIndicatorEnum(
                                IndicatorsPackage.PATTERN_LOW_FREQ_INDICATOR,
                                IndicatorsPackage.Literals.PATTERN_LOW_FREQ_INDICATOR,
                                "pattern low frequency table",
                                null),

    MeanIndicatorEnum(IndicatorsPackage.MEAN_INDICATOR, IndicatorsPackage.Literals.MEAN_INDICATOR, "mean", null),
    MedianIndicatorEnum(IndicatorsPackage.MEDIAN_INDICATOR, IndicatorsPackage.Literals.MEDIAN_INDICATOR, "median", null),
    MinValueIndicatorEnum(
                          IndicatorsPackage.MIN_VALUE_INDICATOR,
                          IndicatorsPackage.Literals.MIN_VALUE_INDICATOR,
                          "min.value",
                          null),
    MaxValueIndicatorEnum(
                          IndicatorsPackage.MAX_VALUE_INDICATOR,
                          IndicatorsPackage.Literals.MAX_VALUE_INDICATOR,
                          "max.value",
                          null),
    LowerQuartileIndicatorEnum(
                               IndicatorsPackage.LOWER_QUARTILE_INDICATOR,
                               IndicatorsPackage.Literals.LOWER_QUARTILE_INDICATOR,
                               "lower quartile",
                               null),
    UpperQuartileIndicatorEnum(
                               IndicatorsPackage.UPPER_QUARTILE_INDICATOR,
                               IndicatorsPackage.Literals.UPPER_QUARTILE_INDICATOR,
                               "upper quartile",
                               null),
    CountsIndicatorEnum(
                        IndicatorsPackage.COUNTS_INDICATOR,
                        IndicatorsPackage.Literals.COUNTS_INDICATOR,
                        "Simple Statistics",
                        new IndicatorEnum[] { RowCountIndicatorEnum, NullCountIndicatorEnum, DistinctCountIndicatorEnum,
                                UniqueIndicatorEnum, DuplicateCountIndicatorEnum, BlankCountIndicatorEnum }),
    TextIndicatorEnum(
                      IndicatorsPackage.TEXT_INDICATOR,
                      IndicatorsPackage.Literals.TEXT_INDICATOR,
                      "Text Statistics",
                      new IndicatorEnum[] { MinLengthIndicatorEnum, MaxLengthIndicatorEnum, AverageLengthIndicatorEnum }),
    IQRIndicatorEnum(
                     IndicatorsPackage.IQR_INDICATOR,
                     IndicatorsPackage.Literals.IQR_INDICATOR,
                     "inter quartile range",
                     new IndicatorEnum[] { LowerQuartileIndicatorEnum, UpperQuartileIndicatorEnum }),
    RangeIndicatorEnum(
                       IndicatorsPackage.RANGE_INDICATOR,
                       IndicatorsPackage.Literals.RANGE_INDICATOR,
                       "range",
                       new IndicatorEnum[] { MinValueIndicatorEnum, MaxValueIndicatorEnum }),
    BoxIIndicatorEnum(
                      IndicatorsPackage.BOX_INDICATOR,
                      IndicatorsPackage.Literals.BOX_INDICATOR,
                      "Summary Statistics",
                      new IndicatorEnum[] { MeanIndicatorEnum, MedianIndicatorEnum, IQRIndicatorEnum, RangeIndicatorEnum });

    private EClass indicatorType;

    private String label;

    private IndicatorEnum[] children;

    private IndicatorEnum parent;

    private final int indicatorClassifierId;

    IndicatorEnum(int indicatorClassifierId, EClass indicatorType, String label, IndicatorEnum[] children) {
        this.indicatorClassifierId = indicatorClassifierId;
        this.indicatorType = indicatorType;
        this.label = label;
        setChildren(children);
    }

    /**
     * DOC rli Comment method "setChildren".
     * 
     * @param children
     */
    private void setChildren(IndicatorEnum[] children) {
        this.children = children;
        if (this.children == null) {
            return;
        }
        for (IndicatorEnum indicatorEnum : children) {
            indicatorEnum.setParent(this);
        }
    }

    /**
     * @return the indicator
     */
    public EClass getIndicatorType() {
        return indicatorType;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the children
     */
    public IndicatorEnum[] getChildren() {
        return children;
    }

    /**
     * Getter for indicatorClassifierId.
     * 
     * @return the indicatorClassifierId
     */
    public int getIndicatorClassifierId() {
        return indicatorClassifierId;
    }

    public boolean hasChildren() {
        return children != null;
    }

    public boolean hasParent() {
        return parent != null;
    }

    private void setParent(IndicatorEnum parent) {
        this.parent = parent;
    }

    /**
     * Getter for parent.
     * 
     * @return the parent
     */
    public IndicatorEnum getParent() {
        return parent;
    }

    public static IndicatorEnum findIndicatorEnum(EClass indicatorType) {
        IndicatorEnum returnEnum = null;
        if (indicatorType == RowCountIndicatorEnum.getIndicatorType()) {
            returnEnum = RowCountIndicatorEnum;
        } else if (indicatorType == NullCountIndicatorEnum.getIndicatorType()) {
            returnEnum = NullCountIndicatorEnum;
        } else if (indicatorType == DistinctCountIndicatorEnum.getIndicatorType()) {
            returnEnum = DistinctCountIndicatorEnum;
        } else if (indicatorType == UniqueIndicatorEnum.getIndicatorType()) {
            returnEnum = UniqueIndicatorEnum;
        } else if (indicatorType == DuplicateCountIndicatorEnum.getIndicatorType()) {
            returnEnum = DuplicateCountIndicatorEnum;
        } else if (indicatorType == BlankCountIndicatorEnum.getIndicatorType()) {
            returnEnum = BlankCountIndicatorEnum;
        } else if (indicatorType == MinLengthIndicatorEnum.getIndicatorType()) {
            returnEnum = MinLengthIndicatorEnum;
        } else if (indicatorType == MaxLengthIndicatorEnum.getIndicatorType()) {
            returnEnum = MaxLengthIndicatorEnum;
        } else if (indicatorType == AverageLengthIndicatorEnum.getIndicatorType()) {
            returnEnum = AverageLengthIndicatorEnum;
        } else if (indicatorType == ModeIndicatorEnum.getIndicatorType()) {
            returnEnum = ModeIndicatorEnum;
        } else if (indicatorType == MeanIndicatorEnum.getIndicatorType()) {
            returnEnum = MeanIndicatorEnum;
        } else if (indicatorType == MedianIndicatorEnum.getIndicatorType()) {
            returnEnum = MedianIndicatorEnum;
        } else if (indicatorType == MinValueIndicatorEnum.getIndicatorType()) {
            returnEnum = MinValueIndicatorEnum;
        } else if (indicatorType == MaxValueIndicatorEnum.getIndicatorType()) {
            returnEnum = MaxValueIndicatorEnum;
        } else if (indicatorType == LowerQuartileIndicatorEnum.getIndicatorType()) {
            returnEnum = LowerQuartileIndicatorEnum;
        } else if (indicatorType == UpperQuartileIndicatorEnum.getIndicatorType()) {
            returnEnum = UpperQuartileIndicatorEnum;
        } else if (indicatorType == IQRIndicatorEnum.getIndicatorType()) {
            returnEnum = IQRIndicatorEnum;
        } else if (indicatorType == RangeIndicatorEnum.getIndicatorType()) {
            returnEnum = RangeIndicatorEnum;
        } else if (indicatorType == FrequencyIndicatorEnum.getIndicatorType()) {
            returnEnum = FrequencyIndicatorEnum;
        } else if (indicatorType == LowFrequencyIndicatorEnum.getIndicatorType()) {
            returnEnum = LowFrequencyIndicatorEnum;
        } else if (indicatorType == PatternFreqIndicatorEnum.getIndicatorType()) {
            returnEnum = PatternFreqIndicatorEnum;
        } else if (indicatorType == PatternLowFreqIndicatorEnum.getIndicatorType()) {
            returnEnum = PatternLowFreqIndicatorEnum;
        } else if (indicatorType == TextIndicatorEnum.getIndicatorType()) {
            returnEnum = TextIndicatorEnum;
        } else if (indicatorType == BoxIIndicatorEnum.getIndicatorType()) {
            returnEnum = BoxIIndicatorEnum;
        } else if (indicatorType == CountsIndicatorEnum.getIndicatorType()) {
            returnEnum = CountsIndicatorEnum;
        } else if (indicatorType == RegexpMatchingIndicatorEnum.getIndicatorType()) {
            returnEnum = RegexpMatchingIndicatorEnum;
        } else if (indicatorType == SqlPatternMatchingIndicatorEnum.getIndicatorType()) {
            returnEnum = SqlPatternMatchingIndicatorEnum;
        }

        return returnEnum;

    }

    public static boolean isPlainIndicatorEnum(IndicatorEnum indicatorEnumn) {
        return indicatorEnumn != RegexpMatchingIndicatorEnum && indicatorEnumn != SqlPatternMatchingIndicatorEnum;
    }

}
