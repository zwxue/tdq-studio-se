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
package org.talend.cwm.dburl;

/**
 * DOC rli class global comment. Detailled comment <br/>
 * 
 */
public enum SupportDBUrlType {
    ODBCDEFAULTURL("Generic ODBC", //$NON-NLS-1$
                   null,
                   null,
                   null,
                   null,
                   "sun.jdbc.odbc.JdbcOdbcDriver", //$NON-NLS-1$
                   "datasourceName", "ODBC"), //$NON-NLS-1$ //$NON-NLS-2$
    MYSQLDEFAULTURL("MySQL", //$NON-NLS-1$
                    "localhost", //$NON-NLS-1$
                    "3306", //$NON-NLS-1$
                    "dbname", //$NON-NLS-1$
                    "?", //$NON-NLS-1$
                    "org.gjt.mm.mysql.Driver", //$NON-NLS-1$
                    null,
                    "MySQL"), //$NON-NLS-1$
    ORACLEWITHSIDDEFAULTURL("Oracle with SID", //$NON-NLS-1$
                            "localhost", //$NON-NLS-1$
                            "1521", //$NON-NLS-1$
                            "dbname", //$NON-NLS-1$
                            null,
                            // MOD scorreia 2008-08-22: oracle.jdbc.driver package is not supported anymore: replaced
                            "oracle.jdbc.OracleDriver", //$NON-NLS-1$ 
                            null,
                            "Oracle"), //$NON-NLS-1$
    ORACLEWITHSERVICENAMEDEFAULTURL("Oracle with Service Name", //$NON-NLS-1$
                                    "localhost", //$NON-NLS-1$
                                    "1521", //$NON-NLS-1$
                                    "dbname", //$NON-NLS-1$
                                    null, // replaced
                                    "oracle.jdbc.OracleDriver", //$NON-NLS-1$ 
                                    null,
                                    "Oracle"), //$NON-NLS-1$
    MSSQLDEFAULTURL("SQL Server", //$NON-NLS-1$
                    "localhost", //$NON-NLS-1$
                    "1433", //$NON-NLS-1$
                    "dbname", //$NON-NLS-1$
                    ";", //$NON-NLS-1$
                    "net.sourceforge.jtds.jdbc.Driver", //$NON-NLS-1$
                    null,
                    "SQL Server"), //$NON-NLS-1$
    DB2DEFAULTURL("DB2", //$NON-NLS-1$
                  "localhost", //$NON-NLS-1$
                  "50000", //$NON-NLS-1$
                  "dbname", //$NON-NLS-1$
                  null,
                  "com.ibm.db2.jcc.DB2Driver", //$NON-NLS-1$
                  null,
                  "DB2"), //$NON-NLS-1$
    POSTGRESQLEFAULTURL("PostgreSQL", //$NON-NLS-1$
                        "localhost", //$NON-NLS-1$
                        "5432", //$NON-NLS-1$
                        "dbname", //$NON-NLS-1$
                        null,
                        "org.postgresql.Driver", //$NON-NLS-1$
                        null,
                        "PostgreSQL"), //$NON-NLS-1$
    INTERBASEDEFAULTURL("Interbase", //$NON-NLS-1$
                        "localhost", //$NON-NLS-1$
                        "3060", //$NON-NLS-1$
                        "dbname", //$NON-NLS-1$
                        null,
                        "interbase.interclient.Driver", //$NON-NLS-1$
                        null,
                        "Iterbase"), //$NON-NLS-1$
    SYBASEDEFAULTURL("Sybase", //$NON-NLS-1$
                     "localhost", //$NON-NLS-1$
                     "5007", //$NON-NLS-1$
                     "dbname", //$NON-NLS-1$
                     null,
                     "com.sybase.jdbc3.jdbc.SybDriver", //$NON-NLS-1$
                     null,
                     "Sybase"), //$NON-NLS-1$
    INFORMIXDEFAULTURL("Informix", //$NON-NLS-1$
                       "localhost", //$NON-NLS-1$
                       "1533", //$NON-NLS-1$
                       "dbname", //$NON-NLS-1$
                       ";",
                       "com.informix.jdbc.IfxDriver", //$NON-NLS-1$
                       "myserver", "Informix"), //$NON-NLS-1$ //$NON-NLS-2$
    FIREBIRDDEFAULTURL("FireBird", //$NON-NLS-1$
                       "localhost", //$NON-NLS-1$
                       null, //$NON-NLS-1$
                       "dbname", //$NON-NLS-1$
                       null,
                       "org.firebirdsql.jdbc.FBDriver", //$NON-NLS-1$
                       null,
                       "FireBird"); //$NON-NLS-1$

    private final String dbKey;

    private final String hostName;

    private final String port;

    private String dbName;

    private final String paramSeprator;

    private final String dbDriver;

    private final String dataSource;

    private final String language;

    SupportDBUrlType(String dbKey, String hostName, String port, String dbName, String paramSeprator, String dbDriver,
            String datasource, String language) {
        this.dbKey = dbKey;
        this.hostName = hostName;
        this.port = port;
        this.dbName = dbName;
        this.paramSeprator = paramSeprator;
        this.dbDriver = dbDriver;
        this.dataSource = datasource;
        this.language = language;
    }

    public String getHostName() {
        return hostName;
    }

    public String getPort() {
        return port;
    }

    public void setDBName(String dbName) {
        this.dbName = dbName;
    }

    public String getDBName() {
        return dbName;
    }

    public String getDBKey() {
        return dbKey;
    }

    /**
     * Getter for dbDriver.
     * 
     * @return the dbDriver
     */
    public String getDbDriver() {
        return dbDriver;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getParamSeprator() {
        return paramSeprator;
    }

    public String getLanguage() {
        return language;
    }
}
