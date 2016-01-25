// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.database;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage;
import org.talend.resource.ResourceManager;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class DatabaseMetadataWizardPage extends MetadataWizardPage {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#createControl (org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        pathText.setText(getParameter().getFolderProvider().getFolderURI());
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#
     * createExtendedControl(org.eclipse.swt.widgets.Composite )
     */
    @Override
    protected void createExtendedControl(Composite container) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#addListeners()
     */
    @Override
    protected void addListeners() {
        button.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse .swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                openFolderSelectionDialog(getStoredFolder());
            }
        });

        super.addListeners();
    }

    @Override
    protected IFolder getStoredFolder() {
        return ResourceManager.getConnectionFolder();
    }
}
