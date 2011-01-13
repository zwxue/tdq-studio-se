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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ui.action.actions.DuplicateAction;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class DuplicateResourceProvider extends AbstractCommonActionProvider {

    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        // DOC MOD klliu 2010-12-09 feature15750
        TreeSelection selection = (TreeSelection) this.getContext().getSelection();
        RepositoryNode firstElement = (RepositoryNode) selection.getFirstElement();
        if (firstElement.getType().equals(ENodeType.REPOSITORY_ELEMENT)) {
            if (!selection.isEmpty()) {
                Object[] objs = selection.toArray();
                IFile[] files = new IFile[objs.length];
                boolean showMenu = false;
                for (int i = 0; i < objs.length; i++) {
                    Object obj = objs[i];
                    RepositoryNode node = (RepositoryNode) obj;
                    if (node.getObject() != null) {
                        Item item = node.getObject().getProperty().getItem();
                        EClass eClass = item.eClass();
                        IPath folderPath = WorkbenchUtils.getPath(node);
                        String name = node
                                .getObject()
                                .getLabel()
                                .concat("_")
                                .concat(node.getObject().getProperty().getVersion())
                                .concat(".")
                                .concat(WorkbenchUtils.getItemExtendtion(item != null ? eClass.getClassifierID() : node
                                        .getObject().getProperty().getItem().eClass().getClassifierID()));
                        IPath append = folderPath.append(new Path(name));
                        IFile file = ResourceManager.getRootProject().getFile(append);
                        files[i] = file;
                        showMenu = true;
                    }
                }
                if (showMenu) {
                    DuplicateAction duplicate = new DuplicateAction();
                    menu.add(duplicate);
                }
            }
        }
    }
}
