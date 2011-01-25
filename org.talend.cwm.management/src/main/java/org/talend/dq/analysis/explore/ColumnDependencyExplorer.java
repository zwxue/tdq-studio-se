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
import org.talend.cwm.relational.MeatadataColumn;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.columnset.ColumnDependencyIndicator;
import org.talend.dq.dbms.GenericSQLHandler;
import org.talend.dq.helper.ColumnDependencyHelper;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ColumnDependencyExplorer extends DataExplorer {

    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(MENU_VIEW_VALID_VALUES, this.getValidValuesStatement());
        map.put(MENU_VIEW_INVALID_VALUES, this.getInvalidValuesStatement());
        map.put(MENU_VIEW_DETAILED_INVALID_VALUES, this.getDetailedInvalidValuesStatement());
        map.put(MENU_VIEW_DETAILED_VALID_VALUES, this.getDetailedValidValuesStatement());
        map.put(MENU_VIEW_VALID_ROWS, getValidRowsStatement()); //$NON-NLS-1$
        map.put(MENU_VIEW_INVALID_ROWS, getInvalidRowsStatement()); //$NON-NLS-1$
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
        // MOD by zshen fixed 12039 to distinguish the same name of column.
        String genericSQL = dbmsLanguage.getFDGenericInvalidDetailedValues();
        ColumnDependencyIndicator cdIndicator = ((ColumnDependencyIndicator) this.indicator);
        MeatadataColumn columnA = cdIndicator.getColumnA();
        MeatadataColumn columnB = cdIndicator.getColumnB();
        if (columnA.getName().equals(columnB.getName())) {
            genericSQL = genericSQL.replaceFirst(GenericSQLHandler.COLUMN_NAMES_A, dbmsLanguage.quote(columnA.getName()) + " AS "
                    + columnA.getName() + "_A");
            genericSQL = genericSQL.replaceFirst(GenericSQLHandler.COLUMN_NAMES_B, dbmsLanguage.quote(columnB.getName()) + " AS "
                    + columnA.getName() + "_B");
        }
        return getStatement(genericSQL);
    }

    private String getDetailedValidValuesStatement() {
        // MOD by zshen fixed 12039 to distinguish the same name of column.
        String genericSQL = dbmsLanguage.getFDGenericValidDetailedValues();
        ColumnDependencyIndicator cdIndicator = ((ColumnDependencyIndicator) this.indicator);
        MeatadataColumn columnA = cdIndicator.getColumnA();
        MeatadataColumn columnB = cdIndicator.getColumnB();
        if (columnA.getName().equals(columnB.getName())) {
            genericSQL = genericSQL.replaceFirst(GenericSQLHandler.COLUMN_NAMES_A, dbmsLanguage.quote(columnA.getName()) + " AS "
                    + columnA.getName() + "_A");
            genericSQL = genericSQL.replaceFirst(GenericSQLHandler.COLUMN_NAMES_B, dbmsLanguage.quote(columnB.getName()) + " AS "
                    + columnA.getName() + "_B");
        }
        return getStatement(genericSQL);
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
        MeatadataColumn columnA = cdIndicator.getColumnA();
        MeatadataColumn columnB = cdIndicator.getColumnB();

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
