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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.wizard.urlsetup.URLSetupControl;
import org.talend.dataprofiler.core.ui.wizard.urlsetup.URLSetupControlFactory;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;

/**
 * DOC yyi 2009-09-11 Feature:9030
 */
public class UrlEditDialog extends TrayDialog {

    private URLSetupControl urlSetupControl;

    private Connection tdDataProvider;

    private DBConnectionParameter connectionParam;

    /**
     * DOC yyi UrlEditDialog constructor comment.
     * 
     * @param shell
     * @param tdDataProvider
     */
    public UrlEditDialog(Shell shell, Connection tdDataProvider) {
        super(shell);
        this.tdDataProvider = tdDataProvider;
        setShellStyle(getShellStyle() | SWT.MAX | SWT.RESIZE);
    }

    protected Control createDialogArea(Composite parent) {

        Composite comp = (Composite) super.createDialogArea(parent);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = 600;
        comp.setLayoutData(data);

        Connection connection = SwitchHelpers.CONNECTION_SWITCH.doSwitch(tdDataProvider);

        SupportDBUrlType dbUrlType = null;
        String type = "";
        if (connection instanceof DatabaseConnection) {
            type = ((DatabaseConnection) connection).getDatabaseType();
        } else if (connection instanceof MDMConnection) {
            type = SupportDBUrlType.MDM.getDBName();
        }
        if (null != type && PluginConstant.EMPTY_STRING.equals(type)) {
            // MOD mzhao bug 12313, 2010-04-02 There is no dbType in prv files before 4.0 release, here use driver
            // class
            dbUrlType = SupportDBUrlStore.getInstance().getDBUrlTypeByDriverName(ConnectionHelper.getDriverClass(connection));
        } else {
            dbUrlType = SupportDBUrlStore.getInstance().getDBUrlType(type);
        }
        urlSetupControl = URLSetupControlFactory.createEditControl(dbUrlType, comp, connection,
                createConnectionParam(tdDataProvider));
        urlSetupControl.setConnectionURL(ConnectionHelper.getURL(connection));

        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        this.urlSetupControl.setLayoutData(data);
        comp.layout();
        return comp;
    }

    private DBConnectionParameter createConnectionParam(DataProvider dataProvider) {
        connectionParam = new DBConnectionParameter();

        Connection connection = SwitchHelpers.CONNECTION_SWITCH.doSwitch(dataProvider);
        Properties properties = new Properties();
        properties.setProperty(TaggedValueHelper.USER, ConnectionHelper.getUsername(connection));
        properties.setProperty(TaggedValueHelper.PASSWORD, ConnectionHelper.getPassword(connection));
        connectionParam.setParameters(properties);
        connectionParam.setName(dataProvider.getName());
        connectionParam.setAuthor(MetadataHelper.getAuthor(dataProvider));
        connectionParam.setDescription(MetadataHelper.getDescription(dataProvider));
        connectionParam.setPurpose(MetadataHelper.getPurpose(dataProvider));
        connectionParam.setStatus(MetadataHelper.getDevStatus(dataProvider));
        connectionParam.setDriverPath("");
        connectionParam.setDriverClassName(ConnectionHelper.getDriverClass(connection));
        connectionParam.setJdbcUrl(ConnectionHelper.getURL(connection));
        connectionParam.setHost(ConnectionHelper.getServerName(connection));
        connectionParam.setPort(ConnectionHelper.getPort(connection));
        connectionParam.getParameters().setProperty(TaggedValueHelper.UNIVERSE, ConnectionHelper.getUniverse(connection));
        connectionParam.setDbName(ConnectionHelper.getSID(connection));

        return connectionParam;
    }

    private DBConnectionParameter getConnectionParam() {
        return connectionParam;
    }

    @Override
    public void create() {
        super.create();
        getShell().setText("Edit Connection URL");
        if (null == urlSetupControl)
            getButton(MessageDialog.OK).setEnabled(false);

    }

    public DBConnectionParameter getResult() {
        connectionParam.setJdbcUrl(urlSetupControl.getConnectionURL());
        return getConnectionParam();
    }

}
