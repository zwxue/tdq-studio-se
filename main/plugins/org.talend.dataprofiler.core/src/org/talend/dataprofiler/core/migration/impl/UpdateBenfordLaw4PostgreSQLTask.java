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
package org.talend.dataprofiler.core.migration.impl;

import java.util.Date;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataprofiler.core.migration.helper.IndicatorDefinitionFileHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.indicators.definitions.DefinitionHandler;

/**
 * created by msjian on 2016,3,3 for TDQ-11558: fix the expression can be used for redshift
 *
 */
public class UpdateBenfordLaw4PostgreSQLTask extends AbstractWorksapceUpdateTask {

    private String indicatorLabel = "Benford Law Frequency"; //$NON-NLS-1$

    private String expressionBody = "SELECT SUBSTRING(cast(<%=__COLUMN_NAMES__%> as char),1,2), COUNT(*)  FROM <%=__TABLE_NAME__%> t <%=__WHERE_CLAUSE__%> GROUP BY SUBSTRING(cast(<%=__COLUMN_NAMES__%> as char),1,2) order by SUBSTRING(cast(<%=__COLUMN_NAMES__%> as char),1,2)"; //$NON-NLS-1$

    public Date getOrder() {
        return createDate(2016, 3, 3);
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    @Override
    protected boolean doExecute() throws Exception {
        boolean result = false;
        IndicatorDefinition indiDefinition = IndicatorDefinitionFileHelper.getSystemIndicatorByName(indicatorLabel);
        String sybaseLanguage = SupportDBUrlType.POSTGRESQLEFAULTURL.getLanguage();
        if (indiDefinition != null) {
            EList<TdExpression> sqlGenericExpression = indiDefinition.getSqlGenericExpression();
            for (TdExpression exp : sqlGenericExpression) {
                if (sybaseLanguage.equals(exp.getLanguage())) {
                    exp.setBody(expressionBody);
                }
            }
            result = IndicatorDefinitionFileHelper.save(indiDefinition);
            DefinitionHandler.getInstance().reloadIndicatorsDefinitions();
        }
        return result;
    }
}
