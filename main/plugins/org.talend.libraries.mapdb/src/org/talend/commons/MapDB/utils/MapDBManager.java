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
import java.util.Timer;

import org.mapdb.DB;

/**
 * created by talend on Aug 19, 2014 Detailled comment
 * 
 */
public class MapDBManager {

    /**
     * close db after 5 minute (5 * 60 * 1000).
     */
    private static final int CLOSE_TIME_DELAY = 300000;

    private Map<File, DB> dbMap = new HashMap<File, DB>();

    // store the times the mapdb has been used.
    private Map<DB, Integer> dbRefCountMap = new HashMap<DB, Integer>();

    // store all of task which used to close db
    private Map<DB, CloseDBTimeTask> closeTaskMap = new HashMap<DB, CloseDBTimeTask>();

    private Timer timer = new Timer();

    private static MapDBManager instance = null;

    public static MapDBManager getInstance() {
        if (instance == null) {
            instance = new MapDBManager();
        }
        return instance;
    }

    public DB getDB(File filePath) {
        return dbMap.get(filePath);
    }

    public DB putDB(File filePath, DB db) {
        return dbMap.put(filePath, db);
    }

    public void removeDB(File filePath) {
        dbMap.remove(filePath);
    }

    public void closeDB(File filePath) {
        DB db = dbMap.get(filePath);
        if (db == null) {
            return;
        }
        removeDB(filePath);
        scheduleCloseTask(db);
    }

    public void closeDB(String parentPathStr, String fileName) {
        File filePath = createPath(parentPathStr, fileName);
        closeDB(filePath);
    }

    /**
     * DOC talend Comment method "scheduleCloseTask".
     * 
     * @param db
     */
    protected void scheduleCloseTask(DB db) {
        CloseDBTimeTask closeDBTimeTask = new CloseDBTimeTask(db);
        timer.schedule(closeDBTimeTask, CLOSE_TIME_DELAY);// close db after 5 minute 5*60*1000
        closeTaskMap.put(db, closeDBTimeTask);
    }

    private void closeDB(DB db) {
        if (db == null) {
            return;
        }

        scheduleCloseTask(db);
    }

    /**
     * 
     * Record one db have been used how many times
     * 
     * @param filePath
     */
    public void addDBRef(File filePath) {
        DB db = dbMap.get(filePath);
        if (db == null) {
            return;
        }
        cancelCloseTask(db);

        Integer refConut = dbRefCountMap.get(db);
        if (refConut == null) {
            refConut = 0;
        }
        refConut++;
        dbRefCountMap.put(db, refConut);
    }

    /**
     * DOC talend Comment method "cancelCloseTask".
     * 
     * @param db
     */
    protected void cancelCloseTask(DB db) {
        CloseDBTimeTask closeDBTimeTask = closeTaskMap.get(db);
        if (closeDBTimeTask != null) {
            closeDBTimeTask.cancel();
            timer.purge();
            closeTaskMap.remove(closeDBTimeTask);
        }
    }

    /**
     * 
     * Reduce the times which one db be used. When the count of times is 0 we will close this db.
     * 
     * @param filePath
     */
    public void removeDBRef(File filePath) {
        DB db = dbMap.get(filePath);
        if (db == null) {
            return;
        }
        Integer refConut = dbRefCountMap.get(db);
        if (refConut == null) {
            return;
        }
        refConut--;
        if (refConut > 0) {
            dbRefCountMap.put(db, refConut);
        } else {
            dbRefCountMap.remove(db);
            dbMap.remove(filePath);
            closeDB(db);
        }
    }

    /**
     * Make sure the path of DB file is valid
     * 
     * @param parentPathStr the Path of parent folder
     * @param fileName The name of db File
     * @return The file of special path
     */
    public static File createPath(String parentPathStr, String fileName) {
        File parentFolder = new File(parentPathStr);
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }
        File file = new File(parentFolder.getPath() + File.separator + fileName);
        return file;
    }
}
