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
package org.talend.dataprofiler.core.ui.action;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.top.repository.ProxyRepositoryManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class AbstractImportSourceFileAction extends Action implements ICheatSheetAction {

    protected IFolder folder;

    private static String historyFilePath = "./";

    /**
     * DOC bZhou AddSqlFileAction constructor comment.
     * 
     * @param folder
     */
    public AbstractImportSourceFileAction(IFolder folder) {
        this();
        this.folder = folder;
    }

    /**
     * 
     * DOC zshen AddSqlFileAction constructor comment.
     * 
     */
    public AbstractImportSourceFileAction() {
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.IMPORT));
        this.folder = getTypedFolder();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if (folder != null) {
            try {

                File[] files = computeFiles();

                if (files != null) {
                    for (File sourceFile : files) {
                        File targetFile = folder.getFile(sourceFile.getName()).getLocation().toFile();
                        doImport(sourceFile, targetFile);
                    }
                }
            } catch (Exception e) {
                ExceptionHandler.process(e);
            }

            saveAndRefresh();
        }
    }

    protected File[] computeFiles() throws IOException {
        FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

        if (historyFilePath != null) {
            dialog.setFilterPath(historyFilePath);
        }

        if (getFilterExtensions() != null) {
            dialog.setFilterExtensions(getFilterExtensions());
        }
        String path = dialog.open();

        if (path != null) {
            File file = new File(path);
            historyFilePath = file.getParent();

            return new File[] { file };
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "saveAndRefresh".
     */
    private void saveAndRefresh() {
        ProxyRepositoryManager.getInstance().save();
        CorePlugin.getDefault().refreshDQView();
        CorePlugin.getDefault().refreshWorkSpace();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
     * org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run(String[] params, ICheatSheetManager manager) {
        run();
    }

    protected abstract void doImport(File sourceFile, File targetFile) throws Exception;

    protected abstract IFolder getTypedFolder();

    protected abstract String[] getFilterExtensions();
}
