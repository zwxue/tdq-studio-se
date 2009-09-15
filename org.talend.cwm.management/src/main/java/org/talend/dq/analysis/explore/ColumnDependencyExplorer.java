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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.columnset.ColumnDependencyIndicator;
import org.talend.dq.dbms.GenericSQLHandler;
import org.talend.dq.helper.ColumnDependencyHelper;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.resource.relational.Column;


/**
 * DOC xqliu  class global comment. Detailled comment
 */
public class ColumnDependencyExplorer extends DataExplorer {
    
    private static final String INVALID_GENERIC_SQL = "SELECT DISTINCT A , COUNT(*) FROM (SELECT DISTINCT <%=__COLUMN_NAME_A__%> AS A , <%=__COLUMN_NAME_B__%> AS B FROM <%=__TABLE_NAME__%> C ) T GROUP BY A HAVING COUNT(*) > 1";

    private static final String VALID_GENERIC_SQL = "SELECT DISTINCT A , COUNT(*) FROM (SELECT DISTINCT <%=__COLUMN_NAME_A__%> AS A , <%=__COLUMN_NAME_B__%> AS B FROM <%=__TABLE_NAME__%> C ) T GROUP BY A HAVING COUNT(*) = 1";

    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(Messages.getString("ColumnDependencyExplorer.browseValidRows"), getValidRowsStatement()); //$NON-NLS-1$
        map.put(Messages.getString("ColumnDependencyExplorer.browseInvalidRows"), getInvalidRowsStatement()); //$NON-NLS-1$
        return map;
    }

    /**
     * DOC xqliu Comment method "getInvalidRowsStatement".
     * 
     * @return
     */
    private String getInvalidRowsStatement() {
        return getStatement(INVALID_GENERIC_SQL);
    }

    /**
     * DOC xqliu Comment method "getValidRowsStatement".
     * 
     * @return
     */
    private String getValidRowsStatement() {
        return getStatement(VALID_GENERIC_SQL);
    }

    /**
     * DOC xqliu Comment method "getStatement".
     * 
     * @param genericSQL
     * @return
     */
    private String getStatement(String genericSQL) {
        ColumnDependencyIndicator cdIndicator = ((ColumnDependencyIndicator) this.indicator);
        Column columnA = cdIndicator.getColumnA();
        Column columnB = cdIndicator.getColumnB();

        GenericSQLHandler sqlHandler = new GenericSQLHandler(genericSQL);
        sqlHandler.replaceColumnA(dbmsLanguage.quote(columnA.getName())).replaceColumnB(dbmsLanguage.quote(columnB.getName()))
                .replaceTable(dbmsLanguage.quote(getFullyQualifiedTableName(columnA)));

        String instantiatedSQL = sqlHandler.getSqlString();

        List<String> whereClauses = new ArrayList<String>();
        String dataFilter = AnalysisHelper.getStringDataFilter(this.analysis);
        if (!StringUtils.isEmpty(dataFilter)) {
            whereClauses.add(dataFilter);
        }

        instantiatedSQL = dbmsLanguage.addWhereToSqlStringStatement(instantiatedSQL, whereClauses);

        return instantiatedSQL;
    }

    public void setEnitty(ChartDataEntity entity) {
        this.entity = entity;
        this.indicator = entity.getIndicator();
        this.indicatorEnum = IndicatorEnum.findIndicatorEnum(indicator.eClass());
        this.columnName = ColumnDependencyHelper.getIndicatorName(entity.getIndicator());
    }
}
