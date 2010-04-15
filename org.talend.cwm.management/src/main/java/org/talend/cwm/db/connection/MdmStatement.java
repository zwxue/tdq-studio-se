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

import org.talend.cwm.xml.TdXMLDocument;

/**
 * DOC xqliu  class global comment. Detailled comment
 */
public class MdmStatement {

    private MdmConnection connection;

    private String[] resultSet;

    MdmStatement(MdmConnection connection) {
        this.connection = connection;
    }

    /**
     * DOC xqliu Comment method "execute". ADD xqliu 2010-04-15 bug 12568
     * 
     * @param xmlDocument
     * @param xmlSql
     * @return
     * @throws RemoteException
     * @throws ServiceException
     */
    public boolean execute(TdXMLDocument xmlDocument, String xmlSql) throws RemoteException, ServiceException {
        resultSet = connection.runQuery(xmlDocument, xmlSql);
        return true;
    }

    public boolean execute(String xmlSql) throws RemoteException, ServiceException {
        // MOD xqliu 2010-04-15 bug 12568
        return execute(null, xmlSql);
    }

    public String[] getResultSet() {
        return resultSet;
    }
}
