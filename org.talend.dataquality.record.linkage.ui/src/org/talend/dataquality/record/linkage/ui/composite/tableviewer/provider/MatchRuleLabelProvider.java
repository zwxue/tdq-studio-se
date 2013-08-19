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
import org.talend.dataquality.rules.MatchKeyDefinition;

/**
 * created by zshen on Aug 1, 2013 Detailled comment
 * 
 */
public class MatchRuleLabelProvider extends LabelProvider implements ITableLabelProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof MatchKeyDefinition) {
            MatchKeyDefinition mkd = (MatchKeyDefinition) element;
            switch (columnIndex) {
            case 0:
                return mkd.getName();
            case 1:
                return mkd.getColumn();
            case 2:
                return mkd.getAlgorithm().getAlgorithmType();
            case 3:
                return mkd.getAlgorithm().getAlgorithmParameters();
            case 4:
                return String.valueOf(mkd.getConfidenceWeight());
            case 5:
                return mkd.getHandleNull();
            }

        }
        // TODO zshen return empty is enough?
        return "";
    }

}
