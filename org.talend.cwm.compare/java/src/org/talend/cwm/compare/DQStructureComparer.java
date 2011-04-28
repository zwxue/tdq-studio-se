// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.compare;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.MetadataFillFactory;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.IUIHandler;
import org.talend.cwm.compare.i18n.DefaultMessagesImpl;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.helper.ParameterUtil;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public final class DQStructureComparer {

    public static final String COMPARE_FILE_EXTENSION = "comp"; //$NON-NLS-1$

    public static final String RESULT_EMFDIFF_FILE_EXTENSION = "emfdiff"; //$NON-NLS-1$

    private static final String NEED_RELOAD_ELEMENTS_PRV = ".needReloadElements.comp"; //$NON-NLS-1$

    private static final String RESULT_EMFDIFF_FILE = ".result.emfdiff"; //$NON-NLS-1$

    private static final String TEMP_REFRESH_FILE = ".refresh.comp"; //$NON-NLS-1$

    // ADD mzhao 2009-01-20 Add two temporary comparison files and one diff
    // result file at local
    // structure.
    private static final String FIRST_COMPARE_FILE = ".first_local.comp"; //$NON-NLS-1$

    private static final String SECOND_COMPARE_FILE = ".second_local.comp"; //$NON-NLS-1$

    private static final String RESULT_EMFDIFF_LOCAL_FILE = ".result_local.emfdiff"; //$NON-NLS-1$

    private static final Class<DQStructureComparer> THAT = DQStructureComparer.class;

    protected static Logger log = Logger.getLogger(THAT);

    @SuppressWarnings("unused")
    private static DQStructureComparer comparer = new DQStructureComparer();

    private DQStructureComparer() {
    }

    /**
     * Method "getCopyedFile" copies the source file into the destination file .
     * 
     * @param sourceFile
     * @param destinationFile
     * @return
     */
    public static IFile copyedToDestinationFile(IFile sourceFile, IFile destinationFile) {
        IFile desFile = destinationFile;
        try {
            if (destinationFile.exists()) {
                IFolder parentFolder = (IFolder) destinationFile.getParent();
                String fileName = desFile.getName();
                deleteFile(destinationFile);
                desFile = parentFolder.getFile(fileName);
            }

            sourceFile.copy(desFile.getFullPath(), true, new NullProgressMonitor());
        } catch (CoreException e) {
            log.error(e, e);
        }
        return desFile;
    }

    public static IFile getTempRefreshFile() {
        IFile file = iterateGetNotExistFile(TEMP_REFRESH_FILE);
        return file;
    }

    private static IFile iterateGetNotExistFile(String fileName) {
        IFile file = getFile(fileName);
        if (file.exists()) {
            return iterateGetNotExistFile(fileName.substring(0, fileName.lastIndexOf(".")) + "c" //$NON-NLS-1$ //$NON-NLS-2$
                    + fileName.substring(fileName.lastIndexOf("."))); //$NON-NLS-1$
        } else {
            return file;
        }
    }

    public static IFile getNeedReloadElementsFile() {
        IFile file = iterateGetNotExistFile(NEED_RELOAD_ELEMENTS_PRV);
        return file;
    }

    /**
     * 
     * DOC mzhao Comment method "getFirstComparisonLocalFile".
     * 
     * @return First comparison file.
     */
    public static IFile getFirstComparisonLocalFile() {
        IFile file = getFile(FIRST_COMPARE_FILE);
        return file;
    }

    /**
     * 
     * DOC mzhao Comment method "getSecondComparisonLocalFile".
     * 
     * @return Second comparison file.
     */
    public static IFile getSecondComparisonLocalFile() {
        IFile file = getFile(SECOND_COMPARE_FILE);
        return file;
    }

    /**
     * Method "deleteCopiedResourceFile".
     * 
     * @return true if temporary file ".refresh.prv" has been deleted (or did not exist)
     */
    // public static boolean deleteCopiedResourceFile() {
    // return deleteFile(getTempRefreshFile());
    // }
    // public static boolean deleteNeedReloadElementFile() {
    // return deleteFile(getNeedReloadElementsFile());
    // }
    /**
     * 
     * DOC mzhao Delete first selected resource tmp file.
     * 
     * @return
     */
    public static boolean deleteFirstResourceFile() {
        return deleteFile(getFirstComparisonLocalFile());
    }

    /**
     * 
     * DOC mzhao Delete second selected resource tmp file.
     * 
     * @return
     */
    public static boolean deleteSecondResourceFile() {
        return deleteFile(getSecondComparisonLocalFile());
    }

    public static IFile getDiffResourceFile() {
        IFile file = iterateGetNotExistFile(RESULT_EMFDIFF_FILE);
        try {
            InputStream inputStream = new ByteArrayInputStream("".getBytes()); //$NON-NLS-1$
            file.create(inputStream, true, new NullProgressMonitor());
            inputStream.close();
        } catch (CoreException e) {
            log.error(e, e);
        } catch (IOException e) {
            log.error(e, e);
        }
        return file;
    }

    /**
     * 
     * DOC mzhao Get compared emf diff result file.
     * 
     * @return
     */
    public static IFile getLocalDiffResourceFile() {
        IFile file = getFile(RESULT_EMFDIFF_LOCAL_FILE);
        return file;
    }

    /**
     * To delete the file of "DB Connections" folder by the specific fileName.
     * 
     * @return
     */
    private static boolean deleteFile(IFile file) {
        boolean retValue = false;
        if (file.exists()) {
            URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
            EMFSharedResources.getInstance().unloadResource(uri.toString());
            try {
                file.delete(true, new NullProgressMonitor());
                retValue = true;
            } catch (CoreException e) {

                log.warn(DefaultMessagesImpl.getString("DQStructureComparer.errorDelTmpFile", file.getFullPath().toOSString()), e);//$NON-NLS-1$
                retValue = false;
            }
        } else {
            retValue = true;
        }
        return retValue;
    }

    /**
     * 
     * DOC mzhao get file by name at the same location.
     * 
     * @param fileName
     * @return IFile
     */
    private static IFile getFile(String fileName) {
        IFolder folder = ResourceManager.getConnectionFolder();
        IFile file = folder.getFile(fileName);
        return file;
    }

    public static TypedReturnCode<Connection> getRefreshedDataProvider(Connection prevDataProvider) {
        // ADD xqliu 2010-03-29 bug 11951
        TypedReturnCode<Connection> returnProvider = new TypedReturnCode<Connection>();
        boolean mdm = ConnectionUtils.isMdmConnection(prevDataProvider);
        // ~11951
        String urlString = JavaSqlFactory.getURL(prevDataProvider);
        String driverClassName = JavaSqlFactory.getDriverClass(prevDataProvider);
        Properties properties = new Properties();
        properties.setProperty(TaggedValueHelper.USER, JavaSqlFactory.getUsername(prevDataProvider));
        properties.setProperty(TaggedValueHelper.PASSWORD, JavaSqlFactory.getPassword(prevDataProvider));
        DBConnectionParameter connectionParameters = new DBConnectionParameter();

        connectionParameters.setName(prevDataProvider.getName());
        connectionParameters.setAuthor(MetadataHelper.getAuthor(prevDataProvider));
        connectionParameters.setDescription(MetadataHelper.getDescription(prevDataProvider));
        connectionParameters.setPurpose(MetadataHelper.getPurpose(prevDataProvider));
        connectionParameters.setStatus(MetadataHelper.getDevStatus(prevDataProvider));

        // MOD qiongli 2010-12-2 bug 16605.setSqlTypeName for connectionParameters.
        String dbType = ConnectionUtils.getDatabaseType(prevDataProvider);
        connectionParameters.setSqlTypeName(dbType);
        connectionParameters.setJdbcUrl(urlString);
        connectionParameters.setDriverClassName(driverClassName);
        connectionParameters.setParameters(properties);
        // ADD xqliu 2010-03-04 feature 11412
        connectionParameters.setDbName(ConnectionUtils.getSID(prevDataProvider));
        connectionParameters.setRetrieveAllMetadata(ConnectionHelper.getRetrieveAllMetadata(prevDataProvider));
        if (prevDataProvider instanceof DatabaseConnection) {
            DatabaseConnection dbConn = (DatabaseConnection) prevDataProvider;
            connectionParameters.setOtherParameter(dbConn.getUiSchema());
        }
        // ~11412
        // MOD xqliu 2010-03-29 bug 11951 have mod by zshen 2010/11/29
        IMetadataConnection metadataConnection = MetadataFillFactory.getMDMInstance().fillUIParams(
                ParameterUtil.toMap(connectionParameters));
        List<String> packageFilter=ConnectionUtils.getPackageFilter(connectionParameters);
        Connection conn=null;
        if (mdm) {
            conn = MetadataFillFactory.getMDMInstance().fillUIConnParams(metadataConnection, null);
            MetadataFillFactory.getMDMInstance().fillSchemas(conn, null, packageFilter);
            // returnProvider.setObject(TalendCwmFactory.createMdmTdDataProvider(connectionParameters));
        } else {
            TypedReturnCode<?> trc = (TypedReturnCode<?>) MetadataFillFactory.getDBInstance().checkConnection(
                    metadataConnection);
            Object sqlConnObject = trc.getObject();
            DatabaseMetaData dbJDBCMetadata = null;
            if (trc.isOk() && sqlConnObject instanceof java.sql.Connection) {
                java.sql.Connection sqlConn = (java.sql.Connection) sqlConnObject;
                try {
                    dbJDBCMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(sqlConn);
                } catch (SQLException e) {
                    log.error(e, e);
                }
            }
            conn =MetadataFillFactory.getDBInstance().fillUIConnParams(metadataConnection, null);
            MetadataFillFactory.getDBInstance().fillCatalogs(conn, dbJDBCMetadata, packageFilter);
            MetadataFillFactory.getDBInstance().fillSchemas(conn, dbJDBCMetadata, packageFilter);
            // returnProvider = ConnectionService.createConnection(connectionParameters);
        }
        if (conn == null) {
            returnProvider.setOk(false);
        } else {
            returnProvider.setObject(conn);
        }
        // ~11951
        return returnProvider;
    }

    /**
     * Find the matched package of matchDataProvider.
     * 
     * @param selectedPackage
     * @param matchDataProvider
     * @return
     * @throws ReloadCompareException
     */
    public static Package findMatchedPackage(Package selectedPackage, Connection matchDataProvider)
            throws ReloadCompareException {

        // code clean by gdbu 2011-4-18 : when conn is null , throw a ReloadCompareException.
        if (null == matchDataProvider) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString("DQStructureComparer.ConnectionIsNull")); //$NON-NLS-1$
        }
        // code clean

        Catalog catalogCase = SwitchHelpers.CATALOG_SWITCH.doSwitch(selectedPackage);
        if (catalogCase != null) {
            return findMatchedCatalogObj(catalogCase, matchDataProvider);
        } else {
            Schema schemaCase = (Schema) selectedPackage;
            Catalog parentCatalog = CatalogHelper.getParentCatalog(schemaCase);
            if (parentCatalog != null) {
                Catalog matchCatalog = findMatchedCatalogObj(parentCatalog, matchDataProvider);
                List<Schema> schemas = CatalogHelper.getSchemas(matchCatalog);
                return findMatchedSchema(schemaCase, schemas);
            } else {
                List<Schema> schemas = ConnectionHelper.getSchema(matchDataProvider);
                return findMatchedSchema(schemaCase, schemas);
            }
        }
    }

    /**
     * Find the matched columnSet of matchDataProvider.
     * 
     * @param selectedColumnSet
     * @return
     * @throws ReloadCompareException
     */
    public static ColumnSet findMatchedColumnSet(ColumnSet selectedColumnSet, Connection toMatchDataProvider)
            throws ReloadCompareException {
        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(selectedColumnSet);

        // find the corresponding package from reloaded object.
        Package toReloadPackage = DQStructureComparer.findMatchedPackage(parentCatalogOrSchema, toMatchDataProvider);

        // find the corresponding columnSet from reloaded object.
        TdTable oldTable = SwitchHelpers.TABLE_SWITCH.doSwitch(selectedColumnSet);
        ColumnSet toReloadcolumnSet = null;
        if (oldTable != null) {
            List<TdTable> tables = PackageHelper.getTables(toReloadPackage);
            for (TdTable table : tables) {
                // bug 11934 MOD zshen judge the tableOwner when database is sybase.
                if (oldTable.getName().equals(table.getName())
                        && (ColumnSetHelper.getTableOwner(oldTable) == null || ColumnSetHelper.getTableOwner(oldTable).equals(
                                ColumnSetHelper.getTableOwner(table)))) {
                    // ~11934
                    toReloadcolumnSet = table;
                }
            }

        } else {
            List<TdView> views = PackageHelper.getViews(toReloadPackage);
            for (TdView view : views) {
                // bug 11934 MOD zshen judge the viewOwner when database is sybase.
                if (selectedColumnSet.getName().equals(view.getName())
                        && (ColumnSetHelper.getTableOwner(selectedColumnSet) == null || ColumnSetHelper.getTableOwner(
                                selectedColumnSet).equals(ColumnSetHelper.getTableOwner(view)))) {
                    // ~11934
                    toReloadcolumnSet = view;
                }
            }
        }
        if (toReloadcolumnSet == null) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString("DQStructureComparer.NotFindCorrespondNode",//$NON-NLS-1$
                    selectedColumnSet.getName()));
        }
        return toReloadcolumnSet;
    }

    /**
     * 
     * DOC mzhao Find the matched column of toMatchDataProvider.
     * 
     * @param column
     * @param toMatchDataProvider
     * @return
     * @throws ReloadCompareException
     */
    public static TdColumn findMatchedColumn(TdColumn column, Connection toMatchDataProvider) throws ReloadCompareException {
        // MOD klliu update ColumnHelper.getColumnSetOwner(column)
        ColumnSet columnSet = ColumnHelper.getColumnOwnerAsColumnSet(column);
        ColumnSet toReloadColumnSet = DQStructureComparer.findMatchedColumnSet(columnSet, toMatchDataProvider);
        List<TdColumn> columns = ColumnSetHelper.getColumns(toReloadColumnSet);
        TdColumn oldColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(column);
        TdColumn toMatchedColumn = null;
        if (oldColumn != null) {
            for (TdColumn col : columns) {
                if (oldColumn.getName().equals(col.getName())) {
                    toMatchedColumn = col;
                    break;
                }
            }
        }

        if (toMatchedColumn == null) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString("DQStructureComparer.NotFoundCorrespondColumnNode",//$NON-NLS-1$
                    column.getName()));
        }
        return toMatchedColumn;

    }

    /**
     * DOC rli Comment method "findMatchSchema".
     * 
     * @param schemaCase
     * @param schemas
     * @throws ReloadCompareException
     */
    private static Schema findMatchedSchema(Schema schemaCase, List<Schema> schemas) throws ReloadCompareException {
        for (Schema schema : schemas) {
            if (schemaCase.getName().equals(schema.getName())) {
                return schema;
            }
        }
        throw new ReloadCompareException(DefaultMessagesImpl.getString("DQStructureComparer.NotFoundCorrespondSchemaNode", //$NON-NLS-1$
                schemaCase.getName()));
    }

    /**
     * DOC rli Comment method "findMatchCatalogObj".
     * 
     * @param catalog
     * @throws ReloadCompareException
     */
    private static Catalog findMatchedCatalogObj(Catalog catalog, Connection matchDataProvider)
            throws ReloadCompareException {
        List<Catalog> tdCatalogs = ConnectionHelper.getCatalogs(matchDataProvider);
        for (Catalog matchCatalog : tdCatalogs) {
            if (catalog.getName().equals(matchCatalog.getName())) {
                return matchCatalog;
            }
        }
        throw new ReloadCompareException(DefaultMessagesImpl.getString("DQStructureComparer.NotFoundCorrespondCatalogNode" //$NON-NLS-1$
                , catalog.getName()));
    }

    public static void clearSubNode(ModelElement needReloadElement) {
        Connection dataProvider = SwitchHelpers.CONNECTION_SWITCH.doSwitch(needReloadElement);
        if (dataProvider != null) {
            List<Catalog> tdCatalogs = ConnectionHelper.getCatalogs(dataProvider);
            for (Catalog catalog : tdCatalogs) {
                clearSubNode(catalog);
            }
            List<Schema> tdSchemas = ConnectionHelper.getSchema(dataProvider);
            for (Schema schema : tdSchemas) {
                clearSubNode(schema);
            }
            return;
        }
        Catalog tdCatalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(needReloadElement);
        if (tdCatalog != null) {
            List<Schema> schemas = CatalogHelper.getSchemas(tdCatalog);
            for (Schema schema : schemas) {
                clearSubNode(schema);
            }
            if (schemas.size() == 0) {
                tdCatalog.getOwnedElement().clear();
            }
            return;
        }
        Schema tdSchema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(needReloadElement);
        if (tdSchema != null) {
            tdSchema.getOwnedElement().clear();
            return;
        }

        ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(needReloadElement);
        if (columnSet != null) {
            columnSet.getFeature().clear();
            columnSet.getTaggedValue().clear();
            // ~MOD mzhao 2009-03-12 Clear primary key(contains in ownedElement)
            // as well. If not clear, it will cause
            // exception: not contained in
            // a resource...
            columnSet.getOwnedElement().clear();
            return;
        }
        TdColumn column = SwitchHelpers.COLUMN_SWITCH.doSwitch(needReloadElement);
        if (column != null) {
            column.getTaggedValue().clear();
            // ~MOD mzhao 2009-03-12 Clear primary key as well. If not clear, it
            // will cause exception: not contained in
            // a resource...
            column.getUniqueKey().clear();
            // ~
            // ~MOD mzhao 2009-04-08 Clear foreign key.
            column.getKeyRelationship().clear();
            return;
        }
    }

    /**
     *Open a compare editor UI, will clear the information which hasn't relationship with current selected level
     * first(For example: if we compare the catalog level, will clear it's table(view) from every catalog), then will
     * compare current level object.
     * 
     * @param rightResource
     * @param oldDataProviderFile
     * @return
     * @throws ReloadCompareException
     */
    public static DiffModel openDiffCompareEditor(Resource leftResource, Resource rightResource, Map<String, Object> opt,
            IUIHandler guiHandler, IFile efmDiffResultFile, String dbName, Object selectedObject, boolean compareEachOther)
            throws ReloadCompareException {

        MatchModel match = null;
        try {
            match = MatchService.doResourceMatch(leftResource, rightResource, opt);
        } catch (InterruptedException e) {
            throw new ReloadCompareException(e);
        }
        final DiffModel diff = DiffService.doDiff(match);
        // ~ MOD mzhao bug 11449. 2010-03-16
        if (leftResource.getContents() == null || leftResource.getContents().size() == 0) {
            // Could not merge this.
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    DefaultMessagesImpl.getString("DQStructureComparer.errorDialog1"),
                    DefaultMessagesImpl.getString("DQStructureComparer.errorDialog2"));//$NON-NLS-1$ $NON-NLS-2$
            return null;
        }
        EList<DiffElement> ownedElements = diff.getOwnedElements();
        for (DiffElement de : ownedElements) {
            EList<DiffElement> subDiffElements = de.getSubDiffElements();
            for (DiffElement difElement : subDiffElements) {
                if (difElement instanceof ModelElementChangeRightTarget) {
                    ((ModelElementChangeRightTarget) difElement).setLeftParent(leftResource.getContents().get(0));

                }
            }
        }
        // ~

        // Open UI for different comparison
        final ComparisonResourceSnapshot snapshot = DiffFactory.eINSTANCE.createComparisonResourceSnapshot();
        snapshot.setDate(Calendar.getInstance().getTime());
        snapshot.setMatch(match);
        snapshot.setDiff(diff);
        IFile createDiffResourceFile = efmDiffResultFile;
        try {
            // klliu
            createDiffResourceFile.clearHistory(new NullProgressMonitor());
            final String fullPath = createDiffResourceFile.getLocation().toOSString();

            ModelUtils.save(snapshot, fullPath);
        } catch (IOException e) {
            throw new ReloadCompareException(e);
        } catch (CoreException e) {
            log.error(e);
        }
        if (guiHandler != null) {
            guiHandler.popComparisonUI(createDiffResourceFile.getLocation(), dbName, selectedObject, compareEachOther);
        }
        return diff;
    }
}
