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
package org.talend.dq.connection;

import java.sql.SQLException;
import java.util.LinkedList;

import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.talend.cwm.db.connection.DBConnect;
import org.talend.cwm.db.connection.TalendCwmFactory;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.management.connection.DatabaseConstant;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class DataProviderBuilder {

    static Logger log = Logger.getLogger(DataProviderBuilder.class);

    private boolean initialized = false;

    private DataProvider dataProvider;

    public ReturnCode initializeDataProvider(DBConnectionParameter parameter) {
        ReturnCode returnCode = new ReturnCode();
        String msg = "";
        if (initialized) {
            msg = "DataProvider already initialized. ";
            log.warn(msg);

            returnCode.setOk(false);
            returnCode.setMessage(msg);
            return returnCode;
        }

        // MOD xqliu 2009-12-17 feature 10238 add support for MDM
        if (parameter.getSqlTypeName().equals(SupportDBUrlType.MDM.getDBKey())) {
            dataProvider = TalendCwmFactory.createMdmTdDataProvider(parameter);
        }
        // MOD mzhao feature 10238 add support for xmldb(e.g eXist).
        else if (parameter.getDriverClassName().equals(DatabaseConstant.XML_EXIST_DRIVER_NAME)) {
            dataProvider = TalendCwmFactory.createEXistTdDataProvider(parameter);
        } else {
            DBConnect connector = new DBConnect(parameter);
            try {
                dataProvider = TalendCwmFactory.createDataProvider(connector);
            } catch (SQLException e) {
                msg = "Failed to create a data provider for the given connection parameters: " + e.getMessage();
                log.warn(msg, e);

                returnCode.setOk(false);
                returnCode.setMessage(msg);
            } catch (TalendException e) {
                // TODO Auto-generated catch block
                msg = "Failed to create a data provider for the given connection parameters: " + e.getMessage();
                log.warn(msg, e);

                returnCode.setOk(false);
                returnCode.setMessage(msg);
            } finally {
                connector.closeConnection();
            }
        }

        if (dataProvider != null) {
            String connectionName = parameter.getName();
            dataProvider.setName(connectionName);
            returnCode.setOk(true);
            return returnCode;
        } else {
            returnCode.setOk(false);
            // returnCode.setMessage("Can't create data provider!");
            return returnCode;
        }
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * DOC bzhou Comment method "buildDriverForSQLExploer".
     * 
     * @param name
     * @param driverClassName
     * @param url
     * @param jars
     * @return
     */
    public ManagedDriver buildDriverForSQLExploer(String name, String driverClassName, String url, LinkedList<String> jars) {
        ManagedDriver driver = new ManagedDriver(SQLExplorerPlugin.getDefault().getDriverModel().createUniqueId());

        driver.setName(name);
        driver.setJars(jars);
        driver.setDriverClassName(driverClassName);
        driver.setUrl(url);
        SQLExplorerPlugin.getDefault().getDriverModel().addDriver(driver);

        return driver;
    }
}
