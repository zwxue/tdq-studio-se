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
package org.talend.dataprofiler.core.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.talend.dataprofiler.core.ui.dialog.IndicatorSelectDialog;

/**
 * DOC yyi class global comment. Detailled comment
 */
public class SelectIndicatorsHandler extends AbstractHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        Shell activeShell = HandlerUtil.getActiveShell(event);
        if (null != activeShell) {
            Object dialog = activeShell.getData();
            if (dialog instanceof IndicatorSelectDialog) {
                ((IndicatorSelectDialog) dialog).selectAllIndicators(event.getCommand().getId().equals(
"org.talend.dataprofiler.core.selectAllIndicators"));//$NON-NLS-1$ 
            }
        }
        return null;
    }
}
