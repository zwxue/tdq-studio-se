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
package org.talend.dataprofiler.core.ui.wizard.urlsetup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.model.dburl.SupportDBUrlStore;
import org.talend.dataprofiler.core.model.dburl.SupportDBUrlType;

/**
 * 
 */
public class BasicThreePartURLSetupControl extends URLSetupControl {

    private Text urlText;

    public BasicThreePartURLSetupControl(Composite parent, SupportDBUrlType dbType) {
        super(parent, dbType);
    }

    protected void createPart(Composite parent) {

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        parent.setLayout(layout);

        boolean compositeEnable = !(dbType.getHostName() == null);
        Label label = new Label(parent, SWT.NONE);
        label.setText("Hostname");
        final Text hostNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
        hostNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (compositeEnable) {
            hostNameText.setText(dbType.getHostName());
        }
        label.setEnabled(compositeEnable);
        hostNameText.setEnabled(compositeEnable);

        compositeEnable = !(dbType.getPort() == null);
        label = new Label(parent, SWT.NONE);
        label.setText("Port");
        final Text portText = new Text(parent, SWT.BORDER | SWT.SINGLE);
        portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (compositeEnable) {
            portText.setText(dbType.getPort());
        }
        label.setEnabled(compositeEnable);
        portText.setEnabled(compositeEnable);

        compositeEnable = !(dbType.getDBName() == null);
        label = new Label(parent, SWT.NONE);
        label.setText("DBname"); //$NON-NLS-1$
        final Text databaseNameText = new Text(parent, SWT.BORDER | SWT.SINGLE);
        databaseNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (compositeEnable) {
            databaseNameText.setText(dbType.getDBName());
        }
        label.setEnabled(compositeEnable);
        databaseNameText.setEnabled(compositeEnable);

        compositeEnable = !(dbType.getParamSeprator() == null);
        label = new Label(parent, SWT.NONE);
        label.setText("Additional JDBC Parameters"); //$NON-NLS-1$
        final Text parameterText = new Text(parent, SWT.BORDER | SWT.SINGLE);
        parameterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (dbType.getParamSeprator() != null) {
            parameterText.setText(PluginConstant.DEFAULT_PARAMETERS);
        } else {
            parameterText.setText(PluginConstant.EMPTY_STRING);
        }
        parameterText.setEnabled(compositeEnable);
        label.setEnabled(compositeEnable);
        parameterText.setEnabled(compositeEnable);

        compositeEnable = !(dbType.getDataSource() == null);
        label = new Label(parent, SWT.NONE);
        label.setText("DataSource");
        final Text dataSourceText = new Text(parent, SWT.BORDER | SWT.SINGLE);
        dataSourceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (compositeEnable) {
            dataSourceText.setText(dbType.getDataSource());
        }
        label.setEnabled(compositeEnable);
        dataSourceText.setEnabled(compositeEnable);

        label = new Label(parent, SWT.NONE);
        label.setText("Url");
        urlText = new Text(parent, SWT.BORDER | SWT.SINGLE);
        urlText.setEditable(false);
        urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        urlText.setText(getConnectionURL());

        dataSourceText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {
                setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(),
                        portText.getText(), databaseNameText.getText(), dataSourceText.getText(), parameterText.getText()));
                urlText.setText(getConnectionURL());
            }
        });

        hostNameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {
                setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(),
                        portText.getText(), databaseNameText.getText(), dataSourceText.getText(), parameterText.getText()));
                urlText.setText(getConnectionURL());
            }
        });

        portText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {

                setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(),
                        portText.getText(), databaseNameText.getText(), dataSourceText.getText(), parameterText.getText()));
                urlText.setText(getConnectionURL());
            }
        });
        portText.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                Long portValue = null;
                try {
                    portValue = new Long(portText.getText());
                } catch (NumberFormatException e1) {
                    // JUMP
                }
                if (portValue == null || portValue <= 0) {
                    portText.setText(PluginConstant.EMPTY_STRING); //$NON-NLS-1$
                }
            }
        });

        databaseNameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent event) {
                setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(),
                        portText.getText(), databaseNameText.getText(), dataSourceText.getText(), parameterText.getText()));
                urlText.setText(getConnectionURL());
                SupportDBUrlStore.getInstance().changeAllDBNmae(databaseNameText.getText());
            }
        });

        parameterText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setConnectionURL(SupportDBUrlStore.getInstance().getDBUrl(dbType.getDBKey(), hostNameText.getText(),
                        portText.getText(), databaseNameText.getText(), dataSourceText.getText(), parameterText.getText()));
                urlText.setText(getConnectionURL());
            }

        });
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
}
