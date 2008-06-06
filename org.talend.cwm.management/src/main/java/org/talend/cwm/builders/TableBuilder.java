// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.builders;

import java.sql.Connection;

import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdTable;
import org.talend.utils.sql.metadata.constants.TableType;

/**
 * @author scorreia
 * 
 * A class for creating Tables from a connection. By default, no column is retrieved.
 */
public class TableBuilder extends AbstractTableBuilder<TdTable> {

    /**
     * TableBuilder constructor.
     * 
     * @param conn the connection from which the tables will be created.
     */
    public TableBuilder(Connection conn) {
        super(conn, TableType.TABLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.builders.AbstractTableBuilder#createTable()
     */
    @Override
    protected TdTable createTable() {
        return RelationalFactory.eINSTANCE.createTdTable();
    }

}
