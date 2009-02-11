// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package net.sourceforge.sqlexplorer;

import java.net.URL;
import java.util.LinkedList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public enum EDriverName {
    ODBCDEFAULTURL("Generic ODBC", //$NON-NLS-1$
                   "sun.jdbc.odbc.JdbcOdbcDriver", //$NON-NLS-1$
                   "-1"), //$NON-NLS-1$ 
    MYSQLDEFAULTURL("Mysql", //$NON-NLS-1$
                    "org.gjt.mm.mysql.Driver", //$NON-NLS-1$
                    "-6", //$NON-NLS-1$
                    "lib/mysql-connector-java-5.1.0-bin.jar"), //$NON-NLS-1$
    HSQLDEFAULTURL("HSql", //$NON-NLS-1$
                   "org.hsqldb.jdbcDriver", //$NON-NLS-1$
                   "-18", //$NON-NLS-1$
                   "lib/hsqldb.jar"), //$NON-NLS-1$
    ORACLEDEFAULTURL("Oracle", //$NON-NLS-1$
                     // MOD scorreia 2008-08-22: oracle.jdbc.driver package is not supported anymore: replaced
                     "oracle.jdbc.OracleDriver", //$NON-NLS-1$
                     "-4", //$NON-NLS-1$
                     "lib/ojdbc14.jar"), //$NON-NLS-1$
    MSSQLDEFAULTURL("MSSQL", //$NON-NLS-1$
                    "net.sourceforge.jtds.jdbc.Driver", //$NON-NLS-1$
                    "-11", "lib/jtds-1.2.jar"), //$NON-NLS-1$ //$NON-NLS-2$
    DB2DEFAULTURL("DB2", //$NON-NLS-1$
                  "com.ibm.db2.jcc.DB2Driver", //$NON-NLS-1$
                  "-24", //$NON-NLS-1$
                  "lib/db2jcc_license_cu.jar", //$NON-NLS-1$
                  "lib/db2jcc_license_cisuz.jar", //$NON-NLS-1$
                  "lib/db2jcc.jar"), //$NON-NLS-1$
    POSTGRESQLEFAULTURL("PostgreSQL", //$NON-NLS-1$
                        "org.postgresql.Driver", //$NON-NLS-1$
                        "-7", //$NON-NLS-1$
                        "lib/postgresql-8.1-405.jdbc3.jar"), //$NON-NLS-1$
    INTERBASEDEFAULTURL("Interbase", //$NON-NLS-1$
                        "interbase.interclient.Driver", //$NON-NLS-1$
                        "-3", //$NON-NLS-1$
                        "lib/interclient.jar"), //$NON-NLS-1$
    SYBASEDEFAULTURL("Sybase", //$NON-NLS-1$
                     "com.sybase.jdbc3.jdbc.SybDriver", //$NON-NLS-1$
                     "-9", //$NON-NLS-1$
                     "lib/jconn3.jar"), //$NON-NLS-1$
    INFORMIXDEFAULTURL("Informix", //$NON-NLS-1$
                       "com.informix.jdbc.IfxDriver", //$NON-NLS-1$
                       null,
                       "lib/ifxjdbc.jar"), //$NON-NLS-1$ 
    FIREBIRDDEFAULTURL("FireBird", //$NON-NLS-1$
                       "org.firebirdsql.jdbc.FBDriver", //$NON-NLS-1$
                       "-25", //$NON-NLS-1$
                       "lib/jaybird-2.1.1.jar"), //$NON-NLS-1$
    TERADATADEFAULTURL(
                       "Teradata", //$NON-NLS-1$
                       "com.ncr.teradata.TeraDriver", //$NON-NLS-1$
                       "-50", //$NON-NLS-1$
                       "lib/terajdbc4.jar", //$NON-NLS-1$
                       "lib/tdgssconfig.jar", //$NON-NLS-1$
                       "lib/tdgssjava.jar"), //$NON-NLS-1$
    SQLITE3DEFAULTURL("SQLite3", "org.sqlite.JDBC", "-30", "lib/sqlitejdbc_v037_nested.jar"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private final String dbKey;

    private final String dbDriver;

    private String sqlEid;

    private String[] jars;

    EDriverName(String dbKey, String dbDriver, String sqlEid, String... jars) {
        this.dbKey = dbKey;
        this.dbDriver = dbDriver;
        this.sqlEid = sqlEid;
        this.jars = jars;
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

    /**
     * Getter for sqlEid.
     * 
     * @return the sqlEid
     */
    public String getSqlEid() {
        return this.sqlEid;
    }

    /**
     * DOC qzhang Comment method "getJars".
     * 
     * @return
     */
    public LinkedList<String> getJars() {
        LinkedList<String> linkedList = new LinkedList<String>();
        String plugins = "org.talend.libraries"; //$NON-NLS-1$
        switch (this) {
        case DB2DEFAULTURL:
            plugins = "org.talend.libraries.jdbc.db2"; //$NON-NLS-1$
            break;
        case MSSQLDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.jtds"; //$NON-NLS-1$
            break;
        case MYSQLDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.mysql"; //$NON-NLS-1$
            break;
        case ORACLEDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.oracle"; //$NON-NLS-1$
            break;
        case POSTGRESQLEFAULTURL:
            plugins = "org.talend.libraries.jdbc.postgresql"; //$NON-NLS-1$
            break;
        case SYBASEDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.sybase"; //$NON-NLS-1$
            break;
        case HSQLDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.hsql"; //$NON-NLS-1$
            break;
        case TERADATADEFAULTURL:
            plugins = "org.talend.libraries.jdbc.teradata"; //$NON-NLS-1$
            break;
        case INFORMIXDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.informix"; //$NON-NLS-1$
            break;
        case SQLITE3DEFAULTURL:
            plugins = "org.talend.libraries.jdbc.sqlite3"; //$NON-NLS-1$
            break;
        default:
            return linkedList;
        }
        Bundle bundle = Platform.getBundle(plugins);
        if (bundle != null) {
            try {
                String requires = (String) bundle.getHeaders().get(Constants.BUNDLE_CLASSPATH);
                ManifestElement[] elements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, requires);
                URL hsqldbJar = null;
                if (jars != null) {
                    for (String jar : jars) {
                        String value = elements[0].getValue();
                        for (ManifestElement element : elements) {
                            if (jar.equals(element.getValue())) {
                                value = element.getValue();
                                hsqldbJar = FileLocator.toFileURL(bundle.getEntry(value));
                                linkedList.add(hsqldbJar.getPath());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return linkedList;
    }

    /**
     * DOC qzhang Comment method "getId".
     * 
     * @param string
     */
    public static String getId(String driver) {
        String id = ODBCDEFAULTURL.sqlEid;
        for (EDriverName driverName : values()) {
            if (driverName.dbDriver.equalsIgnoreCase(driver)) {
                return driverName.sqlEid;
            }
        }
        return id;
    }
}
