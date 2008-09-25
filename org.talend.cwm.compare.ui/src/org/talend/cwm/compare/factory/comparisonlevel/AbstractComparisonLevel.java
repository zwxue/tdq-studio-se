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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.AddModelElement;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.RemoveModelElement;
import org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.api.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractComparisonLevel implements IComparisonLevel {

    private DiffSwitch<AddModelElement> addModelSwitch;

    private DiffSwitch<RemoveModelElement> removeModelSwitch;

    private boolean removeElementConfirm = false;

    protected Object selectedObj;

    protected TdDataProvider oldDataProvider;

    // protected IFile tempConnectionFile;

    protected TdDataProvider tempReloadProvider;

    public AbstractComparisonLevel(Object selectedObj) {
        this.selectedObj = selectedObj;
        initSwitchValue();
    }

    protected void initSwitchValue() {

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
        EObject savedReloadObject = getSavedReloadObject();

        if (compareWithReloadObject(savedReloadObject)) {
            saveReloadResult();
        }
    }

    /**
     * DOC rli Comment method "createTempConnectionFile".
     * 
     * @throws ReloadCompareException
     */
    protected void createTempConnectionFile() throws ReloadCompareException {
        IFile tempConnectionFile = DQStructureComparer.createTempConnectionFile();
        TypedReturnCode<TdDataProvider> returnProvider = getRefreshedDataProvider(oldDataProvider);
        if (!returnProvider.isOk()) {
            throw new ReloadCompareException(returnProvider.getMessage());
        }
        tempReloadProvider = returnProvider.getObject();
        DqRepositoryViewService.saveDataProviderResource(tempReloadProvider, (IFolder) tempConnectionFile.getParent(),
                tempConnectionFile);
    }

    protected abstract EObject getSavedReloadObject() throws ReloadCompareException;

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
     * 
     * 
     * @param oldDataProvider
     */
    protected boolean compareWithReloadObject(EObject reloadedObj) throws ReloadCompareException {

        // add option for ignoring some elements
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
        // options.put(MatchOptions.OPTION_SEARCH_WINDOW, 500);
        MatchModel match = null;
        try {
            match = MatchService.doMatch(oldDataProvider, reloadedObj, options);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        final DiffModel diff = DiffService.doDiff(match);

        // final ModelInputSnapshot snapshot = DiffFactory.eINSTANCE.createModelInputSnapshot();
        // snapshot.setDate(Calendar.getInstance().getTime());
        // snapshot.setMatch(match);
        // snapshot.setDiff(diff);
        // IFile createDiffResourceFile = DQStructureComparer.createDiffResourceFile();
        // try {
        // ModelUtils.save(snapshot, createDiffResourceFile.getFullPath().toString());
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // new ModelCompareEditorLauncher().open(createDiffResourceFile.getLocation());

        EList<DiffElement> ownedElements = diff.getOwnedElements();
        for (DiffElement de : ownedElements) {
            EList<DiffElement> subDiffElements = de.getSubDiffElements();
            for (DiffElement difElement : subDiffElements) {
                handleDiffPackageElement(difElement);
            }
        }
        return true;
    }

    protected TypedReturnCode<TdDataProvider> getRefreshedDataProvider(TdDataProvider oldDataProvider) {
        TypedReturnCode<TdProviderConnection> tdProviderConnection = DataProviderHelper.getTdProviderConnection(oldDataProvider);
        String urlString = tdProviderConnection.getObject().getConnectionString();
        String driverClassName = tdProviderConnection.getObject().getDriverClassName();
        Properties properties = new Properties();
        properties.setProperty(PluginConstant.USER_PROPERTY, TaggedValueHelper.getValue(PluginConstant.USER_PROPERTY,
                tdProviderConnection.getObject()));
        properties.setProperty(PluginConstant.PASSWORD_PROPERTY, TaggedValueHelper.getValue(PluginConstant.PASSWORD_PROPERTY,
                tdProviderConnection.getObject()));
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
            final Display display = PlatformUI.getWorkbench().getDisplay();
            final TdDataProvider provider = oldDataProvider;
            display.asyncExec(new Runnable() {

                public void run() {

                    DeleteModelElementConfirmDialog.showElementImpactDialog(new Shell(display), new ModelElement[] { provider },
                            "The following analyses will be impacted:");
                }
            });
            removeElementConfirm = true;
        }
    }

    /**
     * DOC rli Comment method "findMatchPackage".
     * 
     * @param selectedPackage
     * @return
     * @throws ReloadCompareException
     */
    protected Package findMatchPackage(Package selectedPackage) throws ReloadCompareException {
        TdCatalog catalogCase = SwitchHelpers.CATALOG_SWITCH.doSwitch(selectedPackage);
        if (catalogCase != null) {
            return findMatchCatalogObj(catalogCase);
        } else {
            TdSchema schemaCase = (TdSchema) selectedPackage;
            TdCatalog parentCatalog = CatalogHelper.getParentCatalog(schemaCase);
            if (parentCatalog != null) {
                TdCatalog matchCatalog = findMatchCatalogObj(parentCatalog);
                List<TdSchema> schemas = CatalogHelper.getSchemas(matchCatalog);
                return findMatchSchema(schemaCase, schemas);
            } else {
                List<TdSchema> tdSchemas = DataProviderHelper.getTdSchema(tempReloadProvider);
                return findMatchSchema(schemaCase, tdSchemas);
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
    private TdSchema findMatchSchema(TdSchema schemaCase, List<TdSchema> schemas) throws ReloadCompareException {
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
    private TdCatalog findMatchCatalogObj(TdCatalog catalog) throws ReloadCompareException {
        List<TdCatalog> tdCatalogs = DataProviderHelper.getTdCatalogs(this.tempReloadProvider);
        for (TdCatalog matchCatalog : tdCatalogs) {
            if (catalog.getName().equals(matchCatalog.getName())) {
                return matchCatalog;
            }
        }
        throw new ReloadCompareException("Can't find out the corresponding reload catalog node for current selected node:"
                + catalog.getName());
    }

}
