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

import java.sql.SQLException;

/**
 * created by talend on Jan 15, 2015 Detailled comment.
 *
 */
public class TalendResultSet implements ITalendResultSet {

    protected int currentIndex = 1;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.TalendResultSet.ITalendResultSet#absolute(int)
     */
    public boolean absolute(int rowIndex) {
        if (currentIndex == rowIndex) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.TalendResultSet.ITalendResultSet#getObject(java.lang.String)
     */
    public Object getObject(String columnName) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

}
