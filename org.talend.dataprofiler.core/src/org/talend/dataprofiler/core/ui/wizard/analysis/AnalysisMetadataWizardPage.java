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
package org.talend.dataprofiler.core.ui.wizard.analysis;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage;
import org.talend.dq.analysis.parameters.AnalysisParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class AnalysisMetadataWizardPage extends MetadataWizardPage {

    private Text typeText;

    private final String pageTitle = DefaultMessagesImpl.getString("AnalysisMetadataWizardPage.newAnalysis"); //$NON-NLS-1$

    private final String pageMessage = DefaultMessagesImpl.getString("AnalysisMetadataWizardPage.addsAnalysis"); //$NON-NLS-1$

    public AnalysisMetadataWizardPage() {
        setTitle(pageTitle);
        setDescription(pageMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#createExtendedControl()
     */
    @Override
    protected void createExtendedControl(Composite container) {
        GridData dataForTypeText = new GridData();
        dataForTypeText.widthHint = 200;

        Label typeLabel = new Label(container, SWT.NONE);
        typeLabel.setText(DefaultMessagesImpl.getString("AnalysisMetadataWizardPage.type")); //$NON-NLS-1$

        typeText = new Text(container, SWT.BORDER);
        AnalysisParameter parameter = (AnalysisParameter) getParameter();
        typeText.setText(parameter.getAnalysisTypeName() != null ? parameter.getAnalysisTypeName() : ""); //$NON-NLS-1$
        typeText.setLayoutData(dataForTypeText);
        typeText.setEnabled(false);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        pathText.setText(getParameter().getFolderProvider().getFolderURI());
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
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {

                openFolderSelectionDialog(DQStructureManager.DATA_PROFILING, DQStructureManager.ANALYSIS);
            }
        });

        super.addListeners();
    }

    @Override
    public boolean checkFieldsValue() {
        if (typeText.getText() == "" || pathText.getText() == "") { //$NON-NLS-1$ //$NON-NLS-2$
            updateStatus(IStatus.ERROR, MSG_EMPTY);
            return false;
        }

        return super.checkFieldsValue();
    }

    @Override
    protected IFolder getStoredFolder() {
        // MOD mzhao 2009-03-13 Feature 6066 Move all folders into single project.
        return ResourcesPlugin.getWorkspace().getRoot().getProject(org.talend.dataquality.PluginConstant.ROOTPROJECTNAME).getFolder(
                PluginConstant.DATA_PROFILING_PROJECTNAME).getFolder(DQStructureManager.ANALYSIS);
    }

}
