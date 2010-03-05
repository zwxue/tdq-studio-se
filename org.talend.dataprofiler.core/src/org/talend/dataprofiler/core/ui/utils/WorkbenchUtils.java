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

import org.apache.log4j.Logger;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class WorkbenchUtils {

    private static Logger log = Logger.getLogger(WorkbenchUtils.class);

    private WorkbenchUtils() {

    }

    /**
     * DOC bZhou Comment method "changePerspective".
     * 
     * @param perspectiveID
     */
    public static void changePerspective(String perspectiveID) {
        IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        try {
            PlatformUI.getWorkbench().showPerspective(perspectiveID, activeWindow);
        } catch (WorkbenchException e) {
            log.error(e.getMessage(), e);
        }
    }
}
