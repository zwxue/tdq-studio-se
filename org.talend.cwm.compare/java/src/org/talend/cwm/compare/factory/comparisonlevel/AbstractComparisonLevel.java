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
package org.talend.cwm.compare.factory.comparisonlevel;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.compare.factory.IUIHandler;
import org.talend.cwm.compare.factory.update.AddTdRelationalSwitch;
import org.talend.cwm.compare.factory.update.RemoveTdRelationalSwitch;
import org.talend.cwm.compare.factory.update.UpdateTdRelationalSwitch;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.AbstractDatabaseFolderNode;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.objectmodel.core.util.CoreSwitch;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractComparisonLevel implements IComparisonLevel {

    private static Logger log = Logger.getLogger(AbstractComparisonLevel.class);

    protected DiffSwitch<ModelElementChangeRightTarget> addModelSwitch;

    protected DiffSwitch<UpdateModelElement> updateModelSwitch;

    protected DiffSwitch<ModelElementChangeLeftTarget> removeModelSwitch;

    protected DiffSwitch<ReferenceChangeRightTarget> removeReferenceValueSwitch;

    protected DiffSwitch<ReferenceChangeLeftTarget> addReferenceValueSwitch;

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
    }

    private void initSwitchValue() {

        addModelSwitch = new DiffSwitch<ModelElementChangeRightTarget>() {

            @Override
            public ModelElementChangeRightTarget caseModelElementChangeRightTarget(ModelElementChangeRightTarget object) {
                return object;
            }

        };

        updateModelSwitch = new DiffSwitch<UpdateModelElement>() {

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

            public Package casePackage(Package object) {
                return object;
            }
        };
    }

    public Connection reloadCurrentLevelElement() throws ReloadCompareException {
        if (!isValid()) {
            return null;
        }
        oldDataProvider = findDataProvider();
        if (oldDataProvider == null) {
            return null;
        }
        createTempConnectionFile();

        if (compareWithReloadObject()) {
            saveReloadResult();
        }

        return oldDataProvider;
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
        // DQStructureComparer.deleteCopiedResourceFile();
        // DQStructureComparer.deleteNeedReloadElementFile();
        createTempConnectionFile();
        createCopyedProvider();
        // MOD mzhao 2009-01-20 Extract method openDiffCompareEditor to class
        // DQStructureComparer.
        // MOD mzhao 2009-03-09 add param dbname for displaying datasource(db
        // name) in compare
        // editor.

        Object needReloadObject = dbFolderNode == null ? selectedObj : dbFolderNode;
        DQStructureComparer.openDiffCompareEditor(getLeftResource(), getRightResource(), options, guiHandler, DQStructureComparer
                .getDiffResourceFile(), oldDataProvider.getName(), needReloadObject, false);

        // testInit();

    }

    protected void createCopyedProvider() {
        if (oldDataProvider.eIsProxy()) {
            ResourceSet resourceSet = ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider()
                    .getResourceManager().resourceSet;
            oldDataProvider = (Connection) EcoreUtil.resolve(oldDataProvider, resourceSet);
        }
        IFile selectedFile = WorkspaceUtils.getModelElementResource(oldDataProvider);
        IFile createNeedReloadElementsFile = DQStructureComparer.getNeedReloadElementsFile();
        IFile copyedFile = DQStructureComparer.copyedToDestinationFile(selectedFile, createNeedReloadElementsFile);
        TypedReturnCode<Connection> returnValue = DqRepositoryViewService.readFromFile(copyedFile);
        copyedDataProvider = returnValue.getObject();

    }

    /**
     * DOC rli Comment method "createTempConnectionFile".
     * 
     * @throws ReloadCompareException
     */
    protected void createTempConnectionFile() throws ReloadCompareException {
        IFile tempConnectionFile = DQStructureComparer.getTempRefreshFile();
        // MOD mzhao ,Extract method getRefreshedDataProvider to class
        // DQStructureComparer for common use.
        TypedReturnCode<Connection> returnProvider = DQStructureComparer.getRefreshedDataProvider(oldDataProvider);
        if (!returnProvider.isOk()) {
            throw new ReloadCompareException(returnProvider.getMessage());
        }
        tempReloadProvider = returnProvider.getObject();
        tempReloadProvider.setComponent(oldDataProvider.getComponent());
        // MOD mzhao bug:9012 2009-09-08
        ElementWriterFactory.getInstance().createDataProviderWriter().create(tempReloadProvider,
                tempConnectionFile.getFullPath(), false);
        tempReloadProvider.setComponent(null);
        oldDataProvider.getComponent();
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
        PrvResourceFileHelper.getInstance().save(oldDataProvider);
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
            if (oldDataProvider.eIsProxy()) {
                ResourceSet resourceSet = ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider()
                        .getResourceManager().resourceSet;
                oldDataProvider = (Connection) EcoreUtil.resolve(oldDataProvider, resourceSet);
            }
            match = MatchService.doMatch(oldDataProvider, getSavedReloadObject(), options);
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
            log.warn("Element not updated: " + difElement.getLeftElement());
        }
    }

    private void handleReferenceValuesChange(ReferenceChangeRightTarget difElement) {
        addRelationalSwitch.setLeftElement(difElement.getLeftElement());
        final Boolean updated = addRelationalSwitch.doSwitch(difElement.getRightTarget());
        if (!Boolean.TRUE.equals(updated)) {
            log.warn("Element not updated: " + difElement.getLeftElement());
        }
    }

    protected abstract void handleRemoveElement(ModelElementChangeLeftTarget removeElement);

    protected abstract void handleAddElement(ModelElementChangeRightTarget addElement);

    protected void handleUpdateElement(UpdateAttribute updateAttribute) {
        EObject leftElement = updateAttribute.getLeftElement();
        EObject rightElement = updateAttribute.getRightElement();
        this.updateRelationalStructSwitch.setRightElement(rightElement);
        final Boolean updated = updateRelationalStructSwitch.doSwitch(leftElement);
        if (!Boolean.TRUE.equals(updated)) {
            log.warn("Element not updated: " + leftElement);
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

}
