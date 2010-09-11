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

import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.utils.sugars.ReturnCode;

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
        return DqRepositoryViewService.saveOpenDataProvider(connItem, false);
    }

}
