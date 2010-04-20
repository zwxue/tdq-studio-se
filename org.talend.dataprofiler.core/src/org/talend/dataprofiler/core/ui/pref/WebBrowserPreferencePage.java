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
package org.talend.dataprofiler.core.ui.pref;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.dataprofiler.core.CorePlugin;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class WebBrowserPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Button blockWebBtn;

    private boolean isBlockWeb;

    public static final String BLOCK_WEB_BROWSER = "BLOCK WEB BROWSER";

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());
        top.setLayoutData(new GridData(GridData.FILL_BOTH));

        blockWebBtn = new Button(top, SWT.CHECK);
        blockWebBtn.setText("Block browser help ");
        blockWebBtn.setSelection(isBlockWeb);
        blockWebBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                isBlockWeb = blockWebBtn.getSelection();
            }
        });

        return top;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        isBlockWeb = Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID, BLOCK_WEB_BROWSER, false,
                new IScopeContext[] { new InstanceScope() });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        blockWebBtn.setSelection(false);
        isBlockWeb = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        IEclipsePreferences node = new InstanceScope().getNode(CorePlugin.PLUGIN_ID);
        if (node != null) {
            node.putBoolean(BLOCK_WEB_BROWSER, isBlockWeb);
        }

        return super.performOk();
    }
}
