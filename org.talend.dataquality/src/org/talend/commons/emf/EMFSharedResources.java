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

    private final ResourceSet resourceSet = emfUtil.getResourceSet();

    /**
     * Getter for instance.
     * 
     * @return the instance
     */
    public static synchronized EMFSharedResources getInstance() {
        if (instance == null) {
            instance = new EMFSharedResources();
        }
        return instance;
    }

    /**
     * Getter for emfUtil.
     * 
     * @return the emfUtil
     * @deprecated do not use directly EMFUtil.
     */
    private EMFUtil getEmfUtil() {
        return this.emfUtil;
    }

    /**
     * Method "unloadResources" unloads and removes all the resources from the resource set.
     */
    public synchronized void unloadResources() {
        List<Resource> resources = new ArrayList<Resource>(resourceSet.getResources());
        for (Resource resource : resources) {
            resource.unload();
            resourceSet.getResources().remove(resource);
        }
    }

    /**
     * Method "saveAll" saves all the resources of the resourceSet.
     * 
     * @return true when ok
     */
    public synchronized boolean saveAll() {
        return this.emfUtil.save();
    }

    /**
     * DOC scorreia Comment method "getSharedEmfUtil".
     * 
     * @return
     * @deprecated do not use directly EMFUtil.
     * 
     * TODO rli use {@link #saveAll()} method when needed.
     * 
     * TODO rli create other methods in this class when needed.
     */
    public static EMFUtil getSharedEmfUtil() {
        return getInstance().getEmfUtil();
    }
}
