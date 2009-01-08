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
package org.talend.dataprofiler.core.migration.impl;

import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class CopyDefineFileTask extends AbstractMigrationTask {

    public boolean execute() {
        IPath path = new Path("/libraries/.Talend.definition");
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        if (root.exists(path)) {
            // do copy
            IFile file = root.getFile(path);
            path = new Path("/libraries/.Talend.definition.bak");
            try {
                file.copy(path, true, null);
                return true;
            } catch (CoreException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public Date getOrder() {
        return null;
    }

}
