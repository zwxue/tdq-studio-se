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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.talend.cwm.management.api.DbmsLanguage;
import org.talend.cwm.management.api.DbmsLanguageFactory;

/**
 * @author scorreia
 * 
 * This class is used by children for converting a java.sql.connection into CWM classes.
 */
abstract class CwmBuilder {

    private static Logger log = Logger.getLogger(CwmBuilder.class);

    protected final Connection connection;

    protected DbmsLanguage dbms;

    /**
     * CwmBuilder constructor.
     * 
     * @param conn a connection
     */
    public CwmBuilder(Connection conn) {
        this.connection = conn;
        this.dbms = DbmsLanguageFactory.createDbmsLanguage(connection);
    }

    protected void print(String tag, String str) { // for tests only
        System.out.println(tag + " " + str);
    }

    protected DatabaseMetaData getConnectionMetadata(Connection conn) throws SQLException {
        assert conn != null : "Connection should not be null in " + getClass().getName() + " for connection "
                + getConnectionInformations(conn);
        return conn.getMetaData();
    }

    protected String getConnectionInformations(Connection conn) {
        return conn.toString(); // TODO scorreia give more user friendly informations.
    }

    /**
     * DOC scorreia Comment method "executeGetCommentStatement".
     * 
     * @param queryStmt
     * @return
     */
    protected String executeGetCommentStatement(String queryStmt) {
        String comment = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            statement.execute(queryStmt);

            // get the results
            resultSet = statement.getResultSet();
            if (resultSet != null) {
                while (resultSet.next()) {
                    comment = (String) resultSet.getObject(1);
                }
            }
        } catch (SQLException e) {
            // do nothing here
        } finally {
            // -- release resources
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    log.error(e, e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.error(e, e);
                }
            }
        }
        return comment;
    }
}
