package org.talend.dataprofiler.core.migration.impl;

import java.util.Date;

import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataprofiler.core.migration.helper.IndicatorDefinitionFileHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.indicators.definitions.DefinitionHandler;


public class UpdateIndicatorsForVertica extends AbstractWorksapceUpdateTask {

    private final String BODY_PATTERN_MATCH_SQL = "SELECT COUNT(CASE WHEN REGEXP_LIKE(TO_CHAR(<%=__COLUMN_NAMES__%>),<%=__PATTERN_EXPR__%>) THEN 1 END), COUNT(*) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"; //$NON-NLS-1$


    private final String IND_DEF_ID = "_yb-_8Dh8Ed2XmO7pl5Yuyg"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2013, 8, 26);
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
        DefinitionHandler definitionHandler = DefinitionHandler.getInstance();
        IndicatorDefinition patternMatchIndDef = definitionHandler.getDefinitionById(IND_DEF_ID);
        if (patternMatchIndDef != null) {
            IndicatorDefinitionFileHelper.addSqlExpression(patternMatchIndDef, SupportDBUrlType.VERTICA.getLanguage(),
                    BODY_PATTERN_MATCH_SQL);
            IndicatorDefinitionFileHelper.save(patternMatchIndDef);
            definitionHandler.reloadIndicatorsDefinitions();
        }
        return true;
    }

}
