// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.indicators.definitions;

import org.eclipse.emf.ecore.EObject;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.AvgLengthWithBlankIndicator;
import org.talend.dataquality.indicators.AvgLengthWithBlankNullIndicator;
import org.talend.dataquality.indicators.AvgLengthWithNullIndicator;
import org.talend.dataquality.indicators.BenfordLawFrequencyIndicator;
import org.talend.dataquality.indicators.BinFrequencyIndicator;
import org.talend.dataquality.indicators.BinLowFrequencyIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.BoxIndicator;
import org.talend.dataquality.indicators.CountsIndicator;
import org.talend.dataquality.indicators.DateFrequencyIndicator;
import org.talend.dataquality.indicators.DateLowFrequencyIndicator;
import org.talend.dataquality.indicators.DatePatternFreqIndicator;
import org.talend.dataquality.indicators.DefValueCountIndicator;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.FormatFreqPieIndicator;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.IQRIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.InvalidRegCodeCountIndicator;
import org.talend.dataquality.indicators.LengthIndicator;
import org.talend.dataquality.indicators.LowFrequencyIndicator;
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
import org.talend.dataquality.indicators.MonthFrequencyIndicator;
import org.talend.dataquality.indicators.MonthLowFrequencyIndicator;
import org.talend.dataquality.indicators.NullCountIndicator;
import org.talend.dataquality.indicators.PatternFreqIndicator;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.PhoneNumbStatisticsIndicator;
import org.talend.dataquality.indicators.PossiblePhoneCountIndicator;
import org.talend.dataquality.indicators.QuarterFrequencyIndicator;
import org.talend.dataquality.indicators.QuarterLowFrequencyIndicator;
import org.talend.dataquality.indicators.RangeIndicator;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.SoundexFreqIndicator;
import org.talend.dataquality.indicators.SoundexLowFreqIndicator;
import org.talend.dataquality.indicators.SqlPatternMatchingIndicator;
import org.talend.dataquality.indicators.SumIndicator;
import org.talend.dataquality.indicators.TextIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.UpperQuartileIndicator;
import org.talend.dataquality.indicators.ValidPhoneCountIndicator;
import org.talend.dataquality.indicators.ValidRegCodeCountIndicator;
import org.talend.dataquality.indicators.WeekFrequencyIndicator;
import org.talend.dataquality.indicators.WeekLowFrequencyIndicator;
import org.talend.dataquality.indicators.WellFormE164PhoneCountIndicator;
import org.talend.dataquality.indicators.WellFormIntePhoneCountIndicator;
import org.talend.dataquality.indicators.WellFormNationalPhoneCountIndicator;
import org.talend.dataquality.indicators.YearFrequencyIndicator;
import org.talend.dataquality.indicators.YearLowFrequencyIndicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;
import org.talend.dataquality.indicators.columnset.MinMaxDateIndicator;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dataquality.indicators.columnset.WeakCorrelationIndicator;
import org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dataquality.indicators.schema.TableIndicator;
import org.talend.dataquality.indicators.schema.ViewIndicator;
import org.talend.dataquality.indicators.schema.util.SchemaSwitch;
import org.talend.dataquality.indicators.util.IndicatorsSwitch;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * 
 * DOC scorreia. All indicator definitions defined in .Talend.definition file must be implemented here.
 * 
 * WARNING: The label of the indicator definition in .Talend.definition must be exactly the same as the strings used
 * here. <br>
 * mzhao split system indicators. feature 13676, 2010-07-12
 */
class IndcatorSwitchImp extends IndicatorsSwitch<Boolean> {

    public IndcatorSwitchImp() {
    }

    private Boolean setIndicatorDefinition(Indicator indicator, String definitionLabel) {
        // get the definition
        IndicatorDefinition indicatorDefinition = DefinitionHandler.getInstance().getIndicatorDefinition(definitionLabel);
        if (indicatorDefinition == null) {
            return false;
        }
        // else
        indicator.setIndicatorDefinition(indicatorDefinition);
        return true;
    }

    private final SchemaSwitch<Boolean> schemaIndicatorSwitch = new SchemaSwitch<Boolean>() {

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch# caseCatalogIndicator(org.talend.dataquality
         * .indicators.schema.CatalogIndicator)
         */
        @Override
        public Boolean caseCatalogIndicator(CatalogIndicator object) {
            return setIndicatorDefinition(object, "Catalog Overview"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch#
         * caseConnectionIndicator(org.talend.dataquality .indicators.schema.ConnectionIndicator)
         */
        @Override
        public Boolean caseConnectionIndicator(ConnectionIndicator object) {
            return setIndicatorDefinition(object, "Connection Overview"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch# caseSchemaIndicator(org.talend.dataquality
         * .indicators.schema.SchemaIndicator)
         */
        @Override
        public Boolean caseSchemaIndicator(SchemaIndicator object) {
            return setIndicatorDefinition(object, "Schema Overview"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch# caseTableIndicator(org.talend.dataquality.
         * indicators.schema.TableIndicator)
         */
        @Override
        public Boolean caseTableIndicator(TableIndicator object) {
            return setIndicatorDefinition(object, "Table Overview"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch# caseViewIndicator(org.talend.dataquality.
         * indicators.schema.ViewIndicator)
         */
        @Override
        public Boolean caseViewIndicator(ViewIndicator object) {
            return setIndicatorDefinition(object, "View Overview"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.schema.util.SchemaSwitch# defaultCase(org.eclipse.emf.ecore.EObject)
         */
        @Override
        public Boolean defaultCase(EObject object) {
            return false;
        }

    };

    private final ColumnsetSwitch<Boolean> columnIndicatorSwitch = new ColumnsetSwitch<Boolean>() {

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch #caseRowMatchingIndicator(org.talend.
         * dataquality.indicators.columnset.RowMatchingIndicator)
         */
        @Override
        public Boolean caseRowMatchingIndicator(RowMatchingIndicator object) {
            return setIndicatorDefinition(object, "Row Comparison"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch #caseColumnSetMultiValueIndicator(org
         * .talend.dataquality.indicators .columnset.ColumnSetMultiValueIndicator)
         */
        @Override
        public Boolean caseColumnSetMultiValueIndicator(ColumnSetMultiValueIndicator object) {
            return setIndicatorDefinition(object, "Multiple Column Frequency Table"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch
         * #caseAllMatchIndicator(org.talend.dataquality .indicators .columnset.AllMatchIndicator)
         */
        public Boolean caseAllMatchIndicator(org.talend.dataquality.indicators.columnset.AllMatchIndicator object) {
            return setIndicatorDefinition(object, "All Match"); //$NON-NLS-1$
        };

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.dataquality.indicators.columnset.util.ColumnsetSwitch#caseSimpleStatIndicator(org.talend.
         * dataquality.indicators.columnset.SimpleStatIndicator)
         */
        @Override
        public Boolean caseSimpleStatIndicator(SimpleStatIndicator object) {
            return setIndicatorDefinition(object, "Multiple Column Simple Statistics"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch
         * #caseWeakCorrelationIndicator(org.talend .dataquality.indicators.columnset.WeakCorrelationIndicator)
         */
        @Override
        public Boolean caseWeakCorrelationIndicator(WeakCorrelationIndicator object) {
            return setIndicatorDefinition(object, "Multiple Column Correlation"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch #caseCountAvgNullIndicator(org.talend
         * .dataquality.indicators.columnset.CountAvgNullIndicator)
         */
        @Override
        public Boolean caseCountAvgNullIndicator(CountAvgNullIndicator object) {
            return setIndicatorDefinition(object, "Averaged Multiple Column Frequency Table"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * 
         * 
         * @seeorg.talend.dataquality.indicators.columnset.util.ColumnsetSwitch #caseMinMaxDateIndicator(org.talend.
         * dataquality.indicators.columnset.MinMaxDateIndicator)
         */
        @Override
        public Boolean caseMinMaxDateIndicator(MinMaxDateIndicator object) {
            return setIndicatorDefinition(object, "Min Max Date Multiple Column Frequency Table"); //$NON-NLS-1$
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.indicators.columnset.util.ColumnsetSwitch
         * #defaultCase(org.eclipse.emf.ecore.EObject )
         */
        @Override
        public Boolean defaultCase(EObject object) {
            // try schemaswitch
            return schemaIndicatorSwitch.doSwitch(object);
        }

    };

    @Override
    public Boolean defaultCase(EObject object) {
        // try with columnSetSwitch
        return columnIndicatorSwitch.doSwitch(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch# caseDefValueCountIndicator(org.talend.dataquality
     * .indicators.DefValueCountIndicator)
     */
    @Override
    public Boolean caseDefValueCountIndicator(DefValueCountIndicator object) {
        return setIndicatorDefinition(object, "Default Value Count"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch# caseLowFrequencyIndicator(org.talend.dataquality.
     * indicators.LowFrequencyIndicator)
     */
    @Override
    public Boolean caseLowFrequencyIndicator(LowFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Low Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseDateLowFrequencyIndicator(DateLowFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Date Low Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseWeekLowFrequencyIndicator(WeekLowFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Week Low Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseMonthLowFrequencyIndicator(MonthLowFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Month Low Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseQuarterLowFrequencyIndicator(QuarterLowFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Quarter Low Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseYearLowFrequencyIndicator(YearLowFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Year Low Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseBinLowFrequencyIndicator(BinLowFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Bin Low Frequency Table"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch# casePatternFreqIndicator(org.talend.dataquality.
     * indicators.PatternFreqIndicator)
     */
    @Override
    public Boolean casePatternFreqIndicator(PatternFreqIndicator object) {
        return setIndicatorDefinition(object, "Pattern Frequency Table"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch# casePatternLowFreqIndicator(org.talend.dataquality
     * .indicators.PatternLowFreqIndicator)
     */
    @Override
    public Boolean casePatternLowFreqIndicator(PatternLowFreqIndicator object) {
        return setIndicatorDefinition(object, "Pattern Low Frequency Table"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.util.IndicatorsSwitch# caseDatePatternFreqIndicator(org.talend.dataquality
     * .indicators.DatePatternFreqIndicator)
     */
    @Override
    public Boolean caseDatePatternFreqIndicator(DatePatternFreqIndicator object) {
        return setIndicatorDefinition(object, "Date Pattern Frequency Table"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch# caseRegexpMatchingIndicator(org.talend.dataquality
     * .indicators.RegexpMatchingIndicator)
     */
    @Override
    public Boolean caseRegexpMatchingIndicator(RegexpMatchingIndicator object) {
        return setIndicatorDefinition(object, DefinitionHandler.REGULAR_EXPRESSION_MATCHING);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch#
     * caseSqlPatternMatchingIndicator(org.talend.dataquality .indicators.SqlPatternMatchingIndicator)
     */
    @Override
    public Boolean caseSqlPatternMatchingIndicator(SqlPatternMatchingIndicator object) {
        return setIndicatorDefinition(object, "SQL Pattern Matching"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch# casePatternMatchingIndicator(org.talend.dataquality
     * .indicators.PatternMatchingIndicator)
     */
    @Override
    public Boolean casePatternMatchingIndicator(PatternMatchingIndicator object) {
        return setIndicatorDefinition(object, "Pattern Matching Indicator"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseCountsIndicator(CountsIndicator object) {
        return setIndicatorDefinition(object, "Simple Statistics"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseTextIndicator(TextIndicator object) {
        return setIndicatorDefinition(object, "Text Statistics"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseUniqueCountIndicator(UniqueCountIndicator object) {
        return setIndicatorDefinition(object, "Unique Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseSumIndicator(SumIndicator object) {
        return setIndicatorDefinition(object, "Sum"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseRowCountIndicator(RowCountIndicator object) {
        return setIndicatorDefinition(object, "Row Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseRangeIndicator(RangeIndicator object) {
        return setIndicatorDefinition(object, "Range"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseNullCountIndicator(NullCountIndicator object) {
        return setIndicatorDefinition(object, "Null Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseModeIndicator(ModeIndicator object) {
        return setIndicatorDefinition(object, "Mode"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseMinValueIndicator(MinValueIndicator object) {
        return setIndicatorDefinition(object, "Minimum"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseMinLengthIndicator(MinLengthIndicator object) {
        return setIndicatorDefinition(object, "Minimal Length"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseMinLengthWithNullIndicator(MinLengthWithNullIndicator object) {
        return setIndicatorDefinition(object, IndicatorEnum.MinLengthWithNullIndicatorEnum.getLabel()); //$NON-NLS-1$
    }

    @Override
    public Boolean caseMinLengthWithBlankIndicator(MinLengthWithBlankIndicator object) {
        return setIndicatorDefinition(object, IndicatorEnum.MinLengthWithBlankIndicatorEnum.getLabel());
    }

    @Override
    public Boolean caseMinLengthWithBlankNullIndicator(MinLengthWithBlankNullIndicator object) {
        return setIndicatorDefinition(object, IndicatorEnum.MinLengthWithBlankNullIndicatorEnum.getLabel());
    }

    @Override
    public Boolean caseMedianIndicator(MedianIndicator object) {
        return setIndicatorDefinition(object, "Median"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseMeanIndicator(MeanIndicator object) {
        return setIndicatorDefinition(object, "Mean"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseMaxValueIndicator(MaxValueIndicator object) {
        return setIndicatorDefinition(object, "Maximum"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseMaxLengthWithNullIndicator(MaxLengthWithNullIndicator object) {
        return setIndicatorDefinition(object, IndicatorEnum.MaxLengthWithNullIndicatorEnum.getLabel());
    }

    @Override
    public Boolean caseMaxLengthWithBlankIndicator(MaxLengthWithBlankIndicator object) {
        return setIndicatorDefinition(object, IndicatorEnum.MaxLengthWithBlankIndicatorEnum.getLabel());
    }

    @Override
    public Boolean caseMaxLengthWithBlankNullIndicator(MaxLengthWithBlankNullIndicator object) {
        return setIndicatorDefinition(object, IndicatorEnum.MaxLengthWithBlankNullIndicatorEnum.getLabel());
    }

    @Override
    public Boolean caseMaxLengthIndicator(MaxLengthIndicator object) {
        return setIndicatorDefinition(object, "Maximal Length"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseLengthIndicator(LengthIndicator object) {
        return setIndicatorDefinition(object, "Length"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseIQRIndicator(IQRIndicator object) {
        return setIndicatorDefinition(object, "IQR"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseFrequencyIndicator(FrequencyIndicator object) {
        return setIndicatorDefinition(object, "Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseDateFrequencyIndicator(DateFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Date Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseWeekFrequencyIndicator(WeekFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Week Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseMonthFrequencyIndicator(MonthFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Month Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseQuarterFrequencyIndicator(QuarterFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Quarter Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseYearFrequencyIndicator(YearFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Year Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseBinFrequencyIndicator(BinFrequencyIndicator object) {
        return setIndicatorDefinition(object, "Bin Frequency Table"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch# caseSoundexFreqIndicator(org.talend.dataquality.
     * indicators.SoundexFreqIndicator)
     */
    @Override
    public Boolean caseSoundexFreqIndicator(SoundexFreqIndicator object) {
        return setIndicatorDefinition(object, "Soundex Frequency Table"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataquality.indicators.util.IndicatorsSwitch# caseSoundexLowFreqIndicator(org.talend.dataquality
     * .indicators.SoundexLowFreqIndicator)
     */
    @Override
    public Boolean caseSoundexLowFreqIndicator(SoundexLowFreqIndicator object) {
        return setIndicatorDefinition(object, "Soundex Low Frequency Table"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseDuplicateCountIndicator(DuplicateCountIndicator object) {
        return setIndicatorDefinition(object, "Duplicate Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseDistinctCountIndicator(DistinctCountIndicator object) {
        return setIndicatorDefinition(object, "Distinct Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseBoxIndicator(BoxIndicator object) {
        return setIndicatorDefinition(object, "Summary Statistics"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseBlankCountIndicator(BlankCountIndicator object) {
        return setIndicatorDefinition(object, "Blank Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseAverageLengthIndicator(AverageLengthIndicator object) {
        return setIndicatorDefinition(object, "Average Length"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseAvgLengthWithNullIndicator(AvgLengthWithNullIndicator object) {
        return setIndicatorDefinition(object, IndicatorEnum.AverageLengthWithNullIndicatorEnum.getLabel());
    }

    @Override
    public Boolean caseAvgLengthWithBlankIndicator(AvgLengthWithBlankIndicator object) {
        return setIndicatorDefinition(object, IndicatorEnum.AverageLengthWithBlankIndicatorEnum.getLabel());
    }

    @Override
    public Boolean caseAvgLengthWithBlankNullIndicator(AvgLengthWithBlankNullIndicator object) {
        return setIndicatorDefinition(object, IndicatorEnum.AverageLengthWithNullBlankIndicatorEnum.getLabel());
    }

    @Override
    public Boolean caseLowerQuartileIndicator(LowerQuartileIndicator object) {
        return setIndicatorDefinition(object, "Lower Quartile"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseUpperQuartileIndicator(UpperQuartileIndicator object) {
        return setIndicatorDefinition(object, "Upper Quartile"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseValidPhoneCountIndicator(ValidPhoneCountIndicator object) {
        return setIndicatorDefinition(object, "Valid Phone Number Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean casePossiblePhoneCountIndicator(PossiblePhoneCountIndicator object) {
        return setIndicatorDefinition(object, "Possible Phone Number Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseValidRegCodeCountIndicator(ValidRegCodeCountIndicator object) {
        return setIndicatorDefinition(object, "Valid Region Code Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseInvalidRegCodeCountIndicator(InvalidRegCodeCountIndicator object) {
        return setIndicatorDefinition(object, "Invalid Region Code Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseWellFormNationalPhoneCountIndicator(WellFormNationalPhoneCountIndicator object) {
        return setIndicatorDefinition(object, "Well Formed National Phone Number Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseWellFormIntePhoneCountIndicator(WellFormIntePhoneCountIndicator object) {
        return setIndicatorDefinition(object, "Well Formed International Phone Number Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseWellFormE164PhoneCountIndicator(WellFormE164PhoneCountIndicator object) {
        return setIndicatorDefinition(object, "Well Formed E164 Phone Number Count"); //$NON-NLS-1$
    }

    @Override
    public Boolean casePhoneNumbStatisticsIndicator(PhoneNumbStatisticsIndicator object) {
        return setIndicatorDefinition(object, "Phone Number Statistics"); //$NON-NLS-1$
    }

    @Override
    public Boolean caseFormatFreqPieIndicator(FormatFreqPieIndicator object) {
        return setIndicatorDefinition(object, "Format Frequency Pie");
    }

    @Override
    public Boolean caseBenfordLawFrequencyIndicator(BenfordLawFrequencyIndicator object) {
        return setIndicatorDefinition(object, IndicatorEnum.BenfordLawFrequencyIndicatorEnum.getLabel());
    }

}
