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
package org.talend.cwm.management.api;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.management.connection.DatabaseContentRetriever;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.utils.sugars.TypedReturnCode;

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
                    softwareSystem = DatabaseContentRetriever.getSoftwareSystem(connection);
                    if (softwareSystem != null && softwareSystem.eResource() != null) { // store it
                        if (ConnectionHelper.setSoftwareSystem(dataProvider, softwareSystem)) {
                            saveSoftwareSystem(softwareSystem);
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
     * DOC scorreia Comment method "saveSoftwareSystem".
     * 
     * @param util
     * @param softwareSystem
     */
    public static boolean saveSoftwareSystem(TdSoftwareSystem softwareSystem) {
        EMFSharedResources util = EMFSharedResources.getInstance();
        return util.saveSoftwareDeploymentResource(softwareSystem);
    }

}
