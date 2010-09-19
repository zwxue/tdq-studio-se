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
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.dataprofiler.core.ui.action.actions.DuplicateAction;

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
        TreeSelection selection = (TreeSelection) this.getContext().getSelection();
        if (!selection.isEmpty()) {
            Object[] objs = selection.toArray();
            IFile[] files = new IFile[objs.length];
            for (int i = 0; i < objs.length; i++) {
                IFile file = (IFile) objs[i];
                files[i] = file;
            }

            DuplicateAction duplicate = new DuplicateAction(files);
            menu.add(duplicate);
        }
    }
}
