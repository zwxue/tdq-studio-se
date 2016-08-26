// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ide.ResourceUtil;
import org.talend.core.model.properties.Item;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.repository.model.IRepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * 
 * DOC mzhao Abstract editor input for TDQ items.
 */
public abstract class AbstractItemEditorInput implements IEditorInput {

    /**
     * AbstractItemEditorInput constructor.
     * 
     * @param repNode
     */
    public AbstractItemEditorInput(IRepositoryNode repNode) {
        setRepNode(repNode);
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return getPath() + getModel().getName();
    }

    public String getToolTipText() {
        return getPath() + getModel().getName();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public Object getAdapter(Class adapter) {
        if (adapter.equals(IFile.class)) {
            return PropertyHelper.getItemFile(this.getItem().getProperty());
        }
        return null;
    }

    public String getPath() {
        return getItem().getState().getPath() + "/";//$NON-NLS-1$ 
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractItemEditorInput) {
            AbstractItemEditorInput other = (AbstractItemEditorInput) obj;
            boolean isEqualsId = getItem().getProperty().getId().equals(other.getItem().getProperty().getId());
            if (isEqualsId) {
                if (StringUtils.equals(getName(), other.getName())) {
                    if (getItem().getProperty().getVersion().equals(other.getItem().getProperty().getVersion())) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        } else if (obj instanceof IFileEditorInput) {
            IFile newFile = ResourceUtil.getFile(this);
            if (newFile != null) {
                return newFile.equals(ResourceUtil.getFile(obj));
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Item getItem() {
        Item item = getRepNode().getObject().getProperty().getItem();
        item = (Item) EObjectHelper.resolveObject(item);
        return item;
    }

    /**
     * get current RepNode.
     * 
     * @return
     */
    public abstract IRepositoryNode getRepNode();

    public abstract void setRepNode(IRepositoryNode node);

    /**
     * get the Model in the RepNode. for example: anaRepNode return its getAnalysis(), etc.
     * 
     * @return
     */
    public abstract ModelElement getModel();

    /**
     * get the Uuid of the ModelElement which included in this IEditorInput.
     * 
     * @return
     */
    public String getModelElementUuid() {
        if (getRepNode() != null) {
            return ResourceHelper.getUUID(getModel());
        }
        return null;
    }

}
