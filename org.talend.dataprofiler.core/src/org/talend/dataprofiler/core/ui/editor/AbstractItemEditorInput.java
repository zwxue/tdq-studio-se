// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.talend.core.model.properties.Item;
import org.talend.dq.helper.EObjectHelper;

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
        return null;
    }

    public String getPath() {
        return item.getState().getPath() + "/";
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
            } else
                return false;
        }

        return false;
    }

    public Item getItem() {
        if (item.eIsProxy()) {
            item = (Item) EObjectHelper.resolveObject(item);
        }
        return item;
    }
}
