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
package org.talend.dq.datascience;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.talend.utils.exceptions.TalendException;

/**
 * created by zhao Sampling datasource regarding jdbc connection.
 *
 */
public class JDBCSamplingDataSource implements SamplingDataSource<ResultSet> {

    private static Logger log = Logger.getLogger(JDBCSamplingDataSource.class);

    private ResultSet jdbcResultSet = null;

    private long datasize = 0;

    private int columnSize = 0;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#setDataSource(java.lang.Object)
     */
    public void setDataSource(ResultSet ds) {
        jdbcResultSet = ds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#getDatasource()
     */
    public ResultSet getDatasource() {
        return jdbcResultSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#getDatasize()
     */
    public boolean hasNext() throws TalendException {
        try {
            return jdbcResultSet.next();
        } catch (SQLException e) {
            throw new TalendException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#setColumnSize(int)
     */
    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#getRecord()
     */
    public Object[] getRecord() throws TalendException {
        try {
            Object[] oneRow = new Object[columnSize];
            // --- for each column
            for (int i = 0; i < columnSize; i++) {
                // --- get content of column
                Object object = null;
                try {
                    oneRow[i] = jdbcResultSet.getObject(i + 1);
                } catch (SQLException e) {
                    if (NULLDATE.equals(jdbcResultSet.getString(i + 1))) {
                        oneRow[i] = null;
                    } else {
                        throw new TalendException(e);
                    }
                }
            }
            return oneRow;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private static final String NULLDATE = "0000-00-00 00:00:00"; //$NON-NLS-1$
}
