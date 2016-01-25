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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import org.eclipse.core.resources.IFile;
import org.talend.commons.exception.BusinessException;
import org.talend.core.model.properties.Property;
import org.talend.repository.model.IRepositoryNode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class XMLDataProviderHandle extends RepositoryViewObjectHandle {

    /**
     * DOC bZhou XMLDataProviderHandle constructor comment.
     * 
     * @param file
     */
    XMLDataProviderHandle(Property property) {
        super(property);
    }

    XMLDataProviderHandle(IRepositoryNode node) {
        super(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicate(java.lang.String)
     */
    public IFile duplicate(String newLabel) throws BusinessException {
        Property property = getProperty();
        if (property != null) {
            IFile copyFile = new EMFResourceHandle(property).duplicate(newLabel);

            return copyFile;
        }
        return null;
    }

}
