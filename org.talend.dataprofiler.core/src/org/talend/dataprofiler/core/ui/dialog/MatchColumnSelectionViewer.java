// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFTableRepNode;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class MatchColumnSelectionViewer extends ColumnSelectionViewer {

    /**
     * DOC yyin MatchColumnSelectionViewer constructor comment.
     * 
     * @param parent
     * @param style
     */
    public MatchColumnSelectionViewer(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected boolean isNotTableCase(CheckStateChangedEvent event, Object element) {
        if (element instanceof DBTableRepNode || element instanceof DBViewRepNode || element instanceof DFTableRepNode) {

            return true;
        }
        MessageDialogWithToggle
                .openWarning(
                        null,
                        DefaultMessagesImpl.getString("ColumnSelectionViewer.warning"), DefaultMessagesImpl.getString("MatchColumnSelectionViewer.notTableCase")); //$NON-NLS-1$
        return false;
    }

}
