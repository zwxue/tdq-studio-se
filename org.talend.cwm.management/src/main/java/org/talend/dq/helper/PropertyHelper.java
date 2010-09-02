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

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.core.model.properties.User;
import org.talend.core.model.properties.util.PropertiesSwitch;
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
        if (file != null && file.exists()) {

            if (StringUtils.equalsIgnoreCase(file.getFileExtension(), FactoriesUtil.PROPERTIES_EXTENSION)) {
                return getProperty(file.getLocation().toFile());
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
     * @param propertyFile
     * @return
     */
    public static Property getProperty(File propertyFile) {
        if (propertyFile.exists()) {
            if (propertyFile.getName().endsWith(FactoriesUtil.PROPERTIES_EXTENSION)) {
                URI propURI = URI.createFileURI(propertyFile.getAbsolutePath());
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
     * DOC bZhou Comment method "getItemTypedPath".
     * 
     * This method is to get the typed path for a specified item. Each typed item has a firm static path.
     * 
     * e.g. Project/TypedPath/StatePath/xxxx.property
     * 
     * @param item
     * @return
     */
    public static IPath getItemTypedPath(Property property) {
        EResourceConstant rc = EResourceConstant.getTypedConstant(property.getItem());
        return rc != null ? new Path(rc.getPath()) : null;
    }

    /**
     * DOC bZhou Comment method "getItemStatePath".
     * 
     * This method is to get the relative path to the typed path of a item.
     * 
     * e.g. Project/TypedPath/StatePath/xxxx.property
     * 
     * @param property
     * @return
     */
    public static IPath getItemStatePath(Property property) {
        Item item = property.getItem();
        URI propURI = property.eResource().getURI();
        String statePathStr = item.getState().getPath();

        if (StringUtils.isBlank(statePathStr) && propURI.isPlatformResource()) {
            IPath propPath = new Path(propURI.toPlatformString(true)).removeLastSegments(1);
            IPath typedPath = ResourceManager.getRootProject().getFullPath().append(getItemTypedPath(property));
            return propPath.makeRelativeTo(typedPath);
        }

        return new Path(statePathStr);
    }

    /**
     * DOC bZhou Comment method "getItemPath".
     * 
     * This method is to get the entire path of a item.
     * 
     * e.g. getItemPath() = Project/TypedPath/StatePath/xxxx.property.
     * 
     * @param property
     * @return
     */
    public static IPath getItemPath(Property property) {
        TDQItem item = (TDQItem) property.getItem();
        return ResourceManager.getRootProject().getFullPath().append(getItemTypedPath(property)).append(
                getItemStatePath(property)).append(item.getFilename());
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

    /**
     * DOC zshen Comment method "retrieveElement".
     * 
     * @param item
     * @return
     */
    public static ModelElement retrieveElement(Item item) {
        if (item == null) {
            return null;
        }
        PropertiesSwitch propertiesSwitch = new PropertiesSwitch() {

            @Override
            public Object caseConnectionItem(ConnectionItem object) {
                return object.getConnection();
            }

        };
        return (ModelElement) propertiesSwitch.doSwitch(item);
    }

    /**
     * DOC bZhou Comment method "extractProjectLabel".
     * 
     * This method is to extract the project technical label.
     * 
     * @param property
     * @return
     */
    public static String extractProjectLabel(Property property) {
        User author = property.getAuthor();
        if (author != null && !author.eIsProxy()) {
            InternalEObject iAuthor = (InternalEObject) property.getAuthor();
            Resource projResource = iAuthor.eResource();
            if (projResource != null) {
                IPath projectPath = new Path(projResource.getURI().toFileString());
                Object projOBJ = EObjectHelper.retrieveEObject(projectPath, PropertiesPackage.eINSTANCE.getProject());
                if (projOBJ != null) {
                    Project project = (Project) projOBJ;
                    return project.getTechnicalLabel();
                }
            }
        }

        return ReponsitoryContextBridge.PROJECT_DEFAULT_NAME;
    }
}
