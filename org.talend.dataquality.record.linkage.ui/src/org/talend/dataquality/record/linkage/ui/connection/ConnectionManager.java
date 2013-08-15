// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.connection;

import java.util.ArrayList;
import java.util.List;

import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC yyin  class global comment. Detailled comment
 */
public class ConnectionManager {

    /**
     * execute the query of the sql from the connection
     * 
     * @param connection
     * @param sql
     * @return
     */
    public static List<Object[]> executeQuery(DataManager connection, String sql) {
        List<Object[]> dataFromTable = new ArrayList<Object[]>();
        dataFromTable.add(new String[] { "1", "a", "aa", "bb", "cc", "" });
        dataFromTable.add(new String[] { "2", "b", "bb1", "bb2", "bb3", "" });
        dataFromTable.add(new String[] { "3", "c", "cc1", "cc2", "cc3", "" });
        return dataFromTable;
    }

    /**
     * if the column is db: if the column is MDM: if the column is file:
     * 
     * create the sql by the given columns
     * 
     * @param columns
     * @return
     */
    public static String createSqlStatement(ModelElement[] columns) {
        if (columns == null || columns.length < 1) {
            return null;
        }
        StringBuilder sql = new StringBuilder("SELECT '");//$NON-NLS-1$
        if (columns[0] instanceof TdColumn) {
            ColumnSet columnSetOwner = ColumnHelper.getColumnOwnerAsColumnSet((TdColumn) columns[0]);

            for (int i = 0; i < columns.length; i++) {
                TdColumn column = (TdColumn) columns[i];
            sql.append(column.getName());
            sql.append(',');
        }
        sql.append("from ");
        sql.append(columnSetOwner);
        return sql.toString();
        } else {

        }
        return sql.toString();
    }


}
