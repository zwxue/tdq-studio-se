// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.dialog.message;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC rli  class global comment. Detailled comment
 */
public class TreeMessageInfoDialog extends MessageDialog {

    private ITreeContentProvider contentProvider;

    private ILabelProvider labelProvider;

    private Object input;

    private Button checkButton;

    private boolean isChecked = false;

    private boolean needCheckbox = false;

    public TreeMessageInfoDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
            int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
    }

    protected Control createCustomArea(Composite parent) {
        // MOD qiongli 2011-2-15,feature 17588.
        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.numColumns = 1;
        mainComposite.setLayout(layout);
        if (isNeedCheckbox()) {
            Composite checkComp = new Composite(mainComposite, SWT.NONE);
            GridLayout layout1 = new GridLayout();
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            layout.numColumns = 1;
            checkComp.setLayout(layout1);
            checkButton = new Button(checkComp, SWT.CHECK);
            checkButton.setText(DefaultMessagesImpl.getString("DQDeleteAction.deleteAllDependency"));//$NON-NLS-1$
            checkButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    isChecked = checkButton.getSelection();
                }

            });
        }
        TreeViewer viewer = new TreeViewer(mainComposite, SWT.BORDER);
        GridData gd = new GridData();
        gd.heightHint = 150;
        gd.widthHint = 450;
        viewer.getTree().setLayoutData(gd);
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(labelProvider);
        viewer.setInput(input);
        applyDialogFont(viewer.getControl());
        viewer.expandAll();
        return viewer.getControl();
    }

    public void setContentProvider(ITreeContentProvider provider) {
        contentProvider = provider;
    }

    public void setLabelProvider(ILabelProvider provider) {
        labelProvider = provider;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public boolean isNeedCheckbox() {
        return this.needCheckbox;
    }

    public void setNeedCheckbox(boolean needCheckbox) {
        this.needCheckbox = needCheckbox;
    }

}
