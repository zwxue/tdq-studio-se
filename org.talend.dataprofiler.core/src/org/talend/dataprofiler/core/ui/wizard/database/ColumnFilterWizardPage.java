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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
public class ColumnFilterWizardPage extends AbstractWizardPage {

    private static final String MSG_FILTER_VALID = DefaultMessagesImpl.getString("TableViewColumnFilterWizardPage.filterValid");

    private static final String MSG_FILTER_INVALID = DefaultMessagesImpl
            .getString("TableViewColumnFilterWizardPage.filterInvalid");

    private ColumnFilterWizard parent;

    private Text columnFilterText;

    public Text getColumnFilterText() {
        return columnFilterText;
    }

    public void setColumnFilterText(Text columnFilterText) {
        this.columnFilterText = columnFilterText;
    }

    public ColumnFilterWizardPage() {
        super();
    }

    public ColumnFilterWizardPage(ColumnFilterWizard parent) {
        this();
        this.parent = parent;
    }

    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout grid = new GridLayout(2, false);
        comp.setLayout(grid);

        GridData gd = new GridData();
        gd.widthHint = 280;
        gd.heightHint = 15;

        Label l1 = new Label(comp, SWT.NONE);
        l1.setText(DefaultMessagesImpl.getString("ColumnFilterWizardPage.conn"));

        Label t11 = new Label(comp, SWT.BORDER);
        t11.setText(this.parent.getTdDataProvider().getName());
        t11.setLayoutData(gd);

        Label l2 = new Label(comp, SWT.NONE);
        l2.setText(DefaultMessagesImpl.getString("ColumnFilterWizardPage.catalog"));

        Label t22 = new Label(comp, SWT.BORDER);
        t22.setText(this.parent.getPackageObj().getName());
        t22.setLayoutData(gd);

        Label l3 = new Label(comp, SWT.NONE);
        l3.setText(DefaultMessagesImpl.getString("ColumnFilterWizardPage.table"));

        Label t33 = new Label(comp, SWT.BORDER);
        t33.setText(this.parent.getNamedColumnSet().getName());
        t33.setLayoutData(gd);

        Label label1 = new Label(comp, SWT.NONE);
        label1.setText(DefaultMessagesImpl.getString("ColumnFilterWizardPage.columnFilter"));

        columnFilterText = new Text(comp, SWT.BORDER);
        String temp = this.parent.getOldColumnFilter();
        temp = temp == null ? "" : temp;
        columnFilterText.setText(temp);
        columnFilterText.setLayoutData(gd);

        addFieldsListeners();

        this.setControl(comp);
    }

    @Override
    public boolean checkFieldsValue() {
        String columnFilter = this.columnFilterText.getText();
        if (columnFilter.indexOf("\\") > -1 || columnFilter.indexOf("/") > -1) {
            return false;
        }
        return true;
    }

    private void addFieldsListeners() {
        columnFilterText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (checkFieldsValue()) {
                    updateStatus(IStatus.OK, MSG_FILTER_VALID);
                } else {
                    updateStatus(IStatus.ERROR, MSG_FILTER_INVALID);
                }
            }
        });
    }
}
