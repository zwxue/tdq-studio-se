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

import java.util.HashMap;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage;
import org.talend.dataprofiler.help.HelpPlugin;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class CreatePatternWizardPage1 extends MetadataWizardPage {

    private IFolder defaultFolderProviderRes;

    protected HashMap<String, String> metadata;

    /**
     * DOC qzhang CreateSqlFileWizardPage constructor comment.
     */
    public CreatePatternWizardPage1() {
        metadata = new HashMap<String, String>();
        setPageComplete(false);
        defaultFolderProviderRes = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES).getFolder(
                DQStructureManager.PATTERNS);
    }

    /**
     * DOC qzhang CreatePatternWizardPage1 constructor comment.
     * 
     * @param folder
     */
    public CreatePatternWizardPage1(IFolder folder) {
        this();
        this.defaultFolderProviderRes = folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        super.createControl(container);
        pathText.setText(defaultFolderProviderRes.getFullPath().toString());

        setControl(container);

        button.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                openFolderSelectionDialog(DQStructureManager.LIBRARIES, DQStructureManager.PATTERNS);
            }
        });
        if (getControl() != null) {
            try {
                PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), HelpPlugin.PATTERN_CONTEXT_HELP_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        if (defaultFolderProviderRes != null) {
            FolderProvider folderProvider = getConnectionParams().getFolderProvider();
            if (folderProvider == null) {
                folderProvider = new FolderProvider();
            }
            folderProvider.setFolderResource(defaultFolderProviderRes);
            getConnectionParams().setFolderProvider(folderProvider);
        }

        super.setVisible(visible);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#createExtendedControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createExtendedControl(Composite container) {

    }

    /**
     * Getter for pathText.
     * 
     * @return the pathText
     */
    public Text getPathText() {
        return this.pathText;
    }

}
