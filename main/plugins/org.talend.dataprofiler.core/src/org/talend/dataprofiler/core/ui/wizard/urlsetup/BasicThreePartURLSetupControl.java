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
package org.talend.dataprofiler.core.ui.wizard.urlsetup;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlStore;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.composite.MultipleSelectionCombo;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;
import org.talend.dataprofiler.core.ui.wizard.database.DatabaseWizardPage;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.helper.SqlExplorerUtils;

/**
 * 
 */
public class BasicThreePartURLSetupControl extends URLSetupControl {

    private static Logger log = Logger.getLogger(BasicThreePartURLSetupControl.class);

    private Text urlText;

    protected AbstractWizardPage abstractWizardPage;

    public BasicThreePartURLSetupControl(Composite parent, SupportDBUrlType dbType) {
        super(parent, dbType);
    }

    public BasicThreePartURLSetupControl(Composite parent, SupportDBUrlType dbType, AbstractWizardPage abstractWizardPage) {
        super(parent, dbType);
        this.abstractWizardPage = abstractWizardPage;
    }

    @Override
    protected void createPart(Composite parent, String dbLiteral, final DBConnectionParameter connectionParam) {
        if (dbLiteral.trim().equals("Generic JDBC")) { //$NON-NLS-1$
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            parent.setLayout(layout);
            Label labelJar = new Label(parent, SWT.NONE);
            labelJar.setText("Driver jar"); //$NON-NLS-1$
            final Text jarText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            jarText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            jarText.setEditable(false);
            jarText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    connectionParam.setDriverPath(jarText.getText());
                }

            });
            final Button selectJar = new Button(parent, SWT.PUSH);
            final StringBuilder filenameAll = new StringBuilder();
            selectJar.setText("..."); //$NON-NLS-1$
            selectJar.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());
                    String filename = dialog.open();
                    if (filename != null) {
                        filenameAll.append(filename + ";"); //$NON-NLS-1$
                        // filenameAll.deleteCharAt(0);
                        jarText.setText(filenameAll.toString());
                        int length = filenameAll.length();
                        filenameAll.delete(0, length);

                    } else {
                        jarText.setText(""); //$NON-NLS-1$
                    }
                }
            });
            Label labelDriver = new Label(parent, SWT.NONE);
            labelDriver.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.DriverClassName")); //$NON-NLS-1$
            // int comboUrlCount = 0;
            // String[] dbDriverName = new String[SupportDBUrlType.values().length - 1];
            // for (SupportDBUrlType dbType : SupportDBUrlType.values()) {
            // if (!dbType.getDbDriver().trim().equals("")) {
            // dbDriverName[comboUrlCount++] = dbType.getDbDriver();
            // }
            // }
            final Combo comboDriver = new Combo(parent, SWT.READ_ONLY);
            comboDriver.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            // comboDriver.setItems(dbDriverName);
            comboDriver.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    String driverClassName = comboDriver.getText();
                    connectionParam.setDriverClassName(driverClassName);
                    if (abstractWizardPage instanceof DatabaseWizardPage) {
                        ((DatabaseWizardPage) abstractWizardPage).updateLoginPassEnable(!SupportDBUrlType.SQLITE3DEFAULTURL
                                .getDbDriver().equals(driverClassName));
                    }
                }

            });

            Button listDriverBtn = new Button(parent, SWT.PUSH);
            listDriverBtn.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.ListDrivers")); //$NON-NLS-1$
            listDriverBtn.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    comboDriver.removeAll();
                    for (String stringToFile : jarText.getText().trim().split(";")) { //$NON-NLS-1$
                        File file = new File(stringToFile);
                        if (file != null) {
                            try {
                                Class[] classes = SqlExplorerUtils.getDefault()
                                        .getMyURLClassLoaderAssignableClasses(file.toURL());
                                for (int i = 0; i < classes.length; ++i) {
                                    comboDriver.add(classes[i].getName());
                                }
                            } catch (MalformedURLException e1) {
                                log.error(e1, e1);
                            }
                        }
                    }
                    if (comboDriver.getItemCount() > 0) {
                        String driverClassName = comboDriver.getItem(0);
                        comboDriver.setText(driverClassName);
                        connectionParam.setDriverClassName(driverClassName);
                        if (abstractWizardPage instanceof DatabaseWizardPage) {
                            ((DatabaseWizardPage) abstractWizardPage).updateButtonState();
                            ((DatabaseWizardPage) abstractWizardPage).updateLoginPassEnable(!SupportDBUrlType.SQLITE3DEFAULTURL
                                    .getDbDriver().equals(driverClassName));
                        }
                    }
                }
            });

            Label labelUrl = new Label(parent, SWT.NONE);
            labelUrl.setText("Url"); //$NON-NLS-1$
            final Text urlText2 = new Text(parent, SWT.BORDER | SWT.SINGLE);
            urlText2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            urlText2.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    connectionParam.setJdbcUrl(urlText2.getText());
                    if (abstractWizardPage instanceof DatabaseWizardPage) {
                        ((DatabaseWizardPage) abstractWizardPage).updateButtonState();
                    }
                }

            });

        } else if (dbLiteral.trim().equals(SupportDBUrlType.SQLITE3DEFAULTURL.getLanguage())) {
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            parent.setLayout(layout);

            Label labelfile = new Label(parent, SWT.NONE);
            final Text fileText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            final Button selectFile = new Button(parent, SWT.PUSH);
            Label labelUrl = new Label(parent, SWT.NONE);
            final Text urlText3 = new Text(parent, SWT.BORDER | SWT.SINGLE);

            labelfile.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.File")); //$NON-NLS-1$
            fileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            fileText.setEditable(true);
            fileText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    connectionParam.setFilePath(fileText.getText());
                }

            });

            selectFile.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.Browser")); //$NON-NLS-1$

            labelUrl.setText("Url"); //$NON-NLS-1$
            urlText3.setEditable(false);
            urlText3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), "", "", fileText.getText(), "", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            urlText3.setText(getConnectionURL());
            urlText3.setEditable(false);

            urlText3.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    setConnectionURL(urlText3.getText());
                }

            });

            urlText3.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    urlText3.setEditable(true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    urlText3.setEditable(false);
                }
            });

            selectFile.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());
                    String filename = dialog.open();
                    if (filename != null) {
                        fileText.setText(filename);
                    } else {
                        fileText.setText(""); //$NON-NLS-1$
                    }
                    String url = SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), "", "", fileText.getText(), "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

                    if (log.isInfoEnabled()) {
                        log.info("the formated url is :" + url); //$NON-NLS-1$
                    }

                    setConnectionURL(url);
                    urlText3.setText(getConnectionURL());
                }
            });

        } else if (dbLiteral.trim().equals(SupportDBUrlType.MDM.getLanguage())) {
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            parent.setLayout(layout);

            boolean compositeEnable = !(dbType.getHostName() == null);
            Label label = new Label(parent, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.Hostname")); //$NON-NLS-1$
            final Text hostNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            hostNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (compositeEnable) {
                hostNameText.setText(dbType.getHostName());
                connectionParam.setHost(dbType.getHostName());
            }
            label.setEnabled(compositeEnable);
            hostNameText.setEnabled(compositeEnable);

            compositeEnable = !(dbType.getPort() == null);
            label = new Label(parent, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.Port")); //$NON-NLS-1$
            final Text portText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (compositeEnable) {
                portText.setText(dbType.getPort());
                connectionParam.setPort(dbType.getPort());
            }
            label.setEnabled(compositeEnable);
            portText.setEnabled(compositeEnable);

            compositeEnable = false;
            label = new Label(parent, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.DBname")); //$NON-NLS-1$
            final Text databaseNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            databaseNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            databaseNameText.setText(dbType.getDBName());
            label.setEnabled(compositeEnable);
            databaseNameText.setEnabled(compositeEnable);

            label = new Label(parent, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.datafilter")); //$NON-NLS-1$
            dataFilterCombo = new MultipleSelectionCombo(parent, SWT.READ_ONLY);
            dataFilterCombo.setEnabled(false);
            dataFilterCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            dataFilterCombo.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    connectionParam.getParameters().setProperty(TaggedValueHelper.DATA_FILTER, ((Text) e.getSource()).getText());

                }

            });

            label = new Label(parent, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.universe")); //$NON-NLS-1$
            final Text universeText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            universeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            universeText.setText(""); //$NON-NLS-1$

            label = new Label(parent, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.url")); //$NON-NLS-1$
            urlText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            urlText.setEditable(false);
            urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            urlText.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    urlText.setEditable(true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    urlText.setEditable(false);
                }
            });
            urlText.setText(getConnectionURL());
            urlText.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    setConnectionURL(urlText.getText());
                }

            });

            universeText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent event) {
                    setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(),
                            portText.getText(), databaseNameText.getText(), universeText.getText(), "")); //$NON-NLS-1$
                    urlText.setText(getConnectionURL());
                    connectionParam.getParameters().setProperty(TaggedValueHelper.UNIVERSE, universeText.getText());
                }
            });

            hostNameText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent event) {
                    String host = hostNameText.getText();
                    setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), host, portText.getText(),
                            databaseNameText.getText(), universeText.getText(), "")); //$NON-NLS-1$
                    urlText.setText(getConnectionURL());
                    connectionParam.setHost(host);
                }
            });

            portText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent event) {

                    String port = portText.getText();
                    setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(), port,
                            databaseNameText.getText(), universeText.getText(), "")); //$NON-NLS-1$
                    urlText.setText(getConnectionURL());
                    connectionParam.setPort(port);
                }
            });
            portText.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    Long portValue = null;
                    try {
                        portValue = new Long(portText.getText());
                    } catch (NumberFormatException e1) {
                        // JUMP
                    }
                    if (portValue == null || portValue <= 0) {
                        portText.setText(PluginConstant.EMPTY_STRING);
                    }
                }
            });

        } else {
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            parent.setLayout(layout);

            boolean compositeEnable = !(dbType.getHostName() == null);
            Label label = new Label(parent, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.Hostname")); //$NON-NLS-1$
            final Text hostNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            hostNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (compositeEnable) {
                hostNameText.setText(dbType.getHostName());
                connectionParam.setHost(dbType.getHostName());
            }
            label.setEnabled(compositeEnable);
            hostNameText.setEnabled(compositeEnable);

            compositeEnable = !(dbType.getPort() == null);
            label = new Label(parent, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.Port")); //$NON-NLS-1$
            final Text portText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (compositeEnable) {
                portText.setText(dbType.getPort());
                connectionParam.setPort(dbType.getPort());
            }
            label.setEnabled(compositeEnable);
            portText.setEnabled(compositeEnable);

            compositeEnable = !(dbType.getDBName() == null);
            label = new Label(parent, SWT.NONE);
            if (dbType == SupportDBUrlType.ORACLEWITHSIDDEFAULTURL) {
                label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.SID")); //$NON-NLS-1$
            } else if (dbType == SupportDBUrlType.ORACLEWITHSERVICENAMEDEFAULTURL) {
                label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.serviceName")); //$NON-NLS-1$
            } else {
                label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.DBname")); //$NON-NLS-1$
            }
            final Text databaseNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            databaseNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (compositeEnable) {
                databaseNameText.setText(dbType.getDBName());
            }
            label.setEnabled(compositeEnable);
            databaseNameText.setEnabled(compositeEnable);
            // MOD klliu 2010-10-09 feature 15821
            if (dbType == SupportDBUrlType.ORACLEWITHSIDDEFAULTURL) {
                label = new Label(parent, SWT.NONE);
                label.setText("Schema"); //$NON-NLS-1$
                final Text schemaText = new Text(parent, SWT.BORDER | SWT.SINGLE);
                schemaText.setEditable(true);
                schemaText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

                schemaText.addModifyListener(new ModifyListener() {

                    public void modifyText(ModifyEvent event) {
                        String schema = schemaText.getText();
                        connectionParam.setOtherParameter(schema);
                    }
                });

            }
            compositeEnable = !(dbType.getParamSeprator() == null);
            label = new Label(parent, SWT.NONE);
            label.setText("Additional JDBC Parameters"); //$NON-NLS-1$
            final Text parameterText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            parameterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (dbType.getParamSeprator() != null) {
                parameterText.setText(org.talend.core.model.metadata.builder.database.PluginConstant.DEFAULT_PARAMETERS);
                connectionParam
                        .setADDParameter(org.talend.core.model.metadata.builder.database.PluginConstant.DEFAULT_PARAMETERS);
            } else {
                parameterText.setText(PluginConstant.EMPTY_STRING);
                connectionParam.setADDParameter(PluginConstant.EMPTY_STRING);
            }
            parameterText.setEnabled(compositeEnable);
            label.setEnabled(compositeEnable);
            parameterText.setEnabled(compositeEnable);

            compositeEnable = !(dbType.getDataSource() == null);
            label = new Label(parent, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.dataSource")); //$NON-NLS-1$
            final Text dataSourceText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            dataSourceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (compositeEnable) {
                dataSourceText.setText(dbType.getDataSource());
            }
            label.setEnabled(compositeEnable);
            dataSourceText.setEnabled(compositeEnable);

            label = new Label(parent, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("BasicThreePartURLSetupControl.url")); //$NON-NLS-1$
            urlText = new Text(parent, SWT.BORDER | SWT.SINGLE);
            urlText.setEditable(false);
            urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            urlText.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    urlText.setEditable(true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    urlText.setEditable(false);
                }
            });
            urlText.setText(getConnectionURL());

            urlText.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    setConnectionURL(urlText.getText());
                }

            });

            dataSourceText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent event) {
                    setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(),
                            portText.getText(), databaseNameText.getText(), dataSourceText.getText(), parameterText.getText()));
                    urlText.setText(getConnectionURL());
                }
            });

            hostNameText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent event) {
                    String host = hostNameText.getText();
                    setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), host, portText.getText(),
                            databaseNameText.getText(), dataSourceText.getText(), parameterText.getText()));
                    urlText.setText(getConnectionURL());
                    connectionParam.setHost(host);
                }
            });

            portText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent event) {

                    String port = portText.getText();
                    setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(), port,
                            databaseNameText.getText(), dataSourceText.getText(), parameterText.getText()));
                    urlText.setText(getConnectionURL());
                    connectionParam.setPort(port);
                }
            });

            // MOD yyi 2010-12-14 16293 Allow Ingres Database input letter as port
            final boolean ingresdb = isIngres(dbLiteral);
            portText.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if (!ingresdb) {

                        Long portValue = null;
                        try {
                            portValue = new Long(portText.getText());
                        } catch (NumberFormatException e1) {
                            // JUMP
                        }
                        if (portValue == null || portValue <= 0) {
                            portText.setText(PluginConstant.EMPTY_STRING);
                        }
                    }
                }
            });

            databaseNameText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent event) {
                    String dbName = databaseNameText.getText();
                    setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(),
                            portText.getText(), dbName, dataSourceText.getText(), parameterText.getText()));
                    urlText.setText(getConnectionURL());
                    SupportDBUrlStore.getInstance().changeAllDBNmae(dbName);
                    connectionParam.setDbName(dbName);
                }
            });

            parameterText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    connectionParam.setADDParameter(parameterText.getText());
                    setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(),
                            portText.getText(), databaseNameText.getText(), dataSourceText.getText(), parameterText.getText()));
                    urlText.setText(getConnectionURL());
                }

            });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.urlsetup.URLSetupControl#setConnectionURL(java.lang.String)
     */
    @Override
    public void setConnectionURL(String connectionURL) {
        super.setConnectionURL(connectionURL);
        if (urlText != null) {
            this.urlText.setText(connectionURL);
        }
    }

    /**
     * ADD yyi 2010-12-14 check is Ingres DB.
     * 
     * @param dbName
     * @return true if the db name is 'Ingres'
     */
    private boolean isIngres(String dbName) {
        return dbName.trim().equals(SupportDBUrlType.INGRESDEFAULTURL.getLanguage());
    }
}
