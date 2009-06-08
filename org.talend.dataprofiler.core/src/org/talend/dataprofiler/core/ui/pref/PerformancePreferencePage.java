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
package org.talend.dataprofiler.core.ui.pref;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.indicators.definitions.DefinitionHandler;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class PerformancePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Text pageSizeText;

    private Composite mainComposite;

    public static final String ANALYZED_ITEMS_PER_PAGE = "ANALYZED_ITEMS_PER_PAGE"; //$NON-NLS-1$

    public static final String DEFAULT_PAGE_SIZE = "5";

    @Override
    protected Control createContents(Composite parent) {
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite reportFolderComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout gridLayout2 = new GridLayout(2, false);
        reportFolderComposite.setLayout(gridLayout2);

        GridData gridDataFill = new GridData(GridData.FILL_HORIZONTAL);
        gridDataFill.horizontalSpan = 6;
        reportFolderComposite.setLayoutData(gridDataFill);
        Label dfofLable = new Label(reportFolderComposite, SWT.NONE);
        dfofLable.setText(DefaultMessagesImpl.getString("PerformancePreferencePage.analyzedItemsPerPage")); //$NON-NLS-1$
        GridData gd = new GridData();
        gd.widthHint = 140;
        dfofLable.setLayoutData(gd);

        pageSizeText = new Text(reportFolderComposite, SWT.BORDER);
        String pageSize = ResourcesPlugin.getPlugin().getPluginPreferences().getString(ANALYZED_ITEMS_PER_PAGE);
        if (pageSize == null || pageSize.equals("")) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        pageSizeText.setText(pageSize);
        GridData gd2 = new GridData();
        gd2.widthHint = 100;
        pageSizeText.setLayoutData(gd2);

        return mainComposite;
    }

    public void init(IWorkbench workbench) {
    }

    public PerformancePreferencePage() {
        super();
        // Set the preference store for the preference page.
        IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
    }

    @Override
    public boolean performOk() {
        if (checkPageSize()) {
            ResourcesPlugin.getPlugin().getPluginPreferences().setValue(ANALYZED_ITEMS_PER_PAGE, pageSizeText.getText());
            DefinitionHandler.getInstance().saveResource();
            return super.performOk();
        } else {
            MessageDialogWithToggle.openInformation(getShell(), DefaultMessagesImpl
                    .getString("PerformancePreferencePage.information"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("PerformancePreferencePage.pageSizeMsg")); //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        }
    }

    private boolean checkPageSize() {
        try {
            int pageSize = Integer.parseInt(this.pageSizeText.getText());
            if (pageSize < 1) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
