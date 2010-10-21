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
package org.talend.cwm.db.connection;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.EMFUtil;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.database.conn.version.EDatabaseVersion4Drivers;
import org.talend.core.model.metadata.MetadataTalendType;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.database.EDatabaseSchemaOrCatalogMapping;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.cwm.management.connection.DatabaseContentRetriever;
import org.talend.cwm.management.connection.JavaSqlFactory;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import org.talend.utils.time.TimeTracer;
import orgomg.cwm.objectmodel.core.Classifier;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;
import orgomg.cwm.resource.relational.impl.CatalogImpl;

/**
 * @author scorreia
 * 
 * Factory to create CWM classes from a DBConnect object.
 */
public final class TalendCwmFactory {

    private static final Class<TalendCwmFactory> THAT = TalendCwmFactory.class;

    private static Logger log = Logger.getLogger(THAT);

    private TalendCwmFactory() {
    }

    /**
     * Method "initializeConnection" initializes objects, close connection and serializes objects. (Not for public
     * usage.)
     * 
     * @param connector
     * @param folderProvider
     * @throws SQLException
     * @throws TalendException
     */
    static void initializeConnection(DBConnect connector, FolderProvider folderProvider) throws SQLException, TalendException {
        Connection dataProvider = createDataProvider(connector);

        // --- close connection now
        connector.closeConnection();

        // --- save on disk
        ElementWriterFactory.getInstance().createDataProviderWriter().create(dataProvider, folderProvider.getFolderResource());
    }

    /**
     * Method "createDataProvider" create the data provider, the catalogs and the schemas. The created data provider and
     * its related Catalog and Schemas are stored in the DBConnect class. In order to finally serialize them in a file,
     * the method {@link DBConnect#saveInFiles()} must be called.
     * 
     * @param connector the helper for building CWM objects from a connection
     * @param folderProvider contains the path where the file will be stored.
     * @return the data provider
     * @throws SQLException
     * @throws TalendException
     */
    public static Connection createDataProvider(DBConnect connector) throws SQLException, TalendException {
        // --- connect and check the connection
        checkConnection(connector);

        // --- get data provider
        Connection dataProvider = getTdDataProvider(connector);
        // --- get the connection provider
        // MOD mzhao feature 10814 , 2010-05-26
        // TdProviderConnection providerConnection = connector.getDataProvider();
        // --- get software system
        if (connector.retrieveDeployedSystemInformations()) {
            TdSoftwareSystem softwareSystem = connector.getSoftwareSystem();
            if (softwareSystem != null) {
                ConnectionHelper.setSoftwareSystem(dataProvider, softwareSystem);
            }
        }

        // --- get database structure informations
        Collection<Catalog> catalogs = getCatalogs(connector);
        Collection<Schema> schemata = getSchemata(connector);

        // --- link everything
        // DataProviderHelper.addProviderConnection(providerConnection, dataProvider);
        boolean allAdded = false;
        // TODO scorreia probably add only when catalogs is empty.
        String dbType = connector.getDbConnectionParameter().getSqlTypeName();
        if (catalogs.isEmpty()) {
            allAdded = ConnectionHelper.addSchemas(schemata, dataProvider);
            if (log.isDebugEnabled()) {
                log.debug("all " + schemata.size() + " schemata added: " + allAdded);
            }
        } else {
            allAdded = ConnectionHelper.addCatalogs(catalogs, dataProvider);
            if (log.isDebugEnabled()) {
                log.debug("all " + catalogs.size() + " catalogs added: " + allAdded);
            }
        }

        if (log.isInfoEnabled()) {
            log.info(catalogs.size() + " catalog(s) loaded from database");
            log.info(schemata.size() + " schema(s) loaded from database");
            // MOD klliu if sechema
            if (schemata.size() == 0 && SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getLanguage().equals(dbType)) {
                throw new TalendException("Please check the otherParameter which didn't be found!");
            }
        }
        // --- print some informations
        if (log.isDebugEnabled()) {
            printInformations(catalogs, schemata);
        }
        if (!ReponsitoryContextBridge.isDefautProject()) {
            // String dbType = connector.getDbConnectionParameter().getSqlTypeName();
            EDatabaseTypeName edatabasetypeInstance = EDatabaseTypeName.getTypeFromDisplayName(dbType);
            String product = edatabasetypeInstance.getProduct();
            // AdditionalParams
            String additionalParams = ((DatabaseConnection) dataProvider).getAdditionalParams();
            if (additionalParams == null) {
                ((DatabaseConnection) dataProvider).setAdditionalParams(connector.getDbConnectionParameter().getaDDParameter());
            }
            // uischema
            if (edatabasetypeInstance.isNeedSchema()) {
                if (schemata.size() == 0 && catalogs.size() > 0) {
                    schemata = CatalogHelper.getSchemas(SwitchHelpers.CATALOG_SWITCH
                            .doSwitch((CatalogImpl) catalogs.toArray()[0]));
                }

                if (edatabasetypeInstance.getSchemaMappingField() == EDatabaseSchemaOrCatalogMapping.Schema
                        && schemata.size() > 0) {
                    Iterator<Schema> iter = schemata.iterator();
                    while (iter.hasNext()) {
                        String uischema = iter.next().getName();
                        ((DatabaseConnection) dataProvider).setUiSchema(uischema);
                        break;
                    }

                }
            }

            // change catalog
            String dbname = connector.getDbConnectionParameter().getDbName();
            if ((dbname == null || "".equals(dbname)) && catalogs != null) {
                Iterator<Catalog> iter = catalogs.iterator();
                while (iter.hasNext()) {
                    String sid = iter.next().getName();
                    ((DatabaseConnection) dataProvider).setSID(sid);
                    connector.getDbConnectionParameter().setDbName(sid);
                    break;
                }
            }
            // set DbmsId and setProductId
            String mapping = MetadataTalendType.getDefaultDbmsFromProduct(product).getId();
            if (dataProvider instanceof DatabaseConnection) {
                // add the attribute of productID for TOS
                ((DatabaseConnection) dataProvider).setProductId(product);
                // add the attribute of DbmsId for TOS
                ((DatabaseConnection) dataProvider).setDbmsId(mapping);
                // add the attribute of DbVersion for TOS
                int versionNum = connector.getConnection().getMetaData().getDatabaseMajorVersion();
                String connectionDbType = ((DatabaseConnection) dataProvider).getDatabaseType();
                if (connectionDbType == null) {
                    connectionDbType = connector.getDbConnectionParameter().getSqlTypeName();
                    ((DatabaseConnection) dataProvider).setDatabaseType(connectionDbType);
                }
                List<EDatabaseVersion4Drivers> dbTypeList = EDatabaseVersion4Drivers.indexOfByDbType(connectionDbType);
                if (dbTypeList.size() == 1) {
                    ((DatabaseConnection) dataProvider).setDbVersionString(dbTypeList.get(0).getVersionValue());
                } else if (dbTypeList.size() > 1) {
                    for (EDatabaseVersion4Drivers eDatabaseVersion : dbTypeList) {
                        String[] strArray = eDatabaseVersion.getVersionValue().split("_");
                        if (strArray.length > 1 && strArray[1].startsWith(Integer.toString(versionNum))) {
                            ((DatabaseConnection) dataProvider).setDbVersionString(eDatabaseVersion.getVersionValue());
                            break;
                        }
                    }
                }
            }
        }
        return dataProvider;
    }

    /**
     * Instantiate a data provider from xml documents. DOC mzhao Comment method "getTdDataProvider".
     * 
     * @param parameter
     * @return
     */
    public static DatabaseConnection createEXistTdDataProvider(DBConnectionParameter parameter) {
        IXMLDBConnection xmlDBConnection = new EXistXMLDBConnection(parameter.getDriverClassName(), parameter.getJdbcUrl());
        ReturnCode rt = xmlDBConnection.checkDatabaseConnection();
        if (rt.isOk()) {
            DatabaseConnection dataProvider = ConnectionHelper.createDatabaseConnection(parameter.getName());
            xmlDBConnection.setSofewareSystem(dataProvider, parameter);
            xmlDBConnection.setProviderConnection(dataProvider, parameter);
            DataProviderHelper.addXMLDocuments(xmlDBConnection.createConnection(), dataProvider);
            return dataProvider;
        }
        return null;
    }

    /**
     * Instantiate a data provider from mdm service. DOC xqliu feature 10238.
     * 
     * @param parameter
     * @return
     */
    public static MDMConnection createMdmTdDataProvider(DBConnectionParameter parameter) {
        MdmWebserviceConnection mdmConnection = new MdmWebserviceConnection(parameter.getJdbcUrl(), parameter.getParameters());
        ReturnCode rt = mdmConnection.checkDatabaseConnection();
        if (rt.isOk()) {
            MDMConnection dataProvider = DataProviderHelper.createMDMConnection(parameter.getName());
            mdmConnection.setSofewareSystem(dataProvider, parameter);
            mdmConnection.setProviderConnection(dataProvider, parameter);
            DataProviderHelper.addXMLDocuments(mdmConnection.createConnection(), dataProvider);
            dataProvider.setUsername(mdmConnection.getUserName());
            dataProvider.setPassword(mdmConnection.getUserPass());
            // MOD qiongli bug 14469:if don't setLable,it will run ConnectionUtils.fillMdmConnectionInformation(mdmConn)
            // and create redundant folder under folder '.xsd'.
            dataProvider.setLabel(parameter.getName());
            return dataProvider;
        }
        return null;
    }

    /**
     * Method "getTdDataProvider" simply tries to instantiate a data provider from the given connection. The connector
     * should have already open its connection. If not, this method tries to open a connection. The caller should close
     * the connection.
     * 
     * @param connector the database connector
     * @return the DataProvider for which the name is null. The data provider does not contain structure.
     * @throws SQLException
     */
    public static Connection getTdDataProvider(DBConnect connector) throws SQLException {
        checkConnection(connector);
        boolean driverInfoRetrieved = connector.retrieveDriverInformations();
        if (!driverInfoRetrieved) {
            log.error("Could not retrieve the driver informations");
            return null; // stop here
        }

        return connector.getDataProvider();
    }

    /**
     * Method "getCatalogs". the connector should have already open its connection. If not, this method tries to open a
     * connection. The caller should close the connection.
     * 
     * @param connector the database connector
     * @return the catalogs (never null but could be empty depending on the database type)
     * @throws SQLException
     */
    public static Collection<Catalog> getCatalogs(DBConnect connector) throws SQLException {
        checkConnection(connector);
        boolean dbStructureRetrieved = connector.retrieveDatabaseStructure();
        if (!dbStructureRetrieved) {
            log.error("Could not retrieve the database structure");
            return Collections.emptyList();
        }
        return connector.getCatalogs();
    }

    /**
     * Method "getSchemata". the connector should have already open its connection. If not, this method tries to open a
     * connection. The caller should close the connection.
     * 
     * @param connector the database connector
     * @return the schemas (never null but could be empty depending on the database type)
     * @throws SQLException
     */
    public static Collection<Schema> getSchemata(DBConnect connector) throws SQLException {
        checkConnection(connector);
        boolean dbStructureRetrieved = connector.retrieveDatabaseStructure();
        if (!dbStructureRetrieved) {
            log.error("Could not retrieve the database structure");
            return Collections.emptyList();
        }
        return connector.getSchemata();
    }

    private static String getDriverClassName(DBConnectionParameter connectionParams) {
        // TODO scorreia create the utility class for this
        return null;
    }

    // private static void addInRelationResourceSet(String folder, DBConnect connector,
    // Collection<? extends ModelElement> elements) {
    // for (ModelElement elt : elements) {
    // addInResourceSet(folder, connector, elt, RelationalPackage.eNAME);
    // }
    // }

    // private static void addInSoftwareSystemResourceSet(String folder, DBConnect connector, ModelElement elt) {
    // addInResourceSet(folder, connector, elt, FactoriesUtil.PROV);
    // // ORIG addInResourceSet(folder, connector, elt, SoftwaredeploymentPackage.eNAME);
    // }

    // private static void addInResourceSet(String folder, DBConnect connector, ModelElement pack, String extension) {
    // if (pack != null) {
    // String filename = createFilename(folder, pack.getName(), extension);
    // connector.storeInResourceSet(pack, filename);
    // }
    // }

    /**
     * Method "checkConnection" checks whether the connection is open. If not, tries to connect.
     * 
     * @param connector
     * @throws SQLException
     */
    private static void checkConnection(DBConnect connector) throws SQLException {
        if (!connector.isConnected()) {
            boolean connected = connector.connect();
            if (!connected) {
                throw new SQLException(Messages.getString("TalendCwmFactory.ConnectionFailed", connector.getDatabaseUrl())); //$NON-NLS-1$
            }
        }
    }

    /**
     * Method "printInformations" only for test purposes.
     * 
     * @param catalogs
     * @param schemata
     */
    private static void printInformations(Collection<Catalog> catalogs, Collection<Schema> schemata) {
        for (Catalog catalog : catalogs) {
            System.out.println("Catalog = " + catalog); //$NON-NLS-1$
        }
        for (Schema schema : schemata) {
            System.out.println("Schema = " + schema + " in catalog " + schema.getNamespace()); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public static void main(String[] args) throws TalendException {

        // --- load connection parameters from properties file
        TypedProperties connectionParams = PropertiesLoader.getProperties(THAT, "db.properties"); //$NON-NLS-1$

        String driverClassName = connectionParams.getProperty("driver"); //$NON-NLS-1$
        String dbUrl = connectionParams.getProperty("url"); //$NON-NLS-1$

        DBConnect connector = new DBConnect(dbUrl, driverClassName, connectionParams);
        try {
            TimeTracer tt = new TimeTracer("DB CONNECT", log); //$NON-NLS-1$
            tt.start();

            // --- set where to save the files
            FolderProvider folderProvider = new FolderProvider();
            folderProvider.setFolder(new File("out")); //$NON-NLS-1$
            initializeConnection(connector, folderProvider);
            tt.end(Messages.getString("TalendCwmFactory.EverythingSaved")); //$NON-NLS-1$

            // --- now create the lower structure (tables, columns)
            // recreate a connection from the TdProviderConnection
            Connection providerConnection = connector.getDataProvider();
            TypedReturnCode<java.sql.Connection> rc = JavaSqlFactory.createConnection(connector.getDataProvider());
            if (!rc.isOk()) {
                log.error(rc.getMessage());
                return;
            }
            boolean ok = false;
            Collection<Catalog> catalogs = connector.getCatalogs();
            java.sql.Connection connection = rc.getObject();
            for (Catalog catalog : catalogs) {
                List<Schema> schemas = CatalogHelper.getSchemas(catalog);
                for (Schema schema : schemas) {
                    List<TdTable> tables = SchemaHelper.getTables(schema);
                    if (tables.isEmpty()) {
                        // TODO try to load them from DB.
                        List<TdTable> tablesWithAllColumns = DatabaseContentRetriever.getTablesWithColumns(catalog.getName(),
                                schema.getName(), null, connector.getDataProvider());
                        ok = SchemaHelper.addTables(tablesWithAllColumns, schema);
                    }
                }
                // first try to get the columns
                List<TdTable> tables = CatalogHelper.getTables(catalog);
                if (tables.isEmpty()) {
                    // TODO try to load them from DB.
                    List<TdTable> tablesWithAllColumns = DatabaseContentRetriever.getTablesWithColumns(catalog.getName(), null,
                            null, connector.getDataProvider());
                    ok = CatalogHelper.addTables(tablesWithAllColumns, catalog);

                    // --- get the resource of the catalog
                    Resource resource = catalog.eResource();
                    if (resource == null) {
                        log.error("Resource null");
                    }
                    // --- add column type to resource set
                    for (TdTable tdTable : tablesWithAllColumns) {
                        List<TdColumn> columns = TableHelper.getColumns(tdTable);
                        for (TdColumn tdColumn : columns) {
                            if (resource != null) {
                                Classifier type = tdColumn.getType();
                                if (type != null) {
                                    resource.getContents().add(type);
                                }
                            }

                        }
                    }

                }
            }
            if (!ok) {
                log.error("Tables not retrieved.");
            } else {
                log.info("table retrieved.");

            }

            connection.close();

            // --- save on disk

            EMFUtil util = new EMFUtil();
            ResourceSet resourceSet = providerConnection.eResource().getResourceSet();
            util.setResourceSet(resourceSet);
            util.save();
            // OLD code : connector.saveInFiles();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            log.error(e, e);
        }
    }
}
