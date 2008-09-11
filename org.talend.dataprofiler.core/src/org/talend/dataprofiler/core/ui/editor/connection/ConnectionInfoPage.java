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

import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ConnectionInfoPage extends AbstractMetadataFormPage {

    private static Logger log = Logger.getLogger(ConnectionInfoPage.class);

    private TdDataProvider tdDataProvider;

    private Text loginText;

    private Text passwordText;

    private Text urlText;

    public ConnectionInfoPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        tdDataProvider = PrvResourceFileHelper.getInstance().findProvider(input.getFile()).getObject();
        return tdDataProvider;
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm form = managedForm.getForm();
        form.setText("Connection Settings");
        this.metadataSection.setText("Connection Metadata");
        this.metadataSection.setDescription("Set the properties of connnection.");
        createInformationSection(form, topComp);

        Button checkBtn = toolkit.createButton(topComp, " Check ", SWT.NONE);
        GridData gd = new GridData();
        // gd.horizontalSpan = 2;
        gd.verticalSpan = 20;
        gd.horizontalAlignment = SWT.CENTER;
        checkBtn.setLayoutData(gd);

        checkBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ReturnCode code = checkDBConnection();
                if (code.isOk()) {
                    MessageDialog.openInformation(null, "check connections", "Check connection successful.");
                } else {
                    MessageDialog.openInformation(null, "check connections", "Check connection failure:" + code.getMessage());
                }
            }

        });
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
        passwordText = new Text(sectionClient, SWT.BORDER | SWT.PASSWORD);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(passwordText);

        TdProviderConnection connection = DataProviderHelper.getTdProviderConnection(tdDataProvider).getObject();
        String loginValue = TaggedValueHelper.getValue(PluginConstant.USER_PROPERTY, connection);
        loginText.setText(loginValue == null ? PluginConstant.EMPTY_STRING : loginValue);

        String passwordValue = TaggedValueHelper.getValue(PluginConstant.PASSWORD_PROPERTY, connection);
        passwordText.setText(passwordValue == null ? PluginConstant.EMPTY_STRING : passwordValue);

        Label urlLabel = new Label(sectionClient, SWT.NONE);
        urlLabel.setText("Url:");
        urlText = new Text(sectionClient, SWT.BORDER);
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

    private ReturnCode checkDBConnection() {
        Properties props = new Properties();
        props.put(PluginConstant.USER_PROPERTY, loginText.getText());
        props.put(PluginConstant.PASSWORD_PROPERTY, passwordText.getText());
        TdProviderConnection connection = DataProviderHelper.getTdProviderConnection(tdDataProvider).getObject();
        ReturnCode returnCode = ConnectionService.checkConnection(this.urlText.getText(), connection.getDriverClassName(), props);
        return returnCode;
    }

    @Override
    protected void fireTextChange() {
        this.tdDataProvider.setName(nameText.getText());
        TaggedValueHelper.setPurpose(purposeText.getText(), this.tdDataProvider);
        TaggedValueHelper.setDescription(descriptionText.getText(), this.tdDataProvider);
        TaggedValueHelper.setAuthor(this.tdDataProvider, authorText.getText());
        TaggedValueHelper.setDevStatus(this.tdDataProvider, DevelopmentStatus.get(statusCombo.getText()));

        TdProviderConnection connection = DataProviderHelper.getTdProviderConnection(tdDataProvider).getObject();
        TaggedValueHelper.setTaggedValue(connection, PluginConstant.USER_PROPERTY, loginText.getText());
        TaggedValueHelper.setTaggedValue(connection, PluginConstant.PASSWORD_PROPERTY, passwordText.getText());
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
        ReturnCode returnCode = PrvResourceFileHelper.getInstance().save(tdDataProvider);
        if (returnCode.isOk()) {
            log.info("Saved in  " + tdDataProvider.eResource().getURI().toFileString() + " successful");
        } else {
            throw new DataprofilerCoreException("Problem saving file: " + tdDataProvider.eResource().getURI().toFileString()
                    + ": " + returnCode.getMessage());
        }
    }

}
