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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
        if (propertyPath != null) {
            return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(propertyPath));
        }

        return null;
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
     * @param propertyFile
     * @return Null if can't find.
     */
    public static Property getProperty(IFile propertyFile) {
        URI propURI = URI.createPlatformResourceURI(propertyFile.getFullPath().toString(), false);
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
        return null;
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

        IPath proPath = getItemWorkspaceBasePath(item);
        proPath = proPath.append(item.getState().getPath());
        proPath = proPath.append(item.getFilename());

        return proPath;
    }

    /**
     * DOC bZhou Comment method "getItemBasePath".
     * 
     * @param item
     * @return
     */
    public static IPath getItemBasePath(TDQItem item) {
        final String filename = item.getFilename();
        if (filename != null) {
            String fileExtension = new Path(filename).getFileExtension();
            return getItemBasePath(fileExtension);
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "getItemBasePath".
     * 
     * @param fileExtension
     * @return
     */
    public static IPath getItemBasePath(String fileExtension) {
        EResourceConstant rc = EResourceConstant.getResourceConstant(fileExtension);
        if (rc != null) {
            return new Path(rc.getPath());
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "getItemWorkspaceBasePath".
     * 
     * @param item
     * @return
     */
    public static IPath getItemWorkspaceBasePath(TDQItem item) {
        final String filename = item.getFilename();
        if (filename != null) {
            String fileExtension = new Path(filename).getFileExtension();
            return getItemWorkspaceBasePath(fileExtension);
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "getItemWorkspaceBasePath".
     * 
     * @param fileExtension
     * @return
     */
    public static IPath getItemWorkspaceBasePath(String fileExtension) {
        IPath itemBasePath = getItemBasePath(fileExtension);
        return ResourceManager.getRootProject().getFolder(itemBasePath).getFullPath();
    }
}
