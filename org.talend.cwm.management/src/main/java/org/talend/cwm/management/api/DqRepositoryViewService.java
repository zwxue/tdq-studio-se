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
package org.talend.cwm.management.api;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.builders.AbstractTableBuilder;
import org.talend.cwm.builders.ColumnBuilder;
import org.talend.cwm.builders.TableBuilder;
import org.talend.cwm.builders.ViewBuilder;
import org.talend.cwm.builders.XMLSchemaBuilder;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.management.connection.JavaSqlFactory;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.RelationalPackage;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.cwm.xml.TdXMLContent;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.DomainPackage;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.helper.NeedSaveDataProviderHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.utils.string.AsciiUtils;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.NamedColumnSet;
import orgomg.cwm.resource.relational.PrimaryKey;
import orgomg.cwm.resource.relational.Schema;

/**
 * @author scorreia
 * 
 * Services for the DQ Repository view.
 */
public final class DqRepositoryViewService {

    private static Logger log = Logger.getLogger(DqRepositoryViewService.class);

    private static final SimpleDateFormat SMPL_DATE_FMT = new SimpleDateFormat("yyyyMMddhhmmss"); //$NON-NLS-1$

    /**
     * if true, the catalogs (and schemas) are stored in the same file as the data provider. Used for tests only.
     * 
     * TODO scorreia (saving catalog outside data provider's file) set it to false for big databases.
     * 
     * In case when optimization is needed: set this boolean to false and correct code so that everything works as
     * before (DQ Repository view must not show catalog's files and Catalogs must still be children of the Data
     * provider). Check also that old files (.prv) are still readable by the application.
     */

    private DqRepositoryViewService() {
    }

    /**
     * Filter on the files with extension meaning data provider.
     */
    public static final FilenameFilter PRV_FILTER = new FilenameFilter() {

        public boolean accept(File dir, String name) {
            // TODO check to path (in metadata)?
            return name != null && name.endsWith(FactoriesUtil.PROV);
        }
    };

    private static final String CHARS_TO_REMOVE = "/"; //$NON-NLS-1$

    private static final String REPLACEMENT_CHARS = "_"; //$NON-NLS-1$

    /**
     * Method "createTechnicalName" creates a technical name used for file system storage. MOD mzhao make this method as
     * public access.
     * 
     * @param functionalName the user friendly name
     * @return the technical name created from the user given name.
     */
    public static String createTechnicalName(final String functionalName) {
        String techname = "no_name"; //$NON-NLS-1$
        if (functionalName == null) {
            log.warn("A functional name should not be null");
            return techname;
        }
        // encode in base 64 so that all characters such white spaces, accents, everything that is dangerous when used
        // for file names are removed
        try {
            // encode
            String b64 = new String(Base64.encodeBase64(functionalName.getBytes()), "UTF-8"); //$NON-NLS-1$
            // replace special characters
            String date = SMPL_DATE_FMT.format(new Date(System.currentTimeMillis()));
            techname = AsciiUtils.replaceCharacters(b64, CHARS_TO_REMOVE, REPLACEMENT_CHARS) + date;
        } catch (UnsupportedEncodingException e) {
            log.error(e, e);
        } // .replaceAll(B64ID, PREFIX);
        if (log.isDebugEnabled()) {
            log.debug("Functional name: " + functionalName + " -> techname: " + techname);
        }
        return techname;
    }

    /**
     * Method "listTdDataProviders" list all the connections in the given folder.
     * 
     * @param folder the path to the folder containing TdDataProviders
     * @param containSubFolders if it contains all sub folders.
     * @return the list of all TdDataProviders in the folder (never null).
     */
    public static List<TdDataProvider> listTdDataProviders(IFolder folder, boolean containSubFolders) {
        ArrayList<TdDataProvider> providers = new ArrayList<TdDataProvider>();
        IResource[] members = null;
        try {
            members = folder.members();
        } catch (CoreException e) {
            log.error(e, e);
            return new ArrayList<TdDataProvider>();
        }
        for (IResource res : members) {
            if ((res instanceof IFile) && (FactoriesUtil.PROV.equals(res.getFileExtension()))) {

                TypedReturnCode<TdDataProvider> rc = readFromFile((IFile) res);
                if (rc.isOk()) {
                    TdDataProvider dataProvider = rc.getObject();
                    providers.add(dataProvider);
                } else {
                    log.warn(rc.getMessage());
                }
            } else {
                if (containSubFolders && (res instanceof IFolder)) {
                    providers.addAll(listTdDataProviders((IFolder) res, containSubFolders));
                }
            }
        }
        return providers;
    }

    /**
     * Method "refreshDataProvider" reload database structure. Existing elements (catalogs, tables...) must not be
     * replaced by new elements. Only their content must be updated because these elements can be refered to by
     * analysis.
     * 
     * @param dataProvider
     * @param catalogPattern the catalogs to load (can be null, meaning all are loaded)
     * @param schemaPattern the schema to load (can be null, meaning all are loaded)
     * @return true if the catalog have been reload
     */
    public static boolean refreshDataProvider(TdDataProvider dataProvider, String catalogPattern, String schemaPattern) {
        // TODO scorreia implement me
        return false;
    }

    /**
     * DOC scorreia Comment method "refreshTables".
     * 
     * @param schema
     * @param tablePattern
     * @return
     */
    public static boolean refreshTables(Package schema, String tablePattern) {
        // TODO scorreia implement me
        return false;
    }

    /**
     * DOC scorreia Comment method "refreshViews".
     * 
     * @param schema
     * @param viewPattern
     * @return
     */
    public static boolean refreshViews(Package schema, String viewPattern) {
        // TODO scorreia implement me
        return false;
    }

    /**
     * DOC scorreia Comment method "refreshColumns".
     * 
     * @param table
     * @param columnPattern
     * @return
     */
    public static boolean refreshColumns(ColumnSet table, String columnPattern) {
        // TODO scorreia implement me
        return false;
    }

    /**
     * Method "getTables" loads the tables from the database or get the tables from the catalog .
     * 
     * @param dataProvider the data provider
     * @param catalog the catalog (must not be null)
     * @param tablePattern the pattern of the tables to be loaded (from the DB)
     * @param loadFromDB true if we want to load tables from the database. false when the tables are already in the
     * catalog.
     * @return the list of tables. Theses tables are not added to the given catalog. It must be done by the caller.
     * @throws TalendException
     */
    public static List<TdTable> getTables(TdDataProvider dataProvider, Catalog catalog, String tablePattern, boolean loadFromDB)
            throws TalendException {
        if (loadFromDB) {
            return loadTables(dataProvider, catalog, tablePattern);
        } else {
            return CatalogHelper.getTables(catalog);
        }
    }

    public static List<TdTable> getTables(TdDataProvider dataProvider, Schema schema, String tablePattern, boolean loadFromDB)
            throws TalendException {
        if (loadFromDB) {
            final TdCatalog parentCatalog = CatalogHelper.getParentCatalog(schema);
            return loadTables(dataProvider, parentCatalog, schema, tablePattern);
        } else {
            return SchemaHelper.getTables(schema);
        }
    }

    public static List<TdView> getViews(TdDataProvider dataProvider, Catalog catalog, String viewPattern, boolean loadFromDB)
            throws TalendException {
        if (loadFromDB) {
            return loadViews(dataProvider, catalog, viewPattern);
        } else {
            return CatalogHelper.getViews(catalog);
        }
    }

    public static List<TdView> getViews(TdDataProvider dataProvider, Schema schema, String viewPattern, boolean loadFromDB)
            throws TalendException {
        if (loadFromDB) {
            // get catalog is exists
            final TdCatalog parentCatalog = CatalogHelper.getParentCatalog(schema);
            return loadViews(dataProvider, parentCatalog, schema, viewPattern);
        } else {
            return SchemaHelper.getViews(schema);
        }
    }

    /**
     * Method "getColumns". The link between the column set and its columns is set in this method when required.
     * 
     * @param dataProvider the data provider for connecting to database (can be null when the columns are not loaded
     * from the database)
     * @param columnSet a column set (Table or View)
     * @param columnPattern the pattern of the columns to get (can be null)
     * @param loadFromDB true if columns must be loaded from the database
     * @return
     * @throws TalendException
     */
    public static List<TdColumn> getColumns(TdDataProvider dataProvider, ColumnSet columnSet, String columnPattern,
            boolean loadFromDB) throws TalendException {
        if (loadFromDB) {
            return loadColumns(dataProvider, columnSet, columnPattern);
        } else {
            return ColumnSetHelper.getColumns(columnSet);
        }
    }

    /**
     * DOC scorreia Comment method "saveSoftwareSystem".
     * 
     * @param util
     * @param softwareSystem
     */
    public static boolean saveSoftwareSystem(TdSoftwareSystem softwareSystem) {
        EMFSharedResources util = EMFSharedResources.getInstance();
        return util.saveSoftwareDeploymentResource(softwareSystem);
    }

    /**
     * Method "saveOpenDataProvider" saves a Data provider which has already a resource (has already been saved once).
     * 
     * @param dataProvider
     * @param addPackage decide whether need to add the Package(catalog/schema) element to dataprovider.
     * @return
     */
    public static ReturnCode saveOpenDataProvider(TdDataProvider dataProvider, boolean addPackage) {
        assert dataProvider != null;

        Resource resource = dataProvider.eResource();
        if (addPackage) {
            // MOD zshen bug 10633: Reload Database List can't display new Schema in DQ Repository view(Oracle Database)
            Collection<? extends ModelElement> catalogsorSchemas = DataProviderHelper.getTdCatalogs(dataProvider);
            if (catalogsorSchemas.size() == 0) {
                catalogsorSchemas = DataProviderHelper.getTdSchema(dataProvider);
            }
            resource.getContents().addAll(catalogsorSchemas);
        }

        ReturnCode rc = new ReturnCode();
        if (resource == null) {
            rc.setReturnCode(Messages.getString("DqRepositoryViewService.DataProviderSaveFirst",//$NON-NLS-1$
                    dataProvider.getName()), false);
        } else {
            // add by hcheng
            // MOD scorreia 2009-01-09 password decryption is handled elsewhere
            // PasswordHelper.encryptResource(resource);
            rc.setOk(EMFUtil.saveResource(resource));
            // MOD scorreia 2009-01-09 password decryption is handled elsewhere
            // PasswordHelper.decryptResource(resource);
        }
        return rc;
    }

    private static String getName(ModelElement element) {
        return element != null ? element.getName() : null;
    }

    /**
     * @deprecated do not use this method. It is used for test purpose only.
     * 
     * Method "saveDomain".
     * @param domain
     * @param folderProvider
     * @return
     */
    public static boolean saveDomain(Domain domain, FolderProvider folderProvider) {
        assert domain != null;

        String folder = ((folderProvider != null) && folderProvider.getFolder() != null) ? folderProvider.getFolder()
                .getAbsolutePath() : null;
        if (folder == null) { // do not serialize data
            if (log.isInfoEnabled()) {
                log.info("Domain not serialized: no folder given.");
            }
            return false;
        }

        String filename = folderProvider.getFolder() + File.separator + createFilename(domain.getName(), DomainPackage.eNAME);
        EMFUtil util = new EMFUtil();
        util.setUsePlatformRelativePath(false);
        util.addPoolToResourceSet(new File(filename), domain);

        return util.save();
    }

    /**
     * DOC bZhou Comment method "createLogicalFileNmae".
     * 
     * @param element
     * @param extension
     * @return
     */
    public static String createLogicalFileName(ModelElement element, String extension) {
        return createTechnicalName(element.getName()) + "_" + MetadataHelper.getVersion(element) + "." + extension;
    }

    /**
     * Method "createFilename".
     * 
     * @param folder the folder
     * @param basename the filename without extension
     * @param extension the extension of the file
     * @return the path "folder/basename.extension"
     */
    public static String createFilename(String basename, String extension) {
        return createTechnicalName(basename) + "." + extension;
    }

    /**
     * Method "loadTables".
     * 
     * @param dataProvider
     * @param catalog (must not be null)
     * @param tablePattern
     * @return the list of tables matching the given pattern
     * @throws TalendException
     */
    private static List<TdTable> loadTables(TdDataProvider dataProvider, Catalog catalog, String tablePattern)
            throws TalendException {
        List<TdTable> tables = new ArrayList<TdTable>();
        assert dataProvider != null;
        assert catalog != null;

        String catalogName = catalog.getName();
        if (catalogName == null) {
            log.error("No catalog given. Cannot retrieve tables!");
            return tables;
        }
        return loadTables(dataProvider, catalog, null, tablePattern);
    }

    private static List<TdTable> loadTables(TdDataProvider dataProvider, Catalog catalog, Schema schema, String tablePattern)
            throws TalendException {
        List<TdTable> tables = new ArrayList<TdTable>();
        // PTODO scorreia check return code
        loadColumnSets(dataProvider, catalog, schema, tablePattern, RelationalPackage.TD_TABLE, tables);
        return tables;
    }

    private static List<TdView> loadViews(TdDataProvider dataProvider, Catalog catalog, Schema schema, String viewPattern)
            throws TalendException {
        assert schema != null : Messages.getString("DqRepositoryViewService.NoSchemaGiven"); //$NON-NLS-1$
        List<TdView> views = new ArrayList<TdView>();
        // PTODO scorreia check return code
        loadColumnSets(dataProvider, catalog, schema, viewPattern, RelationalPackage.TD_VIEW, views);
        return views;
    }

    /**
     * DOC scorreia Comment method "loadViews".
     * 
     * @param dataProvider
     * @param catalog
     * @param viewPattern
     * @return
     * @throws TalendException
     */
    private static List<TdView> loadViews(TdDataProvider dataProvider, Catalog catalog, String viewPattern)
            throws TalendException {
        assert catalog != null : Messages.getString("DqRepositoryViewService.NoCatalogGiven"); //$NON-NLS-1$
        List<TdView> views = new ArrayList<TdView>();
        // PTODO scorreia check return code
        loadColumnSets(dataProvider, catalog, null, viewPattern, RelationalPackage.TD_VIEW, views);
        return views;
    }

    private static List<TdColumn> loadColumns(TdDataProvider dataProvider, ColumnSet table, String columnPattern)
            throws TalendException {
        assert table != null;
        List<TdColumn> columns = new ArrayList<TdColumn>();
        TypedReturnCode<Connection> rcConn = JavaSqlFactory.createConnection(dataProvider);
        if (!rcConn.isOk()) {
            log.error(rcConn.getMessage()); // scorreia show error to the user
            throw new TalendException(rcConn.getMessage());
        }
        Connection connection = rcConn.getObject();
        ColumnBuilder colBuilder = new ColumnBuilder(connection);

        String catalogName = getName(CatalogHelper.getParentCatalog(table));
        TdSchema schema = SchemaHelper.getParentSchema(table);
        if (catalogName == null && schema != null) {
            catalogName = getName(CatalogHelper.getParentCatalog(schema));
        }
        String schemaPattern = getName(schema);
        String tablePattern = getName(table);

        try {
            columns = colBuilder.getColumns(catalogName, schemaPattern, tablePattern, columnPattern);
            ColumnSetHelper.addColumns(table, columns);
            // MOD scorreia 2009-01-29 get primary keys of the table
            if (orgomg.cwm.resource.relational.RelationalPackage.eINSTANCE.getTable().isSuperTypeOf(table.eClass())) {
                TableBuilder tableBuild = new TableBuilder(connection);
                // link PKs to Columns
                List<PrimaryKey> primaryKeys = tableBuild.getPrimaryKeys(catalogName, schemaPattern, tablePattern);
                TableHelper.addPrimaryKeys(table, primaryKeys);
                List<ForeignKey> foreignKeys = tableBuild.getForeignKeys(catalogName, schemaPattern, tablePattern);
                TableHelper.addForeignKeys(table, foreignKeys);
                for (TdColumn tdColumn : columns) {
                    String colname = tdColumn.getName();
                    PrimaryKey primaryKey = tableBuild.getPrimaryKey(colname);
                    if (primaryKey != null) {
                        tdColumn.getUniqueKey().add(primaryKey);
                    }
                    ForeignKey foreignKey = tableBuild.getForeignKey(colname);
                    if (foreignKey != null) {
                        tdColumn.getKeyRelationship().add(foreignKey);
                    }
                    // TODO scorreia should create relationship between FK and PK
                }
            }
        } catch (SQLException e) {
            log.error(e, e);
            throw new TalendException(e);
        } finally {
            ConnectionUtils.closeConnection(connection);
        }
        return columns;
    }

    /**
     * Method "loadColumnSets".
     * 
     * @param dataProvider
     * @param catalog (can be null)
     * @param schema (can be null)
     * @param tablePattern (can be null)
     * @param classifierID a either {@link RelationalPackage#TD_TABLE} or {@link RelationalPackage#TD_VIEW}
     * @param tables the list of tables or views to be loaded.
     * @return true if ok
     * @throws TalendException
     */
    private static <T extends List<? extends NamedColumnSet>> boolean loadColumnSets(TdDataProvider dataProvider,
            Catalog catalog, Schema schema, String tablePattern, int classifierID, final T tables) throws TalendException {
        boolean ok = false;
        TypedReturnCode<Connection> rcConn = JavaSqlFactory.createConnection(dataProvider);
        if (!rcConn.isOk()) {
            log.error(rcConn.getMessage());
            throw new TalendException(rcConn.getMessage());
        }

        Connection connection = rcConn.getObject();

        String schemaName = (schema == null) ? null : schema.getName();
        String catalogName = (catalog == null) ? null : catalog.getName();
        try {
            AbstractTableBuilder<? extends NamedColumnSet> tableBuilder = getBuilder(connection, classifierID);
            // tableBuilder.setColumnsRequested(true);
            List loadedTables = tableBuilder.getColumnSets(catalogName, schemaName, tablePattern);
            tables.addAll(loadedTables);
            ok = true;
        } catch (SQLException e) {
            log.error(e);
            ok = false;
        } finally {
            if (connection != null) {
                ConnectionUtils.closeConnection(connection);
            }
        }
        return ok;
    }

    /**
     * DOC scorreia Comment method "getBuilder".
     * 
     * @param connection
     * @param classifierID
     * @return
     */
    private static AbstractTableBuilder<? extends NamedColumnSet> getBuilder(Connection connection, int classifierID) {
        switch (classifierID) {
        case RelationalPackage.TD_TABLE:
            return new TableBuilder(connection);
        case RelationalPackage.TD_VIEW:
            return new ViewBuilder(connection);
        default:
            return null;
        }
    }

    /**
     * Method "readFromFile".
     * 
     * @param file the file to read
     * @return the Data provider if found.
     */
    public static TypedReturnCode<TdDataProvider> readFromFile(IFile file) {
        TypedReturnCode<TdDataProvider> rc = new TypedReturnCode<TdDataProvider>();
        Resource r = EMFSharedResources.getInstance().getResource(
                URI.createPlatformResourceURI(file.getFullPath().toString(), false), true);
        Collection<TdDataProvider> tdDataProviders = DataProviderHelper.getTdDataProviders(r.getContents());
        if (tdDataProviders.isEmpty()) {
            rc.setReturnCode(
                    Messages.getString("DqRepositoryViewService.NoDataProviderFound", file.getFullPath().toString()), false); //$NON-NLS-1$
        }
        if (tdDataProviders.size() > 1) {
            rc.setReturnCode(Messages.getString("DqRepositoryViewService.FoundTooManyDataProvider", tdDataProviders.size(), //$NON-NLS-1$
                    file.getFullPath().toString()), false);
        }
        TdDataProvider prov = tdDataProviders.iterator().next();
        rc.setObject(prov);
        return rc;
    }

    /**
     * DOC bZhou Comment method "buildElementName".
     * 
     * @param element
     * @return
     */
    public static String buildElementName(ModelElement element) {
        return element.getName() + " " + MetadataHelper.getVersion(element);
    }

    public static List<ModelElement> getXMLElements(TdXMLDocument document) {
        List<ModelElement> elements = document.getOwnedElement();
        // Load from dababase
        if (elements == null || elements.size() == 0) {
            XMLSchemaBuilder xmlScheBuilder = XMLSchemaBuilder.getSchemaBuilder(document);
            elements = xmlScheBuilder.getRootElements(document);
            document.getOwnedElement().addAll(elements);
            TdDataProvider dataManager = (TdDataProvider) document.getDataManager().get(0);
            NeedSaveDataProviderHelper.register(dataManager.eResource().getURI().path(), dataManager);
            NeedSaveDataProviderHelper.saveAllDataProvider();
        }
        return elements;
    }

    public static List<TdXMLElement> getXMLElements(TdXMLElement element) {
        TdXMLContent xmlContent = element.getXmlContent();
        List<TdXMLElement> elements = null;
        // Load from dababase
        if (xmlContent == null) {
            XMLSchemaBuilder xmlScheBuilder = XMLSchemaBuilder.getSchemaBuilder(element.getOwnedDocument());
            elements = xmlScheBuilder.getChildren(element);
            TdDataProvider dataManager = (TdDataProvider) element.getOwnedDocument().getDataManager().get(0);
            NeedSaveDataProviderHelper.register(dataManager.eResource().getURI().path(), dataManager);
            NeedSaveDataProviderHelper.saveAllDataProvider();
        } else {
            elements = xmlContent.getXmlElements();
        }
        return elements;
    }

    public static Boolean hasChildren(TdXMLElement element) {
        XMLSchemaBuilder xmlScheBuilder = XMLSchemaBuilder.getSchemaBuilder(element.getOwnedDocument());
        return xmlScheBuilder.isLeafNode(element).isOk();
    }
}
