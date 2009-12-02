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
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.management.i18n.Messages;
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
     * DOC Administrator Comment method "getRowsNotMatchStatement".
     * 
     * @return
     */
    public String getRowsNotMatchStatement() {

        String queryMatch = getRowsMatchStatement();
        String query = null;
        query = queryMatch.replace(dbmsLanguage.in(), " NOT IN "); //$NON-NLS-1$
        if (query.indexOf(dbmsLanguage.and()) > 0) {
            query = query.replace(dbmsLanguage.and(), dbmsLanguage.or());
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
                String clause = "SELECT " + fullColumnName + dbmsLanguage.from() + getFullyQualifiedTableName(tableb)
                        + dbmsLanguage.where() + fullColumnName + dbmsLanguage.isNotNull();
                query += where + dbmsLanguage.quote(tableA) + "." + dbmsLanguage.quote(columnSetA.get(i).getName()) //$NON-NLS-1$
                        + dbmsLanguage.in() + inBrackets(clause);
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
        return "SELECT * " + dbmsLanguage.from() + getFullyQualifiedTableName(table); //$NON-NLS-1$
    }

}
