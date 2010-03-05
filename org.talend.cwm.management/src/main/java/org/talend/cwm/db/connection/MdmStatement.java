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
package org.talend.cwm.db.connection;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;


/**
 * DOC xqliu  class global comment. Detailled comment
 */
public class MdmStatement {

    private MdmConnection connection;

    private String[] resultSet;

    MdmStatement(MdmConnection connection) {
        this.connection = connection;
    }

    public boolean execute(String xmlSql) throws RemoteException, ServiceException {
        resultSet = connection.runQuery(xmlSql);
        return true;
    }

    public String[] getResultSet() {
        return resultSet;
    }
}
