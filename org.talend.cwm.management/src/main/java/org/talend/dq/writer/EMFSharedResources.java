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
package org.talend.dq.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public final class EMFSharedResources {

    private static EMFSharedResources instance;

    private final EMFUtil emfUtil = new EMFUtil();

    private final ResourceSet resourceSet = emfUtil.getResourceSet();

    private Resource softwareDeploymentResource = null;

    /**
     * Getter for softwareDeploymentResource.
     * 
     * @return the softwareDeploymentResource
     */
    public Resource getSoftwareDeploymentResource() {
        if (softwareDeploymentResource == null) {
            softwareDeploymentResource = initSoftwareDeploymentResource();
        }
        return this.softwareDeploymentResource;
    }

    public boolean saveSoftwareDeploymentResource(TdSoftwareSystem softwareSystem) {
        getSoftwareDeploymentResource().getContents().add(softwareSystem);
        return saveSoftwareDeploymentResource();
    }

    public boolean saveSoftwareDeploymentResource() {
        return (softwareDeploymentResource != null) ? EMFUtil.saveSingleResource(softwareDeploymentResource) : false;
    }

    /**
     * DOC scorreia Comment method "initSoftwareDeploymentResource".
     * 
     * @return
     */
    private Resource initSoftwareDeploymentResource() {
        // MOD mzhao 2009-03-23,Feature 6066
        URI sUri = URI
                .createPlatformResourceURI(
                        "/"     + ReponsitoryContextBridge.getProjectName() + "/TDQ_Libraries/.softwaresystem." + SoftwaredeploymentPackage.eNAME, false); //$NON-NLS-1$
        Resource resource = resourceSet.getResource(sUri, false);

        if (resource == null) {
            resource = resourceSet.createResource(sUri);
        }

        return resource;

    }

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
     * Method "unloadResource" unload and remove the specification resource from the resource set.
     * 
     * @param uriString the uri sting of resource.
     */
    public synchronized void unloadResource(String uriString) {
        List<Resource> resources = new ArrayList<Resource>(resourceSet.getResources());
        for (Resource res : resources) {
            if (uriString.equals(res.getURI().toString())) {
                res.unload();
                resourceSet.getResources().remove(res);
            }
        }
    }

    /**
     * Method "addEObjectToResourceSet".
     * 
     * @param filePath the file path to the resource which contains the given object
     * @param eObject an EMF object to save in the appropriate resource
     * @return true when ok
     */
    public boolean addEObjectToResourceSet(String filePath, EObject eObject) {
        return this.emfUtil.addPoolToResourceSet(filePath, eObject);
    }

    public Resource getResource(URI uri, boolean loadOnDemand) {
        return resourceSet.getResource(uri, loadOnDemand);
    }

    /**
     * 
     * DOC mzhao Comment method "createResource".
     * 
     * @param uri
     * @return
     */
    public Resource createResource(URI uri) {
        return resourceSet.createResource(uri);
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
     * Method "saveResource" saves the resources of the resourceSet.
     * 
     * @return true when ok
     */
    public boolean saveResource(Resource resource) {
        return EMFUtil.saveResource(resource);
    }

    /**
     * Save the resource to destinationUri and saved the related resources.
     * 
     * @param res
     * @param destinationUri
     * @return
     */
    public URI saveToUri(Resource res, URI destinationUri) {

        // resolve all proxies of the resource to be moved
        EcoreUtil.resolveAll(res);

        // get all external cross references and for each resolve all proxies
        // (inverse links)
        Map<EObject, Collection<Setting>> find = EcoreUtil.ExternalCrossReferencer.find(res);
        List<Resource> needSaves = new ArrayList<Resource>();
        for (EObject object : find.keySet()) {
            Resource resource = object.eResource();
            if (resource == null) {
                continue;
            }
            EcoreUtil.resolveAll(resource);
            needSaves.add(resource);
        }

        URI changeUri = EMFUtil.changeUri(res, destinationUri);
        needSaves.add(res);
        for (Resource toSave : needSaves) {
            saveResource(toSave);
        }
        return changeUri;
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

    /**
     * Method "getLastErrorMessage".
     * 
     * @return the last error message or null when none exists.
     */
    public String getLastErrorMessage() {
        return this.emfUtil.getLastErrorMessage();
    }

    public EObject copyEObject(EObject oldObject) {
        EObject newObject = EcoreUtil.copy(oldObject);

        return newObject;
    }
}
