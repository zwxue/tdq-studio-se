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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;

/**
 * @author scorreia This class creates the EMF resources and save them. All resources are stored in a ResourceSet (which
 * can be changed with the setter and getter methods). By default, path are platform relative.
 * 
 * This class also offers some static methods to help handling resources.
 * 
 */
public final class EMFUtil {

    private static Logger log = Logger.getLogger(EMFUtil.class);

    /** the encoding for xml files. */
    private static final String ENCODING = "UTF-8";

    /** the options needed for saving the resources. */
    private final Map<String, Object> options;

    /**
     * Whether path are platform relative.
     */
    private boolean usePlatformRelativePath = true;

    /** Static initialization of all EMF packages needed for the application. Done only once. */
    static {
        initialize();
    }

    private ResourceSet resourceSet;

    private String lastErrorMessage = null;

    /**
     * This CTOR initializes all packages and create a resource set.
     * 
     * @param fileExtensions the list of extensions (without the dot).
     */
    public EMFUtil() {
        // set the options
        options = new HashMap<String, Object>();
        options.put(XMIResource.OPTION_DECLARE_XML, Boolean.TRUE);
        options.put(XMIResource.OPTION_ENCODING, ENCODING);
        // the OPTION_DEFER_IDREF_RESOLUTION option needs at least EMF 2.3.1 to work correctly
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=216009
        // options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);

        // Obtain a new resource set
        resourceSet = new ResourceSetImpl();
    }

    /**
     * Method "initialize" initializes EMF factories, packages and file extensions.
     */
    private static void initialize() {
        // Initialize the enterprise factories
        FactoriesUtil.initializeAllFactories();

        // Initialize the enterprise packages
        FactoriesUtil.initializeAllPackages();

        // Register the XMI resource factory for the .enterprise extension
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;

        final Collection<String> fileExtensions = FactoriesUtil.getExtensions();
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        for (String extension : fileExtensions) {
            m.put(extension, new CwmResourceFactory());
        }
    }

    /**
     * Creates a new Resource in the ResourceSet. The file will be actually written when the save() method will be
     * called. By default, paths are platform relative. In order to call this method outside an Eclipse platform, set
     * the usePlatformRelativePath boolean to false.
     * 
     * @param uri the uri of the file in which the pool will be stored
     * @param eObject the pool that contains objects.
     * @return true (as per the general contract of the <tt>Collection.add</tt> method).
     */
    public boolean addPoolToResourceSet(String uri, EObject eObject) {
        return usePlatformRelativePath ? addPoolToResourceSet(URI.createPlatformResourceURI(uri, false), eObject)
                : addPoolToResourceSet(URI.createURI(uri), eObject);
    }

    /**
     * Creates a new Resource in the ResourceSet. The file will be actually written when the save() method will be
     * called. Do not use this method is you need to save with platform relative URIs.
     * 
     * @param file the file in which the pool will be stored
     * @param eObject the pool that contains objects.
     * @return true (as per the general contract of the <tt>Collection.add</tt> method).
     */
    public boolean addPoolToResourceSet(File file, EObject eObject) {
        return addPoolToResourceSet(URI.createFileURI(file.getAbsolutePath()), eObject);
    }

    /**
     * Creates a new Resource in the ResourceSet. The file will be actually written when the save() method will be
     * called.
     * 
     * @param uri the uri of the file in which the pool will be stored
     * @param eObject the pool that contains objects.
     * @return true (as per the general contract of the <tt>Collection.add</tt> method).
     */
    public boolean addPoolToResourceSet(URI uri, EObject eObject) {
        Resource res = resourceSet.createResource(uri);
        if (res == null) {
            lastErrorMessage = "No factory has been found for URI: " + uri;
            return false;
        }
        return res.getContents().add(eObject);
    }

    /**
     * Method "saveLastResource" saves the last resource added to the resource set.
     * 
     * @return true when ok.
     */
    public boolean saveLastResource() {
        boolean ok = true;
        EList<Resource> resources = resourceSet.getResources();
        Resource ress = resources.get(resources.size() - 1);
        try {
            ress.save(options);
        } catch (IOException e) {
            log.error("Error during the saving of resource. Uri=" + ress.getURI().toString(), e);
            // possible cause is a missing factory initialization and filename extension.
            ok = false;
        }
        return ok;
    }

    /**
     * Saves each resource of the resource set.
     * 
     * @return true if ok
     */
    public boolean save() {
        boolean ok = true;
        Iterator<Resource> r = resourceSet.getResources().iterator();
        while (r.hasNext()) {
            Resource ress = r.next();
            try {
                ress.save(options);
                if (log.isDebugEnabled()) {
                    log.debug("Resource saved in:" + ress.getURI());
                }
            } catch (IOException e) {
                log.error("Error during the saving of resource. Uri=" + ress.getURI().toString(), e);
                // possible cause is a missing factory initialization and filename extension.
                ok = false;
            }
        }
        return ok;
    }

    public static URI saveToUri(Resource res, URI destinationUri) {
        EMFUtil newEmfUtil = new EMFUtil();
        ResourceSet newResourceSet = newEmfUtil.getResourceSet();
        newResourceSet.getResources().add(res);

        // resolve all proxies of the resource to be moved
        EcoreUtil.resolveAll(res);

        // get all external cross references and for each resolve all proxies (inverse links)
        Map<EObject, Collection<Setting>> find = EcoreUtil.ExternalCrossReferencer.find(res);
        for (EObject object : find.keySet()) {
            Resource resource = object.eResource();
            if (resource == null) {
                continue;
            }
            EcoreUtil.resolveAll(resource);
        }
        Iterator<Resource> iterator = newResourceSet.getResources().iterator();
        while (iterator.hasNext()) {
            Resource newRes = iterator.next();
            if (!newRes.isLoaded()) {
                iterator.remove();
            }
        }

        URI changeUri = changeUri(res, destinationUri);
        newEmfUtil.save();
        return changeUri;
    }

    /**
     * @return the resource set (never null)
     */
    public ResourceSet getResourceSet() {
        return resourceSet;
    }

    /**
     * @param set the resource set (MUST not be null)
     */
    public void setResourceSet(ResourceSet set) {
        assert set != null;
        if (set != null) {
            resourceSet = set;
        }
    }

    /**
     * @return
     */
    public Map<String, Object> getOptions() {
        return options;
    }

    /**
     * Method "getLastErrorMessage".
     * 
     * @return the last error message or null when no error happened since the creation of this object.
     */
    public String getLastErrorMessage() {
        return this.lastErrorMessage;
    }

    /**
     * Utility method.
     * 
     * Changes the uri of a given resource. The new uri is formed with the name of the input resource's uri appended to
     * the path outputUri.
     * 
     * @param res the input resource
     * @param destinationUri the destination directory
     * @return the new uri.
     */
    public static URI changeUri(Resource res, URI destinationUri) {
        URI uri = res.getURI();
        URI newUri = destinationUri.appendSegment(uri.lastSegment());
        res.setURI(newUri);
        return newUri;
    }

    /**
     * Utility method.
     * 
     * Method "saveResource" saves the given resource. This method is a helper for saving quickly a given resource and
     * all its related resources (if any).
     * 
     * @param resource the resource to save
     * @return true if no problem
     */
    public static boolean saveResource(Resource resource) {
        // ResourceSet resourceSet = resource.getResourceSet();
        // EMFUtil util = new EMFUtil();
        // if (resourceSet != null) {
        // util.setResourceSet(resourceSet);
        // return util.save();
        // }
        // // else
        // util.getResourceSet().getResources().add(resource);
        return saveSingleResource(resource);
    }

    /**
     * Utility method.
     * 
     * Method "saveSingleResource" saves the given resource only. This method is a helper for saving quickly a given
     * resource. It does not saved the related resources. This could result in an exception when other related resources
     * should be saved with this resource.
     * 
     * @param resource the resource to save
     * @return true if no problem
     */
    public static boolean saveSingleResource(Resource resource) {
        EMFUtil util = new EMFUtil();
        util.getResourceSet().getResources().add(resource);
        boolean save = util.save();
        if (!save) {
            log.warn("Problem when saving resources " + util.getLastErrorMessage());
        }
        return save;
    }

    /**
     * Getter for usePlatformRelativePath.
     * 
     * @return the usePlatformRelativePath
     */
    public boolean isUsePlatformRelativePath() {
        return this.usePlatformRelativePath;
    }

    /**
     * Sets the usePlatformRelativePath.
     * 
     * @param usePlatformRelativePath the usePlatformRelativePath to set
     */
    public void setUsePlatformRelativePath(boolean usePlatformRelativePath) {
        this.usePlatformRelativePath = usePlatformRelativePath;
    }

}
