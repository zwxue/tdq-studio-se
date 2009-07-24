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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.ui.viewer.content.part.diff.ModelContentMergeDiffTab;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.cwm.helper.DataProviderHelper;
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
			ModelElement matchedElement = null;
			IFolderNode folderNode = null;
			if (selectedElement instanceof ColumnSet) {
				Package parentPackage = (Package) ((IFolderNode) selectedOjbect)
						.getParent();
				((ColumnSet) selectedElement).setNamespace(parentPackage);
				matchedElement = findMatchedModelElement(parentPackage,
						selectedElement);
				folderNode = FolderNodeHelper.getFolderNodes(matchedElement)[0];
			} else if (selectedElement instanceof Package) {
				TdDataProvider dataProvider = PrvResourceFileHelper
						.getInstance().findProvider((IFile) selectedOjbect)
						.getObject();
				((Package) selectedElement).getDataManager().add(dataProvider);
				matchedElement = findMatchedModelElement(dataProvider,
						selectedElement);
				if (actionType == TABLE_COMPARE) {
					folderNode = FolderNodeHelper
							.getFolderNodes(matchedElement)[0];
				} else {
					folderNode = FolderNodeHelper
							.getFolderNodes(matchedElement)[1];
				}
				
			} else {
				return;
			}

			folderNode.loadChildren();
			closeCurrentEditor();
			popCompUIAction.setSelectedObject(folderNode);
			popCompUIAction.run();
			// Remove namespace link
			if (selectedElement instanceof ColumnSet) {
				Package parentPackage = (Package) ((IFolderNode) selectedOjbect)
						.getParent();
				Package originPackage = parentPackage;
				originPackage.getOwnedElement().remove(selectedElement);

				EMFSharedResources.getInstance().saveResource(
						originPackage.eResource());
			}
		}
	}

	private ModelElement findMatchedModelElement(EObject parent,
			EObject similarElement) {
		if (parent instanceof TdDataProvider) {
			List<TdCatalog> catalogs = DataProviderHelper
					.getTdCatalogs((DataProvider) parent);
			for (TdCatalog catalog : catalogs) {
				if (catalog.getName().equalsIgnoreCase(
						((TdCatalog) similarElement).getName())) {
					return catalog;
				}
			}
			List<TdSchema> schames = DataProviderHelper
					.getTdSchema((DataProvider) parent);
			for (TdSchema schame : schames) {
				if (schame.getName()
						.equalsIgnoreCase(
						((TdSchema) similarElement).getName())) {
					return schame;
				}
			}
		} else if (parent instanceof Package) {
			EList<ModelElement> columnSets = ((Package) parent)
					.getOwnedElement();
			for (ModelElement columnSet : columnSets) {
				if (((ColumnSet) columnSet).getName().equalsIgnoreCase(
						((ColumnSet) similarElement).getName())) {
					return columnSet;
				}
			}
		}
		return null;
	}

	private void closeCurrentEditor() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		activePage.closeEditor(activePage.getActiveEditor(), false);
	}

}