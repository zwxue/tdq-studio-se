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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.Table;

/**
 * DOC hcheng class global comment. Detailled comment
 */
public class RowMatchExplorer extends DataExplorer {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getQueryMap()
     */
    public Map<String, String> getQueryMap() {
        // TODO Auto-generated method stub
        Map<String, String> map = new HashMap<String, String>();
        map.put(MENU_VIEW_MATCH_ROWS, getRowsMatchStatement()); //$NON-NLS-1$
        map.put(MENU_VIEW_NOT_MATCH_ROWS, getRowsNotMatchStatement()); //$NON-NLS-1$
        map.put(MENU_VIEW_ROWS, getAllRowsStatement()); //$NON-NLS-1$
        return map;
    }

    /**
     * DOC yyi Comment method "getRowsNotMatchStatement".
     * 
     * @return
     */
    public String getRowsNotMatchStatement() {

        Table tablea = (Table) indicator.getAnalyzedElement();
        String tableA = tablea.getName();
        String query = "SELECT A.*" + dbmsLanguage.from();
        if (ColumnsetPackage.eINSTANCE.getRowMatchingIndicator() == indicator.eClass()) {
            Table tableb = (Table) ColumnHelper.getColumnSetOwner(((RowMatchingIndicator) indicator).getColumnSetB().get(0));
            String tableB = tableb.getName();
            EList<Column> columnSetA = ((RowMatchingIndicator) indicator).getColumnSetA();
            EList<Column> columnSetB = ((RowMatchingIndicator) indicator).getColumnSetB();

            String clauseA = " (SELECT *" + dbmsLanguage.from() + getFullyQualifiedTableName(tablea);
            String clauseB = " (SELECT *" + dbmsLanguage.from() + getFullyQualifiedTableName(tableb);
            String where = null;
            String onClause = " ON ";
            String realWhereClause = dbmsLanguage.where();
            String columnsName = "";

            for (int i = 0; i < columnSetA.size(); i++) {
                String fullColumnAName = getFullyQualifiedTableName(tablea) + "."
                        + dbmsLanguage.quote(columnSetA.get(i).getName());

                where = dbmsLanguage.and();
                if (i == 0) {
                    where = dbmsLanguage.where();
                    columnsName += " (" + fullColumnAName + " ";
                } else {
                    onClause += where;
                    realWhereClause += where;
                    columnsName += ", " + fullColumnAName + " ";
                }

                realWhereClause += " B" + dbmsLanguage.getDelimiter() + dbmsLanguage.quote(columnSetB.get(i).getName())
                        + dbmsLanguage.isNull();

                onClause += " (A" + dbmsLanguage.getDelimiter() + dbmsLanguage.quote(columnSetA.get(i).getName()) + "=" + " B"
                        + dbmsLanguage.getDelimiter() + dbmsLanguage.quote(columnSetB.get(i).getName()) + ") ";
            }
            columnsName += ") ";
            clauseA += (tableA.equals(tableB) ? whereDataFilter(tableA,
                    (getdataFilterIndex(null) == AnalysisHelper.DATA_FILTER_A ? AnalysisHelper.DATA_FILTER_A
                            : AnalysisHelper.DATA_FILTER_B)) : whereDataFilter(tableA, null))
                    + ") A";

            clauseB += (tableB.equals(tableA) ? whereDataFilter(tableB,
                    (getdataFilterIndex(null) == AnalysisHelper.DATA_FILTER_A ? AnalysisHelper.DATA_FILTER_B
                            : AnalysisHelper.DATA_FILTER_A)) : whereDataFilter(tableB, null))
                    + ") B";
            query += clauseA + " LEFT JOIN " + clauseB + onClause + realWhereClause;
        }
        return query;
    }

    /**
     * DOC Administrator Comment method "getRowsMatchStatement".
     * 
     * @return
     */
    public String getRowsMatchStatement() {
        Table tablea = (Table) indicator.getAnalyzedElement();
        String tableA = tablea.getName();
        String query = "";
        if (ColumnsetPackage.eINSTANCE.getRowMatchingIndicator() == indicator.eClass()) {
            Table tableb = (Table) ColumnHelper.getColumnSetOwner(((RowMatchingIndicator) indicator).getColumnSetB().get(0));
            String tableB = tableb.getName();
            EList<Column> columnSetA = ((RowMatchingIndicator) indicator).getColumnSetA();
            EList<Column> columnSetB = ((RowMatchingIndicator) indicator).getColumnSetB();

            String clauseA = " (SELECT *" + dbmsLanguage.from() + getFullyQualifiedTableName(tablea);
            String clauseB = " (SELECT *" + dbmsLanguage.from() + getFullyQualifiedTableName(tableb);
            String where = null;
            String onClause = " ON ";
            String realWhereClause = dbmsLanguage.where();
            String columnsName = "";

            for (int i = 0; i < columnSetA.size(); i++) {
                String fullColumnAName = getFullyQualifiedTableName(tablea) + "."
                        + dbmsLanguage.quote(columnSetA.get(i).getName());

                where = dbmsLanguage.and();
                if (i == 0) {
                    where = dbmsLanguage.where();
                    columnsName += " (" + fullColumnAName + " ";
                } else {
                    onClause += where;
                    realWhereClause += where;
                    columnsName += ", " + fullColumnAName + " ";
                }

                realWhereClause += " B" + dbmsLanguage.getDelimiter() + dbmsLanguage.quote(columnSetB.get(i).getName())
                        + dbmsLanguage.isNull();

                onClause += " (A" + dbmsLanguage.getDelimiter() + dbmsLanguage.quote(columnSetA.get(i).getName()) + "=" + " B"
                        + dbmsLanguage.getDelimiter() + dbmsLanguage.quote(columnSetB.get(i).getName()) + ") ";

            }
            columnsName += ") ";
            clauseA += (tableA.equals(tableB) ? whereDataFilter(tableA,
                    (getdataFilterIndex(null) == AnalysisHelper.DATA_FILTER_A ? AnalysisHelper.DATA_FILTER_A
                            : AnalysisHelper.DATA_FILTER_B)) : whereDataFilter(tableA, null))
                    + ") A";

            clauseB += (tableB.equals(tableA) ? whereDataFilter(tableB,
                    (getdataFilterIndex(null) == AnalysisHelper.DATA_FILTER_A ? AnalysisHelper.DATA_FILTER_B
                            : AnalysisHelper.DATA_FILTER_A)) : whereDataFilter(tableB, null))
                    + ") B";

            query = "SELECT * FROM " + getFullyQualifiedTableName(tablea);

            for (int i = 0; i < columnSetA.size(); i++) {
                String clause = "";
                String fullColumnAName = getFullyQualifiedTableName(tablea) + "."
                        + dbmsLanguage.quote(columnSetA.get(i).getName());
                String columnNameByAlias = " A" + dbmsLanguage.getDelimiter() + dbmsLanguage.quote(columnSetA.get(i).getName());
                clause = "(SELECT " + columnNameByAlias + dbmsLanguage.from() + clauseA + " LEFT JOIN " + clauseB + onClause
                        + realWhereClause + ")";
                if (i == 0) {

                    clause = dbmsLanguage.where() + "(" + fullColumnAName + dbmsLanguage.notIn() + clause;
                } else {
                    clause = dbmsLanguage.or() + fullColumnAName + dbmsLanguage.notIn() + clause;
                }
                query += clause;
            }
            query += ") "
                    + (tableA.equals(tableB) ? andDataFilter(tableA,
                            (getdataFilterIndex(null) == AnalysisHelper.DATA_FILTER_A ? AnalysisHelper.DATA_FILTER_A
                                    : AnalysisHelper.DATA_FILTER_B)) : andDataFilter(tableA, null));
        }

        return query;
    }

    /**
     * DOC Administrator Comment method "getAllRowsStatement".
     * 
     * @return
     */
    public String getAllRowsStatement() {
        Table tablea = (Table) indicator.getAnalyzedElement();
        String tableA = tablea.getName();
        Table tableb = (Table) ColumnHelper.getColumnSetOwner(((RowMatchingIndicator) indicator).getColumnSetB().get(0));
        String tableB = tableb.getName();
        return "SELECT * " + dbmsLanguage.from() + getFullyQualifiedTableName(tablea) + whereDataFilter(tableA.equals(tableB) ? null : tableA, null); //$NON-NLS-1$
    }

    /**
     * 
     * DOC zshen 2010-01-15 Comment method "getdataFilterIndex".
     * 
     * @param tableOrViewName the name of table or view.if null get index of current indicator in analysis
     * @return the index for datafilter. return -1 when can't find
     */
    private int getdataFilterIndex(Object nameOrIndicator) {
        if (nameOrIndicator == null) {
            nameOrIndicator = this.indicator;
        }
        Iterator<Indicator> iter = this.analysis.getResults().getIndicators().iterator();
        int result = 0;
        Object currentObj = null;
        while (iter.hasNext()) {
            Indicator indicator = iter.next();
            if (nameOrIndicator instanceof String) {
                currentObj = indicator.getAnalyzedElement().getName();
            } else {
                currentObj = indicator;
            }
            if (currentObj.equals(nameOrIndicator)) {
                return result;
            } else {
                result++;
            }
        }
        return -1;

    }

    /**
     * 
     * DOC zshen Comment method "andDataFilter".
     * 
     * @param tableOrViewName the name of table or view
     * @return DataFilter clause
     */
    private String andDataFilter(String tableOrViewName, Integer index) {
        String andTable = null;
        if (index == null) {
            andTable = AnalysisHelper.getStringDataFilter(analysis, getdataFilterIndex(tableOrViewName));
        } else {
            andTable = AnalysisHelper.getStringDataFilter(analysis, index.intValue());
        }
        if (null != andTable && !andTable.equals("")) {
            andTable = dbmsLanguage.and() + andTable;
        }
        if (andTable == null)
            andTable = "";
        return andTable;

    }

    /**
     * 
     * DOC zshen Comment method "andDataFilter".
     * 
     * @param tableOrViewName the name of table or view.
     * @param index have known index.
     * @return DataFilter clause
     */
    private String whereDataFilter(Object tableOrViewName, Integer index) {
        String andTable = null;
        if (index == null) {
            andTable = AnalysisHelper.getStringDataFilter(analysis, getdataFilterIndex(tableOrViewName));
        } else {
            andTable = AnalysisHelper.getStringDataFilter(analysis, index.intValue());
        }
        // String andTable = AnalysisHelper.getStringDataFilter(analysis, getdataFilterIndex(tableOrViewName));
        if (null != andTable && !andTable.equals("")) {
            andTable = dbmsLanguage.where() + andTable;
        }
        if (andTable == null)
            andTable = "";
        return andTable;

    }
}
