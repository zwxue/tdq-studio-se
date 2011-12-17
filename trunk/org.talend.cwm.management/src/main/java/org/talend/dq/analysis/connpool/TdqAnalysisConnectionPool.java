// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis.connpool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TdqAnalysisConnectionPool {

    private static final int MAX_CONNECTIONS = 50;

    private static final int DEFAULT_WAIT_MILLISECOND = 500;

    private static final int DEFAULT_WAIT_TIMES = 10;

    private static final float DEFAULT_CONNECTION_NUMBER_OFFSET = 0.5f;

    private static final boolean SHOW_CONNECTIONS_INFO = false;

    private static Logger log = Logger.getLogger(TdqAnalysisConnectionPool.class);

    private org.talend.core.model.metadata.builder.connection.Connection tConnection;

    private Vector<PooledTdqAnalysisConnection> pConnections;

    private int maxConnections = MAX_CONNECTIONS;

    /**
     * DOC xqliu TdqAnalysisConnectionPool constructor comment.
     * 
     * @param tConnection
     */
    public TdqAnalysisConnectionPool(org.talend.core.model.metadata.builder.connection.Connection tConnection) {
        this.setTConnection(tConnection);
    }

    /**
     * DOC xqliu Comment method "getTConnection".
     * 
     * @return
     */
    public synchronized org.talend.core.model.metadata.builder.connection.Connection getTConnection() {
        return this.tConnection;
    }

    /**
     * DOC xqliu Comment method "setTConnection".
     * 
     * @param tConnection
     */
    public synchronized void setTConnection(org.talend.core.model.metadata.builder.connection.Connection tConnection) {
        this.tConnection = tConnection;
    }

    /**
     * DOC xqliu Comment method "getPConnections".
     * 
     * @return
     */
    public synchronized Vector<PooledTdqAnalysisConnection> getPConnections() {
        if (this.pConnections == null) {
            this.pConnections = new Vector<PooledTdqAnalysisConnection>();
        }
        return this.pConnections;
    }

    /**
     * DOC xqliu Comment method "setPConnections".
     * 
     * @param pConnections
     */
    public synchronized void setPConnections(Vector<PooledTdqAnalysisConnection> pConnections) {
        this.pConnections = pConnections;
    }

    /**
     * DOC xqliu Comment method "getMaxConnections".
     * 
     * @return
     */
    public synchronized int getMaxConnections() {
        return this.maxConnections;
    }

    /**
     * DOC xqliu Comment method "setMaxConnections".
     * 
     * @param maxConnections
     */
    public synchronized void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * DOC xqliu Comment method "getConnection".
     * 
     * @return
     * @throws SQLException
     */
    public synchronized Connection getConnection() throws SQLException {
        Connection conn = findFreeConnection();
        while (conn == null) {
            wait(DEFAULT_WAIT_MILLISECOND);
            conn = findFreeConnection();
            if (conn == null) {
                newConnection();
            }
        }
        showConnectionInfo();
        return conn;
    }

    /**
     * DOC xqliu Comment method "newConnection".
     * 
     * @return
     */
    private synchronized Connection newConnection() {
        Connection conn = null;
        if (isFull()) {
            return conn;
        }

        try {
            TypedReturnCode<Connection> trcConn = JavaSqlFactory.createConnection(this.getTConnection());
            if (trcConn.isOk()) {
                conn = trcConn.getObject();
                this.getPConnections().add(new PooledTdqAnalysisConnection(conn));
            }

        } catch (Exception e) {
            log.debug(e);
        }

        try {
            DatabaseMetaData metaData = conn.getMetaData();
            int driverMaxConnections = new Float(metaData.getMaxConnections() * DEFAULT_CONNECTION_NUMBER_OFFSET).intValue();
            if (driverMaxConnections > 0 && this.getMaxConnections() > driverMaxConnections) {
                this.setMaxConnections(driverMaxConnections);
            }
        } catch (Exception e) {
            log.debug(e, e);
        }

        return conn;
    }

    /**
     * DOC xqliu Comment method "isFull".
     * 
     * @return
     */
    private synchronized boolean isFull() {
        boolean result = true;
        if (this.getMaxConnections() < 1) {
            result = false;
        } else {
            result = !(this.getPConnections().size() < this.getMaxConnections());
        }
        return result;
    }

    /**
     * DOC xqliu Comment method "findFreeConnection".
     * 
     * @return
     */
    private synchronized Connection findFreeConnection() {
        Connection conn = null;

        Enumeration<PooledTdqAnalysisConnection> enumerate = this.getPConnections().elements();
        while (enumerate.hasMoreElements()) {
            PooledTdqAnalysisConnection pConn = (PooledTdqAnalysisConnection) enumerate.nextElement();
            try {
                if (!pConn.isBusy()) {
                    Connection tempConn = pConn.getConnection();
                    if (tempConn.isClosed()) {
                        removeConnection(tempConn);
                    } else {
                        conn = tempConn;
                        pConn.setBusy(true);
                        break;
                    }
                }
            } catch (Exception e) {
                log.debug(e);
            }
        }

        return conn;
    }

    /**
     * DOC xqliu Comment method "returnConnection".
     * 
     * @param conn
     */
    public synchronized void returnConnection(Connection conn) {
        if (conn == null) {
            return;
        }

        Enumeration<PooledTdqAnalysisConnection> enumerate = this.getPConnections().elements();
        while (enumerate.hasMoreElements()) {
            PooledTdqAnalysisConnection pConn = (PooledTdqAnalysisConnection) enumerate.nextElement();
            if (conn == pConn.getConnection()) {
                pConn.setBusy(false);
                break;
            }
        }

        showConnectionInfo();
    }

    /**
     * DOC xqliu Comment method "showConnectionInfo".
     */
    public synchronized void showConnectionInfo() {
        if (SHOW_CONNECTIONS_INFO) {
            int i = 0;
            Enumeration<PooledTdqAnalysisConnection> enumerate = this.getPConnections().elements();
            try {
                boolean hasElement = false;
                while (enumerate.hasMoreElements()) {
                    hasElement = true;
                    PooledTdqAnalysisConnection pConn = (PooledTdqAnalysisConnection) enumerate.nextElement();
                    i++;
                    log.info("pConn: id=[" + i + "] conn=[" + pConn.getConnection().toString() + "] [closed="
                            + pConn.getConnection().isClosed() + "] busy=[" + pConn.isBusy() + "]");
                }
                if (!hasElement) {
                    log.info("the connection pool is empty!");
                }
            } catch (Exception e) {
                log.debug(e);
            }
        }
    }

    /**
     * DOC xqliu Comment method "refreshConnections".
     */
    public synchronized void refreshConnections() {
        Enumeration<PooledTdqAnalysisConnection> enumerate = this.getPConnections().elements();
        while (enumerate.hasMoreElements()) {
            PooledTdqAnalysisConnection pConn = (PooledTdqAnalysisConnection) enumerate.nextElement();
            int times = 0;
            busy: while (pConn.isBusy()) {
                try {
                    if (pConn.getConnection().isClosed()) {
                        break busy;
                    }
                } catch (Exception e) {
                    log.debug(e);
                }
                times++;
                wait(DEFAULT_WAIT_MILLISECOND);
                if (times > DEFAULT_WAIT_TIMES) {
                    break busy;
                }
            }

            closeConnection(pConn.getConnection());
            pConn.setConnection(newConnection());
            pConn.setBusy(false);
        }
    }

    /**
     * DOC xqliu Comment method "closeConnectionPool".
     */
    public synchronized void closeConnectionPool() {
        Enumeration<PooledTdqAnalysisConnection> enumerate = this.getPConnections().elements();
        while (enumerate.hasMoreElements()) {
            PooledTdqAnalysisConnection pConn = (PooledTdqAnalysisConnection) enumerate.nextElement();
            int times = 0;
            busy: if (pConn.isBusy()) {
                times++;
                wait(DEFAULT_WAIT_MILLISECOND);
                if (times > DEFAULT_WAIT_TIMES) {
                    break busy;
                }
            }
            closeConnection(pConn.getConnection());
            this.getPConnections().removeElement(pConn);
        }
        this.setPConnections(null);
    }

    /**
     * DOC xqliu Comment method "closeConnection".
     * 
     * @param conn
     */
    public synchronized void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (Exception e) {
                log.debug(e);
            }

            showConnectionInfo();
        }
    }

    /**
     * DOC xqliu Comment method "removeConnection".
     * 
     * @param conn
     */
    public synchronized void removeConnection(Connection conn) {
        Enumeration<PooledTdqAnalysisConnection> enumerate = this.getPConnections().elements();

        while (enumerate.hasMoreElements()) {
            PooledTdqAnalysisConnection pConn = (PooledTdqAnalysisConnection) enumerate.nextElement();
            if (pConn.getConnection().equals(conn)) {
                this.getPConnections().remove(pConn);
                break;
            }
        }

        showConnectionInfo();
    }

    /**
     * DOC xqliu Comment method "wait".
     * 
     * @param mSeconds
     */
    private void wait(int mSeconds) {
        try {
            Thread.sleep(mSeconds);
        } catch (InterruptedException e) {
            log.debug(e, e);
        }
    }

    /**
     * DOC xqliu TdqAnalysisConnectionPool class global comment. Detailled comment
     */
    class PooledTdqAnalysisConnection {

        Connection connection = null;

        boolean busy = false;

        public PooledTdqAnalysisConnection(Connection connection) {
            this.connection = connection;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        public boolean isBusy() {
            return busy;
        }

        public void setBusy(boolean busy) {
            this.busy = busy;
        }
    }
}
