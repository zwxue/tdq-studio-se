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
package org.talend.cwm.compare.ui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class DQRepositoryListenerView extends ViewPart implements ISelectionListener {

    private Label label;

    public DQRepositoryListenerView() {
        super();
    }

    public void setFocus() {
        label.setFocus();
    }

    public void createPartControl(Composite parent) {
        label = new Label(parent, 0);
        label.setText(""); //$NON-NLS-1$
        getViewSite().getPage().addSelectionListener(this);
    }

    /**
     * @see ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            Object first = ((IStructuredSelection) selection).getFirstElement();
            if (first instanceof TdDataProvider) {
                label.setText(((TdDataProvider) first).getName() + "\n\r" + first.getClass().toString()); //$NON-NLS-1$
            } else if (first instanceof TdCatalog) {
                label.setText(((TdCatalog) first).getName() + "\n\r" + first.getClass().toString()); //$NON-NLS-1$
            } else if (first instanceof TdSchema) {
                label.setText(((TdSchema) first).getName() + "\n\r" + first.getClass().toString()); //$NON-NLS-1$
            } else if (first instanceof TdTable) {
                label.setText(((TdTable) first).getName() + "\n\r" + first.getClass().toString()); //$NON-NLS-1$
            } else if (first instanceof TdView) {
                label.setText(((TdView) first).getName() + "\n\r" + first.getClass().toString()); //$NON-NLS-1$
            } else if (first instanceof TdColumn) {
                label.setText(((TdColumn) first).getName() + "\n\r" + first.getClass().toString()); //$NON-NLS-1$
            } else {
                label.setText(first.toString() + "\n\r" + first.getClass().toString()); //$NON-NLS-1$
                if (first.toString().endsWith(".prv")) {
                    // MessageDialog.openInformation(getSite().getShell(), "DQRepositoryListenerView", "This is " +
                    // first.toString());
                }
            }

        }
    }
}
