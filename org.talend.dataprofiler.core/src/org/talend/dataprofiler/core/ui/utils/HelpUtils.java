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
package org.talend.dataprofiler.core.ui.utils;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.ui.internal.views.HelpTray;
import org.eclipse.help.ui.internal.views.ReusableHelpPart;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.IndicatorParameterTypes;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class HelpUtils {

    private static int activeCount;

    public static WizardDialog injectWithHelp(WizardDialog dialog, Wizard wizard, final String helpID,
            final IndicatorUnit indicator) {
        try {

            activeCount = 0;
            // open the dialog
            dialog = new WizardDialog(null, wizard) {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.jface.dialogs.TrayDialog#openTray(org.eclipse.jface.dialogs.DialogTray)
                 */
                @SuppressWarnings("restriction")
                @Override
                public void openTray(DialogTray tray) throws IllegalStateException, UnsupportedOperationException {
                    super.openTray(tray);
                    if (tray instanceof HelpTray) {
                        HelpTray helpTray = (HelpTray) tray;
                        ReusableHelpPart helpPart = helpTray.getHelpPart();
                        helpPart.getForm().getForm().notifyListeners(SWT.Activate, new Event());
                    }
                }

            };
            dialog.setPageSize(300, 400);
            dialog.create();
            dialog.getShell().addShellListener(new ShellAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.ShellAdapter#shellActivated(org.eclipse.swt.events.ShellEvent)
                 */
                @Override
                public void shellActivated(ShellEvent e) {

                    if (activeCount < 2) {
                        Point point = e.widget.getDisplay().getCursorLocation();
                        IContext context = HelpSystem.getContext(helpID);
                        IHelpResource[] relatedTopics = context.getRelatedTopics();
                        for (IHelpResource topic : relatedTopics) {
                            topic.getLabel();
                            topic.getHref();
                        }
                        IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
                        helpSystem.displayContext(context, point.x + 15, point.y);
                        activeCount++;
                        ReusableHelpPart lastActiveInstance = ReusableHelpPart.getLastActiveInstance();
                        if (lastActiveInstance != null) {
                            String href = IndicatorParameterTypes.getHref(indicator);
                            if (href != null) {
                                lastActiveInstance.showURL(href);
                            }
                        }
                    }
                }

            });

            return dialog;
        } catch (AssertionFailedException ex) {
            MessageDialogWithToggle.openInformation(null, "Indicator Option", "No options to set!");
        }

        return null;
    }

    public static Dialog injectWithHelp(WizardDialog dialog, Wizard wizard, String helpID) {
        return injectWithHelp(dialog, wizard, helpID, null);
    }
}
