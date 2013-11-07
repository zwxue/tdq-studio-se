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
package org.talend.dataquality.record.linkage.ui.composite.table;

import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;

/**
 * Manage the sort direction for the same or changed column
 */
public class SortState {

    private SortDirectionEnum sortDirection = SortDirectionEnum.NONE;

    private int selectedColumnIndex = -1;

    // because the grp_size is String type, so need special operation before sorting
    private int grpSizeIndex = -1;

    // if the current column is the first time to click, should reset the sort direction
    // if the clicked column=current selected column, only need to get next direction
    private boolean isColumnChanged = Boolean.FALSE;

    public void reset() {
        sortDirection = SortDirectionEnum.NONE;
        selectedColumnIndex = -1;
        isColumnChanged = Boolean.FALSE;
        grpSizeIndex = -1;
    }

    public SortDirectionEnum getNextSortDirection() {
        if (isColumnChanged) {
            // get the first direction when column changed
            sortDirection = SortDirectionEnum.ASC;
        } else {
            sortDirection = sortDirection.getNextSortDirection();
        }
        return this.sortDirection;
    }

    public SortDirectionEnum getCurrentSortDirection() {
        return this.sortDirection;
    }

    public int getSelectedColumnIndex() {
        return this.selectedColumnIndex;
    }

    public void setSelectedColumnIndex(int clickedColumnIndex) {
        // if the selected column changed, set isColumnChanged= true
        if (clickedColumnIndex != selectedColumnIndex) {
            isColumnChanged = Boolean.TRUE;
        } else {
            isColumnChanged = Boolean.FALSE;
        }
        this.selectedColumnIndex = clickedColumnIndex;
    }

    public void setGrpSizeIndex(int grpSizeIndex) {
        this.grpSizeIndex = grpSizeIndex;
    }

    public int getGrpSizeIndex() {
        return this.grpSizeIndex;
    }

    public boolean isGroupSizeColumn() {
        return grpSizeIndex == selectedColumnIndex;
    }
}
