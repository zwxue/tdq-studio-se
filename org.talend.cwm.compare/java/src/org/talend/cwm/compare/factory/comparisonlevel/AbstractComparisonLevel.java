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
package org.talend.cwm.compare.factory.comparisonlevel;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.compare.factory.IUIHandler;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.util.RelationalSwitch;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractComparisonLevel implements IComparisonLevel {

    private static Logger log = Logger.getLogger(AbstractComparisonLevel.class);

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
        // MOD mzhao 2009-01-20 Extract method openDiffCompareEditor to class
        // DQStructureComparer.
        DQStructureComparer.openDiffCompareEditor(getLeftResource(), getRightResource(), options, guiHandler, DQStructureComparer
                .getDiffResourceFile());

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
        // MOD mzhao ,Extract method getRefreshedDataProvider to class
        // DQStructureComparer for common use.
        TypedReturnCode<TdDataProvider> returnProvider = DQStructureComparer.getRefreshedDataProvider(oldDataProvider);
        if (!returnProvider.isOk()) {
            throw new ReloadCompareException(returnProvider.getMessage());
        }
        tempReloadProvider = returnProvider.getObject();
        tempReloadProvider.setComponent(oldDataProvider.getComponent());
        DqRepositoryViewService.saveDataProviderResource(tempReloadProvider, (IFolder) tempConnectionFile.getParent(),
                tempConnectionFile);
        tempReloadProvider.setComponent(null);
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

}
