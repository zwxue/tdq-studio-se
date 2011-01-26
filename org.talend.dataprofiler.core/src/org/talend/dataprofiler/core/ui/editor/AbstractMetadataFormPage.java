// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.EmfHelper;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.properties.Property;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.helper.PropertyHelper;
import orgomg.cwm.objectmodel.core.CorePackage;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractMetadataFormPage extends AbstractFormPage {

    /**
     * 
     */
    private static final int MAX_TEXT_FIELD_STRING_SIZE_FOR_USUAL_STRING = 200;

    public static final String ACTION_HANDLER = "ACTION_HANDLER"; //$NON-NLS-1$

    private static final int META_FIELD_WIDTH = 200;

    private static final String NAME_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.name"); //$NON-NLS-1$

    private static final String PURPOSE_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.purpose"); //$NON-NLS-1$

    private static final String DESCRIPTION_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.description"); //$NON-NLS-1$

    private static final String AUTHOR_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.author"); //$NON-NLS-1$

    private static final String VERSION_LABEL = "Version:";

    private static final String STATUS_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.status"); //$NON-NLS-1$

    protected Text nameText;

    protected Text purposeText;

    protected Text descriptionText;

    protected Text authorText;

    protected Text versionText;

    protected CCombo statusCombo;

    protected Composite topComp;

    protected Section metadataSection;

    protected ModelElement currentModelElement;

    protected ScrolledForm form;

    private String formTitle;

    private String metadataTitle;

    public AbstractMetadataFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        currentModelElement = getCurrentModelElement(editor);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        form = managedForm.getForm();
        form.setText(getFormTitle());
        Composite body = form.getBody();

        // TableWrapLayout layout = new TableWrapLayout();
        body.setLayout(new GridLayout());

        topComp = toolkit.createComposite(body);
        GridData anasisData = new GridData(GridData.FILL_BOTH);

        topComp.setLayoutData(anasisData);
        topComp.setLayout(new GridLayout());
        metadataSection = creatMetadataSection(form, topComp);
    }

    /**
     * DOC bZhou Comment method "getIntactElemenetName".
     * 
     * @return
     */
    public String getIntactElemenetName() {
        if (currentModelElement == null) {
            currentModelElement = getCurrentModelElement(getEditor());
        }
        return DqRepositoryViewService.buildElementName(currentModelElement);
    }

    protected abstract ModelElement getCurrentModelElement(FormEditor editor);

    protected Section creatMetadataSection(final ScrolledForm form, Composite topComp) {
        Section section = createSection(form, topComp, getMetadataTitle(), ""); //$NON-NLS-1$ //$NON-NLS-2$
        Composite parent = toolkit.createComposite(section);
        parent.setLayout(new GridLayout(2, false));

        nameText = createMetadataTextFiled(NAME_LABEL, parent);
        // set the max number of characters to be entered in the text field
        // ADDED sgandon 16/03/2010 bug 11760
        nameText.setTextLimit(EmfHelper.getStringMaxSize(CorePackage.Literals.MODEL_ELEMENT__NAME,
                MAX_TEXT_FIELD_STRING_SIZE_FOR_USUAL_STRING));

        purposeText = createMetadataTextFiled(PURPOSE_LABEL, parent);
        // set the max number of characters to be entered in the text field
        // ADDED sgandon 16/03/2010 bug 11760
        purposeText.setTextLimit(TaggedValueHelper.getStringMaxSize(TaggedValueHelper.PURPOSE,
                MAX_TEXT_FIELD_STRING_SIZE_FOR_USUAL_STRING));

        // description fields
        // ADDED sgandon 16/03/2010 bug 11760
        toolkit.createLabel(parent, DESCRIPTION_LABEL);

        descriptionText = toolkit.createText(parent, null, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().hint(META_FIELD_WIDTH, 60).applyTo(descriptionText);
        // set the max number of characters to be entered in the text field
        descriptionText.setTextLimit(TaggedValueHelper.getStringMaxSize(TaggedValueHelper.DESCRIPTION,
                MAX_TEXT_FIELD_STRING_SIZE_FOR_USUAL_STRING));

        authorText = createMetadataTextFiled(AUTHOR_LABEL, parent);
        // ADDED 2010-04-01 sgandon bug 11760 : author size limitation
        authorText.setTextLimit(TaggedValueHelper.getStringMaxSize(TaggedValueHelper.AUTHOR,
                MAX_TEXT_FIELD_STRING_SIZE_FOR_USUAL_STRING));

        // MOD 2009-09-08 yyi Feature: 8870.
        if (!ReponsitoryContextBridge.isDefautProject()) {
            authorText.setEnabled(false);
            authorText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        }
        // toolkit.createLabel(parent, VERSION_LABEL);
        // createVersionUI(parent);

        toolkit.createLabel(parent, STATUS_LABEL); //$NON-NLS-1$
        statusCombo = new CCombo(parent, SWT.BORDER);
        statusCombo.setEditable(false);

        // MOD mzhao feature 7479 2009-10-16
        String statusValue = DevelopmentStatus.DRAFT.getLiteral();
        TaggedValue taggedValue = TaggedValueHelper.getTaggedValue(TaggedValueHelper.DEV_STATUS, getCurrentModelElement(
                this.getEditor()).getTaggedValue());
        if (taggedValue != null) {
            statusValue = taggedValue.getValue();
        }

        List<org.talend.core.model.properties.Status> statusList = MetadataHelper.getTechnicalStatus();
        if (statusList != null && statusList.size() > 0) {
            List<String> statusArray = MetadataHelper.toArray(statusList);
            String[] tempString = new String[statusList.size()];
            statusCombo.setItems(statusArray.toArray(tempString));
            if (statusArray.contains(statusValue)) {
                statusCombo.remove(statusValue);
                statusCombo.add(statusValue, 0);
            }
        } else {
            for (DevelopmentStatus status : DevelopmentStatus.values()) {
                statusCombo.add(status.getLiteral());
            }
            // statusCombo.remove(statusValue);
            statusCombo.add(statusValue, 0);
        }

        initMetaTextFied();

        nameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                // fireTextChange();
            }

        });
        purposeText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                // fireTextChange();
            }

        });
        descriptionText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                // fireTextChange();
            }

        });

        authorText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                // fireTextChange();
            }
        });

        // versionText.addModifyListener(new ModifyListener() {
        //
        // /*
        // * (non-Javadoc)
        // *
        // * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
        // */
        // public void modifyText(ModifyEvent e) {
        // setDirty(true);
        // }
        // });

        statusCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                // fireTextChange();
            }

        });

        section.setClient(parent);
        return section;
    }

    /**
     * DOC bZhou Comment method "createVersionUI".
     * 
     * @param parent
     */
    private void createVersionUI(Composite parent) {
        Composite versionContainer = new Composite(parent, SWT.NONE);
        versionContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout versionLayout = new GridLayout(3, false);
        versionLayout.marginHeight = 0;
        versionLayout.marginWidth = 0;
        versionLayout.horizontalSpacing = 0;
        versionContainer.setLayout(versionLayout);

        versionText = new Text(versionContainer, SWT.BORDER);
        versionText.setEnabled(false);
        versionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button versionMajorBtn = new Button(versionContainer, SWT.PUSH);
        versionMajorBtn.setText("M"); //$NON-NLS-1$

        Button versionMinorBtn = new Button(versionContainer, SWT.PUSH);
        versionMinorBtn.setText("m"); //$NON-NLS-1$

        versionMajorBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String version = versionText.getText();
                version = VersionUtils.upMajor(version);
                versionText.setText(version);
            }
        });

        versionMinorBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String version = versionText.getText();
                version = VersionUtils.upMinor(version);
                versionText.setText(version);
            }
        });
    }

    /**
     * DOC bZhou Comment method "createMetadataTextFiled".
     * 
     * @param text
     * @param parent
     * @return MOD sgandon 16/03/2010 bug 11760 : unecessary parameter removed
     */
    private Text createMetadataTextFiled(String label, Composite parent) {
        toolkit.createLabel(parent, label);

        Text text = toolkit.createText(parent, null, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(text);
        ((GridData) text.getLayoutData()).widthHint = META_FIELD_WIDTH;
        return text;
    }

    protected void initMetaTextFied() {
        String name = currentModelElement.getName();
        String purpose = MetadataHelper.getPurpose(currentModelElement);
        String description = MetadataHelper.getDescription(currentModelElement);
        String author = MetadataHelper.getAuthor(currentModelElement);
        String version = MetadataHelper.getVersion(currentModelElement);
        String devStatus = MetadataHelper.getDevStatus(currentModelElement);

        if (currentModelElement instanceof DatabaseConnection) {
            Property property = PropertyHelper.getProperty(currentModelElement);
            if (property != null) {
                purpose = property.getPurpose();
                description = property.getDescription();
                author = property.getAuthor().getLogin();
                version = property.getVersion();
                devStatus = property.getStatusCode();
            }
        }

        nameText.setText(name == null ? PluginConstant.EMPTY_STRING : name);
        purposeText.setText(purpose == null ? PluginConstant.EMPTY_STRING : purpose);
        descriptionText.setText(description == null ? PluginConstant.EMPTY_STRING : description);
        authorText.setText(author == null ? PluginConstant.EMPTY_STRING : author);
        // versionText.setText(version == null ? VersionUtils.DEFAULT_VERSION : version);
        statusCombo.setText(devStatus == null ? PluginConstant.EMPTY_STRING : devStatus);
    }

    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        saveTextChange();
    }

    protected void saveTextChange() {
        currentModelElement.setName(nameText.getText());
        MetadataHelper.setPurpose(purposeText.getText(), currentModelElement);
        MetadataHelper.setDescription(descriptionText.getText(), currentModelElement);
        MetadataHelper.setAuthor(currentModelElement, authorText.getText());
        // MetadataHelper.setVersion(versionText.getText(), currentModelElement);
        MetadataHelper.setDevStatus(currentModelElement, statusCombo.getText());
    }

    public boolean performGlobalAction(String actionId) {
        Control focusControl = getFocusControl();
        if (focusControl == null) {
            return false;
        }
        AbstractAnalysisActionHandler focusPart = getFocusSection();
        if (focusPart != null) {
            return focusPart.doGlobalAction(actionId);
        }
        return false;
    }

    protected Control getFocusControl() {
        IManagedForm form = getManagedForm();
        if (form == null) {
            return null;
        }
        Control control = form.getForm();
        if (control == null || control.isDisposed()) {
            return null;
        }
        Display display = control.getDisplay();
        Control focusControl = display.getFocusControl();
        if (focusControl == null || focusControl.isDisposed()) {
            return null;
        }
        return focusControl;
    }

    private AbstractAnalysisActionHandler getFocusSection() {
        Control focusControl = getFocusControl();
        if (focusControl == null) {
            return null;
        }
        Composite parent = focusControl.getParent();
        AbstractAnalysisActionHandler targetPart = null;
        while (parent != null) {
            Object data = parent.getData(ACTION_HANDLER);
            if (data != null && data instanceof AbstractAnalysisActionHandler) {
                targetPart = (AbstractAnalysisActionHandler) data;
                break;
            }
            parent = parent.getParent();
        }
        return targetPart;
    }

    /**
     * Sets the formTitle.
     * 
     * @param formTitle the formTitle to set
     */
    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    /**
     * Getter for formTitle.
     * 
     * @return the formTitle
     */
    public String getFormTitle() {
        return formTitle == null ? "" : formTitle; //$NON-NLS-1$
    }

    /**
     * Sets the metadataTitle.
     * 
     * @param metadataTitle the metadataTitle to set
     */
    protected void setMetadataTitle(String metadataTitle) {
        this.metadataTitle = metadataTitle;
    }

    /**
     * Getter for metadataTitle.
     * 
     * @return the metadataTitle
     */
    protected String getMetadataTitle() {
        return metadataTitle == null ? "" : metadataTitle; //$NON-NLS-1$
    }
}
