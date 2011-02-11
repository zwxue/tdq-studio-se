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
package org.talend.dataprofiler.core.helper;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.talend.commons.utils.WorkspaceUtils;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public final class WorkspaceResourceHelper {

    private WorkspaceResourceHelper() {
    }



    /**
     * 
     * DOC Administrator Comment method "getModelElementPath".
     * 
     * @param me
     * @return
     */
    public static IPath getModelElementPath(ModelElement me) {
        return WorkspaceUtils.getModelElementResource(me).getFullPath();
    }

    /**
     * DOC bZhou Comment method "getInstallPath".
     * 
     * @return the install path for the current release.
     */
    public static String getInstallPath() {
        return Platform.getInstallLocation().getURL().getPath().substring(1);
    }

    /**
     * DOC bZhou Comment method "getLocationPath".
     * 
     * @return
     */
    public static String getLocationPath() {
        return Platform.getInstanceLocation().getURL().getPath().substring(1);
    }
}
