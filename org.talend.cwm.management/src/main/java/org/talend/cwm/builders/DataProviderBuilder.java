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
package org.talend.cwm.builders;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.management.connection.DatabaseContentRetriever;
import org.talend.cwm.softwaredeployment.TdDataProvider;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class DataProviderBuilder extends CwmBuilder {

    private final TdDataProvider dataProvider;

    /**
     * DataProviderBuilder constructor.
     * 
     * @param conn the connection
     * @param driver the JDBC driver
     * @param databaseUrl the database connection string (must not be null)
     * @param driverProperties the properties passed to the driver (could be null)
     * @throws SQLException
     */
    public DataProviderBuilder(Connection conn, Driver driver, String databaseUrl, Properties driverProperties)
            throws SQLException {
        super(conn);
        this.dataProvider = DatabaseContentRetriever.getDataProvider(driver, databaseUrl, driverProperties);
        // MOD xqliu 2009-07-13 bug 7888
        String identifierQuote = ConnectionUtils.getConnectionMetadata(conn).getIdentifierQuoteString();
        // ~
        // MOD xqliu 2009-11-24 bug 7888
        DataProviderHelper.setIdentifierQuoteString(identifierQuote == null ? "" : identifierQuote, dataProvider);
        // ~
    }

    // TODO scorreia ctor with TdProviderConnection

    public TdDataProvider getDataProvider() {
        return this.dataProvider;
    }
}
