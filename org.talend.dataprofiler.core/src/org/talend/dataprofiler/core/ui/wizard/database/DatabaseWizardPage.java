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
package org.talend.dataprofiler.core.ui.wizard.database;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;
import org.talend.dataprofiler.core.ui.wizard.urlsetup.URLSetupControl;
import org.talend.dataprofiler.core.ui.wizard.urlsetup.URLSetupControlFactory;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.utils.sugars.ReturnCode;

/**
 * This class reused the class in QuantumPlugin.
 * 
 */
class DatabaseWizardPage extends AbstractWizardPage {

    /* use this to paint a more helpful UI for the JDBC URL */
    private SupportDBUrlType lastTimeDBType;

    private String userid;

    private String password;

    private String connectionURL = null;

    private Label jdbcLabel;

    private Text jdbcUrl;

    private URLSetupControl urlSetupControl;

    private Composite container;

    private Button checkButton;

    private DBConnectionParameter connectionParam;

    private boolean dbTypeSwitchFlag = false;

    private PropertyChangeListener listener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent event) {
            if (PluginConstant.CONNECTION_URL_PROPERTY.equals(event.getPropertyName())) {
                DatabaseWizardPage.this.setConnectionURL((String) event.getNewValue());
                DatabaseWizardPage.this.updateButtonState();
            }
        }
    };

    public void createControl(Composite parent) {
        connectionParam = (DBConnectionParameter) getParameter();
        setPageComplete(false);

        Composite comp = new Composite(parent, SWT.NULL);

        comp.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        comp.setLayout(layout);
        layout.numColumns = 2;
        layout.verticalSpacing = 9;

        GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        data.horizontalSpan = 1;
        Composite tempComp = new Composite(comp, SWT.NULL);
        tempComp.setLayoutData(data);
        GridLayout tempLayout = new GridLayout();
        tempComp.setLayout(tempLayout);
        tempLayout.numColumns = 2;

        Label label = new Label(tempComp, SWT.NULL);
        label.setText("Login");
        Text username = new Text(tempComp, SWT.BORDER | SWT.SINGLE);

        GridData fullHorizontal = new GridData(GridData.FILL_HORIZONTAL);
        username.setLayoutData(fullHorizontal);
        username.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {
                String userId = ((Text) event.getSource()).getText();
                setUserid(userId);
                updateButtonState();
            }
        });

        label = new Label(tempComp, SWT.NULL);
        label.setText("Password");
        final Text passwordText = new Text(tempComp, SWT.BORDER | SWT.SINGLE);
        passwordText.setEchoChar('*');
        fullHorizontal = new GridData(GridData.FILL_HORIZONTAL);
        passwordText.setLayoutData(fullHorizontal);
        passwordText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {
                String passwordStr = ((Text) event.getSource()).getText();
                setPassword(passwordStr);
                updateButtonState();
            }
        });

        label = new Label(tempComp, SWT.NULL);
        label.setText("DB Type");
        final Combo dbTypeCombo = new Combo(tempComp, SWT.READ_ONLY);
        dbTypeCombo.setItems(SupportDBUrlStore.getInstance().getDBTypes());
        dbTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        dbTypeCombo.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                String selectedItem = ((Combo) e.getSource()).getText();
                setDBType(selectedItem);
                dbTypeSwitchFlag = true;
                rebuildJDBCControls(SupportDBUrlStore.getInstance().getDBUrlType(selectedItem));
            }
        });
        String defalutItem = SupportDBUrlType.MYSQLDEFAULTURL.getDBKey();
        dbTypeCombo.setText(defalutItem);
        setDBType(defalutItem);
        lastTimeDBType = SupportDBUrlStore.getInstance().getDBUrlType(dbTypeCombo.getText());

        checkButton = new Button(comp, SWT.NULL);
        GridData buttonData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        buttonData.heightHint = 25;
        buttonData.widthHint = 100;
        checkButton.setLayoutData(buttonData);
        checkButton.setText("Check");
        checkButton.setToolTipText("Check the connection");
        checkButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                ReturnCode code = checkDBConnection();
                if (code.isOk()) {
                    MessageDialog.openInformation(getShell(), "check connections", "Check connection successful.");
                } else {
                    MessageDialog.openInformation(getShell(), "check connections", "Check connection failure:"
                            + code.getMessage());
                }
            }

        });

        this.container = comp;
        setControl(comp);

        rebuildJDBCControls(SupportDBUrlType.MYSQLDEFAULTURL);

        String tempUserid = connectionParam.getParameters().getProperty(PluginConstant.USER_PROPERTY);
        if (tempUserid != null) {
            userid = tempUserid;
            username.setText(userid);
        }
        String tempPassword = connectionParam.getParameters().getProperty(org.talend.dq.PluginConstant.PASSWORD_PROPERTY);
        if (tempPassword != null) {
            password = tempPassword;
            passwordText.setText(password);
        }
        String tempURL = connectionParam.getJdbcUrl();
        if (tempURL != null) {
            connectionURL = tempURL;
            // passwordText.setText(password);
        } else {
            // System.out.println("NULL URL");
        }
    }

    /**
     * @param container
     */
    private void createStandardJDBCWidgets(Composite container) {
        setConnectionURL(""); //$NON-NLS-1$

        this.jdbcLabel = new Label(container, SWT.NULL);
        this.jdbcLabel.setText("url"); //$NON-NLS-1$

        this.jdbcUrl = new Text(container, SWT.BORDER | SWT.SINGLE);
        this.jdbcUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.jdbcUrl.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {
                setConnectionURL(((Text) event.getSource()).getText());
                updateButtonState();
            }
        });

        updateButtonState();
    }

    private ReturnCode checkDBConnection() {
        ReturnCode returnCode = ConnectionService.checkConnection(this.connectionParam.getJdbcUrl(), this.connectionParam
                .getDriverClassName(), this.connectionParam.getParameters());
        return returnCode;
    }

    /**
     * 
     */
    private void rebuildJDBCControls(SupportDBUrlType dbType) {
        Point windowSize = getShell().getSize();
        Point oldSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);

        if (URLSetupControlFactory.hasControl(dbType)) {
            disposeOfCurrentJDBCControls();

            this.urlSetupControl = URLSetupControlFactory.create(dbType, this.container);
            GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
            data.horizontalSpan = 2;
            this.urlSetupControl.setLayoutData(data);

            if (connectionURL != null && !dbTypeSwitchFlag) {
                urlSetupControl.setConnectionURL(connectionURL);
            } else {
                setConnectionURL(this.urlSetupControl.getConnectionURL());
            }

            this.urlSetupControl.addPropertyChangeListener(this.listener);

            resizeWindow(windowSize, oldSize);

        } else if (this.jdbcLabel == null || this.jdbcUrl == null) {

            disposeOfCurrentJDBCControls();
            createStandardJDBCWidgets(this.container);

            resizeWindow(windowSize, oldSize);

        }

        this.container.layout();
        this.container.setVisible(true);
        this.container.redraw();
        updateButtonState();
    }

    /**
     * @param windowSize
     * @param oldSize
     */
    private void resizeWindow(Point windowSize, Point oldSize) {
        Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if (newSize.y > windowSize.y) {
            getShell().setSize(new Point(windowSize.x, windowSize.y + (newSize.y - oldSize.y)));
        }
    }

    private void disposeOfCurrentJDBCControls() {
        if (this.jdbcUrl != null) {
            this.jdbcUrl.dispose();
            this.jdbcUrl = null;
        }
        if (this.jdbcLabel != null) {
            this.jdbcLabel.dispose();
            this.jdbcLabel = null;
        }
        if (this.urlSetupControl != null) {
            this.urlSetupControl.removePropertyChangeListener(this.listener);
            this.urlSetupControl.dispose();
            this.urlSetupControl = null;
        }
        // if (this.checkButton != null) {
        // this.checkButton.dispose();
        // this.checkButton = null;
        // }
    }

    /**
     * 
     */
    private void updateButtonState() {
        boolean complete = true;
        complete &= (this.connectionURL != null && this.connectionURL.trim().length() > 0 && this.userid != null && this.userid
                .trim().length() > 0);
        if (checkButton != null) {
            checkButton.setEnabled(complete);
        }
        setPageComplete(complete);
    }

    /**
     * @return Returns the userid.
     */
    public String getUserid() {
        return this.userid;
    }

    /**
     * @param userid The userid to set.
     */
    public void setUserid(String userid) {
        if (userid != null && !userid.equals(this.userid)) {
            this.userid = userid;
            this.connectionParam.getParameters().setProperty("user", userid);
        }
    }

    public void setDBType(String dbType) {
        this.connectionParam.setDriverClassName(SupportDBUrlStore.getInstance().getDBUrlType(dbType).getDbDriver());
    }

    /**
     * @param connectionURL The connectionURL to set.
     */
    private void setConnectionURL(String connectionURL) {
        if (connectionURL != null && !connectionURL.equals(this.connectionURL)) {
            this.connectionURL = connectionURL;
            this.connectionParam.setJdbcUrl(connectionURL);
        }
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        if (password != null && !password.equals(this.password)) {
            this.password = password;
            this.connectionParam.getParameters().setProperty("password", password);
        }
    }

    @Override
    public void dispose() {
        if (this.checkButton != null) {
            this.checkButton.dispose();
            this.checkButton = null;
        }
    }

}
