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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.dataquality.rules.BlockKeyDefinition;


/**
 * created by zshen on Aug 6, 2013
 * Detailled comment
 *
 */
public class BlockingKeyTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof BlockKeyDefinition) {
            BlockKeyDefinition bkd = (BlockKeyDefinition) element;
            switch (columnIndex) {
            case 0:
                return bkd.getName();
            case 1:
                return bkd.getColumn();
            case 2:
                return bkd.getPreAlgorithm().getAlgorithmType();
            case 3:
                return bkd.getPreAlgorithm().getAlgorithmParameters();
            case 4:
                return bkd.getAlgorithm().getAlgorithmType();
            case 5:
                return bkd.getAlgorithm().getAlgorithmParameters();
            case 6:
                return bkd.getPostAlgorithm().getAlgorithmType();
            case 7:
                return bkd.getPostAlgorithm().getAlgorithmParameters();
            }

        }
        return "";
    }

}
