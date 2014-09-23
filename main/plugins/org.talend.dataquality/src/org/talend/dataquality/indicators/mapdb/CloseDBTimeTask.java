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

import java.util.TimerTask;

import org.mapdb.DB;

/**
 * created by talend on Sep 18, 2014 Detailled comment
 * 
 */
public class CloseDBTimeTask extends TimerTask {

    private DB db = null;

    public CloseDBTimeTask(DB closeDb) {
        this.db = closeDb;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        if (db != null && !db.isClosed()) {
            db.close();
        }
    }

}
