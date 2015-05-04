// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * created by talend on Jan 15, 2015 Detailled comment
 *
 */
public class DatabaseResultSet extends TalendResultSet {

    private ResultSet rs = null;

    private static final String NULLDATE = "0000-00-00 00:00:00"; //$NON-NLS-1$

    public DatabaseResultSet(ResultSet resultSet) {
        this.rs = resultSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.talendResultSet.TalendResultSet#absolute(int)
     */
    @Override
    public boolean absolute(int rowIndex) {
        try {
            return rs.absolute(rowIndex);
        } catch (SQLException e) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.talendResultSet.TalendResultSet#getObject(java.lang.String)
     */
    @Override
    public Object getObject(String columnName) throws SQLException {
        Object object = null;
        try {
            object = rs.getObject(columnName);
        } catch (SQLException e) {
            if (NULLDATE.equals(rs.getString(columnName))) {
                object = null;
            } else {
                throw e;
            }

        }
        return object;
    }

}
