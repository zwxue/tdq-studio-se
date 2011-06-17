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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.dialog.TwoPartCheckSelectionDialog;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dataprofiler.core.ui.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.filters.TDQEEConnectionFolderFilter;
import org.talend.dataprofiler.core.ui.filters.TypedViewerFilter;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewLabelProvider;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider;
import org.talend.dataprofiler.core.ui.wizard.analysis.table.TableContentProvider;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DBCatalogRepNode;
import org.talend.dq.nodes.DBSchemaRepNode;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewFolderRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFConnectionFolderRepNode;
import org.talend.dq.nodes.MDMConnectionFolderRepNode;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TablesSelectionDialog extends TwoPartCheckSelectionDialog {

    private static Logger log = Logger.getLogger(TablesSelectionDialog.class);

    private Map<RepositoryNodeKey, TableCheckedMap> packageCheckedMap;

    private RepositoryNode metadataFolder = RepositoryNodeHelper.getRootNode(ERepositoryObjectType.METADATA);

    private TableSelectionType tableType;

    public TableSelectionType getTableType() {
        return this.tableType;
    }

    public void setTableType(TableSelectionType tableType) {
        this.tableType = tableType;
    }

    public TablesSelectionDialog(AbstractAnalysisMetadataPage metadataFormPage, Shell parent, String title,
            List<IRepositoryNode> setList, String message) {
        super(metadataFormPage, parent, message);
        this.setDialogType(DIALOG_TYPE_TABLE);
        addFirstPartFilters();
        this.setInput(metadataFolder);
        packageCheckedMap = new HashMap<RepositoryNodeKey, TableCheckedMap>();
        initCheckedTable(setList);
        this.setTitle(title);
        this.tableType = TableSelectionType.TABLE;
    }

    @Override
    /**
     * DOC mzhao bug 9240 mzhao 2009-11-05.
     * 
     * @param columnSetList
     */
    protected void unfoldToCheckedElements() {
        Iterator<RepositoryNodeKey> it = packageCheckedMap.keySet().iterator();
        while (it.hasNext()) {
            RepositoryNodeKey csk = it.next();
            getTreeViewer().expandToLevel(csk.getPackageNode(), 1);
            IRepositoryNode packageNode = csk.getPackageNode();
            List<IRepositoryNode> filterTableView = filterTableView(packageNode.getChildren());
            StructuredSelection structSel = new StructuredSelection(packageNode);
            if (filterTableView.size() > 0) {
                structSel = new StructuredSelection(filterTableView.get(0));
            }
            getTreeViewer().setSelection(structSel);
        }
    }

    private void initCheckedTable(List<IRepositoryNode> tableList) {
        List<IRepositoryNode> packageList = new ArrayList<IRepositoryNode>();
        for (IRepositoryNode tableNode : tableList) {
            if (tableNode instanceof DBTableRepNode) {
                DBTableRepNode tableRepNode = (DBTableRepNode) tableNode;
                IRepositoryNode parentPackageNode = DBTableRepNode.getParentPackageNode(tableRepNode);
                if (parentPackageNode != null) {
                    if (!packageList.contains(parentPackageNode)) {
                        packageList.add(parentPackageNode);
                    }
                    RepositoryNodeKey packageKey = new RepositoryNodeKey(parentPackageNode);
                    TableCheckedMap tableCheckedMap = packageCheckedMap.get(packageKey);
                    if (tableCheckedMap == null) {
                        tableCheckedMap = new TableCheckedMap();
                        this.packageCheckedMap.put(packageKey, tableCheckedMap);
                    }
                    tableCheckedMap.putTableChecked(tableNode, Boolean.TRUE);
                }
            }
        }
        this.setInitialElementSelections(packageList);
    }

    protected void initProvider() {
        fLabelProvider = new DQRepositoryViewLabelProvider();
        fContentProvider = new DBTreeViewContentProvider();
        sLabelProvider = new TableLabelProvider();
        sContentProvider = new TableContentProvider();
    }

    @SuppressWarnings("unchecked")
    private void addFirstPartFilters() {
        final Class[] acceptedClasses = new Class[] { IResource.class, IFolderNode.class, EObject.class,
                IRepositoryViewObject.class, IRepositoryNode.class };
        IProject rootProject = ResourceManager.getRootProject();
        IResource[] allResource = null;
        try {
            allResource = rootProject.members();
        } catch (CoreException e) {
            log.error(e, e);
        }
        ArrayList rejectedElements = new ArrayList(allResource.length);
        // MOD mzhao 2009-03-13 Feature 6066 Move all folders into one project.
        for (int i = 0; i < allResource.length; i++) {
            if (!allResource[i].equals(ResourceManager.getMetadataFolder())) {
                rejectedElements.add(allResource[i]);
            }
        }
        rejectedElements.add(ResourceManager.getMetadataFolder().getFile(".project")); //$NON-NLS-1$
        ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());
        this.addFilter(filter);
        this.addFilter(new EMFObjFilter());
        // MOD qiongli 2010-6-17 bug 13727
        addFilter(new DQFolderFliter(true));
        addFilter(new TDQEEConnectionFolderFilter());
    }

    @Override
    protected void addCheckedListener() {
        // When user checks a checkbox in the tree, check all its children
        getTreeViewer().addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {

                TreePath treePath = new TreePath(new Object[] { event.getElement() });
                getTreeViewer().setSelection(new TreeSelection(treePath));

                if (event.getChecked()) {
                    Object element = event.getElement();
                    getTreeViewer().setSubtreeChecked(element, true);
                    if (element instanceof IRepositoryNode) {
                        setOutput(element);
                        handleTablesChecked((IRepositoryNode) element, true);
                    }

                } else {
                    Object element = event.getElement();
                    getTreeViewer().setSubtreeChecked(element, false);
                    if (element instanceof IRepositoryNode) {
                        setOutput(element);
                        handleTablesChecked((IRepositoryNode) element, false);
                    }
                }
            }
        });

        getTableViewer().addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                Object element = event.getElement();
                if (element instanceof DBTableRepNode || element instanceof DBViewRepNode) {
                    handleTableChecked((IRepositoryNode) element, event.getChecked());
                }
            }
        });
    }

    private IRepositoryNode[] getCheckedTableNodes(IRepositoryNode node) {
        RepositoryNodeKey packageKey = new RepositoryNodeKey(node);
        TableCheckedMap tableCheckMap = packageCheckedMap.get(packageKey);
        if (tableCheckMap == null) {
            Boolean allCheckFlag = this.getTreeViewer().getChecked(node);
            this.getTableViewer().setAllChecked(allCheckFlag);
            tableCheckMap = new TableCheckedMap();
            List<IRepositoryNode> nmaedColumnSetNodes = RepositoryNodeHelper.getNmaedColumnSetNodes(node);
            IRepositoryNode[] tables = nmaedColumnSetNodes.toArray(new IRepositoryNode[nmaedColumnSetNodes.size()]);
            tableCheckMap.putAllChecked(tables, allCheckFlag);
            packageCheckedMap.put(packageKey, tableCheckMap);
            return allCheckFlag ? tables : null;
        } else {
            return tableCheckMap.getCheckedTableNodes(RepositoryNodeHelper.getNmaedColumnSetNodes(node));
        }
    }

    private void handleTableChecked(IRepositoryNode table, Boolean checkedFlag) {
        IRepositoryNode parentPackageNode = DBTableRepNode.getParentPackageNode(table);
        if (checkedFlag) {
            getTreeViewer().setChecked(parentPackageNode, true);
        }
        TableCheckedMap tableCheckMap = packageCheckedMap.get(new RepositoryNodeKey(parentPackageNode));
        if (tableCheckMap != null) {
            tableCheckMap.putTableChecked(table, checkedFlag);
        }
    }

    private void handleTablesChecked(IRepositoryNode packageNode, Boolean checkedFlag) {
        RepositoryNodeKey key = new RepositoryNodeKey(packageNode);
        TableCheckedMap tableCheckMap = packageCheckedMap.get(key);
        List<IRepositoryNode> nmaedColumnSetNodes = RepositoryNodeHelper.getNmaedColumnSetNodes(packageNode);
        if (tableCheckMap != null) {
            tableCheckMap.clear();
            tableCheckMap
                    .putAllChecked(nmaedColumnSetNodes.toArray(new IRepositoryNode[nmaedColumnSetNodes.size()]), checkedFlag);
        } else {
            tableCheckMap = new TableCheckedMap();
            tableCheckMap
                    .putAllChecked(nmaedColumnSetNodes.toArray(new IRepositoryNode[nmaedColumnSetNodes.size()]), checkedFlag);
            packageCheckedMap.put(key, tableCheckMap);
        }
        getTableViewer().setAllChecked(checkedFlag);
    }

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
                packageCheckedMap.clear();
                if (getTableViewer().getInput() != null) {
                    handleTablesChecked((IRepositoryNode) getTableViewer().getInput(), true);
                }
                updateOKStatus();
            }
        };
        selectButton.addSelectionListener(listener);

        listener = new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                getTreeViewer().setCheckedElements(new Object[0]);
                packageCheckedMap.clear();
                if (getTableViewer().getInput() != null) {
                    handleTablesChecked((IRepositoryNode) getTableViewer().getInput(), false);
                }
                updateOKStatus();
            }
        };
        deselectButton.addSelectionListener(listener);
    }

    public void selectionChanged(SelectionChangedEvent event) {
        Object selectedObj = ((IStructuredSelection) event.getSelection()).getFirstElement();
        if (selectedObj instanceof DBTableFolderRepNode || selectedObj instanceof DBViewFolderRepNode) {
            IRepositoryNode node = (IRepositoryNode) selectedObj;
            this.setOutput(node);
            IRepositoryNode[] tables = getCheckedTableNodes(node);
            if (tables != null) {
                this.getTableViewer().setCheckedElements(tables);
            }
        }
    }

    /**
     * This class will combine catlogName and/or schemaName as a key.
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    class RepositoryNodeKey {

        private static final String CATALOG_NAME = "__Catalog_Name__";//$NON-NLS-1$

        private static final String SCHEMA_NAME = "__Schema_Name__";//$NON-NLS-1$

        private String catalogName;

        private String schemaName;

        private IRepositoryNode node = null;

        public RepositoryNodeKey(IRepositoryNode node) {
            this.node = node;
            initNames(node);
        }

        private void initNames(IRepositoryNode node) {
            if (node instanceof DBCatalogRepNode) {
                DBCatalogRepNode catalogNode = (DBCatalogRepNode) node;
                catalogName = SwitchHelpers.CATALOG_SWITCH.doSwitch(catalogNode.getCatalog()) == null ? CATALOG_NAME
                        : SwitchHelpers.CATALOG_SWITCH.doSwitch(catalogNode.getCatalog()).getName();
                schemaName = SCHEMA_NAME;
            } else if (node instanceof DBSchemaRepNode) {
                DBSchemaRepNode schemaNode = (DBSchemaRepNode) node;
                RepositoryNode parentNode = schemaNode.getParent();
                if (parentNode instanceof DBCatalogRepNode) {
                    initNames(parentNode);
                } else {
                    catalogName = CATALOG_NAME;
                }
                schemaName = SwitchHelpers.SCHEMA_SWITCH.doSwitch(schemaNode.getSchema()) == null ? SCHEMA_NAME
                        : SwitchHelpers.SCHEMA_SWITCH.doSwitch(schemaNode.getSchema()).getName();
            } else if (node instanceof DBTableFolderRepNode || node instanceof DBViewFolderRepNode) {
                initNames(node.getParent());
            } else {
                catalogName = CATALOG_NAME;
                schemaName = SCHEMA_NAME;
            }
        }

        public IRepositoryNode getPackageNode() {
            return this.node;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((catalogName == null) ? 0 : catalogName.hashCode());
            result = prime * result + ((schemaName == null) ? 0 : schemaName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RepositoryNodeKey other = (RepositoryNodeKey) obj;
            if (catalogName == null) {
                if (other.catalogName != null) {
                    return false;
                }
            } else if (!catalogName.equals(other.catalogName)) {
                return false;
            }
            if (schemaName == null) {
                if (other.schemaName != null) {
                    return false;
                }
            } else if (!schemaName.equals(other.schemaName)) {
                return false;
            }
            return true;
        }
    }

    /**
     * @author xqliu
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    class TableCheckedMap {

        Map<String, Boolean> tableNameMap = new HashMap<String, Boolean>();

        public void putTableChecked(IRepositoryNode set, Boolean isChecked) {
            tableNameMap.put(set.getLabel(), isChecked);
        }

        public Boolean getTableChecked(IRepositoryNode set) {
            return tableNameMap.get(set.getLabel());
        }

        public void putAllChecked(IRepositoryNode[] sets, Boolean isChecked) {
            for (int i = 0; i < sets.length; i++) {
                tableNameMap.put(sets[i].getLabel(), isChecked);
            }
        }

        public IRepositoryNode[] getCheckedTableNodes(List<IRepositoryNode> setList) {
            List<IRepositoryNode> checkedTables = new ArrayList<IRepositoryNode>();
            for (IRepositoryNode set : setList) {
                if (tableNameMap.containsKey(set.getLabel()) && tableNameMap.get(set.getLabel())) {
                    checkedTables.add(set);
                }
            }
            return checkedTables.toArray(new IRepositoryNode[checkedTables.size()]);
        }

        public List<IRepositoryNode> getCheckedTableNodeList(IRepositoryNode packageNode) {
            List<IRepositoryNode> checkedTables = new ArrayList<IRepositoryNode>();

            List<IRepositoryNode> setList = RepositoryNodeHelper.getNmaedColumnSetNodes(packageNode);

            for (IRepositoryNode set : setList) {
                if (tableNameMap.containsKey(set.getLabel()) && tableNameMap.get(set.getLabel())) {
                    checkedTables.add(set);
                }
            }
            return checkedTables;
        }

        public void clear() {
            tableNameMap.clear();
        }

    }

    @Override
    protected void computeResult() {
        setResult(getAllCheckedTables());
    }

    private List<IRepositoryNode> getAllCheckedTables() {
        Object[] checkedNodes = this.getTreeViewer().getCheckedElements();
        List<IRepositoryNode> tableList = new ArrayList<IRepositoryNode>();
        for (Object obj : checkedNodes) {
            if (!(obj instanceof DBTableFolderRepNode || obj instanceof DBViewFolderRepNode)) {
                continue;
            }
            IRepositoryNode node = (IRepositoryNode) obj;
            RepositoryNodeKey packageKey = new RepositoryNodeKey(node);
            if (packageCheckedMap.containsKey(packageKey)) {
                TableCheckedMap tableMap = packageCheckedMap.get(packageKey);
                tableList.addAll(tableMap.getCheckedTableNodeList(node));
            } else {
                tableList.addAll(RepositoryNodeHelper.getNmaedColumnSetNodes(node));
            }
        }
        return tableList;
    }

    protected void okPressed() {
        super.okPressed();
        this.packageCheckedMap = null;
    }

    /**
     * label provider for table viewer.
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    class TableLabelProvider extends LabelProvider {

        public Image getImage(Object element) {
            if (element instanceof DBTableRepNode) {
                return ImageLib.getImage(ImageLib.TABLE);
            } else if (element instanceof DBViewRepNode) {
                return ImageLib.getImage(ImageLib.VIEW);
            } else {
                return null;
            }
        }

        public String getText(Object element) {
            if (element instanceof IRepositoryNode) {
                return ((IRepositoryNode) element).getLabel();
            }
            return "";//$NON-NLS-1$
        }

    }

    /**
     * DOC xqliu TablesSelectionDialog class global comment. Detailled comment
     */
    class DBTreeViewContentProvider extends ResourceViewContentProvider {

        public DBTreeViewContentProvider() {
            super();
        }

        public Object[] getChildren(Object parentElement) {
            if (!(parentElement instanceof DBTableFolderRepNode || parentElement instanceof DBViewFolderRepNode)) {
                if (parentElement instanceof IRepositoryNode) {
                    IRepositoryNode repoNode = (IRepositoryNode) parentElement;
                    return filterTableView(repoNode.getChildren()).toArray();
                }
            }
            return null;
        }

        public Object getParent(Object element) {
            if (element instanceof IRepositoryNode) {
                return ((IRepositoryNode) element).getParent();
            }
            return null;
        }

        public boolean hasChildren(Object element) {
            if (element instanceof IRepositoryNode) {
                if (!(element instanceof DBTableFolderRepNode || element instanceof DBViewFolderRepNode)) {
                    return ((IRepositoryNode) element).hasChildren();
                }
            }
            return false;
        }

    }

    /**
     * Table Selection Dialog type enum.
     */
    public enum TableSelectionType {
        TABLE("table", "Table"), //$NON-NLS-1$//$NON-NLS-2$
        VIEW("view", "View"), //$NON-NLS-1$//$NON-NLS-2$
        ALL("all", "Table/View"); //$NON-NLS-1$//$NON-NLS-2$

        private String key;

        private String label;

        public String getKey() {
            return this.key;
        }

        public String getLabel() {
            return this.label;
        }

        TableSelectionType() {
            this("table", "Table"); //$NON-NLS-1$//$NON-NLS-2$
        }

        TableSelectionType(String key) {
            this(key, key);
        }

        TableSelectionType(String key, String label) {
            this.key = key;
            this.label = label;
        }
    }

    /**
     * filter for table and view according to the dialog's type.
     * 
     * @param nodes
     * @return
     */
    public List<IRepositoryNode> filterTableView(List<IRepositoryNode> nodes) {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        for (IRepositoryNode node : nodes) {
            // MOD klliu 2001-03-08 bug 19243: In table selection wizard should filter mdm and file connection
            if (!(node instanceof DFConnectionFolderRepNode || node instanceof MDMConnectionFolderRepNode)) {
                if (node instanceof DBTableFolderRepNode) {
                    if (TableSelectionType.TABLE.equals(getTableType()) || TableSelectionType.ALL.equals(getTableType())) {
                        list.add(node);
                    }
                } else if (node instanceof DBViewFolderRepNode) {
                    if (TableSelectionType.VIEW.equals(getTableType()) || TableSelectionType.ALL.equals(getTableType())) {
                        list.add(node);
                    }
                } else {
                    list.add(node);
                }
            }
        }
        return list;
    }
}
