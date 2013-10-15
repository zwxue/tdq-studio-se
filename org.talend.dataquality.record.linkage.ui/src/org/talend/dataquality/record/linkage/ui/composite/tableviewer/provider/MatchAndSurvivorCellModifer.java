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
package org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.TableItem;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchCellModifier;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition.MatchKeyAndSurvivorDefinition;
import org.talend.dataquality.record.linkage.utils.HandleNullEnum;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class MatchAndSurvivorCellModifer extends AbstractMatchCellModifier<MatchKeyAndSurvivorDefinition> {

    public MatchAndSurvivorCellModifer(AbstractMatchAnalysisTableViewer<MatchKeyAndSurvivorDefinition> newTableViewer) {
        this.tableViewer = newTableViewer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean canModify(Object element, String property) {
        if (element != null && element instanceof MatchKeyAndSurvivorDefinition) {
            MatchKeyAndSurvivorDefinition mkd = (MatchKeyAndSurvivorDefinition) element;
            if (MatchAnalysisConstant.CUSTOM_MATCHER.equalsIgnoreCase(property)) {
                if (!AttributeMatcherType.CUSTOM.name().equals(mkd.getMatchKey().getAlgorithm().getAlgorithmType())) {
                    return false;
                }
            } 
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
     */
    @Override
    public Object getValue(Object element, String property) {
        MatchKeyAndSurvivorDefinition mkd = (MatchKeyAndSurvivorDefinition) element;
        if (MatchAnalysisConstant.HANDLE_NULL.equalsIgnoreCase(property)) {
            return HandleNullEnum.getTypeByValue(mkd.getMatchKey().getHandleNull()).ordinal();
        } else if (MatchAnalysisConstant.MATCHING_TYPE.equalsIgnoreCase(property)) {
            return AttributeMatcherType.valueOf(mkd.getMatchKey().getAlgorithm().getAlgorithmType()).ordinal();
        } else if (MatchAnalysisConstant.CUSTOM_MATCHER.equalsIgnoreCase(property)) {
            return mkd.getMatchKey().getAlgorithm().getAlgorithmParameters();
        } else if (MatchAnalysisConstant.INPUT_COLUMN.equalsIgnoreCase(property)) {
            return columnList.indexOf(mkd.getMatchKey().getColumn());
        } else if (MatchAnalysisConstant.CONFIDENCE_WEIGHT.equalsIgnoreCase(property)) {
            return String.valueOf(mkd.getMatchKey().getConfidenceWeight());
        } else if (MatchAnalysisConstant.MATCH_KEY_NAME.equalsIgnoreCase(property)) {
            return mkd.getMatchKey().getName();
        } else if (MatchAnalysisConstant.THRESHOLD.equalsIgnoreCase(property)) {
            return String.valueOf(mkd.getMatchKey().getThreshold());
        } else if (MatchAnalysisConstant.FUNCTION.equalsIgnoreCase(property)) {
            return SurvivorShipAlgorithmEnum.getTypeBySavedValue(mkd.getSurvivorShipKey().getFunction().getAlgorithmType())
                    .getIndex();
        } else if (MatchAnalysisConstant.ALLOW_MANUAL_RESOLUTION.equalsIgnoreCase(property)) {
            return mkd.getSurvivorShipKey().isAllowManualResolution();
        }
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public void modify(Object element, String property, Object value) {
        if (element instanceof TableItem) {
            MatchKeyAndSurvivorDefinition mkd = (MatchKeyAndSurvivorDefinition) ((TableItem) element).getData();
            String newValue = String.valueOf(value);
            if (MatchAnalysisConstant.HANDLE_NULL.equalsIgnoreCase(property)) {
                HandleNullEnum valueByIndex = HandleNullEnum.values()[Integer.valueOf(newValue)];
                if (StringUtils.equals(mkd.getMatchKey().getHandleNull(), valueByIndex.getValue())) {
                    return;
                }
                mkd.getMatchKey().setHandleNull(valueByIndex.getValue());
            } else if (MatchAnalysisConstant.MATCHING_TYPE.equalsIgnoreCase(property)) {
                int idx = Integer.valueOf(newValue);
                if (idx == AttributeMatcherType.DUMMY.ordinal()) {
                    idx += 1; // The DUMMY algorithm is internal and does not exist in the combo lists of
                              // MatchRuleItemEditor or MatchRuleAnalysisEditor. So we increment the index by 1 in order
                              // to get the correct Matcher.
                }
                AttributeMatcherType valueByIndex = AttributeMatcherType.values()[idx];
                if (StringUtils.equals(mkd.getMatchKey().getAlgorithm().getAlgorithmType(), valueByIndex.name())) {
                    return;
                }
                mkd.getMatchKey().getAlgorithm().setAlgorithmType(valueByIndex.name());
                mkd.getMatchKey().getAlgorithm().setAlgorithmParameters(StringUtils.EMPTY);
            } else if (MatchAnalysisConstant.CUSTOM_MATCHER.equalsIgnoreCase(property)) {
                if (StringUtils.equals(mkd.getMatchKey().getAlgorithm().getAlgorithmParameters(), newValue)) {
                    return;
                }
                mkd.getMatchKey().getAlgorithm().setAlgorithmParameters(String.valueOf(value));
            }else if (MatchAnalysisConstant.CONFIDENCE_WEIGHT.equalsIgnoreCase(property)) {
                if (mkd.getMatchKey().getConfidenceWeight() == Integer.valueOf(newValue).intValue()) {
                    return;
                }
                mkd.getMatchKey().setConfidenceWeight(Integer.valueOf(newValue).intValue());
            } else if (MatchAnalysisConstant.MATCH_KEY_NAME.equalsIgnoreCase(property)) {
                if (StringUtils.equals(mkd.getMatchKey().getName(), newValue)) {
                    return;
                }
                mkd.getMatchKey().setName(newValue);
            } else if (MatchAnalysisConstant.THRESHOLD.equalsIgnoreCase(property)) {
                if (mkd.getMatchKey().getThreshold() == Double.parseDouble(newValue)) {
                    return;
                }
                try {
                    mkd.getMatchKey().setThreshold(Double.parseDouble(newValue));
                } catch (NumberFormatException e) {
                    // revert user change at here so don't need do anything
                }
            } else if (MatchAnalysisConstant.FUNCTION.equalsIgnoreCase(property)) {
                SurvivorShipAlgorithmEnum valueByIndex = SurvivorShipAlgorithmEnum.getTypeByIndex(Integer.valueOf(newValue)
                        .intValue());
                if (StringUtils.equals(mkd.getSurvivorShipKey().getFunction().getAlgorithmType(),
                        valueByIndex.getComponentValueName())) {
                    return;
                }
                mkd.getSurvivorShipKey().getFunction().setAlgorithmType(valueByIndex.getComponentValueName());
            } else if (MatchAnalysisConstant.ALLOW_MANUAL_RESOLUTION.equalsIgnoreCase(property)) {
                if (mkd.getSurvivorShipKey().isAllowManualResolution() == Boolean.valueOf(newValue)) {
                    return;
                }
                mkd.getSurvivorShipKey().setAllowManualResolution(Boolean.valueOf(newValue));
            } else {
                return;
            }
            tableViewer.update(mkd, null);
        }
    }
}
