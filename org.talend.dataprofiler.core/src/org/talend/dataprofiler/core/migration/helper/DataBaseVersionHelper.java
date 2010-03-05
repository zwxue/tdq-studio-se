// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginChecker;
import org.talend.utils.ProductVersion;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class DataBaseVersionHelper {

    private static Logger log = Logger.getLogger(DataBaseVersionHelper.class);

    private static Preferences pluginPreferences = ResourcesPlugin.getPlugin().getPluginPreferences();

    private DataBaseVersionHelper() {

    }

    /**
     * Getter for dbType.
     * 
     * @return the dbType
     */
    public static String getDbType() {
        return pluginPreferences.getString("TDQ_DBTYPE");
    }

    /**
     * Getter for url.
     * 
     * @return the url
     */
    public static String getUrl() {
        return pluginPreferences.getString("TDQ_URL");
    }

    /**
     * Getter for dbName.
     * 
     * @return the dbName
     */
    public static String getDbName() {
        return pluginPreferences.getString("TDQ_DBNAME");
    }

    /**
     * Getter for driver.
     * 
     * @return the driver
     */
    public static String getDriver() {
        return pluginPreferences.getString("TDQ_DRIVER");
    }

    /**
     * Getter for pass.
     * 
     * @return the pass
     */
    public static String getPass() {
        return pluginPreferences.getString("TDQ_PASSWORD");
    }

    /**
     * Getter for user.
     * 
     * @return the user
     */
    public static String getUser() {
        return pluginPreferences.getString("TDQ_USER");
    }

    /**
     * Getter for props.
     * 
     * @return the props
     */
    public static Properties getProps() {
        Properties props = new Properties();
        props.setProperty("user", getUser());
        props.setProperty("password", getPass());
        return props;
    }

    /**
     * DOC bZhou Comment method "checkConnection".
     * 
     * @return
     */
    public static boolean checkConnection() {

        int index = getUrl().indexOf(getDbName());
        String surl = ""; //$NON-NLS-1$
        if (index > 0) {
            surl = getUrl().substring(0, index);
        }

        ReturnCode checkConnection = ConnectionService.checkConnection(surl, getDriver(), getProps());

        return checkConnection.isOk();
    }

    /**
     * DOC bZhou Comment method "checkDatabase".
     * 
     * @return
     */
    public static boolean checkDatabase() {
        ReturnCode checkConnection = ConnectionService.checkConnection(getUrl() + getDbName(), getDriver(), getProps());

        return checkConnection.isOk();
    }

    /**
     * DOC bZhou Comment method "storeVersion".
     * 
     * @return
     */
    public static boolean storeVersion() {
        if (PluginChecker.isTDQLoaded()) {

            if (getDbType() != null && getDbType().equals(SupportDBUrlType.MYSQLDEFAULTURL.getDBKey())) {

                return updateVersionInDB(getUrl() + getDbName(), getDriver(), getProps());
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

        ProductVersion curVersion = CorePlugin.getDefault().getProductVersion();
        String sql = "update TDQ_PRODUCT set PR_VERSION = '" + curVersion + "'";

        try {
            connection = ConnectionUtils.createConnection(url, driver, props);
            if (connection != null) {
                Statement stat = connection.createStatement();
                return stat.executeUpdate(sql) > 0;
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
        return getVersion(getUrl(), getDriver(), getProps());
    }

    /**
     * DOC bZhou Comment method "getVersion".
     * 
     * @param url
     * @param driver
     * @param props
     * @return
     */
    public static ProductVersion getVersion(String url, String driver, Properties props) {
        Connection connection = null;

        try {
            connection = ConnectionUtils.createConnection(url, driver, props);
            if (connection != null) {
                Statement stat = connection.createStatement();
                ResultSet result = stat.executeQuery("select PR_VERSION from " + getDbName() + ".TDQ_PRODUCT");
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
