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
package org.talend.commons.utils;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.talend.resource.ResourceManager;
import org.talend.utils.string.AsciiUtils;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public final class WorkspaceUtils {

    private WorkspaceUtils() {

    }

    public static File ifileToFile(IFile ifile) {
        IPath location = ifile.getLocation() == null ? ResourceManager.getRootProject().getLocation().append(ifile.getFullPath())
                : ifile.getLocation();
        return location.toFile();
    }

    public static IFile fileToIFile(File file) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath location = Path.fromOSString(file.getAbsolutePath());
        IFile ifile = workspace.getRoot().getFileForLocation(location);
        return ifile;
    }

    /**
     * 
     * DOC mzhao convert emf resource to workspace resource.
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

    public static String normalize(String pathName) {
        return pathName == null ? pathName : AsciiUtils.replaceCharacters(pathName, "/: \\", "____");

    }
}
