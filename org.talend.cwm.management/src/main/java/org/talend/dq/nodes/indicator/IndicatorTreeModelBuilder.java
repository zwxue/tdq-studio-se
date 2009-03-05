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
package org.talend.dq.nodes.indicator;

import org.talend.dq.nodes.indicator.impl.IndicatorCategoryNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.i18n.Messages;

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
                IndicatorEnum.PatternLowFreqIndicatorEnum };
        IndicatorCategoryNode patternFinderCategoryNode = new IndicatorCategoryNode(Messages
                .getString("IndicatorTreeModelBuilder.PatternStatistics"), //$NON-NLS-1$
                patternFinderEnums);
        // MOD mzhao 2009-03-05 build Soundex Statistic categoryNode
        IndicatorEnum[] soundexIndicatorEnums = new IndicatorEnum[] { IndicatorEnum.SoundexIndicatorEnum,
                IndicatorEnum.SoundexLowIndicatorEnum };
        IndicatorCategoryNode soundexCategoryNode = new IndicatorCategoryNode(Messages
                .getString("IndicatorTreeModelBuilder.SoundexStatistics"), //$NON-NLS-1$
                soundexIndicatorEnums);

        // build Nominal Statistic categoryNode
        IndicatorEnum[] advanceIndicatorEnums = new IndicatorEnum[] { IndicatorEnum.ModeIndicatorEnum,
                IndicatorEnum.FrequencyIndicatorEnum, IndicatorEnum.LowFrequencyIndicatorEnum };
        IndicatorCategoryNode advanceCategoryNode = new IndicatorCategoryNode(Messages
                .getString("IndicatorTreeModelBuilder.AdvancedStatistics"), advanceIndicatorEnums); //$NON-NLS-1$

        indicatorCategoryNodes = new IndicatorCategoryNode[] { simpleCategoryNode, textCategoryNode, boxCategoryNode,
                patternFinderCategoryNode, soundexCategoryNode, advanceCategoryNode };
        return indicatorCategoryNodes;
    }
}
