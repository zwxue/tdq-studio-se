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
package org.talend.dq.helper;

import java.util.Collection;

import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC mzhao This class helps to store the needed save connection object.
 * 
 * @param <T> MDM connection, DB connection, File connection etc.
 */
public abstract class TDQConnectionReposViewObjDelegator<T extends Connection> extends
        AbstractDQRepositoryViewObjectDelegator<Connection> {

    protected TDQConnectionReposViewObjDelegator() {
    }

    @Override
    protected ReturnCode save(Item item) {
        ConnectionItem connItem = (ConnectionItem) item;
        return saveOpenDataProvider(connItem, false);
    }

    @Override
    protected void setActiveElement(IRepositoryViewObject viewObj, Connection element) {
        ConnectionItem connItem = (ConnectionItem) viewObj.getProperty().getItem();
        connItem.setConnection(element);
        // Set dependency additionally.
        // connItem.getConnection().getSupplierDependency().clear();
        // connItem.getConnection().getSupplierDependency().addAll(element.getSupplierDependency());
    }

    /**
     * Method "saveOpenDataProvider" saves a Data provider which has already a resource (has already been saved once).
     * 
     * @param dataProvider
     * @param addPackage decide whether need to add the Package(catalog/schema) element to dataprovider.
     * @return
     */
    public static ReturnCode saveOpenDataProvider(ConnectionItem connItem, boolean addPackage) {
        Connection conn = connItem.getConnection();
        assert conn != null;
        if (addPackage) {
            // MOD zshen bug 10633: Reload Database List can't display new
            // Schema in DQ Repository view(Oracle Database)
            Collection<? extends Package> catalogsorSchemas = ConnectionHelper.getCatalogs(conn);
            if (catalogsorSchemas.size() == 0) {
                catalogsorSchemas = ConnectionHelper.getSchema(conn);
            }
            conn.getDataPackage().addAll(catalogsorSchemas);
        }

        ReturnCode rc = new ReturnCode();
        ElementWriterFactory.getInstance().createDataProviderWriter().save(connItem);
        return rc;
    }
}
