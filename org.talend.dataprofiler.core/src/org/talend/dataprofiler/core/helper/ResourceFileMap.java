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
package org.talend.dataprofiler.core.helper;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.talend.commons.emf.EMFSharedResources;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ResourceFileMap {

    private Map<IFile, Resource> registedResourceMap = new HashMap<IFile, Resource>();

    protected boolean resourceChanged = true;

    // private ResourceFileMapHelper instance = new ResourceFileMapHelper();

    public void register(IFile fileString, Resource resource) {
        registedResourceMap.put(fileString, resource);
    }

    public void remove(IFile file) {
        this.registedResourceMap.remove(file);
    }

    public void clear() {
        this.registedResourceMap.clear();
    }

    /**
     * DOC rli Comment method "createResource".
     * 
     * @param file
     * @return
     */
    public Resource getFileResource(IFile file) {
        Resource res = registedResourceMap.get(file);
        if (res != null) {
            return res;
        }
        URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
        ResourceSet rs = EMFSharedResources.getSharedEmfUtil().getResourceSet();
        Resource resource = rs.getResource(uri, true);
        this.registedResourceMap.put(file, resource);
        return resource;
    }

    /**
     * Getter for resourceChanged.
     * 
     * @return the resourceChanged
     */
    public boolean isResourceChanged() {
        return resourceChanged;
    }

    /**
     * Sets the resourceChanged.
     * 
     * @param resourceChanged the resourceChanged to set
     */
    public void setResourceChanged(boolean resourceChanged) {
        this.resourceChanged = resourceChanged;
    }

}
