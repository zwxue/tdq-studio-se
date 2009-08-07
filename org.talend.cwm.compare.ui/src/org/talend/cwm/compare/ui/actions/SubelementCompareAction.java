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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.compare.ui.viewer.content.part.diff.ModelContentMergeDiffTab;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.helper.FolderNodeHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class SubelementCompareAction extends Action {

	public static final int COLUMN_COMPARE = 0;

	public static final int TABLE_COMPARE = 1;

	public static final int VIEW_COMPARE = 2;

	private String text = null;

	private ModelContentMergeDiffTab diffTabLeft = null;

	private int actionType;

	private Object selectedOjbect = null;

	private PopComparisonUIAction popCompUIAction = null;

	public SubelementCompareAction(String showText,
			ModelContentMergeDiffTab diffTabLeft, Object selectedOjbect,
			int actionType) {
		text = showText;
		this.diffTabLeft = diffTabLeft;
		this.selectedOjbect = selectedOjbect;
		this.actionType = actionType;
		popCompUIAction = new PopComparisonUIAction(text);
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) diffTabLeft
				.getSelection();
		EObject selectedElement = null;
		if (selection.toList().size() == 1) {
			selectedElement = (EObject) selection.getFirstElement();
			IFolderNode folderNode = null;
			if (selectedElement instanceof ColumnSet) {
				Package parentPackage = (Package) ((IFolderNode) selectedOjbect)
						.getParent();
				// MOD mzhao 2009-08-05 Bug 8581
				// PackageHelper.addColumnSet((ColumnSet) selectedElement,
				// parentPackage);
				// ((ColumnSet) selectedElement).setNamespace(parentPackage);
				ModelElement matchedElement = findMatchedModelElement(
						parentPackage, selectedElement);
				folderNode = FolderNodeHelper.getFolderNodes(matchedElement)[0];
				openComparisonEditor(folderNode);
			} else if (selectedElement instanceof TdCatalog) {
				// Judge and see if there are schemas under the catalog.(Case of
				// SQL Servers)
				List<TdSchema> schemas = CatalogHelper
						.getSchemas((TdCatalog) selectedElement);
				if (schemas != null && schemas.size() > 0) {
					for (TdSchema sch : schemas) {
						folderNode = getTableOrViewFolder(sch);
						openComparisonEditor(folderNode);
					}
				} else {
					folderNode = getTableOrViewFolder(selectedElement);
					openComparisonEditor(folderNode);
				}
			} else if (selectedElement instanceof TdSchema) {
				folderNode = getTableOrViewFolder(selectedElement);
				openComparisonEditor(folderNode);
			} else {
				return;
			}

			// Remove namespace link
			if (selectedElement instanceof ColumnSet) {
				Package parentPackage = (Package) ((IFolderNode) selectedOjbect)
						.getParent();
				Package originPackage = parentPackage;
				PackageHelper.removeColumnSet((ColumnSet) selectedElement,
						originPackage);
				EMFSharedResources.getInstance().saveResource(
						originPackage.eResource());
			}
		}
	}

	public void openComparisonEditor(IFolderNode folderNode) {
		folderNode.loadChildren();
		// closeCurrentEditor();
		popCompUIAction.setSelectedObject(folderNode);
		popCompUIAction.run();
	}

	private IFolderNode getTableOrViewFolder(EObject selectedElement) {
		IFolderNode folderNode = null;
		TdDataProvider dataProvider = PrvResourceFileHelper.getInstance()
				.findProvider((IFile) selectedOjbect).getObject();
		// ((Package) selectedElement).getDataManager().add(dataProvider);
		ModelElement matchedElement = findMatchedModelElement(dataProvider,
				selectedElement);
		if (actionType == TABLE_COMPARE) {
			folderNode = FolderNodeHelper.getFolderNodes(matchedElement)[0];
		} else {
			folderNode = FolderNodeHelper.getFolderNodes(matchedElement)[1];
		}

		return folderNode;
	}

	private ModelElement findMatchedModelElement(EObject parent,
			EObject similarElement) {
		if (parent instanceof TdDataProvider) {
			List<TdCatalog> catalogs = DataProviderHelper
					.getTdCatalogs((DataProvider) parent);
			for (TdCatalog catalog : catalogs) {
				// MOD mzhao bug 8581 2009-08-05 (Case of MS SQL Server)
				TdCatalog ctl = CatalogHelper
						.getParentCatalog((ModelElement) similarElement);
				if (ctl == null
						|| !ctl.getName().equalsIgnoreCase(catalog.getName())) {
					continue;
				}
				List<TdSchema> schemas = CatalogHelper.getSchemas(catalog);
				if (schemas != null && schemas.size() > 0) {
					for (TdSchema tdSchema : schemas) {
						if (tdSchema.getName().equalsIgnoreCase(
								((TdSchema) similarElement).getName())) {
							return tdSchema;
						}
					}
				}
			}

			// Case of Mysql
			for (TdCatalog catalog : catalogs) {
				if (catalog.getName().equalsIgnoreCase(
						((TdCatalog) similarElement).getName())) {
					return catalog;
				}
			}

			// Case of Oracle
			List<TdSchema> schames = DataProviderHelper
					.getTdSchema((DataProvider) parent);
			for (TdSchema schame : schames) {
				if (schame.getName().equalsIgnoreCase(
						((TdSchema) similarElement).getName())) {
					return schame;
				}
			}
		} else if (parent instanceof Package) {
			// MOD mzhao bug 8581 2009-08-05
			List<ColumnSet> columnSets = new ArrayList<ColumnSet>();
			columnSets.addAll(PackageHelper.getTables((Package) parent));
			columnSets.addAll(PackageHelper.getViews((Package) parent));
			for (ModelElement columnSet : columnSets) {
				if (((ColumnSet) columnSet).getName().equalsIgnoreCase(
						((ColumnSet) similarElement).getName())) {
					return columnSet;
				}
			}
		}
		return null;
	}

	// private void closeCurrentEditor() {
	// IWorkbenchPage activePage =
	// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	// activePage.closeEditor(activePage.getActiveEditor(), false);
	// }

}
