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

import org.talend.i18n.Messages;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.resource.relational.Table;

/**
 * DOC hcheng class global comment. Detailled comment
 */
public class RowMatchExplorer extends DataExplorer {

    private static RowMatchExplorer instance = null;

    public static RowMatchExplorer getInstance() {
        if (instance == null) {
            instance = new RowMatchExplorer();
        }
        return instance;
    }

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
        Table table = (Table) indicator.getAnalyzedElement();
        int i = getFullyQualifiedTableName(table).indexOf("."); //$NON-NLS-1$
        String dbname = getFullyQualifiedTableName(table).substring(0, i + 1);

        Expression instantiatedExpression = dbmsLanguage.getInstantiatedExpression(indicator);
        String instantiatedSQL = instantiatedExpression.getBody();
        if (instantiatedSQL == null) {
            return null;
        }
        int b = instantiatedSQL.indexOf(dbmsLanguage.from());

        String fromClause1 = instantiatedSQL.substring(b);
        String fromClause2 = fromClause1.replace(" LEFT JOIN ", " LEFT JOIN " + dbname); //$NON-NLS-1$ //$NON-NLS-2$
        String fromClause = fromClause2.replace(dbmsLanguage.from(), dbmsLanguage.from() + dbname);

        return "select " + table.getName() + ".*" + fromClause; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * DOC Administrator Comment method "getRowsMatchStatement".
     * 
     * @return
     */
    public String getRowsMatchStatement() {
        Table table = (Table) indicator.getAnalyzedElement();
        int i = getFullyQualifiedTableName(table).indexOf("."); //$NON-NLS-1$
        String dbname = getFullyQualifiedTableName(table).substring(0, i + 1);

        Expression instantiatedExpression = dbmsLanguage.getInstantiatedExpression(indicator);
        String instantiatedSQL = instantiatedExpression.getBody();
        if (instantiatedSQL == null) {
            return null;
        }
        int l = instantiatedSQL.indexOf(" LEFT JOIN "); //$NON-NLS-1$
        int w = instantiatedSQL.indexOf(dbmsLanguage.where());
        String whereClause = instantiatedSQL.substring(w).replace(dbmsLanguage.isNull(), dbmsLanguage.isNotNull());

        String fromClause = instantiatedSQL.substring(l, w).replace(" JOIN ", " JOIN " + dbname); //$NON-NLS-1$ //$NON-NLS-2$
        return "select * " + dbmsLanguage.from() + getFullyQualifiedTableName(table) + fromClause + whereClause; //$NON-NLS-1$
    }

    /**
     * DOC Administrator Comment method "getAllRowsStatement".
     * 
     * @return
     */
    public String getAllRowsStatement() {
        Table table = (Table) indicator.getAnalyzedElement();
        return "select * " + dbmsLanguage.from() + getFullyQualifiedTableName(table); //$NON-NLS-1$
    }

}
