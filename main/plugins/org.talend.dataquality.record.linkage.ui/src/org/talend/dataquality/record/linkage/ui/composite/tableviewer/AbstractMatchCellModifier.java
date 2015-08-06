// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ICellModifier;

/**
 * created by zshen on Sep 13, 2013 Detailled comment
 * 
 * Abstract class for cell Modifier in match key and block key table
 */
public abstract class AbstractMatchCellModifier<T> implements ICellModifier {

    protected AbstractMatchAnalysisTableViewer<T> tableViewer = null;

    protected List<String> columnList = new ArrayList<String>();

    /**
     * Getter for columnList.
     * 
     * @return the columnList
     */
    public List<String> getColumnList() {
        return this.columnList;
    }

    /**
     * Sets the columnList.
     * 
     * @param columnList the columnList to set
     */
    public void setColumnMap(List<String> columnList) {
        if (columnList != null) {
            this.columnList.addAll(columnList);
        }
    }

    /**
     * Getter for tableViewer.
     * 
     * @return the tableViewer
     */
    public AbstractMatchAnalysisTableViewer<T> getTableViewer() {
        return this.tableViewer;
    }

    /**
     * Sets the tableViewer.
     * 
     * @param tableViewer the tableViewer to set
     */
    public void setTableViewer(AbstractMatchAnalysisTableViewer<T> tableViewer) {
        this.tableViewer = tableViewer;
    }

}
