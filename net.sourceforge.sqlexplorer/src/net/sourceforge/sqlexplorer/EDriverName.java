// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
                    "-6",
                    "lib/mysql-connector-java-5.1.3-bin.jar"),
    HSQLDEFAULTURL("HSql", //$NON-NLS-1$
                   "org.hsqldb.jdbcDriver", //$NON-NLS-1$
                   "-18",
                   "lib/hsqldb.jar"),
    ORACLEDEFAULTURL("Oracle", //$NON-NLS-1$
                     // MOD scorreia 2008-08-22: oracle.jdbc.driver package is not
                     // supported anymore: replaced
                     "oracle.jdbc.OracleDriver", //$NON-NLS-1$
                     "-4",
                     "lib/ojdbc14.jar"),
    MSSQLDEFAULTURL("MSSQL", //$NON-NLS-1$
                    "net.sourceforge.jtds.jdbc.Driver", //$NON-NLS-1$
                    "-11", "lib/jtds-1.2.jar"), //$NON-NLS-1$
    MSSQL2008URL("MSSQL2008", //$NON-NLS-1$
                 "com.microsoft.sqlserver.jdbc.SQLServerDriver", //$NON-NLS-1$
                 "-52", isAboveJDK15() ? "lib/sqljdbc4.jar" : "lib/sqljdbc.jar"), //$NON-NLS-1$
    DB2DEFAULTURL("DB2", //$NON-NLS-1$
                  "com.ibm.db2.jcc.DB2Driver", //$NON-NLS-1$
                  "-24",
                  "lib/db2jcc_license_cu.jar",
                  "lib/db2jcc_license_cisuz.jar",
                  "lib/db2jcc.jar"),
    POSTGRESQLEFAULTURL("PostgreSQL", //$NON-NLS-1$
                        "org.postgresql.Driver", //$NON-NLS-1$
                        "-7",
                        "lib/postgresql-8.1-405.jdbc3.jar"),
    INTERBASEDEFAULTURL("Interbase", //$NON-NLS-1$
                        "interbase.interclient.Driver", //$NON-NLS-1$
                        "-3",
                        "lib/interclient.jar"),
    SYBASEDEFAULTURL("Sybase", //$NON-NLS-1$
                     "com.sybase.jdbc3.jdbc.SybDriver", //$NON-NLS-1$
                     "-9",
                     "lib/jconn3.jar"),
    INFORMIXDEFAULTURL("Informix", //$NON-NLS-1$
                       "com.informix.jdbc.IfxDriver", //$NON-NLS-1$
                       "-26",
                       "lib/ifxjdbc.jar"), //$NON-NLS-1$ 
    FIREBIRDDEFAULTURL("FireBird", //$NON-NLS-1$
                       "org.firebirdsql.jdbc.FBDriver", //$NON-NLS-1$
                       "-25",
                       "lib/jaybird-2.1.1.jar"),
    TERADATADEFAULTURL("Teradata",
    // MOD klliu 2010-06-04 bug 12819: upgrade jdbc driver class used in sql explorer
                       "com.teradata.jdbc.TeraDriver",
                       "-50",
                       "lib/terajdbc4.jar",
                       "lib/tdgssconfig.jar"),
    SQLITE3DEFAULTURL("SQLite", "org.sqlite.JDBC", "-30", "lib/sqlitejdbc_v037_nested.jar"),
    AS400DEFAULTURL("AS400", "com.ibm.as400.access.AS400JDBCDriver", "-51", "lib/jt400_V5R3.jar"),
    // MOD klliu bug 14791 add ingres database url and modify default_driver.xml
    INGRESDEFAULTURL("Ingres", "com.ingres.jdbc.IngresDriver", "-88", "lib/iijdbc.jar"),
    NETEZZADEFAULTURL("Netezza", "org.netezza.Driver", "-66", "lib/nzjdbc.jar") //$NON-NLS-1$
    ;

    private final String dbKey;

    private final String dbDriver;

    private String sqlEid;

    private String[] jars;

    // MOD gdbu 2011-4-20 bug : 18975
    private static Map<String, ArrayList<String>> special_database = new HashMap<String, ArrayList<String>>();
    static {
        ArrayList spDB = new ArrayList();
        spDB.add("com.mysql.jdbc.Driver");
        special_database.put("org.gjt.mm.mysql.Driver", spDB);

        //MOD gdbu 2011-6-9 bug : 21854
        ArrayList oracleJDBCDB = new ArrayList();
        oracleJDBCDB.add("oracle.jdbc.driver.OracleDriver");
        special_database.put("oracle.jdbc.OracleDriver", oracleJDBCDB);
        //~ 21854
        
        // According to the above example we can continue to fill special driverclass name...
    }
    // ~18975

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
        String plugins = "org.talend.libraries";
        switch (this) {
        case DB2DEFAULTURL:
            plugins = "org.talend.libraries.jdbc.db2";
            break;
        case MSSQLDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.jtds";
            break;
        case MSSQL2008URL:
            plugins = "org.talend.libraries.jdbc.mssql";
            break;
        case MYSQLDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.mysql";
            break;
        case ORACLEDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.oracle";
            break;
        case POSTGRESQLEFAULTURL:
            plugins = "org.talend.libraries.jdbc.postgresql";
            break;
        case SYBASEDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.sybase";
            break;
        case HSQLDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.hsql";
            break;
        case TERADATADEFAULTURL:
            plugins = "org.talend.libraries.jdbc.teradata";
            break;
        case INFORMIXDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.informix";
            break;
        case SQLITE3DEFAULTURL:
            plugins = "org.talend.libraries.jdbc.sqlite3";
            break;
        case AS400DEFAULTURL:
            plugins = "org.talend.libraries.jdbc.as400";
            break;
        case INGRESDEFAULTURL:
            plugins = "org.talend.libraries.jdbc.ingres";
            break;
        case NETEZZADEFAULTURL:
            plugins = "org.talend.libraries.jdbc.netezza";
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

    public static Boolean isAboveJDK15() {
        Float curVersion = Float.parseFloat(StringUtils.substringBeforeLast(System.getProperty("java.version"), "."));
        return NumberUtils.compare(curVersion, 1.5) > 0;
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

        // MOD gdbu 2011-4-20 bug : 18975
        String specID = getDriFromSpecialDB(driver);
        if (!specID.equals("")) {
            return specID;
        }
        // ~18975

        return id;
    }

    /**
     * MOD gdbu 2011-4-20 bug : 18975
     * 
     * DOC gdbu Comment method "getDriFromSpecialDB".
     * 
     * If the above method : getId() can not return required driverclass, we can return by this method requires the
     * driverclass, but only if we have to fill specialdatabase instance
     * 
     * @param driver
     * @return driverID
     */
    private static String getDriFromSpecialDB(String driver) {
        if (driver != null) {
            Set<String> kss = special_database.keySet();
            for (String ks : kss) {
                ArrayList<String> specDb = special_database.get(ks);
                for (int i = 0; i < specDb.size(); i++) {
                    if (specDb.get(i).trim().equals(driver.trim())) {
                        return getId(ks);
                    }
                }
            }
        }
        return "";
    }

}
