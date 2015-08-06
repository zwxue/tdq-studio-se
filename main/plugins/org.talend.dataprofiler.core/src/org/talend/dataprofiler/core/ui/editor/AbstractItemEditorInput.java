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
package org.talend.dataprofiler.core.ui.editor;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.ide.ResourceUtil;
import org.talend.core.model.properties.Item;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;

/**
 * 
 * DOC mzhao Abstract editor input for TDQ items.
 */
public class AbstractItemEditorInput implements IEditorInput {

    protected Item item = null;

    public AbstractItemEditorInput(Item tdqItem) {
        this.item = tdqItem;

    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return item.getProperty().getLabel();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return item.getProperty().getLabel();
    }

    public Object getAdapter(Class adapter) {
        if (adapter.equals(IFile.class)) {
            return PropertyHelper.getItemFile(this.getItem().getProperty());
        }
        return null;
    }

    public String getPath() {
        return item.getState().getPath() + "/";//$NON-NLS-1$ 
    }

    @Override
    public boolean equals(Object obj) {
        // if (this == obj) {
        // return true;
        // }
        if (obj instanceof AbstractItemEditorInput) {
            AbstractItemEditorInput other = (AbstractItemEditorInput) obj;
            boolean isEqualsId = item.getProperty().getId().equals(other.item.getProperty().getId());
            if (isEqualsId) {
                if (StringUtils.equals(item.getProperty().getLabel(), other.item.getProperty().getLabel())) {
                    if (item.getProperty().getVersion().equals(other.item.getProperty().getVersion())) {
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
        if (item.eIsProxy()) {
            item = (Item) EObjectHelper.resolveObject(item);
        }
        return item;
    }

    /**
     * get the Uuid of the ModelElement which included in this IEditorInput.
     * 
     * @return
     */
    public String getModelElementUuid() {
        return null;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
