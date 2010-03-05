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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.help.ui.internal.views.HelpTray;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Shell;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.pref.WebBrowserPreferencePage;
import org.talend.dataprofiler.help.HelpPlugin;

/**
 * DOC bZhou class global comment. Detailled comment
 * 
 * open a dialog with one specified context help.
 * 
 */
public class OpeningHelpWizardDialog extends WizardDialog {

    private String href;

    private WizardPage page;

    /**
     * DOC bZhou OpeningHelpWizardDialog constructor comment.
     * 
     * @param parentShell
     * @param newWizard
     * @param href
     */
    public OpeningHelpWizardDialog(Shell parentShell, IWizard newWizard, String href) {
        this(parentShell, newWizard, href, null);
    }

    /**
     * DOC bZhou OpeningHelpWizardDialog constructor comment.
     * 
     * @param parentShell
     * @param newWizard
     * @param href
     * @param page
     */
    public OpeningHelpWizardDialog(Shell parentShell, IWizard newWizard, String href, WizardPage page) {
        super(parentShell, newWizard);
        this.href = href;
        this.page = page;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#create()
     */
    @Override
    public void create() {
        super.create();

        if (href != null && page == null) {
            showHelp();
        }
    }

    @Override
    protected void nextPressed() {
        super.nextPressed();

        if (getCurrentPage() != null && this.page != null && getCurrentPage() == page) {
            showHelp();
        }
    }

    /**
     * DOC bZhou Comment method "showHelp".
     */
    public void showHelp() {

        boolean isBlock = Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID,
                WebBrowserPreferencePage.BLOCK_WEB_BROWSER, true, new IScopeContext[] { new InstanceScope() });

        if (isValidHref(href) && !isBlock) {
            if (getTray() == null) {
                openTray(new HelpTray());
            }

            ((HelpTray) getTray()).getHelpPart().showURL(href);
        }

    }

    /**
     * DOC bZhou Comment method "isValidHref".
     * 
     * @param href
     * @return
     */
    private boolean isValidHref(String href) {
        return href != null && href.endsWith(HelpPlugin.HELP_FILE_SUFFIX);
    }

    /**
     * Sets the href.
     * 
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }
}
