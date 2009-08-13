// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
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
     * DOC mzhao Comment method "getModelElementResource".
     * 
     * @param me ,modelElement of EObject
     * @return File this element links.
     */
    public static IFile getModelElementResource(ModelElement me) {
        IFile resourceFile = null;
        URI uri = me.eResource().getURI();
        uri = me.eResource().getResourceSet().getURIConverter().normalize(uri);
        String scheme = uri.scheme();
        if ("platform".equals(scheme) && uri.segmentCount() > 1 && "resource".equals(uri.segment(0))) { //$NON-NLS-1$ //$NON-NLS-2$
            StringBuffer platformResourcePath = new StringBuffer();
            for (int j = 1, size = uri.segmentCount(); j < size; ++j) {
                platformResourcePath.append('/');
                platformResourcePath.append(uri.segment(j));
            }
            resourceFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformResourcePath.toString()));
        }
        return resourceFile;
    }

    /**
     * 
     * DOC Administrator Comment method "getModelElementPath".
     * 
     * @param me
     * @return
     */
    public static IPath getModelElementPath(ModelElement me) {
        return getModelElementResource(me).getFullPath();
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
