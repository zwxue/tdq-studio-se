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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.management.connection.DatabaseContentRetriever;
import org.talend.cwm.management.connection.JavaSqlFactory;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * @author scorreia
 * 
 * This class manages the software systems. TODO scorreia store SoftwareSystem in Namespace object.
 */
public final class SoftwareSystemManager {

    private static Logger log = Logger.getLogger(SoftwareSystemManager.class);

    private Map<TdDataProvider, TdSoftwareSystem> urlToSoftwareSystem = new HashMap<TdDataProvider, TdSoftwareSystem>();

    private static SoftwareSystemManager instance;

    private SoftwareSystemManager() {
    }

    public static SoftwareSystemManager getInstance() {
        if (instance == null) {
            instance = new SoftwareSystemManager();
        }
        return instance;
    }

    // FIXME handle delete of data provider
    /**
     * Method "getSoftwareSystem".
     * 
     * @param dataProvider
     * @return the software system that is referenced by the data provider.
     */
    public TdSoftwareSystem getSoftwareSystem(TdDataProvider dataProvider) {
        TdSoftwareSystem tdSoftwareSystem = urlToSoftwareSystem.get(dataProvider);
        if (tdSoftwareSystem == null) {
            try {
                // create it
                TypedReturnCode<Connection> trc = JavaSqlFactory.createConnection(dataProvider);
                if (trc.isOk()) {
                    Connection connection = trc.getObject();
                    tdSoftwareSystem = DatabaseContentRetriever.getSoftwareSystem(connection);
                    if (tdSoftwareSystem != null) {
                       if (DataProviderHelper.setSoftwareSystem(dataProvider, tdSoftwareSystem)) {
                            DqRepositoryViewService.saveSoftwareSystem(tdSoftwareSystem);
                        }
                    }
                    // store it in map
                    urlToSoftwareSystem.put(dataProvider, tdSoftwareSystem);
                }
            } catch (SQLException e) {
                log.error(e, e);
            }
        }
        // else not null
        return tdSoftwareSystem;
    }

}
