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
package org.talend.dataprofiler.core.ui.wizard.analysis;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage;
import org.talend.dq.analysis.parameters.AnalysisParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class AnalysisMetadataWizardPage extends MetadataWizardPage {

    private Text typeText;

    private final String pageTitle = "New Analysis";

    private final String pageMessage = "Adds an analysis in the repository.";

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
        typeLabel.setText("Type");
        typeText = new Text(container, SWT.BORDER);
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
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        super.createControl(container);
        defaultFolderProviderRes = ResourcesPlugin.getWorkspace().getRoot().getProject(PluginConstant.DATA_PROFILING_PROJECTNAME)
                .getFolder(DQStructureManager.ANALYSIS);
        pathText.setText(defaultFolderProviderRes.getFullPath().toString());

        setControl(container);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {

        if (getConnectionParams() != null) {
            if (getConnectionParams() instanceof AnalysisParameter) {

                AnalysisParameter parameters = (AnalysisParameter) getConnectionParams();
                String typeName = parameters.getAnalysisTypeName();
                if (typeName != null) {
                    typeText.setText(typeName);
                }
            }
        }

        super.setVisible(visible);
    }

    @Override
    public boolean checkFieldsValue() {
        if (typeText.getText() == "" || pathText.getText() == "") {
            updateStatus(IStatus.ERROR, MSG_EMPTY);
            return false;
        }

        return super.checkFieldsValue();
    }

}
