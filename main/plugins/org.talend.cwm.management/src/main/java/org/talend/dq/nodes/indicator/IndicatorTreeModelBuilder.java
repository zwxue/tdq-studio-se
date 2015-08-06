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
package org.talend.dq.nodes.indicator;

import org.talend.cwm.management.i18n.Messages;
import org.talend.dq.nodes.indicator.impl.IndicatorCategoryNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * This class for the indicator tree building.
 * 
 */
public final class IndicatorTreeModelBuilder {

    private IndicatorTreeModelBuilder() {
    }

    private static IndicatorCategoryNode[] indicatorCategoryNodes;

    public static IIndicatorNode[] buildIndicatorCategory() {
        if (indicatorCategoryNodes != null) {
            return indicatorCategoryNodes;
        }
        // build Basic Statistic categoryNode
        IndicatorCategoryNode simpleCategoryNode = new IndicatorCategoryNode(IndicatorEnum.CountsIndicatorEnum);
        // simpleCategoryNode.creatChildren(simpleIndicatorEnums);

        // build Text statistics categoryNode
        IndicatorCategoryNode textCategoryNode = new IndicatorCategoryNode(IndicatorEnum.TextIndicatorEnum);

        // build Summary Statistic categoryNode
        IndicatorCategoryNode boxCategoryNode = new IndicatorCategoryNode(IndicatorEnum.BoxIIndicatorEnum);

        // build pattern finder categoryNode
        IndicatorEnum[] patternFinderEnums = new IndicatorEnum[] { IndicatorEnum.PatternFreqIndicatorEnum,
                IndicatorEnum.PatternLowFreqIndicatorEnum, IndicatorEnum.DatePatternFreqIndicatorEnum };
        IndicatorCategoryNode patternFinderCategoryNode = new IndicatorCategoryNode(Messages
                .getString("IndicatorTreeModelBuilder.PatternStatistics"), //$NON-NLS-1$
                patternFinderEnums);
        // MOD mzhao 2009-03-05 build Soundex Statistic categoryNode
        IndicatorEnum[] soundexIndicatorEnums = new IndicatorEnum[] { IndicatorEnum.SoundexIndicatorEnum,
                IndicatorEnum.SoundexLowIndicatorEnum };
        IndicatorCategoryNode soundexCategoryNode = new IndicatorCategoryNode(Messages
                .getString("IndicatorTreeModelBuilder.SoundexStatistics"), //$NON-NLS-1$
                soundexIndicatorEnums);
        IndicatorCategoryNode phoneCategoryNode = new IndicatorCategoryNode(IndicatorEnum.PhoneNumbStatisticsIndicatorEnum);

        // build Nominal Statistic categoryNode
        IndicatorEnum[] advanceIndicatorEnums = new IndicatorEnum[] { IndicatorEnum.ModeIndicatorEnum,
                IndicatorEnum.FrequencyIndicatorEnum, IndicatorEnum.DateFrequencyIndicatorEnum,
                IndicatorEnum.WeekFrequencyIndicatorEnum, IndicatorEnum.MonthFrequencyIndicatorEnum,
                IndicatorEnum.QuarterFrequencyIndicatorEnum, IndicatorEnum.YearFrequencyIndicatorEnum,
                IndicatorEnum.BinFrequencyIndicatorEnum, IndicatorEnum.LowFrequencyIndicatorEnum,
                IndicatorEnum.DateLowFrequencyIndicatorEnum, IndicatorEnum.WeekLowFrequencyIndicatorEnum,
                IndicatorEnum.MonthLowFrequencyIndicatorEnum, IndicatorEnum.QuarterLowFrequencyIndicatorEnum,
                IndicatorEnum.YearLowFrequencyIndicatorEnum, IndicatorEnum.BinLowFrequencyIndicatorEnum };
        IndicatorCategoryNode advanceCategoryNode = new IndicatorCategoryNode(Messages
                .getString("IndicatorTreeModelBuilder.AdvancedStatistics"), advanceIndicatorEnums); //$NON-NLS-1$

        // Added yyin 20120827, TDQ-5076, build Fraud Detection category and SIndicator
        IndicatorEnum[] fraudIndicatorEnums = new IndicatorEnum[] { IndicatorEnum.BenfordLawFrequencyIndicatorEnum };
        IndicatorCategoryNode fraudCategoryNode = new IndicatorCategoryNode(
                Messages.getString("IndicatorTreeModelBuilder.FraudStatistics"), fraudIndicatorEnums);

        indicatorCategoryNodes = new IndicatorCategoryNode[] { simpleCategoryNode, textCategoryNode, boxCategoryNode,
                advanceCategoryNode, patternFinderCategoryNode, soundexCategoryNode, phoneCategoryNode, fraudCategoryNode };
        return indicatorCategoryNodes;
    }
}
