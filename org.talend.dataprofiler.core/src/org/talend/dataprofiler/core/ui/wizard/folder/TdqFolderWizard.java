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
package org.talend.dataprofiler.core.ui.wizard.folder;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dq.helper.ReportUtils;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.ReportSubFolderRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * This class is a wizard to create/rename a folder on workspace.
 */
public class TdqFolderWizard extends org.talend.repository.ui.wizards.folder.FolderWizard {

    private IRepositoryNode _node;

    private File _tarFile;

    public TdqFolderWizard(IPath path, ERepositoryObjectType objectType, IRepositoryNode node) {
        super(path, objectType, node.getObject().getLabel());
        _node = node;
    }

    public TdqFolderWizard(IPath path, ERepositoryObjectType objectType, RepositoryNode node, File tarFile) {
        this(path, objectType, node);
        _tarFile = tarFile;
    }

    @Override
    public boolean performFinish() {
        boolean rc = super.performFinish();
        if (_node instanceof ReportSubFolderRepNode) {
            if (rc) {
                String value2 = getMainPage().getName();
                IFolder folder = RepositoryNodeHelper.getIFolder(this._node);
                if (folder != null && _tarFile != null) {
                    ReportUtils.copyAndUpdateRepGenDocFileInfo(folder.getParent().getFolder(new Path(value2)), _tarFile,
                            folder.getName());
                }
            } else {
                // delete temporary files
                FilesUtils.deleteFile(_tarFile, Boolean.TRUE);
                rc = true;
            }
        }

        return rc;
    }

}
