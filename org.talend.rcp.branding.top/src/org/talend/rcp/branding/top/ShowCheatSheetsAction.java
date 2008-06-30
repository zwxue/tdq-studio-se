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

package org.talend.rcp.branding.top;

import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.eclipse.ui.internal.intro.IIntroConstants;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class ShowCheatSheetsAction extends Action implements IIntroAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite, java.util.Properties)
     */
    public void run(IIntroSite site, Properties params) {
        String property = params.getProperty("id");
        OpenCheatSheetAction action = new OpenCheatSheetAction(property);
        action.run();
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewPart findView = activePage.findView(IIntroConstants.INTRO_VIEW_ID);
        if (findView != null) {
            activePage.hideView(findView);
        }
    }

}
