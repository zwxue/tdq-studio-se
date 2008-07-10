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
package org.talend.dataprofiler.core.pattern;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.ui.internal.views.HelpTray;
import org.eclipse.help.ui.internal.views.ReusableHelpPart;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.talend.dataprofiler.help.HelpPlugin;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class CheatSheetWizardDialog extends WizardDialog {

    private static int activeCount = 0;

    private String href;

    /**
     * DOC qzhang CheatSheetWizardDialog constructor comment.
     * 
     * @param parentShell
     * @param newWizard
     */
    public CheatSheetWizardDialog(Shell parentShell, IWizard newWizard, String href) {
        super(parentShell, newWizard);
        this.href = href;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.TrayDialog#openTray(org.eclipse.jface.dialogs.DialogTray)
     */
    public void openTray(DialogTray tray) throws IllegalStateException, UnsupportedOperationException {
        super.openTray(tray);
        if (tray instanceof HelpTray) {
            HelpTray helpTray = (HelpTray) tray;
            ReusableHelpPart helpPart = helpTray.getHelpPart();
            helpPart.getForm().getForm().notifyListeners(SWT.Activate, new Event());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#create()
     */
    @Override
    public void create() {
        super.create();
        getShell().addShellListener(new ShellAdapter() {

            public void shellActivated(ShellEvent e) {
                if (activeCount < 2 && href != null && href.length() > 0) {
                    Point point = e.widget.getDisplay().getCursorLocation();
                    IContext context = HelpSystem.getContext(HelpPlugin.PATTERN_CONTEXT_HELP_ID);
                    IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
                    helpSystem.displayContext(context, point.x + 15, point.y);
                    activeCount++;
                    ReusableHelpPart lastActiveInstance = ReusableHelpPart.getLastActiveInstance();
                    if (lastActiveInstance != null) {
                        lastActiveInstance.showURL(href);
                    }
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardDialog#close()
     */
    @Override
    public boolean close() {
        activeCount = 0;
        return super.close();
    }
}
