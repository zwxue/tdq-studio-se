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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataquality.record.linkage.ui.section.SelectAlgorithmSection;
import org.talend.dataquality.record.linkage.ui.section.definition.BlockingKeyDefinitionSection;
import org.talend.dataquality.record.linkage.ui.section.definition.MatchKeyDefinitionSection;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;


/**
 * created by zshen on Aug 19, 2013
 * Detailled comment
 *
 */
public class MatchRuleMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    private static final String ID = "MatchRuleEditor.masterPage"; //$NON-NLS-1$

    private static final String TITLE = DefaultMessagesImpl.getString("DQRuleEditor.matchRuleSettings"); //$NON-NLS-1$

    SelectAlgorithmSection selectAlgorithmSection = null;

    BlockingKeyDefinitionSection blockingKeyDefinitionSection = null;

    private Composite blockKeyComp = null;
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
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub

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
        IEditorInput editorInput = editor.getEditorInput();
        if (editorInput instanceof MatchRuleItemEditorInput) {
            MatchRuleItemEditorInput input = (MatchRuleItemEditorInput) editor.getEditorInput();
            this.currentModelElement = input.getMatchRule();
        }
        return this.currentModelElement;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#canSave()
     */
    @Override
    public ReturnCode canSave() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {
        // TODO Auto-generated method stub

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
        // blockKeyComp = new Composite(topComp, SWT.NONE);
        createGenerationOfBlockingKeySection();
        selectAlgorithmSection.setBk(blockingKeyDefinitionSection);
        createMatchingKeySection(topComp);
        // createDQRuleDefinitionSection(topComp);
        // createJoinConditionSection(topComp);
    }

    /**
     * DOC zshen Comment method "createMatchingKeySection".
     *
     * @param mainComp
     */
    private void createMatchingKeySection(Composite mainComp) {
        MatchKeyDefinitionSection matchingKeyDefinitionSection = new MatchKeyDefinitionSection(form, mainComp, toolkit);
        matchingKeyDefinitionSection.setMatchRules(getMatchRuleList());
        matchingKeyDefinitionSection.createContent();

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
    private void createGenerationOfBlockingKeySection() {
//        if (blockingKeyDefinitionSection != null) {
//            blockingKeyDefinitionSection.getSection().dispose();
//        }
        blockingKeyDefinitionSection = new BlockingKeyDefinitionSection(form, topComp, toolkit);
        blockingKeyDefinitionSection.setMatchRuleDef((MatchRuleDefinition) getCurrentModelElement(getEditor()));
        blockingKeyDefinitionSection.createContent();
//        blockKeyComp.layout();
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
    }



}
