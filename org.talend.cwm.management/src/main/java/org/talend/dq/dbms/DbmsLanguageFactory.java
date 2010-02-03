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
package org.talend.dq.dbms;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.SoftwareSystemManager;
import org.talend.cwm.management.connection.DatabaseConstant;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.utils.ProductVersion;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.foundation.softwaredeployment.SoftwareSystem;

/**
 * @author scorreia
 * 
 * Factory for the creation of DbmsLanguage objects.
 */
public final class DbmsLanguageFactory {

    private static Logger log = Logger.getLogger(DbmsLanguageFactory.class);

    private DbmsLanguageFactory() {
        // avoid instantiation
    }

    /**
     * Method "createDbmsLanguage".
     * 
     * @param dataManager a data manager used for initializing the correct language in the created DbmsLanguage
     * @return a new DbmsLanguage even if the data manager did not allow to get the correct language
     */
    public static DbmsLanguage createDbmsLanguage(DataManager dataManager) {
        DbmsLanguage dbmsLanguage = new DbmsLanguage();
        if (dataManager == null) {
            return dbmsLanguage;
        }
        TdDataProvider dataprovider = SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(dataManager);
        if (dataprovider == null) {
            return dbmsLanguage;
        }

        TdSoftwareSystem softwareSystem = SoftwareSystemManager.getInstance().getSoftwareSystem(dataprovider);
        boolean isMdm = ConnectionUtils.isMdmConnection(DataProviderHelper.getTdProviderConnection(dataprovider).getObject());
        if (softwareSystem != null || isMdm) {
            final String dbmsSubtype = isMdm ? DbmsLanguage.MDM : softwareSystem.getSubtype();
            if (log.isDebugEnabled()) {
                log.debug("Software system subtype (Database type): " + dbmsSubtype);
            }
            if (StringUtils.isNotBlank(dbmsSubtype)) {
                String version = isMdm ? DatabaseConstant.MDM_VERSION : softwareSystem.getVersion();
                dbmsLanguage = createDbmsLanguage(dbmsSubtype, version);
            }
        }
        String identifierQuoteString = DataProviderHelper.getIdentifierQuoteString(dataprovider);
        if (identifierQuoteString == null || identifierQuoteString.length() == 0) {
            // MOD scorreia 2009-11-24 check for null because in some cases (DB2 z/OS and TOP 3.2.2), the identifier
            // quote was stored as null.
            // given data provider has not stored the identifier quote (version 1.1.0 of TOP)
            // we must set it by hand
            identifierQuoteString = dbmsLanguage.getHardCodedQuoteIdentifier();
        }
        dbmsLanguage.setDbQuoteString(identifierQuoteString);
        return dbmsLanguage;
    }

    /**
     * Method "createDbmsLanguage".
     * 
     * @param dbmsSubtype
     * @return the appropriate DbmsLanguage
     */
    private static DbmsLanguage createDbmsLanguage(String dbmsSubtype, String databaseVersion) {
        ProductVersion dbVersion = ProductVersion.fromString(databaseVersion, true);
        if (isMySQL(dbmsSubtype)) {
            return new MySQLDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isOracle(dbmsSubtype)) {
            return new OracleDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isDB2(dbmsSubtype)) {
            return new DB2DbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isMSSQL(dbmsSubtype)) {
            return new MSSqlDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isPostgresql(dbmsSubtype)) {
            return new PostgresqlDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isSybaseASE(dbmsSubtype)) {
            return new SybaseASEDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isSQLite(dbmsSubtype)) {
            return new SQLiteDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isTeradata(dbmsSubtype)) {
            return new TeradataDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isIngres(dbmsSubtype)) {
            return new IngresDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isMdm(dbmsSubtype)) {
            return new MdmDbmsLanguage(dbmsSubtype, dbVersion);
        }
        // MOD zshen fixed bug 11005: SQL syntax error for all analysis on Informix databases in Talend Open Profiler
        if (isInfomix(dbmsSubtype)) {
            return new InfomixDbmsLanguage(dbmsSubtype, dbVersion);
        }// ~11005
        // TODO other supported databases here
        return new DbmsLanguage(dbmsSubtype, dbVersion);
    }

    public static DbmsLanguage createDbmsLanguage(String dataType) {
        SupportDBUrlType dbType = SupportDBUrlStore.getInstance().getDBUrlType(dataType);
        return createDbmsLanguage(dbType);
    }

    /**
     * DOC jet Comment method "getDbmsLanguage".
     * 
     * @param dbType
     * @return
     */
    public static DbmsLanguage createDbmsLanguage(SupportDBUrlType dbType) {

        DbmsLanguage result = null;

        if (dbType == null) {
            return new DbmsLanguage();
        }
        switch (dbType) {
        case DB2ZOSDEFAULTURL:
            result = new DB2DbmsLanguage();
            break;

        case ORACLEWITHSERVICENAMEDEFAULTURL:
        case ORACLEWITHSIDDEFAULTURL:
            result = new OracleDbmsLanguage();
            break;

        case SYBASEDEFAULTURL:
            result = new SybaseASEDbmsLanguage();
            break;

        case MSSQLDEFAULTURL:
            result = new MSSqlDbmsLanguage();
            break;

        case MYSQLDEFAULTURL:
        default:
            result = new DbmsLanguage();
        }
        return result;
    }

    /**
     * Method "createDbmsLanguage".
     * 
     * @param connection a connection (must be open)
     * @return the appropriate DbmsLanguage or a default one if something failed with the connection.
     */
    public static DbmsLanguage createDbmsLanguage(Connection connection) {
        assert connection != null;
        // MOD xqliu 2009-07-13 bug 7888
        String databaseProductName = null;
        try {
            databaseProductName = ConnectionUtils.getConnectionMetadata(connection).getDatabaseProductName();
            databaseProductName = databaseProductName == null ? "" : databaseProductName; //$NON-NLS-1$
            String databaseProductVersion = null;
            try {
                databaseProductVersion = ConnectionUtils.getConnectionMetadata(connection).getDatabaseProductVersion();
                databaseProductVersion = databaseProductVersion == null ? "0" : databaseProductVersion;
            } catch (Exception e) {
                log.warn("Exception when retrieving database product version of " + databaseProductName, e);
            }
            DbmsLanguage dbmsLanguage = createDbmsLanguage(databaseProductName, databaseProductVersion);
            dbmsLanguage.setDbQuoteString(ConnectionUtils.getConnectionMetadata(connection).getIdentifierQuoteString());
            return dbmsLanguage;
        } catch (SQLException e) {
            log.warn("Exception when retrieving database informations:" + e + ". Creating a default DbmsLanguage.", e);
            return new DbmsLanguage();
        }
    }

    private static boolean isMySQL(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.MYSQL, dbms);
    }

    public static boolean isOracle(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.ORACLE, dbms);
    }

    private static boolean isPostgresql(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.POSTGRESQL, dbms);
    }

    private static boolean isMSSQL(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.MSSQL, dbms);
    }

    private static boolean isDB2(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.DB2, dbms);
    }

    private static boolean isSybaseASE(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.SYBASE_ASE, dbms);
    }

    private static boolean isSQLite(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.SQLITE3, dbms);
    }

    private static boolean isTeradata(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.TERADATA, dbms);
    }

    private static boolean isIngres(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.INGRES, dbms);
    }

    private static boolean isMdm(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.MDM, dbms);
    }

    // MOD zshen 11005: SQL syntax error for all analysis on Informix databases in Talend Open Profiler
    public static boolean isInfomix(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.INFOMIX, dbms);
    }

    // ~11005

    static boolean compareDbmsLanguage(String lang1, String lang2) {
        if (lang1 == null || lang2 == null) {
            return false;
        }
        // MOD 2008-08-04 scorreia: for DB2 database, dbName can be "DB2/NT" or "DB2/6000" or "DB2"...
        if (lang1.startsWith(DbmsLanguage.DB2)) {
            return StringUtils.upperCase(lang1).startsWith(StringUtils.upperCase(lang2))
                    || StringUtils.upperCase(lang2).startsWith(StringUtils.upperCase(lang1));
        } else
        // MOD 2010-01-26 zshen: for informix database, dbName can be "informix" or "informix Dynamic Server"
        if (lang1.startsWith(DbmsLanguage.INFOMIX)) {
            return StringUtils.upperCase(lang1).startsWith(StringUtils.upperCase(lang2))
                    || StringUtils.upperCase(lang2).startsWith(StringUtils.upperCase(lang1));
        }
        return StringUtils.equalsIgnoreCase(lang1, lang2);
    }

    /**
     * Method "createDbmsLanguage".
     * 
     * @param analysis
     * @return the dbms language associated to the connection of the given analysis or a default one.
     */
    public static DbmsLanguage createDbmsLanguage(Analysis analysis) {
        final AnalysisContext context = analysis.getContext();
        if (context != null) {
            final DataManager dm = context.getConnection();
            if (dm != null) {
                return createDbmsLanguage(dm);
            }
        }
        return new DbmsLanguage();
    }

    public static DbmsLanguage createDbmsLanguage(SoftwareSystem softwareSystem) {
        if (softwareSystem != null) {
            return createDbmsLanguage(softwareSystem.getName(), softwareSystem.getVersion());
        }

        return new DbmsLanguage();
    }
}
