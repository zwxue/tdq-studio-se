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
package org.talend.cwm.db.connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.helper.AnalysisExecutorHelper;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC yyin  class global comment. Detailled comment
 */
public class DatabaseSQLExecutor implements ISQLExecutor {

    private static Logger log = Logger.getLogger(DatabaseSQLExecutor.class);

    private int limit;

    /* (non-Javadoc)
     * @see org.talend.cwm.db.connection.ISQLExecutor#executeQuery(org.talend.dataquality.analysis.Analysis)
     */
    public List<Object[]> executeQuery(Analysis analysis) throws SQLException {
        List<Object[]> dataFromTable = new ArrayList<Object[]>();
        DataManager dataprovider = analysis.getContext().getConnection();
        int columnListSize = analysis.getContext().getAnalysedElements().size();

        TypedReturnCode<java.sql.Connection> sqlconnection = JavaSqlFactory.createConnection((Connection) dataprovider);
        if (!sqlconnection.isOk()) {
            throw new SQLException(sqlconnection.getMessage());
        }
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = sqlconnection.getObject().createStatement();
            statement.execute(createSqlStatement(analysis));
            resultSet = statement.getResultSet();

            while (resultSet.next()) {
                Object[] oneRow = new Object[columnListSize];
                // --- for each column
                for (int i = 0; i < columnListSize; i++) {
                    // --- get content of column
                    oneRow[i] = resultSet.getObject(i + 1);
                }
                dataFromTable.add(oneRow);
            }
        } catch (SQLException e) {
            log.error(e, e);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            ReturnCode closed = ConnectionUtils.closeConnection(sqlconnection.getObject());
            if (!closed.isOk()) {
                log.warn(closed.getMessage());
            }
        }
        return dataFromTable;
    }

    private String createSqlStatement(Analysis analysis) {
        DbmsLanguage dbms = createDbmsLanguage(analysis);
        EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();
        StringBuilder sql = new StringBuilder("SELECT ");//$NON-NLS-1$
        final Iterator<ModelElement> iterator = analysedElements.iterator();
        while (iterator.hasNext()) {
            ModelElement modelElement = iterator.next();
            TdColumn col = SwitchHelpers.COLUMN_SWITCH.doSwitch(modelElement);
            sql.append(dbms.quote(col.getName()));
            // append comma if more columns exist
            if (iterator.hasNext()) {
                sql.append(',');//$NON-NLS-1$
            }
        }

        sql.append(dbms.from());
        sql.append(AnalysisExecutorHelper.getTableName(analysedElements.get(0), dbms));
        String completedSqlString = sql.toString();
        return dbms.getTopNQuery(completedSqlString, limit);

    }

    private DbmsLanguage createDbmsLanguage(Analysis analysis) {
        DataManager connection = analysis.getContext().getConnection();
        return DbmsLanguageFactory.createDbmsLanguage(connection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.db.connection.ISQLExecutor#setLimit(int)
     */
    public void setLimit(int limit) {
        this.limit = limit;

    }
}
