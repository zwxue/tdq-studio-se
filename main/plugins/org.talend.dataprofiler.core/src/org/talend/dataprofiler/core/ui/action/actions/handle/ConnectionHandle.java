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
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.PropertyHelper;
import org.talend.repository.model.IRepositoryNode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ConnectionHandle extends RepositoryViewObjectHandle {

    /**
     * DOC bZhou ConnectionHandle constructor comment.
     * 
     * @param property
     */
    ConnectionHandle(Property property) {
        super(property);
    }

    ConnectionHandle(IRepositoryNode node) {
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
            Item item = PropertyHelper.getProperty(copyFile).getItem();
            if (item instanceof ConnectionItem) {
                Connection connection = ((ConnectionItem) item).getConnection();
                CWMPlugin.getDefault().addConnetionAliasToSQLPlugin(connection);
            }
            return copyFile;
        }
        return null;
    }
}
