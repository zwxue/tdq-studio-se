// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;

/**
 * DOC xqliu class global comment. Detailled comment
 */

public class UpdateTdqIndicatorValueTask extends AbstractMigrationTask {

    private static final String TALEND_DQ = "talend_dq";

    private static final String USER = "user";

    private static final String PASSWORD = "password";

    private Connection connection;

    public Connection getConnection() {
        if (connection == null) {
            setConnection(createConnection());
        }
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private Connection createConnection() {
        Connection conn = null;
        String tdqDBType = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_DBTYPE");
        if (tdqDBType != null && tdqDBType.equalsIgnoreCase(SupportDBUrlType.MYSQLDEFAULTURL.getDBKey())) {
            String url = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_URL") + TALEND_DQ;
            String driverClassName = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_DRIVER");
            String user = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_USER");
            String password = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_PASSWORD");
            Properties props = new Properties();
            props.put(USER, user);
            props.put(PASSWORD, password);
            try {
                conn = ConnectionUtils.createConnection(url, driverClassName, props);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IPath rootPath = root.getLocation();
            String path = rootPath.toOSString() + File.separator + "reporting_db" + File.separator + TALEND_DQ;
            String url = "jdbc:hsqldb:file:" + path + "";
            String driverClassName = "org.hsqldb.jdbcDriver";
            String user = "sa";
            String password = "";
            Properties props = new Properties();
            props.put(USER, user);
            props.put(PASSWORD, password);
            try {
                conn = ConnectionUtils.createConnection(url, driverClassName, props);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public boolean execute() {
        boolean ret = false;
        String updateSql1 = "ALTER TABLE " + TALEND_DQ + ".TDQ_INDICATOR_VALUE ADD INDV_ITHRESH_PERC_OK CHAR(1) NULL";
        String updateSql2 = "ALTER TABLE " + TALEND_DQ + ".TDQ_INDICATOR_VALUE ADD INDV_ITHRESH_PERC_LOW DOUBLE PRECISION NULL";
        String updateSql3 = "ALTER TABLE " + TALEND_DQ + ".TDQ_INDICATOR_VALUE ADD INDV_ITHRESH_PERC_HI DOUBLE PRECISION NULL";
        Connection conn = getConnection();
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                stmt.execute(updateSql1);
                stmt.execute(updateSql2);
                stmt.execute(updateSql3);
                ret = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2009, 2, 2);
        return calender.getTime();
    }
}
