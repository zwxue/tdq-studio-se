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

import org.talend.cwm.relational.TdColumn;


/**
 * @author scorreia
 * 
 * This class generate select statement for any function found in an instantiated query of the type:
 * "SELECT function(col), COUNT(*) FROM ..."
 */
public class FunctionFrequencyStatExplorer extends FrequencyStatisticsExplorer {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.FrequencyStatisticsExplorer#getFreqRowsStatement()
     */
    @Override
    protected String getFreqRowsStatement() {
        // generate SELECT * FROM TABLE WHERE function(columnName) = labelToFind
        TdColumn column = (TdColumn) indicator.getAnalyzedElement();

        String clause = super.getInstantiatedClause();
        return "SELECT * FROM " + getFullyQualifiedTableName(column) + dbmsLanguage.where() + inBrackets(clause) //$NON-NLS-1$
                + andDataFilterClause();
    }

}
