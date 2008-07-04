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
package org.talend.dataprofiler.core.ui.wizard;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.ui.dialog.FolderSelectionDialog;
import org.talend.dataprofiler.core.ui.dialog.filter.TypedViewerFilter;
import org.talend.dataprofiler.core.ui.utils.CheckValueUtils;
import org.talend.dq.analysis.parameters.IParameterConstant;

/**
 * @author zqin
 * 
 */
public abstract class MetadataWizardPage extends AbstractWizardPage {

    private static Logger log = Logger.getLogger(MetadataWizardPage.class);

    // protected members
    protected Text nameText;

    protected Text purposeText;

    protected Text descriptionText;

    protected Text authorText;

    protected Text versionText;

    protected Button button;

    protected IFolder defaultFolderProviderRes;

    protected CCombo statusText;

    protected Text pathText;

    protected HashMap<String, String> metadata;

    // private members
    private Button versionMajorBtn;

    private Button versionMinorBtn;

    private boolean readOnly;

    private boolean editPath = true;

    public MetadataWizardPage() {

        metadata = new HashMap<String, String>();
        setPageComplete(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.PropertiesWizardPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        // TODO Auto-generated method stub
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gdLayout = new GridLayout(2, false);
        container.setLayout(gdLayout);

        GridData data;

        // Name
        Label nameLab = new Label(container, SWT.NONE);
        nameLab.setText("Name");

        nameText = new Text(container, SWT.BORDER);
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        nameText.setEditable(!readOnly);

        // Purpose
        Label purposeLab = new Label(container, SWT.NONE);
        purposeLab.setText("Purpose");

        purposeText = new Text(container, SWT.BORDER);
        purposeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        purposeText.setEditable(!readOnly);

        // Description
        Label descriptionLab = new Label(container, SWT.NONE);
        descriptionLab.setText("Description");
        descriptionLab.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        descriptionText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = 60;
        descriptionText.setLayoutData(data);
        descriptionText.setEditable(!readOnly);

        // Author
        Label authorLab = new Label(container, SWT.NONE);
        authorLab.setText("Author");

        authorText = new Text(container, SWT.BORDER);
        authorText.setEnabled(!readOnly);
        authorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Version
        // Label versionLab = new Label(container, SWT.NONE);
        // versionLab.setText("Version");
        //
        // Composite versionContainer = new Composite(container, SWT.NONE);
        // versionContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // GridLayout versionLayout = new GridLayout(3, false);
        // versionLayout.marginHeight = 0;
        // versionLayout.marginWidth = 0;
        // versionLayout.horizontalSpacing = 0;
        // versionContainer.setLayout(versionLayout);
        //
        // versionText = new Text(versionContainer, SWT.BORDER);
        // versionText.setEnabled(false);
        // versionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //
        // versionMajorBtn = new Button(versionContainer, SWT.PUSH);
        // versionMajorBtn.setText("M");
        // versionMajorBtn.setEnabled(!readOnly);
        //
        // versionMinorBtn = new Button(versionContainer, SWT.PUSH);
        // versionMinorBtn.setText("m"); //$NON-NLS-1$
        // versionMinorBtn.setEnabled(!readOnly);

        // Status
        Label statusLab = new Label(container, SWT.NONE);
        statusLab.setText("Status"); //$NON-NLS-1$

        statusText = new CCombo(container, SWT.BORDER);
        statusText.setText(DevelopmentStatus.DRAFT.getLiteral());
        statusText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        statusText.setEditable(false);
        statusText.setEnabled(!readOnly);

        for (DevelopmentStatus status : DevelopmentStatus.values()) {

            statusText.add(status.getLiteral());
        }

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
        button.setText("Select..");

        createExtendedControl(container);
        addListeners();

        setControl(container);
    }

    @SuppressWarnings("unchecked")
    protected void openFolderSelectionDialog(String projectName, String folderName) {

        final Class[] acceptedClasses = new Class[] { IProject.class, IFolder.class };
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        ArrayList rejectedElements = new ArrayList();

        if (projectName != null) {
            IProject theProject = root.getProject(projectName);
            IProject[] allProjects = root.getProjects();
            for (int i = 0; i < allProjects.length; i++) {
                if (!allProjects[i].equals(theProject)) {
                    rejectedElements.add(allProjects[i]);
                }
            }

            if (folderName != null) {
                try {
                    IResource[] resourse = theProject.members();
                    for (IResource one : resourse) {
                        if (one.getType() == IResource.FOLDER && !one.getName().equals(folderName)) {
                            rejectedElements.add(one);
                        }
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        }

        ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());

        ILabelProvider lp = new WorkbenchLabelProvider();
        ITreeContentProvider cp = new WorkbenchContentProvider();

        FolderSelectionDialog dialog = new FolderSelectionDialog(getShell(), lp, cp);
        // dialog.setValidator(validator);
        dialog.setTitle("Select folder");
        dialog.setMessage("Select the folder in which the item will be created");
        dialog.setInput(root);
        dialog.addFilter(filter);
        dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

        if (dialog.open() == Window.OK) {
            Object elements = dialog.getResult()[0];
            IResource elem = (IResource) elements;
            if (elem instanceof IFolder) {
                pathText.setText(elem.getFullPath().toString());

                getConnectionParams().getFolderProvider().setFolderResource((IFolder) elem);
                // FolderProvider provider = new FolderProvider();
                // provider.setFolderResource((IFolder) elem);
                // getConnectionParams().setFolderProvider(provider);
            }
        }
    }

    protected void addListeners() {

        nameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                checkFieldsValue();
                if (isStatusOnValid()) {
                    metadata.put(IParameterConstant.ANALYSIS_NAME, nameText.getText());
                    getConnectionParams().setMetadate(metadata);
                }
            }
        });

        purposeText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (purposeText.getText().length() != 0) {
                    metadata.put(IParameterConstant.ANALYSIS_PURPOSE, purposeText.getText());
                    getConnectionParams().setMetadate(metadata);
                }
            }
        });

        descriptionText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (descriptionText.getText().length() != 0) {
                    metadata.put(IParameterConstant.ANALYSIS_DESCRIPTION, descriptionText.getText());
                    getConnectionParams().setMetadate(metadata);
                }

            }
        });

        authorText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                checkFieldsValue();
                if (isStatusOnValid()) {
                    metadata.put(IParameterConstant.ANALYSIS_AUTHOR, authorText.getText());
                    getConnectionParams().setMetadate(metadata);
                }
            }

        });
        // versionMajorBtn.addSelectionListener(new SelectionAdapter() {
        //
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        //
        // }
        // });
        //
        // versionMinorBtn.addSelectionListener(new SelectionAdapter() {
        //
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        //
        // }
        // });

        statusText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String selected = ((CCombo) e.getSource()).getText();
                metadata.put(IParameterConstant.ANALYSIS_STATUS, selected);
                getConnectionParams().setMetadate(metadata);
            }

        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        if (statusText != null) {
            String status = statusText.getText();
            if (status != null) {
                metadata.put(IParameterConstant.ANALYSIS_STATUS, status);
                getConnectionParams().setMetadate(metadata);
            }
        }
        if (defaultFolderProviderRes != null) {
            FolderProvider defaultFolder = new FolderProvider();
            defaultFolder.setFolderResource(defaultFolderProviderRes);
            getConnectionParams().setFolderProvider(defaultFolder);
        }

        super.setVisible(visible);
    }

    @Override
    public boolean checkFieldsValue() {
        if (nameText.getText() == "") {
            updateStatus(IStatus.ERROR, MSG_EMPTY);
            return false;
        }

        if (!CheckValueUtils.isStringValue(nameText.getText())) {
            updateStatus(IStatus.ERROR, MSG_ONLY_CHAR);
            return false;
        }

        updateStatus(IStatus.OK, MSG_OK);
        return super.checkFieldsValue();
    }

    protected abstract void createExtendedControl(Composite container);

}
