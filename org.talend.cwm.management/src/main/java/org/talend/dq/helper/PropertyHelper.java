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
package org.talend.dq.helper;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class PropertyHelper {

    private PropertyHelper() {

    }

    /**
     * DOC bZhou Comment method "getPropertyFile".
     * 
     * @param elementFile
     * @return null if can't find.
     */
    public static IFile getPropertyFile(IFile elementFile) {
        ModelElement modelElement = ModelElementFileFactory.getModelElement(elementFile);
        if (modelElement != null) {
            return getPropertyFile(modelElement);
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "getPropertyFile".
     * 
     * @param modelElement
     * @return null if can't find.
     */
    public static IFile getPropertyFile(ModelElement modelElement) {
        String propertyPath = MetadataHelper.getPropertyPath(modelElement);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        IPath propPath;

        if (propertyPath != null) {
            propPath = new Path(propertyPath);
        } else {
            String platformString = modelElement.eResource().getURI().toPlatformString(false);
            propPath = new Path(platformString).removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
        }

        return root.getFile(propPath);
    }

    /**
     * DOC bZhou Comment method "getPropertyFile".
     * 
     * @param elementResource
     * @return null if can't find.
     */
    public static IFile getPropertyFile(Resource elementResource) {
        EList<EObject> contents = elementResource.getContents();
        if (contents != null && !contents.isEmpty()) {
            ModelElement element = (ModelElement) contents.get(0);
            return getPropertyFile(element);
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "getProperty".
     * 
     * @param file
     * @return Null if can't find.
     */
    public static Property getProperty(IFile file) {
        if (file.exists()) {

            if (StringUtils.equalsIgnoreCase(file.getFileExtension(), FactoriesUtil.PROPERTIES_EXTENSION)) {
                URI propURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
                Resource resource = EMFSharedResources.getInstance().getResource(propURI, true);

                // in this case, we need to reload the content again.
                if (resource.getContents().isEmpty()) {
                    resource = new ResourceSetImpl().getResource(propURI, true);
                }

                if (resource.getContents() != null) {
                    Object object = EcoreUtil.getObjectByType(resource.getContents(), PropertiesPackage.eINSTANCE.getProperty());
                    if (object != null) {
                        return (Property) object;
                    }
                }
            } else {
                // try to get property from element file.
                IFile propertyFile = getPropertyFile(file);
                return getProperty(propertyFile);
            }

        }
        return null;
    }

    /**
     * DOC bZhou Comment method "getProperty".
     * 
     * @param element
     * @return
     */
    public static Property getProperty(ModelElement element) {
        IFile propertyFile = PropertyHelper.getPropertyFile(element);
        return propertyFile != null ? getProperty(propertyFile) : null;
    }

    /**
     * DOC bZhou Comment method "getTDQItem".
     * 
     * @param propertyFile
     * @return Null if can't find.
     */
    public static TDQItem getTDQItem(IFile propertyFile) {
        Property property = getProperty(propertyFile);
        if (property != null) {
            return (TDQItem) property.getItem();
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "getElementPath".
     * 
     * @param property
     * @return
     */
    public static IPath getElementPath(Property property) {
        TDQItem item = (TDQItem) property.getItem();

        IPath itemPath = getItemWorkspaceBasePath(property);
        String path = item.getState().getPath();
        if (path == null || StringUtils.isEmpty(path)) {
            IPath propPath = new Path(property.eResource().getURI().toString());
            IPath mathPath = new Path(itemPath.lastSegment());
            int matchIndex = indexOfPath(propPath, mathPath);
            IPath relativePath = propPath.uptoSegment(matchIndex + 1);
            path = propPath.makeRelativeTo(relativePath).removeLastSegments(1).toString();
        }
        itemPath = itemPath.append(path);
        itemPath = itemPath.append(item.getFilename());

        return itemPath;
    }

    /**
     * DOC bZhou Comment method "indexOfPath".
     * 
     * @param path
     * @param indexPath
     * @return
     */
    private static int indexOfPath(IPath path, IPath indexPath) {
        Assert.isNotNull(path);
        Assert.isNotNull(indexPath);

        int count = 0;
        for (int i = 0; i < path.segmentCount(); i++) {
            if (path.segment(i).equals(indexPath.toString())) {
                return count;
            }
            count++;
        }
        return count;
    }

    /**
     * DOC bZhou Comment method "getItemTypedPath".
     * 
     * @param item
     * @return
     */
    public static IPath getItemTypedPath(Property property) {
        ModelElement element = retrieveElement((TDQItem) property.getItem());

        EResourceConstant rc = EResourceConstant.getTypedConstant(element);
        return rc != null ? new Path(rc.getPath()) : null;
    }

    /**
     * DOC bZhou Comment method "getItemWorkspaceBasePath".
     * 
     * @param property
     * @return
     */
    public static IPath getItemWorkspaceBasePath(Property property) {
        IPath itemBasePath = getItemTypedPath(property);
        return itemBasePath != null ? ResourceManager.getRootProject().getFolder(itemBasePath).getFullPath() : null;
    }

    /**
     * DOC bZhou Comment method "retrieveElement".
     * 
     * @param item
     * @return
     */
    public static ModelElement retrieveElement(TDQItem item) {
        URI itemURI = item.eResource().getURI();
        URI elementURI = itemURI.trimSegments(1).appendSegment(item.getFilename());

        Resource elementResource = item.eResource().getResourceSet().getResource(elementURI, false);
        if (elementResource == null) {
            elementResource = EMFSharedResources.getInstance().getResource(elementURI, true);
            // ResourceSet resourceSet = new EMFUtil().getResourceSet();
            // elementResource = resourceSet.getResource(elementURI, true);
        }

        return (ModelElement) elementResource.getContents().get(0);
    }
}
