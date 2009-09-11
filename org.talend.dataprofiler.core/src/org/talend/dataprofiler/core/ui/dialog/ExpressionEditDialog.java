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
package org.talend.dataprofiler.core.ui.dialog;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC yyi 2009-09-11 Feature:9030
 */
public class ExpressionEditDialog extends TrayDialog {

    private static final String TITLE = DefaultMessagesImpl.getString("ExpressionEditDialog.title"); //$NON-NLS-1$

    private String[] templates;

    private String expression;

    private Text editText;

    public ExpressionEditDialog(Shell parentShell, String expression, String[] templates) {
        super(parentShell);

        this.expression = expression;
        this.templates = templates;

    }

    protected Control createDialogArea(Composite parent) {

        Composite comp = (Composite) super.createDialogArea(parent);
        GridData data = new GridData();
        data.widthHint = 650;
        data.heightHint = 380;
        comp.setLayoutData(data);

        SashForm sform = new SashForm(comp, SWT.VERTICAL | SWT.SMOOTH | SWT.FILL);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        sform.setLayoutData(data);

        Group group = new Group(sform, SWT.NONE);
        group.setText(DefaultMessagesImpl.getString("ExpressionEditDialog.groupTitle"));
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 20;
        group.setLayout(gridLayout);

        editText = new Text(group, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
        editText.setText(expression);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        editText.setLayoutData(data);

        Table templatesTable = new Table(sform, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
        templatesTable.setLinesVisible(false);
        templatesTable.setHeaderVisible(true);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 200;
        templatesTable.setLayoutData(data);
        templatesTable.addMouseListener(new MouseAdapter() {

            public void mouseDoubleClick(MouseEvent e) {
                TableItem item[] = ((Table) e.widget).getSelection();
                String head, end;
                head = editText.getText().substring(0, editText.getSelection().x);
                end = editText.getText().substring(editText.getSelection().x + editText.getSelectionCount());

                editText.setText(head + item[0].getText() + end);
                editText.setFocus();
            }
        });

        TableColumn column = new TableColumn(templatesTable, SWT.NONE);
        column.setText(DefaultMessagesImpl.getString("ExpressionEditDialog.columnTitle"));

        for (String template : templates) {
            TableItem item = new TableItem(templatesTable, SWT.NONE);
            item.setText(0, template);

        }
        templatesTable.getColumn(0).pack();

        sform.setWeights(new int[] { 2, 1 });
        return comp;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(TITLE);
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.MAX | SWT.RESIZE);
    }

    public String getResult() {
        return expression;
    }

    @Override
    protected void okPressed() {
        expression = editText.getText();
        super.okPressed();
    }
}
