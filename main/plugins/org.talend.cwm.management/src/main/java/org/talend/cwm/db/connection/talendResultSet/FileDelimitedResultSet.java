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
package org.talend.cwm.db.connection.talendResultSet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.fileprocess.FileInputDelimited;

/**
 * created by talend on Jan 15, 2015 Detailled comment.
 *
 */
public class FileDelimitedResultSet extends TalendResultSet {
    
    private static Logger log = Logger.getLogger(FileCSVResultSet.class);

    private FileInputDelimited fileInputDelimited = null;

    private List<String> columnLabels = null;


    public FileDelimitedResultSet(FileInputDelimited fileInputDelimited, List<String> columnLabels) {
        this.fileInputDelimited = fileInputDelimited;
        this.columnLabels = columnLabels;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.talendResultSet.TalendResultSet#absolute(int)
     */
    @Override
    public boolean absolute(int rowIndex) {
        try {
            if (this.currentIndex == rowIndex) {
                boolean readNext = fileInputDelimited.nextRecord();
                currentIndex++;
                return readNext;
            } else if (this.currentIndex - rowIndex == 1) {
                return true;
            } else if (this.currentIndex < rowIndex) {
                while (fileInputDelimited.nextRecord()) {
                    currentIndex++;
                    if (this.currentIndex == rowIndex) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            log.error(e, e);
            return false;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.talendResultSet.TalendResultSet#getObject(java.lang.String)
     */
    @Override
    public Object getObject(String columnName) throws SQLException {
        try {
            return fileInputDelimited.get(getColumnIndex(columnName));
        } catch (IOException e) {
            log.error(e, e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * DOC talend Comment method "getColumnIndex".
     * 
     * @param columnName
     * @return
     */
    private int getColumnIndex(String columnName) {
        if (columnLabels == null) {
            return -1;
        }
        return columnLabels.indexOf(columnName);
    }
}
