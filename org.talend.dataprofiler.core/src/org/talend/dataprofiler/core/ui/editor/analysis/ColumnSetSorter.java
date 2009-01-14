// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.analysis;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.talend.cwm.relational.TdColumn;

/**
 * The sorter extends ViewerSorter for column set sorting.
 * 
 * @author xqliu
 */
public class ColumnSetSorter extends ViewerSorter {

    public static final int COLUMN = 1;

    private int sortType;

    public ColumnSetSorter(int sortType) {
        this.sortType = sortType;
    }

    public int compare(Viewer viewer, Object o1, Object o2) {
        TdColumn column1 = (TdColumn) o1;
        TdColumn column2 = (TdColumn) o2;
        String value1;
        String value2;
        switch (sortType) {
        case COLUMN:
            value1 = column1.getName();
            value2 = column2.getName();
            return value1.compareTo(value2);

        case -COLUMN:
            value1 = column1.getName();
            value2 = column2.getName();
            return value2.compareTo(value1);
        default:
            return 0;
        }
    }
}
