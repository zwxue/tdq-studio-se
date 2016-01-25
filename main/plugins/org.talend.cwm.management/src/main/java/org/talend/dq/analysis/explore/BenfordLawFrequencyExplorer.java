// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis.explore;

import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;

/**
 * return the where clause for benford law indicator, but for different DB type, the clause is different.
 */
public class BenfordLawFrequencyExplorer extends FrequencyStatisticsExplorer {

    @Override
    protected String getInstantiatedClause() {
        if (entity.getKey().toString().equals("invalid")) {//$NON-NLS-1$ 
            return getInvalidClause();
        }
        Object value = "'" + entity.getKey() + "%'"; //$NON-NLS-1$ //$NON-NLS-2$

        String clause = entity.isLabelNull() ? getColumnName() + dbmsLanguage.isNull() : getColumnName() + dbmsLanguage.like()
                + value;

        if (isInformix()) {
            return " SUBSTR( " + getColumnName() + " ,0,1)" + dbmsLanguage.like() + value;
        }
        return clause;
    }

    /**
     * when the drill down is : invalid, should find all rows which not start with 1~9,and 0. this include: null, and
     * not digitals.
     * 
     * @return
     */
    private String getInvalidClause() {
        String value = " not REGEXP '^[0-9]'"; //$NON-NLS-1$
        if (isSybase()) {
            return columnName
                    + " is null or left(convert(char(15)," + this.columnName + "),1) not " + dbmsLanguage.like() + "'%[0-9]%'";//$NON-NLS-1$ //$NON-NLS-2$
        } else if (isPostGreSQL()) {
            return columnName + " is null or SUBSTRING(" + columnName + ", 1,1)  ~ '[^0-9]'";//$NON-NLS-1$ //$NON-NLS-2$
        } else if (isTeradata()) {
            return columnName
                    + " is null or cast(" + columnName + " as char(1)) not in ('0','1','2','3','4','5','6','7','8','9')";//$NON-NLS-1$ //$NON-NLS-2$
        } else if (isOracle()) {
            return columnName + " is null or " + "  regexp_like(SUBSTR(" + columnName + ",0,1),'^[^[:digit:]]+$')";//$NON-NLS-1$ //$NON-NLS-2$
        } else if (isDB2()) {
            return columnName + " is null or LEFT(" + columnName + ",1)" + " not in ('0','1','2','3','4','5','6','7','8','9')";//$NON-NLS-1$ //$NON-NLS-2$
        } else if (isSqlServer()) {
            return columnName + " is null or LEFT(" + columnName + ",1) not" + dbmsLanguage.like() + "'%[0-9]%'";//$NON-NLS-1$ //$NON-NLS-2$
        } else if (isInformix()) {
            return columnName + " is null or SUBSTR(" + columnName + ",0,1) not in ('0','1','2','3','4','5','6','7','8','9')";//$NON-NLS-1$ //$NON-NLS-2$
        } else if (isNetezza()) {
            return columnName + " is null or Substring(" + columnName + ",1,1) not in ('0','1','2','3','4','5','6','7','8','9')";//$NON-NLS-1$ //$NON-NLS-2$
        }

        return columnName + " is null or " + columnName + value; //$NON-NLS-1$
    }

    /**
     * DOC yyin Comment method "isInformix".
     * 
     * @return
     */
    private boolean isInformix() {
        if (dbmsLanguage.getDbmsName().startsWith(SupportDBUrlType.INFORMIXDEFAULTURL.getLanguage())) {
            return true;
        }
        return false;
    }

    /**
     * DOC yyin Comment method "isInformix".
     * 
     * @return
     */
    private boolean isNetezza() {
        if (dbmsLanguage.getDbmsName().startsWith(SupportDBUrlType.NETEZZADEFAULTURL.getLanguage())) {
            return true;
        }
        return false;
    }

    /**
     * DOC yyin Comment method "isSqlServer".
     * 
     * @return
     */
    private boolean isSqlServer() {
        if (SupportDBUrlType.MSSQLDEFAULTURL.getLanguage().equals(dbmsLanguage.getDbmsName())) {
            return true;
        } else if (SupportDBUrlType.MSSQL2008URL.getLanguage().equals(dbmsLanguage.getDbmsName())) {
            return true;
        }
        return false;
    }

    /**
     * DOC yyin Comment method "isDB2".
     * 
     * @return
     */
    private boolean isDB2() {
        if (SupportDBUrlType.DB2DEFAULTURL.getLanguage().equals(dbmsLanguage.getDbmsName())) {
            return true;
        } else if (dbmsLanguage.getDbmsName().startsWith(SupportDBUrlType.DB2DEFAULTURL.getLanguage())) {
            return true;
        }
        return false;
    }

    /**
     * DOC yyin Comment method "getColumnName".
     * 
     * @return
     */
    private String getColumnName() {
        if (isSybase()) {
            return "convert(char(15)," + this.columnName + ")";//$NON-NLS-1$ //$NON-NLS-2$
        } else if (isPostGreSQL() || isTeradata()) {
            return "cast(" + this.columnName + " as char)";//$NON-NLS-1$ //$NON-NLS-2$
        }
        return this.columnName;
    }

    /**
     * DOC yyin Comment method "isOracle".
     * 
     * @return
     */
    private boolean isOracle() {
        if (SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getLanguage().equals(dbmsLanguage.getDbmsName())) {
            return true;
        }
        return false;
    }

    private boolean isSybase() {
        if (SupportDBUrlType.SYBASEDEFAULTURL.getLanguage().equals(dbmsLanguage.getDbmsName())) {
            return true;
        }
        return false;
    }

    private boolean isPostGreSQL() {
        if (SupportDBUrlType.POSTGRESQLEFAULTURL.getLanguage().equals(dbmsLanguage.getDbmsName())) {
            return true;
        }
        return false;
    }

    private boolean isTeradata() {
        if (SupportDBUrlType.TERADATADEFAULTURL.getLanguage().equals(dbmsLanguage.getDbmsName())) {
            return true;
        }
        return false;
    }
}
