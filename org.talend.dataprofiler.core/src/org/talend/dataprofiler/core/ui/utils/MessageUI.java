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
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public final class MessageUI {

    private MessageUI() {
    }

    public static void openError(String message) {
        MessageDialogWithToggle.openError(null, DefaultMessagesImpl.getString("MessageUI.Error"), message); //$NON-NLS-1$
    }

    public static void openWarning(String message) {
        MessageDialogWithToggle.openWarning(null, DefaultMessagesImpl.getString("MessageUI.Warning"), message); //$NON-NLS-1$
    }

    public static boolean openConfirm(String message) {
        return MessageDialogWithToggle.openConfirm(null, DefaultMessagesImpl.getString("MessageUI.Confirm"), message); //$NON-NLS-1$
    }

}
