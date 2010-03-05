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
 * DOC xqliu class global comment. Detailled comment
 */
public class ColumnDependencyExplorer extends DataExplorer {


    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("View valid values", this.getValidValuesStatement());
        map.put("View invalid values", this.getInvalidValuesStatement());
        map.put("View detailed invalid values", this.getDetailedInvalidValuesStatement());
        map.put("View detailed valid values", this.getDetailedValidValuesStatement());
        map.put(Messages.getString("ColumnDependencyExplorer.viewValidRows"), getValidRowsStatement()); //$NON-NLS-1$
        map.put(Messages.getString("ColumnDependencyExplorer.viewInvalidRows"), getInvalidRowsStatement()); //$NON-NLS-1$
        return map;
    }

    /**
     * DOC xqliu Comment method "getInvalidRowsStatement".
     * 
     * @return
     */
    private String getInvalidRowsStatement() {
        return getStatement(dbmsLanguage.getFDGenericInvalidRows());
    }

    private String getDetailedInvalidValuesStatement() {
        return getStatement(dbmsLanguage.getFDGenericInvalidDetailedValues());
    }

    private String getDetailedValidValuesStatement() {
        return getStatement(dbmsLanguage.getFDGenericValidDetailedValues());
    }
    
    private String getInvalidValuesStatement() {
        return getStatement(dbmsLanguage.getFDGenericInvalidValues());
    }

    private String getValidValuesStatement() {
        return getStatement(dbmsLanguage.getFDGenericValidValues());
    }

    
    /**
     * DOC xqliu Comment method "getValidRowsStatement".
     * 
     * @return
     */
    private String getValidRowsStatement() {
        return getStatement(dbmsLanguage.getFDGenericValidRows());
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
