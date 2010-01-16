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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.management.i18n.Messages;
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
        map.put(Messages.getString("RowMatchExplorer.viewMatch"), getRowsMatchStatement()); //$NON-NLS-1$
        map.put(Messages.getString("RowMatchExplorer.viewNotMatch"), getRowsNotMatchStatement()); //$NON-NLS-1$
        map.put(Messages.getString("RowMatchExplorer.viewRows"), getAllRowsStatement()); //$NON-NLS-1$
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
        String query = "SELECT *" + dbmsLanguage.from() + getFullyQualifiedTableName(tablea); //$NON-NLS-1$

        if (ColumnsetPackage.eINSTANCE.getRowMatchingIndicator() == indicator.eClass()) {
            Table tableb = (Table) ColumnHelper.getColumnSetOwner(((RowMatchingIndicator) indicator).getColumnSetB().get(0));
            String tableB = tableb.getName();
            EList<Column> columnSetA = ((RowMatchingIndicator) indicator).getColumnSetA();
            EList<Column> columnSetB = ((RowMatchingIndicator) indicator).getColumnSetB();

            String where = null;
            for (int i = 0; i < columnSetA.size(); i++) {
                where = dbmsLanguage.or();
                if (i == 0) {
                    where = dbmsLanguage.where();
                }
                String fullColumnAName = dbmsLanguage.quote(tableA) + "." + dbmsLanguage.quote(columnSetA.get(i).getName());
                String fullColumnBName = dbmsLanguage.quote(tableB) + "." + dbmsLanguage.quote(columnSetB.get(i).getName());
                String clause = "SELECT "
                        + fullColumnBName
                        + dbmsLanguage.from()
                        + getFullyQualifiedTableName(tableb)
                        + dbmsLanguage.where()
                        + fullColumnBName
                        + dbmsLanguage.isNotNull()
                        // MOD 10913 zshen: find data Filter clause to adapt when ColumnA and ColumnB to be exchange
                        + (tableA.equals(tableB) ? andDataFilter(tableB, AnalysisHelper.DATA_FILTER_B) : andDataFilter(tableB,
                                null));
                // ~10913
                query += where
                        + fullColumnAName
                        + dbmsLanguage.notIn()
                        + inBrackets(clause)
                        // MOD 10913 zshen: find data Filter clause to adapt when ColumnA and ColumnB to be exchange
                        + (tableB.equals(tableA) ? andDataFilter(tableB, AnalysisHelper.DATA_FILTER_A) : andDataFilter(tableA,
                                null));
                // ~10913
                // MOD yyi 2009-12-07 9538 list nulls in no match rows
                String listNulls = dbmsLanguage.or() + fullColumnAName + dbmsLanguage.isNull();
                query += listNulls;
                // ~
            }
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
        String query = "SELECT *" + dbmsLanguage.from() + getFullyQualifiedTableName(tablea); //$NON-NLS-1$
        if (ColumnsetPackage.eINSTANCE.getRowMatchingIndicator() == indicator.eClass()) {
            Table tableb = (Table) ColumnHelper.getColumnSetOwner(((RowMatchingIndicator) indicator).getColumnSetB().get(0));
            String tableB = tableb.getName();
            EList<Column> columnSetA = ((RowMatchingIndicator) indicator).getColumnSetA();
            EList<Column> columnSetB = ((RowMatchingIndicator) indicator).getColumnSetB();

            String where = null;
            for (int i = 0; i < columnSetA.size(); i++) {
                where = dbmsLanguage.and();
                if (i == 0) {
                    where = dbmsLanguage.where();
                }

                String fullColumnName = dbmsLanguage.quote(tableB) + "." + dbmsLanguage.quote(columnSetB.get(i).getName());
                String clause = "SELECT "
                        + fullColumnName
                        + dbmsLanguage.from()
                        + getFullyQualifiedTableName(tableb)
                        + dbmsLanguage.where()
                        + fullColumnName
                        + dbmsLanguage.isNotNull()
                        // MOD 10913 zshen: find data Filter clause to adapt when ColumnA and ColumnB to be exchange
                        + (tableA.equals(tableB) ? andDataFilter(tableB, AnalysisHelper.DATA_FILTER_B) : andDataFilter(tableB,
                                null));
                // ~10913
                query += where
                        + dbmsLanguage.quote(tableA)
                        + "." + dbmsLanguage.quote(columnSetA.get(i).getName()) //$NON-NLS-1$
                        + dbmsLanguage.in()
                        + inBrackets(clause)
                        // MOD 10913 zshen: find data Filter clause to adapt when ColumnA and ColumnB to be exchange
                        + (tableB.equals(tableA) ? andDataFilter(tableA, AnalysisHelper.DATA_FILTER_A) : andDataFilter(tableA,
                                null));
                // ~10913
            }
        }

        return query;
    }

    /**
     * DOC Administrator Comment method "getAllRowsStatement".
     * 
     * @return
     */
    public String getAllRowsStatement() {
        Table table = (Table) indicator.getAnalyzedElement();
        return "SELECT * " + dbmsLanguage.from() + getFullyQualifiedTableName(table) + whereDataFilter(table.getName()); //$NON-NLS-1$
    }

    /**
     * 
     * DOC zshen 2010-01-15 Comment method "getdataFilterIndex".
     * 
     * @param tableOrViewName the name of table or view.
     * @return the index for datafilter. return -1 when can't find
     */
    private int getdataFilterIndex(String tableOrViewName) {
        Iterator<Indicator> iter = this.analysis.getResults().getIndicators().iterator();
        int result = 0;
        while (iter.hasNext()) {
            Indicator indicator = iter.next();
            String currentName = indicator.getAnalyzedElement().getName();
            if (currentName.equals(tableOrViewName)) {
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
    public String andDataFilter(String tableOrViewNameA, Integer index) {
        String andTable = null;
        if (index == null) {
            andTable = AnalysisHelper.getStringDataFilter(analysis, getdataFilterIndex(tableOrViewNameA));
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
     * @param tableOrViewName the name of table or view
     * @return DataFilter clause
     */
    public String whereDataFilter(String tableOrViewName) {
        String andTable = AnalysisHelper.getStringDataFilter(analysis, getdataFilterIndex(tableOrViewName));
        if (null != andTable && !andTable.equals("")) {
            andTable = dbmsLanguage.where() + andTable;
        }
        if (andTable == null)
            andTable = "";
        return andTable;

    }
}
