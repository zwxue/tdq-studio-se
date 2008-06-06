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
package org.talend.cwm.builders;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author scorreia
 * 
 * This class is used by children for converting a java.sql.connection into CWM classes.
 */
abstract class CwmBuilder {

    protected final Connection connection;

    /**
     * CwmBuilder constructor.
     * 
     * @param conn a connection
     */
    public CwmBuilder(Connection conn) {
        this.connection = conn;
    }

    protected void print(String tag, String str) { // for tests only
        System.out.println(tag + " " + str);
    }

    protected DatabaseMetaData getConnectionMetadata(Connection connection) throws SQLException {
        assert connection != null : "Connection should not be null in " + getClass().getName() + " for connection "
                + getConnectionInformations(connection);
        return connection.getMetaData();
    }

    protected String getConnectionInformations(Connection connection) {
        return connection.toString(); // TODO scorreia give more user friendly informations.
    }

}
