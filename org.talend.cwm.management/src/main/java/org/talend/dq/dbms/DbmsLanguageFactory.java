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
package org.talend.dq.dbms;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.SoftwareSystemManager;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

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
        if (softwareSystem != null) {
            final String dbmsSubtype = softwareSystem.getSubtype();
            if (log.isInfoEnabled()) {
                log.info("Software system subtype (Database type): " + dbmsSubtype);
            }
            if (StringUtils.isNotBlank(dbmsSubtype)) {
                dbmsLanguage = createDbmsLanguage(dbmsSubtype);
            }
        }
        String identifierQuoteString = DataProviderHelper.getIdentifierQuoteString(dataprovider);
        if (identifierQuoteString.length() == 0) {
            // given data provider has not stored the identifier quote (version 1.1.0 of TOP)
            // we must set it by hand
            identifierQuoteString = dbmsLanguage.getSupportedQuoteIdentifier();
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
    private static DbmsLanguage createDbmsLanguage(String dbmsSubtype) {
        if (isMySQL(dbmsSubtype)) {
            return new MySQLDbmsLanguage();
        }
        if (isOracle(dbmsSubtype)) {
            return new OracleDbmsLanguage();
        }
        if (isDB2(dbmsSubtype)) {
            return new DB2DbmsLanguage();
        }
        if (isMSSQL(dbmsSubtype)) {
            return new MSSqlDbmsLanguage();
        }
        if (isPostgresql(dbmsSubtype)) {
            return new PostgresqlDbmsLanguage();
        }
        if (isSybaseASE(dbmsSubtype)) {
            return new SybaseASEDbmsLanguage();
        }
        return new DbmsLanguage();
    }

    /**
     * Method "createDbmsLanguage".
     * 
     * @param connection a connection (must be open)
     * @return the appropriate DbmsLanguage or a default one if something failed with the connection.
     */
    public static DbmsLanguage createDbmsLanguage(Connection connection) {
        assert connection != null;
        String databaseProductName;
        try {
            databaseProductName = connection.getMetaData().getDatabaseProductName();
            DbmsLanguage dbmsLanguage = createDbmsLanguage(databaseProductName);
            dbmsLanguage.setDbQuoteString(connection.getMetaData().getIdentifierQuoteString());
            return dbmsLanguage;
        } catch (SQLException e) {
            log.warn("Exception when retrieving database informations:" + e + ". Creating a default DbmsLanguage.", e);
            return new DbmsLanguage();
        }
    }
    
    private static boolean isMySQL(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.MYSQL, dbms);
    }
    
    private static boolean isOracle(String dbms) {
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


    // TODO add other types
    
    static boolean compareDbmsLanguage(String lang1, String lang2) {
        if (lang1 == null || lang2 == null) {
            return false;
        }
        // MOD 2008-08-04 scorreia: for DB2 database, dbName can be "DB2/NT" or "DB2/6000" or "DB2"...
        if (lang1.startsWith(DbmsLanguage.DB2)) {
            return StringUtils.upperCase(lang1).startsWith(StringUtils.upperCase(lang2))
                    || StringUtils.upperCase(lang2).startsWith(StringUtils.upperCase(lang1));
        }
        return StringUtils.equalsIgnoreCase(lang1, lang2);
    }
}
