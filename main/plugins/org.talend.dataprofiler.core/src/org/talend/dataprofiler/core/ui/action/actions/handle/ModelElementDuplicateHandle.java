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
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.exception.BusinessException;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.exception.ExceptionFactory;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.definition.userdefine.UDIndicatorDefinition;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.writer.AElementPersistance;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * This class is used for duplicate the ModelElements.
 */
public class ModelElementDuplicateHandle implements IDuplicateHandle {

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
        // update the Author
        String author = ReponsitoryContextBridge.getAuthor();
        if (!StringUtils.isEmpty(author)) {
            MetadataHelper.setAuthor(newObject, author);
        }

        return newObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#isExistedLabel(java.lang.String)
     */
    public boolean isExistedLabel(IFile file, String label) {
        List<ModelElement> allElement = ResourceFileMap.getAll();
        for (ModelElement element : allElement) {
            if (StringUtils.equals(label, element.getName())) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicateModelElement(orgomg
     * .cwm.objectmodel.core.ModelElement, java.lang.String)
     */
    protected ModelElement duplicateModelElement(ModelElement oldModelElement, String newName) {
        ModelElement newModelElement = (ModelElement) EMFSharedResources.getInstance().copyEObject(oldModelElement);
        // ModelElement newModelElement = (ModelElement) EObjectHelper.deepCopy(oldModelElement);
        newModelElement.setName(newName);
        newModelElement = update(oldModelElement, newModelElement);

        return newModelElement;
    }

    public void validateModelElement(ModelElement modelElement) throws BusinessException {
        if ((modelElement == null || modelElement.eResource() == null || modelElement.getName() == null || modelElement instanceof UDIndicatorDefinition
                && UDIHelper.getUDICategory(modelElement) == null)) {
            createBusinessException(DefaultMessagesImpl.getString("ModelElementDuplicateHandle.modelIsNull"));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicateItem(org.talend.core.model.properties
     * .Item, java.lang.String)
     */
    public Item duplicateItem(Item oldItem, String newName) throws BusinessException {
        // duplicate the related model element
        ModelElement oldModelElement = PropertyHelper.getModelElement(oldItem.getProperty());
        ModelElement newModelElement = duplicateModelElement(oldModelElement, newName);

        // create the related item and save
        AElementPersistance elementWriter = ElementWriterFactory.getInstance().create(oldModelElement);
        IFolder folder = extractFolder(oldModelElement);
        TypedReturnCode<Object> returnCode = elementWriter.create(newModelElement, folder);

        if (returnCode.isOk()) {
            if (needSaveDepend()) {
                // In order to save related dependency's change, need to save again
                elementWriter.save((Item) returnCode.getObject(), Boolean.TRUE);
            }
            return (Item) returnCode.getObject();
        } else {
            createBusinessException(DefaultMessagesImpl.getString("ModelElementDuplicateHandle.duplicateFail",
                    oldModelElement.getName(), returnCode.getMessage()));
        }
        return null;
    }

    protected void createBusinessException(String message) throws BusinessException {
        BusinessException createBusinessException = ExceptionFactory.getInstance().createBusinessException(message);
        throw createBusinessException;
    }

    /**
     * IF the item need to save the related dependency, return true.
     * 
     * @return
     */
    protected boolean needSaveDepend() {
        return false;
    }
}
