// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.matchrule;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.dqrules.DQRuleEditor;
import org.talend.dataquality.properties.TDQMatchRuleItem;
import org.talend.dataquality.record.linkage.ui.section.SelectAlgorithmSection;
import org.talend.dataquality.record.linkage.ui.section.definition.BlockingKeyDefinitionSection;
import org.talend.dataquality.record.linkage.ui.section.definition.DefaultSurvivorshipDefinitionSection;
import org.talend.dataquality.record.linkage.ui.section.definition.MatchKeyDefinitionSection;
import org.talend.dataquality.record.linkage.ui.section.definition.SurvivorshipDefinitionSection;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * created by zshen on Aug 19, 2013 Detailled comment
 * 
 */
public class MatchRuleMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    private static final String ID = "MatchRuleEditor.masterPage"; //$NON-NLS-1$

    private static final String TITLE = DefaultMessagesImpl.getString("DQRuleEditor.matchRuleSettings"); //$NON-NLS-1$

    private SelectAlgorithmSection selectAlgorithmSection = null;

    private BlockingKeyDefinitionSection blockingKeyDefinitionSection = null;

    private MatchKeyDefinitionSection matchingKeyDefinitionSection = null;

    private SurvivorshipDefinitionSection survivorshipDefinitionSection = null;

    private DefaultSurvivorshipDefinitionSection defaultSurvivorshipDefinitionSection = null;

    /**
     * DOC zshen MatchRuleMasterDetailsPage constructor comment.
     * 
     * @param editor
     */
    public MatchRuleMasterDetailsPage(FormEditor editor) {
        super(editor, ID, TITLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        this.currentModelElement.eResource().unload();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {

        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            this.setDirty(true);
        }
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
        MatchRuleItemEditorInput editorInput = getMatchRuleEditorInput();
        if (editorInput != null) {
            this.currentModelElement = editorInput.getMatchRule();
        } else if (this.getEditor().getEditorInput() instanceof FileEditorInput) {
            FileEditorInput input = (FileEditorInput) this.getEditor().getEditorInput();
            this.currentModelElement = DQRuleResourceFileHelper.getInstance().findMatchRule(input.getFile());
        }
        return this.currentModelElement;
    }

    private MatchRuleItemEditorInput getMatchRuleEditorInput() {
        IEditorInput editorInput = this.getEditor().getEditorInput();
        if (editorInput instanceof MatchRuleItemEditorInput) {
            return (MatchRuleItemEditorInput) editorInput;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#canSave()
     */
    @Override
    public ReturnCode canSave() {
        ReturnCode rc = new ReturnCode(false);
        if (this.isDirty) {
            rc.setOk(true);
        }
        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
        if (isDirty) {
            ((DQRuleEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#createFormContent(org.eclipse.ui.forms.IManagedForm
     * )
     */
    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        form = managedForm.getForm();
        form.setText(DefaultMessagesImpl.getString("DQRuleEditor.matchRuleSettings")); //$NON-NLS-1$

        metadataSection.setText(DefaultMessagesImpl.getString("DQRuleEditor.matchRuleMetadata")); //$NON-NLS-1$
        metadataSection.setDescription(DefaultMessagesImpl.getString("MatchRuleMasterDetailsPage.setProperties")); //$NON-NLS-1$

        // resetJoinElements();
        createSelectRecordLinkageSection(topComp);

        createGenerationOfBlockingKeySection(topComp);
        selectAlgorithmSection.setBlockkeySection(blockingKeyDefinitionSection);

        createMatchingKeySection(topComp);
        selectAlgorithmSection.setMatchKeySection(matchingKeyDefinitionSection);

        createSurvivorshipSection(topComp);
        selectAlgorithmSection.setSurvivorshipDefinitionSection(survivorshipDefinitionSection);

        createDefaultSurvivorshipSection(topComp);
        selectAlgorithmSection.setDefaultSurvivorshipDefinitionSection(defaultSurvivorshipDefinitionSection);

    }

    /**
     * DOC zshen Comment method "createMatchingKeySection".
     * 
     * @param mainComp
     */
    private void createMatchingKeySection(Composite mainComp) {
        matchingKeyDefinitionSection = new MatchKeyDefinitionSection(form, mainComp, toolkit);
        matchingKeyDefinitionSection.setMatchRuleDef((MatchRuleDefinition) getCurrentModelElement(getEditor()));
        matchingKeyDefinitionSection.setAddColumn(!selectAlgorithmSection.isVSRMode());
        matchingKeyDefinitionSection.createContent();
        matchingKeyDefinitionSection.addPropertyChangeListener(this);
        matchingKeyDefinitionSection.getSection().setExpanded(true);
    }

    /**
     * DOC HHB Comment method "createSurvivorshipSection".
     * 
     * @param mainComp
     */
    private void createSurvivorshipSection(Composite mainComp) {
        survivorshipDefinitionSection = new SurvivorshipDefinitionSection(form, mainComp, toolkit);
        survivorshipDefinitionSection.setMatchRuleDef((MatchRuleDefinition) getCurrentModelElement(getEditor()));
        survivorshipDefinitionSection.createContent();
        survivorshipDefinitionSection.addPropertyChangeListener(this);
        survivorshipDefinitionSection.changeSectionDisStatus(!selectAlgorithmSection.isVSRMode());
        survivorshipDefinitionSection.getSection().setExpanded(true);
    }

    private void createDefaultSurvivorshipSection(Composite mainComp) {
        defaultSurvivorshipDefinitionSection = new DefaultSurvivorshipDefinitionSection(form, mainComp, toolkit);
        defaultSurvivorshipDefinitionSection.setMatchRuleDef((MatchRuleDefinition) getCurrentModelElement(getEditor()));
        defaultSurvivorshipDefinitionSection.createContent();
        defaultSurvivorshipDefinitionSection.addPropertyChangeListener(this);
        defaultSurvivorshipDefinitionSection.changeSectionDisStatus(!selectAlgorithmSection.isVSRMode());
        defaultSurvivorshipDefinitionSection.getSection().setExpanded(true);
    }

    /**
     * DOC zshen Comment method "createGenerationOfBlockingKey".
     * 
     * @param topComp
     */
    private void createGenerationOfBlockingKeySection(Composite mainComp) {
        blockingKeyDefinitionSection = new BlockingKeyDefinitionSection(form, mainComp, toolkit);
        blockingKeyDefinitionSection.setMatchRuleDef((MatchRuleDefinition) getCurrentModelElement(getEditor()));
        blockingKeyDefinitionSection.createContent();
        blockingKeyDefinitionSection.addPropertyChangeListener(this);
        blockingKeyDefinitionSection.changeSectionDisStatus(selectAlgorithmSection.isVSRMode());
        blockingKeyDefinitionSection.getSection().setExpanded(true);
    }

    /**
     * DOC zshen Comment method "createSelectRecordLinkageSection".
     * 
     * @param topComp
     */
    private void createSelectRecordLinkageSection(Composite mainComp) {
        selectAlgorithmSection = new SelectAlgorithmSection(form, mainComp, toolkit);
        selectAlgorithmSection.setMatchRuleDef((MatchRuleDefinition) getCurrentModelElement(getEditor()));
        selectAlgorithmSection.createChooseAlgorithmCom();
        selectAlgorithmSection.addPropertyChangeListener(this);
        selectAlgorithmSection.getSection().setExpanded(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        if (!canSave().isOk()) {
            return;
        }
        super.doSave(monitor);
        if (saveMatchRule()) {
            this.isDirty = false;
        }
    }

    /**
     * DOC zshen Comment method "saveMatchRule".
     * 
     * @return
     */
    private boolean saveMatchRule() {
        MatchRuleDefinition saveModelElement = (MatchRuleDefinition) getCurrentModelElement(this.getEditor());
        // // algorithm
        saveModelElement.setRecordLinkageAlgorithm(selectAlgorithmSection.getAlgorithmName());

        ReturnCode rc = ElementWriterFactory.getInstance().createdMatchRuleWriter().save(getInputItem(), Boolean.FALSE);
        if (!rc.isOk()) {
            return false;
        }
        return true;
    }

    private TDQMatchRuleItem getInputItem() {
        MatchRuleItemEditorInput matchRuleEditorInput = getMatchRuleEditorInput();
        if (matchRuleEditorInput == null) {
            return null;
        }
        Item item = matchRuleEditorInput.getItem();
        if (item != null) {
            return (TDQMatchRuleItem) item;
        }
        return null;
    }

}
