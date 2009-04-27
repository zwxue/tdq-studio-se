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
public class TableViewFilterWizardPage extends AbstractWizardPage {

    private TableViewFilterWizard parent;

    private Text tableFilterText;

    private Text viewFilterText;

    public Text getTableFilterText() {
        return tableFilterText;
    }

    public void setTableFilterText(Text tableFilterText) {
        this.tableFilterText = tableFilterText;
    }

    public Text getViewFilterText() {
        return viewFilterText;
    }

    public void setViewFilterText(Text viewFilterText) {
        this.viewFilterText = viewFilterText;
    }

    /**
     * DOC xqliu TableViewFilterWizardPage constructor comment.
     */
    public TableViewFilterWizardPage() {
        super();
    }

    public TableViewFilterWizardPage(TableViewFilterWizard parent) {
        this();
        this.parent = parent;

    }

    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout grid = new GridLayout(2, false);
        comp.setLayout(grid);

        GridData gd = new GridData();
        gd.widthHint = 280;

        Label l1 = new Label(comp, SWT.NONE);
        l1.setText(DefaultMessagesImpl.getString("TableViewFilterWizardPage.conn"));

        Label t11 = new Label(comp, SWT.BORDER);
        t11.setText(this.parent.getTdDataProvider().getName());
        t11.setLayoutData(gd);

        Label l2 = new Label(comp, SWT.NONE);
        l2.setText(DefaultMessagesImpl.getString("TableViewFilterWizardPage.catalog"));

        Label t22 = new Label(comp, SWT.BORDER);
        t22.setText(this.parent.getPackageObj().getName());
        t22.setLayoutData(gd);

        Label label1 = new Label(comp, SWT.NONE);
        label1.setText(DefaultMessagesImpl.getString("TableViewFilterWizardPage.tableFilter"));

        tableFilterText = new Text(comp, SWT.BORDER);
        tableFilterText.setText(this.parent.getOldTableFilter());
        tableFilterText.setLayoutData(gd);

        Label label2 = new Label(comp, SWT.NONE);
        label2.setText(DefaultMessagesImpl.getString("TableViewFilterWizardPage.viewFilter"));

        viewFilterText = new Text(comp, SWT.BORDER);
        viewFilterText.setText(this.parent.getOldViewFilter());
        viewFilterText.setLayoutData(gd);

        this.setControl(comp);

    }
}
