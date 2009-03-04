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
package org.talend.dataprofiler.core.ui.utils;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public final class MessageUI {

    private MessageUI() {
    }

    public static void openError(String message) {
        MessageDialogWithToggle.openError(null, "Error", message);
    }

    public static void openWarning(String message) {
        MessageDialogWithToggle.openWarning(null, "Warning", message);
    }

    public static boolean openConfirm(String message) {
        return MessageDialogWithToggle.openConfirm(null, "Confirm", message);
    }

}
