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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.ui.action.actions.DeleteObjectsAction;
import org.talend.dataprofiler.core.ui.action.actions.DeleteRepositoryObjectAction;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DeleteResourceProvider extends CommonActionProvider {

    /**
     * Adds a submenu to the given menu with the name "New Component".
     */
    public void fillContextMenu(IMenuManager menu) {
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        Action action = null;
        // MOD mzhao, handle delete action for metadata.
        if (obj instanceof IRepositoryViewObject) {
            action = new DeleteRepositoryObjectAction(Boolean.TRUE, (IRepositoryViewObject) obj);
            menu.add(action);
        } else if (obj instanceof IFile) {
            IFile file = (IFile) obj;
            if (FactoriesUtil.isEmfFile(file.getFileExtension())) {
                action = new DeleteObjectsAction(false);
                menu.add(action);
            }
        }
    }

}
