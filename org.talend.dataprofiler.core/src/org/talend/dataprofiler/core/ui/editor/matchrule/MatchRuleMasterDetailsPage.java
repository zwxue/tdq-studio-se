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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
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
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.MatchRuleDefinition;
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

    private Composite blockKeyComp = null;

    private Logger log = Logger.getLogger(MatchRuleMasterDetailsPage.class);

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
            ((DQRuleEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
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
        }
        return this.currentModelElement;
    }

    private MatchRuleItemEditorInput getMatchRuleEditorInput() {
        IEditorInput editorInput = this.getEditor().getEditorInput();
        if (editorInput instanceof MatchRuleItemEditorInput) {
            return (MatchRuleItemEditorInput) this.getEditor().getEditorInput();
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
        survivorshipDefinitionSection.getSection().setVisible(!selectAlgorithmSection.isVSRMode());
    }

    private void createDefaultSurvivorshipSection(Composite mainComp) {
        defaultSurvivorshipDefinitionSection = new DefaultSurvivorshipDefinitionSection(
                form, mainComp, toolkit);
        defaultSurvivorshipDefinitionSection.setMatchRuleDef((MatchRuleDefinition) getCurrentModelElement(getEditor()));
        defaultSurvivorshipDefinitionSection.createContent();
        defaultSurvivorshipDefinitionSection.addPropertyChangeListener(this);
        defaultSurvivorshipDefinitionSection.getSection().setVisible(!selectAlgorithmSection.isVSRMode());

    }

    /**
     * DOC zshen Comment method "getMatchRuleList".
     *
     * @return
     */
    private List<MatchRule> getMatchRuleList() {
        getCurrentModelElement(this.getEditor());
        if (this.currentModelElement != null && this.currentModelElement instanceof MatchRuleDefinition) {
            return ((MatchRuleDefinition) this.currentModelElement).getMatchRules();
        }
        return new ArrayList<MatchRule>();
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
    }

    private List<BlockKeyDefinition> getBlockKeyDefinitionList() {
        getCurrentModelElement(this.getEditor());
        if (this.currentModelElement != null && this.currentModelElement instanceof MatchRuleDefinition) {
            return ((MatchRuleDefinition) this.currentModelElement).getBlockKeys();
        }
        return new ArrayList<BlockKeyDefinition>();
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
        // algorithm
        saveModelElement.setRecordLinkageAlgorithm(selectAlgorithmSection.getAlgorithmName());
        // block key
        saveModelElement.getBlockKeys().clear();
        saveModelElement.getBlockKeys().addAll(blockingKeyDefinitionSection.getBlockKeyList());
        // match key
        saveModelElement.getMatchRules().clear();
        saveModelElement.getMatchRules().addAll(matchingKeyDefinitionSection.getMatchRules());

        // TODO survivorship key

        // TODO default survivorship key

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
