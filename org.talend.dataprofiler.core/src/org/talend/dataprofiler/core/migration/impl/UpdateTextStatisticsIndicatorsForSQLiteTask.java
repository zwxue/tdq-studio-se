// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
 * DOC msjian class global comment. The task is used to add sql template in all 12 Text Statistics
 * for SQLite. 22517: 2011-07-20
 */
public class UpdateTextStatisticsIndicatorsForSQLiteTask extends AbstractWorksapceUpdateTask {

    private static final String AVERAGE_LENGTH = "Average Length"; //$NON-NLS-1$

    private static final String AVERAGE_LENGTH_WITH_BLANK = "Average Length With Blank"; //$NON-NLS-1$

    private static final String AVERAGE_LENGTH_WITH_BLANK_AND_NULL = "Average Length With Blank and Null"; //$NON-NLS-1$
    
    private static final String AVERAGE_LENGTH_WITH_NULL = "Average Length With Null"; //$NON-NLS-1$

    private static final String MAXIMAL_LENGTH = "Maximal Length"; //$NON-NLS-1$

    private static final String MAXIMAL_LENGTH_WITH_BLANK = "Maximal Length With Blank"; //$NON-NLS-1$
    
    private static final String MAXIMAL_LENGTH_WITH_BLANK_AND_NULL = "Maximal Length With Blank and Null"; //$NON-NLS-1$

    private static final String MAXIMAL_LENGTH_WITH_NULL = "Maximal Length With Null"; //$NON-NLS-1$

    private static final String MINIMAL_LENGTH = "Minimal Length"; //$NON-NLS-1$
    
    private static final String MINIMAL_LENGTH_WITH_BLANK = "Minimal Length With Blank"; //$NON-NLS-1$

    private static final String MINIMAL_LENGTH_WITH_BLANK_AND_NULL = "Minimal Length With Blank and Null"; //$NON-NLS-1$

    private static final String MINIMAL_LENGTH_WITH_NULL = "Minimal Length With Null"; //$NON-NLS-1$

    private static final String SQLITE = SupportDBUrlType.SQLITE3DEFAULTURL.getLanguage();
    
    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {

        IndicatorDefinition definition0 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(AVERAGE_LENGTH);
        IndicatorDefinition definition1 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(AVERAGE_LENGTH_WITH_BLANK);
        IndicatorDefinition definition2 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(AVERAGE_LENGTH_WITH_BLANK_AND_NULL);
        IndicatorDefinition definition3 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(AVERAGE_LENGTH_WITH_NULL);
        IndicatorDefinition definition4 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(MAXIMAL_LENGTH);
        IndicatorDefinition definition5 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(MAXIMAL_LENGTH_WITH_BLANK);
        IndicatorDefinition definition6 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(MAXIMAL_LENGTH_WITH_BLANK_AND_NULL);
        IndicatorDefinition definition7 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(MAXIMAL_LENGTH_WITH_NULL);
        IndicatorDefinition definition8 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(MINIMAL_LENGTH);
        IndicatorDefinition definition9 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(MINIMAL_LENGTH_WITH_BLANK);
        IndicatorDefinition definition10 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(MINIMAL_LENGTH_WITH_BLANK_AND_NULL);
        IndicatorDefinition definition11 = IndicatorDefinitionFileHelper.getSystemIndicatorByName(MINIMAL_LENGTH_WITH_NULL);
        
        if (definition0 != null && definition1 != null && definition2 != null && definition3 != null && definition4 != null
                && definition5 != null && definition6 != null && definition7 != null && definition8 != null
                && definition9 != null && definition10 != null && definition11 != null) {

            IndicatorDefinitionFileHelper.addSqlExpression(definition0, SQLITE,
                    "SELECT SUM(LENGTH(<%=__COLUMN_NAMES__%>)), COUNT(<%=__COLUMN_NAMES__%>) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition1, SQLITE,
                    "SELECT SUM(LENGTH(<%=__COLUMN_NAMES__%>)), COUNT(<%=__COLUMN_NAMES__%>) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition2, SQLITE,
                    "SELECT SUM(LENGTH(<%=__COLUMN_NAMES__%>)), COUNT(<%=__COLUMN_NAMES__%>) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition3, SQLITE,
                    "SELECT SUM(LENGTH(<%=__COLUMN_NAMES__%>)), COUNT(<%=__COLUMN_NAMES__%>) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition4, SQLITE,
                    "SELECT MAX(LENGTH(<%=__COLUMN_NAMES__%>)) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition5, SQLITE,
                    "SELECT MAX(LENGTH(<%=__COLUMN_NAMES__%>)) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition6, SQLITE,
                    "SELECT MAX(LENGTH(<%=__COLUMN_NAMES__%>)) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition7, SQLITE,
                    "SELECT MAX(LENGTH(<%=__COLUMN_NAMES__%>)) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition8, SQLITE,
                    "SELECT MIN(LENGTH(<%=__COLUMN_NAMES__%>)) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition9, SQLITE,
                    "SELECT MIN(LENGTH(<%=__COLUMN_NAMES__%>)) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition10, SQLITE,
                    "SELECT MIN(LENGTH(<%=__COLUMN_NAMES__%>)) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            IndicatorDefinitionFileHelper.addSqlExpression(definition11, SQLITE,
                    "SELECT MIN(LENGTH(<%=__COLUMN_NAMES__%>)) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$

            return IndicatorDefinitionFileHelper.save(definition0) & IndicatorDefinitionFileHelper.save(definition1)
                    & IndicatorDefinitionFileHelper.save(definition2) & IndicatorDefinitionFileHelper.save(definition3)
                    & IndicatorDefinitionFileHelper.save(definition4) & IndicatorDefinitionFileHelper.save(definition5)
                    & IndicatorDefinitionFileHelper.save(definition6) & IndicatorDefinitionFileHelper.save(definition7)
                    & IndicatorDefinitionFileHelper.save(definition8) & IndicatorDefinitionFileHelper.save(definition9)
                    & IndicatorDefinitionFileHelper.save(definition10) & IndicatorDefinitionFileHelper.save(definition11);
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2011, 7, 20);
    }

}
