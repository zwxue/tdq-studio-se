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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.helper.FolderNodeHelper;
import org.talend.dataprofiler.core.model.nodes.foldernode.NamedColumnSetFolderNode;
import org.talend.dataprofiler.core.ui.dialog.provider.DBTablesViewLabelProvider;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dataprofiler.core.ui.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewContentProvider;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * @author Select the special columns from this dialog.
 * 
 */
public class ColumnsSelectionDialog extends TwoPartCheckSelectionDialog {

    private static Logger log = Logger.getLogger(ColumnsSelectionDialog.class);

    private Map<ColumnSetKey, ColumnCheckedMap> columnSetCheckedMap;

    private List<ColumnSet> currentCheckedColumnSet = new ArrayList<ColumnSet>();

    private IFolder metadataFolder = ResourceManager.getMetadataFolder();

    public ColumnsSelectionDialog(AbstractAnalysisMetadataPage metadataFormPage, Shell parent, String title,
            List<Column> columnList, String message) {
        super(metadataFormPage, parent, message);
        columnSetCheckedMap = new HashMap<ColumnSetKey, ColumnCheckedMap>();
        initCheckedColumn(columnList);

        addFilter(new EMFObjFilter());
        addFilter(new DQFolderFliter(true));

        setInput(metadataFolder);
        setTitle(title);
    }

    private void initCheckedColumn(List<Column> columnList) {
        List<ColumnSet> columnSetList = new ArrayList<ColumnSet>();
        for (int i = 0; i < columnList.size(); i++) {
            columnList.get(i).eContainer();
            ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(columnList.get(i));
            if (!columnSetList.contains(columnSetOwner)) {
                columnSetList.add(columnSetOwner);
            }
            ColumnSetKey columnSetKey = new ColumnSetKey(columnSetOwner);
            ColumnCheckedMap columnCheckedMap = columnSetCheckedMap.get(columnSetKey);
            if (columnCheckedMap == null) {
                columnCheckedMap = new ColumnCheckedMap();
                this.columnSetCheckedMap.put(columnSetKey, columnCheckedMap);
            }
            columnCheckedMap.putColumnChecked(columnList.get(i), Boolean.TRUE);
        }
        // this.setExpandedElements(columnSetList.toArray());
        this.setInitialElementSelections(columnSetList);
    }

    // protected void checkElementChecked() {
    // for (int i = 0; i < currentCheckedColumnSet.size(); i++) {
    // this.getTreeViewer().setChecked(this.currentCheckedColumnSet.get(i),
    // true);
    // }
    // this.currentCheckedColumnSet.clear();
    // }

    protected void initProvider() {
        fLabelProvider = new DBTablesViewLabelProvider();
        fContentProvider = new DBTreeViewContentProvider();
        sLabelProvider = new ColumnLabelProvider();
        sContentProvider = new ColumnContentProvider();
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

                if (event.getChecked()) {
                    getTreeViewer().setSubtreeChecked(event.getElement(), true);
                    if (event.getElement() instanceof ColumnSet) {
                        setOutput(event.getElement());
                        handleColumnsChecked((ColumnSet) event.getElement(), true);
                    }

                } else {
                    getTreeViewer().setSubtreeChecked(event.getElement(), false);
                    if (event.getElement() instanceof ColumnSet) {
                        setOutput(event.getElement());
                        handleColumnsChecked((ColumnSet) event.getElement(), false);
                    }
                }
            }
        });

        getTableViewer().addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getElement() instanceof TdColumn) {
                    handleColumnChecked((TdColumn) event.getElement(), event.getChecked());
                }
            }
        });
    }

    private TdColumn[] getCheckedColumns(ColumnSet columnSet) {
        ColumnSetKey columnSetKey = new ColumnSetKey(columnSet);
        ColumnCheckedMap columnCheckMap = columnSetCheckedMap.get(columnSetKey);
        if (columnCheckMap == null) {
            Boolean allCheckFlag = this.getTreeViewer().getChecked(columnSet);
            this.getTableViewer().setAllChecked(allCheckFlag);
            columnCheckMap = new ColumnCheckedMap();
            TdColumn[] columns = EObjectHelper.getColumns(columnSet);
            columnCheckMap.putAllChecked(columns, allCheckFlag);
            columnSetCheckedMap.put(columnSetKey, columnCheckMap);
            return allCheckFlag ? columns : null;
        } else {
            return columnCheckMap.getCheckedColumns(ColumnSetHelper.getColumns(columnSet));
        }

    }

    private void handleColumnChecked(TdColumn column, Boolean checkedFlag) {
        ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(column);
        if (checkedFlag) {
            getTreeViewer().setChecked(columnSetOwner, true);
        }
        ColumnCheckedMap columnCheckMap = columnSetCheckedMap.get(new ColumnSetKey(columnSetOwner));
        if (columnCheckMap != null) {
            columnCheckMap.putColumnChecked(column, checkedFlag);
        }

    }

    private void handleColumnsChecked(ColumnSet columnSet, Boolean checkedFlag) {
        ColumnSetKey key = new ColumnSetKey(columnSet);
        ColumnCheckedMap columnCheckMap = columnSetCheckedMap.get(key);
        if (columnCheckMap != null) {
            columnCheckMap.clear();
            columnCheckMap.putAllChecked(EObjectHelper.getColumns(columnSet), checkedFlag);
        } else {
            columnCheckMap = new ColumnCheckedMap();
            columnCheckMap.putAllChecked(EObjectHelper.getColumns(columnSet), checkedFlag);
            columnSetCheckedMap.put(key, columnCheckMap);
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
                columnSetCheckedMap.clear();
                if (getTableViewer().getInput() != null) {
                    handleColumnsChecked((ColumnSet) getTableViewer().getInput(), true);
                }
                updateOKStatus();
            }
        };
        selectButton.addSelectionListener(listener);

        listener = new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                getTreeViewer().setCheckedElements(new Object[0]);
                columnSetCheckedMap.clear();
                if (getTableViewer().getInput() != null) {
                    handleColumnsChecked((ColumnSet) getTableViewer().getInput(), false);
                }
                updateOKStatus();
            }
        };
        deselectButton.addSelectionListener(listener);
    }

    public void selectionChanged(SelectionChangedEvent event) {
        Object selectedObj = ((IStructuredSelection) event.getSelection()).getFirstElement();
        if (selectedObj instanceof ColumnSet) {
            this.setOutput(selectedObj);
            TdColumn[] columns = getCheckedColumns((ColumnSet) selectedObj);
            if (columns != null) {
                this.getTableViewer().setCheckedElements(columns);
            }
        }

    }

    /**
     * This class will combine catlogName and columnSetName as a key.
     */
    class ColumnSetKey {

        private final String catalogName;

        private final String columnSetName;

        public ColumnSetKey(ColumnSet columnSetOwner) {
            Package parent = EObjectHelper.getParent(columnSetOwner);
            if (parent != null) {
                this.catalogName = parent.getName();
            } else {
                this.catalogName = ""; //$NON-NLS-1$
            }
            this.columnSetName = columnSetOwner.getName();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((catalogName == null) ? 0 : catalogName.hashCode());
            result = prime * result + ((columnSetName == null) ? 0 : columnSetName.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
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
            final ColumnSetKey other = (ColumnSetKey) obj;
            if (catalogName == null) {
                if (other.catalogName != null) {
                    return false;
                }
            } else if (!catalogName.equals(other.catalogName)) {
                return false;
            }
            if (columnSetName == null) {
                if (other.columnSetName != null) {
                    return false;
                }
            } else if (!columnSetName.equals(other.columnSetName)) {
                return false;
            }
            return true;
        }
    }

    /**
     * @author rli
     * 
     */
    class ColumnCheckedMap {

        Map<String, Boolean> columnNameMap = new HashMap<String, Boolean>();

        public void putColumnChecked(Column column, Boolean isChecked) {
            columnNameMap.put(column.getName(), isChecked);
        }

        public Boolean getColumnChecked(Column column) {
            return columnNameMap.get(column.getName());
        }

        public void putAllChecked(Column[] columns, Boolean isChecked) {
            for (int i = 0; i < columns.length; i++) {
                columnNameMap.put(columns[i].getName(), isChecked);
            }
        }

        public TdColumn[] getCheckedColumns(List<TdColumn> columnList) {
            List<TdColumn> checkedColumns = new ArrayList<TdColumn>();
            for (TdColumn column : columnList) {
                if (columnNameMap.containsKey(column.getName()) && columnNameMap.get(column.getName())) {
                    checkedColumns.add(column);
                }
            }
            return checkedColumns.toArray(new TdColumn[checkedColumns.size()]);
        }

        public List<TdColumn> getCheckedColumnList(ColumnSet columnSet) {
            List<TdColumn> checkedColumns = new ArrayList<TdColumn>();

            List<TdColumn> columnList = ColumnSetHelper.getColumns(columnSet);

            for (TdColumn column : columnList) {
                if (columnNameMap.containsKey(column.getName()) && columnNameMap.get(column.getName())) {
                    checkedColumns.add(column);
                }
            }
            return checkedColumns;
        }

        public void clear() {
            columnNameMap.clear();
        }

    }

    protected void computeResult() {
        setResult(getAllCheckedColumns());
    }

    private List<TdColumn> getAllCheckedColumns() {
        Object[] checkedNodes = this.getTreeViewer().getCheckedElements();
        List<TdColumn> columnList = new ArrayList<TdColumn>();
        for (int i = 0; i < checkedNodes.length; i++) {
            if (!(checkedNodes[i] instanceof ColumnSet)) {
                continue;
            }
            ColumnSetKey columnSetKey = new ColumnSetKey((ColumnSet) checkedNodes[i]);
            if (columnSetCheckedMap.containsKey(columnSetKey)) {
                ColumnCheckedMap columnMap = columnSetCheckedMap.get(columnSetKey);
                columnList.addAll(columnMap.getCheckedColumnList((ColumnSet) checkedNodes[i]));
            } else {
                columnList.addAll(ColumnSetHelper.getColumns((ColumnSet) checkedNodes[i]));
            }
        }
        return columnList;
    }

    protected void okPressed() {
        super.okPressed();
        this.columnSetCheckedMap = null;
        this.currentCheckedColumnSet = null;
    }

    /**
     * @author rli
     * 
     */
    class ColumnLabelProvider extends LabelProvider {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
         */
        public Image getImage(Object element) {
            return ImageLib.getImage(ImageLib.TD_COLUMN);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         */
        public String getText(Object element) {
            TdColumn columnObj = (TdColumn) element;
            return columnObj.getName() + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT
                    + columnObj.getSqlDataType().getName() + PluginConstant.PARENTHESIS_RIGHT;
        }

    }

    /**
     * @author rli
     * 
     */
    class ColumnContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof ColumnSet) {
                EObject eObj = (EObject) inputElement;
                ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(eObj);
                if (columnSet != null) {
                    TdColumn[] columns = EObjectHelper.getColumns(columnSet);
                    if (columns.length <= 0) {
                        Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
                        if (parentCatalogOrSchema == null) {
                            return null;
                        }
                        TdDataProvider provider = DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);
                        if (provider == null) {
                            return null;
                        }
                        try {
                            List<TdColumn> columnList = DqRepositoryViewService.getColumns(provider, columnSet, null, true);
                            columns = columnList.toArray(new TdColumn[columnList.size()]);
                            // store tables in catalog
                            // MOD scorreia 2009-01-29 columns are stored in the
                            // table
                            // ColumnSetHelper.addColumns(columnSet,
                            // columnList);
                        } catch (TalendException e) {
                            MessageBoxExceptionHandler.process(e);
                        }

                        PrvResourceFileHelper.getInstance().save(provider);
                    }
                    return sort(columns, ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
                }
            }
            return null;
        }

        /**
         * Sort the parameter objects, and return the sorted array.
         * 
         * @param objects
         * @param comparatorId the comparator id has been defined in the {@link ComparatorsFactory};
         * @return
         */
        @SuppressWarnings("unchecked")
        protected Object[] sort(Object[] objects, int comparatorId) {
            if (objects == null || objects.length <= 1) {
                return objects;
            }
            Arrays.sort(objects, ComparatorsFactory.buildComparator(comparatorId));
            return objects;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        }

    }

    /**
     * DOC zqin ColumnsSelectionDialog class global comment. Detailled comment
     */
    class DBTreeViewContentProvider extends DQRepositoryViewContentProvider {

        /**
         * @param adapterFactory
         */
        public DBTreeViewContentProvider() {
            super();
        }

        @SuppressWarnings("unchecked")
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof IContainer) {
                IContainer container = ((IContainer) parentElement);
                IResource[] members = null;
                try {
                    members = container.members();
                } catch (CoreException e) {
                    log.error("Can't get the children of container:" + container.getLocation());
                }

                if (container.equals(metadataFolder.getFolder(org.talend.dataquality.PluginConstant.DB_CONNECTIONS))) {
                    ComparatorsFactory.sort(members, ComparatorsFactory.FILEMODEL_COMPARATOR_ID);
                }
                return members;
            } else if (parentElement instanceof NamedColumnSet) {
                return null;
            } else if (parentElement instanceof NamedColumnSetFolderNode) {
                NamedColumnSetFolderNode folerNode = (NamedColumnSetFolderNode) parentElement;
                folerNode.loadChildren();
                Object[] children = folerNode.getChildren();
                if (children != null && children.length > 0) {
                    if (!(children[0] instanceof ColumnSet)) {
                        return children;
                    }
                    for (int i = 0; i < children.length; i++) {
                        ColumnSet columnSet = (ColumnSet) children[i];
                        ColumnSetKey key = new ColumnSetKey(columnSet);
                        if (columnSetCheckedMap.containsKey(key)) {
                            currentCheckedColumnSet.add(columnSet);
                        }
                    }
                }
                return ComparatorsFactory.sort(children, ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
            }
            return super.getChildren(parentElement);
        }

        public Object getParent(Object element) {
            if (element instanceof EObject) {
                EObject eObj = (EObject) element;
                ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(eObj);
                if (columnSet != null) {
                    IFolderNode folderNode = FolderNodeHelper.getFolderNode(EObjectHelper.getParent((ColumnSet) element),
                            columnSet);
                    return folderNode;
                }

                Package packageValue = SwitchHelpers.PACKAGE_SWITCH.doSwitch(eObj);
                TdCatalog parentCatalog = CatalogHelper.getParentCatalog(packageValue);
                if (parentCatalog != null) {
                    return parentCatalog;
                }

                if (packageValue != null) {
                    TdDataProvider tdDataProvider = DataProviderHelper.getTdDataProvider(packageValue);
                    IFile findCorrespondingFile = PrvResourceFileHelper.getInstance().findCorrespondingFile(tdDataProvider);
                    return findCorrespondingFile;
                    // Path path = new Path(uri.path());
                    // String fileName = path.lastSegment();
                    // IFolder connectionsFolder =
                    // ResourcesPlugin.getWorkspace().getRoot().getProject(
                    // DQStructureManager.getMetaData())
                    // .getFolder(DQStructureManager.DB_CONNECTIONS);
                    // IFile resourceFile = connectionsFolder.getFile(fileName);
                }
            } else if (element instanceof IFolderNode) {
                return ((IFolderNode) element).getParent();
            } else if (element instanceof IResource) {
                return ((IResource) element).getParent();
            }
            return super.getParent(element);
        }

        public boolean hasChildren(Object element) {
            return !(element instanceof TdView || element instanceof TdTable);
        }

    }

}
