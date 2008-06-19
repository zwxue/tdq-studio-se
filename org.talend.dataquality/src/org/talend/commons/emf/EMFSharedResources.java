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
package org.talend.commons.emf;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public final class EMFSharedResources {

    private static EMFSharedResources instance;

    private final EMFUtil emfUtil = new EMFUtil();

    /**
     * Getter for instance.
     * 
     * @return the instance
     */
    public static EMFSharedResources getInstance() {
        if (instance == null) {
            instance = new EMFSharedResources();
        }
        return instance;
    }

    /**
     * Getter for emfUtil.
     * 
     * @return the emfUtil
     */
    public EMFUtil getEmfUtil() {
        return this.emfUtil;
    }

    public void unloadResources() {
        ResourceSet resourceSet = this.emfUtil.getResourceSet();
        List<Resource> resources = new ArrayList<Resource>(resourceSet.getResources());
        for (Resource resource : resources) {
            resource.unload();
            resourceSet.getResources().remove(resource);
        }
    }

    public static EMFUtil getSharedEmfUtil() {
        return getInstance().getEmfUtil();
    }
}
