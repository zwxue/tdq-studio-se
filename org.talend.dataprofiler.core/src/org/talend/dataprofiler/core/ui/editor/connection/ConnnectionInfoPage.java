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
package org.talend.dataprofiler.core.ui.editor.connection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import org.talend.dataprofiler.core.ui.editor.AbstractFormPage;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ConnnectionInfoPage extends AbstractFormPage {

    private static final String PASSWORD_TAG = "password";

    private static final String USER_TAG = "user";

    private static Logger log = Logger.getLogger(ConnnectionInfoPage.class);

    private TdDataProvider tdDataProvider;

    private Text loginText;

    private Text passwordText;

    public ConnnectionInfoPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    /**
     * Primes the form page with the parent editor instance.
     * 
     * @param editor the parent editor
     */
    public void initialize(FormEditor editor) {
        super.initialize(editor);
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        tdDataProvider = PrvResourceFileHelper.getInstance().getTdProvider(input.getFile()).getObject();
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm form = managedForm.getForm();
        this.metadataSection.setText("Connection Metadata");
        this.metadataSection.setDescription("Set the properties of connnection.");
        createInformationSection(form, topComp);
    }

    /**
     * @param form
     * @param toolkit
     * @param topComp
     */
    void createInformationSection(final ScrolledForm form, Composite topComp) {
        Section section = createSection(form, topComp, "Connection informations", false, "The informations of connection.");

        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout(2, false));
        Label loginLabel = new Label(sectionClient, SWT.NONE);
        loginLabel.setText("Login:");

        loginText = new Text(sectionClient, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(loginText);
        Label passwordLabel = new Label(sectionClient, SWT.NONE);
        passwordLabel.setText("Password:");
        passwordText = new Text(sectionClient, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(passwordText);

        String loginValue = TaggedValueHelper.getValue(USER_TAG, tdDataProvider);
        loginText.setText(loginValue == null ? PluginConstant.EMPTY_STRING : loginValue);
        String passwordValue = TaggedValueHelper.getValue(PASSWORD_TAG, tdDataProvider);
        passwordText.setText(passwordValue == null ? PluginConstant.EMPTY_STRING : passwordValue);

        Label urlLabel = new Label(sectionClient, SWT.NONE);
        urlLabel.setText("Url:");
        Text urlText = new Text(sectionClient, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(urlText);
        TypedReturnCode<TdProviderConnection> trc = DataProviderHelper.getTdProviderConnection(tdDataProvider);
        String urlValue = (trc.isOk()) ? trc.getObject().getConnectionString() : PluginConstant.EMPTY_STRING;
        urlText.setText(urlValue == null ? PluginConstant.EMPTY_STRING : urlValue);
        urlText.setEnabled(false);

        ModifyListener listener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                fireTextChange();
            }

        };
        loginText.addModifyListener(listener);
        passwordText.addModifyListener(listener);
        section.setClient(sectionClient);
    }

    @Override
    protected void fireTextChange() {
        this.tdDataProvider.setName(nameText.getText());
        TaggedValueHelper.setPurpose(purposeText.getText(), this.tdDataProvider);
        TaggedValueHelper.setDescription(descriptionText.getText(), this.tdDataProvider);
        TaggedValueHelper.setAuthor(this.tdDataProvider, authorText.getText());
        TaggedValueHelper.setDevStatus(this.tdDataProvider, DevelopmentStatus.get(statusCombo.getText()));
        TaggedValueHelper.setTaggedValue(tdDataProvider, USER_TAG, loginText.getText());
        TaggedValueHelper.setTaggedValue(tdDataProvider, PASSWORD_TAG, passwordText.getText());

    }

    @Override
    protected void initMetaTextFied() {
        nameText.setText(tdDataProvider.getName() == null ? PluginConstant.EMPTY_STRING : tdDataProvider.getName());
        String purpose = TaggedValueHelper.getPurpose(tdDataProvider);
        purposeText.setText(purpose == null ? PluginConstant.EMPTY_STRING : purpose);
        String description = TaggedValueHelper.getDescription(tdDataProvider);
        descriptionText.setText(description == null ? PluginConstant.EMPTY_STRING : description);
        String author = TaggedValueHelper.getAuthor(tdDataProvider);
        authorText.setText(author == null ? PluginConstant.EMPTY_STRING : author);
        DevelopmentStatus devStatus = TaggedValueHelper.getDevStatus(tdDataProvider);
        statusCombo.setText(devStatus == null ? PluginConstant.EMPTY_STRING : devStatus.getLiteral());
    }

    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((ConnectionEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }

    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        try {
            saveConnectionInfo();
            this.isDirty = false;
        } catch (DataprofilerCoreException e) {
            ExceptionHandler.process(e, Level.ERROR);
            e.printStackTrace();
        }
    }

    private void saveConnectionInfo() throws DataprofilerCoreException {
        ReturnCode returnCode = DqRepositoryViewService.saveOpenDataProvider(this.tdDataProvider);
        if (returnCode.isOk()) {
            log.info("Saved in  " + tdDataProvider.eResource().getURI().toFileString() + " successful");
        } else {
            throw new DataprofilerCoreException("Problem saving file: " + tdDataProvider.eResource().getURI().toFileString()
                    + ": " + returnCode.getMessage());
        }
    }

}
