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
package org.talend.cwm.db.connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ILibraryManagerService;
import org.talend.core.IRepositoryContextService;
import org.talend.core.database.EDatabase4DriverClassName;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.database.conn.version.EDatabaseVersion4Drivers;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.MetadataFillFactory;
import org.talend.core.model.metadata.builder.ConvertionHelper;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.FileConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.database.ExtractMetaDataFromDataBase;
import org.talend.core.model.metadata.builder.database.ExtractMetaDataUtils;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.core.model.metadata.builder.database.XMLSchemaBuilder;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.core.model.metadata.builder.util.DatabaseConstant;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.metadata.connection.hive.HiveConnVersionInfo;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.MDMConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.utils.CloneConnectionUtils;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.cwm.xml.TdXmlContent;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.CWMPlugin;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.SqlExplorerUtils;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.metadata.managment.connection.manager.HiveConnectionManager;
import org.talend.repository.ui.utils.DBConnectionContextUtils;
import org.talend.repository.ui.utils.ManagerConnection;
import org.talend.utils.ProductVersion;
import org.talend.utils.sql.metadata.constants.GetColumn;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.foundation.softwaredeployment.ProviderConnection;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.objectmodel.core.TaggedValue;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * Utility class for database connection handling.
 */
public final class ConnectionUtils {

    private static Logger log = Logger.getLogger(ConnectionUtils.class);

    // MOD xqliu 2009-02-02 bug 5261
    public static final int LOGIN_TEMEOUT_MILLISECOND = 20000;

    public static final int LOGIN_TIMEOUT_SECOND = 20;

    private static Boolean timeout = null;

    // MOD mzhao 2009-06-05 Bug 7571
    // private static final Map<String, Driver> DRIVER_CACHE = new HashMap<String, Driver>();

    public static boolean isTimeout() {
        if (timeout == null) {
            IPreferencesService service = Platform.getPreferencesService();
            if (service == null) {
                timeout = true;
            } else {
                timeout = service.getBoolean(CWMPlugin.getDefault().getBundle().getSymbolicName(),
                        PluginConstant.CONNECTION_TIMEOUT, false, null);
            }
        }
        return timeout;
    }

    public static void setTimeout(boolean timeout) {
        ConnectionUtils.timeout = timeout;
    }

    /**
     * private constructor.
     */
    private ConnectionUtils() {
    }

    /**
     * Method "createConnection".
     * 
     * @param url the database url
     * @param driverClassName the Driver classname
     * @param props properties passed to the driver manager for getting the connection (normally at least a "user" and
     * "password" property should be included)
     * @return the connection
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws IOException
     * @deprecated @{@link ExtractMetaDataUtils#connect(String, String, String, String, String, String, String, String)}
     */
    @Deprecated
    public static java.sql.Connection createConnection(String url, String driverClassName, Properties props) throws SQLException,
            InstantiationException, IllegalAccessException, ClassNotFoundException {
        Driver driver = getClassDriverFromSQLExplorer(driverClassName, props);

        if (driver != null) {
            DriverManager.registerDriver(driver);
            if (log.isDebugEnabled()) {
                log.debug("SQL driver found and registered: " + driverClassName);//$NON-NLS-1$
                log.debug("Enumerating all drivers:");//$NON-NLS-1$
                Enumeration<Driver> drivers = DriverManager.getDrivers();
                while (drivers.hasMoreElements()) {
                    log.debug(drivers.nextElement());
                }
            }
            java.sql.Connection connection = null;
            if (driverClassName.equals(EDatabase4DriverClassName.HSQLDB.getDriverClass())) {
                // getClassDriver
                // MOD mzhao 2009-04-13, Try to load driver first as there will
                // cause exception: No suitable driver
                // found... if not load.
                try {
                    Class.forName(EDatabase4DriverClassName.HSQLDB.getDriverClass());
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
     * check whether the connection is available.
     * 
     * @param datamanager
     * @return boolean
     */
    public static boolean checkConnection(DataManager datamanager) {
        Connection analysisDataProvider = ConnectionUtils.getConnectionFromDatamanager(datamanager);
        ReturnCode connectionAvailable = new ReturnCode(false);
        connectionAvailable = ConnectionUtils.isConnectionAvailable(analysisDataProvider);
        if (!connectionAvailable.isOk()) {
            log.error(connectionAvailable.getMessage());
            MessageDialogWithToggle.openWarning(Display.getCurrent().getActiveShell(),
                    Messages.getString("ConnectionUtils.checkConnFailTitle"),//$NON-NLS-1$
                    Messages.getString("ConnectionUtils.checkConnFailMsg"));//$NON-NLS-1$
            return false;
        }
        return true;
    }

    /**
     * Method "checkConnection".
     * 
     * @param url the database url
     * @param driverClassName the driver class name to use for connection
     * @param props the properties of the connection
     * @return a return code with a message (not null when error)
     * @deprecated use {@link ManagerConnection#check(IMetadataConnection, boolean...)} instead.
     */
    @Deprecated
    public static ReturnCode checkConnection(String url, String driverClassName, Properties props) {
        ReturnCode rc = new ReturnCode();

        java.sql.Connection connection = null;
        try {
            connection = ConnectionUtils.createConnection(url, driverClassName, props);
            rc = (ConnectionUtils.isValid(connection));
            // ADD xqliu 2012-01-05 TDQ-4162
            String dbType = props.getProperty(TaggedValueHelper.DBTYPE);
            String dbName = props.getProperty(TaggedValueHelper.DBNAME);
            if (!StringUtils.isBlank(dbType) && !StringUtils.isBlank(dbName)) {
                boolean checkSchemaOK = ExtractMetaDataFromDataBase.checkSchemaConnection(dbName, connection, true, dbType);
                if (!checkSchemaOK) {
                    rc.setReturnCode(Messages.getString("ConnectionUtils.SchemaNotFound"), false); //$NON-NLS-1$
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            rc.setReturnCode(Messages.getString("ConnectionUtils.SQLException", e.getMessage(), url), false); //$NON-NLS-1$
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
            rc.setReturnCode(e.getMessage(), false);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            rc.setReturnCode(e.getMessage(), false);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            rc.setReturnCode(Messages.getString("ConnectionService.DriverNotFound", e.getMessage()), false); //$NON-NLS-1$
        } finally {
            if (connection != null) {
                ReturnCode closed = ConnectionUtils.closeConnection(connection);
                if (!closed.isOk()) {
                    log.warn(closed.getMessage());
                }
            }
        }

        return rc;
    }

    /**
     * This method is used to check conectiton is avalible for analysis or report ,when analysis or report runs.
     * 
     * @param analysisDataProvider
     * @return
     */

    public static ReturnCode isConnectionAvailable(Connection analysisDataProvider) {
        ReturnCode returnCode = new ReturnCode();

        // check hive connection
        IMetadataConnection metadataConnection = ConvertionHelper.convert(analysisDataProvider);
        if (metadataConnection != null) {
            if (EDatabaseTypeName.HIVE.getXmlName().equalsIgnoreCase(metadataConnection.getDbType())) {
                try {
                    // need to do this first when check for hive embed connection.
                    if (isHiveEmbedded(analysisDataProvider)) {
                        JavaSqlFactory.doHivePreSetup(analysisDataProvider);
                    }
                    HiveConnectionManager.getInstance().checkConnection(metadataConnection);
                    returnCode.setOk(true);
                    return returnCode;
                } catch (ClassNotFoundException e) {
                    returnCode.setOk(false);
                    returnCode.setMessage(e.toString());
                    return returnCode;
                } catch (InstantiationException e) {
                    returnCode.setOk(false);
                    returnCode.setMessage(e.toString());
                    return returnCode;
                } catch (IllegalAccessException e) {
                    returnCode.setOk(false);
                    returnCode.setMessage(e.toString());
                    return returnCode;
                } catch (SQLException e) {
                    returnCode.setOk(false);
                    returnCode.setMessage(e.toString());
                    return returnCode;
                }
            }
        }

        // MOD klliu check file connection is available
        if (analysisDataProvider instanceof FileConnection) {
            FileConnection fileConn = (FileConnection) analysisDataProvider;
            // ADD msjian TDQ-4559 2012-2-28: when the fileconnection is context mode, getOriginalFileConnection.
            if (fileConn.isContextMode()) {
                IRepositoryContextService service = CoreRuntimePlugin.getInstance().getRepositoryContextService();
                if (service != null) {
                    fileConn = service.cloneOriginalValueConnection(fileConn);
                }
            }
            // TDQ-4559 ~
            String filePath = fileConn.getFilePath();
            try {
                BufferedReader filePathAvalible = FilesUtils.isFilePathAvailable(filePath, fileConn);
                if (filePathAvalible != null) {
                    returnCode.setOk(true);
                    return returnCode;
                }
            } catch (UnsupportedEncodingException e) {
                returnCode.setOk(false);
                returnCode.setMessage(filePath);
                return returnCode;
            } catch (FileNotFoundException e) {
                returnCode.setOk(false);
                returnCode.setMessage(filePath);
                return returnCode;
            } catch (IOException e) {
                returnCode.setOk(false);
                returnCode.setMessage(filePath);
                return returnCode;
            }
        }
        // ~
        Properties props = new Properties();
        String userName = JavaSqlFactory.getUsername(analysisDataProvider);
        String password = JavaSqlFactory.getPassword(analysisDataProvider);
        String url = JavaSqlFactory.getURL(analysisDataProvider);
        props.put(TaggedValueHelper.USER, userName);
        props.put(TaggedValueHelper.PASSWORD, password);
        if (analysisDataProvider instanceof MDMConnection) {
            props.put(TaggedValueHelper.UNIVERSE, ConnectionHelper.getUniverse((MDMConnection) analysisDataProvider));
            props.put(TaggedValueHelper.DATA_FILTER, ConnectionHelper.getDataFilter((MDMConnection) analysisDataProvider));
            MdmWebserviceConnection mdmWebserviceConnection = new MdmWebserviceConnection(
                    JavaSqlFactory.getURL(analysisDataProvider), props);
            returnCode = mdmWebserviceConnection.checkDatabaseConnection();
            return returnCode;
        } else if (analysisDataProvider instanceof DatabaseConnection) {
            // MOD qiongli TDQ-11507,for GeneralJdbc,should check connection too after validation jar and jdbc driver .
            if (isGeneralJdbc(analysisDataProvider)) {
                try {
                    ReturnCode rcJdbc = checkGeneralJdbcJarFilePathDriverClassName((DatabaseConnection) analysisDataProvider);
                    if (!rcJdbc.isOk()) {
                        return rcJdbc;
                    }
                } catch (MalformedURLException e) {
                    return new ReturnCode(e.getMessage(), false);
                }
            }
            // MOD qiongli 2014-5-14 in order to check and connect a dbConnection by a correct driver,replace
            // 'ConnectionUtils.checkConnection(...)' with 'managerConn.check(metadataConnection)'.
            ManagerConnection managerConn = new ManagerConnection();
            boolean check = managerConn.check(metadataConnection);
            returnCode.setOk(check);
            if (!check) {
                returnCode.setMessage(managerConn.getMessageException());
            }

        }
        return returnCode;
    }

    /**
     * if the Connection's type is hive embedded return true.
     * 
     * @param analysisDataProvider
     * @return
     */
    public static boolean isHiveEmbedded(Connection analysisDataProvider) {
        if (!Platform.isRunning()) {
            return false;
        }
        // MOD 20130313 TDQ-6524 avoid popup context select dialog when running analysis,yyin
        IMetadataConnection metadataConnection = ConvertionHelper.convert(analysisDataProvider, false,
                analysisDataProvider.getContextName());
        return isHiveEmbedded(metadataConnection);
    }

    /**
     * if the Connection's type is hive embedded return true.
     * 
     * @param metadataConnection
     * @return
     */
    public static boolean isHiveEmbedded(IMetadataConnection metadataConnection) {
        if (!Platform.isRunning()) {
            return false;
        }
        // FIXME do not use metadata connection to test hive mode if possible.
        String dbType = metadataConnection.getDbType();
        String dbVersionString = metadataConnection.getDbVersionString();
        if (EDatabaseTypeName.HIVE.getDisplayName().equalsIgnoreCase(dbType)
                && HiveConnVersionInfo.MODE_EMBEDDED.getKey().equalsIgnoreCase(dbVersionString)) {
            return true;
        }
        return false;
    }

    /**
     * if the Connection's type is General JDBC return true.
     * 
     * @param conn a database connection
     * @return
     */
    public static boolean isGeneralJdbc(Connection conn) {
        boolean jdbc = false;
        if (conn instanceof DatabaseConnection) {
            DatabaseConnection dbConn = (DatabaseConnection) conn;
            EDatabaseTypeName databaseType = EDatabaseTypeName.getTypeFromDbType(dbConn.getDatabaseType());
            if (EDatabaseTypeName.GENERAL_JDBC.equals(databaseType)) {
                jdbc = true;
            }
        }
        return jdbc;
    }

    /**
     * if the DriverClassName is empty or Jar File Path is invalid return false.
     * 
     * @param dbConn a General JDBC database connection
     * @return
     */
    public static ReturnCode checkGeneralJdbcJarFilePathDriverClassName(DatabaseConnection dbConn) throws MalformedURLException {
        ReturnCode returnCode = new ReturnCode();
        String driverClass = JavaSqlFactory.getDriverClass(dbConn);
        String driverJarPath = JavaSqlFactory.getDriverJarPath(dbConn);
        if (driverClass == null || driverClass.trim().equals("")) { //$NON-NLS-1$
            returnCode.setOk(false);
            returnCode.setMessage(Messages.getString("ConnectionUtils.DriverClassEmpty")); //$NON-NLS-1$
        } else {
            if (driverJarPath == null || driverJarPath.trim().equals("")) { //$NON-NLS-1$
                returnCode.setOk(false);
                returnCode.setMessage(Messages.getString("ConnectionUtils.DriverJarFileEmpty")); //$NON-NLS-1$
            } else {
                List<String> driverJarNameList = new ArrayList<String>();
                String[] splits = driverJarPath.split(";"); //$NON-NLS-1$
                for (String str : splits) {
                    if (!StringUtils.isBlank(str)) {
                        driverJarNameList.add(str);
                    }
                }
                LinkedList<String> driverJarRealPaths = getDriverJarRealPaths(driverJarNameList);
                for (String str : driverJarRealPaths) {
                    File jarFile = new File(str);
                    if (!jarFile.exists() || jarFile.isDirectory()) {
                        returnCode.setOk(false);
                        returnCode.setMessage(Messages.getString("ConnectionUtils.DriverJarFileInvalid")); //$NON-NLS-1$
                        break;
                    }

                }
            }
        }
        return returnCode;
    }

    /**
     * DOC xqliu Comment method "createConnectionWithTimeout".
     * 
     * @param driver
     * @param url
     * @param props
     * @return
     * @throws SQLException
     * @deprecated use @
     * {@link ExtractMetaDataUtils#connect(String, String, String, String, String, String, String, String)} instead.
     */
    @Deprecated
    public static synchronized java.sql.Connection createConnectionWithTimeout(Driver driver, String url, Properties props)
            throws SQLException {
        java.sql.Connection ret = null;
        if (isTimeout()) {
            ConnectionCreator cc = new ConnectionCreator(driver, url, props);
            Thread t = new Thread(cc);
            t.start();
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
            if (isTimeout) {
                cc = null;
                throw new SQLException(Messages.getString("ConnectionUtils.ConnectionTimeout")); //$NON-NLS-1$
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
            // MOD klliu TDQ-4659 sso could not check passed.2012-02-10
            try {
                ret = org.talend.utils.sql.ConnectionUtils.createConnection(url, driver, props);
            } catch (InstantiationException e) {
                log.error(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }
        return ret;
    }

    /**
     * 
     * abstract this function from getClassDriver(String driverClassName, String url, Properties props). load the jdbc
     * driver based on ManagedDrivr.if it is registed,return the driver.else load jars from lib manage system and
     * regist.
     * 
     * @param driverClassName
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private static Driver getClassDriverFromSQLExplorer(String driverClassName, Properties props) throws InstantiationException,
            IllegalAccessException {
        return SqlExplorerUtils.getDefault().getClassDriverFromSQLExplorer(driverClassName, props);
    }

    /**
     * Method "isValid".
     * 
     * @param connection the connection to test
     * @return a return code with the appropriate message (never null)
     */
    public static ReturnCode isValid(final java.sql.Connection connection) {
        return org.talend.utils.sql.ConnectionUtils.isValid(connection);
    }

    /**
     * Method "closeConnection".
     * 
     * @param connection the connection to close.
     * @return a ReturnCode with true if ok, false if problem. {@link ReturnCode#getMessage()} gives the error message
     * when there is a problem.
     */
    public static ReturnCode closeConnection(final java.sql.Connection connection) {
        return org.talend.utils.sql.ConnectionUtils.closeConnection(connection);
    }

    // ADD xqliu 2009-11-09 bug 9403
    private static final String DEFAULT_TABLE_NAME = "TDQ_PRODUCT";//$NON-NLS-1$

    /**
     * DOC xqliu Comment method "existTable".
     * 
     * @param url
     * @param driver
     * @param props
     * @param tableName
     * @param schema
     * @return
     * @deprecated
     */
    @Deprecated
    public static boolean existTable(String url, String driver, Properties props, String tableName, String schema) {
        java.sql.Connection connection = null;
        if (tableName == null || org.talend.dataquality.PluginConstant.EMPTY_STRING.equals(tableName.trim())) {
            tableName = DEFAULT_TABLE_NAME;
        }
        try {
            connection = ConnectionUtils.createConnection(url, driver, props);
            if (connection != null) {
                Statement stat = connection.createStatement();
                if (!org.talend.dataquality.PluginConstant.EMPTY_STRING.equals(schema)) {
                    stat.executeQuery("Select * from " + schema.toUpperCase() + org.talend.dataquality.PluginConstant.DOT_STRING + tableName); //$NON-NLS-1$
                } else {
                    stat.executeQuery("Select * from " + tableName);//$NON-NLS-1$
                }
                stat.close();
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

    /**
     * DOC xqliu Comment method "isOdbcMssql". bug 9822
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static boolean isOdbcMssql(java.sql.Connection connection) throws SQLException {
        @SuppressWarnings("deprecation")
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.ODBC_DRIVER_NAME)
                && connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().equals(DatabaseConstant.ODBC_MSSQL_PRODUCT_NAME)) {
            return true;
        }
        return false;
    }

    public static boolean isOdbcProgress(java.sql.Connection connection) throws SQLException {
        @SuppressWarnings("deprecation")
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.ODBC_DRIVER_NAME)
                && connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().toLowerCase().indexOf(DatabaseConstant.ODBC_PROGRESS_PRODUCT_NAME) > -1) {
            return true;
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isTeradata".
     * 
     * @param connection
     * @return
     */
    public static boolean isTeradata(Connection connection) {
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection);
        if (dbConn != null) {
            String databaseType = dbConn.getDatabaseType() == null ? org.talend.dataquality.PluginConstant.EMPTY_STRING : dbConn
                    .getDatabaseType();
            return EDatabaseTypeName.TERADATA.getXmlName().equalsIgnoreCase(databaseType);
        }
        return false;
    }

    /**
     * DOC zshen Comment method "isMssql".
     * 
     * @param connection
     * @return decide to whether is mssql connection
     * @throws SQLException
     */
    public static boolean isMssql(java.sql.Connection connection) throws SQLException {
        @SuppressWarnings("deprecation")
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && !connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.ODBC_DRIVER_NAME)
                && connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().equals(DatabaseConstant.ODBC_MSSQL_PRODUCT_NAME)) {
            return true;
        }
        return false;
    }

    /**
     * mzhao bug: TDQ-4622 Is the connection is an ingres connection?
     * 
     * @param connection
     * @return true if connection is ingres, false otherwise
     */
    public static boolean isIngres(Connection connection) throws SQLException {
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection);
        if (dbConn != null) {
            String databaseType = dbConn.getDatabaseType() == null ? org.talend.dataquality.PluginConstant.EMPTY_STRING : dbConn
                    .getDatabaseType();
            return EDatabaseTypeName.INGRES.getXmlName().equalsIgnoreCase(databaseType);
        }
        return false;
    }

    /**
     * mzhao bug: TDQ-4622 Is the connection is an informix connection?
     * 
     * @param connection
     * @return true if connection is informix, false otherwise
     */
    public static boolean isInformix(Connection connection) throws SQLException {
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection);
        if (dbConn != null) {
            String databaseType = dbConn.getDatabaseType() == null ? org.talend.dataquality.PluginConstant.EMPTY_STRING : dbConn
                    .getDatabaseType();
            return EDatabaseTypeName.INFORMIX.getXmlName().equalsIgnoreCase(databaseType);
        }
        return false;
    }

    /**
     * mzhao bug: TDQ-4622 Is the connection is an DB2 connection?
     * 
     * @param connection
     * @return true if connection is DB2, false otherwise
     */
    public static boolean isDB2(Connection connection) throws SQLException {
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection);
        if (dbConn != null) {
            String databaseType = dbConn.getDatabaseType() == null ? org.talend.dataquality.PluginConstant.EMPTY_STRING : dbConn
                    .getDatabaseType();
            // databaseType: IBM DB2, but DBKey is DB2
            return databaseType.contains(EDatabaseTypeName.IBMDB2.getXmlName());
        }
        return false;
    }

    /**
     * Comment method "isDB2".
     * 
     * @param metadata
     * @return
     * @throws SQLException
     */
    public static boolean isDB2(DatabaseMetaData metadata) throws SQLException {
        if (metadata != null && metadata.getDatabaseProductName() != null
                && metadata.getDatabaseProductName().indexOf(DatabaseConstant.IBM_DB2_ZOS_PRODUCT_NAME) > -1) {
            return true;
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isMssql".
     * 
     * @param connection
     * @return
     */
    public static boolean isMssql(Connection connection) {
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection);
        if (dbConn != null) {
            String databaseType = dbConn.getDatabaseType() == null ? org.talend.dataquality.PluginConstant.EMPTY_STRING : dbConn
                    .getDatabaseType();
            return EDatabaseTypeName.MSSQL.getXmlName().equalsIgnoreCase(databaseType);
        }
        return false;
    }

    /**
     * DOC hwang Comment method "isMysql".
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static boolean isMysql(java.sql.Connection connection) throws SQLException {
        @SuppressWarnings("deprecation")
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.MYSQL_PRODUCT_NAME)
                && connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().toLowerCase().indexOf(DatabaseConstant.MYSQL_PRODUCT_NAME) > -1) {
            return true;
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isAs400".
     * 
     * @param connection
     * @return
     */
    public static boolean isAs400(Connection connection) {
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection);
        if (dbConn != null) {
            String databaseType = dbConn.getDatabaseType() == null ? org.talend.dataquality.PluginConstant.EMPTY_STRING : dbConn
                    .getDatabaseType();
            return EDatabaseTypeName.AS400.getDisplayName().equalsIgnoreCase(databaseType);
        }
        return false;
    }

    public static boolean isSybaseeDBProducts(String productName) {
        return Arrays.asList(org.talend.utils.sql.ConnectionUtils.getSybaseDBProductsName()).contains(productName);
    }

    /**
     * DOC xqliu Comment method "isMdmConnection".
     * 
     * @param dataprovider
     * @return
     */
    public static boolean isMdmConnection(DataProvider dataprovider) {
        return dataprovider instanceof MDMConnection;
    }

    /**
     * DOC qiongli Comment method "isDelimitedFileConnection".
     * 
     * @param dataprovider
     * @return
     */
    public static boolean isDelimitedFileConnection(DataProvider dataprovider) {
        return dataprovider instanceof DelimitedFileConnection;
    }

    /**
     * DOC xqliu Comment method "isMdmConnection".
     * 
     * @param object
     * @return
     */
    public static boolean isMdmConnection(Object object) {
        if (object != null) {
            if (object instanceof ProviderConnection) {
                // FIXME it will cause stack overflow.
                return isMdmConnection(object);
            } else if (object instanceof DataProvider) {
                return isMdmConnection((DataProvider) object);
            } else if (object instanceof IRepositoryViewObject) {
                Item item = ((IRepositoryViewObject) object).getProperty().getItem();
                return item instanceof MDMConnectionItem;
            }
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isMdmConnection".
     * 
     * @param file
     * @return
     */
    public static boolean isMdmConnection(IRepositoryViewObject reposViewObj) {
        // MOD qiongli 2011-3-17,bug 19530.avoid NPE.
        if (reposViewObj == null) {
            return false;
        }
        Item item = reposViewObj.getProperty().getItem();
        return item instanceof MDMConnectionItem;
    }

    /**
     * DOC zshen Comment method "isOdbcMssql". feature 10630
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static boolean isOdbcExcel(java.sql.Connection connection) throws SQLException {
        @SuppressWarnings("deprecation")
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.ODBC_DRIVER_NAME)
                && connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().equals(DatabaseConstant.ODBC_EXCEL_PRODUCT_NAME)) {
            return true;
        }
        return false;
    }

    /**
     * DOC zshen Comment method "isOdbcConnection". feature 10630
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static boolean isOdbcConnection(java.sql.Connection connection) throws SQLException {
        @SuppressWarnings("deprecation")
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.ODBC_DRIVER_NAME)) {
            return true;
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isPostgresql".
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static boolean isPostgresql(java.sql.Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        if (metaData != null) {
            String databaseProductName = metaData.getDatabaseProductName();
            if (databaseProductName != null) {
                return databaseProductName.toLowerCase().indexOf(DatabaseConstant.POSTGRESQL_PRODUCT_NAME) > -1;
            }
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isPostgresql".
     * 
     * @param connection
     * @return
     */
    public static boolean isPostgresql(Connection connection) {
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection);
        if (dbConn != null) {
            String databaseType = dbConn.getDatabaseType() == null ? org.talend.dataquality.PluginConstant.EMPTY_STRING : dbConn
                    .getDatabaseType();
            return EDatabaseTypeName.PSQL.getXmlName().equalsIgnoreCase(databaseType);
        }
        return false;
    }

    public static boolean isPostgresql(DBConnectionParameter connectionParam) {
        String sqlTypeName = connectionParam == null ? null : connectionParam.getSqlTypeName();
        if (sqlTypeName != null) {
            return sqlTypeName.toLowerCase().contains("postgres"); //$NON-NLS-1$
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isOdbcPostgresql".
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static boolean isOdbcPostgresql(java.sql.Connection connection) throws SQLException {
        @SuppressWarnings("deprecation")
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.ODBC_DRIVER_NAME)
                && connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().toLowerCase().indexOf(DatabaseConstant.POSTGRESQL_PRODUCT_NAME) > -1) {
            return true;
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isOdbcOracle".
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static boolean isOdbcOracle(java.sql.Connection connection) throws SQLException {
        @SuppressWarnings("deprecation")
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.ODBC_DRIVER_NAME)
                && connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().toLowerCase().indexOf(DatabaseConstant.ODBC_ORACLE_PRODUCT_NAME) > -1) {
            return true;
        }
        return false;
    }

    /**
     * 
     * DOC qiongli Comment method "isOdbcOracle".
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static boolean isOdbcTeradata(java.sql.Connection connection) throws SQLException {
        @SuppressWarnings("deprecation")
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.ODBC_DRIVER_NAME)
                && connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().toLowerCase().indexOf(DatabaseConstant.ODBC_TERADATA_PRODUCT_NAME) > -1) {
            return true;
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isOdbcIngres".
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    public static boolean isOdbcIngres(java.sql.Connection connection) throws SQLException {
        @SuppressWarnings("deprecation")
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.ODBC_DRIVER_NAME)
                && connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().toLowerCase().indexOf(DatabaseConstant.INGRES_PRODUCT_NAME) > -1) {
            return true;
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isJdbcIngres".
     * 
     * @param connection
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("deprecation")
    public static boolean isJdbcIngres(java.sql.Connection connection) throws SQLException {
        DatabaseMetaData connectionMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection);
        if (connectionMetadata.getDriverName() != null
                && connectionMetadata.getDriverName().equals(DatabaseConstant.JDBC_INGRES_DEIVER_NAME)
                && connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().toLowerCase().indexOf(DatabaseConstant.INGRES_PRODUCT_NAME) > -1) {
            return true;
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "isSqlite".
     * 
     * @param connection
     * @return
     */
    public static boolean isSqlite(Connection connection) {
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection);
        if (dbConn != null) {
            return SupportDBUrlType.SQLITE3DEFAULTURL.getDBKey().equals(dbConn.getDatabaseType());
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "printResultSetColumns".
     * 
     * @param rs
     */
    public static void printResultSetColumns(ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnSize = metaData.getColumnCount();
            for (int i = 0; i < columnSize; ++i) {
                System.out.println("[" + (i + 1) + "]:" + metaData.getColumnName(i + 1));//$NON-NLS-1$//$NON-NLS-2$
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOC xqliu Comment method "getDatabaseType".
     * 
     * @param connection
     * @return the database type string or null
     */
    public static String getDatabaseType(Connection connection) {
        MDMConnection mdmConn = SwitchHelpers.MDMCONNECTION_SWITCH.doSwitch(connection);
        if (mdmConn != null) {
            return SupportDBUrlType.MDM.getDBKey();
        }
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection);
        if (dbConn != null) {
            return dbConn.getDatabaseType();
        }
        return null;
    }

    /**
     * DOC zshen Comment method "setName".
     * 
     * @param conn
     * @param password
     */
    public static void setName(Connection conn, String name) {
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(conn);
        if (dbConn != null) {
            dbConn.setName(name);
            dbConn.setLabel(name);
        }
        MDMConnection mdmConn = SwitchHelpers.MDMCONNECTION_SWITCH.doSwitch(conn);
        if (mdmConn != null) {
            mdmConn.setName(name);
            mdmConn.setLabel(name);
        }
    }

    /**
     * DOC connection created by TOS need to fill the basic information for useing in TOP.<br>
     * 
     * @deprecated Not be useful anymore later, TOS should use the common filler API to create the metadata objects,
     * then TOP don't complement again. Use MetadataFillFactory.java
     * 
     * @param conn
     * @return
     */
    @Deprecated
    public static Connection fillConnectionInformation(Connection conn) {
        boolean saveFlag = false;
        // fill metadata of connection
        if (conn.getName() == null || conn.getLabel() == null) {
            saveFlag = true;
            conn = fillConnectionMetadataInformation(conn);
        }
        // fill structure of connection
        List<Catalog> catalogs = ConnectionHelper.getCatalogs(conn);
        List<Schema> schemas = ConnectionHelper.getSchema(conn);
        // MOD xqliu 2010-10-19 bug 16441: case insensitive
        if ((catalogs.isEmpty() && schemas.isEmpty())
                || (ConnectionHelper.getAllSchemas(conn).isEmpty() && (ConnectionUtils.isMssql(conn)
                        || ConnectionUtils.isPostgresql(conn) || ConnectionUtils.isAs400(conn)))) {
            // ~ 16441
            DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(conn);
            if (dbConn != null) {
                saveFlag = true;

                conn = fillDbConnectionInformation(dbConn);
            } else {
                MDMConnection mdmConn = SwitchHelpers.MDMCONNECTION_SWITCH.doSwitch(conn);
                if (mdmConn != null) {
                    if (mdmConn.getDataPackage().isEmpty()) {
                        saveFlag = true;
                        conn = fillMdmConnectionInformation(mdmConn);
                    }
                }
            }
        }
        if (saveFlag && conn != null) {
            ElementWriterFactory.getInstance().createDataProviderWriter().save(conn);
        }
        return conn;
    }

    /**
     * DOC xqliu Comment method "fillConnectionInformation".
     * 
     * @param conns
     * @return
     * @deprecated Is Replaced By DBConnectionFiller.fillUIConnParams
     */
    @Deprecated
    public static List<Connection> fillConnectionInformation(List<Connection> conns) {
        List<Connection> results = new ArrayList<Connection>();
        for (Connection conn : conns) {

            results.add(fillConnectionInformation(conn));
        }
        return results;
    }

    /**
     * DOC xqliu Comment method "fillMdmConnectionInformation".
     * 
     * @param mdmConn
     * @return
     */
    public static MDMConnection fillMdmConnectionInformation(MDMConnection mdmConn) {
        // fill database structure
        Properties properties = new Properties();
        properties.put(TaggedValueHelper.USER, mdmConn.getUsername());
        properties.put(TaggedValueHelper.PASSWORD, mdmConn.getValue(mdmConn.getPassword(), false));
        properties.put(TaggedValueHelper.UNIVERSE,
                mdmConn.getUniverse() == null ? org.talend.dataquality.PluginConstant.EMPTY_STRING : mdmConn.getUniverse());
        MdmWebserviceConnection mdmWsConn = new MdmWebserviceConnection(mdmConn.getPathname(), properties);
        ConnectionHelper.addXMLDocuments(mdmWsConn.createConnection(mdmConn));
        return mdmConn;
    }

    /**
     * DOC xqliu Comment method "fillDbConnectionInformation".
     * 
     * @deprecated Is Replaced By DBConnectionFiller.fillUIConnParams
     * 
     * @param dbConn
     * @return
     */
    @Deprecated
    public static DatabaseConnection fillDbConnectionInformation(DatabaseConnection dbConn) {
        // fill database structure
        boolean noStructureExists = ConnectionHelper.getAllCatalogs(dbConn).isEmpty()
                && ConnectionHelper.getAllSchemas(dbConn).isEmpty();
        java.sql.Connection sqlConn = null;
        try {
            if (noStructureExists) { // do no override existing catalogs or
                                     // schemas
                                     // Map<String, String> paramMap =
                                     // ParameterUtil.toMap(ConnectionUtils.createConnectionParam(dbConn));
                IMetadataConnection metaConnection = ConvertionHelper.convert(dbConn);
                MetadataFillFactory dbInstance = MetadataFillFactory.getDBInstance(dbConn);
                dbConn = (DatabaseConnection) dbInstance.fillUIConnParams(metaConnection, dbConn);
                sqlConn = MetadataConnectionUtils.createConnection(metaConnection).getObject();

                if (sqlConn != null) {
                    DatabaseMetaData dm = ExtractMetaDataUtils.getInstance().getDatabaseMetaData(sqlConn, dbConn, false);
                    dbInstance.fillCatalogs(dbConn, dm, MetadataConnectionUtils.getPackageFilter(dbConn, dm, true));
                    dbInstance.fillSchemas(dbConn, dm, MetadataConnectionUtils.getPackageFilter(dbConn, dm, false));
                }

            }
        } finally {
            if (sqlConn != null) {
                ConnectionUtils.closeConnection(sqlConn);
            }

        }
        return dbConn;
    }

    /**
     * DOC xqliu Comment method "createConnectionParam".
     * 
     * @param conn
     * @return
     */
    public static DBConnectionParameter createConnectionParam(Connection conn) {
        DBConnectionParameter connectionParam = new DBConnectionParameter();

        Properties properties = new Properties();
        // MOD xqliu 2010-08-06 bug 14593
        properties.setProperty(TaggedValueHelper.USER, JavaSqlFactory.getUsername(conn));
        properties.setProperty(TaggedValueHelper.PASSWORD, JavaSqlFactory.getPassword(conn));
        // ~ 14593
        connectionParam.setParameters(properties);
        connectionParam.setName(conn.getName());
        connectionParam.setAuthor(MetadataHelper.getAuthor(conn));
        connectionParam.setDescription(MetadataHelper.getDescription(conn));
        connectionParam.setPurpose(MetadataHelper.getPurpose(conn));
        connectionParam.setStatus(MetadataHelper.getDevStatus(conn));
        connectionParam.setDriverPath(((DatabaseConnection) conn).getDriverJarPath());
        connectionParam.setDriverClassName(JavaSqlFactory.getDriverClass(conn));
        connectionParam.setJdbcUrl(JavaSqlFactory.getURL(conn));
        connectionParam.setHost(JavaSqlFactory.getServerName(conn));
        connectionParam.setPort(JavaSqlFactory.getPort(conn));

        if (conn instanceof DatabaseConnection) {
            connectionParam.setSqlTypeName(((DatabaseConnection) conn).getDatabaseType());
            String dbmsId = ((DatabaseConnection) conn).getDbmsId();
            connectionParam.setDbmsId(dbmsId);
            // ADD klliu 2011-05-19 21704: Refactoring this "otherParameter" !
            connectionParam.setFilterCatalog(dbmsId);
        }
        // MOD klliu if oracle set schema to other parameter
        if (conn instanceof DatabaseConnection) {
            DatabaseConnection dbConnection = (DatabaseConnection) conn;
            connectionParam.setOtherParameter(dbConnection.getUiSchema());
            // ADD klliu 2011-05-19 21704: Refactoring this "otherParameter" !
            connectionParam.setFilterSchema(dbConnection.getUiSchema());
        }
        // MOD mzhao adapte model. MDM connection editing need handle
        // additionally.
        // connectionParam.getParameters().setProperty(TaggedValueHelper.UNIVERSE,
        // DataProviderHelper.getUniverse(connection));
        connectionParam.setDbName(JavaSqlFactory.getSID(conn));
        // MOD by zshen for bug 15314
        String retrieveAllMetadata = MetadataHelper.getRetrieveAllMetadata(conn);
        connectionParam.setRetrieveAllMetadata(retrieveAllMetadata == null ? true : new Boolean(retrieveAllMetadata)
                .booleanValue());

        return connectionParam;
    }

    /**
     * DOC xqliu Comment method "fillConnectionMetadataInformation".
     * 
     * @param conn
     * @return
     */
    public static Connection fillConnectionMetadataInformation(Connection conn) {
        // ADD xqliu 2010-10-13 bug 15756
        int tSize = conn.getTaggedValue().size();
        EList<Package> dataPackage = conn.getDataPackage();
        // ~ 15756
        Property property = PropertyHelper.getProperty(conn);
        // fill name and label
        conn.setName(property.getLabel());
        conn.setLabel(property.getLabel());
        // fill metadata
        MetadataHelper.setAuthor(conn, property.getAuthor().getLogin());
        MetadataHelper.setDescription(property.getDescription(), conn);
        String statusCode = property.getStatusCode() == null ? org.talend.dataquality.PluginConstant.EMPTY_STRING : property
                .getStatusCode();
        MetadataHelper.setDevStatus(conn,
                org.talend.dataquality.PluginConstant.EMPTY_STRING.equals(statusCode) ? DevelopmentStatus.DRAFT.getLiteral()
                        : statusCode);
        MetadataHelper.setPurpose(property.getPurpose(), conn);
        MetadataHelper.setVersion(property.getVersion(), conn);
        String retrieveAllMetadataStr = MetadataHelper.getRetrieveAllMetadata(conn);
        // ADD xqliu 2010-10-13 bug 15756
        if (tSize == 0 && dataPackage.size() == 1
                && !org.talend.dataquality.PluginConstant.EMPTY_STRING.equals(dataPackage.get(0).getName())) {
            retrieveAllMetadataStr = "false";//$NON-NLS-1$
        }
        // ~ 15756
        // MOD klliu bug 15821 retrieveAllMetadataStr for Diff database
        MetadataHelper.setRetrieveAllMetadata(retrieveAllMetadataStr == null ? "true" : retrieveAllMetadataStr, conn);//$NON-NLS-1$
        String schema = MetadataHelper.getOtherParameter(conn);
        MetadataHelper.setOtherParameter(schema, conn);
        return conn;
    }

    /**
     * DOC xqliu Comment method "createDatabaseVersionString".
     * 
     * @param dbConn
     * @return
     */
    public static String createDatabaseVersionString(DatabaseConnection dbConn) {
        List<EDatabaseVersion4Drivers> eVersions = EDatabaseVersion4Drivers.indexOfByDbType(dbConn.getProductId());
        if (eVersions != null && !eVersions.isEmpty()) {
            return eVersions.get(0).getVersionValue();
        }
        return null;
    }

    /**
     * retrieve sqlDataType if it have a name is "Null".
     * 
     * @param tdTable
     */
    public static void retrieveColumn(MetadataTable tdTable) {
        List<TdColumn> columnList = ColumnSetHelper.getColumns((ColumnSet) tdTable);
        if (columnList != null && columnList.size() > 0) {
            TdColumn tempColumn = columnList.get(0);
            if (tempColumn.getSqlDataType() == null || "NULL".equalsIgnoreCase(tempColumn.getSqlDataType().getName())//$NON-NLS-1$
                    && 0 == tempColumn.getSqlDataType().getJavaDataType()) {

                if (tdTable != null) {
                    Connection tempConnection = ConnectionHelper.getConnection(tempColumn);
                    if (tempConnection != null) {
                        java.sql.Connection connection = JavaSqlFactory.createConnection(tempConnection).getObject();
                        if (connection == null) {
                            return;
                        }
                        for (TdColumn colobj : columnList) {
                            TdColumn tdColumn = colobj;

                            try {
                                List<TdSqlDataType> newDataTypeList = getDataType(
                                        ConnectionUtils.getName(CatalogHelper.getParentCatalog(tdTable)),
                                        ConnectionUtils.getName(SchemaHelper.getParentSchema(tdTable)), tdTable.getName(),
                                        tdColumn.getName(), connection);
                                if (newDataTypeList.size() > 0) {
                                    tdColumn.setSqlDataType(newDataTypeList.get(0));
                                }
                            } catch (SQLException e) {
                                log.error(e, e);
                            }
                        }
                        ConnectionUtils.closeConnection(connection);
                    }
                    ElementWriterFactory.getInstance().createDataProviderWriter().save(tempConnection);
                }
            }
        }
    }

    public static void retrieveColumn(List<? extends MetadataTable> tdTableList) {
        if (tdTableList == null) {
            return;
        }
        for (MetadataTable currColumnSet : tdTableList) {
            retrieveColumn(currColumnSet);
        }
    }

    /**
     * method "fillAttributeBetweenConnection".
     * 
     * @param target the connection which will be filled attribute.
     * @param source the connection which will provider attribute.
     * 
     * @deprecated the method will be deleted when the connection fetch from
     * IRepositoryViewObject.getProperty().getItem().getConnection
     */
    @Deprecated
    public static void fillAttributeBetweenConnection(Connection target, Connection source) {
        if (target == null || source == null) {
            return;
        }
        target.setName(source.getName());
        target.setLabel(source.getLabel());
        MetadataHelper.setPurpose(MetadataHelper.getPurpose(source), target);
        MetadataHelper.setDescription(MetadataHelper.getDescription(source), target);
        MetadataHelper.setAuthor(target, MetadataHelper.getAuthor(source));
        // MetadataHelper.setVersion(versionText.getText(),
        // currentModelElement);
        MetadataHelper.setDevStatus(target, MetadataHelper.getDevStatus(source));
        ConnectionUtils.setName(target, source.getName());
        JavaSqlFactory.setUsername(target, JavaSqlFactory.getUsername(source));
        JavaSqlFactory.setPassword(target, JavaSqlFactory.getPassword(source));
        JavaSqlFactory.setURL(target, JavaSqlFactory.getURL(source));
    }

    static String getName(ModelElement element) {
        String name = element == null ? null : element.getName();
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return name;
    }

    /**
     * DOC zshen Comment method "getPackageFilter".
     * 
     * @param connectionParam
     * @return
     * @deprecated After branch4.2 we unique the ui for the wizard which to create connection so no retrieveAll and Data
     * filter again don't needed this method. And have all kinds of filter can be use in the repository view when the
     * tree be display not fill connection.
     */
    @Deprecated
    public static List<String> getPackageFilter(DBConnectionParameter connectionParam) {
        List<String> packageFilter = null;
        if (connectionParam.getSqlTypeName().equals(SupportDBUrlType.MDM.getDBKey())) {
            String dataFilter = connectionParam.getParameters().getProperty(TaggedValueHelper.DATA_FILTER);
            if (dataFilter != null) {
                packageFilter = Arrays.asList(dataFilter.split(","));//$NON-NLS-1$
            }
        } else {
            if (!connectionParam.isRetrieveAllMetadata()) {
                packageFilter = new ArrayList<String>();
                String dbName = connectionParam.getDbName();
                // String otherParameter = null; // MOD scorreia 2010-10-20 bug 16562 avoid NPE
                // MOD by msjian 2011-5-16 20875: "reload table list" for postgres have some issue
                if (isOracle(connectionParam)) {
                    dbName = getDbName(connectionParam);
                }
                // MOD qiongli 2011-9-14 TDQ-3317,avoid empty string
                if (dbName != null && !dbName.equals(PluginConstant.EMPTY_STRING)) {
                    packageFilter.add(dbName);
                }
            }
        }
        return packageFilter;
    }

    private static boolean isOracle(DBConnectionParameter connectionParam) {
        if (connectionParam == null) {
            return false;
        } else {
            return connectionParam.getSqlTypeName().equalsIgnoreCase(SupportDBUrlType.ORACLEWITHSERVICENAMEDEFAULTURL.getDBKey())
                    || connectionParam.getSqlTypeName().equalsIgnoreCase(SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getDBKey());
        }
    }

    /**
     * get the DbName from DBConnectionParameter, this value is for package filter only.
     * 
     * @param connectionParam
     * @return
     */
    private static String getDbName(DBConnectionParameter connectionParam) {
        String dbName = null;
        String filterSchema = null;
        if (connectionParam != null && connectionParam.getFilterSchema() != null) {
            filterSchema = connectionParam.getFilterSchema().toUpperCase();
        }
        if (filterSchema != null) {
            dbName = filterSchema;
        } else {
            dbName = connectionParam.getParameters().getProperty(TaggedValueHelper.USER).toUpperCase();
        }
        return dbName;
    }

    public static List<ModelElement> getXMLElements(TdXmlSchema document) {
        List<ModelElement> elements = document.getOwnedElement();
        // Load from dababase
        if (elements == null || elements.size() == 0) {
            if (!DQRepositoryNode.isOnFilterring()) {
                XMLSchemaBuilder xmlScheBuilder = XMLSchemaBuilder.getSchemaBuilder(document);
                elements = xmlScheBuilder.getRootElements(document);
                document.getOwnedElement().addAll(elements);
                Connection conn = (Connection) document.getDataManager().get(0);
                ElementWriterFactory.getInstance().createDataProviderWriter().save(conn);
            } else {
                elements = elements == null ? new ArrayList<ModelElement>() : elements;
            }
        }
        return elements;
    }

    /**
     * DOC gdbu Comment method "getXMLElementsWithOutSave".
     * 
     * @param document
     * @return
     */
    public static List<ModelElement> getXMLElementsWithOutSave(TdXmlSchema document) {
        List<ModelElement> elements = document.getOwnedElement();
        // Load from dababase
        if (elements == null || elements.size() == 0) {
            if (!DQRepositoryNode.isOnFilterring()) {
                XMLSchemaBuilder xmlScheBuilder = XMLSchemaBuilder.getSchemaBuilder(document);
                elements = xmlScheBuilder.getRootElements(document);
            } else {
                elements = elements == null ? new ArrayList<ModelElement>() : elements;
            }
        }
        return elements;
    }

    /**
     * DOC gdbu Comment method "getXMLElementsWithOutSave".
     * 
     * @param element
     * @return
     */
    public static List<TdXmlElementType> getXMLElementsWithOutSave(TdXmlElementType element) {
        TdXmlContent xmlContent = element.getXmlContent();
        List<TdXmlElementType> elements = xmlContent == null ? new ArrayList<TdXmlElementType>() : xmlContent.getXmlElements();
        // Load from dababase
        if ((xmlContent == null || elements == null || elements.size() == 0) && !DQRepositoryNode.isOnFilterring()) {
            XMLSchemaBuilder xmlScheBuilder = XMLSchemaBuilder.getSchemaBuilder(element.getOwnedDocument());
            elements = xmlScheBuilder.getChildren(element);
        }
        return elements;
    }

    public static List<TdXmlElementType> getXMLElements(TdXmlElementType element) {
        TdXmlContent xmlContent = element.getXmlContent();
        List<TdXmlElementType> elements = xmlContent == null ? new ArrayList<TdXmlElementType>() : xmlContent.getXmlElements();
        // Load from dababase
        if ((xmlContent == null || elements == null || elements.size() == 0) && !DQRepositoryNode.isOnFilterring()) {
            XMLSchemaBuilder xmlScheBuilder = XMLSchemaBuilder.getSchemaBuilder(element.getOwnedDocument());
            elements = xmlScheBuilder.getChildren(element);
            Connection conn = (Connection) element.getOwnedDocument().getDataManager().get(0);
            ElementWriterFactory.getInstance().createDataProviderWriter().save(conn);
        }
        return elements;
    }

    /**
     * Method "getDataType".
     * 
     * @param catalogName the catalog (can be null)
     * @param schemaPattern the schema(s) (can be null)
     * @param tablePattern the table(s)
     * @param columnPattern the column(s)
     * @param connection the connection
     * @return the list of datatypes of the given columns
     * @throws SQLException
     */
    public static List<TdSqlDataType> getDataType(String catalogName, String schemaPattern, String tablePattern,
            String columnPattern, java.sql.Connection connection) throws SQLException {
        List<TdSqlDataType> sqlDatatypes = new ArrayList<TdSqlDataType>();
        @SuppressWarnings("deprecation")
        ResultSet columns = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection).getColumns(catalogName,
                schemaPattern, tablePattern, columnPattern);
        while (columns.next()) {
            sqlDatatypes.add(createDataType(columns));
        }
        columns.close();
        return sqlDatatypes;
    }

    public static TdSqlDataType createDataType(ResultSet columns) throws SQLException {
        TdSqlDataType sqlDataType = RelationalFactory.eINSTANCE.createTdSqlDataType();
        try {
            sqlDataType.setJavaDataType(columns.getInt(GetColumn.DATA_TYPE.name()));
        } catch (Exception e1) {
            log.warn(e1, e1);
        }
        try {
            sqlDataType.setName(columns.getString(GetColumn.TYPE_NAME.name()));
        } catch (Exception e1) {
            log.warn(e1, e1);
        }
        try {
            sqlDataType.setNumericPrecision(columns.getInt(GetColumn.DECIMAL_DIGITS.name()));
            sqlDataType.setNumericPrecisionRadix(columns.getInt(GetColumn.NUM_PREC_RADIX.name()));
        } catch (Exception e) {
            log.warn(e);
        }
        return sqlDataType;
    }

    /**
     * if the connection has sid return false, else return true (don't need the TaggedValue any more).
     * 
     * @param element
     * @return
     */
    public static boolean getRetrieveAllMetadata(ModelElement element) {
        if (element != null && element instanceof Connection) {
            if (element instanceof DatabaseConnection) {
                DatabaseConnection dbConn = (DatabaseConnection) element;
                String sid = JavaSqlFactory.getSID(dbConn);
                if (sid != null && !"".equals(sid.trim())) { //$NON-NLS-1$
                    // MOD klliu bug 22900
                    TaggedValue taggedValue = TaggedValueHelper.getTaggedValue(TaggedValueHelper.RETRIEVE_ALL,
                            element.getTaggedValue());
                    // if connection is created by 4.2 or 5.0 ,the tagedValue(RETRIEVE_ALL) has been removed.
                    if (taggedValue != null) {
                        String value = taggedValue.getValue();
                        if (value.equals("true")) { //$NON-NLS-1$
                            return true;
                        }
                    }
                    // ~
                    if (ConnectionHelper.isOracle(dbConn) || isPostgresql(dbConn)) {
                        String uiSchema = dbConn.getUiSchema();
                        if (uiSchema != null && !"".equals(uiSchema.trim())) { //$NON-NLS-1$
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else if (element instanceof MDMConnection) {
                MDMConnection mdmConn = (MDMConnection) element;
                String context = mdmConn.getContext();
                if (context != null && !"".equals(context.trim())) { //$NON-NLS-1$
                    return false;
                } else {
                    return true;
                }
            } else if (element instanceof FileConnection) {
                // do file connection can filter catalog/schema?
                return true;
            }
        }
        return true;
    }

    /**
     * Get the original DatabaseConnection for context mode.
     * 
     * @param connection
     * @return
     */
    public static DatabaseConnection getOriginalDatabaseConnection(DatabaseConnection connection) {
        if (connection == null) {
            return null;
        }
        if (connection.isContextMode()) {
            String contextName = connection.getContextName();
            if (contextName == null) {
                return DBConnectionContextUtils.cloneOriginalValueConnection(connection, true, null);
            }
            return DBConnectionContextUtils.cloneOriginalValueConnection(connection, false, contextName);
        }
        return connection;
    }

    /**
     * Get the original FileConnection for context mode.
     * 
     * @param fileConn
     * @return
     */
    public static FileConnection getOriginalFileConnection(FileConnection fileConn) {
        if (fileConn == null) {
            return null;
        }
        if (fileConn.isContextMode()) {
            return CloneConnectionUtils.cloneOriginalValueConnection(fileConn, false);
        }
        return fileConn;
    }

    /**
     * if sqlite connection don't set username, set it with a default username.
     * 
     * @param connection
     */
    public static void checkUsernameBeforeSaveConnection4Sqlite(Connection connection) {
        if (ConnectionUtils.isSqlite(connection)) {
            String username = JavaSqlFactory.getUsername(connection);
            if (username == null || "".equals(username)) { //$NON-NLS-1$
                ConnectionHelper.setUsername(connection, JavaSqlFactory.DEFAULT_USERNAME);
            }
        }
    }

    /**
     * 
     * Get connection from data manager.
     * 
     * @param datamanager
     * @return
     */
    public static Connection getConnectionFromDatamanager(DataManager datamanager) {
        if (datamanager != null && datamanager.eIsProxy()) {
            datamanager = (DataManager) EObjectHelper.resolveObject(datamanager);
        }
        Connection analysisDataProvider = SwitchHelpers.CONNECTION_SWITCH.doSwitch(datamanager);
        return analysisDataProvider;

    }

    /**
     * Updata DB_PRODUCT tagged values for connection item in case they are not present in current file.
     * 
     * @throws SQLException
     */
    public static synchronized void updataTaggedValueForConnectionItem(Connection dataProvider) {
        if (dataProvider instanceof DatabaseConnection
                && StringUtils.isBlank(TaggedValueHelper.getValueString(TaggedValueHelper.DB_PRODUCT_NAME, dataProvider))) {
            Property property = PropertyHelper.getProperty(dataProvider);
            if (property != null) {
                Item item = property.getItem();
                if (item != null) {
                    DatabaseConnection dbConn = (DatabaseConnection) dataProvider;
                    IMetadataConnection metaConnection = ConvertionHelper.convert(dbConn);
                    dbConn = (DatabaseConnection) MetadataFillFactory.getDBInstance(dataProvider).fillUIConnParams(
                            metaConnection, dbConn);
                    if (dbConn != null && Platform.isRunning()) {
                        try {
                            ProxyRepositoryFactory.getInstance().save(item);
                        } catch (PersistenceException e) {
                            ExceptionHandler.process(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * get the database product version.
     * 
     * @param connection
     * @return
     */
    public static ProductVersion getDatabaseVersion(IMetadataConnection connection) {
        ProductVersion version = null;

        Connection conn = (Connection) connection.getCurrentConnection();
        Properties props = new Properties();
        String userName = JavaSqlFactory.getUsername(conn);
        String password = JavaSqlFactory.getPassword(conn);
        props.put(TaggedValueHelper.USER, userName);
        props.put(TaggedValueHelper.PASSWORD, password);
        String dbType = connection.getDbType();
        if (dbType != null) {
            props.put(TaggedValueHelper.DBTYPE, dbType);
        }

        java.sql.Connection createConnection = null;
        try {
            List list = ExtractMetaDataUtils.getInstance().connect(dbType, connection.getUrl(), userName, password,
                    connection.getDriverClass(), connection.getDriverJarPath(), connection.getDbVersionString(),
                    connection.getAdditionalParams());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof java.sql.Connection) {
                    createConnection = (java.sql.Connection) list.get(i);
                    break;
                }
            }
            if (createConnection != null) {
                if (createConnection.getMetaData() != null) {
                    String temp = createConnection.getMetaData().getDatabaseProductVersion();
                    if (temp != null) {
                        version = ProductVersion.fromString(temp);
                    }

                    if (version == null) {
                        version = ProductVersion.fromString(createConnection.getMetaData().getDatabaseMajorVersion() + "." //$NON-NLS-1$
                                + createConnection.getMetaData().getDatabaseMinorVersion() + ".0"); //$NON-NLS-1$
                    }
                }
            }

        } catch (Exception e) {
            ExceptionHandler.process(e);
        } finally {
            if (createConnection != null) {
                try {
                    createConnection.close();
                } catch (SQLException e) {
                    ExceptionHandler.process(e);
                }
            }
        }

        if (version == null) {
            version = ProductVersion.fromString("0.0.0"); //$NON-NLS-1$
        }

        return version;
    }

    /**
     * find driver jar path from 'temp\dbWizard',if nof found,find it from 'lib\java' and "librariesIndex.xml".
     * 
     * @return
     * @throws MalformedURLException
     */
    public static LinkedList<String> getDriverJarRealPaths(List<String> driverJarNameList) throws MalformedURLException {
        LinkedList<String> linkedList = new LinkedList<String>();
        boolean jarNotFound = false;

        for (String jarName : driverJarNameList) {
            String tempLibPath = ExtractMetaDataUtils.getInstance().getJavaLibPath();
            File tempFolder = new File(tempLibPath);
            if (tempFolder.exists()) {
                List<File> jarFiles = FilesUtils.getJarFilesFromFolder(tempFolder, jarName);
                if (!jarFiles.isEmpty()) {
                    linkedList.add(jarFiles.get(0).getPath());
                    continue;
                }
            }
            if (GlobalServiceRegister.getDefault().isServiceRegistered(ILibraryManagerService.class)) {
                ILibraryManagerService libManagerServic = (ILibraryManagerService) GlobalServiceRegister.getDefault().getService(
                        ILibraryManagerService.class);
                String libPath = libManagerServic.getJarPath(jarName);
                if (libPath == null) {
                    jarNotFound = true;
                    break;
                }
                linkedList.add(libPath);
            } else {
                jarNotFound = true;
            }
        }
        // if has one jar file not be found,return a empty list
        if (jarNotFound) {
            linkedList.clear();
        }

        return linkedList;
    }

}
