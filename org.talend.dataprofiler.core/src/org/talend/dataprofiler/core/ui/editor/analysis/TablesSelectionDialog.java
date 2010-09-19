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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.nodes.foldernode.NamedColumnSetFolderNode;
import org.talend.dataprofiler.core.ui.dialog.TwoPartCheckSelectionDialog;
import org.talend.dataprofiler.core.ui.dialog.provider.DBTablesViewLabelProvider;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dataprofiler.core.ui.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.filters.TDQEEConnectionFolderFilter;
import org.talend.dataprofiler.core.ui.filters.TypedViewerFilter;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewContentProvider;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.NamedColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TablesSelectionDialog extends TwoPartCheckSelectionDialog {

    private static Logger log = Logger.getLogger(TablesSelectionDialog.class);

    private Map<PackageKey, TableCheckedMap> packageCheckedMap;

    private IFolder metadataFolder = ResourceManager.getMetadataFolder();

    public TablesSelectionDialog(AbstractAnalysisMetadataPage metadataFormPage, Shell parent, String title,
            List<NamedColumnSet> setList, String message) {
        super(metadataFormPage, parent, message);
        this.setDialogType(DIALOG_TYPE_TABLE);
        addFirstPartFilters();
        this.setInput(metadataFolder);
        packageCheckedMap = new HashMap<PackageKey, TableCheckedMap>();
        initCheckedTable(setList);
        this.setTitle(title);
    }

    @Override
    /*
     * 
     * DOC mzhao bug 9240 mzhao 2009-11-05
     * 
     * @param columnSetList
     */
    protected void unfoldToCheckedElements() {
        Iterator<PackageKey> it = packageCheckedMap.keySet().iterator();
        while (it.hasNext()) {
            PackageKey csk = it.next();
            getTreeViewer().expandToLevel(csk.getPackage(), 1);
            StructuredSelection structSel = new StructuredSelection(csk.getPackage());
            getTreeViewer().setSelection(structSel);
        }
    }

    private void initCheckedTable(List<NamedColumnSet> tableList) {
        List<Package> packageList = new ArrayList<Package>();
        for (int i = 0; i < tableList.size(); i++) {
            tableList.get(i).eContainer();
            Package packageOwner = PackageHelper.getCatalogOrSchema(tableList.get(i).getNamespace());
            if (!packageList.contains(packageOwner)) {
                packageList.add(packageOwner);
            }
            PackageKey packageKey = new PackageKey(packageOwner);
            TableCheckedMap tableCheckedMap = packageCheckedMap.get(packageKey);
            if (tableCheckedMap == null) {
                tableCheckedMap = new TableCheckedMap();
                this.packageCheckedMap.put(packageKey, tableCheckedMap);
            }
            tableCheckedMap.putTableChecked(tableList.get(i), Boolean.TRUE);
        }
        this.setInitialElementSelections(packageList);
    }

    protected void initProvider() {
        fLabelProvider = new DBTablesViewLabelProvider();
        fContentProvider = new DBTreeViewContentProvider();
        sLabelProvider = new TableLabelProvider();
        sContentProvider = new TableContentProvider();
    }

    @SuppressWarnings("unchecked")
    private void addFirstPartFilters() {
        final Class[] acceptedClasses = new Class[] { IResource.class, IFolderNode.class, EObject.class,
                IRepositoryViewObject.class };
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

    protected void addCheckedListener() {

        // When user checks a checkbox in the tree, check all its children
        getTreeViewer().addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {

                TreePath treePath = new TreePath(new Object[] { event.getElement() });
                getTreeViewer().setSelection(new TreeSelection(treePath));

                if (event.getChecked()) {
                    getTreeViewer().setSubtreeChecked(event.getElement(), true);
                    if (event.getElement() instanceof Package) {
                        setOutput(event.getElement());
                        handleTablesChecked((Package) event.getElement(), true);
                    }

                } else {
                    getTreeViewer().setSubtreeChecked(event.getElement(), false);
                    if (event.getElement() instanceof Package) {
                        setOutput(event.getElement());
                        handleTablesChecked((Package) event.getElement(), false);
                    }
                }
            }
        });

        getTableViewer().addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getElement() instanceof NamedColumnSet) {
                    handleTableChecked((NamedColumnSet) event.getElement(), event.getChecked());
                }
            }
        });
    }

    private NamedColumnSet[] getCheckedTables(Package pckg) {
        PackageKey packageKey = new PackageKey(pckg);
        TableCheckedMap tableCheckMap = packageCheckedMap.get(packageKey);
        if (tableCheckMap == null) {
            Boolean allCheckFlag = this.getTreeViewer().getChecked(pckg);
            this.getTableViewer().setAllChecked(allCheckFlag);
            tableCheckMap = new TableCheckedMap();
            List<NamedColumnSet> temp = PackageHelper.getNmaedColumnSets(pckg);
            NamedColumnSet[] tables = temp.toArray(new NamedColumnSet[temp.size()]);
            tableCheckMap.putAllChecked(tables, allCheckFlag);
            packageCheckedMap.put(packageKey, tableCheckMap);
            return allCheckFlag ? tables : null;
        } else {
            return tableCheckMap.getCheckedTables(PackageHelper.getNmaedColumnSets(pckg));
        }
    }

    private void handleTableChecked(NamedColumnSet table, Boolean checkedFlag) {
        Package pckg = PackageHelper.getCatalogOrSchema(table.getNamespace());
        if (checkedFlag) {
            getTreeViewer().setChecked(pckg, true);
        }
        TableCheckedMap tableCheckMap = packageCheckedMap.get(new PackageKey(pckg));
        if (tableCheckMap != null) {
            tableCheckMap.putTableChecked(table, checkedFlag);
        }
    }

    private void handleTablesChecked(Package pckg, Boolean checkedFlag) {
        PackageKey key = new PackageKey(pckg);
        TableCheckedMap tableCheckMap = packageCheckedMap.get(key);
        List<NamedColumnSet> temp = PackageHelper.getNmaedColumnSets(pckg);
        if (tableCheckMap != null) {
            tableCheckMap.clear();
            tableCheckMap.putAllChecked(temp.toArray(new NamedColumnSet[temp.size()]), checkedFlag);
        } else {
            tableCheckMap = new TableCheckedMap();
            tableCheckMap.putAllChecked(temp.toArray(new NamedColumnSet[temp.size()]), checkedFlag);
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
                    handleTablesChecked((Package) getTableViewer().getInput(), true);
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
                    handleTablesChecked((Package) getTableViewer().getInput(), false);
                }
                updateOKStatus();
            }
        };
        deselectButton.addSelectionListener(listener);
    }

    public void selectionChanged(SelectionChangedEvent event) {
        Object selectedObj = ((IStructuredSelection) event.getSelection()).getFirstElement();
        if (selectedObj instanceof Package) {
            Package pckg = (Package) selectedObj;
            this.setOutput(pckg);
            NamedColumnSet[] tables = getCheckedTables(pckg);
            if (tables != null) {
                this.getTableViewer().setCheckedElements(tables);
            }
        }
    }

    /**
     * This class will combine catlogName and/or schemaName as a key.
     */
    class PackageKey {

        private final String catalogName;

        private final String schemaName;

        private Package pakg = null;

        public PackageKey(Package pckg) {
            schemaName = SwitchHelpers.SCHEMA_SWITCH.doSwitch(pckg) == null ? "__Schema_Name__" : SwitchHelpers.SCHEMA_SWITCH //$NON-NLS-1$
                    .doSwitch(pckg).getName();
            catalogName = SwitchHelpers.CATALOG_SWITCH.doSwitch(pckg) == null ? "__Catalog_Name__" : SwitchHelpers.CATALOG_SWITCH //$NON-NLS-1$
                    .doSwitch(pckg).getName();
            this.pakg = pckg;
        }

        public Package getPackage() {
            return pakg;
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
            final PackageKey other = (PackageKey) obj;
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
     */
    class TableCheckedMap {

        Map<String, Boolean> tableNameMap = new HashMap<String, Boolean>();

        public void putTableChecked(NamedColumnSet set, Boolean isChecked) {
            tableNameMap.put(set.getName(), isChecked);
        }

        public Boolean getTableChecked(NamedColumnSet set) {
            return tableNameMap.get(set.getName());
        }

        public void putAllChecked(NamedColumnSet[] sets, Boolean isChecked) {
            for (int i = 0; i < sets.length; i++) {
                tableNameMap.put(sets[i].getName(), isChecked);
            }
        }

        public NamedColumnSet[] getCheckedTables(List<NamedColumnSet> setList) {
            List<NamedColumnSet> checkedTables = new ArrayList<NamedColumnSet>();
            for (NamedColumnSet set : setList) {
                if (tableNameMap.containsKey(set.getName()) && tableNameMap.get(set.getName())) {
                    checkedTables.add(set);
                }
            }
            return checkedTables.toArray(new NamedColumnSet[checkedTables.size()]);
        }

        public List<NamedColumnSet> getCheckedTableList(Package pckg) {
            List<NamedColumnSet> checkedTables = new ArrayList<NamedColumnSet>();

            List<NamedColumnSet> setList = PackageHelper.getNmaedColumnSets(pckg);

            for (NamedColumnSet set : setList) {
                if (tableNameMap.containsKey(set.getName()) && tableNameMap.get(set.getName())) {
                    checkedTables.add(set);
                }
            }
            return checkedTables;
        }

        public void clear() {
            tableNameMap.clear();
        }

    }

    protected void computeResult() {
        setResult(getAllCheckedTables());
    }

    private List<NamedColumnSet> getAllCheckedTables() {
        Object[] checkedNodes = this.getTreeViewer().getCheckedElements();
        List<NamedColumnSet> tableList = new ArrayList<NamedColumnSet>();
        for (int i = 0; i < checkedNodes.length; i++) {
            if (!(checkedNodes[i] instanceof Package)) {
                continue;
            }
            PackageKey packageKey = new PackageKey((Package) checkedNodes[i]);
            if (packageCheckedMap.containsKey(packageKey)) {
                TableCheckedMap tableMap = packageCheckedMap.get(packageKey);
                tableList.addAll(tableMap.getCheckedTableList((Package) checkedNodes[i]));
            } else {
                tableList.addAll(PackageHelper.getTables((Package) checkedNodes[i]));
            }
        }
        return tableList;
    }

    protected void okPressed() {
        super.okPressed();
        this.packageCheckedMap = null;
        // this.currentCheckedPackage = null;
    }

    /**
     * @author xqliu
     */
    class TableLabelProvider extends LabelProvider {

        public Image getImage(Object element) {
            if (element instanceof TdTable) {
                return ImageLib.getImage(ImageLib.TABLE);
            } else if (element instanceof TdView) {
                return ImageLib.getImage(ImageLib.VIEW);
            } else {
                return null;
            }
        }

        public String getText(Object element) {
            return ((NamedColumnSet) element).getName();
        }

    }

    /**
     * DOC xqliu TablesSelectionDialog class global comment. Detailled comment
     */
    class DBTreeViewContentProvider extends DQRepositoryViewContentProvider {

        /**
         * DOC xqliu DBTreeViewContentProvider constructor comment.
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
                    log.error(DefaultMessagesImpl.getString("TablesSelectionDialog.cannotGetChildren") + container.getLocation()); //$NON-NLS-1$
                }

                if (container.equals(ResourceManager.getConnectionFolder())) {
                    ComparatorsFactory.sort(members, ComparatorsFactory.FILEMODEL_COMPARATOR_ID);
                }
                if (ResourceManager.getConnectionFolder().equals(container)) {
                    return ProxyRepositoryViewObject.fetchAllDBRepositoryViewObjects(false).toArray();
                } else if (ResourceManager.getMDMConnectionFolder().equals(container)) {
                    return ProxyRepositoryViewObject.fetchAllMDMRepositoryViewObjects(false).toArray();
                }
                return members;
            } else if (parentElement instanceof Schema) {
                return null;
            } else if (parentElement instanceof Catalog) {
                Catalog catalog = (Catalog) parentElement;
                EList<ModelElement> eList = catalog.getOwnedElement();
                if (!(eList.size() > 0 && eList.get(0) instanceof Schema)) {
                    return null;
                }
            }
            return super.getChildren(parentElement);
        }

        public Object getParent(Object element) {
            if (element instanceof EObject) {
                EObject eObj = (EObject) element;
                Package packageValue = SwitchHelpers.PACKAGE_SWITCH.doSwitch(eObj);
                Catalog parentCatalog = CatalogHelper.getParentCatalog(packageValue);
                if (parentCatalog != null) {
                    return parentCatalog;
                }

                if (packageValue != null) {
                    Connection conn = DataProviderHelper.getTdDataProvider(packageValue);
                    // IFile findCorrespondingFile =
                    // PrvResourceFileHelper.getInstance().findCorrespondingFile(tdDataProvider);
                    return ProxyRepositoryViewObject.getRepositoryViewObject(conn);
                }
            } else if (element instanceof IFolderNode) {
                return ((IFolderNode) element).getParent();
            } else if (element instanceof IResource) {
                return ((IResource) element).getParent();
            }
            return super.getParent(element);
        }

        public boolean hasChildren(Object element) {
            return !(element instanceof NamedColumnSetFolderNode);
        }

    }

    /**
     * @author xqliu
     */
    class TableContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof Package) {
                EObject eObj = (EObject) inputElement;
                Package pckg = SwitchHelpers.PACKAGE_SWITCH.doSwitch(eObj);

                if (pckg != null) {
                    Catalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(pckg);
                    Schema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(pckg);
                    List<NamedColumnSet> temp = PackageHelper.getNmaedColumnSets(pckg);
                    NamedColumnSet[] tables = temp.toArray(new NamedColumnSet[temp.size()]);
                    if (tables.length <= 0) {
                        Connection provider = DataProviderHelper.getTdDataProvider(pckg);
                        if (provider == null) {
                            return null;
                        }
                        try {
                            List<NamedColumnSet> setList = new ArrayList<NamedColumnSet>();
                            if (catalog != null) {
                                setList.addAll(DqRepositoryViewService.getTables(provider, catalog, null, true));
                                setList.addAll(DqRepositoryViewService.getViews(provider, catalog, null, true));
                            }
                            if (schema != null) {
                                setList.addAll(DqRepositoryViewService.getTables(provider, schema, null, true));
                                setList.addAll(DqRepositoryViewService.getViews(provider, schema, null, true));
                            }
                            tables = setList.toArray(new NamedColumnSet[setList.size()]);
                            // save the Table from db into EMF Object.
                            pckg.getOwnedElement().addAll(setList);
                        } catch (TalendException e) {
                            MessageBoxExceptionHandler.process(e);
                        }

                        ProxyRepositoryViewObject.save(provider);
                    }
                    return sort(tables, ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
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

}
