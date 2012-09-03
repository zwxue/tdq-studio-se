// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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
        Object value = "'" + entity.getKey() + "%'"; //$NON-NLS-1$ //$NON-NLS-2$

        String clause = entity.isLabelNull() ? getColumnName() + dbmsLanguage.isNull() : getColumnName() + dbmsLanguage.like()
                + value;
        return clause;
    }

    /**
     * DOC yyin Comment method "getColumnName".
     * 
     * @return
     */
    private String getColumnName() {
        String name = this.dbmsLanguage.getDbmsName();

        if (SupportDBUrlType.SYBASEDEFAULTURL.getLanguage().equals(name)) {
            return "convert(char(15)," + this.columnName + ")";
        } else if (SupportDBUrlType.TERADATADEFAULTURL.getLanguage().equals(name)
                || SupportDBUrlType.POSTGRESQLEFAULTURL.getLanguage().equals(name)) {
            return "cast(" + this.columnName + " as char)";
        }
        return this.columnName;
    }

}
