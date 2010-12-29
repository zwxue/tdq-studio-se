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
package org.talend.dataprofiler.core.ui.action.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.dataprofiler.core.ui.action.actions.OverviewAnalysisAction;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.resource.relational.Catalog;

/**
 * DOC rli class global comment. Detailled comment
 */
public class OverViewAnalysisActionProvider extends AbstractCommonActionProvider {

    /**
     * DOC rli OverViewAnalysisActionProvider constructor comment.
     */
    public OverViewAnalysisActionProvider() {
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }

        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());
        List list = currentSelection.toList();
        int catlogNumber = 0;
        int schemaNumber = 0;
        // List<Package> packageList = new ArrayList<Package>();
        // DOC MOD klliu 2010-12-14 feature 15750 for overview catalog or schema analysis.
        List<IRepositoryViewObject> packageList = new ArrayList<IRepositoryViewObject>();
        boolean showMenu = false;
        for (Object obj : list) {
            // MOD by zshen for 14891: use same repository API with TOS to persistent metadata
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                IRepositoryViewObject viewObject = node.getObject();
                if (viewObject instanceof MetadataSchemaRepositoryObject) {
                    MetadataSchemaRepositoryObject schemaObject = (MetadataSchemaRepositoryObject) viewObject;
                    packageList.add((IRepositoryViewObject) schemaObject);
                    schemaNumber++;
                    showMenu = true;
                } else if (viewObject instanceof MetadataCatalogRepositoryObject) {
                    MetadataCatalogRepositoryObject catalogObject = (MetadataCatalogRepositoryObject) viewObject;
                    //
                    packageList.add((IRepositoryViewObject) catalogObject);
                    catlogNumber++;
                    showMenu = true;
                } else {
                    Item item = viewObject.getProperty().getItem();
                    if (item instanceof ConnectionItem) {
                        // Connection connection = ((ConnectionItem) item).getConnection();
                        List<IRepositoryViewObject> children = viewObject.getChildren();
                        // packageList.addAll(connection.getDataPackage());
                        packageList.addAll(children);
                        showMenu = true;
                    }
                }
            } else if (obj instanceof Catalog) {
                packageList.add((IRepositoryViewObject) obj);
                catlogNumber++;
            } else {
                packageList.add((IRepositoryViewObject) obj);
                schemaNumber++;
            }
            if (catlogNumber != 0 && schemaNumber != 0) {
                return;
            }
        }
        OverviewAnalysisAction overviewAnalysisAction = new OverviewAnalysisAction(
                (orgomg.cwm.objectmodel.core.Package[]) packageList.toArray(new Package[packageList
                .size()]));

        if (showMenu) {
            // OverviewAnalysisAction overviewAnalysisAction = new OverviewAnalysisAction(packageList);
            // menu.add(overviewAnalysisAction);
        }
    }
}
