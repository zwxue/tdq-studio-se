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
package org.talend.dataprofiler.core.ui.utils;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Shell;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public final class MessageUI {

    private MessageUI() {
    }

    public static void openError(Shell shell, String message) {
        MessageDialogWithToggle.openError(shell, DefaultMessagesImpl.getString("MessageUI.Error"), message); //$NON-NLS-1$
    }

    public static void openWarning(Shell shell, String message) {
        MessageDialogWithToggle.openWarning(shell, DefaultMessagesImpl.getString("MessageUI.Warning"), message); //$NON-NLS-1$
    }

    public static boolean openConfirm(Shell shell, String message) {
        return MessageDialogWithToggle.openConfirm(shell, DefaultMessagesImpl.getString("MessageUI.Confirm"), message); //$NON-NLS-1$
    }

}
