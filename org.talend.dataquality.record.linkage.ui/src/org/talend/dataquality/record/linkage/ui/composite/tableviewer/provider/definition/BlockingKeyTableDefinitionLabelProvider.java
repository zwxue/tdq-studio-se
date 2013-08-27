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
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.BlockingKeyTableLabelProvider;
import org.talend.dataquality.rules.BlockKeyDefinition;


/**
 * created by zshen on Aug 27, 2013
 * Detailled comment
 *
 */
public class BlockingKeyTableDefinitionLabelProvider extends BlockingKeyTableLabelProvider {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.BlockingKeyTableLabelProvider#getColumnText
     * (java.lang.Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof BlockKeyDefinition) {
            BlockKeyDefinition bkd = (BlockKeyDefinition) element;
            switch (columnIndex) {
            case 0:
                return bkd.getName();
            case 1:
                return bkd.getPreAlgorithm().getAlgorithmType();
            case 2:
                return bkd.getPreAlgorithm().getAlgorithmParameters();
            case 3:
                return bkd.getAlgorithm().getAlgorithmType();
            case 4:
                return bkd.getAlgorithm().getAlgorithmParameters();
            case 5:
                return bkd.getPostAlgorithm().getAlgorithmType();
            case 6:
                return bkd.getPostAlgorithm().getAlgorithmParameters();
            }

        }
        return element == null ? StringUtils.EMPTY : element.toString();
    }

}
