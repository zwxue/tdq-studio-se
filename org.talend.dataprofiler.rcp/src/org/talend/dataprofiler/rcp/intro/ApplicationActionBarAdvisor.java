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
package org.talend.dataprofiler.rcp.intro;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;
import org.talend.dataprofiler.core.ui.perspective.PerspectiveMenuManager;

/**
 * DOC rli class global comment. Detailled comment <br/>
 * 
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    private IWorkbenchAction exitAction;

    private IWorkbenchAction preferenceAction;

    private IWorkbenchAction aboutAction;

    private IWorkbenchAction saveAction;

    private IWorkbenchAction saveAllAction;

    private IWorkbenchAction helpAction;

    private IWorkbenchAction welcomeAction;

    private IWorkbenchAction resetPerspectiveAction;

    private IWorkbenchAction colseAction;

    private IWorkbenchAction colseAllAction;

    private IWorkbenchWindow window;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
        this.window = window;
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);

        colseAction = ActionFactory.CLOSE.create(window);
        colseAllAction = ActionFactory.CLOSE_ALL.create(window);

        saveAction = ActionFactory.SAVE.create(window);
        register(saveAction);

        saveAllAction = ActionFactory.SAVE_ALL.create(window);
        register(saveAllAction);

        register(ActionFactory.DELETE.create(window));

        preferenceAction = ActionFactory.PREFERENCES.create(window);
        register(preferenceAction);

        welcomeAction = ActionFactory.INTRO.create(window);
        register(welcomeAction);

        helpAction = ActionFactory.HELP_CONTENTS.create(window);
        register(helpAction);

        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);

        resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
        register(resetPerspectiveAction);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
        this.beforefillMenuBar();
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW); //$NON-NLS-1$
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP); //$NON-NLS-1$

        menuBar.add(fileMenu);
        menuBar.add(windowMenu);
        // Add a group marker indicating where action set menus will appear.
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(helpMenu);

        fileMenu.add(colseAction);
        fileMenu.add(colseAllAction);
        fileMenu.add(new Separator());

        // File
        fileMenu.add(saveAction);
        fileMenu.add(saveAllAction);
        fileMenu.add(exitAction);

        // Window

        MenuManager perspMenu = new PerspectiveMenuManager();
        menuBar.add(windowMenu);
        windowMenu.add(perspMenu);
        windowMenu.add(preferenceAction);
        windowMenu.add(resetPerspectiveAction);
        windowMenu.add(new ShowViewAction());
        // Help
        helpMenu.add(welcomeAction);
        helpMenu.add(helpAction);
        helpMenu.add(aboutAction);
    }

    private static final String[] ACTIONSETID = new String[] { "org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo", //$NON-NLS-1$
            "org.eclipse.ui.edit.text.actionSet.annotationNavigation", "org.eclipse.ui.NavigateActionSet", //$NON-NLS-1$ //$NON-NLS-2$
            "org.eclipse.ui.WorkingSetActionSet", "org.eclipse.ui.edit.text.actionSet.navigation", //$NON-NLS-1$ //$NON-NLS-2$
            "org.eclipse.search.searchActionSet", "org.eclipse.ui.edit.text.actionSet.openExternalFile", //$NON-NLS-1$ //$NON-NLS-2$
            "org.eclipse.jdt.ui.actions.GoToPackage", "org.eclipse.jdt.ui.actions.GoToType", //$NON-NLS-1$ //$NON-NLS-2$
            "org.eclipse.jdt.ui.actions.OpenExternalJavaDoc", "org.eclipse.jdt.ui.actions.OpenSuperImplementation", //$NON-NLS-1$ //$NON-NLS-2$
            "org.eclipse.jdt.ui.actions.CopyQualifiedName", "org.eclipse.jdt.ui.actions.Open", //$NON-NLS-1$ //$NON-NLS-2$
            "org.eclipse.jdt.ui.actions.OpenTypeHierarchy", "org.eclipse.jdt.ui.actions.OpenCallHierarchy" }; //$NON-NLS-1$ //$NON-NLS-2$

    private void beforefillMenuBar() {
        this.removeAction();
    }

    /**
     * remove the unnecessary actions.
     */
    @SuppressWarnings("restriction")//$NON-NLS-1$
    private void removeAction() {
        ActionSetRegistry reg = WorkbenchPlugin.getDefault().getActionSetRegistry();
        IActionSetDescriptor[] actionSets = reg.getActionSets();
        List<String> list = Arrays.asList(ACTIONSETID);
        for (int i = 0; i < actionSets.length; i++) {
            if (list.contains(actionSets[i].getId())) {
                IExtension ext = actionSets[i].getConfigurationElement().getDeclaringExtension();
                reg.removeExtension(ext, new Object[] { actionSets[i] });
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.ActionBarAdvisor#fillCoolBar(org.eclipse.jface.action.ICoolBarManager)
     */
    @Override
    protected void fillCoolBar(ICoolBarManager coolBar) {
        IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(toolbar, "switch_persp")); //$NON-NLS-1$
        toolbar.add(new ChangePerspectiveAction(true));
        toolbar.add(ActionFactory.SAVE.create(window));
    }
}
