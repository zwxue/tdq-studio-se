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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.ui.internal.views.HelpTray;
import org.eclipse.help.ui.internal.views.ReusableHelpPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.help.HelpPlugin;
import org.talend.dataquality.domain.pattern.ExpressionType;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class CreatePatternAction extends Action {

    private IFolder folder;

    private static int activeCount = 0;

    private ExpressionType type;

    /**
     * DOC qzhang AddSqlFileAction constructor comment.
     * 
     * @param folder
     * @param type
     */
    public CreatePatternAction(IFolder folder, ExpressionType type) {
        switch (type) {
        case SQL_LIKE:
            setText("Create a new sql pattern");
            break;
        default:
            setText("Create a new regular pattern");
            break;
        }
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.PATTERN_REG));
        this.folder = folder;
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        CreatePatternWizard fileWizard = new CreatePatternWizard(folder, type) {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.jface.wizard.Wizard#dispose()
             */
            public void dispose() {
                activeCount = 0;
                super.dispose();
            }
        };
        WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), fileWizard) {

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

        };
        fileWizard.setWindowTitle(getText());
        dialog.create();
        dialog.getShell().addShellListener(new ShellAdapter() {

            public void shellActivated(ShellEvent e) {
                if (activeCount < 2) {
                    Point point = e.widget.getDisplay().getCursorLocation();
                    IContext context = HelpSystem.getContext(HelpPlugin.PATTERN_CONTEXT_HELP_ID);
                    IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
                    helpSystem.displayContext(context, point.x + 15, point.y);
                    activeCount++;
                    ReusableHelpPart lastActiveInstance = ReusableHelpPart.getLastActiveInstance();
                    if (lastActiveInstance != null) {
                        IHelpResource[] relatedTopics = context.getRelatedTopics();
                        String href = relatedTopics[0].getHref();
                        switch (type) {
                        case SQL_LIKE:
                            href = relatedTopics[1].getHref();
                            break;
                        default:
                            break;
                        }
                        lastActiveInstance.showURL(href);
                    }
                }
            }
        });
        if (WizardDialog.OK == dialog.open()) {
            try {
                folder.refreshLocal(IResource.DEPTH_INFINITE, null);
                IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fileWizard.getLocation());
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(file),
                        PluginConstant.PATTERN_EDITOR);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }
}
