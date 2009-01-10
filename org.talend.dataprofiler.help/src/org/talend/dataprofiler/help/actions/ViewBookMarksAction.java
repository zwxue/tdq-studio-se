package org.talend.dataprofiler.help.actions;

import org.eclipse.help.internal.base.BaseHelpSystem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.help.TdHelpView;

@SuppressWarnings("restriction")
public class ViewBookMarksAction implements IWorkbenchWindowActionDelegate {

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public void init(IWorkbenchWindow window) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings( { "static-access" })
    public void run(IAction action) {
        try {

            if (BaseHelpSystem.getInstance().ensureWebappRunning()) {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (window != null) {
                    IWorkbenchPage page = window.getActivePage();
                    if (page != null) {
                        try {
                            IViewPart part = page.findView(TdHelpView.HELP_VIEW_ID);
                            if (part == null) {
                                page.showView(TdHelpView.HELP_VIEW_ID);
                            }
                        } catch (PartInitException e) {
                        }
                    }
                }
                // DefaultHelpUI.getInstance().displayHelp();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // TODO Auto-generated method stub

    }

}
