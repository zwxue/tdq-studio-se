// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;

/**
 * DOC xqliu  class global comment. Detailled comment
 */
public class SourceFileHandle extends SimpleHandle {

    SourceFileHandle(Property property) {
        super(property);
    }

    SourceFileHandle(IFile file) {
        super(file);
    }

    SourceFileHandle(IRepositoryNode node) {
        super(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicate(java.lang.String)
     */
    public IFile duplicate(String newLabel) {
        String fileExtension = file.getFileExtension();

        IPath newFileNamePath = new Path(newLabel).addFileExtension(fileExtension);
        IFile newFile = file.getParent().getFile(newFileNamePath);

        if (PluginConstant.SQL_STRING.equalsIgnoreCase(fileExtension)) {
            DQStructureManager.getInstance().createSourceFileItem(
                    WorkspaceUtils.ifileToFile(file),
                    newFile.getFullPath().removeLastSegments(1)
                            .makeRelativeTo(ResourceManager.getSourceFileFolder().getFullPath().removeFirstSegments(1)),
                    newLabel, fileExtension);
            return newFile;
        }
        return null;
    }
}
