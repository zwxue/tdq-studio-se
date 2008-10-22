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
package org.talend.cwm.compare.factory.comparisonlevel;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.AddModelElement;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelInputSnapshot;
import org.eclipse.emf.compare.diff.metamodel.RemoveModelElement;
import org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.api.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.compare.factory.IUIHandler;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.util.RelationalSwitch;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractComparisonLevel implements IComparisonLevel {

    protected DiffSwitch<AddModelElement> addModelSwitch;

    protected DiffSwitch<RemoveModelElement> removeModelSwitch;

    protected RelationalSwitch<Package> packageSwitch;

    private boolean removeElementConfirm = false;

    protected Object selectedObj;

    protected TdDataProvider oldDataProvider;

    protected TdDataProvider tempReloadProvider;

    protected Map<String, Object> options;

    protected TdDataProvider copyedDataProvider;

    protected IUIHandler guiHandler;

    public AbstractComparisonLevel(Object selectedObj) {
        this.selectedObj = selectedObj;
        initSwitchValue();
        options = new HashMap<String, Object>();
        options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
    }

    private void initSwitchValue() {

        addModelSwitch = new DiffSwitch<AddModelElement>() {

            public AddModelElement caseAddModelElement(AddModelElement object) {
                return object;
            }
        };
        removeModelSwitch = new DiffSwitch<RemoveModelElement>() {

            public RemoveModelElement caseRemoveModelElement(RemoveModelElement object) {
                return object;
            }
        };

        packageSwitch = new RelationalSwitch<Package>() {

            public Package casePackage(Package object) {
                return object;
            }
        };
    }

    public void reloadCurrentLevelElement() throws ReloadCompareException {
        if (!isValid()) {
            return;
        }
        DQStructureComparer.deleteCopiedResourceFile();
        oldDataProvider = findDataProvider();
        if (oldDataProvider == null) {
            return;
        }
        createTempConnectionFile();

        if (compareWithReloadObject()) {
            saveReloadResult();
        }
    }

    public void popComparisonUI(IUIHandler uiHandler) throws ReloadCompareException {
        this.guiHandler = uiHandler;
        if (!isValid()) {
            return;
        }
        oldDataProvider = findDataProvider();
        if (oldDataProvider == null) {
            return;
        }
        DQStructureComparer.deleteCopiedResourceFile();
        DQStructureComparer.deleteNeedReloadElementFile();
        createTempConnectionFile();
        createCopyedProvider();
        openDiffCompareEditor(getLeftResource(), getRightResource(), options);

        // testInit();

    }

    private void createCopyedProvider() {
        IFile selectedFile = PrvResourceFileHelper.getInstance().findCorrespondingFile(oldDataProvider);
        IFile createNeedReloadElementsFile = DQStructureComparer.getNeedReloadElementsFile();
        IFile copyedFile = DQStructureComparer.copyedToDestinationFile(selectedFile, createNeedReloadElementsFile);
        TypedReturnCode<TdDataProvider> returnValue = DqRepositoryViewService.readFromFile(copyedFile);
        copyedDataProvider = returnValue.getObject();

    }

    /**
     * DOC rli Comment method "createTempConnectionFile".
     * 
     * @throws ReloadCompareException
     */
    protected void createTempConnectionFile() throws ReloadCompareException {
        IFile tempConnectionFile = DQStructureComparer.getTempRefreshFile();
        TypedReturnCode<TdDataProvider> returnProvider = getRefreshedDataProvider(oldDataProvider);
        if (!returnProvider.isOk()) {
            throw new ReloadCompareException(returnProvider.getMessage());
        }
        tempReloadProvider = returnProvider.getObject();
        DqRepositoryViewService.saveDataProviderResource(tempReloadProvider, (IFolder) tempConnectionFile.getParent(),
                tempConnectionFile);
    }

    // private void testInit() throws ReloadCompareException {
    //
    // URI uri = URI.createPlatformResourceURI(DQStructureComparer.getNeedReloadElementsFile().getFullPath().toString(),
    // false);
    // Resource leftResource = EMFSharedResources.getInstance().getResource(uri, true);
    // uri = URI.createPlatformResourceURI(DQStructureComparer.getTempRefreshFile().getFullPath().toString(), false);
    // Resource rightResource = EMFSharedResources.getInstance().getResource(uri, true);
    //
    // openDiffCompareEditor(leftResource, rightResource, options);
    // }

    protected abstract EObject getSavedReloadObject() throws ReloadCompareException;

    protected abstract Resource getRightResource() throws ReloadCompareException;

    protected void saveReloadResult() {
        PrvResourceFileHelper.getInstance().save(oldDataProvider);
    }

    /**
     * Method "findDataProvider".
     * 
     * @return the data provider of the selected object
     */
    protected abstract TdDataProvider findDataProvider();

    protected boolean isValid() {
        return true;
    }

    /**
     * Compare the old selected object with reload object(rightResource), and updated the content of old selected
     * object.
     * 
     * @param rightResource
     * @return
     * @throws ReloadCompareException
     */
    protected boolean compareWithReloadObject() throws ReloadCompareException {
        // options.put(MatchOptions.OPTION_SEARCH_WINDOW, 500);

        MatchModel match = null;
        try {
            match = MatchService.doMatch(oldDataProvider, getSavedReloadObject(), options);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        final DiffModel diff = DiffService.doDiff(match);

        EList<DiffElement> ownedElements = diff.getOwnedElements();
        for (DiffElement de : ownedElements) {
            EList<DiffElement> subDiffElements = de.getSubDiffElements();
            for (DiffElement difElement : subDiffElements) {
                handleDiffPackageElement(difElement);
            }
        }
        return true;
    }

    protected abstract Resource getLeftResource() throws ReloadCompareException;

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
    private DiffModel openDiffCompareEditor(Resource leftResource, Resource rightResource, Map<String, Object> options)
            throws ReloadCompareException {

        MatchModel match = null;
        try {
            match = MatchService.doResourceMatch(leftResource, rightResource, options);
        } catch (InterruptedException e) {
            throw new ReloadCompareException(e);
        }
        final DiffModel diff = DiffService.doDiff(match);

        // Open UI for different comparison
        final ModelInputSnapshot snapshot = DiffFactory.eINSTANCE.createModelInputSnapshot();
        snapshot.setDate(Calendar.getInstance().getTime());
        snapshot.setMatch(match);
        snapshot.setDiff(diff);
        IFile createDiffResourceFile = DQStructureComparer.getDiffResourceFile();
        try {
            final String fullPath = createDiffResourceFile.getLocation().toOSString();
            ModelUtils.save(snapshot, fullPath);
        } catch (IOException e) {
            throw new ReloadCompareException(e);
        }
        if (guiHandler != null) {
            this.guiHandler.popComparisonUI(createDiffResourceFile.getLocation());
        }
        return diff;
    }

    protected void clearSubNode(ModelElement needReloadElement) {
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
            return;
        }
        TdColumn column = SwitchHelpers.COLUMN_SWITCH.doSwitch(needReloadElement);
        if (column != null) {
            column.getTaggedValue().clear();
            return;
        }
    }

    protected TypedReturnCode<TdDataProvider> getRefreshedDataProvider(TdDataProvider oldDataProvider) {
        TypedReturnCode<TdProviderConnection> tdProviderConnection = DataProviderHelper.getTdProviderConnection(oldDataProvider);
        String urlString = tdProviderConnection.getObject().getConnectionString();
        String driverClassName = tdProviderConnection.getObject().getDriverClassName();
        Properties properties = new Properties();
        properties.setProperty(PluginConstant.USER_PROPERTY, TaggedValueHelper.getValue(PluginConstant.USER_PROPERTY,
                tdProviderConnection.getObject()));
        properties.setProperty(org.talend.dq.PluginConstant.PASSWORD_PROPERTY, TaggedValueHelper.getValue(
                org.talend.dq.PluginConstant.PASSWORD_PROPERTY, tdProviderConnection.getObject()));
        DBConnectionParameter connectionParameters = new DBConnectionParameter();

        connectionParameters.setName(oldDataProvider.getName());
        connectionParameters.setAuthor(TaggedValueHelper.getAuthor(oldDataProvider));
        connectionParameters.setDescription(TaggedValueHelper.getDescription(oldDataProvider));
        connectionParameters.setPurpose(TaggedValueHelper.getPurpose(oldDataProvider));
        connectionParameters.setStatus(TaggedValueHelper.getDevStatus(oldDataProvider).getLiteral());

        connectionParameters.setJdbcUrl(urlString);
        connectionParameters.setDriverClassName(driverClassName);
        connectionParameters.setParameters(properties);
        TypedReturnCode<TdDataProvider> returnProvider = ConnectionService.createConnection(connectionParameters);
        return returnProvider;
    }

    protected void handleDiffPackageElement(DiffElement difElement) {
        AddModelElement addElement = addModelSwitch.doSwitch(difElement);
        if (addElement != null) {
            handleAddElement(addElement);
            return;
        }
        RemoveModelElement removeElement = removeModelSwitch.doSwitch(difElement);
        if (removeElement != null) {
            handleRemoveElement(removeElement);
        }
    }

    protected abstract void handleRemoveElement(RemoveModelElement removeElement);

    protected abstract void handleAddElement(AddModelElement addElement);

    protected void popRemoveElementConfirm() {
        if (!removeElementConfirm) {
            final TdDataProvider provider = oldDataProvider;
            if (guiHandler != null) {
                guiHandler.popRemoveElement(provider);
            }            
            removeElementConfirm = true;
        }
    }

    /**
     * Find the matched package of matchDataProvider.
     * 
     * @param selectedPackage
     * @param matchDataProvider TODO
     * @return
     * @throws ReloadCompareException
     */
    protected Package findMatchedPackage(Package selectedPackage, TdDataProvider matchDataProvider) throws ReloadCompareException {
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
     * DOC rli Comment method "findMatchSchema".
     * 
     * @param schemaCase
     * @param schemas
     * @throws ReloadCompareException
     */
    private TdSchema findMatchedSchema(TdSchema schemaCase, List<TdSchema> schemas) throws ReloadCompareException {
        for (TdSchema schema : schemas) {
            if (schemaCase.getName().equals(schema.getName())) {
                return schema;
            }
        }
        throw new ReloadCompareException("Can't find out the corresponding reload schema node for current selected node:"
                + schemaCase.getName());
    }

    /**
     * DOC rli Comment method "findMatchCatalogObj".
     * 
     * @param catalog
     * @throws ReloadCompareException
     */
    private TdCatalog findMatchedCatalogObj(TdCatalog catalog, TdDataProvider matchDataProvider) throws ReloadCompareException {
        List<TdCatalog> tdCatalogs = DataProviderHelper.getTdCatalogs(matchDataProvider);
        for (TdCatalog matchCatalog : tdCatalogs) {
            if (catalog.getName().equals(matchCatalog.getName())) {
                return matchCatalog;
            }
        }
        throw new ReloadCompareException("Can't find out the corresponding reload catalog node for current selected node:"
                + catalog.getName());
    }

}
