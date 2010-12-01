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
package org.talend.dataprofiler.core.sql;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.talend.dataprofiler.core.ui.action.AbstractImportSourceFileAction;
import org.talend.resource.ResourceManager;
import org.talend.utils.io.FilesUtils;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ImportSqlFileAction extends AbstractImportSourceFileAction {

    protected static Logger log = Logger.getLogger(ImportSqlFileAction.class);

    /**
     * DOC bZhou ImportSqlFileAction constructor comment.
     */
    public ImportSqlFileAction(IFolder folder) {
        super(folder);
        setText("Import SQL");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractImportSourceFileAction#doImport(java.io.File, java.io.File)
     */
    @Override
    protected void doImport(File sourceFile, File targetFile) throws Exception {
        IFile iFile = folder.getFile(targetFile.getName());

        boolean isImport = true;
        if (iFile.exists()) {
            isImport = MessageDialog.openConfirm(null, "Import Conform",
                    "A item with the same name already exists, do you want to replace the existing items by the new one?");
        }

        if (isImport) {
            FilesUtils.copyFile(new FileInputStream(sourceFile), targetFile);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractImportSourceFileAction#getTypedFolder()
     */
    @Override
    protected IFolder getTypedFolder() {
        return ResourceManager.getSourceFileFolder();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractImportSourceFileAction#getFilterExtensions()
     */
    @Override
    protected String[] getFilterExtensions() {
        return new String[] { "*.sql" };
    }
}
