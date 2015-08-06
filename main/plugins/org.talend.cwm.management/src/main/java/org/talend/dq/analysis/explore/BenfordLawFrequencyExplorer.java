// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
            return dbmsLanguage.getInvalidClauseBenFord(this.columnName);
        }
        Object value = "'" + entity.getKey() + "%'"; //$NON-NLS-1$ //$NON-NLS-2$

        String clause = entity.isLabelNull() ? getColumnName() + dbmsLanguage.isNull() : getColumnName() + dbmsLanguage.like()
                + value;

        if (isInformix()) {
            return " SUBSTR(" + getColumnName() + ",0,1)" + dbmsLanguage.like() + value; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return clause;
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
     * DOC yyin Comment method "getColumnName".
     * 
     * @return
     */
    private String getColumnName() {
        return dbmsLanguage.castColumnNameToChar(columnName);
    }

}
