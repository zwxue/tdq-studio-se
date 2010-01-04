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
package org.talend.dq.nodes.indicator.type;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.sql.IndicatorSqlPackage;

/**
 * @author rli
 * 
 */
public enum IndicatorEnum {
    SqlPatternMatchingIndicatorEnum(
                                    IndicatorsPackage.SQL_PATTERN_MATCHING_INDICATOR,
                                    IndicatorsPackage.Literals.SQL_PATTERN_MATCHING_INDICATOR,
                                    "sql like matching", //$NON-NLS-1$
                                    null),
    RegexpMatchingIndicatorEnum(
                                IndicatorsPackage.REGEXP_MATCHING_INDICATOR,
                                IndicatorsPackage.Literals.REGEXP_MATCHING_INDICATOR,
                                "regexp matching", //$NON-NLS-1$
                                null),
    RowCountIndicatorEnum(IndicatorsPackage.ROW_COUNT_INDICATOR, IndicatorsPackage.Literals.ROW_COUNT_INDICATOR, "row count", //$NON-NLS-1$
                          null),
    NullCountIndicatorEnum(IndicatorsPackage.NULL_COUNT_INDICATOR, IndicatorsPackage.Literals.NULL_COUNT_INDICATOR, "null count", //$NON-NLS-1$
                           null),
    DistinctCountIndicatorEnum(
                               IndicatorsPackage.DISTINCT_COUNT_INDICATOR,
                               IndicatorsPackage.Literals.DISTINCT_COUNT_INDICATOR,
                               "distinct count", //$NON-NLS-1$
                               null),
    UniqueIndicatorEnum(
                        IndicatorsPackage.UNIQUE_COUNT_INDICATOR,
                        IndicatorsPackage.Literals.UNIQUE_COUNT_INDICATOR,
                        "unique count", //$NON-NLS-1$
                        null),
    DuplicateCountIndicatorEnum(
                                IndicatorsPackage.DUPLICATE_COUNT_INDICATOR,
                                IndicatorsPackage.Literals.DUPLICATE_COUNT_INDICATOR,
                                "duplicate count", //$NON-NLS-1$
                                null),
    BlankCountIndicatorEnum(
                            IndicatorsPackage.BLANK_COUNT_INDICATOR,
                            IndicatorsPackage.Literals.BLANK_COUNT_INDICATOR,
                            "blank count", //$NON-NLS-1$
                            null),
    DefValueCountIndicatorEnum(
                               IndicatorsPackage.DEF_VALUE_COUNT_INDICATOR,
                               IndicatorsPackage.Literals.DEF_VALUE_COUNT_INDICATOR,
                               "default value count", //$NON-NLS-1$
                               null),

    MinLengthIndicatorEnum(IndicatorsPackage.MIN_LENGTH_INDICATOR, IndicatorsPackage.Literals.MIN_LENGTH_INDICATOR, "min length", //$NON-NLS-1$
                           null),
    MaxLengthIndicatorEnum(IndicatorsPackage.MAX_LENGTH_INDICATOR, IndicatorsPackage.Literals.MAX_LENGTH_INDICATOR, "max length", //$NON-NLS-1$
                           null),

    AverageLengthIndicatorEnum(
                               IndicatorsPackage.AVERAGE_LENGTH_INDICATOR,
                               IndicatorsPackage.Literals.AVERAGE_LENGTH_INDICATOR,
                               "average length", //$NON-NLS-1$
                               null),

    ModeIndicatorEnum(IndicatorsPackage.MODE_INDICATOR, IndicatorsPackage.Literals.MODE_INDICATOR, "mode", null), //$NON-NLS-1$
    FrequencyIndicatorEnum(
                           IndicatorsPackage.FREQUENCY_INDICATOR,
                           IndicatorsPackage.Literals.FREQUENCY_INDICATOR,
                           "frequency table", //$NON-NLS-1$
                           null),
    LowFrequencyIndicatorEnum(
                              IndicatorsPackage.LOW_FREQUENCY_INDICATOR,
                              IndicatorsPackage.Literals.LOW_FREQUENCY_INDICATOR,
                              "low frequency table", //$NON-NLS-1$
                              null),

    // MOD mzhao 2009-03-05 Soundex frequency indicator.
    SoundexIndicatorEnum(
                         IndicatorsPackage.SOUNDEX_FREQ_INDICATOR,
                         IndicatorsPackage.Literals.SOUNDEX_FREQ_INDICATOR,
                         "soundex frequency table", //$NON-NLS-1$
                         null),
    SoundexLowIndicatorEnum(
                            IndicatorsPackage.SOUNDEX_LOW_FREQ_INDICATOR,
                            IndicatorsPackage.Literals.SOUNDEX_LOW_FREQ_INDICATOR,
                            "soundex low frequency table", //$NON-NLS-1$
                            null),

    PatternFreqIndicatorEnum(
                             IndicatorsPackage.PATTERN_FREQ_INDICATOR,
                             IndicatorsPackage.Literals.PATTERN_FREQ_INDICATOR,
                             "pattern frequency table", //$NON-NLS-1$
                             null),
    PatternLowFreqIndicatorEnum(
                                IndicatorsPackage.PATTERN_LOW_FREQ_INDICATOR,
                                IndicatorsPackage.Literals.PATTERN_LOW_FREQ_INDICATOR,
                                "pattern low frequency table", //$NON-NLS-1$
                                null),

    MeanIndicatorEnum(IndicatorsPackage.MEAN_INDICATOR, IndicatorsPackage.Literals.MEAN_INDICATOR, "mean", null), //$NON-NLS-1$
    MedianIndicatorEnum(IndicatorsPackage.MEDIAN_INDICATOR, IndicatorsPackage.Literals.MEDIAN_INDICATOR, "median", null), //$NON-NLS-1$
    MinValueIndicatorEnum(IndicatorsPackage.MIN_VALUE_INDICATOR, IndicatorsPackage.Literals.MIN_VALUE_INDICATOR, "min.value", //$NON-NLS-1$
                          null),
    MaxValueIndicatorEnum(IndicatorsPackage.MAX_VALUE_INDICATOR, IndicatorsPackage.Literals.MAX_VALUE_INDICATOR, "max.value", //$NON-NLS-1$
                          null),
    LowerQuartileIndicatorEnum(
                               IndicatorsPackage.LOWER_QUARTILE_INDICATOR,
                               IndicatorsPackage.Literals.LOWER_QUARTILE_INDICATOR,
                               "lower quartile", //$NON-NLS-1$
                               null),
    UpperQuartileIndicatorEnum(
                               IndicatorsPackage.UPPER_QUARTILE_INDICATOR,
                               IndicatorsPackage.Literals.UPPER_QUARTILE_INDICATOR,
                               "upper quartile", //$NON-NLS-1$
                               null),
    CountsIndicatorEnum(IndicatorsPackage.COUNTS_INDICATOR, IndicatorsPackage.Literals.COUNTS_INDICATOR, "Simple Statistics", //$NON-NLS-1$
                        new IndicatorEnum[] { RowCountIndicatorEnum, NullCountIndicatorEnum, DistinctCountIndicatorEnum,
                                UniqueIndicatorEnum, DuplicateCountIndicatorEnum, BlankCountIndicatorEnum,
                                DefValueCountIndicatorEnum }),
    TextIndicatorEnum(IndicatorsPackage.TEXT_INDICATOR, IndicatorsPackage.Literals.TEXT_INDICATOR, "Text Statistics", //$NON-NLS-1$
                      new IndicatorEnum[] { MinLengthIndicatorEnum, MaxLengthIndicatorEnum, AverageLengthIndicatorEnum }),
    IQRIndicatorEnum(IndicatorsPackage.IQR_INDICATOR, IndicatorsPackage.Literals.IQR_INDICATOR, "Inter Quartile Range", //$NON-NLS-1$
                     new IndicatorEnum[] { LowerQuartileIndicatorEnum, UpperQuartileIndicatorEnum }),
    RangeIndicatorEnum(IndicatorsPackage.RANGE_INDICATOR, IndicatorsPackage.Literals.RANGE_INDICATOR, "Range", //$NON-NLS-1$
                       new IndicatorEnum[] { MinValueIndicatorEnum, MaxValueIndicatorEnum }),
    BoxIIndicatorEnum(IndicatorsPackage.BOX_INDICATOR, IndicatorsPackage.Literals.BOX_INDICATOR, "Summary Statistics", //$NON-NLS-1$
                      new IndicatorEnum[] { MeanIndicatorEnum, MedianIndicatorEnum, IQRIndicatorEnum, RangeIndicatorEnum }),
    WhereRuleIndicatorEnum(
                           IndicatorSqlPackage.WHERE_RULE_INDICATOR,
                           IndicatorSqlPackage.Literals.WHERE_RULE_INDICATOR,
                           "where rule", //$NON-NLS-1$
                           null),
    UserDefinedIndicatorEnum(
                             IndicatorSqlPackage.USER_DEF_INDICATOR,
                             IndicatorSqlPackage.Literals.USER_DEF_INDICATOR,
                             "User Defined Indicator", //$NON-NLS-1$
                             null),
    ColumnDependencyIndicatorEnum(
                                  ColumnsetPackage.COLUMN_DEPENDENCY_INDICATOR,
                                  ColumnsetPackage.Literals.COLUMN_DEPENDENCY_INDICATOR,
                                  "Column Dependency Indicator", //$NON-NLS-1$
                                  null),
    ColumnSetMultiValueIndicatorEnum(
                                     ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR,
                                     ColumnsetPackage.Literals.COLUMN_SET_MULTI_VALUE_INDICATOR,
                                     "Column Set Multi Value Indicator", //$NON-NLS-1$
                                     null),
    SimpleStatIndicatorEnum(
                            ColumnsetPackage.SIMPLE_STAT_INDICATOR,
                            ColumnsetPackage.Literals.SIMPLE_STAT_INDICATOR,
                            "Simple Stat Indicator", //$NON-NLS-1$
                            null);

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
        } else if (indicatorType == SoundexIndicatorEnum.getIndicatorType()) {
            returnEnum = SoundexIndicatorEnum;
        } else if (indicatorType == SoundexLowIndicatorEnum.getIndicatorType()) {
            returnEnum = SoundexLowIndicatorEnum;
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
        } else if (indicatorType == WhereRuleIndicatorEnum.getIndicatorType()) {
            returnEnum = WhereRuleIndicatorEnum;
        } else if (indicatorType == DefValueCountIndicatorEnum.getIndicatorType()) {
            returnEnum = DefValueCountIndicatorEnum;
        } else if (indicatorType == UserDefinedIndicatorEnum.getIndicatorType()) {
            returnEnum = UserDefinedIndicatorEnum;
        } else if (indicatorType == ColumnDependencyIndicatorEnum.getIndicatorType()) {
            returnEnum = ColumnDependencyIndicatorEnum;
        }

        return returnEnum;

    }

    public static boolean isPlainIndicatorEnum(IndicatorEnum indicatorEnumn) {
        return indicatorEnumn != RegexpMatchingIndicatorEnum && indicatorEnumn != SqlPatternMatchingIndicatorEnum
                && indicatorEnumn != WhereRuleIndicatorEnum && indicatorEnumn != UserDefinedIndicatorEnum;
    }

    public boolean isAChildOf(IndicatorEnum parent) {
        if (parent.getChildren() != null) {
            for (IndicatorEnum child : parent.getChildren()) {
                if (child == this) {
                    return true;
                }
            }
        }

        return false;
    }
}
