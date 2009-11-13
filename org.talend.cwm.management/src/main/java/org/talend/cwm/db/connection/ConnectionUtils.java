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
package org.talend.cwm.db.connection;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.util.MyURLClassLoader;

import org.apache.log4j.Logger;
import org.talend.commons.utils.database.DB2ForZosDataBaseMetadata;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dq.CWMPlugin;
import org.talend.dq.PluginConstant;
import org.talend.utils.sugars.ReturnCode;

/**
 * Utility class for database connection handling.
 */
public final class ConnectionUtils {

	private static Logger log = Logger.getLogger(ConnectionUtils.class);

	// MOD xqliu 2009-02-02 bug 5261
	public static final int LOGIN_TEMEOUT_MILLISECOND = 20000;

	public static final int LOGIN_TIMEOUT_SECOND = 20;

	private static boolean timeout = CWMPlugin.getDefault()
			.getPluginPreferences().getBoolean(
					PluginConstant.CONNECTION_TIMEOUT);

	// MOD mzhao 2009-06-05 Bug 7571
	private static final Map<String, Driver> DRIVER_CACHE = new HashMap<String, Driver>();

	public static boolean isTimeout() {
		return timeout;
	}

	public static void setTimeout(boolean timeout) {
		ConnectionUtils.timeout = timeout;
	}

	/**
	 * The query to execute in order to verify the connection.
	 */
	// private static final String PING_SELECT = "SELECT 1";
	/**
	 * private constructor.
	 */
	private ConnectionUtils() {
	}

	/**
	 * Method "createConnection".
	 * 
	 * @param url
	 *            the database url
	 * @param driverClassName
	 *            the Driver classname
	 * @param props
	 *            properties passed to the driver manager for getting the
	 *            connection (normally at least a "user" and "password" property
	 *            should be included)
	 * @return the connection
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Connection createConnection(String url,
			String driverClassName, Properties props) throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Driver driver = getClassDriver(driverClassName);
		if (driver != null) {
			DriverManager.registerDriver(driver);
			if (log.isDebugEnabled()) {
				log
						.debug("SQL driver found and registered: "
								+ driverClassName);
				log.debug("Enumerating all drivers:");
				Enumeration<Driver> drivers = DriverManager.getDrivers();
				while (drivers.hasMoreElements()) {
					log.debug(drivers.nextElement());
				}
			}
			Connection connection = null;
			if (driverClassName.equals("org.hsqldb.jdbcDriver")) { //$NON-NLS-1$getClassDriver 
				// MOD mzhao 2009-04-13, Try to load driver first as there will
				// cause exception: No suitable driver
				// found... if not load.
				try {
					Class.forName("org.hsqldb.jdbcDriver");
				} catch (ClassNotFoundException e) {
					log.error(e, e);
				}
				// MOD xqliu 2009-02-02 bug 5261
				if (isTimeout()) {
					DriverManager.setLoginTimeout(LOGIN_TIMEOUT_SECOND);
				}
				connection = DriverManager.getConnection(url, props);
			} else {
				// MOD xqliu 2009-02-02 bug 5261
				connection = createConnectionWithTimeout(driver, url, props);
			}

			return connection;
		}
		return null;

	}

	/**
	 * 
	 * DOC xqliu Comment method "createConnectionWithTimeout".
	 * 
	 * @param driver
	 * @param url
	 * @param props
	 * @return
	 * @throws SQLException
	 */
	public synchronized static Connection createConnectionWithTimeout(
			Driver driver, String url, Properties props) throws SQLException {
		Connection ret = null;
		if (isTimeout()) {
			ConnectionCreator cc = new ConnectionCreator(driver, url, props);
			new Thread(cc).start();
			long begin = System.currentTimeMillis();
			boolean isTimeout = false;
			boolean isOK = false;
			boolean isException = false;
			while (true) {
				if (cc.getConnection() != null) {
					isOK = true;
					ret = cc.getConnection();
					break;
				}
				if (cc.getExecption() != null) {
					isException = true;
					break;
				}
				if (System.currentTimeMillis() - begin > LOGIN_TEMEOUT_MILLISECOND) {
					isTimeout = true;
					break;
				}
			}
			if (isTimeout) {
				cc = null;
				throw new SQLException(Messages
						.getString("ConnectionUtils.ConnectionTimeout")); //$NON-NLS-1$
			}
			if (isException) {
				SQLException e = cc.getExecption();
				cc = null;
				throw e;
			}
			if (isOK) {
				ret = cc.getConnection();
				cc = null;
			}
		} else {
			ret = driver.connect(url, props);
		}
		return ret;
	}

	/**
	 * DOC qzhang Comment method "getClassDriver".
	 * 
	 * @param driverClassName
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static Driver getClassDriver(String driverClassName)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		// MOD mzhao 2009-06-05,Bug 7571 Get driver from catch first, if not
		// exist then get a new instance.
		Driver driver = DRIVER_CACHE.get(driverClassName);
		if (driver != null) {
			return driver;
		}

		SQLExplorerPlugin sqlExplorerPlugin = SQLExplorerPlugin.getDefault();
		if (sqlExplorerPlugin != null) {
			net.sourceforge.sqlexplorer.dbproduct.DriverManager driverModel = sqlExplorerPlugin
					.getDriverModel();
			try {
				Collection<ManagedDriver> drivers = driverModel.getDrivers();
				for (ManagedDriver managedDriver : drivers) {
					LinkedList<String> jars = managedDriver.getJars();
					List<URL> urls = new ArrayList<URL>();
					for (int i = 0; i < jars.size(); i++) {
						File file = new File(jars.get(i));
						if (file.exists()) {
							urls.add(file.toURL());
						}
					}
					if (!urls.isEmpty()) {
						try {
							MyURLClassLoader cl;
							cl = new MyURLClassLoader(urls.toArray(new URL[0]));
							Class clazz = cl.findClass(driverClassName);
							if (clazz != null) {
								driver = (Driver) clazz.newInstance();
								// MOD mzhao 2009-06-05,Bug 7571 Get driver from
								// catch first, if not
								// exist then get a new instance.
								DRIVER_CACHE.put(driverClassName, driver);
								return driver; // driver is found
							}
						} catch (ClassNotFoundException e) {
							// do nothings
						}
					}

				}
			} catch (MalformedURLException e) {
				// do nothings
			}
		}
		if (driver == null) {
			driver = (Driver) Class.forName(driverClassName).newInstance();
		}
		// MOD mzhao 2009-06-05,Bug 7571 Get driver from catch first, if not
		// exist then get a new instance.
		DRIVER_CACHE.put(driverClassName, driver);
		return driver;
	}

	/**
	 * Method "isValid".
	 * 
	 * @param connection
	 *            the connection to test
	 * @return a return code with the appropriate message (never null)
	 */
	public static ReturnCode isValid(final Connection connection) {
		return org.talend.utils.sql.ConnectionUtils.isValid(connection);
	}

	/**
	 * Method "closeConnection".
	 * 
	 * @param connection
	 *            the connection to close.
	 * @return a ReturnCode with true if ok, false if problem.
	 *         {@link ReturnCode#getMessage()} gives the error message when
	 *         there is a problem.
	 */
	public static ReturnCode closeConnection(final Connection connection) {
		return org.talend.utils.sql.ConnectionUtils.closeConnection(connection);
	}
	/**
     * DOC xqliu Comment method "getConnectionMetadata". 2009-07-13 bug 7888.
     * 
     * @param conn
     * @return
     * @throws SQLException
     */

    public static DatabaseMetaData getConnectionMetadata(Connection conn) throws SQLException {
        DatabaseMetaData dbMetaData = conn.getMetaData();
        if (dbMetaData == null || dbMetaData.getConnection() == null
                || dbMetaData.getConnection() != conn) {
            dbMetaData = createFakeDatabaseMetaData(conn);
        }
        return dbMetaData;
    }

    /**
     * only for db2 on z/os right now. 2009-07-13 bug 7888.
     * 
     * @param conn2
     * @return
     */
    private static DatabaseMetaData createFakeDatabaseMetaData(Connection conn) {
        DB2ForZosDataBaseMetadata dmd = new DB2ForZosDataBaseMetadata(conn);
        return dmd;
    }

    // ADD xqliu 2009-11-09 bug 9403
    private static final String DEFAULT_TABLE_NAME = "TDQ_PRODUCT";

    /**
     * DOC xqliu Comment method "existTable".
     * 
     * @param url
     * @param driver
     * @param props
     * @param tableName
     * @return
     */
    public static boolean existTable(String url, String driver, Properties props, String tableName) {
        Connection connection = null;
        if (tableName == null || "".equals(tableName.trim())) {
            tableName = DEFAULT_TABLE_NAME;
        }
        try {
            connection = ConnectionUtils.createConnection(url, driver, props);
            if (connection != null) {
                Statement stat = connection.createStatement();
                stat.executeQuery("Select * from " + tableName);
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.warn(e);
                }
            }
        }
        return true;
    }
    // ~
}
