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
package org.talend.dataquality.indicators.mapdb;


/**
 * created by talend on Aug 28, 2014 Detailled comment
 * 
 */
public interface MapDBDrillDown {

    /**
     * 
     * Get MapDB by dbName
     * 
     * @param dbName The name of DB(note that it is not the name of db file, one db file can contains many dbs and every
     * db have a name)
     * 
     * @return null when MapDB can not be used by current indicator
     * 
     */
    @SuppressWarnings("rawtypes")
    public AbstractDB getMapDB(String dbName);

    /**
     * DOC talend Comment method "handleDrillDownData".
     * 
     * @param masterObject
     * @param currentObject
     * @param columnCount
     * @param currentIndex
     * @param currentColumnName
     */
    void handleDrillDownData(Object masterObject, Object currentObject, int columnCount, int currentIndex,
            String currentColumnName);

}
