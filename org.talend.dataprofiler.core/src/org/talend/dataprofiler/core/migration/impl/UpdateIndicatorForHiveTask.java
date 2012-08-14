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
package org.talend.dataprofiler.core.migration.impl;

import java.util.Date;

import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataprofiler.core.migration.helper.IndicatorDefinitionFileHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;

/**
 * 
 * Add Sql Expression in some indicators for hive language.
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class UpdateIndicatorForHiveTask extends AbstractWorksapceUpdateTask {

    private final String UNIQUE_LABEL = "Unique Count"; //$NON-NLS-1$

    private final String DUPLICATE_LABEL = "Duplicate Count"; //$NON-NLS-1$

    private final String REGEXPATTERNMATCH_LABEL = "Regular Expression Matching"; //$NON-NLS-1$

    private final String ROWCOMPAREMATCH_LABEL = "Row Comparison"; //$NON-NLS-1$

    private final String HIVE = SupportDBUrlType.HIVEDEFAULTURL.getLanguage();

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2012, 8, 10);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        IndicatorDefinition uniqueDefinition = IndicatorDefinitionFileHelper.getSystemIndicatorByName(UNIQUE_LABEL);
        if (uniqueDefinition != null && !IndicatorDefinitionFileHelper.isExistSqlExprWithLanguage(uniqueDefinition, HIVE)) {
            IndicatorDefinitionFileHelper
                    .addSqlExpression(
                            uniqueDefinition,
                            HIVE,
                            "SELECT COUNT(*) FROM (SELECT <%=__COLUMN_NAMES__%>, COUNT(*) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%> GROUP BY <%=__COLUMN_NAMES__%> HAVING COUNT(*) = 1) myquery"); //$NON-NLS-1$
            IndicatorDefinitionFileHelper.save(uniqueDefinition);
        }
        IndicatorDefinition duplicateDefinition = IndicatorDefinitionFileHelper.getSystemIndicatorByName(DUPLICATE_LABEL);
        if (duplicateDefinition != null && !IndicatorDefinitionFileHelper.isExistSqlExprWithLanguage(duplicateDefinition, HIVE)) {

            IndicatorDefinitionFileHelper
                    .addSqlExpression(
                            duplicateDefinition,
                            HIVE,
                            "SELECT COUNT(*) FROM (SELECT <%=__COLUMN_NAMES__%>, COUNT(*) FROM <%=__TABLE_NAME__%>  m <%=__WHERE_CLAUSE__%> GROUP BY <%=__COLUMN_NAMES__%> HAVING COUNT(*) > 1) myquery"); //$NON-NLS-1$
            IndicatorDefinitionFileHelper.save(duplicateDefinition);
        }
        IndicatorDefinition regexPatternDefinition = IndicatorDefinitionFileHelper
                .getSystemIndicatorByName(REGEXPATTERNMATCH_LABEL);
        if (regexPatternDefinition != null
                && !IndicatorDefinitionFileHelper.isExistSqlExprWithLanguage(regexPatternDefinition, HIVE)) {
            IndicatorDefinitionFileHelper
                    .addSqlExpression(
                            regexPatternDefinition,
                            HIVE,
                            "SELECT COUNT(CASE WHEN <%=__COLUMN_NAMES__%> REGEXP <%=__PATTERN_EXPR__%> THEN 1 END), COUNT(*) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$
            IndicatorDefinitionFileHelper.save(regexPatternDefinition);
        }
        IndicatorDefinition rowCompareDefinition = IndicatorDefinitionFileHelper.getSystemIndicatorByName(ROWCOMPAREMATCH_LABEL);
        if (rowCompareDefinition != null && !IndicatorDefinitionFileHelper.isExistSqlExprWithLanguage(rowCompareDefinition, HIVE)) {
            IndicatorDefinitionFileHelper
                    .addSqlExpression(
                            rowCompareDefinition,
                            HIVE,
                            "SELECT COUNT(*) FROM <%=__TABLE_NAME__%> LEFT OUTER JOIN <%=__TABLE_NAME_2__%> ON (<%=__JOIN_CLAUSE__%>) WHERE (<%=__WHERE_CLAUSE__%>)"); //$NON-NLS-1$
            IndicatorDefinitionFileHelper.save(rowCompareDefinition);
        }
        return true;
    }

}
