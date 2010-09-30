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

package org.talend.cwm.compare.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.compare.ui.viewer.content.part.diff.ModelContentMergeDiffTab;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.dataprofiler.core.helper.FolderNodeHelper;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.dq.writer.EMFSharedResources;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

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

    public SubelementCompareAction(String showText, ModelContentMergeDiffTab diffTabLeft, Object selectedOjbect, int actionType) {
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
        IStructuredSelection selection = (IStructuredSelection) diffTabLeft.getSelection();
        EObject selectedElement = null;
        if (selection.toList().size() == 1) {
            selectedElement = (EObject) selection.getFirstElement();
            IFolderNode folderNode = null;
            if (selectedElement instanceof ColumnSet) {
                Package parentPackage = (Package) ((IFolderNode) selectedOjbect).getParent();
                // MOD mzhao 2009-08-05 Bug 8581
                // PackageHelper.addColumnSet((ColumnSet) selectedElement,
                // parentPackage);
                // ((ColumnSet) selectedElement).setNamespace(parentPackage);
                ModelElement matchedElement = findMatchedModelElement(parentPackage, selectedElement);
                folderNode = FolderNodeHelper.getFolderNodes(matchedElement)[0];
                openComparisonEditor(folderNode);
            } else if (selectedElement instanceof Catalog) {
                // Judge and see if there are schemas under the catalog.(Case of
                // SQL Servers)
                List<Schema> schemas = CatalogHelper.getSchemas((Catalog) selectedElement);
                if (schemas != null && schemas.size() > 0) {
                    IRepositoryViewObject reposViewObj = (IRepositoryViewObject) selectedOjbect;
                    ConnectionItem item = (ConnectionItem) reposViewObj.getProperty().getItem();
                    Connection conn = item.getConnection();
                    popCompUIAction.setSelectedObject(findMatchedModelElement(conn, selectedElement));
                    popCompUIAction.run();
                } else {
                    folderNode = getTableOrViewFolder(selectedElement);
                    openComparisonEditor(folderNode);
                }
            } else if (selectedElement instanceof Schema) {
                folderNode = getTableOrViewFolder(selectedElement);
                openComparisonEditor(folderNode);
            } else {
                return;
            }

            // Remove namespace link
            if (selectedElement instanceof ColumnSet) {
                Package parentPackage = (Package) ((IFolderNode) selectedOjbect).getParent();
                Package originPackage = parentPackage;
                PackageHelper.removeColumnSet((ColumnSet) selectedElement, originPackage);
                EMFSharedResources.getInstance().saveResource(originPackage.eResource());
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
        Connection conn = null;
        if (selectedOjbect instanceof Catalog) {
            conn = ConnectionHelper.getTdDataProvider(((Catalog) selectedOjbect));
        } else {
            IRepositoryViewObject reposViewObj = (IRepositoryViewObject) selectedOjbect;
            ConnectionItem item = (ConnectionItem) reposViewObj.getProperty().getItem();
            conn = item.getConnection();
        }
        // ((Package) selectedElement).getDataManager().add(dataProvider);
        ModelElement matchedElement = findMatchedModelElement(conn, selectedElement);
        if (actionType == TABLE_COMPARE) {
            folderNode = FolderNodeHelper.getFolderNodes(matchedElement)[0];
        } else {
            folderNode = FolderNodeHelper.getFolderNodes(matchedElement)[1];
        }

        return folderNode;
    }

    private ModelElement findMatchedModelElement(EObject parent, EObject similarElement) {
        if (parent instanceof Connection) {
            if (similarElement instanceof Schema) {
                // Case of MS SQL Server.
                if (selectedOjbect instanceof Catalog) {
                    List<Schema> schemas = CatalogHelper.getSchemas((Catalog) selectedOjbect);
                    if (schemas != null && schemas.size() > 0) {
                        for (Schema tdSchema : schemas) {
                            if (tdSchema.getName().equalsIgnoreCase(((Schema) similarElement).getName())) {
                                return tdSchema;
                            }
                        }
                    }
                }
                // Case of Oracle
                List<Schema> schames = ConnectionHelper.getSchema((Connection) parent);
                for (Schema schame : schames) {
                    if (schame.getName().equalsIgnoreCase(((Schema) similarElement).getName())) {
                        return schame;
                    }
                }
            }

            // Case of Mysql
            if (similarElement instanceof Catalog) {
                List<Catalog> catalogs = ConnectionHelper.getCatalogs((Connection) parent);
                for (Catalog catalog : catalogs) {
                    if (catalog.getName().equalsIgnoreCase(((Catalog) similarElement).getName())) {
                        return catalog;
                    }
                }
            }

        } else if (parent instanceof Package) {
            // MOD klliu bug 15822 resolve proxy caused two Instance,so we must use one Instance
            if (parent != null && parent.eIsProxy()) {
                parent = EObjectHelper.resolveObject(parent);
            }
            // MOD mzhao bug 8581 2009-08-05
            List<ColumnSet> columnSets = new ArrayList<ColumnSet>();
            columnSets.addAll(PackageHelper.getTables((Package) parent));
            columnSets.addAll(PackageHelper.getViews((Package) parent));
            for (ModelElement columnSet : columnSets) {
                if (((ColumnSet) columnSet).getName().equalsIgnoreCase(((ColumnSet) similarElement).getName())) {
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
