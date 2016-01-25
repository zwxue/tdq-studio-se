// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.db.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;

/**
 * created by zhao on Oct 20, 2013 Detailled comment
 * 
 */
public abstract class SQLExecutor implements ISQLExecutor {

    private static Logger log = Logger.getLogger(SQLExecutor.class);

    protected Boolean storeOnDisk = Boolean.FALSE;

    protected StoreOnDiskHandler storeOnDiskHandler = null;

    public static final String STORE_ON_DISK_KEY = "STORE_ON_DISK"; //$NON-NLS-1$

    public static final String MAX_BUFFER_SIZE = "MAX_BUFFER_SIZE";//$NON-NLS-1$

    public static final String TEMP_DATA_DIR = "TEMP_DATA_DIR";//$NON-NLS-1$

    /**
     * List of rows obtained from data source.
     */
    protected List<Object[]> dataFromTable = new ArrayList<Object[]>();

    public void initStoreOnDiskHandler(Analysis analysis, RecordMatchingIndicator recordMatchingIndicator,
            Map<MetadataColumn, String> columnMap) {
        if (storeOnDisk) {
            try {
                String tempDataPath = TaggedValueHelper.getValueString(TEMP_DATA_DIR, analysis);
                int bufferSize = Integer.valueOf(TaggedValueHelper.getValueString(MAX_BUFFER_SIZE, analysis));
                storeOnDiskHandler = new StoreOnDiskHandler(recordMatchingIndicator, columnMap, tempDataPath, bufferSize);

            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * The client can override the method to prepare an environment before query.
     */
    protected void beginQuery() throws Exception {
        if (storeOnDisk) {
            storeOnDiskHandler.beginQuery();
        }
    }

    /**
     * mzhao finalize the query, the client can override this method.
     */
    protected void endQuery() throws Exception {
        if (storeOnDisk) {
            storeOnDiskHandler.endQuery();
        }
    }

    protected void handleRow(Object[] oneRow) throws Exception {
        if (storeOnDisk) {
            storeOnDiskHandler.handleRow(oneRow);
        } else {
            dataFromTable.add(oneRow);
        }
    }

    /**
     * Sets the storeOnDisk.
     * 
     * @param storeOnDisk the storeOnDisk to set
     */
    public void setStoreOnDisk(Boolean storeOnDisk) {
        this.storeOnDisk = storeOnDisk;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.ISQLExecutor#getStoreOnDisk()
     */
    public Boolean getStoreOnDisk() {
        return storeOnDisk;
    }

    /**
     * Getter for storeOnDiskHandler.
     * 
     * @return the storeOnDiskHandler
     */
    public StoreOnDiskHandler getStoreOnDiskHandler() {
        return this.storeOnDiskHandler;
    }
}
