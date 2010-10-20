// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.management.connection;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * @author scorreia
 * 
 * This utility class provides methods that convert CWM object into java.sql object. It is a kind of inverse of the
 * DatabaseContentRetriever class.
 */
public final class JavaSqlFactory {

    private static Logger log = Logger.getLogger(JavaSqlFactory.class);

    private JavaSqlFactory() {
    }

    /**
     * Method "createConnection" returns the connection with {@link ReturnCode#getObject()} if {@link ReturnCode#isOk()}
     * is true. This is the behaviour when everything goes ok.
     * <p>
     * When something goes wrong, {@link ReturnCode#isOk()} is false and {@link ReturnCode#getMessage()} gives the error
     * message.
     * <p>
     * The created connection must be closed by the caller. (use {@link ConnectionUtils#closeConnection(Connection)})
     * 
     * @param providerConnection the provider connection
     * @return a ReturnCode (never null)
     * @deprecated
     */
    public static TypedReturnCode<java.sql.Connection> createConnection(DatabaseConnection providerConnection) {
        TypedReturnCode<java.sql.Connection> rc = new TypedReturnCode<java.sql.Connection>(false);
        String url = providerConnection.getURL();
        if (url == null) {
            rc.setMessage(Messages.getString("JavaSqlFactory.DatabaseConnectionNull")); //$NON-NLS-1$
            rc.setOk(false);
        }
        String driverClassName = providerConnection.getDriverClass();
        Collection<TaggedValue> taggedValues = providerConnection.getTaggedValue();
        Properties props = new Properties();
        props.put(TaggedValueHelper.USER, providerConnection.getUsername());
        props.put(TaggedValueHelper.PASSWORD, providerConnection.getPassword());
        String pass = props.getProperty(TaggedValueHelper.PASSWORD);
        if (pass != null) {
            String clearTextPassword = providerConnection.getPassword();
            if (clearTextPassword == null) {
                rc.setMessage(Messages.getString("JavaSqlFactory.UnableDecryptPassword")); //$NON-NLS-1$
                rc.setOk(false);
            } else {
                props.setProperty(TaggedValueHelper.PASSWORD, clearTextPassword);
            }
        }
        try {
            java.sql.Connection connection = ConnectionUtils.createConnection(url, driverClassName, props);
            rc.setObject(connection);
            rc.setOk(true);
        } catch (SQLException e) {
            rc.setReturnCode(e.getMessage(), false);
        } catch (InstantiationException e) {
            rc.setReturnCode(e.getMessage(), false);
        } catch (IllegalAccessException e) {
            rc.setReturnCode(e.getMessage(), false);
        } catch (ClassNotFoundException e) {
            rc.setReturnCode(e.getMessage(), false);
        }
        return rc;
    }

    /**
     * Method "createConnection" returns the connection with {@link ReturnCode#getObject()} if {@link ReturnCode#isOk()}
     * is true. This is the behaviour when everything goes ok.
     * <p>
     * When something goes wrong, {@link ReturnCode#isOk()} is false and {@link ReturnCode#getMessage()} gives the error
     * message.
     * <p>
     * The created connection must be closed by the caller. (use {@link ConnectionUtils#closeConnection(Connection)})
     * 
     * @param connection the connection (DatabaseConnection MDMConnection or others)
     * @return a ReturnCode (never null)
     */
    public static TypedReturnCode<java.sql.Connection> createConnection(Connection connection) {
        TypedReturnCode<java.sql.Connection> rc = new TypedReturnCode<java.sql.Connection>(false);
        String url = ConnectionUtils.getURL(connection);
        if (url == null) {
            rc.setMessage(Messages.getString("JavaSqlFactory.DatabaseConnectionNull")); //$NON-NLS-1$
            rc.setOk(false);
            return rc; // MOD scorreia 2010-10-20 bug 16403 avoid NPE while importing items
        }
        String driverClassName = ConnectionUtils.getDriverClass(connection);
        // MOD scorreia 2010-10-20 bug 16403 avoid NPE while importing items
        if (StringUtils.isEmpty(driverClassName)) {
            rc.setMessage("No classname given to find the driver");
            rc.setOk(false);
            return rc;
        }
        Properties props = new Properties();
        // MOD xqliu 2010-08-06 bug 14593
        props.put(TaggedValueHelper.USER, ConnectionUtils.getUsernameDefault(connection));
        props.put(TaggedValueHelper.PASSWORD, ConnectionUtils.getPasswordDefault(connection));
        // ~ 14593
        try {
            java.sql.Connection sqlConnection = ConnectionUtils.createConnection(url, driverClassName, props);
            rc.setObject(sqlConnection);
            rc.setOk(true);
        } catch (SQLException e) {
            rc.setReturnCode(e.getMessage(), false);
        } catch (InstantiationException e) {
            rc.setReturnCode(e.getMessage(), false);
        } catch (IllegalAccessException e) {
            rc.setReturnCode(e.getMessage(), false);
        } catch (ClassNotFoundException e) {
            rc.setReturnCode(e.getMessage(), false);
        }
        return rc;
    }
}
