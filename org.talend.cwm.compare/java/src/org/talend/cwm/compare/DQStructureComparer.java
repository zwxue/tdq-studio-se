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
package org.talend.cwm.compare;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelInputSnapshot;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.IUIHandler;
import org.talend.cwm.compare.i18n.DefaultMessagesImpl;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;

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

    private static DQStructureComparer comparer = new DQStructureComparer();

    public static DQStructureComparer getInstance() {
        return comparer;
    }

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

                log.warn("Problem while trying to delete temp file:" + file.getFullPath().toOSString(), e);
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
        IFolder folder = ResourceManager.getMetadataFolder().getFolder(PluginConstant.DB_CONNECTIONS);
        IFile file = folder.getFile(fileName);
        return file;
    }

    public static TypedReturnCode<TdDataProvider> getRefreshedDataProvider(TdDataProvider prevDataProvider) {
        TypedReturnCode<TdProviderConnection> tdProviderConnection = DataProviderHelper.getTdProviderConnection(prevDataProvider);
        String urlString = tdProviderConnection.getObject().getConnectionString();
        String driverClassName = tdProviderConnection.getObject().getDriverClassName();
        Properties properties = new Properties();
        properties.setProperty(TaggedValueHelper.USER, DataProviderHelper.getUser(tdProviderConnection.getObject()));
        properties.setProperty(TaggedValueHelper.PASSWORD, DataProviderHelper.getClearTextPassword(prevDataProvider));
        DBConnectionParameter connectionParameters = new DBConnectionParameter();

        connectionParameters.setName(prevDataProvider.getName());
        connectionParameters.setAuthor(MetadataHelper.getAuthor(prevDataProvider));
        connectionParameters.setDescription(MetadataHelper.getDescription(prevDataProvider));
        connectionParameters.setPurpose(MetadataHelper.getPurpose(prevDataProvider));
        connectionParameters.setStatus(MetadataHelper.getDevStatus(prevDataProvider).getLiteral());

        connectionParameters.setJdbcUrl(urlString);
        connectionParameters.setDriverClassName(driverClassName);
        connectionParameters.setParameters(properties);
        TypedReturnCode<TdDataProvider> returnProvider = ConnectionService.createConnection(connectionParameters);
        return returnProvider;
    }

    /**
     * Find the matched package of matchDataProvider.
     * 
     * @param selectedPackage
     * @param matchDataProvider TODO
     * @return
     * @throws ReloadCompareException
     */
    public static Package findMatchedPackage(Package selectedPackage, TdDataProvider matchDataProvider)
            throws ReloadCompareException {
        TdCatalog catalogCase = SwitchHelpers.CATALOG_SWITCH.doSwitch(selectedPackage);
        if (catalogCase != null) {
            return findMatchedCatalogObj(catalogCase, matchDataProvider);
        } else {
            TdSchema schemaCase = (TdSchema) selectedPackage;
            TdCatalog parentCatalog = CatalogHelper.getParentCatalog(schemaCase);
            if (parentCatalog != null) {
                TdCatalog matchCatalog = findMatchedCatalogObj(parentCatalog, matchDataProvider);
                List<TdSchema> schemas = CatalogHelper.getSchemas(matchCatalog);
                return findMatchedSchema(schemaCase, schemas);
            } else {
                List<TdSchema> tdSchemas = DataProviderHelper.getTdSchema(matchDataProvider);
                return findMatchedSchema(schemaCase, tdSchemas);
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
    public static ColumnSet findMatchedColumnSet(ColumnSet selectedColumnSet, TdDataProvider toMatchDataProvider)
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
                if (oldTable.getName().equals(table.getName())) {
                    toReloadcolumnSet = table;
                }
            }

        } else {
            List<TdView> views = PackageHelper.getViews(toReloadPackage);
            for (TdView view : views) {
                if (selectedColumnSet.getName().equals(view.getName())) {
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
    public static Column findMatchedColumn(Column column, TdDataProvider toMatchDataProvider) throws ReloadCompareException {
        ColumnSet columnSet = ColumnHelper.getColumnSetOwner(column);
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
    private static TdSchema findMatchedSchema(TdSchema schemaCase, List<TdSchema> schemas) throws ReloadCompareException {
        for (TdSchema schema : schemas) {
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
    private static TdCatalog findMatchedCatalogObj(TdCatalog catalog, TdDataProvider matchDataProvider)
            throws ReloadCompareException {
        List<TdCatalog> tdCatalogs = DataProviderHelper.getTdCatalogs(matchDataProvider);
        for (TdCatalog matchCatalog : tdCatalogs) {
            if (catalog.getName().equals(matchCatalog.getName())) {
                return matchCatalog;
            }
        }
        throw new ReloadCompareException(DefaultMessagesImpl.getString("DQStructureComparer.NotFoundCorrespondCatalogNode" //$NON-NLS-1$
                , catalog.getName()));
    }

    public static void clearSubNode(ModelElement needReloadElement) {
        TdDataProvider dataProvider = SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(needReloadElement);
        if (dataProvider != null) {
            List<TdCatalog> tdCatalogs = DataProviderHelper.getTdCatalogs(dataProvider);
            for (TdCatalog catalog : tdCatalogs) {
                clearSubNode(catalog);
            }
            List<TdSchema> tdSchemas = DataProviderHelper.getTdSchema(dataProvider);
            for (TdSchema schema : tdSchemas) {
                clearSubNode(schema);
            }
            return;
        }
        TdCatalog tdCatalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(needReloadElement);
        if (tdCatalog != null) {
            List<TdSchema> schemas = CatalogHelper.getSchemas(tdCatalog);
            for (TdSchema schema : schemas) {
                clearSubNode(schema);
            }
            if (schemas.size() == 0) {
                tdCatalog.getOwnedElement().clear();
            }
            return;
        }
        TdSchema tdSchema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(needReloadElement);
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
            IUIHandler guiHandler, IFile efmDiffResultFile, String dbName,
			Object selectedObject, boolean compareEachOther)
			throws ReloadCompareException {

        MatchModel match = null;
        try {
            match = MatchService.doResourceMatch(leftResource, rightResource, opt);
        } catch (InterruptedException e) {
            throw new ReloadCompareException(e);
        }
        final DiffModel diff = DiffService.doDiff(match);

        // Open UI for different comparison
        final ModelInputSnapshot snapshot = DiffFactory.eINSTANCE.createModelInputSnapshot();
        snapshot.setDate(Calendar.getInstance().getTime());
        snapshot.setMatch(match);
        snapshot.setDiff(diff);
        IFile createDiffResourceFile = efmDiffResultFile;
        try {
            final String fullPath = createDiffResourceFile.getLocation().toOSString();
            ModelUtils.save(snapshot, fullPath);
        } catch (IOException e) {
            throw new ReloadCompareException(e);
        }
        if (guiHandler != null) {
            guiHandler.popComparisonUI(createDiffResourceFile.getLocation(),
					dbName, selectedObject, compareEachOther);
        }
        return diff;
    }
}
