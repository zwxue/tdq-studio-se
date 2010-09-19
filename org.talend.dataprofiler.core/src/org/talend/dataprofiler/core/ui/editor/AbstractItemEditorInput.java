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
package org.talend.dataprofiler.core.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.talend.core.model.repository.IRepositoryViewObject;

/**
 * 
 * DOC mzhao Abstract editor input for TDQ items.
 */
public class AbstractItemEditorInput implements IEditorInput {

    protected IRepositoryViewObject reposViewObj = null;

    public AbstractItemEditorInput(IRepositoryViewObject reposViewObj) {
        this.reposViewObj = reposViewObj;
    }
    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return reposViewObj.getLabel();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return reposViewObj.getLabel();
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AbstractItemEditorInput) {
            AbstractItemEditorInput other = (AbstractItemEditorInput) obj;
            return reposViewObj.getLabel().equals(other.reposViewObj.getLabel());
        }
        return false;
    }

    public IRepositoryViewObject getRepositoryViewObject() {
        return reposViewObj;
    }
}
