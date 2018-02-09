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
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition.MatchKeyAndSurvivorDefinition;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchRuleLabelProvider;
import org.talend.dataquality.record.linkage.utils.CustomAttributeMatcherClassNameConvert;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class MatchKeyAndSurvivorLabelProvider extends MatchRuleLabelProvider {

    boolean isAddThresholdColumn = false;

    public MatchKeyAndSurvivorLabelProvider(boolean isAddThresholdColumn) {
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
        if (element instanceof MatchKeyAndSurvivorDefinition) {
            MatchKeyAndSurvivorDefinition mkd = (MatchKeyAndSurvivorDefinition) element;
            // used for MDM T-swoosh
            switch (columnIndex) {
            case 0:
                return mkd.getMatchKey().getName();
            case 1:
                return AttributeMatcherType.valueOf(mkd.getMatchKey().getAlgorithm().getAlgorithmType()).getLabel();
            case 2:
                return CustomAttributeMatcherClassNameConvert.getClassName(mkd.getMatchKey().getAlgorithm()
                        .getAlgorithmParameters());
            case 3:
                return String.valueOf(mkd.getMatchKey().getThreshold());
            case 4:
                return String.valueOf(mkd.getMatchKey().getConfidenceWeight());
            case 5:
                return mkd.getMatchKey().getHandleNull();
            case 6:
                return SurvivorShipAlgorithmEnum.getTypeBySavedValue(mkd.getSurvivorShipKey().getFunction().getAlgorithmType())
                        .getValue();

            case 7:
                return mkd.getSurvivorShipKey().getFunction().getAlgorithmParameters();

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

    // used for ViewerComparator's compare method, when move up/down
    @Override
    public String getText(Object element) {
        if (element == null) {
            return StringUtils.EMPTY;
        } else if (element instanceof MatchKeyAndSurvivorDefinition) {
            return ((MatchKeyAndSurvivorDefinition) element).getMatchKey().getName();
        } else {
            return StringUtils.EMPTY;
        }
    }

}
