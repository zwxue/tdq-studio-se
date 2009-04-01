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

package org.talend.dq.analysis.explore;

import java.util.Map;

import org.talend.dataquality.analysis.Analysis;
import orgomg.cwm.resource.relational.Column;

/**
 * 
 * DOC mzhao Get sql query statement for correlation.(apply to multiColumnSetValue indicator)
 */
public class MultiColumnSetValueExplorer extends DataExplorer {

    private static final String SELECT_ALL_FROM = "SELECT * FROM "; //$NON-NLS-1$

    private static final String WHERE = " WHERE ";

    private static MultiColumnSetValueExplorer instance = null;

    private MultiColumnSetValueExplorer() {

    }

    public static MultiColumnSetValueExplorer getInstance() {
        if (instance == null) {
            instance = new MultiColumnSetValueExplorer();
        }
        return instance;
    }

    public Map<String, String> getQueryMap() {
        return null;
    }

    public String getQueryStirng(Column column, Analysis ana, String columnName, String columnValue) {
        setAnalysis(ana);
        String queryString = SELECT_ALL_FROM + getFullyQualifiedTableName(column) + WHERE + columnName + dbmsLanguage.equal()
                + columnValue;
        return queryString;
    }
}
