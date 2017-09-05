// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;

import com.talend.csv.CSVReader;

/**
 * created by talend on Jan 15, 2015 Detailled comment.
 *
 */
public class FileCSVResultSet extends TalendResultSet {
    
    private static Logger log = Logger.getLogger(FileCSVResultSet.class);

    private CSVReader csvReader = null;

    private List<String> columnLabels = null;


    public FileCSVResultSet(CSVReader csvReader, List<String> columnLabels) {
        this.csvReader = csvReader;
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
                boolean readNext = csvReader.readNext();
                currentIndex++;
                return readNext;
            } else if (this.currentIndex - rowIndex == 1) {
                return true;
            } else if (this.currentIndex < rowIndex) {
                while (csvReader.readNext()) {
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
        return csvReader.get(getColumnIndex(columnName));
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
