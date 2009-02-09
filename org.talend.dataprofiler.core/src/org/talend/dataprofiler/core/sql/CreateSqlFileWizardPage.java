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
package org.talend.dataprofiler.core.sql;

import java.util.HashMap;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class CreateSqlFileWizardPage extends MetadataWizardPage {

    private Text nameText;

    private Button button;

    protected HashMap<String, String> metadata;

    /**
     * DOC qzhang CreateSqlFileWizardPage constructor comment.
     * 
     * @param folder
     */
    public CreateSqlFileWizardPage() {
        metadata = new HashMap<String, String>();
        setPageComplete(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gdLayout = new GridLayout(2, false);
        container.setLayout(gdLayout);

        // Name
        Label nameLab = new Label(container, SWT.NONE);
        nameLab.setText(DefaultMessagesImpl.getString("CreateSqlFileWizardPage.names")); //$NON-NLS-1$

        nameText = new Text(container, SWT.BORDER);
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Path:
        Label pathLab = new Label(container, SWT.NONE);
        pathLab.setText("Path"); //$NON-NLS-1$

        Composite pathContainer = new Composite(container, SWT.NONE);
        pathContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout pathLayout = new GridLayout(2, false);
        pathLayout.marginHeight = 0;
        pathLayout.marginWidth = 0;
        pathLayout.horizontalSpacing = 0;
        pathContainer.setLayout(pathLayout);

        pathText = new Text(pathContainer, SWT.BORDER);
        pathText.setEnabled(false);
        pathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        button = new Button(pathContainer, SWT.PUSH);
        button.setText(DefaultMessagesImpl.getString("CreateSqlFileWizardPage.select_1")); //$NON-NLS-1$

        pathText.setText(getParameter().getFolderProvider().getFolderURI());
        setControl(container);
        nameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                getParameter().setName(nameText.getText());
                setPageComplete(true);
            }
        });

        button.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                openFolderSelectionDialog(DQStructureManager.LIBRARIES, DQStructureManager.SOURCE_FILES);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.wizard.MetadataWizardPage#createExtendedControl(org.eclipse.swt.widgets.Composite
     * )
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

    @Override
    protected IFolder getStoredFolder() {
        return null;
    }

}
