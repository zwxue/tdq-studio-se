// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprofiler.rcp.intro;

import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.eclipse.ui.internal.intro.IIntroConstants;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.talend.commons.ui.utils.CheatSheetUtils;

/**
 * after welcome page, show the cheat cheet view action.
 * 
 */
@SuppressWarnings("restriction")
public class ShowCheatSheetsAction extends Action implements IIntroAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite, java.util.Properties)
     */
    @Override
    public void run(IIntroSite site, Properties params) {
        IPerspectiveDescriptor persDescription1 = PlatformUI.getWorkbench().getPerspectiveRegistry()
                .findPerspectiveWithId(CheatSheetUtils.DQ_PERSPECTIVE_ID);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(persDescription1);
        String property = params.getProperty("id"); //$NON-NLS-1$

        if (CheatSheetUtils.getInstance().isFirstTime()) {
            OpenCheatSheetAction action = new OpenCheatSheetAction(property);
            action.run();
        }

        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewPart findView = activePage.findView(IIntroConstants.INTRO_VIEW_ID);
        if (findView != null) {
            activePage.hideView(findView);
        }

        // ADD msjian TDQ-7407 2013-8-23: if the cheat sheet view can be find, then do maximum display
        CheatSheetUtils.getInstance().findAndmaxDisplayCheatSheet();
        // TDQ-7407~
    }
}
