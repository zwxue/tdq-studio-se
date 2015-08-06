// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffGroup;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.ReferenceChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ReferenceChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.UpdateAttribute;
import org.eclipse.emf.compare.diff.metamodel.UpdateModelElement;
import org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.compare.factory.IUIHandler;
import org.talend.cwm.compare.factory.update.AddTdRelationalSwitch;
import org.talend.cwm.compare.factory.update.RemoveTdRelationalSwitch;
import org.talend.cwm.compare.factory.update.UpdateTdRelationalSwitch;
import org.talend.cwm.compare.i18n.DefaultMessagesImpl;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.nodes.foldernode.AbstractDatabaseFolderNode;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.objectmodel.core.util.CoreSwitch;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractComparisonLevel implements IComparisonLevel {

    private static Logger log = Logger.getLogger(AbstractComparisonLevel.class);

    protected static final boolean CASE_INSENSTIVE = true;

    protected DiffSwitch<ModelElementChangeRightTarget> addModelSwitch;

    protected DiffSwitch<UpdateModelElement> updateModelSwitch;

    protected DiffSwitch<ModelElementChangeLeftTarget> removeModelSwitch;

    protected DiffSwitch<ReferenceChangeRightTarget> removeReferenceValueSwitch;

    protected DiffSwitch<ReferenceChangeLeftTarget> addReferenceValueSwitch;

    protected DiffSwitch<DiffGroup> diffGroupSwitch;

    // MOD klliu bug 14689 2010-08-04
    protected CoreSwitch<Package> packageSwitch;

    protected UpdateTdRelationalSwitch updateRelationalStructSwitch = new UpdateTdRelationalSwitch();

    protected RemoveTdRelationalSwitch removeRelationalSwitch = new RemoveTdRelationalSwitch();

    protected AddTdRelationalSwitch addRelationalSwitch = new AddTdRelationalSwitch();

    private boolean removeElementConfirm = false;

    protected Object selectedObj;

    private AbstractDatabaseFolderNode dbFolderNode = null;

    protected Connection oldDataProvider;

    protected Connection tempReloadProvider;

    protected Map<String, Object> options;

    protected Connection copyedDataProvider;

    protected IUIHandler guiHandler;

    protected EMFSharedResources util = EMFSharedResources.getInstance();

    public AbstractComparisonLevel(Object selObj) {
        if (selObj instanceof AbstractDatabaseFolderNode) {
            AbstractDatabaseFolderNode fNode = (AbstractDatabaseFolderNode) selObj;
            Package ctatlogSwtich = SwitchHelpers.PACKAGE_SWITCH.doSwitch((EObject) fNode.getParent());
            ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch((EObject) fNode.getParent());
            if (ctatlogSwtich != null) {
                this.selectedObj = ctatlogSwtich;
            } else if (columnSet != null) {

                this.selectedObj = columnSet;
            }
            this.dbFolderNode = fNode;
        } else {
            selectedObj = selObj;
        }

        initSwitchValue();
        options = new HashMap<String, Object>();
        options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
        options.put(MatchOptions.OPTION_IGNORE_ID, true);
    }

    private void initSwitchValue() {

        addModelSwitch = new DiffSwitch<ModelElementChangeRightTarget>() {

            @Override
            public ModelElementChangeRightTarget caseModelElementChangeRightTarget(ModelElementChangeRightTarget object) {
                return object;
            }

        };

        updateModelSwitch = new DiffSwitch<UpdateModelElement>() {

            @Override
            public UpdateModelElement caseUpdateModelElement(UpdateModelElement object) {
                return object;
            }
        };
        removeModelSwitch = new DiffSwitch<ModelElementChangeLeftTarget>() {

            @Override
            public ModelElementChangeLeftTarget caseModelElementChangeLeftTarget(ModelElementChangeLeftTarget object) {
                return object;
            }

        };

        packageSwitch = new CoreSwitch<Package>() {

            @Override
            public Package casePackage(Package object) {
                return object;
            }
        };
        diffGroupSwitch = new DiffSwitch<DiffGroup>() {

            @Override
            public DiffGroup caseDiffGroup(DiffGroup object) {
                return object;
            };
        };
    }

    public Connection reloadCurrentLevelElement() throws ReloadCompareException {
        if (!isValid()) {
            return null;
        }
        oldDataProvider = upperCaseConnection(findDataProvider());
        if (oldDataProvider == null) {
            return null;
        }
        IFile tempFile = createTempConnectionFile();

        if (compareWithReloadObject()) {
            saveReloadResult();
        }
        deleteTempConnectionFile(tempFile);
        return oldDataProvider;
    }

    /**
     * Delete temp file
     */
    private void deleteTempConnectionFile(IFile tempFile) {
        DQStructureComparer.deleteFile(tempFile);
    }

    public void popComparisonUI(IUIHandler uiHandler) throws ReloadCompareException {
        this.guiHandler = uiHandler;
        if (!isValid()) {
            return;
        }
        oldDataProvider = upperCaseConnection(findDataProvider());
        if (oldDataProvider == null) {
            return;
        }
        // DQStructureComparer.deleteCopiedResourceFile();
        // DQStructureComparer.deleteNeedReloadElementFile();
        createCopyedProvider();
        createTempConnectionFile();
        // MOD mzhao 2009-01-20 Extract method openDiffCompareEditor to class
        // DQStructureComparer.
        // MOD mzhao 2009-03-09 add param dbname for displaying datasource(db
        // name) in compare
        // editor.

        Object needReloadObject = dbFolderNode == null ? selectedObj : dbFolderNode;
        DQStructureComparer.openDiffCompareEditor(getLeftResource(), getRightResource(), options, guiHandler,
                DQStructureComparer.getDiffResourceFile(), oldDataProvider.getName(), needReloadObject, false);

        // testInit();

    }

    @SuppressWarnings("deprecation")
    protected void createCopyedProvider() throws ReloadCompareException {
        IFile selectedFile = WorkspaceUtils.getModelElementResource(oldDataProvider);
        IFile createNeedReloadElementsFile = DQStructureComparer.getNeedReloadElementsFile();
        IFile copyedFile = DQStructureComparer.copyedToDestinationFile(selectedFile, createNeedReloadElementsFile);
        TypedReturnCode<Connection> returnValue = DqRepositoryViewService.readFromFile(copyedFile);

        if (returnValue.isOk()) {
            copyedDataProvider = returnValue.getObject();
        } else {
            DQStructureComparer.deleteFile(createNeedReloadElementsFile);
            throw new ReloadCompareException(returnValue.getMessage());
        }

    }

    /**
     * DOC rli Comment method "createTempConnectionFile".
     * 
     * @throws ReloadCompareException
     */
    protected IFile createTempConnectionFile() throws ReloadCompareException {
        IFile tempConnectionFile = DQStructureComparer.getTempRefreshFile();
        // MOD mzhao ,Extract method getRefreshedDataProvider to class
        // DQStructureComparer for common use.
        TypedReturnCode<Connection> returnProvider = DQStructureComparer.getRefreshedDataProvider(oldDataProvider);
        if (!returnProvider.isOk()) {
            throw new ReloadCompareException(returnProvider.getMessage());
        }
        tempReloadProvider = upperCaseConnection(returnProvider.getObject());
        tempReloadProvider.setComponent(oldDataProvider.getComponent());
        // MOD mzhao bug:9012 2009-09-08
        ElementWriterFactory.getInstance().createDataProviderWriter()
                .create(tempReloadProvider, tempConnectionFile.getFullPath(), false);
        tempReloadProvider.setComponent(null);
        oldDataProvider.getComponent();
        return tempConnectionFile;
    }

    // private void testInit() throws ReloadCompareException {
    //
    // URI uri =
    // URI.createPlatformResourceURI(DQStructureComparer.getNeedReloadElementsFile
    // ().getFullPath().toString(),
    // false);
    // Resource leftResource = EMFSharedResources.getInstance().getResource(uri,
    // true);
    // uri =
    // URI.createPlatformResourceURI(DQStructureComparer.getTempRefreshFile
    // ().getFullPath().toString(), false);
    // Resource rightResource =
    // EMFSharedResources.getInstance().getResource(uri, true);
    //
    // openDiffCompareEditor(leftResource, rightResource, options);
    // }

    protected abstract EObject getSavedReloadObject() throws ReloadCompareException;

    protected abstract Resource getRightResource() throws ReloadCompareException;

    protected void saveReloadResult() {

        if (oldDataProvider.getName() == null) {
            oldDataProvider.setName(PropertyHelper.getProperty(oldDataProvider).getLabel());
        }

        // MOD klliu bug 15940 201-09-30
        // MOD msjian 2011-5-23 bug 20875: fix the issue2 error(because two times updateDependecy)
        ElementWriterFactory.getInstance().createDataProviderWriter().save(oldDataProvider, true);
        // PrvResourceFileHelper.getInstance().save(oldDataProvider);
    }

    /**
     * Method "findDataProvider".
     * 
     * @return the data provider of the selected object
     */
    protected abstract Connection findDataProvider();

    protected boolean isValid() {
        return true;
    }

    /**
     * remove the jrxml from the ResourceSet before doMatch, if the ResourceSet is null, return empty Map. Should call
     * this method only before doMatch.
     * 
     * @return
     */
    protected Map<ResourceSet, List<Resource>> removeJrxmlsFromResourceSet() {
        Map<ResourceSet, List<Resource>> map = new HashMap<ResourceSet, List<Resource>>();

        ResourceSet resourceSet = oldDataProvider.eResource() == null ? null : oldDataProvider.eResource().getResourceSet();
        if (resourceSet != null) {
            map.put(resourceSet, ResourceManager.removeJrxmls(resourceSet));
        }

        return map;
    }

    /**
     * add the jrxml into the ResourceSet after doMatch. Should call this method only after doMatch
     * 
     * @param rsJrxmlMap
     */
    protected void addJrxmlsIntoResourceSet(Map<ResourceSet, List<Resource>> rsJrxmlMap) {
        if (!rsJrxmlMap.isEmpty()) {
            ResourceSet resourceSet = rsJrxmlMap.keySet().iterator().next();
            List<Resource> jrxmlResources = rsJrxmlMap.get(resourceSet);
            if (resourceSet != null && !jrxmlResources.isEmpty()) {
                resourceSet.getResources().addAll(jrxmlResources);
            }
        }
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
            // remove the jrxml from the ResourceSet before doMatch
            Map<ResourceSet, List<Resource>> rsJrxmlMap = removeJrxmlsFromResourceSet();

            match = MatchService.doMatch(oldDataProvider, getSavedReloadObject(), options);

            // add the jrxml into the ResourceSet after doMatch
            addJrxmlsIntoResourceSet(rsJrxmlMap);
        } catch (InterruptedException e) {
            log.error(e, e);
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

    protected void handleDiffPackageElement(DiffElement difElement) {
        DiffGroup diffGroup = diffGroupSwitch.doSwitch(difElement);
        if (diffGroup != null) {
            EList<DiffElement> subDiffElements = diffGroup.getSubDiffElements();
            // Handle diff group
            for (DiffElement de : subDiffElements) {
                handleDiffPackageElement(de);
            }
            return;
        }

        ModelElementChangeRightTarget addElement = addModelSwitch.doSwitch(difElement);
        if (addElement != null) {
            handleAddElement(addElement);
            return;
        }

        // If attribute changes. MOD hcheng 2009-06-26,for 7772,error reload
        // column list.
        if (difElement instanceof UpdateAttribute) {
            handleUpdateElement((UpdateAttribute) difElement);
            return;
        }
        if (difElement instanceof ReferenceChangeLeftTarget) {
            handleReferenceValuesChange((ReferenceChangeLeftTarget) difElement);
            return;
        }

        if (difElement instanceof ReferenceChangeRightTarget) {
            handleReferenceValuesChange((ReferenceChangeRightTarget) difElement);
            return;
        }
        ModelElementChangeLeftTarget removeElement = removeModelSwitch.doSwitch(difElement);
        if (removeElement != null) {
            handleRemoveElement(removeElement);
        }
    }

    private void handleReferenceValuesChange(ReferenceChangeLeftTarget difElement) {
        removeRelationalSwitch.setLeftElement(difElement.getLeftElement());
        final Boolean updated = removeRelationalSwitch.doSwitch(difElement.getLeftTarget());
        if (!Boolean.TRUE.equals(updated)) {
            log.warn(DefaultMessagesImpl.getString("AbstractComparisonLevel.ElementNotUpdated", difElement.getLeftElement()));//$NON-NLS-1$
        }
    }

    private void handleReferenceValuesChange(ReferenceChangeRightTarget difElement) {
        addRelationalSwitch.setLeftElement(difElement.getLeftElement());
        final Boolean updated = addRelationalSwitch.doSwitch(difElement.getRightTarget());
        if (!Boolean.TRUE.equals(updated)) {
            log.warn(DefaultMessagesImpl.getString("AbstractComparisonLevel.ElementNotUpdated", difElement.getLeftElement()));//$NON-NLS-1$
        }
    }

    protected abstract void handleRemoveElement(ModelElementChangeLeftTarget removeElement);

    protected abstract void handleAddElement(ModelElementChangeRightTarget addElement);

    protected void handleUpdateElement(UpdateAttribute updateAttribute) {
        EObject leftElement = updateAttribute.getLeftElement();
        EObject rightElement = updateAttribute.getRightElement();
        this.updateRelationalStructSwitch.setLeftElement(leftElement);
        this.updateRelationalStructSwitch.setRightElement(rightElement);
        final Boolean updated = updateRelationalStructSwitch.doSwitch(leftElement);
        if (!Boolean.TRUE.equals(updated)) {
            log.warn(DefaultMessagesImpl.getString("AbstractComparisonLevel.ElementNotUpdated", leftElement));//$NON-NLS-1$
        }
    }

    protected void popRemoveElementConfirm() {
        if (!removeElementConfirm) {
            final Connection provider = oldDataProvider;
            if (guiHandler != null) {
                guiHandler.popRemoveElement(provider);
            }
            removeElementConfirm = true;
        }
    }

    protected Resource upperCaseResource(Resource res) {
        if (CASE_INSENSTIVE) {
            EList<EObject> contents = res.getContents();
            for (EObject eo : contents) {
                upperCase(eo);
            }
        }
        return res;
    }

    private Connection upperCaseConnection(Connection connection) {
        if (CASE_INSENSTIVE) {
            EList<ModelElement> ownedElement = connection.getOwnedElement();
            for (ModelElement me : ownedElement) {
                upperCase(me);
            }
        }
        return connection;
    }

    private void upperCase(EObject eObject) {
        Package pckg = SwitchHelpers.PACKAGE_SWITCH.doSwitch(eObject);
        ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(eObject);
        TdColumn column = SwitchHelpers.COLUMN_SWITCH.doSwitch(eObject);
        if (column != null) {
            column.setName(column.getName() == null ? "" : column.getName().toUpperCase());//$NON-NLS-1$
            column.setLabel(column.getLabel() == null ? "" : column.getLabel().toUpperCase());//$NON-NLS-1$
            column.getSqlDataType().setName(
                    column.getSqlDataType().getName() == null ? "" : column.getSqlDataType().getName().toUpperCase());//$NON-NLS-1$
            column.setContentType(column.getContentType() == null ? "" : column.getContentType().toUpperCase());//$NON-NLS-1$
            column.setSourceType(column.getSourceType() == null ? "" : column.getSourceType().toUpperCase());//$NON-NLS-1$
        } else if (pckg != null) {
            pckg.setName(pckg.getName() == null ? "" : pckg.getName().toUpperCase());//$NON-NLS-1$
        } else if (columnSet != null) {
            columnSet.setName(columnSet.getName() == null ? "" : columnSet.getName().toUpperCase());//$NON-NLS-1$
        }
    }
}
