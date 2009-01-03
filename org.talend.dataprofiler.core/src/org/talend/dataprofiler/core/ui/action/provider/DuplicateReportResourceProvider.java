// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.ui.action.actions.DuplicateCWMResourceAction;

/**
 * 
 * DOC mzhao class global comment. Duplicate report file(s) provider class.
 */
public class DuplicateReportResourceProvider extends CommonActionProvider {

    @Override
    public void fillContextMenu(IMenuManager menu) {
        TreeSelection selection = (TreeSelection) this.getContext().getSelection();
        if (!selection.isEmpty()) {
            Object[] objs = selection.toArray();
            IFile[] files = new IFile[objs.length];
            for (int i = 0; i < objs.length; i++) {
                IFile file = (IFile) objs[i];
                files[i] = file;
            }
            DuplicateCWMResourceAction duplicate = new DuplicateCWMResourceAction(files);
            menu.add(duplicate);
        }
    }
}
