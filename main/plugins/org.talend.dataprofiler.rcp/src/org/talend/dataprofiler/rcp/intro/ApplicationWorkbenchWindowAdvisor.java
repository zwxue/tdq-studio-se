// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.talend.commons.ui.utils.CheatSheetUtils;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.core.utils.ProductUtils;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;

/**
 * DOC rli class global comment. Detailled comment <br/>
 * 
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    @Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        Rectangle screenSize = Display.getDefault().getClientArea();
        configurer.setInitialSize(new Point(screenSize.width, screenSize.height));
        configurer.setShowCoolBar(true);
        // TDQ-9268 Progress display on statausLine.
        configurer.setShowStatusLine(true);
        configurer.setShowProgressIndicator(true);
        String buildId = VersionUtils.getVersion();
        IBrandingService brandingService = GlobalServiceRegister.getDefault().getBrandingService(IBrandingService.class);
        configurer.setTitle(brandingService.getFullProductName() + " (" + buildId + ")"); //$NON-NLS-1$ //$NON-NLS-2$

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowOpen()
     */
    @Override
    public void postWindowOpen() {

        this.getWindowConfigurer().getWindow().getShell().setMaximized(true);
        super.postWindowOpen();
        ChangePerspectiveAction.getAction().switchTitle();
        ITDQRepositoryService tdqRepositoryService = (ITDQRepositoryService) org.talend.core.GlobalServiceRegister.getDefault()
                .getService(ITDQRepositoryService.class);
        if (tdqRepositoryService != null) {
            tdqRepositoryService.addPartListener();
            tdqRepositoryService.addSoftwareSystemUpdateListener();
        }

        IWorkbenchWindowConfigurer workbenchWindowConfigurer = getWindowConfigurer();

        // hide Preference page
        PreferenceManager preferenceManager = workbenchWindowConfigurer.getWindow().getWorkbench().getPreferenceManager();
        preferenceManager.remove("org.eclipse.debug.ui.DebugPreferencePage" + WorkbenchPlugin.PREFERENCE_PAGE_CATEGORY_SEPARATOR
                + "org.eclipse.ui.externaltools.ExternalToolsPreferencePage");

        // hide toolBar item
        IActionBarConfigurer actionBarConfigurer = workbenchWindowConfigurer.getActionBarConfigurer();
        ICoolBarManager coolBarManager = actionBarConfigurer.getCoolBarManager();
        IContributionItem toolBarItem = coolBarManager.find("org.eclipse.debug.ui.launchActionSet");
        if (toolBarItem != null) {
            coolBarManager.remove(toolBarItem);
        }

        // hide run menu
        IMenuManager menuManager = actionBarConfigurer.getMenuManager();
        IContributionItem[] menuItems = menuManager.getItems();
        for (IContributionItem menuItem : menuItems) {
            // Hack to remove the Run menu - it seems you cannot do this using the
            // "org.eclipse.ui.activities" extension
            // Hack to remove the Navigate menu -which can't be removed by "org.eclipse.ui.activities
            if ("org.eclipse.ui.run".equals(menuItem.getId()) || "navigate".equals(menuItem.getId())) { //$NON-NLS-1$//$NON-NLS-2$
                menuManager.remove(menuItem);
            }
        }

        // max the cheat sheet at first time
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (activePage != null) {
            if (CheatSheetUtils.getInstance().isFirstTime()
                    && activePage.getPerspective().getId().equals(ProductUtils.PERSPECTIVE_DQ_ID)) {
                CheatSheetUtils.getInstance().findAndmaxDisplayCheatSheet("org.talend.dataprofiler.core.talenddataprofiler");
            }
        }

    }

}
