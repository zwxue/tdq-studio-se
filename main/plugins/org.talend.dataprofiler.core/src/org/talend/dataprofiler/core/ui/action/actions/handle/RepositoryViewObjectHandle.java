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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dq.helper.EObjectHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class RepositoryViewObjectHandle implements IDuplicateHandle {
    private Property property;

    private IRepositoryViewObject repositoryObject;

    /**
     * DOC bZhou RepositoryViewObjectHandle constructor comment.
     * 
     * @param property
     * @deprecated use createDuplicateHandle(IRepositoryNode) instead
     */
    RepositoryViewObjectHandle(Property property) {
        if (property.eIsProxy()) {
            property = (Property) EObjectHelper.resolveObject(property);
        }
    }

    RepositoryViewObjectHandle(IRepositoryNode node) {
        this.property = node.getObject().getProperty();
        this.repositoryObject = node.getObject();
        // if (property.eIsProxy()) {
        // property = (Property) EObjectHelper.resolveObject(property);
        // }
        // this.repositoryObject = repositoryObject;
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IActionHandle#getProperty()
     */
    public Property getProperty() {
        return property;
    }

    /**
     * DOC bZhou Comment method "getRepositoryObject".
     * 
     * @return
     */
    public IRepositoryViewObject getRepositoryObject() {
        return repositoryObject;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#getDependencies()
     */
    public List<ModelElement> getDependencies() {
        return EObjectHelper.getDependencyClients(repositoryObject);

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
        List<ConnectionItem> allMetadataConnectionsItem = new ArrayList<ConnectionItem>();
        try {
            allMetadataConnectionsItem = ProxyRepositoryFactory.getInstance().getMetadataConnectionsItem();
        } catch (PersistenceException e) {
        }
        for (ConnectionItem connectionItem : allMetadataConnectionsItem) {
            if (StringUtils.equals(label, connectionItem.getConnection().getName())) {
                return true;
            }
        }

        return false;
    }
}
