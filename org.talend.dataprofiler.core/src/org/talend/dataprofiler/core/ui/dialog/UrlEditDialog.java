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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
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

    private TdDataProvider tdDataProvider;

    private DBConnectionParameter connectionParam;

    /**
     * DOC yyi UrlEditDialog constructor comment.
     * 
     * @param shell
     * @param tdDataProvider
     */
    public UrlEditDialog(Shell shell, TdDataProvider tdDataProvider) {
        super(shell);
        this.tdDataProvider = tdDataProvider;
        setShellStyle(getShellStyle() | SWT.MAX | SWT.RESIZE);
    }

    protected Control createDialogArea(Composite parent) {

        Composite comp = (Composite) super.createDialogArea(parent);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = 600;
        comp.setLayoutData(data);

        TdProviderConnection connection = DataProviderHelper.getTdProviderConnection(tdDataProvider).getObject();
        String type = DataProviderHelper.getDBType(connection);
        if (null != type && PluginConstant.EMPTY_STRING.equals(type)) {
            CLabel msgLabel = new CLabel(comp, SWT.SHADOW_NONE);
            msgLabel.setImage(Display.getCurrent().getSystemImage(SWT.ICON_WARNING));
            msgLabel.setText(DefaultMessagesImpl.getString("UrlEditDialog.CanNotEdit"));
            data = new GridData(SWT.FILL, SWT.CENTER, true, false);
            msgLabel.setLayoutData(data);
        } else {
            SupportDBUrlType dbUrlType = SupportDBUrlStore.getInstance().getDBUrlType(type);

            urlSetupControl = URLSetupControlFactory.createEditControl(dbUrlType, comp, connection,
                    createConnectionParam(tdDataProvider));
            urlSetupControl.setConnectionURL(connection.getConnectionString());

            data = new GridData(SWT.FILL, SWT.FILL, true, true);
            this.urlSetupControl.setLayoutData(data);
        }
        comp.layout();
        return comp;
    }

    private DBConnectionParameter createConnectionParam(DataProvider dataProvider) {
        connectionParam = new DBConnectionParameter();

        TdProviderConnection connection = DataProviderHelper.getTdProviderConnection(tdDataProvider).getObject();
        Properties properties = new Properties();
        properties.setProperty(TaggedValueHelper.USER, DataProviderHelper.getUser(connection));
        properties.setProperty(TaggedValueHelper.PASSWORD, DataProviderHelper.getClearTextPassword(connection));
        connectionParam.setParameters(properties);
        connectionParam.setName(dataProvider.getName());
        connectionParam.setAuthor(MetadataHelper.getAuthor(dataProvider));
        connectionParam.setDescription(MetadataHelper.getDescription(dataProvider));
        connectionParam.setPurpose(MetadataHelper.getPurpose(dataProvider));
        connectionParam.setStatus(MetadataHelper.getDevStatus(dataProvider));
        connectionParam.setDriverPath("");
        connectionParam.setDriverClassName(connection.getDriverClassName());
        connectionParam.setJdbcUrl(connection.getConnectionString());
        connectionParam.setHost(DataProviderHelper.getHost(connection));
        connectionParam.setPort(DataProviderHelper.getPort(connection));
        connectionParam.getParameters().setProperty(TaggedValueHelper.UNIVERSE, DataProviderHelper.getUniverse(connection));
        connectionParam.setDbName(DataProviderHelper.getDBName(connection));

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
