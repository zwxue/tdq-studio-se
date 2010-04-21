// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.talend.cwm.relational.TdColumn;
import org.talend.dq.dbms.DB2DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.utils.sql.Java2SqlType;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * @author scorreia
 * 
 * This class generate select statement for any function found in an instantiated query of the type:
 * "SELECT function(col), COUNT(*) FROM ..."
 */
public class FunctionFrequencyStatExplorer extends FrequencyStatisticsExplorer {

    // MOD mzhao 2010-04-12 bug 11554
    private static final String REGEX = ".*\\s*SELECT.*\\s* (REPLACE.*|TRANSLATE.*|TO_NUMBER.*|DATEPART.*)\\s*, COUNT\\(\\*\\)\\s*(AS|as)?\\s*\\w*\\s* FROM"; //$NON-NLS-1$

    private static final String REGEX_INFORMIX = ".*\\s*SELECT (REPLACE.*)\\s*(AS|as)+\\s*\\w+\\s* FROM"; //$NON-NLS-1$

    private static final String REGEX_INFOMIX_DATE = ".*\\s*SELECT.*\\s*(YEAR.*)\\s*, COUNT\\(\\*\\)\\s*(AS|as)?\\s*\\w*\\s* FROM"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.FrequencyStatisticsExplorer#getFreqRowsStatement()
     */
    @Override
    protected String getFreqRowsStatement() {
        // generate SELECT * FROM TABLE WHERE function(columnName) = labelToFind
        TdColumn column = (TdColumn) indicator.getAnalyzedElement();

        String clause = getInstantiatedClause();
        return "SELECT * FROM " + getFullyQualifiedTableName(column) + dbmsLanguage.where() + inBrackets(clause) //$NON-NLS-1$
                + andDataFilterClause();
    }

    protected String getInstantiatedClause() {
        // get function which convert data into a pattern
        String function = null;
        TdColumn column = (TdColumn) indicator.getAnalyzedElement();
        int javaType = column.getJavaType();
        if (!Java2SqlType.isNumbericInSQL(javaType)) {
            function = getFunction();
        }
        // MOD mzhao bug 9681 2009-11-09

        Object value = null;
        if (Java2SqlType.isNumbericInSQL(javaType) && dbmsLanguage instanceof DB2DbmsLanguage) {
            value = entity.getKey();
        } else {
            value = "'" + entity.getKey() + "'";
        }

        String clause = entity.isLabelNull() ? columnName + dbmsLanguage.isNull() : ((function == null ? columnName : function)
                + dbmsLanguage.equal() + value); //$NON-NLS-1$ //$NON-NLS-2$
        return clause;
    }

    private String getFunction() {
        Expression instantiatedExpression = dbmsLanguage.getInstantiatedExpression(indicator);
        final String body = instantiatedExpression.getBody();
        Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

        // MOD mzhao 2010-04-12 bug 11554
        String dbmsName = this.dbmsLanguage.getDbmsName();
        if (DbmsLanguageFactory.isInfomix(dbmsName)) {
            // Handle date type.
            TdColumn column = (TdColumn) indicator.getAnalyzedElement();
            int javaType = column.getJavaType();
            if (Java2SqlType.isDateInSQL(javaType)) {
                p = Pattern.compile(REGEX_INFOMIX_DATE, Pattern.CASE_INSENSITIVE);
            } else {
                p = Pattern.compile(REGEX_INFORMIX, Pattern.CASE_INSENSITIVE);
            }
        }
        // ~
        Matcher matcher = p.matcher(body);
        matcher.find();
        // MOD mzhao 2009-11-09 bug 9681: Catch the possibility that the sql body contains "TOP" keywords.
        // MOD MOD mzhao 2010-04-12 bug 11554
        String group = matcher.group(1);
        return group;
    }
}
