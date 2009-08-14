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

package org.talend.cwm.compare.ui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffGroup;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.api.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.helper.FolderNodeHelper;
import org.talend.dq.connection.DataProviderWriter;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class RenameComparedElementAction extends Action {

    private static Logger log = Logger.getLogger(RenameComparedElementAction.class);

    private ColumnSet theSelectedElement = null;

    private Package originCompareElement = null;

    private IFolderNode selectedFolderNode = null;

    private Map<String, Object> options = null;

    private List<ColumnSet> newAddedColumnSet = null;

    public RenameComparedElementAction(IFolderNode selectedFolderNode, ColumnSet theSelectedElement,
            List<ColumnSet> addElementList) {
        this.selectedFolderNode = selectedFolderNode;
        this.originCompareElement = (Package) selectedFolderNode.getParent();
        this.theSelectedElement = theSelectedElement;

        this.newAddedColumnSet = addElementList;
        options = new HashMap<String, Object>();
        options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
    }

    @Override
    public String getText() {
        return "Renamed element";
    }

    @Override
    public void run() {
        // Open the add element model dialog

        if (newAddedColumnSet == null || newAddedColumnSet.size() == 0) {
            MessageDialog.openConfirm(null, "", "There is no newly added element on right side to be propagated!");
            return;
        }

        RightPanelAddedElementsDialog addedEleDialog = new RightPanelAddedElementsDialog(null, newAddedColumnSet);
        if (addedEleDialog.open() != Window.OK) {
            return;
        }
        // Propagate the changes

        theSelectedElement.setNamespace(originCompareElement);
        ColumnSet checkedColumnSet = addedEleDialog.getCheckedColumnSet();
        checkedColumnSet.setNamespace(originCompareElement);

        // Check if the sub structure is the same.
        List<DiffElement> diffElementList = checkSubStructure(checkedColumnSet);
        originCompareElement.getOwnedElement().remove(theSelectedElement);
        originCompareElement.getOwnedElement().remove(checkedColumnSet);

        if (diffElementList != null && diffElementList.size() != 0) {
            if (!MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "",
                    "The substructure changed, Propagate or not?")) {
                refreshEditor();
                return;
            }
        }
        // Save to the copied resource.
        refreshReposigoryTree(checkedColumnSet);
        refreshEditor();
    }

    public void refreshEditor() {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        activePage.closeEditor(activePage.getActiveEditor(), false);
        new PopComparisonUIAction(selectedFolderNode, "Compare").run();
    }

    private void refreshReposigoryTree(ColumnSet checkedColumnSet) {
        // ~ Save to the original resource.

        // Remove theSelectedElement by iteratively comparing its name.
        // Because theSelectedElement in compare editor is not the same
        // instance with that of in repository tree.
        for (Iterator<ModelElement> it = originCompareElement.getOwnedElement().iterator(); it.hasNext();) {
            if (it.next().getName().equalsIgnoreCase(theSelectedElement.getName())) {
                it.remove();
                break;
            }

        }
        originCompareElement.getOwnedElement().add(checkedColumnSet);
        EMFSharedResources.getInstance().saveResource(originCompareElement.eResource());
        // ~
    }

    private List<DiffElement> checkSubStructure(ColumnSet checkedColumnSet) {
        MatchModel match = null;

        try {
            match = MatchService.doResourceMatch(getLeftResource(), getRightResource(checkedColumnSet), options);

        } catch (InterruptedException e) {
            log.error(e, e);
        } catch (ReloadCompareException e) {
            log.error(e, e);
        }
        List<DiffElement> subDeffElements = new ArrayList<DiffElement>();
        final DiffModel diff = DiffService.doDiff(match);
        if (diff.getOwnedElements() != null && diff.getOwnedElements().size() > 0) {
            for (DiffElement diffEle : diff.getOwnedElements()) {
                getDiffElements(diffEle, subDeffElements);
            }
        }
        return subDeffElements;
    }

    private void getDiffElements(DiffElement diffEle, List<DiffElement> diffElementList) {
        if (diffEle instanceof DiffGroup) {
            for (DiffElement subDiffEle : ((DiffGroup) diffEle).getSubDiffElements()) {
                getDiffElements(subDiffEle, diffElementList);
            }
        } else {
            diffElementList.add(diffEle);
        }
    }

    private Resource getLeftResource() throws ReloadCompareException {
        ColumnSet selectedColumnSet = theSelectedElement;
        TdDataProvider copyedDataProvider = createCopyedProvider();
        ColumnSet findMatchedColumnSet = DQStructureComparer.findMatchedColumnSet(selectedColumnSet, copyedDataProvider);
        List<TdColumn> columnList = new ArrayList<TdColumn>();
        columnList.addAll(ColumnSetHelper.getColumns(findMatchedColumnSet));

        // URI uri =
        // URI.createPlatformResourceURI(copyedFile.getFullPath().toString(),
        // false);
        Resource leftResource = copyedDataProvider.eResource();
        // leftResource = EMFSharedResources.getInstance().getResource(uri,
        // true);
        // if (leftResource == null) {
        // throw new
        // ReloadCompareException("No factory has been found for URI: " + uri);
        // }

        leftResource.getContents().clear();
        for (TdColumn column : columnList) {
            DQStructureComparer.clearSubNode(column);
            leftResource.getContents().add(column);
        }
        EMFSharedResources.getInstance().saveResource(leftResource);
        return leftResource;
    }

    private TdDataProvider createCopyedProvider() {
        IFile selectedFile = PrvResourceFileHelper.getInstance().findCorrespondingFile(
                (TdDataProvider) originCompareElement.getDataManager().get(0));
        IFile createNeedReloadElementsFile = DQStructureComparer.getFirstComparisonLocalFile();
        IFile copyedFile = DQStructureComparer.copyedToDestinationFile(selectedFile, createNeedReloadElementsFile);
        TypedReturnCode<TdDataProvider> returnValue = DqRepositoryViewService.readFromFile(copyedFile);
        return returnValue.getObject();

    }

    private Resource getRightResource(ColumnSet selectedColumnSet) throws ReloadCompareException {
        TdDataProvider tempReloadProvider = createTempConnectionFile();
        Package matchedPackage = DQStructureComparer.findMatchedPackage(originCompareElement, tempReloadProvider);
        IFolderNode columnSetFolderNode = FolderNodeHelper.getFolderNode(matchedPackage, selectedColumnSet);
        columnSetFolderNode.loadChildren();

        ColumnSet findMatchedColumnSet = DQStructureComparer.findMatchedColumnSet(selectedColumnSet, tempReloadProvider);
        List<TdColumn> columns = null;
        try {
            columns = DqRepositoryViewService.getColumns(tempReloadProvider, findMatchedColumnSet, null, true);
        } catch (TalendException e1) {
            throw new ReloadCompareException(e1);
        }

        URI uri = tempReloadProvider.eResource().getURI();
        Resource rightResource = null;
        rightResource = EMFSharedResources.getInstance().getResource(uri, true);
        if (rightResource == null) {
            throw new ReloadCompareException("NoFactoryFoundForURI" + uri.toFileString()); //$NON-NLS-1$
        }
        rightResource.getContents().clear();
        for (TdColumn column : columns) {
            DQStructureComparer.clearSubNode(column);
            rightResource.getContents().add(column);
        }
        EMFSharedResources.getInstance().saveResource(rightResource);
        return rightResource;
    }

    private TdDataProvider createTempConnectionFile() throws ReloadCompareException {
        TdDataProvider oldDataProvider = (TdDataProvider) originCompareElement.getDataManager().get(0);
        IFile tempConnectionFile = DQStructureComparer.getSecondComparisonLocalFile();
        // MOD mzhao ,Extract method getRefreshedDataProvider to class
        // DQStructureComparer for common use.
        TypedReturnCode<TdDataProvider> returnProvider = DQStructureComparer.getRefreshedDataProvider(oldDataProvider);
        if (!returnProvider.isOk()) {
            throw new ReloadCompareException(returnProvider.getMessage());
        }
        TdDataProvider tempReloadProvider = returnProvider.getObject();
        tempReloadProvider.setComponent(oldDataProvider.getComponent());
        DataProviderWriter.getInstance().saveDataProviderResource(tempReloadProvider, (IFolder) tempConnectionFile.getParent(),
                tempConnectionFile);
        tempReloadProvider.setComponent(null);
        oldDataProvider.getComponent();
        return tempReloadProvider;
    }

    /**
     * 
     * DOC mzhao RenameComparedElementAction class global comment. Detailled comment
     */
    private class RightPanelAddedElementsDialog extends Dialog {

        private CheckboxTableViewer tableViewer = null;

        private List<ColumnSet> newAddedColumnSet;

        private ColumnSet checkedColumnSet = null;

        protected RightPanelAddedElementsDialog(Shell parentShell, List<ColumnSet> newAddedColumnSet) {
            super(parentShell);
            this.newAddedColumnSet = newAddedColumnSet;
        }

        public ColumnSet getCheckedColumnSet() {
            return checkedColumnSet;
        }

        @Override
        protected boolean isResizable() {
            return true;
        }

        /*
         * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets .Shell)
         */
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            newShell.setText("New name of the element");
        }

        protected Point getInitialSize() {
            return new Point(455, 340);
        }

        @Override
        protected void okPressed() {
            if (tableViewer.getCheckedElements().length > 0) {
                checkedColumnSet = (ColumnSet) tableViewer.getCheckedElements()[0];
            }

            super.okPressed();
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = (Composite) super.createDialogArea(parent);
            composite.setLayout(new FillLayout());
            tableViewer = CheckboxTableViewer.newCheckList(composite, SWT.SINGLE);
            tableViewer.setContentProvider(new IStructuredContentProvider() {

                public Object[] getElements(Object inputElement) {
                    if (inputElement instanceof List) {
                        return ((List<?>) inputElement).toArray();
                    }
                    return new Object[] { inputElement };
                }

                public void dispose() {
                }

                public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                }

            });

            tableViewer.setLabelProvider(new ILabelProvider() {

                public Image getImage(Object element) {
                    return null;
                }

                public String getText(Object element) {
                    ColumnSet columnSet = (ColumnSet) element;
                    return columnSet.getName();
                }

                public void addListener(ILabelProviderListener listener) {

                }

                public void dispose() {
                }

                public boolean isLabelProperty(Object element, String property) {
                    return false;
                }

                public void removeListener(ILabelProviderListener listener) {

                }
            });
            tableViewer.setInput(newAddedColumnSet);
            tableViewer.addCheckStateListener(new ICheckStateListener() {

                public void checkStateChanged(CheckStateChangedEvent event) {
                    tableViewer.setAllChecked(false);
                    tableViewer.setChecked(event.getElement(), event.getChecked());
                }

            });
            return composite;
        }
    }

}
