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
package org.talend.dataprofiler.core.ui.wizard.database;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
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
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.UIMessages;
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

    protected static Logger log = Logger.getLogger(DatabaseWizardPage.class);

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
        label.setText(DefaultMessagesImpl.getString("DatabaseWizardPage.login")); //$NON-NLS-1$
        final Text username = new Text(tempComp, SWT.BORDER | SWT.SINGLE);

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
        label.setText(DefaultMessagesImpl.getString("DatabaseWizardPage.password")); //$NON-NLS-1$
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
        label.setText(DefaultMessagesImpl.getString("DatabaseWizardPage.DBType")); //$NON-NLS-1$
        final Combo dbTypeCombo = new Combo(tempComp, SWT.READ_ONLY);
        dbTypeCombo.setItems(SupportDBUrlStore.getInstance().getDBTypes());
        dbTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        dbTypeCombo.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                if (dbTypeCombo.getText().trim().equals("Generic JDBC")) { //$NON-NLS-1$
                    updateStatus(IStatus.WARNING, UIMessages.MSG_SELECT_GENERIC_JDBC);
                }
                if (dbTypeCombo.getText().trim().equals("SQLite3")) { //$NON-NLS-1$
                    username.setEnabled(false);
                    passwordText.setEnabled(false);
                } else {
                    username.setEnabled(true);
                    passwordText.setEnabled(true);
                }
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
        checkButton.setText(DefaultMessagesImpl.getString("DatabaseWizardPage.check")); //$NON-NLS-1$
        checkButton.setToolTipText(DefaultMessagesImpl.getString("DatabaseWizardPage.checkConnection")); //$NON-NLS-1$
        checkButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                ReturnCode code = checkDBConnection();
                if (code.isOk()) {
                    MessageDialog
                            .openInformation(
                                    getShell(),
                                    DefaultMessagesImpl.getString("DatabaseWizardPage.checkConnections"), DefaultMessagesImpl.getString("DatabaseWizardPage.checkSuccessful")); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    MessageDialog
                            .openInformation(
                                    getShell(),
                                    DefaultMessagesImpl.getString("DatabaseWizardPage.checkConnectionss"), DefaultMessagesImpl.getString("DatabaseWizardPage.checkFailure") //$NON-NLS-1$ //$NON-NLS-2$
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
        if (this.connectionParam.getDriverPath() != null) {
            CorePlugin corePlugin = CorePlugin.getDefault();
            ReturnCode rc = new ReturnCode();
            // String[] driverpaths = this.connectionParam.getDriverPath().split(";");
            Driver externalDriver = createGenericJDBC(this.connectionParam.getDriverPath(), this.connectionParam
                    .getDriverClassName());
            if (externalDriver != null) {
                try {
                    DriverManager.registerDriver(externalDriver);
                    // MOD xqliu 2009-02-03 bug 5261
                    Connection connection = ConnectionUtils.createConnectionWithTimeout(externalDriver, this.connectionParam
                            .getJdbcUrl(), this.connectionParam.getParameters());
                    if (connection == null) {
                        rc.setOk(false);
                    }
                } catch (Exception e) {
                    rc.setOk(false);
                    log.error(e, e);
                }
            } else {
                rc.setOk(false);
            }
            return rc;
        } else {
            ReturnCode returnCode = ConnectionService.checkConnection(this.connectionParam.getJdbcUrl(), this.connectionParam
                    .getDriverClassName(), this.connectionParam.getParameters());
            return returnCode;
        }
    }

    private Driver createGenericJDBC(String driverJars, String driverName) {
        Driver driver = null;
        String[] driverJarPath = driverJars.split(";"); //$NON-NLS-1$
        try {
            int driverCount = 0;
            URL[] driverUrl = new URL[driverJarPath.length];
            for (String dirverpath : driverJarPath) {
                driverUrl[driverCount++] = new File(dirverpath).toURL();
            }
            URLClassLoader cl = URLClassLoader.newInstance(driverUrl, Thread.currentThread().getContextClassLoader());
            Class c = cl.loadClass(driverName);
            driver = (Driver) c.newInstance();
        } catch (Exception ex) {
            log.error(ex, ex);
            return null;
        }
        return driver;
    }

    /**
     * 
     */
    private void rebuildJDBCControls(SupportDBUrlType dbType) {
        Point windowSize = getShell().getSize();
        Point oldSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);

        if (URLSetupControlFactory.hasControl(dbType)) {

            disposeOfCurrentJDBCControls();

            this.urlSetupControl = URLSetupControlFactory.create(dbType, this.container, connectionParam);
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
        if (dbType.getLanguage().trim().equals("Generic JDBC")) { //$NON-NLS-1$
            // updateEditableState();
        } else {
            updateButtonState();
        }
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
     * DOC zhaoxinyi Comment method "updateEditableState".
     */

    private void updateEditableState() {
        String user = connectionParam.getParameters().getProperty(PluginConstant.USER_PROPERTY);
        String password = connectionParam.getParameters().getProperty(org.talend.dq.PluginConstant.PASSWORD_PROPERTY);
        boolean isPasswordBlank = password != null && !password.trim().equals(""); //$NON-NLS-1$
        boolean isUserBlank = user != null && !user.trim().equals(""); //$NON-NLS-1$
        boolean isUrlBlank = connectionParam.getJdbcUrl() != null && !connectionParam.getJdbcUrl().trim().equals(""); //$NON-NLS-1$
        boolean isDriverNameBlank = connectionParam.getDriverClassName() != null
                && !connectionParam.getDriverClassName().trim().equals(""); //$NON-NLS-1$
        boolean isDriverFileBlank = connectionParam.getDriverPath() != null && !connectionParam.getDriverPath().equals(""); //$NON-NLS-1$
        boolean isComplete = isPasswordBlank && isUserBlank && isDriverNameBlank && isUrlBlank && isDriverFileBlank;
        if (isComplete) {
            checkButton.setEnabled(!isComplete);
            setPageComplete(!isComplete);
        }
        setPageComplete(isComplete);
    }

    /**
     * 
     */
    private void updateButtonState() {
        boolean complete = true;
        if (connectionURL.trim().equals("")) { //$NON-NLS-1$
            if (checkButton != null) {
                checkButton.setEnabled(complete);
            }
            setPageComplete(complete);
        } else {

            String dbTypeName = this.connectionParam.getSqlTypeName();

            if (!SupportDBUrlType.MSSQLDEFAULTURL.getDBKey().equals(dbTypeName)) {
                if (!SupportDBUrlType.SQLITE3DEFAULTURL.getDBKey().equals(dbTypeName)) {
                    complete &= this.userid != null && !this.userid.trim().equals(""); //$NON-NLS-1$
                } else {
                    // deal with sqlite;
                    String filename = this.connectionParam.getFilePath();
                    complete &= filename != null && !filename.trim().equals(""); //$NON-NLS-1$
                }
            }

            if (checkButton != null) {
                checkButton.setEnabled(complete);
            }
            setPageComplete(complete);
        }
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
            this.connectionParam.getParameters().setProperty("user", userid); //$NON-NLS-1$
        }
    }

    public void setDBType(String dbType) {
        this.connectionParam.setSqlTypeName(dbType);
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
            this.connectionParam.getParameters().setProperty("password", password); //$NON-NLS-1$
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
