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

import org.eclipse.core.resources.IFolder;
import org.talend.core.model.properties.Property;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class UDIHandle extends EMFResourceHandle {

    /**
     * DOC bZhou DuplicateUDIHandle constructor comment.
     */
    UDIHandle(Property propety) {
        super(propety);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.action.actions.duplicate.DuplicateEMFResourceHandle#extractFolder(orgomg.cwm.
     * objectmodel.core.ModelElement)
     */
    @Override
    protected IFolder extractFolder(ModelElement oldObject) {
        return ResourceManager.getUDIFolder();
    }
}
