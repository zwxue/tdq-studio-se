// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.definition;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Color;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchRuleLabelProvider;
import org.talend.dataquality.record.linkage.utils.HandleNullEnum;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dq.helper.CustomAttributeMatcherHelper;

/**
 * created by zshen on Aug 27, 2013 Detailled comment
 * 
 */
public class MatchRuleDefinitionLabelProvider extends MatchRuleLabelProvider {

    boolean isAddThresholdColumn = false;

    public MatchRuleDefinitionLabelProvider(boolean isAddThresholdColumn) {
        this.isAddThresholdColumn = isAddThresholdColumn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchRuleLabelProvider#getColumnText(
     * java.lang.Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof MatchKeyDefinition) {
            MatchKeyDefinition mkd = (MatchKeyDefinition) element;
            if (isAddThresholdColumn) {
                switch (columnIndex) {
                case 0:
                    return mkd.getName();
                case 1:
                    return AttributeMatcherType.valueOf(mkd.getAlgorithm().getAlgorithmType()).getLabel();
                case 2:
                    return CustomAttributeMatcherHelper.getClassName(mkd.getAlgorithm().getAlgorithmParameters());
                case 3:
                    return String.valueOf(mkd.getThreshold());
                case 4:
                    return String.valueOf(mkd.getConfidenceWeight());
                case 5:
                    return HandleNullEnum.getTypeByValue(mkd.getHandleNull()).getLabel();
                }
            } else {
                switch (columnIndex) {
                case 0:
                    return mkd.getName();
                case 1:
                    return AttributeMatcherType.valueOf(mkd.getAlgorithm().getAlgorithmType()).getLabel();
                case 2:
                    return CustomAttributeMatcherHelper.getClassName(mkd.getAlgorithm().getAlgorithmParameters());
                case 3:
                    return String.valueOf(mkd.getConfidenceWeight());
                case 4:
                    return HandleNullEnum.getTypeByValue(mkd.getHandleNull()).getLabel();
                }
            }

        }
        return StringUtils.EMPTY;
    }

    /*
     * (non-Javadoc) differents with parent: lost one column: "column" , so columnIndex+1
     * 
     * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
     */
    @Override
    public Color getBackground(Object element, int columnIndex) {
        return super.getBackground(element, columnIndex + 1);
    }

}
