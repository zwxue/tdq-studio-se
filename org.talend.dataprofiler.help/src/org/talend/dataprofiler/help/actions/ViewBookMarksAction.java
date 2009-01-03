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
package org.talend.dataprofiler.help.actions;

import org.eclipse.help.browser.IBrowser;
import org.eclipse.help.internal.base.BaseHelpSystem;
import org.eclipse.help.internal.server.WebappManager;
import org.eclipse.jface.action.Action;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class ViewBookMarksAction extends Action {

    public ViewBookMarksAction() {
        super("View Bookmarks");
    }

    @Override
    public void run() {

        try {
            // HelpPlugin.getDefault().getWorkbench().getHelpSystem().displayHelp();
            // IWebBrowser browser = HelpPlugin.getDefault().getWorkbench().getBrowserSupport().getExternalBrowser();
            // URL url = new URL("http://127.0.0.1:2754/help/advanced/bookmarksView.jsp?view=bookmarks");
            // browser.openURL(url);

            if (BaseHelpSystem.getInstance().ensureWebappRunning()) {
                IBrowser browser = BaseHelpSystem.getInstance().getHelpBrowser(false);

                browser.displayURL(getFramesetURL());
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getBaseURL() {
        return "http://" //$NON-NLS-1$
                + WebappManager.getHost() + ":" //$NON-NLS-1$
                + WebappManager.getPort() + "/help/"; //$NON-NLS-1$
    }

    private String getFramesetURL() {
        return getBaseURL() + "index.jsp"; //$NON-NLS-1$
    }
}
