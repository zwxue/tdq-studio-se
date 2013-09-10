// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchRuleLabelProvider;
import org.talend.dataquality.rules.MatchKeyDefinition;


/**
 * created by zshen on Aug 27, 2013
 * Detailled comment
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
                    return AttributeMatcherType.getTypeBySavedValue(mkd.getAlgorithm().getAlgorithmType()).getLabel();
                case 2:
                    return mkd.getAlgorithm().getAlgorithmParameters();
                case 3:
                    return String.valueOf(mkd.getThreshold());
                case 4:
                    return String.valueOf(mkd.getConfidenceWeight());
                case 5:
                    return mkd.getHandleNull();
                }
            } else {
                switch (columnIndex) {
                case 0:
                    return mkd.getName();
                case 1:
                    return AttributeMatcherType.getTypeBySavedValue(mkd.getAlgorithm().getAlgorithmType()).getLabel();
                case 2:
                    return mkd.getAlgorithm().getAlgorithmParameters();
                case 3:
                    return String.valueOf(mkd.getConfidenceWeight());
                case 4:
                    return mkd.getHandleNull();
                }
            }

        }
        return StringUtils.EMPTY;
    }

}
