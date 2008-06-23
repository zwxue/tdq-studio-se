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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.helper.EObjectHelper;
import org.talend.dataprofiler.core.helper.NeedSaveDataProviderHelper;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.foldernode.IFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.NamedColumnSetFolderNode;
import org.talend.dataprofiler.core.ui.dialog.filter.TypedViewerFilter;
import org.talend.dataprofiler.core.ui.dialog.provider.DBTablesViewLabelProvider;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.core.ui.views.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewContentProvider;
import orgomg.cwm.objectmodel.core.Package;
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

    public ColumnsSelectionDialog(Shell parent, String title, ColumnIndicator[] columnIndicators, String message) {
        super(parent, message);
        addFirstPartFilters();
        this.setInput(ResourcesPlugin.getWorkspace().getRoot());
        columnSetCheckedMap = new HashMap<ColumnSetKey, ColumnCheckedMap>();
        initCheckedColumn(columnIndicators);
        this.setTitle(title);
    }

    private void initCheckedColumn(ColumnIndicator[] columnIndicators) {

        for (int i = 0; i < columnIndicators.length; i++) {
            columnIndicators[i].getTdColumn().eContainer();
            ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(columnIndicators[i].getTdColumn());
            ColumnSetKey columnSetKey = new ColumnSetKey(columnSetOwner);
            ColumnCheckedMap columnCheckedMap = columnSetCheckedMap.get(columnSetKey);
            if (columnCheckedMap == null) {
                columnCheckedMap = new ColumnCheckedMap();
                this.columnSetCheckedMap.put(columnSetKey, columnCheckedMap);
            }
            columnCheckedMap.putColumnChecked(columnIndicators[i].getTdColumn(), Boolean.TRUE);
        }
    }

    protected void checkElementChecked() {
        for (int i = 0; i < currentCheckedColumnSet.size(); i++) {
            this.getTreeViewer().setChecked(this.currentCheckedColumnSet.get(i), true);
        }
        this.currentCheckedColumnSet.clear();
    }

    protected void initProvider() {
        fLabelProvider = new DBTablesViewLabelProvider();
        fContentProvider = new DBTreeViewContentProvider();
        sLabelProvider = new ColumnLabelProvider();
        sContentProvider = new ColumnContentProvider();
    }

    @SuppressWarnings("unchecked")
    private void addFirstPartFilters() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        final Class[] acceptedClasses = new Class[] { IResource.class, IFolderNode.class, EObject.class };
        IProject[] allProjects = root.getProjects();
        ArrayList rejectedElements = new ArrayList(allProjects.length);
        for (int i = 0; i < allProjects.length; i++) {
            if (!allProjects[i].equals(ResourcesPlugin.getWorkspace().getRoot().getProject(PluginConstant.METADATA_PROJECTNAME))) {
                rejectedElements.add(allProjects[i]);
            }
        }
        rejectedElements.add(ResourcesPlugin.getWorkspace().getRoot().getProject(PluginConstant.METADATA_PROJECTNAME).getFile(
                ".project"));
        ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());
        this.addFilter(filter);
        this.addFilter(new EMFObjFilter());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.dialog.TwoPartCheckSelectionDialog#addCheckedListener()
     */
    protected void addCheckedListener() {

        // When user checks a checkbox in the tree, check all its children
        getTreeViewer().addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                // If the item is checked . . .
                if (event.getChecked()) {
                    // . . . check all its children
                    getTreeViewer().setSubtreeChecked(event.getElement(), true);
                    if (event.getElement() instanceof ColumnSet) {
                        handleColumnsChecked((ColumnSet) event.getElement(), true);
                    }
                } else {
                    getTreeViewer().setSubtreeChecked(event.getElement(), false);
                    if (event.getElement() instanceof ColumnSet) {
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
     * @see org.talend.dataprofiler.core.ui.dialog.TwoPartCheckSelectionDialog#addSelectionButtonListener(org.eclipse.swt.widgets.Button,
     * org.eclipse.swt.widgets.Button)
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
            this.catalogName = EObjectHelper.getParent(columnSetOwner).getName();
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

        public void putColumnChecked(TdColumn column, Boolean isChecked) {
            columnNameMap.put(column.getName(), isChecked);
        }

        public Boolean getColumnChecked(TdColumn column) {
            return columnNameMap.get(column.getName());
        }

        public void putAllChecked(TdColumn[] columns, Boolean isChecked) {
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
        NeedSaveDataProviderHelper.saveAllDataProvider();
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
                            ColumnSetHelper.addColumns(columnSet, columnList);
                        } catch (TalendException e) {
                            MessageBoxExceptionHandler.process(e);
                        }
                        NeedSaveDataProviderHelper.register(provider.getName(), provider);
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

                if (container.equals(ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.METADATA).getFolder(
                        DQStructureManager.DB_CONNECTIONS))) {
                    ComparatorsFactory.sort(members, ComparatorsFactory.FILEMODEL_COMPARATOR_ID);
                }
                return members;
            } else if (parentElement instanceof NamedColumnSet) {
                return null;
            } else if (parentElement instanceof NamedColumnSetFolderNode) {
                NamedColumnSetFolderNode folerNode = (NamedColumnSetFolderNode) parentElement;
                if (!(folerNode.isLoaded())) {
                    folerNode.loadChildren();
                }
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

        public boolean hasChildren(Object element) {
            return !(element instanceof TdView || element instanceof TdTable);
        }

    }

}
