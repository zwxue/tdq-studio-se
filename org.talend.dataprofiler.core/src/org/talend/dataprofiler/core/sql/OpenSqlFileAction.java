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
package org.talend.dataprofiler.core.sql;

import static org.talend.dataprofiler.core.PluginConstant.SE_ID;
import java.io.File;
import java.util.List;

import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class OpenSqlFileAction extends Action {

    private List<IFile> folder;

    /**
     * DOC qzhang AddSqlFileAction constructor comment.
     * 
     * @param selectedFiles
     */
    public OpenSqlFileAction(List<IFile> selectedFiles) {
        setText("Open SQL File");
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
        IWorkbenchWindow aww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage ap = aww.getActivePage();
        ChangePerspectiveAction action = new ChangePerspectiveAction(SE_ID);
        action.run();
        IPath location = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        for (IFile file : folder) {
            String portableString = location.append(DQStructureManager.LIBRARIES).append(DQStructureManager.SOURCE_FILES).append(
                    file.getName()).toPortableString();
            try {
                ap.openEditor(new SQLEditorInput(new File(portableString)),
                        "net.sourceforge.sqlexplorer.plugin.editors.SQLEditor");
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }
}
