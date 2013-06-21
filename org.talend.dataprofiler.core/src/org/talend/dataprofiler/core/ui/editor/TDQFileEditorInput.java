// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor;

import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.RepositoryNode;


/**
 * Added 20130603, yyin TDQ-7143: Used to handle the file editor close event to unlock the file item.
 */
public class TDQFileEditorInput extends FileEditorInput {

    protected Item item = null;

    public static String DEFAULT_EDITOR_ID = "org.eclipse.ui.DefaultTextEditor";//$NON-NLS-1$
    /**
     * DOC yyin TDQFileEditorInput constructor comment.
     * 
     * @param file
     */
    public TDQFileEditorInput(IFile file) {
        super(file);
    }

    public void setFileItem(Item fItem) {
        this.item = fItem;
    }

    public Item getFileItem() {
        return this.item;
    }

    public void addCloseListener() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new PartListener() {

            @Override
            public void partClosed(IWorkbenchPart part) {
                if (getFile() != null && part instanceof SQLEditor) {
                    if (StringUtils.equalsIgnoreCase(getFile().getName(), ((SQLEditor) part).getEditorInput().getName())) {
                        dispose();
                        super.partClosed(part);
                    }
                } else if (getFile() != null && part instanceof TextEditor) {
                    if (StringUtils.equalsIgnoreCase(getFile().getName(), ((TextEditor) part).getEditorInput().getName())) {
                        dispose();
                        super.partClosed(part);
                    }
                }
            }
        });
    }

    public void dispose() {
        ProxyRepositoryManager.getInstance().unLock(item);

        RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFindFile(super.getFile());
        CommonViewer dqCommonViewer = RepositoryNodeHelper.getDQCommonViewer();
        if (dqCommonViewer != null && null != recursiveFind) {
            dqCommonViewer.refresh(recursiveFind);
        } else if (null != recursiveFind) {
            CorePlugin.getDefault().refreshDQView(recursiveFind);
        } else {
            CorePlugin.getDefault().refreshDQView();
        }
    }
}
