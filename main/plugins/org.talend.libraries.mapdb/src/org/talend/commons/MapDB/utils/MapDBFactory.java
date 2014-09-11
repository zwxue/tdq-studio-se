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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.mapdb.DB;

/**
 * created by talend on Aug 19, 2014 Detailled comment
 * 
 */
public class MapDBFactory {

    Map<File, DB> dbMap = new HashMap<File, DB>();

    private static MapDBFactory instance = null;

    public static MapDBFactory getInstance() {
        if (instance == null) {
            instance = new MapDBFactory();
        }
        return instance;
    }

    public DB getDB(File filePath) {
        return dbMap.get(filePath);
    }

    public DB putDB(File filePath, DB db) {
        return dbMap.put(filePath, db);
    }

}
