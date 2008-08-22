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
package org.talend.dq.indicators;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.dataquality.indicators.Indicator;
import org.talend.utils.collections.MultiMapHelper;
import org.talend.utils.sugars.ReturnCode;

/**
 * @author scorreia
 * 
 * Abstract class for computing indicators.
 * @param <T> the type of the object identifying the analyzed element (usually a string).
 */
public abstract class Evaluator<T> {

    private static Logger log = Logger.getLogger(Evaluator.class);

    protected Connection connection;

    protected int fetchSize = 0;

    protected Map<T, List<Indicator>> elementToIndicators = new HashMap<T, List<Indicator>>();

    private Set<Indicator> allIndicators = new HashSet<Indicator>();

    /**
     * Method "storeIndicator" stores the mapping between the analyzed element name and its indicators.
     * 
     * @param elementToAnalyze the element to analyze (column, data provider...)
     * @param indicator the indicator for the given element
     * @return true if ok
     */
    public boolean storeIndicator(T elementToAnalyze, Indicator indicator) {
        this.allIndicators.add(indicator);
        return MultiMapHelper.addUniqueObjectToListMap(elementToAnalyze, indicator, elementToIndicators);
    }

    public List<Indicator> getIndicators(T elementName) {
        List<Indicator> indics = elementToIndicators.get(elementName);
        return (indics != null) ? indics : new ArrayList<Indicator>();
    }

    /**
     * Method "getAnalyzedElements".
     * 
     * @return the unmodifiable set of analyzed elements.
     */
    protected Set<T> getAnalyzedElements() {
        return Collections.unmodifiableSet(this.elementToIndicators.keySet());
    }

    /**
     * Method "evaluateIndicators". A connection must be open. It does not close the connection. It is to the caller
     * responsibility to close the connection.
     * 
     * @param sqlStatement
     * @return a return code with an error message if any problem has been encountered.
     */
    public ReturnCode evaluateIndicators(String sqlStatement) {
        ReturnCode rc = checkConnection();
        if (!rc.isOk()) {
            return rc;
        }
        try {
            if (!prepareIndicators()) {
                rc.setReturnCode("Problem when preparing all indicators", false);
                return rc;
            }
            rc = executeSqlQuery(sqlStatement);
            if (!rc.isOk()) {
                return rc;
            }
            if (!finalizeIndicators()) {
                rc.setReturnCode("Problem when finalizing all indicators", false);
            }
            return rc;
        } catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception while executing SQL query " + sqlStatement, e);
            }
            rc.setReturnCode(e.getMessage(), false);
        }
        return rc;
    }

    private boolean finalizeIndicators() {
        boolean ok = true;
        for (Indicator indic : allIndicators) {
            if (!indic.finalizeComputation()) {
                ok = false;
            }
        }
        return ok;
    }

    private boolean prepareIndicators() {
        boolean ok = true;
        for (Indicator indic : allIndicators) {
            if (!indic.prepare()) {
                ok = false;
            }
        }
        return ok;
    }

    /**
     * DOC scorreia Comment method "executeSqlQuery".
     * 
     * @param sqlStatement
     * @return
     */
    protected abstract ReturnCode executeSqlQuery(String sqlStatement) throws SQLException;

    /**
     * Method "evaluateIndicators". A connection must be open. This method does not close the connection. It is to the
     * caller responsibility to close the connection.
     * 
     * @param sqlStatement
     * @return a return code with an error message if any problem has been encountered.
     */
    public ReturnCode evaluateIndicators(String sqlStatement, boolean closeConnection) {
        ReturnCode rc = evaluateIndicators(sqlStatement);
        if (!closeConnection) {
            return rc;
        } else {
            if (rc.isOk()) {
                return closeConnection();
            } else { // problem with evaluation
                ReturnCode connRc = closeConnection();
                if (!connRc.isOk()) {
                    // add the message to returned code
                    String message = rc.getMessage();
                    message += " Connection problem:" + connRc.getMessage();
                    rc.setMessage(message);

                }
            }
            return rc;
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    protected ReturnCode closeConnection() {

        if (connection != null) {
            return ConnectionUtils.closeConnection(connection);
        }
        return new ReturnCode("Attempting to close a null connection. ", false);
    }

    protected ReturnCode checkConnection() {
        if (connection == null) {
            return new ReturnCode("Attempting to open a null connection. Set the connection parameters first.", false);
        }
        return ConnectionUtils.isValid(connection);
    }

    /**
     * Method "selectCatalog" attempts to set the catalog for the current connection.
     * 
     * @param catalogName the catalog to select
     * @return true if set, false if problem
     */
    public boolean selectCatalog(String catalogName) {
        if (connection == null) {
            return false;
        }
        try {
            connection.setCatalog(catalogName);
            return true;
        } catch (SQLException e) {
            return false;
        }

    }

}
