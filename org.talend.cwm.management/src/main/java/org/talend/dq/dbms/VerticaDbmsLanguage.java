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
package org.talend.dq.dbms;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.talend.utils.ProductVersion;

/**
 * created by xqliu on Aug 7, 2013 Detailled comment
 * 
 */
public class VerticaDbmsLanguage extends DbmsLanguage {

    public VerticaDbmsLanguage() {
        super(DbmsLanguage.VERTICA);
    }

    public VerticaDbmsLanguage(String dbmsType) {
        super(dbmsType);
    }

    public VerticaDbmsLanguage(String dbmsType, ProductVersion dbVersion) {
        super(dbmsType, dbVersion);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#createStatement(java.sql.Connection)
     */
    @Override
    public Statement createStatement(Connection connection, int fetchSize) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setFetchSize(fetchSize);
        return statement;
    }
}
