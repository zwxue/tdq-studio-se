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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * DOC rli  class global comment. Detailled comment
 */
public class TreeMessageInfoDialog extends MessageDialog {

    private ITreeContentProvider contentProvider;

    private ILabelProvider labelProvider;

    private Object input;

    public TreeMessageInfoDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
            int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
    }

    protected Control createCustomArea(Composite parent) {
        TreeViewer viewer = new TreeViewer(parent, SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 150;
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

}
