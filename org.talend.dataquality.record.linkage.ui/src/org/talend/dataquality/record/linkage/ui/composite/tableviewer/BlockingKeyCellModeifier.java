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
import org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.BlockingKeyPostAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.BlockingKeyPreAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.BlockKeyDefinition;


/**
 * created by zshen on Aug 6, 2013
 * Detailled comment
 *
 */
public class BlockingKeyCellModeifier implements ICellModifier {

    TableViewer tableViewer = null;

    /**
     * DOC zshen MatchRuleCellModifier constructor comment.
     */
    public BlockingKeyCellModeifier(TableViewer tableViewer) {
        this.tableViewer = tableViewer;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean canModify(Object element, String property) {
        if (element != null && element instanceof BlockKeyDefinition) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
     */
    @Override
    public Object getValue(Object element, String property) {
        BlockKeyDefinition bkd = (BlockKeyDefinition) element;
        if (MatchAnalysisConstant.PRE_ALGORITHM.equalsIgnoreCase(property)) {
            return BlockingKeyPreAlgorithmEnum.getTypeByValue(bkd.getPreAlgorithm().getAlgorithmType()).getIndex();
        } else if (MatchAnalysisConstant.PRE_VALUE.equalsIgnoreCase(property)) {
            return bkd.getPreAlgorithm().getAlgorithmParameters();
        } else if (MatchAnalysisConstant.ALGORITHM.equalsIgnoreCase(property)) {
            return BlockingKeyAlgorithmEnum.getTypeByValue(bkd.getAlgorithm().getAlgorithmType()).getIndex();
        } else if (MatchAnalysisConstant.VALUE.equalsIgnoreCase(property)) {
            return bkd.getAlgorithm().getAlgorithmParameters();
        } else if (MatchAnalysisConstant.POST_ALGORITHM.equalsIgnoreCase(property)) {
            return BlockingKeyPostAlgorithmEnum.getTypeByValue(bkd.getPostAlgorithm().getAlgorithmType()).getIndex();
        } else if (MatchAnalysisConstant.POST_VALUE.equalsIgnoreCase(property)) {
            return bkd.getPostAlgorithm().getAlgorithmParameters();
        } else if (MatchAnalysisConstant.COLUMN.equalsIgnoreCase(property)) {
            return bkd.getColumn();
        } else if (MatchAnalysisConstant.BLOCK_KEY_NAME.equalsIgnoreCase(property)) {
            return bkd.getName();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public void modify(Object element, String property, Object value) {
        if (element instanceof TableItem) {
            BlockKeyDefinition bkd = (BlockKeyDefinition) ((TableItem) element).getData();
            String newValue = String.valueOf(value);
            if (MatchAnalysisConstant.PRE_ALGORITHM.equalsIgnoreCase(property)) {
                BlockingKeyPreAlgorithmEnum valueByIndex = BlockingKeyPreAlgorithmEnum.getTypeByIndex(Integer.valueOf(newValue)
                        .intValue());
                bkd.getPreAlgorithm().setAlgorithmType(valueByIndex.getValue());
            } else if (MatchAnalysisConstant.PRE_VALUE.equalsIgnoreCase(property)) {
                bkd.getPreAlgorithm().setAlgorithmParameters(String.valueOf(value));
            } else if (MatchAnalysisConstant.ALGORITHM.equalsIgnoreCase(property)) {
                BlockingKeyAlgorithmEnum valueByIndex = BlockingKeyAlgorithmEnum.getTypeByIndex(Integer.valueOf(newValue)
                        .intValue());
                bkd.getAlgorithm().setAlgorithmType(valueByIndex.getValue());
            } else if (MatchAnalysisConstant.VALUE.equalsIgnoreCase(property)) {
                bkd.getAlgorithm().setAlgorithmParameters(newValue);
            } else if (MatchAnalysisConstant.POST_ALGORITHM.equalsIgnoreCase(property)) {
                BlockingKeyPostAlgorithmEnum valueByIndex = BlockingKeyPostAlgorithmEnum.getTypeByIndex(Integer.valueOf(newValue)
                        .intValue());
                bkd.getPostAlgorithm().setAlgorithmType(valueByIndex.getValue());
            } else if (MatchAnalysisConstant.POST_VALUE.equalsIgnoreCase(property)) {
                bkd.getPostAlgorithm().setAlgorithmParameters(newValue);
            } else if (MatchAnalysisConstant.COLUMN.equalsIgnoreCase(property)) {
                bkd.setColumn(newValue);
            } else if (MatchAnalysisConstant.BLOCK_KEY_NAME.equalsIgnoreCase(property)) {
                bkd.setName(newValue);
            }
            tableViewer.update(bkd, null);
        }
    }

}
