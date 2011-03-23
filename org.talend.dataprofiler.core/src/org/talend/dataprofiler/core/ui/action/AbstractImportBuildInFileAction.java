// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2011 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.dataprofiler.core.helper.WorkspaceResourceHelper;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC bZhou class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z bzhou $
 * 
 */
public abstract class AbstractImportBuildInFileAction extends AbstractImportFileAction {

    public AbstractImportBuildInFileAction(RepositoryNode node) {
        super(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractImportFileAction#computeFilePath()
     */
    @Override
    public Map<File, IPath> computeFilePath() throws Exception {
        ERepositoryObjectType type = getRepositoryType();
        Plugin plugin = getPlugin();
        String srcPath = getSrcPath();
        Folder destFolder = (Folder) node.getObject();
        String suffix = getSuffix();
        boolean isResurse = isRecurse();

        Map<File, IPath> resultMap = new HashMap<File, IPath>();
        if (type != null && plugin != null && srcPath != null) {
            WorkspaceResourceHelper.computeFileFromPlugin(plugin, srcPath, isResurse, destFolder, suffix, type, resultMap);
        }
        return resultMap;
    }

    protected boolean isRecurse() {
        return true;
    }

    protected String getSuffix() {
        return null;
    }

    public abstract Plugin getPlugin();

    public abstract String getSrcPath();

}
