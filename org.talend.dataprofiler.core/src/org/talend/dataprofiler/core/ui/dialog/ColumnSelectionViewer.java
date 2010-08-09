// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ColumnSelectionViewer extends ContainerCheckedTreeViewer {

    private final String tooMuchItemSeleted = DefaultMessagesImpl.getString("ColumnSelectionViewer.string"); //$NON-NLS-1$

    /**
     * DOC zqin ColumnSelectionViewer constructor comment.
     * 
     * @param parent
     * @param style
     */
    public ColumnSelectionViewer(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected void fireCheckStateChanged(CheckStateChangedEvent event) {

        boolean checked = event.getChecked();
        Object element = event.getElement();

        boolean isTable = element instanceof TdTable;
        boolean isXmlElement = element instanceof TdXmlElementType;

        if ((checked && (isTable || isXmlElement)) || !checked) {
            super.fireCheckStateChanged(event);
        }
        if (checked && !(isTable || isXmlElement)) {
            if (MessageDialogWithToggle.openConfirm(null,
                    DefaultMessagesImpl.getString("ColumnSelectionViewer.warning"), tooMuchItemSeleted)) { //$NON-NLS-1$
                super.fireCheckStateChanged(event);
            } else {
                event.getCheckable().setChecked(element, false);
            }
        }
    }

}
