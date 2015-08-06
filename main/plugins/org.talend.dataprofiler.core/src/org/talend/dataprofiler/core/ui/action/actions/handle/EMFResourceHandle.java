// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions.handle;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.exception.BusinessException;
import org.talend.core.model.metadata.builder.connection.AbstractMetadataObject;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.Property;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.writer.AElementPersistance;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class EMFResourceHandle implements IDuplicateHandle {

    private ModelElement modelElement;

    /**
     * Getter for modelElement.
     * 
     * @return the modelElement
     */
    public ModelElement getModelElement() {
        return this.modelElement;
    }

    protected IFile file;

    protected Property property;

    /**
     * DOC bZhou DuplicateEMFResourceAction constructor comment.
     */
    EMFResourceHandle(Property property) {
        this.property = property;

        IPath itemPath = PropertyHelper.getItemPath(property);
        this.file = ResourceManager.getRoot().getFile(itemPath);
        this.modelElement = ModelElementFileFactory.getModelElement(file);
    }

    /**
     * use RepositoryNode to construct a handle.
     * 
     * @param node
     */
    EMFResourceHandle(IRepositoryNode node) {
        this.property = node.getObject().getProperty();

        // IPath itemPath = PropertyHelper.getItemPath(property);
        IPath itemPath = WorkbenchUtils.getFilePath(node);
        this.file = ResourceManager.getRoot().getFile(itemPath);
        // this.modelElement = ModelElementFileFactory.getModelElement(file);
        this.modelElement = RepositoryNodeHelper.getResourceModelElement(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicate(java.lang.String)
     */
    public IFile duplicate(String newLabel) throws BusinessException {
        if (modelElement != null) {
            ModelElement newObject = (ModelElement) EMFSharedResources.getInstance().copyEObject(modelElement);

            newObject.setName(newLabel);
            // ADD xqliu 2012-05-03 TDQ-4853
            if (newObject instanceof AbstractMetadataObject) {
                AbstractMetadataObject amObj = (AbstractMetadataObject) newObject;
                amObj.setLabel(newLabel);
            }
            // ~ TDQ-4853

            // ADD msjian TDQ-5962 2012-11-13: when copy a database connection, should copy its datapackage.
            if (modelElement instanceof DatabaseConnection) {
                DatabaseConnection dbcon = (DatabaseConnection) modelElement;
                EList<Package> dataPackages = dbcon.getDataPackage();
                if (dataPackages != null) {
                    for (Package oldDataPackage : dataPackages) {
                        EList<Package> newDataPackages = ((DatabaseConnection) newObject).getDataPackage();
                        Package copyEObject = (Package) EMFSharedResources.getInstance().copyEObject(oldDataPackage);
                        newDataPackages.add(copyEObject);
                    }
                }
            }
            // TDQ-5962~

            IFolder folder = extractFolder(modelElement);

            if (folder != null) {
                newObject = update(modelElement, newObject);

                AElementPersistance elementWriter = ElementWriterFactory.getInstance().create(modelElement);

                if (elementWriter != null) {
                    elementWriter.create(newObject, folder);

                    boolean isNeedToSaveNewObject = false;
                    for (Dependency dependency : modelElement.getClientDependency()) {
                        for (ModelElement supplyier : dependency.getSupplier()) {
                            TypedReturnCode<Dependency> rc = DependenciesHandler.getInstance().setUsageDependencyOn(newObject,
                                    supplyier);
                            EMFSharedResources.getInstance().saveResource(rc.getObject().eResource());
                            isNeedToSaveNewObject = true;

                        }
                    }
                    // TDQ-6298 if called 'setUsageDependencyOn',should save the newObject too ,so that the dependecy
                    // persistence into the newObject file.
                    if (isNeedToSaveNewObject) {
                        EMFSharedResources.getInstance().saveResource(newObject.eResource());
                    }

                    URI uri;
                    if (newObject.eIsProxy()) {
                        uri = ((InternalEObject) newObject).eProxyURI();
                    } else {
                        uri = newObject.eResource().getURI();
                    }

                    return ResourceManager.getRoot().getFile(new Path(uri.toPlatformString(false)));
                }
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#getDependencies()
     */
    public List<ModelElement> getDependencies() {
        return EObjectHelper.getDependencyClients(file);
    }

    /**
     * DOC bZhou Comment method "extractFolder".
     * 
     * @param oldObject
     * @return
     */
    protected IFolder extractFolder(ModelElement oldObject) {
        Resource resource = oldObject.eResource();

        IPath path = new Path(resource.getURI().toPlatformString(false));
        IFile oldFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

        return (IFolder) oldFile.getParent();
    }

    /**
     * DOC bZhou Comment method "update".
     * 
     * @param oldObject
     * @param newObject
     * @return
     */
    protected ModelElement update(ModelElement oldObject, ModelElement newObject) {
        String author = ReponsitoryContextBridge.getAuthor();
        if (!StringUtils.isEmpty(author)) {
            MetadataHelper.setAuthor(newObject, author);
        }

        // newObject.getClientDependency().addAll(oldObject.getClientDependency());

        return newObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IActionHandle#getProperty()
     */
    public Property getProperty() {
        return this.property;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#validDuplicated()
     */
    public ReturnCode validDuplicated() {
        return new ReturnCode(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#isExistedLabel(java.lang.String)
     */
    public boolean isExistedLabel(String label) {
        List<ModelElement> allElement = ResourceFileMap.getAll();
        for (ModelElement element : allElement) {
            if (StringUtils.equals(label, element.getName())) {
                return true;
            }
        }

        return false;
    }
}
