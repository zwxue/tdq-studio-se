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
package org.talend.dataprofiler.core.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.repository.utils.AbstractResourceChangesService;
import org.talend.repository.utils.XmiResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * 
 * DOC mzhao Handle resource unload events from TOS.
 */
public class TDQResourceChangeHandler extends AbstractResourceChangesService {

    private static Logger log = Logger.getLogger(TDQResourceChangeHandler.class);

    private XmiResourceManager xmiResourceManager = ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider()
            .getResourceManager();

    public TDQResourceChangeHandler() {
    }

    @Override
    public void handleUnload(Resource toBeUnloadedResource) {
        for (EObject eObject : toBeUnloadedResource.getContents()) {
            // try {
            if (eObject instanceof DatabaseConnection) {
                ProxyRepositoryViewObject.registerURI((DatabaseConnection) eObject, toBeUnloadedResource.getURI());
                // if (xmiResourceManager != null) {
                // try {
                // xmiResourceManager.saveResource(toBeUnloadedResource);
                // } catch (PersistenceException e) {
                // log.error(e, e);
                // }
                //
                // }
            } else if (eObject instanceof MDMConnection) {

                ProxyRepositoryViewObject.registerURI((MDMConnection) eObject, toBeUnloadedResource.getURI());
                if (xmiResourceManager != null) {
                    try {
                        xmiResourceManager.saveResource(toBeUnloadedResource);
                    } catch (PersistenceException e) {
                        log.error(e, e);
                    }

                }
            }

            // } catch (PersistenceException e) {
            // log.error(e, e);
            // }
            // else anaysis,report etc.
        }
        super.handleUnload(toBeUnloadedResource);
    }

    @Override
    public boolean handleResourceChange(ModelElement modelElement) {
        List<ModelElement> clientDependencys = EObjectHelper.getDependencyClients(modelElement);
        if (clientDependencys.size() > 0) {
            ModelElement[] dependencyElements = clientDependencys.toArray(new ModelElement[clientDependencys.size()]);

            DeleteModelElementConfirmDialog.showDialog(null,
                    PropertyHelper.getItemFile(PropertyHelper.getProperty(modelElement)), dependencyElements, DefaultMessagesImpl
                            .getString("TDQResourceChangeHandler.ConnectionNotBeSave"));
            return false;
        }

        // TODO Handle element deletion from resource, resource delete.
        return super.handleResourceChange(modelElement);

    }

    /**
     * add a prop instance to the static var.
     */
    public void handleLogicalDelete(Property prop) {
        // MOD qiongli 2010-12-6.bug 16843.can not be restored for svn project when it is prox property.
        if (prop == null)
            return;
        if (prop.eIsProxy()) {
            prop = (Property) EObjectHelper.resolveObject(prop);
        }
        // MOD qiongli 2010-10-22,bug 16610
        if (ProxyRepositoryViewObject.getRepositoryViewObjectByProperty(prop) != null) {
            LogicalDeleteFileHandle.refreshDelPropertys(1, prop);
        }
    }

    /**
     * remove prop instance in the static var.
     */
    public void handlePhysicalDelete(Property prop) {
        LogicalDeleteFileHandle.refreshDelPropertys(0, prop);
    }

    /**
     * remove prop instance in the static var.
     */
    public void handleRestore(Property prop) {
        LogicalDeleteFileHandle.refreshDelPropertys(0, prop);
    }
}
