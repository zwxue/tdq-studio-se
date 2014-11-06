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
 * created by talend on Oct 20, 2014 Detailled comment
 * 
 */
public class MapDBFactorImpl implements IMapDBFactory {

    private static IMapDBFactory instance = null;

    MapDBFactorImpl() {

    }

    public static IMapDBFactory getInstance() {
        if (instance == null) {
            instance = new MapDBFactorImpl();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.mapdb.IMapDBFactory#createDBMap(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public <K, V> DBMap<K, V> createDBMap(String parentFullPathStr, String fileName, String mapName) {
        return new DBMap<K, V>(parentFullPathStr, fileName, mapName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.mapdb.IMapDBFactory#createColumnSetDBMap(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ColumnSetDBMap createColumnSetDBMap(String parentFullPathStr, String fileName, String mapName) {
        return new ColumnSetDBMap(parentFullPathStr, fileName, mapName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.mapdb.IMapDBFactory#createDBValueListMap(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public <K> DBValueListMap<K> createDBValueListMap(String parentFullPathStr, String fileName, String mapName) {
        return new DBValueListMap<K>(parentFullPathStr, fileName, mapName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.mapdb.IMapDBFactory#createDBSet()
     */
    @Override
    public <K> DBSet<K> createDBSet(String parentFullPathStr, String fileName, String setName) {
        return new DBSet<K>(parentFullPathStr, fileName, setName);
    }
}
