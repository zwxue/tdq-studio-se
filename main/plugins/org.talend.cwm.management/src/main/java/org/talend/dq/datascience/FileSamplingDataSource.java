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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.talend.utils.exceptions.TalendException;

import com.talend.csv.CSVReader;

/**
 * File delimited data.
 *
 */
public class FileSamplingDataSource implements SamplingDataSource<File> {

    private long datasize = 0;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#setDataSource(java.lang.Object)
     */
    public void setDataSource(File ds) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#getDatasource()
     */
    public File getDatasource() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#getDatasize()
     */
    public boolean hasNext() throws TalendException {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#setDatasize(long)
     */
    public void setDatasize(long size) {
        this.datasize = size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#getRecord()
     */
    public Object[] getRecord() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.datascience.SamplingDataSource#setColumnSize(int)
     */
    public void setColumnSize(int columnSize) {
        // TODO Auto-generated method stub

    }

    /**
     * DOC zhao create csv reader given file, field seperator , encoding and row seperator .
     * 
     * @param file
     * @param fieldSeperator field seperator
     * @param encoding file encoding
     * @param rowSep row seperator
     * @param textEnclosure text encolosure
     * @param escapeChar escapse charactor.
     * @return CSVReader instance.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private CSVReader createCsvReader(File file, String fieldSeperator, String encoding, String rowSep, String textEnclosure,
            String escapeChar) throws UnsupportedEncodingException, FileNotFoundException {

        CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file),
                encoding == null ? "UTF-8" : encoding)), fieldSeperator.charAt(0));
        csvReader.setSeparator(rowSep.charAt(0));

        csvReader.setSkipEmptyRecords(true);
        if (textEnclosure != null && textEnclosure.length() > 0) {
            csvReader.setQuoteChar(textEnclosure.charAt(0));
        }
        if (escapeChar != null) {
            csvReader.setEscapeChar(escapeChar.charAt(0));
        }
        return csvReader;
    }
}