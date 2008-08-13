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
package org.talend.dataprofiler.core.ui.wizard.analysis.connection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizardPage;
import org.talend.dq.analysis.parameters.ConnectionAnalysisParameter;

/**
 * @author zqin
 * 
 */
public class ConnAnalysisPageStep1 extends AbstractAnalysisWizardPage {

    private String defaultInfor = "Set filter on tables and / or views if needed.\n"
            + "By default, all tables and views will be used in the analysis.\n" + "Separate several filters with comma ','";

    private Text tableFilter;

    private Text viewFilter;

    /**
     * @param pageName
     */
    public ConnAnalysisPageStep1() {
        setTitle("New Analysis");
        setMessage("Add the filters for Connection Analysis");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 70;
        Label inforLabel = new Label(container, SWT.BORDER);
        inforLabel.setText(defaultInfor);
        inforLabel.setLayoutData(gd);

        Composite subContainer = new Composite(container, SWT.NONE);
        GridLayout subLayout = new GridLayout(2, false);
        subContainer.setLayout(subLayout);
        subContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label labelTable = new Label(subContainer, SWT.NONE);
        labelTable.setText("Table name filter");
        tableFilter = new Text(subContainer, SWT.BORDER);
        tableFilter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        tableFilter.setText("%");
        tableFilter.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                ((ConnectionAnalysisParameter) getConnectionParams()).setViewFilter(tableFilter.getText());

            }
        });

        Label labelView = new Label(subContainer, SWT.NONE);
        labelView.setText("View name filter");
        viewFilter = new Text(subContainer, SWT.BORDER);
        viewFilter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        viewFilter.setText("%");
        viewFilter.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                ((ConnectionAnalysisParameter) getConnectionParams()).setViewFilter(viewFilter.getText());

            }
        });
        setControl(container);
    }

}
