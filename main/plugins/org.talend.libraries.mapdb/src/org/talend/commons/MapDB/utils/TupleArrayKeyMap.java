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
package org.talend.commons.MapDB.utils;

import org.mapdb.BTreeMap;
import org.mapdb.Serializer;

/**
 * created by talend on Jul 15, 2014 Detailled comment
 * 
 */
public class TupleArrayKeyMap extends DBMap<TupleArray, Long> {

    public TupleArrayKeyMap() {
        super();
    }

    public TupleArrayKeyMap(String parentFullPathStr, String fileName) {
        super(parentFullPathStr, fileName);
    }

    public TupleArrayKeyMap(String parentFullPathStr, String fileName, String mapName) {
        super(parentFullPathStr, fileName, mapName);
    }

    public TupleArrayKeyMap(DBMapParameter parameter) {
        if (parameter == null) {
            initDefaultDB();
        } else {
            initDBByDBMapParameter(parameter);
        }
        initMap();
    }

    @Override
    protected void initMap() {
        dbMap = getDB()
                .createTreeMap("testTupleArrayKeyMap").keySerializer(new TupleArrayKeySerializer(BTreeMap.COMPARABLE_COMPARATOR, BTreeMap.COMPARABLE_COMPARATOR, getDB().getDefaultSerializer(), getDB().getDefaultSerializer())).valueSerializer(Serializer.JAVA).make();//$NON-NLS-1$

    }

    @Override
    protected void initMap(String theMapName) {
        mapName = theMapName;
        if (getDB().exists(theMapName)) {
            dbMap = getDB().getTreeMap(theMapName);
        } else {
            // dbMap = getDB()
            // .createTreeMap(mapName)
            // .keySerializer(
            // new TupleArrayKeySerializer(BTreeMap.COMPARABLE_COMPARATOR, BTreeMap.COMPARABLE_COMPARATOR, getDB()
            // .getDefaultSerializer(), getDB().getDefaultSerializer()))
            // .valueSerializer(talendSerializerBase).make();
            dbMap = getDB().createTreeMap(mapName).keySerializer(talendBasicKeySerializer).valueSerializer(talendSerializerBase)
                    .make();
        }
    }

    public boolean containsKey(String key) {
        TupleArray tupleArray = null;
        if (key != null) {
            tupleArray = new TupleArray(1, new String[] { key });
        }
        return dbMap.containsKey(tupleArray);
    }

    public Long get(String key) {
        TupleArray tupleArray = null;
        if (key != null) {
            tupleArray = new TupleArray(1, new String[] { key });
        }
        return dbMap.get(tupleArray);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractMap#put(java.lang.Object, java.lang.Object)
     */
    public Long put(String key, Long value) {
        TupleArray tupleArray = new TupleArray(1, new String[] { key });
        return dbMap.put(tupleArray, value);
    }

    public Long remove(String key) {
        TupleArray tupleArray = null;
        if (key != null) {
            tupleArray = new TupleArray(1, new String[] { key });
        }

        return dbMap.remove(tupleArray);
    }

}
