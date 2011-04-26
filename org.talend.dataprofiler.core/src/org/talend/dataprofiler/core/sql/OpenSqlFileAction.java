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
package org.talend.dataprofiler.core.sql;

import java.io.File;
import java.util.List;

import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class OpenSqlFileAction extends Action {

    protected static Logger log = Logger.getLogger(OpenSqlFileAction.class);

    private List<IFile> folder;

    private IEditorInput editorInput;

    /**
     * DOC qzhang AddSqlFileAction constructor comment.
     * 
     * @param selectedFiles
     */
    public OpenSqlFileAction(List<IFile> selectedFiles) {
        setText(DefaultMessagesImpl.getString("OpenSqlFileAction.openIn", PluginConstant.DATAEXPLORER_PERSPECTIVE)); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.CREATE_SQL_ACTION));
        this.folder = selectedFiles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        // ADD xqliu 2010-08-20 bug 13729
        new ChangePerspectiveAction(PluginConstant.SE_ID).run();
        // ~ 13729
        IWorkbenchWindow aww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage ap = aww.getActivePage();
        IPath location = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        for (IFile file : folder) {
            String portableString = location.append(file.getFullPath()).toPortableString();
            try {
                editorInput = new SQLEditorInput(new File(portableString));
                ap.openEditor(editorInput, SQLEditor.EDITOR_ID);
            } catch (PartInitException e) {
                log.error(e, e);
            }
        }
    }

    public IEditorInput getEditorInput() {
        return editorInput;
    }
}
