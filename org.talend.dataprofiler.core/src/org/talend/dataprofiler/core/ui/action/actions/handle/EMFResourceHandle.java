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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.writer.AElementPersistance;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class EMFResourceHandle implements IDuplicateHandle, IDeletionHandle {

    private ModelElement modelElement;

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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.duplicate.IDuplicateHandle#duplicate()
     */
    public IFile duplicate() {
        if (modelElement != null) {
            ModelElement newObject = (ModelElement) EMFSharedResources.getInstance().copyEObject(modelElement);

            IFolder folder = extractFolder(modelElement);

            if (folder != null) {
                newObject = update(modelElement, newObject);

                AElementPersistance elementWriter = ElementWriterFactory.getInstance().create(modelElement);

                if (elementWriter != null) {
                    TypedReturnCode<Object> trc = elementWriter.create(newObject, folder);
                    if (trc.getObject() instanceof IFile) {
                        return (IFile) trc.getObject();
                    }
                }
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#delete()
     */
    public boolean delete() throws Exception {
        if (isPhysicalDelete()) {
            LogicalDeleteFileHandle.deleteElement(file);
            ModelElementFileFactory.getResourceFileMap(file).delete(file);
        } else {
            LogicalDeleteFileHandle.deleteLogical(file);
        }

        return true;
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
        newObject.setName("copy of " + newObject.getName()); //$NON-NLS-1$

        String author = ReponsitoryContextBridge.getAuthor();
        if (!StringUtils.isEmpty(author)) {
            MetadataHelper.setAuthor(newObject, author);
        }

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
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#isPhysicalDelete()
     */
    public boolean isPhysicalDelete() {
        return property.getItem().getState().isDeleted();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#validDuplicated()
     */
    public ReturnCode validDuplicated() {
        return new ReturnCode(true);
    }
}
