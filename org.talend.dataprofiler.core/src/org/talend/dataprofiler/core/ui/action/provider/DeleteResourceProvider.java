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
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.dataprofiler.core.ui.action.actions.DeleteObjectsAction;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceService;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DeleteResourceProvider extends AbstractCommonActionProvider {

    /**
     * Adds a submenu to the given menu with the name "New Component".
     */
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();

        if (obj instanceof IResource) {
            IResource resource = (IResource) obj;
            if (!ResourceService.isReadOnlyFolder(resource) && !isSystemIndicator(resource)) {
                menu.add(new DeleteObjectsAction());
            }
        } else {
            menu.add(new DeleteObjectsAction());
        }
    }

    private boolean isSystemIndicator(IResource resource) {
        if (resource instanceof IFile) {
            return resource.getFileExtension().equals("definition")
                    && resource.getFullPath().toOSString().contains(EResourceConstant.SYSTEM_INDICATORS.getName());
        } else {
            return resource.getFullPath().toOSString().contains(EResourceConstant.SYSTEM_INDICATORS.getName());
        }
    }

}
