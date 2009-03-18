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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
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
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.model.nodes.foldernode.NamedColumnSetFolderNode;
import org.talend.dataprofiler.core.ui.dialog.TwoPartCheckSelectionDialog;
import org.talend.dataprofiler.core.ui.dialog.filter.TypedViewerFilter;
import org.talend.dataprofiler.core.ui.dialog.provider.DBTablesViewLabelProvider;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.core.ui.views.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewContentProvider;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;
import orgomg.cwm.resource.relational.Table;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TablesSelectionDialog extends TwoPartCheckSelectionDialog {

	private static Logger log = Logger.getLogger(TablesSelectionDialog.class);

	private Map<PackageKey, TableCheckedMap> packageCheckedMap;

	public TablesSelectionDialog(Shell parent, String title,
			List<Table> tableList, String message) {
		super(parent, message);
		this.setDialogType(DIALOG_TYPE_TABLE);
		addFirstPartFilters();
		this.setInput(ResourcesPlugin.getWorkspace().getRoot());
		packageCheckedMap = new HashMap<PackageKey, TableCheckedMap>();
		initCheckedTable(tableList);
		this.setTitle(title);
	}

	private void initCheckedTable(List<Table> tableList) {
		List<Package> packageList = new ArrayList<Package>();
		for (int i = 0; i < tableList.size(); i++) {
			tableList.get(i).eContainer();
			Package packageOwner = PackageHelper.getCatalogOrSchema(tableList
					.get(i).getNamespace());
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
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final Class[] acceptedClasses = new Class[] { IResource.class,
				IFolderNode.class, EObject.class };
		// MOD mzhao 2009-03-13 feature 6066 Move all folders into one project.
		IProject rootProject = root
				.getProject(org.talend.dataquality.PluginConstant
						.getRootProjectName());
		IResource[] allResource = null;
		try {
			allResource = rootProject.members();
		} catch (CoreException e) {
			log.error(e, e);
		}
		ArrayList rejectedElements = new ArrayList(allResource.length);
		// MOD mzhao 2009-03-13 Feature 6066 Move all folders into one project.
		for (int i = 0; i < allResource.length; i++) {
			if (!allResource[i].equals(ResourcesPlugin.getWorkspace().getRoot()
					.getProject(
							org.talend.dataquality.PluginConstant
									.getRootProjectName()).getFolder(
							PluginConstant.METADATA_PROJECTNAME))) {
				rejectedElements.add(allResource[i]);
			}
		}
		rejectedElements.add(ResourcesPlugin.getWorkspace().getRoot()
				.getProject(
						org.talend.dataquality.PluginConstant
								.getRootProjectName()).getFolder(
						PluginConstant.METADATA_PROJECTNAME)
				.getFile(".project")); //$NON-NLS-1$
		ViewerFilter filter = new TypedViewerFilter(acceptedClasses,
				rejectedElements.toArray());
		this.addFilter(filter);
		this.addFilter(new EMFObjFilter());
	}

	protected void addCheckedListener() {

		// When user checks a checkbox in the tree, check all its children
		getTreeViewer().addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {

				TreePath treePath = new TreePath(new Object[] { event
						.getElement() });
				getTreeViewer().setSelection(new TreeSelection(treePath));

				if (event.getChecked()) {
					getTreeViewer().setSubtreeChecked(event.getElement(), true);
					if (event.getElement() instanceof Package) {
						setOutput(event.getElement());
						handleTablesChecked((Package) event.getElement(), true);
					}

				} else {
					getTreeViewer()
							.setSubtreeChecked(event.getElement(), false);
					if (event.getElement() instanceof Package) {
						setOutput(event.getElement());
						handleTablesChecked((Package) event.getElement(), false);
					}
				}
			}
		});

		getTableViewer().addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getElement() instanceof TdTable) {
					handleTableChecked((TdTable) event.getElement(), event
							.getChecked());
				}
			}
		});
	}

	private TdTable[] getCheckedTables(Package pckg) {
		PackageKey packageKey = new PackageKey(pckg);
		TableCheckedMap tableCheckMap = packageCheckedMap.get(packageKey);
		if (tableCheckMap == null) {
			Boolean allCheckFlag = this.getTreeViewer().getChecked(pckg);
			this.getTableViewer().setAllChecked(allCheckFlag);
			tableCheckMap = new TableCheckedMap();
			List<TdTable> temp = PackageHelper.getTables(pckg);
			TdTable[] tables = temp.toArray(new TdTable[temp.size()]);
			tableCheckMap.putAllChecked(tables, allCheckFlag);
			packageCheckedMap.put(packageKey, tableCheckMap);
			return allCheckFlag ? tables : null;
		} else {
			return tableCheckMap
					.getCheckedTables(PackageHelper.getTables(pckg));
		}
	}

	private void handleTableChecked(TdTable table, Boolean checkedFlag) {
		Package pckg = PackageHelper.getCatalogOrSchema(table.getNamespace());
		if (checkedFlag) {
			getTreeViewer().setChecked(pckg, true);
		}
		TableCheckedMap tableCheckMap = packageCheckedMap.get(new PackageKey(
				pckg));
		if (tableCheckMap != null) {
			tableCheckMap.putTableChecked(table, checkedFlag);
		}
	}

	private void handleTablesChecked(Package pckg, Boolean checkedFlag) {
		PackageKey key = new PackageKey(pckg);
		TableCheckedMap tableCheckMap = packageCheckedMap.get(key);
		List<TdTable> temp = PackageHelper.getTables(pckg);
		if (tableCheckMap != null) {
			tableCheckMap.clear();
			tableCheckMap.putAllChecked(temp.toArray(new TdTable[temp.size()]),
					checkedFlag);
		} else {
			tableCheckMap = new TableCheckedMap();
			tableCheckMap.putAllChecked(temp.toArray(new TdTable[temp.size()]),
					checkedFlag);
			packageCheckedMap.put(key, tableCheckMap);
		}
		getTableViewer().setAllChecked(checkedFlag);
	}

	protected void addSelectionButtonListener(Button selectButton,
			Button deselectButton) {
		SelectionListener listener = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Object[] viewerElements = fContentProvider
						.getElements(getTreeViewer().getInput());
				if (fContainerMode) {
					getTreeViewer().setCheckedElements(viewerElements);
				} else {
					for (int i = 0; i < viewerElements.length; i++) {
						getTreeViewer().setSubtreeChecked(viewerElements[i],
								true);
					}
				}
				packageCheckedMap.clear();
				if (getTableViewer().getInput() != null) {
					handleTablesChecked((Package) getTableViewer().getInput(),
							true);
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
					handleTablesChecked((Package) getTableViewer().getInput(),
							false);
				}
				updateOKStatus();
			}
		};
		deselectButton.addSelectionListener(listener);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		Object selectedObj = ((IStructuredSelection) event.getSelection())
				.getFirstElement();
		if (selectedObj instanceof Package) {
			Package pckg = (Package) selectedObj;
			this.setOutput(pckg);
			TdTable[] tables = getCheckedTables(pckg);
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

		public PackageKey(Package pckg) {
			schemaName = SwitchHelpers.SCHEMA_SWITCH.doSwitch(pckg) == null ? "__Schema_Name__"
					: SwitchHelpers.SCHEMA_SWITCH.doSwitch(pckg).getName();
			catalogName = SwitchHelpers.CATALOG_SWITCH.doSwitch(pckg) == null ? "__Catalog_Name__"
					: SwitchHelpers.CATALOG_SWITCH.doSwitch(pckg).getName();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((catalogName == null) ? 0 : catalogName.hashCode());
			result = prime * result
					+ ((schemaName == null) ? 0 : schemaName.hashCode());
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

		public void putTableChecked(Table table, Boolean isChecked) {
			tableNameMap.put(table.getName(), isChecked);
		}

		public Boolean getTableChecked(Table table) {
			return tableNameMap.get(table.getName());
		}

		public void putAllChecked(Table[] tables, Boolean isChecked) {
			for (int i = 0; i < tables.length; i++) {
				tableNameMap.put(tables[i].getName(), isChecked);
			}
		}

		public TdTable[] getCheckedTables(List<TdTable> tableList) {
			List<TdTable> checkedTables = new ArrayList<TdTable>();
			for (TdTable table : tableList) {
				if (tableNameMap.containsKey(table.getName())
						&& tableNameMap.get(table.getName())) {
					checkedTables.add(table);
				}
			}
			return checkedTables.toArray(new TdTable[checkedTables.size()]);
		}

		public List<TdTable> getCheckedTableList(Package pckg) {
			List<TdTable> checkedTables = new ArrayList<TdTable>();

			List<TdTable> tableList = PackageHelper.getTables(pckg);

			for (TdTable table : tableList) {
				if (tableNameMap.containsKey(table.getName())
						&& tableNameMap.get(table.getName())) {
					checkedTables.add(table);
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

	private List<TdTable> getAllCheckedTables() {
		Object[] checkedNodes = this.getTreeViewer().getCheckedElements();
		List<TdTable> tableList = new ArrayList<TdTable>();
		for (int i = 0; i < checkedNodes.length; i++) {
			if (!(checkedNodes[i] instanceof Package)) {
				continue;
			}
			PackageKey packageKey = new PackageKey((Package) checkedNodes[i]);
			if (packageCheckedMap.containsKey(packageKey)) {
				TableCheckedMap tableMap = packageCheckedMap.get(packageKey);
				tableList.addAll(tableMap
						.getCheckedTableList((Package) checkedNodes[i]));
			} else {
				tableList.addAll(PackageHelper
						.getTables((Package) checkedNodes[i]));
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
			return ImageLib.getImage(ImageLib.TABLE);
		}

		public String getText(Object element) {
			return ((TdTable) element).getName();
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
					log
							.error(DefaultMessagesImpl
									.getString("TablesSelectionDialog.cannotGetChildren") + container.getLocation()); //$NON-NLS-1$
				}
				// MOD mzhao 2009-03-13 Feature 6066 Move all folders into one
				// project.
				if (container.equals(ResourcesPlugin.getWorkspace().getRoot()
						.getProject(
								org.talend.dataquality.PluginConstant
										.getRootProjectName()).getFolder(
								DQStructureManager.getMetaData()).getFolder(
								DQStructureManager.DB_CONNECTIONS))) {
					ComparatorsFactory.sort(members,
							ComparatorsFactory.FILEMODEL_COMPARATOR_ID);
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
				Package packageValue = SwitchHelpers.PACKAGE_SWITCH
						.doSwitch(eObj);
				TdCatalog parentCatalog = CatalogHelper
						.getParentCatalog(packageValue);
				if (parentCatalog != null) {
					return parentCatalog;
				}

				if (packageValue != null) {
					TdDataProvider tdDataProvider = DataProviderHelper
							.getTdDataProvider(packageValue);
					IFile findCorrespondingFile = PrvResourceFileHelper
							.getInstance()
							.findCorrespondingFile(tdDataProvider);
					return findCorrespondingFile;
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
					Catalog catalog = SwitchHelpers.CATALOG_SWITCH
							.doSwitch(pckg);
					Schema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(pckg);
					List<TdTable> temp = PackageHelper.getTables(pckg);
					TdTable[] tables = temp.toArray(new TdTable[temp.size()]);
					if (tables.length <= 0) {
						TdDataProvider provider = DataProviderHelper
								.getTdDataProvider(pckg);
						if (provider == null) {
							return null;
						}
						try {
							List<TdTable> tableList = null;
							if (catalog != null) {
								tableList = DqRepositoryViewService.getTables(
										provider, catalog, null, true);
							}
							if (schema != null) {
								tableList = DqRepositoryViewService.getTables(
										provider, schema, null, true);
							}
							tables = tableList.toArray(new TdTable[tableList
									.size()]);
						} catch (TalendException e) {
							MessageBoxExceptionHandler.process(e);
						}

						PrvResourceFileHelper.getInstance().save(provider);
					}
					return sort(tables,
							ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
				}
			}
			return null;
		}

		/**
		 * Sort the parameter objects, and return the sorted array.
		 * 
		 * @param objects
		 * @param comparatorId
		 *            the comparator id has been defined in the
		 *            {@link ComparatorsFactory};
		 * @return
		 */
		@SuppressWarnings("unchecked")
		protected Object[] sort(Object[] objects, int comparatorId) {
			if (objects == null || objects.length <= 1) {
				return objects;
			}
			Arrays.sort(objects, ComparatorsFactory
					.buildComparator(comparatorId));
			return objects;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}
}
