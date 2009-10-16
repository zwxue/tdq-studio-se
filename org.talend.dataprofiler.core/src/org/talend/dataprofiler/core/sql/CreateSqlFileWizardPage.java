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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.dialog.FolderSelectionDialog;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dq.analysis.parameters.SqlFileParameter;
import org.talend.resource.ResourceManager;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class CreateSqlFileWizardPage extends WizardPage {

    static Logger log = Logger.getLogger(CreateSqlFileWizardPage.class);

    private Text nameText;

    private Text pathText;

    private Button button;

    private SqlFileParameter parameter;

    protected HashMap<String, String> metadata;

    /**
     * DOC qzhang CreateSqlFileWizardPage constructor comment.
     * 
     * @param folder
     */
    public CreateSqlFileWizardPage(SqlFileParameter parameter) {
        super(""); //$NON-NLS-1$

        this.parameter = parameter;
        metadata = new HashMap<String, String>();
        setPageComplete(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets .Composite)
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

        pathText.setText(parameter.getFolderProvider().getFolderURI());

        addListeners();

        setControl(container);
    }

    /**
     * DOC bzhou Comment method "addListeners".
     */
    private void addListeners() {
        nameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                parameter.setFileName(nameText.getText());
                setPageComplete(true);
            }
        });

        button.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse .swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                openFolderSelectionDialog(ResourceManager.LIBRARIES_FOLDER_NAME, DQStructureManager.SOURCE_FILES);
            }
        });
    }

    /**
     * Getter for pathText.
     * 
     * @return the pathText
     */
    public Text getPathText() {
        return this.pathText;
    }

    protected void openFolderSelectionDialog(String projectName, String folderName) {
        IProject rootProject = ResourceManager.getRootProject();
        IFolder inputFolder = rootProject.getFolder(projectName).getFolder(folderName);

        FolderSelectionDialog dialog = new FolderSelectionDialog(getShell());
        dialog.setTitle(DefaultMessagesImpl.getString("MetadataWizardPage.selectFolder")); //$NON-NLS-1$
        dialog.setMessage(DefaultMessagesImpl.getString("MetadataWizardPage.selectFolderItem")); //$NON-NLS-1$
        dialog.setInput(inputFolder);
        dialog.addFilter(new DQFolderFliter());
        dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

        if (dialog.open() == Window.OK) {
            if (dialog.getResult() == null || dialog.getResult().length == 0) {
                return;
            }
            Object elements = dialog.getResult()[0];
            IResource elem = (IResource) elements;
            if (elem instanceof IFolder) {
                pathText.setText(elem.getFullPath().toString());

                parameter.getFolderProvider().setFolderResource((IFolder) elem);
            }
        }
    }
}
