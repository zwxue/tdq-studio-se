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
package org.talend.dq.analysis.explore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.talend.cwm.relational.TdColumn;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * @author scorreia
 * 
 * This class generate select statement for any function found in an instantiated query of the type:
 * "SELECT function(col), COUNT(*) FROM ..."
 */
public class FunctionFrequencyStatExplorer extends FrequencyStatisticsExplorer {

    // private static final String REGEX = "SELECT (.*)\\s*, COUNT\\(\\*\\)\\s*(AS|as)?\\s*\\w*\\s* FROM";
    private static final String REGEX = "SELECT (.*)\\s*, COUNT\\(\\*\\)\\s*(AS|as)?\\s*\\w*\\s* FROM";

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.FrequencyStatisticsExplorer#getFreqRowsStatement()
     */
    @Override
    protected String getFreqRowsStatement() {
        // get function which convert data into a pattern
        String function = getFunction();
        // TODO scorreia handle null function
        // generate SELECT * FROM TABLE WHERE function(columnName) = labelToFind

        TdColumn column = (TdColumn) indicator.getAnalyzedElement();

        String clause = entity.isLabelNull() ? columnName + dbmsLanguage.isNull() : function + dbmsLanguage.equal() + "'"
                + entity.getLabel() + "'";
        return "SELECT * FROM " + getFullyQualifiedTableName(column) + dbmsLanguage.where() + inBrackets(clause)
                + andDataFilterClause();
    }

    private String getFunction() {
        Expression instantiatedExpression = dbmsLanguage.getInstantiatedExpression(indicator);
        final String body = instantiatedExpression.getBody();
        Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(body);
        matcher.find();
        String group = matcher.group(1);
        return group;
    }

}
