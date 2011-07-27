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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.provider.DBTablesViewLabelProvider;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dataprofiler.core.ui.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.filters.TDQEEConnectionFolderFilter;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFColumnRepNode;
import org.talend.dq.nodes.DFTableRepNode;
import org.talend.dq.nodes.MDMSchemaRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * @author mzhao<br>
 * Select the special columns from this dialog.
 * 
 */
public class ColumnsSelectionDialog extends TwoPartCheckSelectionDialog {

    private static final int CHECKED_ELEMENTS_LENGTH = 200;
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
     * DOC mzhao bug 9240 mzhao 2009-11-05.
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
     * DOC mzhao Initiate the checked list in column selection dialog.
     * 
     * @param reposNodeLs
     */
    private void initCheckedElements(List<? extends IRepositoryNode> reposNodeLs) {
        List<IRepositoryNode> containerList = new ArrayList<IRepositoryNode>();
        for (int i = 0; i < reposNodeLs.size(); i++) {
            RepositoryNode repNode = (RepositoryNode) reposNodeLs.get(i);
            // IRepositoryNode parentNode = repNode.getParent().getParent();
            IRepositoryNode parentNode = getParentNode(repNode);
            // Add the parent->children relation to the multi map

            modelElementCheckedMap.put(parentNode, repNode);
            // Check the parent element either if any of their columns had already been checked.
            if (!containerList.contains(parentNode)) {
                containerList.add(parentNode);
            }
        }
        this.setInitialElementSelections(containerList);
    }

    /**
     * DOC xqliu Comment method "getParentNode".
     * 
     * @param repNode
     * @return
     */
    private IRepositoryNode getParentNode(RepositoryNode repNode) {
        IRepositoryNode node = repNode.getParent();
        if (repNode instanceof DBColumnRepNode || repNode instanceof DFColumnRepNode) {
            node = repNode.getParent().getParent();
        } else if (repNode instanceof MDMXmlElementRepNode) {
            node = repNode.getParent();
        }
        return node;
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
                        || selectedNode instanceof DFTableRepNode || selectedNode instanceof MDMSchemaRepNode
                        || selectedNode instanceof MDMXmlElementRepNode) {
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
        // RepositoryNode tableParent = reposNode.getParent().getParent();
        IRepositoryNode tableParent = getParentNode(reposNode);
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
            // MOD klliu 2011-03-03 bug 19195 the MDM node is defferent from DF/DB connection Structure
            // MDM does not have Column folder
            if (repNode instanceof MDMXmlElementRepNode) {
                Object[] children = sContentProvider.getElements(repNode);
                for (Object colNode : children) {
                    modelElementCheckedMap.put(repNode, colNode);
                }
            } else {
                List<IRepositoryNode> children = repNode.getChildren();
                for (IRepositoryNode colFolderNode : children) {
                    for (IRepositoryNode colNode : colFolderNode.getChildren()) {
                        modelElementCheckedMap.put(repNode, colNode);
                    }
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
                modelElementCheckedMap.clear();
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
        // MOD klliu 2011-01-27 15750 todo 31
        RepositoryNode selectedObj = (RepositoryNode) ((IStructuredSelection) event.getSelection()).getFirstElement();
        if (selectedObj != null) {
            if (selectedObj.hasChildren()) {
                this.setOutput(selectedObj);
                Boolean allCheckFlag = this.getTreeViewer().getChecked(selectedObj);
                List<?> repositoryNodeList = (List<?>) modelElementCheckedMap.get(selectedObj);
                if (repositoryNodeList != null) {
                    this.getTableViewer().setCheckedElements(repositoryNodeList.toArray());
                }// MOD klliu check Table/View node is select
                else if (selectedObj instanceof DBTableRepNode || selectedObj instanceof DBViewRepNode
                        || selectedObj instanceof DFTableRepNode) {
                    if (allCheckFlag) {
                        this.getTableViewer().setCheckedElements(selectedObj.getChildren().get(0).getChildren().toArray());
                    }
                } else if (selectedObj instanceof MDMXmlElementRepNode) {
                    if (allCheckFlag) {
                        this.getTableViewer().setCheckedElements(selectedObj.getChildren().toArray());
                    }
                }
                // ~
            }
        }

    }

    List<IRepositoryNode> allCheckedElements = new ArrayList<IRepositoryNode>();

    private void getAllCheckElements() {
        Object[] checkedNodes = this.getTreeViewer().getCheckedElements();
        for (int i = 0; i < checkedNodes.length; i++) {
            IRepositoryNode repNode = (IRepositoryNode) checkedNodes[i];
            if (repNode instanceof DBColumnRepNode || repNode instanceof DFColumnRepNode) {
                if (!allCheckedElements.contains(repNode)) {
                    allCheckedElements.add(repNode);
                }
            } else if (repNode instanceof DFTableRepNode || repNode instanceof DBTableRepNode || repNode instanceof DBViewRepNode) {
                if (!getTableviewCheckedElements(allCheckedElements, repNode)) {
                    List<IRepositoryNode> children = repNode.getChildren().get(0).getChildren();
                    for (IRepositoryNode node : children) {
                        if (!allCheckedElements.contains(node)) {
                            allCheckedElements.add(node);
                        }
                    }
                }
            } else if (repNode instanceof MDMXmlElementRepNode) {
                boolean isLeaf = RepositoryNodeHelper.getMdmChildren(repNode, true).length > 0;
                if (!isLeaf) {
                    if (!getTableviewCheckedElements(allCheckedElements, repNode)) {
                        List<IRepositoryNode> children = repNode.getChildren();
                        allCheckedElements.addAll(children);
                    }
                }
            }
        }

        // ADD msjian 2011-7-8 feature 22206: Add filters
        Iterator<?> it = modelElementCheckedMap.keySet().iterator();
        while (it.hasNext()) {
            RepositoryNode reposNode = (RepositoryNode) it.next();
            List<?> columnList = (ArrayList<?>) modelElementCheckedMap.get(reposNode);
            for (int i = 0; i < columnList.size(); i++) {
                if (columnList.get(i) instanceof DBColumnRepNode || columnList.get(i) instanceof DFColumnRepNode) {
                    if (!allCheckedElements.contains(columnList.get(i))) {
                        allCheckedElements.add((IRepositoryNode) columnList.get(i));
                    }
                }
            }
        }
    }

    private boolean getTableviewCheckedElements(List<IRepositoryNode> allCheckedElements, IRepositoryNode repNode) {
        List<IRepositoryNode> columnFolder = (List<IRepositoryNode>) modelElementCheckedMap.get(repNode);
        if (columnFolder == null) {
            return false;
        }
        for (IRepositoryNode column : columnFolder) {
            // MOD msjian 2011-7-25 22206: for filter, avoid to add the exsit column
            if (!allCheckedElements.contains(column)) {
                allCheckedElements.add(column);
            }
        }
        return columnFolder.size() > 0;
    }



    protected void okPressed() {
        allCheckedElements.clear();
        getAllCheckElements();
        if (allCheckedElements.size() > CHECKED_ELEMENTS_LENGTH) {
            MessageDialog
                    .openWarning(
                            this.getShell(),
                            DefaultMessagesImpl.getString("ColumnsSelectionDialog.ColumnSelection"), "Exceed maximum column restrictions: " + CHECKED_ELEMENTS_LENGTH);//$NON-NLS-1$
        } else {
            super.okPressed();
            ConnectionItem connectionItem = (ConnectionItem) connNode.getObject().getProperty().getItem();
            ElementWriterFactory.getInstance().createDataProviderWriter().save(connectionItem);
            this.modelElementCheckedMap = null;
        }
    }

    @Override
    protected void computeResult() {
        setResult(allCheckedElements);
    }

    /**
     * @author rli
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    class ModelElementContentProvider extends ResourceViewContentProvider {

        @Override
        public Object[] getChildren(Object parentElement) {
            // Table || view node
            if (parentElement instanceof DBTableRepNode || parentElement instanceof DBViewRepNode
                    || parentElement instanceof DFTableRepNode) {
                IRepositoryNode repoNode = (IRepositoryNode) parentElement;
                // feature 22206 2011-7-12 msjian: fixed its note 91852 issue1
                if (repoNode.getChildren().size() == 0) {
                    return repoNode.getChildren().toArray();
                }
                return repoNode.getChildren().get(0).getChildren().toArray();
            }
            // if (parentElement instanceof DBColumnFolderRepNode || parentElement instanceof MDMSchemaRepNode) {
            // IRepositoryNode repoNode = (IRepositoryNode) parentElement;
            // return repoNode.getChildren().toArray();
            // }
            if (parentElement instanceof DBColumnRepNode || parentElement instanceof DFColumnRepNode) {
                return new Object[] { parentElement };
            }

            if (parentElement instanceof MDMSchemaRepNode || parentElement instanceof MDMXmlElementRepNode) {
                return RepositoryNodeHelper.getMdmChildren(parentElement, false);
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
    }

    /**
     * DOC zqin ColumnsSelectionDialog class global comment. Detailled comment
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    class DBTreeViewContentProvider extends ResourceViewContentProvider {

        public DBTreeViewContentProvider() {
            super();
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof IRepositoryNode) {
                if (parentElement instanceof MDMSchemaRepNode || parentElement instanceof MDMXmlElementRepNode) {
                    return RepositoryNodeHelper.getMdmChildren(parentElement, true);
                }
                // IRepositoryNode repoNode = (IRepositoryNode) parentElement;
                // return repoNode.getChildren().toArray();
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
                if (repoNode instanceof DBTableRepNode || repoNode instanceof DBViewRepNode || repoNode instanceof DFTableRepNode) {
                    return Boolean.FALSE;
                } else if (repoNode instanceof MDMSchemaRepNode || repoNode instanceof MDMXmlElementRepNode) {
                    return RepositoryNodeHelper.getMdmChildren(element, true).length > 0;
                }
                return repoNode.hasChildren();
            }
            return superHasChildren(element);
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
