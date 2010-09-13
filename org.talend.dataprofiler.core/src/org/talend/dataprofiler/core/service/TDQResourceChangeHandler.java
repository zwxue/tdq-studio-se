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

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.repository.utils.AbstractResourceChangesService;
import org.talend.repository.utils.XmiResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * 
 * DOC mzhao Handle resource unload events from TOS.
 */
public class TDQResourceChangeHandler extends AbstractResourceChangesService {

    private static Logger log = Logger.getLogger(TDQResourceChangeHandler.class);

    private XmiResourceManager xmiResourceManager = new XmiResourceManager();
    public TDQResourceChangeHandler() {
    }

    @Override
    public void handleUnload(Resource toBeUnloadedResource) {
        for (EObject eObject : toBeUnloadedResource.getContents()) {
            try {
                if (eObject instanceof DatabaseConnection) {
                    ProxyRepositoryViewObject.registerURI((DatabaseConnection) eObject,
                            toBeUnloadedResource.getURI());
                    // XmiResourceManager.getInstance().saveResource(toBeUnloadedResource);
                } else if (eObject instanceof MDMConnection) {
                    ProxyRepositoryViewObject.registerURI((MDMConnection) eObject,
                            toBeUnloadedResource.getURI());
                    xmiResourceManager .saveResource(toBeUnloadedResource);
                }
            } catch (PersistenceException e) {
                log.error(e, e);
            }
            // else anaysis,report etc.
        }
        super.handleUnload(toBeUnloadedResource);
    }

    @Override
    public boolean handleResourceChange(ModelElement modelElement) {
        // EList<Dependency> clientDependencys = modelElement.getSupplierDependency();
        // List<ModelElement> supplierList = new ArrayList<ModelElement>();
        // for (Dependency dependency : clientDependencys) {
        // EList<ModelElement> client = dependency.getClient();
        // if (client != null) {
        // supplierList.addAll(client);
        // }
        // }
        // ModelElement[] modelElements = new ModelElement[supplierList.size()];
        // DeleteModelElementConfirmDialog.showElementImpactConfirmDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
        // .getShell(), supplierList.toArray(modelElements), "", "");

        // TODO Handle element deletion from resource, resource delete.
        return super.handleResourceChange(modelElement);

    }
}
