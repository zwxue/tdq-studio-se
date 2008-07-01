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

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.cwm.constants.DevelopmentStatus;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractFormPage extends FormPage {

    public static final String ACTION_HANDLER = "ACTION_HANDLER";

    protected Text nameText;

    protected Text purposeText;

    protected Text descriptionText;

    protected Text authorText;

    protected CCombo statusCombo;

    protected boolean isDirty = false;

    protected FormToolkit toolkit;

    protected Composite topComp;

    protected Section metadataSection;

    public AbstractFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        this.toolkit = this.getEditor().getToolkit();
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

    protected abstract void initMetaTextFied();

    protected abstract void fireTextChange();

    /**
     * @param form
     * @param toolkit
     * @param anasisDataComp
     * @param title
     * @param expanded
     * @param discription
     * @return
     */
    protected Section createSection(final ScrolledForm form, Composite parent, String title, boolean expanded, String description) {
        final int style = (description == null) ? (Section.TWISTIE | Section.TITLE_BAR)
                : (Section.DESCRIPTION | Section.TWISTIE | Section.TITLE_BAR);
        Section section = toolkit.createSection(parent, style);

        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

        section.addExpansionListener(new ExpansionAdapter() {

            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
            }

        });

        section.setText(title);
        section.setDescription(description);
        section.setExpanded(expanded);
        return section;
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

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
    }

    public abstract void setDirty(boolean isDirty);

    @Override
    public boolean isDirty() {
        return super.isDirty() || isDirty;
    }

}
