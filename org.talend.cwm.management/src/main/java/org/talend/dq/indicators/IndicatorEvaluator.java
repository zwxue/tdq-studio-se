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
package org.talend.dq.indicators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.talend.dataquality.indicators.Indicator;
import org.talend.utils.sugars.ReturnCode;

/**
 * @author scorreia
 * 
 * Computes indicators on columns.
 */
public class IndicatorEvaluator extends Evaluator<String> {

    private static Logger log = Logger.getLogger(IndicatorEvaluator.class);

    protected ReturnCode executeSqlQuery(String sqlStatement) throws SQLException {
        ReturnCode ok = new ReturnCode(true);
        // check analyzed columns
        Set<String> columns = getAnalyzedElements();
        if (columns.isEmpty()) {
            ok.setReturnCode("No column to analyze found? Define the analyzed columns properly, please.", false);
            return ok;
        }

        // create query statement
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                ResultSet.CLOSE_CURSORS_AT_COMMIT);
        statement.setFetchSize(fetchSize);
        // MOD xqliu 2009-02-09 bug 6237
        if (continueRun()) {
            statement.execute(sqlStatement);
        }

        // get the results
        ResultSet resultSet = statement.getResultSet();
        if (resultSet == null) {
            String mess = "No result set for this statement: " + sqlStatement;
            log.warn(mess);
            ok.setReturnCode(mess, false);
            return ok;
        }
        label: while (resultSet.next()) {

            // --- for each column
            for (String col : columns) {
                List<Indicator> indicators = getIndicators(col);

                // --- get content of column
                Object object = resultSet.getObject(col);

                // --- give row to handle to indicators
                for (Indicator indicator : indicators) {
                    // MOD xqliu 2009-02-09 bug 6237
                    if (!continueRun()) {
                        break label;
                    }
                    indicator.handle(object);
                }
            }

            // TODO scorreia give a full row to indicator (indicator will have to handle its data??

        }
        // --- release resultset
        resultSet.close();
        // --- close
        connection.close();
        return ok;
    }

}
