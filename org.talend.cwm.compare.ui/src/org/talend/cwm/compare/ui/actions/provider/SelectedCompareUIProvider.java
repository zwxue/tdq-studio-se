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
package org.talend.cwm.compare.ui.actions.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.compare.ui.actions.SelectedComparisonAction;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.dataprofiler.core.ui.action.provider.AbstractCommonActionProvider;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;


/**
 * 
 * DOC mzhao class global comment. Compare selected model elements.
 */
public class SelectedCompareUIProvider extends AbstractCommonActionProvider {

    private static final String COMPARISON_MENUTEXT = Messages.getString("SelectedCompareUIProvider.Comparison"); //$NON-NLS-1$

    private SelectedComparisonAction selectionCompareAction = null;

    public SelectedCompareUIProvider() {
        selectionCompareAction = new SelectedComparisonAction(COMPARISON_MENUTEXT);

    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        Iterator<?> iter = ((TreeSelection) this.getContext().getSelection()).iterator();
        // remove the "Database Compare" menu when the object is a mdm connection
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof RepositoryNode) {
                if (ConnectionUtils.isMdmConnection(((RepositoryNode) obj).getObject())) {
                    return;
                }
            } else {
                return;
            }
        }
        TreeSelection treeSelection = (TreeSelection) getContext().getSelection();
        if (treeSelection == null) {
            return;
        }

        Object firstElement = treeSelection.getFirstElement();
        RepositoryNode rNode = (RepositoryNode) firstElement;
        IFolder folder = WorkbenchUtils.getFolder(rNode);
        IFolder metadataFolder = ResourceManager.getMetadataFolder();
        if (!folder.getFullPath().toOSString().startsWith(metadataFolder.getFullPath().toOSString())) {
            return;
        }

        Object[] selectedObj = treeSelection.toArray();
        if (selectedObj.length < 2) {
            return;
        }

        List<IRepositoryViewObject> objects = new ArrayList<IRepositoryViewObject>();
        for (Object obj : selectedObj) {
            RepositoryNode node = (RepositoryNode) obj;
            objects.add(node.getObject());
        }

        selectionCompareAction.refreshSelectedObj(objects.get(0), objects.get(1));
        menu.add(selectionCompareAction);

    }

}
