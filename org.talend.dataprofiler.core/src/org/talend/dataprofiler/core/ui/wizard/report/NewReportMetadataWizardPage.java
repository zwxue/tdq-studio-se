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
package org.talend.dataprofiler.core.ui.wizard.report;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage;


/**
 * DOC zqin  class global comment. Detailled comment
 */
public class NewReportMetadataWizardPage extends MetadataWizardPage {

    private final String pageTitle = "New Report Step 1/2";
    
    private final String pageMessage = "Adds an report in the repository.\nOptionnaly one or several analysis can be added to this report.";
    
    /**
     * DOC zqin NewReportMetadataWizardPage constructor comment.
     */
    public NewReportMetadataWizardPage() {
        setTitle(pageTitle);
        setDescription(pageMessage);
        
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.MetadataWizardPage#addListeners()
     */
    @Override
    protected void addListeners() {
        
        button.addSelectionListener(new SelectionAdapter() {

            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                
                openFolderSelectionDialog(DQStructureManager.DATA_PROFILING, DQStructureManager.REPORTS);
            }
        });
        
        super.addListeners();
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.MetadataWizardPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());
        
        super.createControl(container);
        defaultFolderProviderRes = ResourcesPlugin.getWorkspace().getRoot().getProject(PluginConstant.DATA_PROFILING_PROJECTNAME).getFolder(
                DQStructureManager.REPORTS);
        pathText.setText(defaultFolderProviderRes.getFullPath().toString());

        setControl(container);
    }


    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#createExtendedControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createExtendedControl(Composite container) {
        // TODO Auto-generated method stub
        
    }

}
