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

import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class RepositoryViewObjectHandle implements IDuplicateHandle, IDeletionHandle {

    private Property property;

    private IRepositoryViewObject repositoryObject;

    /**
     * DOC bZhou RepositoryViewObjectHandle constructor comment.
     * 
     * @param property
     */
    RepositoryViewObjectHandle(Property property) {
        if(property.eIsProxy()){
            property = (Property) EObjectHelper.resolveObject(property);
        }
        this.property = property;
        repositoryObject = ProxyRepositoryViewObject.getRepositoryViewObjectByProperty(property);
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
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#delete()
     */
    public boolean delete() throws Exception {

        if (property.eIsProxy()) {
            LogicalDeleteFileHandle.refreshDelPropertys(0, property);
            property = (Property) EObjectHelper.resolveObject(property);
        }
        // MOD qiongli 2010-9-10, bug 14469,15515
        // MOD qiongli 2010-10-14,bug 15587,save property instance which is not from file to the static var.
        if (isPhysicalDelete()) {
            ProxyRepositoryFactory.getInstance().deleteObjectPhysical(repositoryObject);
            LogicalDeleteFileHandle.refreshDelPropertys(0, property);
        } else {
            ProxyRepositoryFactory.getInstance().deleteObjectLogical(repositoryObject);
            // LogicalDeleteFileHandle.deleteLogical(file);
            LogicalDeleteFileHandle.refreshDelPropertys(1, property);
        }
        return true;
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
