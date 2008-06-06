// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.db.connection.DBConnect;
import org.talend.cwm.db.connection.TalendCwmFactory;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.utils.sql.ConnectionUtils;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * @author scorreia
 * 
 * utility class for checking or getting connection.
 */
public final class ConnectionService {

    private static Logger log = Logger.getLogger(ConnectionService.class);

    private ConnectionService() {
    }

    /**
     * Method "checkConnection".
     * 
     * @param url the database url
     * @param driverClassName the driver class name to use for connection
     * @param props the properties of the connection
     * @return a return code with a message (not null when error)
     */
    public static ReturnCode checkConnection(String url, String driverClassName, Properties props) {
        ReturnCode rc = new ReturnCode();

        Connection connection = null;
        try {
            connection = ConnectionUtils.createConnection(url, driverClassName, props);
            rc = (ConnectionUtils.isValid(connection));
        } catch (SQLException e) {
            rc.setReturnCode(e.getMessage(), false);
        } catch (InstantiationException e) {
            rc.setReturnCode(e.getMessage(), false);
        } catch (IllegalAccessException e) {
            rc.setReturnCode(e.getMessage(), false);
        } catch (ClassNotFoundException e) {
            rc.setReturnCode("Driver not found: " + e.getMessage(), false);
        } finally {
            if (connection != null) {
                ReturnCode closed = ConnectionUtils.closeConnection(connection);
                if (!closed.isOk()) {
                    log.warn(closed.getMessage());
                }
            }
        }

        return rc;
    }

    /**
     * Method "createConnection" create a data provider from given connection parameters.
     * 
     * @param connectionParameters the connection parameters (with a JDBC url, a driver class name, and other properties
     * with user/password). The name, description and purpose are also set in the Data provider.
     * @return the Data provider.
     */
    public static TypedReturnCode<TdDataProvider> createConnection(DBConnectionParameter connectionParameters) {
        DBConnect connector = new DBConnect(connectionParameters);
        TypedReturnCode<TdDataProvider> rc = new TypedReturnCode<TdDataProvider>();
        try {
            TdDataProvider dataProvider = TalendCwmFactory.createDataProvider(connector);
            String connectionName = connectionParameters.getName();
            dataProvider.setName(connectionName);
            TaggedValueHelper.setDescription(connectionParameters.getDescription(), dataProvider);
            TaggedValueHelper.setPurpose(connectionParameters.getPurpose(), dataProvider);
            TaggedValueHelper.setDevStatus(dataProvider, DevelopmentStatus.get(connectionParameters.getStatus()));
            TaggedValueHelper.setAuthor(dataProvider, connectionParameters.getAuthor());
            rc.setObject(dataProvider);
            return rc;
        } catch (SQLException e) {
            String mess = "Failed to create a data provider for the given connection parameters: " + e.getMessage();
            log.warn(mess);
            rc.setReturnCode(e.getMessage(), false);
        } finally {
            connector.closeConnection();
            // MODRLI 2008-3-10
            // connector.saveInFiles();
            // MODSCA 2008-03-10 do not save here (keep creation and saving separate). Use DqRepositoryViewService if
            // possible
        }
        return rc;
    }
}
