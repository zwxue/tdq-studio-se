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
package org.talend.cwm.management.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.Component;
import orgomg.cwm.foundation.softwaredeployment.DeployedComponent;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author scorreia
 * 
 * This class manages the software systems.
 */
public final class SoftwareSystemManager {

    private static Logger log = Logger.getLogger(SoftwareSystemManager.class);

    private static SoftwareSystemManager instance;

    private SoftwareSystemManager() {
    }

    public static SoftwareSystemManager getInstance() {
        if (instance == null) {
            instance = new SoftwareSystemManager();
        }
        return instance;
    }

    // FIXME handle when data provider is not saved in a resource
    /**
     * Method "getSoftwareSystem".
     * 
     * @param dataProvider
     * @return the software system that is referenced by the data provider.
     * @deprecated
     */
    public TdSoftwareSystem getSoftwareSystem(Connection dataProvider) {
        TdSoftwareSystem softwareSystem = ConnectionHelper.getSoftwareSystem(dataProvider);
        if (softwareSystem == null) {
            // else create it and store it
            if (log.isDebugEnabled()) {
                log.debug("Trying to create the softwareSystem object from the given data provider " + dataProvider.getName());//$NON-NLS-1$
            }

            java.sql.Connection connection = null;

            try {
                // create it
                TypedReturnCode<java.sql.Connection> trc = JavaSqlFactory.createConnection(dataProvider);
                if (trc.isOk()) {
                    connection = trc.getObject();
                    // softwareSystem = DatabaseContentRetriever.getSoftwareSystem(connection);
                    softwareSystem = ConnectionHelper.getSoftwareSystem(connection);
                    if (softwareSystem != null /* && softwareSystem.eResource() != null */) { // store it
                        if (ConnectionHelper.setSoftwareSystem(dataProvider, softwareSystem)) {
                            saveSoftwareSystem(softwareSystem);
                            cleanSoftWareSystem();
                        }
                    }
                }
            } catch (SQLException e) {
                log.error(e, e);
            } finally {
                if (connection != null) {
                    ConnectionUtils.closeConnection(connection);
                }
            }
        } else if (log.isDebugEnabled()) { // only debug
            log.debug("The softwareSystem " + softwareSystem.getName() + " has been found for the given data provider "//$NON-NLS-1$//$NON-NLS-2$
                    + dataProvider.getName());
        }
        return softwareSystem;
    }

    /**
     * 
     * Find the TdSoftwareSystem instance from EMF model, if it does not exist, will NOT reload from database.
     * 
     * @param dataProvider
     * @return
     * @deprecated
     */
    public TdSoftwareSystem getSoftwareSystemFromModel(Connection dataProvider) {
        if (dataProvider == null) {
            return null;
        }
        Resource softwareSystemResource = EMFSharedResources.getInstance().getSoftwareDeploymentResource();
        if (WorkspaceUtils.getModelElementResource(softwareSystemResource.getURI()).exists()) {
            softwareSystemResource = EMFSharedResources.getInstance().reloadsoftwareDeploymentResource();
        }
        if (softwareSystemResource != null) {
            List<EObject> softwareSystems = softwareSystemResource.getContents();
            // Loop the software system from .softwaresystem.softwaredeployment file.
            for (EObject softwareSystem : softwareSystems) {

                if (softwareSystem instanceof TdSoftwareSystem) {
                    List<ModelElement> ownedELements = ((TdSoftwareSystem) softwareSystem).getOwnedElement();
                    // Loop owned element.
                    for (ModelElement me : ownedELements) {
                        if (me == null || !(me instanceof Component)) {
                            continue;
                        }
                        List<DeployedComponent> deployComponents = ((Component) me).getDeployment();
                        // deployComponents != null is true any time
                        if (deployComponents.size() > 0) {
                            if (ResourceHelper.areSame(deployComponents.get(0), dataProvider)) {
                                // Find the software system by data provider.
                                return (TdSoftwareSystem) softwareSystem;
                            }

                        }
                    }

                }

            }

        }
        return null;
    }

    /**
     * DOC scorreia Comment method "saveSoftwareSystem".
     * 
     * @param util
     * @param softwareSystem
     * @deprecated
     */
    public static boolean saveSoftwareSystem(TdSoftwareSystem softwareSystem) {
        EMFSharedResources util = EMFSharedResources.getInstance();
        return util.saveSoftwareDeploymentResource(softwareSystem);
    }

    /**
     * 
     * remove the softWareSystem which have relation about dataprovider and any softWareSystem which don't contain any
     * one.
     * 
     * @param dataProvider
     * @return
     * @deprecated
     */
    public boolean cleanSoftWareSystem(Connection dataProvider) {
        if (dataProvider == null) {
            return false;
        }
        Resource softwareSystemResource = EMFSharedResources.getInstance().getSoftwareDeploymentResource();
        if (WorkspaceUtils.getModelElementResource(softwareSystemResource.getURI()).exists()) {
            softwareSystemResource = EMFSharedResources.getInstance().reloadsoftwareDeploymentResource();
        }
        if (softwareSystemResource != null) {
            List<EObject> softwareSystems = softwareSystemResource.getContents();
            // Loop the software system from .softwaresystem.softwaredeployment file.
            List<EObject> needToRemoves = new ArrayList<EObject>();
            for (EObject softwareSystem : softwareSystems) {
                if (softwareSystem instanceof TdSoftwareSystem) {
                    List<ModelElement> ownedELements = ((TdSoftwareSystem) softwareSystem).getOwnedElement();
                    // Loop owned element.
                    for (ModelElement me : ownedELements) {
                        if (me == null || !(me instanceof Component)) {
                            continue;
                        }
                        List<DeployedComponent> deployComponents = ((Component) me).getDeployment();
                        if (deployComponents.size() > 0) {
                            if (ResourceHelper.areSame(deployComponents.get(0), dataProvider)) {
                                needToRemoves.add(softwareSystem);
                                break;
                            }
                        } else {
                            needToRemoves.add(softwareSystem);
                        }
                    }
                    if (ownedELements.size() <= 0) {
                        needToRemoves.add(softwareSystem);
                    }
                }
            }
            if (needToRemoves.size() > 0) {
                softwareSystems.removeAll(needToRemoves);
                EMFSharedResources.getInstance().saveSoftwareDeploymentResource();
                return true;
            }
        }
        return false;

    }

    public boolean cleanSoftWareSystem() {
        DatabaseConnection createDatabaseConnection = org.talend.core.model.metadata.builder.connection.ConnectionFactory.eINSTANCE
                .createDatabaseConnection();
        return cleanSoftWareSystem(createDatabaseConnection);
    }

}
