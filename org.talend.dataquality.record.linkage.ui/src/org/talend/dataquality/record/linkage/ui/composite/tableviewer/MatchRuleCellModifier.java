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
package org.talend.dataquality.record.linkage.ui.composite.tableviewer;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.HandleNullEnum;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.MatchKeyDefinition;


/**
 * created by zshen on Aug 1, 2013
 * Detailled comment
 *
 */
public class MatchRuleCellModifier implements ICellModifier {

    TableViewer tableViewer = null;

    /**
     * DOC zshen MatchRuleCellModifier constructor comment.
     */
    public MatchRuleCellModifier(TableViewer tableViewer) {
        this.tableViewer = tableViewer;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean canModify(Object element, String property) {
        if (element != null && element instanceof MatchKeyDefinition) {
            MatchKeyDefinition mkd = (MatchKeyDefinition) element;
            if (MatchAnalysisConstant.CUSTOM_MATCHER_CLASS.equalsIgnoreCase(property)) {
                return AttributeMatcherType.getTypeByValue(mkd.getAlgorithm().getAlgorithmType()).isTakeParameter();
            }
            return true;
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
     */
    @Override
    public Object getValue(Object element, String property) {
        MatchKeyDefinition mkd = (MatchKeyDefinition) element;
        if (MatchAnalysisConstant.HANDLE_NULL.equalsIgnoreCase(property)) {
            return HandleNullEnum.getTypeByValue(mkd.getHandleNull()).getIndex();
        } else if (MatchAnalysisConstant.MATCHING_TYPE.equalsIgnoreCase(property)) {
            return AttributeMatcherType.getTypeByValue(mkd.getAlgorithm().getAlgorithmType()).getIndex();
        } else if (MatchAnalysisConstant.CUSTOM_MATCHER_CLASS.equalsIgnoreCase(property)) {
            return mkd.getAlgorithm().getAlgorithmParameters();
        } else if (MatchAnalysisConstant.COLUMN.equalsIgnoreCase(property)) {
            return mkd.getColumn();
        } else if (MatchAnalysisConstant.CONFIDENCE_WEIGHT.equalsIgnoreCase(property)) {
            return String.valueOf(mkd.getConfidenceWeight());
        } else if (MatchAnalysisConstant.MATCH_KEY_NAME.equalsIgnoreCase(property)) {
            return mkd.getName();
        } else if (MatchAnalysisConstant.THRESHOLD.equalsIgnoreCase(property)) {
            return String.valueOf(mkd.getThreshold());
        }
        return null;

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public void modify(Object element, String property, Object value) {
        if (element instanceof TableItem) {
            MatchKeyDefinition mkd = (MatchKeyDefinition) ((TableItem) element).getData();
            String newValue = String.valueOf(value);
            if (MatchAnalysisConstant.HANDLE_NULL.equalsIgnoreCase(property)) {
                HandleNullEnum valueByIndex = HandleNullEnum.getTypeByIndex(Integer.valueOf(newValue).intValue());
                mkd.setHandleNull(valueByIndex.getValue());
            } else if (MatchAnalysisConstant.MATCHING_TYPE.equalsIgnoreCase(property)) {
                AttributeMatcherType valueByIndex = AttributeMatcherType.getTypeByIndex(Integer.valueOf(newValue).intValue());
                mkd.getAlgorithm().setAlgorithmType(valueByIndex.getComponentName());
            } else if (MatchAnalysisConstant.CUSTOM_MATCHER_CLASS.equalsIgnoreCase(property)) {
                mkd.getAlgorithm().setAlgorithmParameters(String.valueOf(value));
            } else if (MatchAnalysisConstant.COLUMN.equalsIgnoreCase(property)) {
                mkd.setColumn(newValue);
            } else if (MatchAnalysisConstant.CONFIDENCE_WEIGHT.equalsIgnoreCase(property)) {
                mkd.setConfidenceWeight(Integer.valueOf(newValue).intValue());
            } else if (MatchAnalysisConstant.MATCH_KEY_NAME.equalsIgnoreCase(property)) {
                mkd.setName(newValue);
            } else if (MatchAnalysisConstant.THRESHOLD.equalsIgnoreCase(property)) {
                try {
                    mkd.setThreshold(Double.parseDouble(newValue));
                } catch (NumberFormatException e) {
                    // revert user change at here so don't need do anything
                }
            } else {
                return;
            }
            tableViewer.update(mkd, null);
        }
    }

}
