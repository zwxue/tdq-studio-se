// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions.handle;

import org.eclipse.core.resources.IFile;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;

/**
 * Duplicate a SQL source file
 */
public class SourceFileDuplicateHandle extends AbstractTDQFileDuplicateHandle {

    SourceFileDuplicateHandle(IRepositoryNode node) {
        super(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicate(java.lang.String)
     */
    @Override
    protected Item createFileItemByDuplicateFile(IFile newFile, String fileExtension, String newName) {

        return DQStructureManager.getInstance().createSourceFileItem(
                WorkspaceUtils.ifileToFile(file),
                newFile.getFullPath().removeLastSegments(1)
                        .makeRelativeTo(ResourceManager.getSourceFileFolder().getFullPath().removeFirstSegments(1)), newName,
                PluginConstant.SQL_STRING);
    }

}
