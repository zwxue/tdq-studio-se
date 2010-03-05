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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.ui.action.actions.CreateUDIAction;
import org.talend.dataprofiler.core.ui.action.actions.ExportUDIAction;
import org.talend.dataprofiler.core.ui.action.actions.ImportUDIAction;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UDIActionProvider extends CommonActionProvider {

    protected static Logger log = Logger.getLogger(UDIActionProvider.class);

    @Override
    public void fillContextMenu(IMenuManager menu) {
        TreeSelection treeSelection = ((TreeSelection) this.getContext().getSelection());

        if (treeSelection.size() == 1) {
            Object obj = treeSelection.getFirstElement();
            if (obj instanceof IFolder) {
                try {
                    IFolder folder = (IFolder) obj;
                    if (ResourceService.isSubFolder(ResourceManager.getUDIFolder(), folder)) {
                        menu.add(new CreateUDIAction(folder));
                        menu.add(new ImportUDIAction(folder));
                        menu.add(new ExportUDIAction(folder, false));
                        menu.add(new ExportUDIAction(folder, true));
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        }
    }
}
