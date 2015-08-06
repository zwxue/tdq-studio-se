// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlStore;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.wizard.urlsetup.URLSetupControl;
import org.talend.dataprofiler.core.ui.wizard.urlsetup.URLSetupControlFactory;
import org.talend.dq.analysis.parameters.DBConnectionParameter;

/**
 * DOC yyi 2009-09-11 Feature:9030.
 */
public class UrlEditDialog extends TrayDialog {

    private URLSetupControl urlSetupControl;

    private Connection tdDataProvider;

    private DBConnectionParameter connectionParam;

    private boolean isMDM;

    /**
     * DOC yyi UrlEditDialog constructor comment.
     * 
     * @param shell
     * @param tdDataProvider
     */
    public UrlEditDialog(Shell shell, Connection tdDataProvider) {
        super(shell);
        // MOD xqliu 2010-07-07 bug 13826
        this.isMDM = tdDataProvider instanceof MDMConnection;
        this.tdDataProvider = isMDM ? (MDMConnection) tdDataProvider : (DatabaseConnection) tdDataProvider;
        // ~ 13826
        setShellStyle(getShellStyle() | SWT.MAX | SWT.RESIZE);
    }

    protected Control createDialogArea(Composite parent) {

        Composite comp = (Composite) super.createDialogArea(parent);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = 600;
        comp.setLayoutData(data);


        SupportDBUrlType dbUrlType = null;
        String type = ConnectionUtils.getDatabaseType(tdDataProvider);
        if (null != type && PluginConstant.EMPTY_STRING.equals(type)) {
            // MOD mzhao bug 12313, 2010-04-02 There is no dbType in prv files before 4.0 release, here use driver
            // class
            dbUrlType = SupportDBUrlStore.getInstance().getDBUrlTypeByDriverName(JavaSqlFactory.getDriverClass(tdDataProvider));
        } else {
            dbUrlType = SupportDBUrlStore.getInstance().getDBUrlType(type);
        }
        urlSetupControl = URLSetupControlFactory.createEditControl(dbUrlType, comp, tdDataProvider,
                createConnectionParam(tdDataProvider));
        urlSetupControl.setConnectionURL(JavaSqlFactory.getURL(tdDataProvider));

        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        this.urlSetupControl.setLayoutData(data);
        comp.layout();
        return comp;
    }

    private DBConnectionParameter createConnectionParam(Connection tdDataProvider2) {
        connectionParam = ConnectionUtils.createConnectionParam(tdDataProvider2);
        return connectionParam;
    }

    private DBConnectionParameter getConnectionParam() {
        return connectionParam;
    }

    @Override
    public void create() {
        super.create();
        getShell().setText("Edit Connection URL");//$NON-NLS-1$
        if (null == urlSetupControl)
            getButton(MessageDialog.OK).setEnabled(false);

    }

    public DBConnectionParameter getResult() {
        connectionParam.setJdbcUrl(urlSetupControl.getConnectionURL());
        return getConnectionParam();
    }
}