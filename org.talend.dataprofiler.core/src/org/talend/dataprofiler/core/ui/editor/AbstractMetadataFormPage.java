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
package org.talend.dataprofiler.core.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractMetadataFormPage extends AbstractFormPage {

    public static final String ACTION_HANDLER = "ACTION_HANDLER";

    protected Text nameText;

    protected Text purposeText;

    protected Text descriptionText;

    protected Text authorText;

    protected CCombo statusCombo;

    protected Composite topComp;

    protected Section metadataSection;

    protected ModelElement currentModelElement;

    public AbstractMetadataFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        currentModelElement = getCurrentModelElement(editor);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        Composite body = form.getBody();

        // TableWrapLayout layout = new TableWrapLayout();
        body.setLayout(new GridLayout());

        topComp = toolkit.createComposite(body);
        GridData anasisData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);

        topComp.setLayoutData(anasisData);
        topComp.setLayout(new GridLayout(1, false));
        metadataSection = creatMetadataSection(form, topComp);
    }

    protected abstract ModelElement getCurrentModelElement(FormEditor editor);

    protected Section creatMetadataSection(final ScrolledForm form, Composite topComp) {
        Section section = createSection(form, topComp, "Title", true, "");
        Composite labelButtonClient = toolkit.createComposite(section);
        labelButtonClient.setLayout(new GridLayout(2, false));
        Label label = toolkit.createLabel(labelButtonClient, "Name:");
        label.setLayoutData(new GridData());
        nameText = toolkit.createText(labelButtonClient, null, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(nameText);
        label = toolkit.createLabel(labelButtonClient, "Purpose:");
        label.setLayoutData(new GridData());
        purposeText = toolkit.createText(labelButtonClient, null, SWT.BORDER);
        // purposeText.setLayoutData(new GridData());
        GridDataFactory.fillDefaults().grab(true, true).applyTo(purposeText);
        label = toolkit.createLabel(labelButtonClient, "Description:");
        label.setLayoutData(new GridData());
        descriptionText = toolkit.createText(labelButtonClient, null, SWT.BORDER);
        // descriptionText.setLayoutData(new GridData());
        GridDataFactory.fillDefaults().grab(true, true).applyTo(descriptionText);
        label = toolkit.createLabel(labelButtonClient, "Author:");
        authorText = toolkit.createText(labelButtonClient, null, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(authorText);
        label = toolkit.createLabel(labelButtonClient, "Status:");
        statusCombo = new CCombo(labelButtonClient, SWT.BORDER);
        statusCombo.setEditable(false);
        for (DevelopmentStatus status : DevelopmentStatus.values()) {

            statusCombo.add(status.getLiteral());
        }
        initMetaTextFied();
        nameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                fireTextChange();
            }

        });
        purposeText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                fireTextChange();
            }

        });
        descriptionText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                fireTextChange();
            }

        });

        authorText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                fireTextChange();
            }
        });

        statusCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                fireTextChange();
            }

        });

        section.setClient(labelButtonClient);
        return section;
    }

    protected void initMetaTextFied() {
        nameText.setText(currentModelElement.getName() == null ? PluginConstant.EMPTY_STRING : currentModelElement.getName());
        purposeText.setText(TaggedValueHelper.getPurpose(currentModelElement) == null ? PluginConstant.EMPTY_STRING
                : TaggedValueHelper.getPurpose(currentModelElement));
        descriptionText.setText(TaggedValueHelper.getDescription(currentModelElement) == null ? PluginConstant.EMPTY_STRING
                : TaggedValueHelper.getDescription(currentModelElement));
        authorText.setText(TaggedValueHelper.getAuthor(currentModelElement) == null ? PluginConstant.EMPTY_STRING
                : TaggedValueHelper.getAuthor(currentModelElement));
        statusCombo.setText(TaggedValueHelper.getDevStatus(currentModelElement) == null ? PluginConstant.EMPTY_STRING
                : TaggedValueHelper.getDevStatus(currentModelElement).getLiteral());
    }

    protected void fireTextChange() {
        currentModelElement.setName(nameText.getText());
        TaggedValueHelper.setPurpose(purposeText.getText(), currentModelElement);
        TaggedValueHelper.setDescription(descriptionText.getText(), currentModelElement);
        TaggedValueHelper.setAuthor(currentModelElement, authorText.getText());
        TaggedValueHelper.setDevStatus(currentModelElement, DevelopmentStatus.get(statusCombo.getText()));
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

}
