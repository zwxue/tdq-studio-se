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
package org.talend.cwm.db.connection.datasource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.dq.helper.FileUtils;

import com.talend.csv.CSVReader;

/**
 * File delimited data.
 *
 */
public class FileSamplingDataSource extends AbstractSamplingDataSource<DelimitedFileConnection> {

    private static Logger log = Logger.getLogger(FileSamplingDataSource.class);

    private CSVReader csvReader = null;

    private DelimitedFileConnection fileConnection = null;

    private String fieldSeperator = ","; //$NON-NLS-1$

    private String encoding = "UTF-8"; //$NON-NLS-1$

    private String rowSep = "\\n"; //$NON-NLS-1$

    private String textEnclosure = "\""; //$NON-NLS-1$

    private String escapeChar = "\\"; //$NON-NLS-1$

    private Map<Integer, String> columnIndexMap = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#setDataSource(java.lang.Object)
     */
    public void setDataSource(DelimitedFileConnection conn) {
        fileConnection = conn;
    }

    public void setFieldSeperator(String sep) {
        this.fieldSeperator = sep;
    }

    /**
     * Sets the rowSep.
     * 
     * @param rowSep the rowSep to set
     */
    public void setRowSep(String rowSep) {
        this.rowSep = rowSep;
    }

    /**
     * Sets the textEnclosure.
     * 
     * @param textEnclosure the textEnclosure to set
     */
    public void setTextEnclosure(String textEnclosure) {
        this.textEnclosure = textEnclosure;
    }

    /**
     * Sets the escapeChar.
     * 
     * @param escapeChar the escapeChar to set
     */
    public void setEscapeChar(String escapeChar) {
        this.escapeChar = escapeChar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#getDatasize()
     */
    public boolean hasNext() throws Exception {
        boolean hasNext = false;
        try {
            if (csvReader == null) {
                return false;
            }
            hasNext = csvReader.readNext();
            if (!hasNext) {
                csvReader.close();
                csvReader = null;
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new Exception(e);
        }
        return hasNext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#getRecord()
     */
    public Object[] getRecord() {
        Object[] oneRow = new Object[columnSize];

        // --- for each column
        try {
            for (int i = 0; i < columnSize; i++) {
                // --- get content of column
                String headerName = columnIndexMap.get(i);
                oneRow[i] = csvReader.get(headerName);
            }
        } catch (IOException e) {
            log.error(e, e);
        }
        return oneRow;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.sampling.SamplingDataSource#finalizeDataSampling()
     */
    public boolean finalizeDataSampling() throws Exception {
        if (csvReader != null) {
            try {
                csvReader.close();
            } catch (IOException e) {
                throw new Exception(e);
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.datasource.AbstractSamplingDataSource#getColumnHeader()
     */
    public void initColumnHeader(String[] columnHeaders) {

        if (fileConnection != null) {
            try {
                int headValue = JavaSqlFactory.getHeadValue(fileConnection);
                csvReader = FileUtils.createCsvReader(fileConnection);
                FileUtils.initializeCsvReader(fileConnection, csvReader);
                if (headValue != 0) {
                    csvReader.readHeaders();
                }
                this.columnIndexMap = new HashMap<Integer, String>();
                for (int index = 0; index < columnHeaders.length; index++) {
                    this.columnIndexMap.put(index, columnHeaders[index]);
                }
            } catch (IOException e) {
                log.error(e, e);
            }
        }
    }
}
