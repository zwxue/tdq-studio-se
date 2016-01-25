// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * ADD gdbu 2011-6-10 bug : 21273
 * 
 * DOC gdbu class global comment. Detailled comment
 */
public class ConnectionPool {

    private TypedReturnCode<Connection> trcConn;

    private int initialConnections = 10; // The size of the initial value

    private int incrementalConnections = 5;// Automatically increase the size of the connection pool

    private int maxConnections = 50; // The maximum size of connection pool

    private Vector<PooledConnection> connections = null; // Store the database connection pool connection vector ,the
                                                         // initial value is null

    private static Logger log = Logger.getLogger(ConnectionPool.class);

    /**
     * To creat a new connection pool. Need to pass parameters TypedReturnCode.
     * 
     */
    public ConnectionPool(TypedReturnCode<Connection> trcConn) {
        this.trcConn = trcConn;
        log.info("Create a new connection pool...");//$NON-NLS-1$
    }

    /**
     * DOC gdbu Comment method "isOK".
     * 
     * Incoming connection is available.(trcConn)
     * 
     * @return
     */
    public Boolean isOK() {
        if (null == trcConn) {
            return false;
        }
        return trcConn.isOk();
    }

    /**
     * DOC gdbu Comment method "createPool".
     * 
     * Create a database connection pool, connection pool of available number of connections set by class members the
     * value of initialConnections.
     * 
     * @throws Exception
     */
    public synchronized void createPool() throws Exception {
        if (connections != null) {
            return;
        }

        connections = new Vector<PooledConnection>();

        createConnections(this.initialConnections);
        log.info("create connections and add those connections to the connection pool.");//$NON-NLS-1$
    }

    /**
     * DOC gdbu Comment method "createConnections".
     * 
     * To create a number of database connection.(Created by numConnections specified number of database connections and
     * connections of these connections into the vector. )
     * 
     * @param numConnections
     * @throws SQLException
     */
    private void createConnections(int numConnections) throws SQLException {
        for (int x = 0; x < numConnections; x++) {
            if (this.maxConnections > 0 && this.connections.size() >= this.maxConnections) {
                break;
            }
            connections.addElement(new PooledConnection(trcConn.getObject()));
        }
    }

    /**
     * DOC gdbu Comment method "newConnection".
     * 
     * Create a new database connection and return it.
     * 
     * @return
     * @throws SQLException
     */
    private Connection newConnection() throws SQLException {
        Connection conn = trcConn.getObject();
        if (connections.size() == 0) {
            DatabaseMetaData metaData = conn.getMetaData();
            int driverMaxConnections = metaData.getMaxConnections();
            if (driverMaxConnections > 0 && this.maxConnections > driverMaxConnections) {
                this.maxConnections = driverMaxConnections;
            }
        }
        return conn;
    }

    /**
     * DOC gdbu Comment method "getConnection".
     * 
     * By calling getFreeConnection () function returns a usable database connection, if the current database connection
     * is not available, and more database connections can not be created (such as the connection pool size limit), this
     * function will wait a moment,and then try to get again.
     * 
     * @return
     * @throws SQLException
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connections == null) {
            return null;
        }
        Connection conn = getFreeConnection();
        while (conn == null) {
            wait(250);
            conn = getFreeConnection();
        }
        return conn;
    }

    /**
     * DOC gdbu Comment method "getFreeConnection".
     * 
     * The function vector connections from the connection pool to return a usable database connection, if the current
     * database connection is not available, this function is set according to the value of incrementalConnections
     * create several database connections, and placed in the pool. If created, all the connections are still are in
     * use, it returns null.
     * 
     * @return
     * @throws SQLException
     */
    private Connection getFreeConnection() throws SQLException {
        Connection conn = findFreeConnection();
        if (conn == null) {
            createConnections(incrementalConnections);
            conn = findFreeConnection();
            if (conn == null) {
                return null;
            }
        }
        return conn;
    }

    /**
     * DOC gdbu Comment method "findFreeConnection".
     * 
     * Find all the connections from the connection pool to find an available database connection, if the connection is
     * not available, return null.
     * 
     * @return
     * @throws SQLException
     */
    private Connection findFreeConnection() throws SQLException {
        Connection conn = null;
        PooledConnection pConn = null;
        Enumeration<PooledConnection> enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            pConn = (PooledConnection) enumerate.nextElement();
            if (!pConn.isBusy()) {
                conn = pConn.getConnection();
                pConn.setBusy(true);
                if (!testConnection(conn)) {
                    try {
                        conn = newConnection();
                    } catch (SQLException e) {
                        return null;
                    }
                    pConn.setConnection(conn);
                }
                break;
            }
        }
        return conn;
    }

    /**
     * 
     * DOC gdbu Comment method "testConnection".
     * 
     * Test a connection is available, and if not available, turn it off and return false otherwise return true.
     * 
     * @param conn
     * @return
     * @throws SQLException
     */
    private boolean testConnection(Connection conn) throws SQLException {
        if (!conn.isClosed()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * DOC gdbu Comment method "returnConnection".
     * 
     * This function returns a database connection to the connection pool, and to the connection set to idle. (When the
     * connection is not being used, we should change the connection release.)
     * 
     * @param conn
     */
    public void returnConnection(Connection conn) {
        if (connections == null) {
            return;
        }

        PooledConnection pConn = null;

        Enumeration<PooledConnection> enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            pConn = (PooledConnection) enumerate.nextElement();
            if (conn == pConn.getConnection()) {
                pConn.setBusy(false);
                break;
            }
        }
    }

    /**
     * DOC gdbu Comment method "closeConnectionPool".
     * 
     * Close and clear the connection pool. If the connection is busy, there will be a second delay time, and then
     * forced to close the connection.
     * 
     * @throws SQLException
     */
    public synchronized void closeConnectionPool() throws SQLException {

        if (connections == null) {
            return;
        }

        PooledConnection pConn = null;
        Enumeration<PooledConnection> enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            pConn = (PooledConnection) enumerate.nextElement();
            if (pConn.isBusy()) {
                wait(250);
            }
            closeConnection(pConn.getConnection());
            connections.removeElement(pConn);
        }
        connections = null;
        log.info("Close connections and connection pool...");//$NON-NLS-1$
    }

    /**
     * DOC gdbu Comment method "closeConnection".
     * 
     * close connection.
     * 
     * @param conn
     */
    private void closeConnection(Connection conn) {
        ConnectionUtils.closeConnection(conn);
    }

    /**
     * DOC gdbu Comment method "wait".
     * 
     * Make the program a given number of milliseconds to wait for.
     * 
     * @param mSeconds
     */
    private void wait(int mSeconds) {
        try {
            Thread.sleep(mSeconds);
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    /**
     * ADD gdbu 2011-6-3 bug : 21273
     * 
     * DOC gdbu ConnectionPool class global comment. Detailled comment
     * 
     * The internal connection pool used by the connection object used to hold the class there are two members of such a
     * connection to the database, and the other is to indicate whether this connection is to use.
     * 
     */
    class PooledConnection {

        Connection connection = null;

        boolean busy = false;

        public PooledConnection(Connection connection) {
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
