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
package org.talend.dataprofiler.core.ui.editor.dqrules;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
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
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class DQRuleMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    private static final String WHERE = "WHERE";

    private static final int CRITICALITY_LEVEL_MIN = 1;

    private static final int CRITICALITY_LEVEL_MAX = 10;

    private WhereRule whereRule;

    private ScrolledForm form;

    private Section dqRuleDefinitionSection;

    private Text whereText;

    private Text criticalityLevelText;

    public Text getCriticalityLevelText() {
        return criticalityLevelText;
    }

    public Text getWhereText() {
        return whereText;
    }

    /**
     * DOC xqliu DQRuleMasterDetailsPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public DQRuleMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#getCurrentModelElement(org.eclipse.ui.forms.editor
     * .FormEditor)
     */
    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        this.whereRule = DQRuleResourceFileHelper.getInstance().findWhereRule(input.getFile());
        return whereRule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((DQRuleEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((DQRuleEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        form = managedForm.getForm();

        form.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.dqRuleSettings")); //$NON-NLS-1$
        metadataSection.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.DQRuleMetadata")); //$NON-NLS-1$
        metadataSection.setDescription(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.setProperties")); //$NON-NLS-1$
        creatDQRuleDefinitionSection(topComp);
        currentEditor.registerSections(new Section[] { dqRuleDefinitionSection });
    }

    /**
     * DOC xqliu Comment method "creatDQRuleDefinitionSection".
     * 
     * @param topComp
     */
    private void creatDQRuleDefinitionSection(Composite topComp) {
        dqRuleDefinitionSection = createSection(form, topComp, DefaultMessagesImpl
                .getString("DQRuleMasterDetailsPage.dqRuleDefinition"), false, null); //$NON-NLS-1$

        Label label = new Label(dqRuleDefinitionSection, SWT.WRAP);
        label.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.text")); //$NON-NLS-1$
        dqRuleDefinitionSection.setDescriptionControl(label);

        createDQRuleDefinitionComp();
    }

    /**
     * DOC xqliu Comment method "createDQRuleDefinitionComp".
     * 
     * @return
     */
    private Composite createDQRuleDefinitionComp() {

        Composite newComp = toolkit.createComposite(dqRuleDefinitionSection);
        newComp.setLayout(new GridLayout());

        Composite container = new Composite(newComp, SWT.NONE);
        GridLayout gdLayout = new GridLayout(2, false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(container);
        container.setLayout(gdLayout);

        Label label1 = new Label(container, SWT.NONE);
        label1.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.criticalityLevel"));

        GridData data;
        criticalityLevelText = new Text(container, SWT.BORDER);
        criticalityLevelText.setText("" + whereRule.getCriticalityLevel());
        data = new GridData(GridData.FILL_HORIZONTAL);
        criticalityLevelText.setLayoutData(data);

        Label label2 = new Label(container, SWT.NONE);
        label2.setText(DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.whereClause"));
        label2.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        whereText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        whereText.setText(whereRule.getWhereExpression());
        data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 180;
        whereText.setLayoutData(data);

        addModifyListeners();

        dqRuleDefinitionSection.setClient(newComp);
        return newComp;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        if (saveDQRule()) {
            this.isDirty = false;
        }
    }

    /**
     * DOC xqliu Comment method "saveDQRule".
     * 
     * @return
     */
    private boolean saveDQRule() {
        boolean ret = false;
        if (checkValus()) {
            TaggedValueHelper.setValidStatus(true, whereRule);
            whereRule.setCriticalityLevel(Integer.valueOf(getCriticalityLevelText().getText()));
            whereRule.setWhereExpression(getWhereText().getText());
            EMFUtil.saveSingleResource(whereRule.eResource());
            ret = true;
        }
        return ret;

    }

    /**
     * DOC xqliu Comment method "checkValus".
     * 
     * @return
     */
    private boolean checkValus() {
        boolean ret = true;
        String msg = "";
        String cl = getCriticalityLevelText().getText();
        if (cl == null || "".equals(cl)) {
            ret = false;
            msg += DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.criticalityLevelIsEmpty") + "\n";
        } else {
            try {
                int i = Integer.valueOf(cl).intValue();
                if (!(i >= CRITICALITY_LEVEL_MIN && i <= CRITICALITY_LEVEL_MAX)) {
                    ret = false;
                    msg += DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.criticalityLevelIsInvalid") + "\n";
                }
            } catch (NumberFormatException e) {
                ret = false;
                msg += DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.criticalityLevelIsInvalid") + "\n";
            }
        }
        String wh = getWhereText().getText();
        if (wh == null || "".equals(wh)) {
            ret = false;
            msg += DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.whereClauseIsEmpty") + "\n";
        } else {
            if (wh.startsWith(WHERE)) {
                ret = false;
                msg += DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.whereClauseIsInvalid") + "\n";
            }
        }
        if (!ret) {
            MessageDialog.openWarning(null, DefaultMessagesImpl.getString("DQRuleMasterDetailsPage.warning"), msg); //$NON-NLS-1$
        }
        return ret;
    }

    /**
     * DOC xqliu Comment method "addModifyListeners".
     */
    protected void addModifyListeners() {
        whereText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
        });
        criticalityLevelText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
        });
    }
}
