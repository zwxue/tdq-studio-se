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
package org.talend.dataprofiler.core.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Status;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.FolderSelectionDialog;
import org.talend.dataprofiler.core.ui.dialog.filter.TypedViewerFilter;
import org.talend.dataprofiler.core.ui.utils.UIMessages;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

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

    protected CCombo statusText;

    protected Text pathText;

    private final static String PROJECT_FILE = "talend.project";

    // private members
    private Button versionMajorBtn;

    private Button versionMinorBtn;

    public MetadataWizardPage() {
        this.setPageComplete(false);
    }

    private List<Status> getTechnicalStatus() {
        org.talend.core.model.properties.Project loadProject = null;
        try {
            loadProject = loadProject();
        } catch (PersistenceException e) {
            log.error(e, e);
            return null;
        }
        if (loadProject == null) {
            return null;
        }

        return copyList(loadProject.getTechnicalStatus());
    }

    private Project loadProject() throws PersistenceException {
        IProject rootProject = ResourceManager.getRootProject();
        IFile talendProjectFile = rootProject.getFile(PROJECT_FILE);
        if (!talendProjectFile.exists()) {
            return null;
        }
        URI uri = URI.createPlatformResourceURI(talendProjectFile.getFullPath().toString(), false);
        EMFSharedResources.getInstance().unloadResource(uri.toString());
        Resource rs = EMFSharedResources.getInstance().getResource(uri, true);
        Project emfProject = (Project) EcoreUtil.getObjectByType(rs.getContents(), PropertiesPackage.eINSTANCE.getProject());
        return emfProject;
    }

    private List<Status> copyList(List<Status> listOfStatus) {
        List<Status> result = new ArrayList<Status>();
        for (Object obj : listOfStatus) {
            result.add((Status) obj);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.PropertiesWizardPage#createControl
     * (org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {

        if (getParameter().getFolderProvider() == null || getParameter().getFolderProvider().isNull()) {
            FolderProvider defaultFolder = new FolderProvider();
            defaultFolder.setFolderResource(getStoredFolder());
            getParameter().setFolderProvider(defaultFolder);
        }

        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gdLayout = new GridLayout(2, false);
        container.setLayout(gdLayout);

        GridData data;

        // Name
        Label nameLab = new Label(container, SWT.NONE);
        nameLab.setText(DefaultMessagesImpl.getString("MetadataWizardPage.name")); //$NON-NLS-1$

        nameText = new Text(container, SWT.BORDER);
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Purpose
        Label purposeLab = new Label(container, SWT.NONE);
        purposeLab.setText(DefaultMessagesImpl.getString("MetadataWizardPage.purpose")); //$NON-NLS-1$

        purposeText = new Text(container, SWT.BORDER);
        purposeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Description
        Label descriptionLab = new Label(container, SWT.NONE);
        descriptionLab.setText(DefaultMessagesImpl.getString("MetadataWizardPage.description")); //$NON-NLS-1$
        descriptionLab.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        descriptionText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = 60;
        descriptionText.setLayoutData(data);

        // Author
        Label authorLab = new Label(container, SWT.NONE);
        authorLab.setText(DefaultMessagesImpl.getString("MetadataWizardPage.author")); //$NON-NLS-1$

        authorText = new Text(container, SWT.BORDER);
        authorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        String author = ReponsitoryContextBridge.getAuthor();
        authorText.setText(author);
        getParameter().setAuthor(author);
        // MOD 2009-09-08 yyi Feature: 8870.
        authorText.setEnabled(isDefaultProject());
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
        // versionText.setText(getParameter().getVersion());
        //
        // versionMajorBtn = new Button(versionContainer, SWT.PUSH);
        // versionMajorBtn.setText("M");
        //
        // versionMinorBtn = new Button(versionContainer, SWT.PUSH);
        //        versionMinorBtn.setText("m"); //$NON-NLS-1$

        // Status
        Label statusLab = new Label(container, SWT.NONE);
        statusLab.setText("Status"); //$NON-NLS-1$

        statusText = new CCombo(container, SWT.BORDER);
        // statusText.setText(DevelopmentStatus.DRAFT.getLiteral());
        statusText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        statusText.setEditable(false);
        // MOD mzhao feature 7479 2009-10-12
        List<org.talend.core.model.properties.Status> statusList;
        statusList = getTechnicalStatus();
        if (statusList != null && statusList.size() > 0) {
            statusText.setItems(toArray(statusList));
        } else {
            for (DevelopmentStatus status : DevelopmentStatus.values()) {
                statusText.add(status.getLiteral());
            }
        }
        statusText.select(0);
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
        button.setText(DefaultMessagesImpl.getString("MetadataWizardPage.select")); //$NON-NLS-1$

        createExtendedControl(container);
        addListeners();

        setControl(container);
    }

    private String[] toArray(List<org.talend.core.model.properties.Status> status) {
        String[] res = new String[status.size()];
        int i = 0;
        for (org.talend.core.model.properties.Status s : status) {
            res[i++] = s.getLabel();
        }
        return res;
    }

    /**
     * DOC yyi Comment method "setAuthorTextEditable" Feature: 8870.
     */
    private boolean isDefaultProject() {
        if (null != ReponsitoryContextBridge.getProjectName()
                && "TOP_DEFAULT_PRJ".equals(ReponsitoryContextBridge.getProjectName())) { //$NON-NLS-1$
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected void openFolderSelectionDialog(String projectName, String folderName) {

        final Class[] acceptedClasses = new Class[] { IProject.class, IFolder.class };
        ArrayList rejectedElements = new ArrayList();

        if (projectName != null) {
            // MOD mzhao 2009-03-13 Move all folders into one single project
            // {@link CorePlugin#ROOTPROJECTNAME}
            IFolder theFolder = ResourceManager.getRootProject().getFolder(projectName);
            IResource[] allFolders = null;
            try {
                allFolders = ResourceManager.getRootProject().members();
            } catch (CoreException e) {
                log.error(e, e);
            }
            for (int i = 0; i < allFolders.length; i++) {
                if (!allFolders[i].equals(theFolder)) {
                    rejectedElements.add(allFolders[i]);
                }
            }

            if (folderName != null) {
                try {
                    IResource[] resourse = theFolder.members();
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
        dialog.setTitle(DefaultMessagesImpl.getString("MetadataWizardPage.selectFolder")); //$NON-NLS-1$
        dialog.setMessage(DefaultMessagesImpl.getString("MetadataWizardPage.selectFolderItem")); //$NON-NLS-1$
        dialog.setInput(ResourceManager.getRootProject());
        dialog.addFilter(filter);
        dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

        if (dialog.open() == Window.OK) {
            if (dialog.getResult() == null || dialog.getResult().length == 0) {
                return;
            }
            Object elements = dialog.getResult()[0];
            IResource elem = (IResource) elements;
            if (elem instanceof IFolder) {
                pathText.setText(elem.getFullPath().toString());

                getParameter().getFolderProvider().setFolderResource((IFolder) elem);
            }
        }
    }

    protected void addListeners() {

        nameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                getParameter().setName(nameText.getText());
                fireEvent();
            }
        });

        purposeText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                getParameter().setPurpose(purposeText.getText());
            }
        });

        descriptionText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                getParameter().setDescription(descriptionText.getText());
            }
        });

        authorText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                getParameter().setAuthor(authorText.getText());
            }

        });

        // versionMajorBtn.addSelectionListener(new SelectionAdapter() {
        //
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // String version = getParameter().getVersion();
        // version = VersionUtils.upMajor(version);
        // versionText.setText(version);
        // getParameter().setVersion(version);
        // }
        // });
        //
        // versionMinorBtn.addSelectionListener(new SelectionAdapter() {
        //
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // String version = getParameter().getVersion();
        // version = VersionUtils.upMinor(version);
        // versionText.setText(version);
        // getParameter().setVersion(version);
        // }
        // });

        statusText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String selected = ((CCombo) e.getSource()).getText();
                getParameter().setStatus(selected);
            }

        });
    }

    @Override
    public boolean checkFieldsValue() {
        if (nameText.getText().trim() == "") { //$NON-NLS-1$
            updateStatus(IStatus.ERROR, MSG_EMPTY);
            return false;
        }

        if (nameText.getText().contains(" ")) { //$NON-NLS-1$
            updateStatus(IStatus.ERROR, MSG_INVALID);
            return false;
        }

        // if (!checkDuplicateModelName()) {
        // return false;
        // }

        updateStatus(IStatus.OK, MSG_OK);
        return super.checkFieldsValue();
    }

    protected abstract void createExtendedControl(Composite container);

    protected abstract IFolder getStoredFolder();

    protected void fireEvent() {
        setPageComplete(checkFieldsValue());
    }

    /**
     * DOC yyi Comment method "checkDuplicateModelName".
     * 
     * @return
     */
    public boolean checkDuplicateModelName() {
        String elementName = getParameter().getName();
        IFolder folderResource = getParameter().getFolderProvider().getFolderResource();
        if (elementName == null || folderResource == null) {
            // updateStatus(IStatus.ERROR, MSG_EMPTY);
            //            return false; //$NON-NLS-1$
        } else {

            Collection<ModelElement> modelElements = new ArrayList<ModelElement>();

            switch (getParameter().getParamType()) {
            case ANALYSIS:
                modelElements.addAll(AnaResourceFileHelper.getInstance().getAllAnalysis(folderResource));
                break;
            case REPORT:
                modelElements.addAll(RepResourceFileHelper.getInstance().getAllReports(folderResource));
                break;
            case PATTERN:
                modelElements.addAll(PatternResourceFileHelper.getInstance().getAllPatternes(folderResource));
                break;
            case DBCONNECTON:
                modelElements.addAll(PrvResourceFileHelper.getInstance().getAllDataProviders(folderResource));
                break;
            case DQRULE:
                modelElements.addAll(DQRuleResourceFileHelper.getInstance().getAllDQRules(folderResource));
                break;
            case UDINDICATOR:
                modelElements.addAll(UDIResourceFileHelper.getInstance().getAllUDIs(folderResource));
                break;
            default:
                break;
            }

            if (!modelElements.isEmpty()) {
                for (ModelElement element : modelElements) {
                    if (element.getName().equals(elementName)) {
                        updateStatus(IStatus.ERROR, UIMessages.MSG_ANALYSIS_SAME_NAME);
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
