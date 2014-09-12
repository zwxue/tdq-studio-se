// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package net.sourceforge.sqlexplorer.dataset.mapdb;

import net.sourceforge.sqlexplorer.dataset.DataSet;

import org.talend.commons.MapDB.utils.DataValidation;

/**
 * created by talend on Sep 2, 2014 Detailled comment
 * 
 */
public class TalendDataSet extends DataSet {

    /**
     * DOC talend TalendDataSet constructor comment.
     * 
     * @param columnLabels
     * @param data
     */
    protected long startIndex = 0;

    protected long endIndex = 0;

    protected String[] columnHeads = null;

    protected DataValidation dataValidator = null;

    public TalendDataSet(String[] columnLabels, Comparable[][] data, int pageSize) {
        super(columnLabels, data);
        columnHeads = columnLabels;
        endIndex = pageSize;

    }

    public DataSet getCurrentPageDataSet() {
        return null;
    }

    /**
     * Sets the startIndex.
     * 
     * @param startIndex the startIndex to set
     */
    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Sets the endIndex.
     * 
     * @param endIndex the endIndex to set
     */
    public void setEndIndex(long endIndex) {
        this.endIndex = endIndex;
    }

}
