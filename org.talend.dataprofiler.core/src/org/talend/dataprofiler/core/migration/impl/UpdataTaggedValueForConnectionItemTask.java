// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.impl;

import java.util.Date;
import java.util.List;

import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.MetadataFillFactory;
import org.talend.core.model.metadata.builder.ConvertionHelper;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class UpdataTaggedValueForConnectionItemTask extends AbstractWorksapceUpdateTask {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2013, 02, 20);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        List<IRepositoryViewObject> allConnectionObject = ProxyRepositoryFactory.getInstance().getAll(
                ERepositoryObjectType.METADATA_CONNECTIONS);

        for (IRepositoryViewObject object : allConnectionObject) {
            ConnectionItem connectionItem = (ConnectionItem) object.getProperty().getItem();
            Connection dataProvider = connectionItem.getConnection();
            if (dataProvider instanceof DatabaseConnection) {
                DatabaseConnection dbConn = (DatabaseConnection) dataProvider;
                if (PluginConstant.EMPTY_STRING.equals(TaggedValueHelper.getValueString(TaggedValueHelper.DB_PRODUCT_NAME,
                        dataProvider))) {
                    IMetadataConnection metaConnection = ConvertionHelper.convert(dbConn);
                    dbConn = (DatabaseConnection) MetadataFillFactory.getDBInstance().fillUIConnParams(metaConnection, dbConn);
                    if (dbConn != null) {
                        try {
                            ProxyRepositoryFactory.getInstance().save(connectionItem);
                        } catch (PersistenceException e) {
                            ExceptionHandler.process(e);
                        }
                    }
                }
            }
        }
        return true;
    }

}
