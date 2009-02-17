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
package org.talend.dataprofiler.core.ui.wizard.dqrules;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class NewDQRulesWizardPage2 extends AbstractWizardPage {

    protected Text whereText;

    public Text getWhereText() {
        return whereText;
    }

    public NewDQRulesWizardPage2() {
        setPageComplete(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gdLayout = new GridLayout(2, false);
        container.setLayout(gdLayout);

        Label label = new Label(container, SWT.NONE);
        label.setText(DefaultMessagesImpl.getString("NewDQRulesWizard.whereClause"));
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        whereText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        whereText.setText(DefaultMessagesImpl.getString("NewDQRulesWizard.where"));
        GridData data = new GridData(GridData.FILL_BOTH);
        whereText.setLayoutData(data);

        setControl(container);
    }

    public boolean checkFieldsValue() {
        return true;
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }
}
