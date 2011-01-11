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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataprofiler.core.ui.dialog.provider.DBTablesViewLabelProvider;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dataprofiler.core.ui.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.filters.TDQEEConnectionFolderFilter;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFColumnRepNode;
import org.talend.dq.nodes.DFTableRepNode;
import org.talend.dq.nodes.MDMSchemaRepNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * @author mzhao<br>
 * Select the special columns from this dialog.
 * 
 */
public class ColumnsSelectionDialog extends TwoPartCheckSelectionDialog {

    // private static Logger log = Logger.getLogger(ColumnsSelectionDialog.class);

    // Multiple map: one left checked element map to multiple right checked map. (e.g:
    // tableA->columnA,columnB,columnC...)
    private MultiMap modelElementCheckedMap = null;

    // Root node (database/xml/flat file connection)
    private RepositoryNode connNode = null;

    public ColumnsSelectionDialog(AbstractAnalysisMetadataPage metadataFormPage, Shell parent, String title,
            List<? extends IRepositoryNode> checkedRepoNodes, RepositoryNode rootNode, String message) {
        super(metadataFormPage, parent, message);
        connNode = rootNode;
        modelElementCheckedMap = new MultiValueMap();
        initCheckedElements(checkedRepoNodes);
        addFilter(new EMFObjFilter());
        addFilter(new DQFolderFliter(true));
        addFilter(new TDQEEConnectionFolderFilter());
        setInput(connNode);
        setTitle(title);
    }

    @Override
    /**
     * DOC mzhao bug 9240 mzhao 2009-11-05
     */
    protected void unfoldToCheckedElements() {
        Iterator<?> it = modelElementCheckedMap.keySet().iterator();
        while (it.hasNext()) {
            RepositoryNode reposNode = (RepositoryNode) it.next();
            getTreeViewer().expandToLevel(reposNode, 1);
            StructuredSelection structSel = new StructuredSelection(reposNode);
            getTreeViewer().setSelection(structSel);
        }
    }

    /**
     * 
     * DOC mzhao Initiate the checked list in column selection dialog
     * 
     * @param reposNodeLs
     */
    private void initCheckedElements(List<? extends IRepositoryNode> reposNodeLs) {
        List<IRepositoryNode> containerList = new ArrayList<IRepositoryNode>();
        for (int i = 0; i < reposNodeLs.size(); i++) {
            RepositoryNode repNode = (RepositoryNode) reposNodeLs.get(i);
            IRepositoryNode parentNode = repNode.getParent().getParent();
            // Add the parent->children relation to the multi map

            modelElementCheckedMap.put(parentNode, repNode);
            // Check the parent element either if any of their columns had already been checked.
            if (!containerList.contains(parentNode)) {
                containerList.add(parentNode);
            }
        }
        this.setInitialElementSelections(containerList);
    }

    protected void initProvider() {
        fLabelProvider = new DBTablesViewLabelProvider();
        fContentProvider = new DBTreeViewContentProvider();
        sLabelProvider = new DBTablesViewLabelProvider();
        sContentProvider = new ModelElementContentProvider();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataprofiler.core.ui.dialog.TwoPartCheckSelectionDialog# addCheckedListener()
     */
    protected void addCheckedListener() {

        // When user checks a checkbox in the tree, check all its children
        getTreeViewer().addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                ColumnSelectionViewer columnViewer = (ColumnSelectionViewer) event.getSource();
                TreePath treePath = new TreePath(new Object[] { event.getElement() });
                columnViewer.setSelection(new TreeSelection(treePath));
                getTreeViewer().setSubtreeChecked(event.getElement(), event.getChecked());
                setOutput(event.getElement());
                RepositoryNode selectedNode = (RepositoryNode) event.getElement();
                if (selectedNode instanceof DBTableRepNode || selectedNode instanceof DBViewRepNode
                        || selectedNode instanceof MDMSchemaRepNode || selectedNode instanceof DFTableRepNode) {
                    handleTreeElementsChecked(selectedNode, event.getChecked());
                }
            }
        });

        getTableViewer().addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                handleTableElementsChecked((RepositoryNode) event.getElement(), event.getChecked());
            }
        });
    }

    /**
     * 
     * DOC mzhao Handle check state of table view elements. propagate to tree viewer and multi map.
     * 
     * @param reposNode
     * @param checkedFlag
     */
    private void handleTableElementsChecked(RepositoryNode reposNode, Boolean checkedFlag) {
        RepositoryNode tableParent = reposNode.getParent().getParent();
        if (checkedFlag) {
            getTreeViewer().setChecked(tableParent, checkedFlag);
            modelElementCheckedMap.put(tableParent, reposNode);
        } else {
            modelElementCheckedMap.remove(tableParent, reposNode);
            // If there is no elements checked in table view (right panel), then the parent element in tree viewer
            // should be unchecked.
            List<?> checkedList = (List<?>) modelElementCheckedMap.get(tableParent);
            if (checkedList == null || checkedList.size() == 0) {
                getTreeViewer().setChecked(tableParent, checkedFlag);
            }
        }
    }

    /**
     * 
     * DOC mzhao Handle check events. propagate the state to the table viewer and update the multi map.
     * 
     * @param repNode
     * @param checkedFlag
     */
    private void handleTreeElementsChecked(RepositoryNode repNode, Boolean checkedFlag) {
        if (checkedFlag) {
            List<IRepositoryNode> children = repNode.getChildren();
            for (IRepositoryNode colFolderNode : children) {
                for (IRepositoryNode colNode : colFolderNode.getChildren()) {
                    modelElementCheckedMap.put(repNode, colNode);
                }
            }

        } else {
            modelElementCheckedMap.remove(repNode);
        }
        getTableViewer().setAllChecked(checkedFlag);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataprofiler.core.ui.dialog.TwoPartCheckSelectionDialog#
     * addSelectionButtonListener(org.eclipse.swt .widgets.Button, org.eclipse.swt.widgets.Button)
     */
    protected void addSelectionButtonListener(Button selectButton, Button deselectButton) {
        SelectionListener listener = new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                Object[] viewerElements = fContentProvider.getElements(getTreeViewer().getInput());
                if (fContainerMode) {
                    getTreeViewer().setCheckedElements(viewerElements);
                } else {
                    for (int i = 0; i < viewerElements.length; i++) {
                        getTreeViewer().setSubtreeChecked(viewerElements[i], true);
                    }
                }
                // modelElementCheckedMap.clear();
                if (getTableViewer().getInput() != null) {
                    handleTreeElementsChecked((RepositoryNode) getTableViewer().getInput(), true);
                }
                updateOKStatus();
            }
        };
        selectButton.addSelectionListener(listener);

        listener = new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                getTreeViewer().setCheckedElements(new Object[0]);
                modelElementCheckedMap.clear();
                if (getTableViewer().getInput() != null) {
                    handleTreeElementsChecked((RepositoryNode) getTableViewer().getInput(), false);
                }
                updateOKStatus();
            }
        };
        deselectButton.addSelectionListener(listener);
    }

    public void selectionChanged(SelectionChangedEvent event) {
        RepositoryNode selectedObj = (RepositoryNode) ((IStructuredSelection) event.getSelection()).getFirstElement();
        if (selectedObj.hasChildren()) {
            this.setOutput(selectedObj);
            List<?> repositoryNodeList = (List<?>) modelElementCheckedMap.get(selectedObj);
            if (repositoryNodeList != null) {
                this.getTableViewer().setCheckedElements(repositoryNodeList.toArray());
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void computeResult() {
        List<RepositoryNode> allCheckedElements = new ArrayList<RepositoryNode>();
        Iterator<?> keyIterate = modelElementCheckedMap.keySet().iterator();
        while (keyIterate.hasNext()) {
            RepositoryNode repNode = (RepositoryNode) keyIterate.next();
            // Parent
            allCheckedElements.add(repNode);
            // Children
            List<RepositoryNode> columnFolder = (List<RepositoryNode>) modelElementCheckedMap.get(repNode);
            for (RepositoryNode column : columnFolder) {
                allCheckedElements.add(column);
            }
        }
        setResult(allCheckedElements);
    }

    protected void okPressed() {
        super.okPressed();
        ConnectionItem connectionItem = (ConnectionItem) connNode.getObject().getProperty().getItem();
        ElementWriterFactory.getInstance().createDataProviderWriter().save(connectionItem);
        this.modelElementCheckedMap = null;
    }

    /**
     * @author rli
     */
    class ModelElementContentProvider extends ResourceViewContentProvider {

        @Override
        public Object[] getChildren(Object parentElement) {
            // Table || view node
            if (parentElement instanceof DBTableRepNode || parentElement instanceof DBViewRepNode
                    || parentElement instanceof DFTableRepNode) {
                IRepositoryNode repoNode = (IRepositoryNode) parentElement;
                return repoNode.getChildren().get(0).getChildren().toArray();
            }
            // if (parentElement instanceof DBColumnFolderRepNode || parentElement instanceof MDMSchemaRepNode) {
            // IRepositoryNode repoNode = (IRepositoryNode) parentElement;
            // return repoNode.getChildren().toArray();
            // }
            if (parentElement instanceof DBColumnRepNode || parentElement instanceof DFColumnRepNode) {
                return new Object[] { parentElement };
            }

            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof RepositoryNode) {
                RepositoryNode repoNode = (RepositoryNode) element;
                return repoNode.getParent();
            }
            return super.getParent(element);
        }

        public boolean hasChildren(Object element) {
            return Boolean.FALSE;
            // ~
        }

        /**
         * If element if TdXmlElementType, the super method hasChildren() return wrong result, so add use this method to
         * get the right result. xqliu 2010-02-04
         * 
         * @param element
         * @return
         */
        private boolean superHasChildren(Object element) {
            boolean hasChildren = super.hasChildren(element);
            if (element instanceof EObject) {
                EObject eobject = (EObject) element;
                if (SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(eobject) != null) {
                    hasChildren = !hasChildren;
                }
            }

            return hasChildren;
        }
    }

    /**
     * DOC zqin ColumnsSelectionDialog class global comment. Detailled comment
     */
    class DBTreeViewContentProvider extends ResourceViewContentProvider {

        // FIXME_15750 This content provider might be reused with the DQ repository view tree viewer.
        /**
         * @param adapterFactory
         */
        public DBTreeViewContentProvider() {
            super();
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof IRepositoryNode) {
                IRepositoryNode repoNode = (IRepositoryNode) parentElement;
                return repoNode.getChildren().toArray();
            }

            return super.getChildren(parentElement);
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof RepositoryNode) {
                RepositoryNode repoNode = (RepositoryNode) element;
                return repoNode.getParent();
            }
            return super.getParent(element);
        }

        public boolean hasChildren(Object element) {
            if (element instanceof RepositoryNode) {
                RepositoryNode repoNode = (RepositoryNode) element;
                if (repoNode instanceof DBTableRepNode || repoNode instanceof DBViewRepNode
                        || repoNode instanceof MDMSchemaRepNode || repoNode instanceof DFTableRepNode) {
                    return Boolean.FALSE;
                }
                return repoNode.hasChildren();
            }
            return superHasChildren(element); // ???
            // ~
        }

        /**
         * If element if TdXmlElementType, the super method hasChildren() return wrong result, so add use this method to
         * get the right result. xqliu 2010-02-04
         * 
         * @param element
         * @return
         */
        private boolean superHasChildren(Object element) {
            boolean hasChildren = super.hasChildren(element);
            if (element instanceof EObject) {
                EObject eobject = (EObject) element;
                if (SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(eobject) != null) {
                    hasChildren = !hasChildren;
                }
            }

            return hasChildren;
        }

    }
}
