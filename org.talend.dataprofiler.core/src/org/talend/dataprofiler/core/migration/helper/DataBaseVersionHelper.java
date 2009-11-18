// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Preferences;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.dataprofiler.core.PluginChecker;
import org.talend.utils.ProductVersion;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class DataBaseVersionHelper {

    private static Logger log = Logger.getLogger(DataBaseVersionHelper.class);

    private static String dbType = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_DBTYPE"); //$NON-NLS-1$

    private static String url = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_URL"); //$NON-NLS-1$

    private static String dbName = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_DBNAME"); //$NON-NLS-1$

    private static String driver = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_DRIVER"); //$NON-NLS-1$

    private static String user = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_USER"); //$NON-NLS-1$

    private static String pass = ResourcesPlugin.getPlugin().getPluginPreferences().getString("TDQ_PASSWORD"); //$NON-NLS-1$

    private static Properties props = new Properties();

    static {
        props.setProperty("user", user); //$NON-NLS-1$
        props.setProperty("password", pass); //$NON-NLS-1$
    }

    private DataBaseVersionHelper() {

    }

    /**
     * DOC bZhou Comment method "checkConnection".
     * 
     * @return
     */
    public static boolean checkConnection() {

        int index = url.indexOf(dbName);
        String surl = ""; //$NON-NLS-1$
        if (index > 0) {
            surl = url.substring(0, index);
        }

        ReturnCode checkConnection = ConnectionService.checkConnection(surl, driver, props);

        return checkConnection.isOk();
    }

    /**
     * DOC bZhou Comment method "checkDatabase".
     * 
     * @return
     */
    public static boolean checkDatabase() {
        ReturnCode checkConnection = ConnectionService.checkConnection(url + dbName, driver, props);

        return checkConnection.isOk();
    }

    /**
     * DOC bZhou Comment method "storeVersion".
     * 
     * @return
     */
    public static boolean storeVersion() {
        if (PluginChecker.isTDQLoaded()) {

            Preferences resourcePreferences = ResourcesPlugin.getPlugin().getPluginPreferences();

            if (dbType != null && dbType.equals(SupportDBUrlType.MYSQLDEFAULTURL.getDBKey())) {

                if (user != null && pass != null) {
                    Properties props = new Properties();
                    props.setProperty("user", user); //$NON-NLS-1$
                    props.setProperty("password", pass); //$NON-NLS-1$

                    return updateVersionInDB(url + dbName, driver, props);
                }
            }
        }

        return false;
    }

    /**
     * DOC bZhou Comment method "updateVersionInDB".
     * 
     * @param url
     * @param driver
     * @param props
     * @return
     */
    public static boolean updateVersionInDB(String url, String driver, Properties props) {
        Connection connection = null;

        try {
            connection = ConnectionUtils.createConnection(url, driver, props);
            if (connection != null) {
                Statement stat = connection.createStatement();
                ProductVersion curVersion = WorkspaceVersionHelper.getVesion();
                return stat.execute("update TDQ_PRODUCT set PR_VERSION = '" + curVersion + "'"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
    
    /**
     * DOC xqliu Comment method "getVersion".
     * 
     * @return
     */
    public static ProductVersion getVersion() {
        Connection connection = null;

        try {
            connection = ConnectionUtils.createConnection(url, driver, props);
            if (connection != null) {
                Statement stat = connection.createStatement();
                ResultSet result = stat.executeQuery("select PR_VERSION from " + dbName + ".TDQ_PRODUCT");
                result.next();
                String versionStr = result.getString(1);
                return ProductVersion.fromString(versionStr);//$NON-NLS-1$
            }
        } catch (Exception e) {
            log.error(e, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
