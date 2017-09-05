// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.db.connection.talendResultSet;

import java.sql.SQLException;

/**
 * created by talend on Jan 15, 2015 Detailled comment.
 *
 */
public interface ITalendResultSet {

    /**
     * 
     * Move the cursor to special row
     * 
     * @param rowIndex the index of row which we want
     * @return true if the action is success else false
     */
    public boolean absolute(int rowIndex);

    /**
     * 
     * Get the data for special column
     * 
     * @param columnName the name of special column
     * @return the content of column
     * @throws SQLException
     */
    public Object getObject(String columnName) throws SQLException;
}
