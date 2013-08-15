// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class ConnectionTypePage extends WizardPage {

    private String defaultInfor = DefaultMessagesImpl.getString("ConnnectionTypePage.defaultInfor"); //$NON-NLS-1$

    private CCombo connectionType;

    /**
     * DOC yyin ConnectionTypePage constructor comment.
     * 
     * @param pageName
     */
    protected ConnectionTypePage(String pageName) {
        super(pageName);
    }

    /*
     * Create the composites, initialize it and add controls.
     */
    @Override
    public void createControl(Composite parent) {
        // create the dropdown list to select the connection type
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 70;
        Label inforLabel = new Label(container, SWT.BORDER);
        inforLabel.setText(defaultInfor);
        inforLabel.setLayoutData(gd);

        // COnnection type
        Label statusLab = new Label(container, SWT.NONE);
        statusLab.setText("Status"); //$NON-NLS-1$

        connectionType = new CCombo(container, SWT.BORDER);

        connectionType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        connectionType.setEditable(false);

        String[] connectionTypeList = { "MDM", "DB", "fileDelimited" };

        connectionType.setItems(connectionTypeList);
        connectionType.select(0);

        setControl(container);
    }

    public int getConnectionType() {
        return this.connectionType.getSelectionIndex();
    }

}
