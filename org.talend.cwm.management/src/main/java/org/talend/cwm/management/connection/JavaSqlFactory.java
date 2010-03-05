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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
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
     * Method "createConnection".
     * 
     * @see JavaSqlFactory#createConnection(TdProviderConnection).
     * @param dataProvider to get the provider connection and create the connection
     * @return the created connection.
     */
    public static TypedReturnCode<Connection> createConnection(TdDataProvider dataProvider) {
        assert dataProvider != null;
        TypedReturnCode<TdProviderConnection> rc = DataProviderHelper.getTdProviderConnection(dataProvider);
        if (!rc.isOk()) {
            log.error(rc.getMessage());
            TypedReturnCode<Connection> rcConn = new TypedReturnCode<Connection>();

            rcConn.setReturnCode(rc.getMessage(), false);
            return rcConn;
        }

        return JavaSqlFactory.createConnection(rc.getObject());
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
     */
    public static TypedReturnCode<Connection> createConnection(TdProviderConnection providerConnection) {
        TypedReturnCode<Connection> rc = new TypedReturnCode<Connection>(false);
        String url = providerConnection.getConnectionString();
        if (url == null) {
            rc.setMessage(Messages.getString("JavaSqlFactory.DatabaseConnectionNull")); //$NON-NLS-1$
            rc.setOk(false);
        }
        String driverClassName = providerConnection.getDriverClassName();
        Collection<TaggedValue> taggedValues = providerConnection.getTaggedValue();
        Properties props = new Properties();
        props.put(TaggedValueHelper.USER, DataProviderHelper.getUser(providerConnection));
        props.put(TaggedValueHelper.PASSWORD, DataProviderHelper.getClearTextPassword(providerConnection));
        // hcheng decrypt password here and update props object
        String pass = props.getProperty(TaggedValueHelper.PASSWORD);
        if (pass != null) {
            String clearTextPassword = DataProviderHelper.getClearTextPassword(providerConnection);
            if (clearTextPassword == null) {
                rc.setMessage(Messages.getString("JavaSqlFactory.UnableDecryptPassword")); //$NON-NLS-1$
                rc.setOk(false);
            } else {
                props.setProperty(TaggedValueHelper.PASSWORD, clearTextPassword);
            }
        }
        try {
            Connection connection = ConnectionUtils.createConnection(url, driverClassName, props);
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

}
