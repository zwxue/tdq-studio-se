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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.core.model.properties.User;
import org.talend.core.model.properties.util.PropertiesSwitch;
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
        if (!FactoriesUtil.PROPERTIES_EXTENSION.equals(elementFile.getFileExtension())) {
            IPath path = elementFile.getFullPath();

            path = path.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

            return ResourceManager.getRoot().getFile(path);
        }

        return elementFile;
    }

    /**
     * DOC bZhou Comment method "getPropertyFile".
     * 
     * @param modelElement
     * @return null if can't find.
     */
    public static IFile getPropertyFile(ModelElement modelElement) {

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        String platformString = modelElement.eResource().getURI().toPlatformString(false);
        IPath propPath = new Path(platformString).removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

        return root.getFile(propPath);
    }

    /**
     * DOC klliu Comment method "getPropertyFile".
     * 
     * @param elementResource
     * @return null if can't find.
     */
    public static IFile getPropertyFile(Resource elementResource) {
        assert elementResource != null;
        // MOD Beacuse of updating Model,then we use the same repository with tos .So wo can't cast the Resource to
        // ModelEelent klliu 2010-09-07
        if (elementResource.getURI().isPlatform()) {
            String pString = elementResource.getURI().toPlatformString(false);
            IPath ePath = new Path(pString);
            ePath = ePath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

            return ResourceManager.getRoot().getFile(ePath);
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
                URI propURI = URI.createPlatformResourceURI(file.getFullPath().toOSString(), false);
                Resource resource = EMFSharedResources.getInstance().getResource(propURI, true);
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
     * @param propertyFile
     * @return
     */
    public static Property getProperty(File propertyFile) {
        if (propertyFile.exists()) {
            if (propertyFile.getName().endsWith(FactoriesUtil.PROPERTIES_EXTENSION)) {
                URI propURI = URI.createFileURI(propertyFile.getAbsolutePath());
                Resource resource = new ResourceSetImpl().getResource(propURI, true);

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
        if (element != null && element.eIsProxy()) {
            element = (ModelElement) EObjectHelper.resolveObject(element);
        }
        URI uri = element.eResource().getURI();
        if (uri.isPlatform()) {
            IFile propertyFile = PropertyHelper.getPropertyFile(element);
            return getProperty(propertyFile);
        } else {
            File file = new Path(uri.toFileString()).removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION)
                    .toFile();
            return getProperty(file);
        }
    }

    /**
     * DOC bZhou Comment method "createFolderItemProperty".
     * 
     * @return
     */
    public static Property createFolderItemProperty() {
        Property property = PropertiesFactory.eINSTANCE.createProperty();

        FolderItem item = PropertiesFactory.eINSTANCE.createFolderItem();
        item.setType(FolderType.FOLDER_LITERAL);

        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        item.setState(itemState);

        property.setId(EcoreUtil.generateUUID());
        property.setItem(item);
        item.setProperty(property);

        return property;
    }

    /**
     * 
     * DOC bZhou Comment method "createFolderItemProperty".
     * 
     * This mothod is to create a tdq item for TOP, this item is used to some element which is not have spacified item
     * definition.
     * 
     * like *.sql files.
     * 
     * @return
     */
    public static Property createTDQItemProperty() {
        Property property = PropertiesFactory.eINSTANCE.createProperty();
        property.setId(EcoreUtil.generateUUID());

        TDQItem item = PropertiesFactory.eINSTANCE.createTDQItem();
        item.setProperty(property);

        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(false);
        item.setState(itemState);

        property.setItem(item);

        return property;
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
        return getItemTypedPath(property.getItem());
    }

    /**
     * DOC bZhou Comment method "getItemTypedPath".
     * 
     * @param item
     * @return
     */
    public static IPath getItemTypedPath(Item item) {
        assert item != null;

        EResourceConstant rc = EResourceConstant.getTypedConstant(item);
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

        String statePathStr = null;
        if (item.getState() != null) {
            statePathStr = item.getState().getPath();
        }

        if (StringUtils.isBlank(statePathStr) && propURI.isPlatformResource()) {
            IPath propPath = new Path(propURI.toPlatformString(true)).removeLastSegments(1);
            IPath typedPath = ResourceManager.getRootProject().getFullPath().append(getItemTypedPath(property));
            return propPath.makeRelativeTo(typedPath);
        }

        return statePathStr != null ? new Path(statePathStr) : null;
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
        Item item = property.getItem();
        if (item instanceof TDQItem) {
            TDQItem tdqItem = (TDQItem) item;
            return ResourceManager.getRootProject().getFullPath().append(getItemTypedPath(property)).append(
                    getItemStatePath(property)).append(tdqItem.getFilename());
        } else {
            if (property.eIsProxy()) {
                property = (Property) EObjectHelper.resolveObject(property);
            }
            IPath itemFilePath = new Path(property.eResource().getURI().lastSegment()).removeFileExtension().addFileExtension(
                    FactoriesUtil.ITEM_EXTENSION);
            return ResourceManager.getRootProject().getFullPath().append(getItemTypedPath(property)).append(
                    getItemStatePath(property)).append(itemFilePath);
        }
    }

    /**
     * DOC bZhou Comment method "getItemFile".
     * 
     * @param property
     * @return
     */
    public static IFile getItemFile(Property property) {
        IPath itemPath = getItemPath(property);

        return ResourceManager.getRoot().getFile(itemPath);
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
                Connection connection = object.getConnection();
                if (connection.getName() == null) {
                    connection.setName(object.getProperty().getLabel());
                }
                return connection;
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
                URI uri = projResource.getURI();
                String pathString = uri.isPlatform() ? uri.toPlatformString(false) : uri.toFileString();
                IPath projectPath = new Path(pathString);

                Object projOBJ = EObjectHelper.retrieveEObject(projectPath, PropertiesPackage.eINSTANCE.getProject());
                if (projOBJ != null) {
                    Project project = (Project) projOBJ;
                    return project.getTechnicalLabel();
                }
            }
        }

        return ReponsitoryContextBridge.getProjectName();
    }

    /**
     * DOC bZhou Comment method "computePath".
     * 
     * @param property
     * @return
     */
    public static String computePath(Property property) {
        Resource eResource = property.eResource();
        if (eResource != null) {
            IPath propPath, typedPath;

            URI propURI = eResource.getURI();
            if (propURI.isPlatform()) {
                propPath = new Path(propURI.toPlatformString(true)).removeLastSegments(1);
                typedPath = ResourceManager.getRootProject().getFullPath().append(PropertyHelper.getItemTypedPath(property));

                IPath itemPath = propPath.makeRelativeTo(typedPath);

                return itemPath.toString();
            } else if (propURI.isFile()) {
                File file = new File(propURI.toFileString());
                return computePath(property, file);
            }
        }

        return "";
    }

    /**
     * DOC bZhou Comment method "computePath".
     * 
     * @param property
     * @param file
     * @return
     */
    public static String computePath(Property property, File file) {
        IPath filePath = new Path(file.getAbsolutePath()).setDevice(null);

        int flag = 0;
        EResourceConstant typedConstant = EResourceConstant.getTypedConstant(property.getItem());
        if (typedConstant != null) {
            String typedName = typedConstant.getName();

            for (int i = 0; i < filePath.segmentCount(); i++) {
                String seg = filePath.segment(i);
                if (seg.equals(typedName)) {
                    flag = i + 1;
                    break;
                }
            }
        } else {
            List<EResourceConstant> typedConstantList = EResourceConstant.getTypedConstantList();
            typedConstantList.add(EResourceConstant.OLD_DB_CONNECTIONS);
            typedConstantList.add(EResourceConstant.MDM_CONNECTIONS);

            for (int i = 0; i < filePath.segmentCount() && flag == 0; i++) {
                String seg = filePath.segment(i);
                for (EResourceConstant constant : typedConstantList) {
                    if (seg.equals(constant.getName())) {
                        flag = i + 1;
                        break;
                    }
                }
            }
        }

        IPath statPath = filePath.removeFirstSegments(flag).removeLastSegments(1);

        return statPath.toString();
    }
}
