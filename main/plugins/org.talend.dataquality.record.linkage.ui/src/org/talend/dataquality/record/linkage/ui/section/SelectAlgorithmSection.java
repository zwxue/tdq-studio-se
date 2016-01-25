// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.section;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.record.linkage.ui.section.definition.BlockingKeyDefinitionSection;
import org.talend.dataquality.record.linkage.ui.section.definition.DefaultSurvivorshipDefinitionSection;
import org.talend.dataquality.record.linkage.ui.section.definition.MatchAndSurvivorKeySection;
import org.talend.dataquality.record.linkage.ui.section.definition.MatchKeyDefinitionSection;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.MatchRuleDefinition;

/**
 * created by zshen on Aug 20, 2013 Detailled comment
 * 
 */
public class SelectAlgorithmSection extends AbstractSectionComposite {

    private boolean isVSRMode = true;

    private BlockingKeyDefinitionSection blockKeySection = null;

    private MatchKeyDefinitionSection matchKeySection = null;

    // private SurvivorshipDefinitionSection survivorshipDefinitionSection = null;

    private MatchAndSurvivorKeySection matchAndSurvivorKeySection = null;

    private DefaultSurvivorshipDefinitionSection defaultSurvivorshipDefinitionSection = null;

    private String algorithmName = null;

    private MatchRuleDefinition matchRuleDef = null;

    public SelectAlgorithmSection(ScrolledForm form, Composite parent, FormToolkit toolkit) {
        this(form, parent, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED, toolkit);
    }

    /**
     * DOC zshen SelectAlgorithmSection constructor comment.
     * 
     * @param parent
     * @param style
     * @param toolkit
     */
    public SelectAlgorithmSection(ScrolledForm form, Composite parent, int style, FormToolkit toolkit) {
        super(form, parent, style, toolkit);
        section.setText(DefaultMessagesImpl.getString("SelectAlgorithmSection.record.linkage.algorithm")); //$NON-NLS-1$
        section.setDescription(DefaultMessagesImpl.getString("SelectAlgorithmSection.choose.algorithm")); //$NON-NLS-1$
    }

    /**
     * DOC zshen Comment method "createChooseAlgorithmCom".
     * 
     * @param dqRuleDefinitionSection
     */
    public void createChooseAlgorithmCom() {
        Composite mainComp = toolkit.createComposite(section);
        mainComp.setLayout(new GridLayout());

        Composite container = toolkit.createComposite(mainComp, SWT.NONE);
        GridLayout gdLayout = new GridLayout(1, false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(container);
        container.setLayout(gdLayout);

        Button tSwooshButton = toolkit.createButton(container, RecordMatcherType.T_SwooshAlgorithm.getLabel(), SWT.RADIO);
        if (this.algorithmName == null) {
            this.algorithmName = RecordMatcherType.simpleVSRMatcher.name();
        }
        tSwooshButton.setSelection(!isVSRMode());
        tSwooshButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                isVSRMode = false;
                matchKeySection.setAddColumn(true);
                noticeOtherSection();
                algorithmName = RecordMatcherType.T_SwooshAlgorithm.name();
                matchRuleDef.setRecordLinkageAlgorithm(algorithmName);
                blockKeySection.removeAllBlockingKey();
                listeners.firePropertyChange(MatchAnalysisConstant.ISDIRTY_PROPERTY, RecordMatcherType.simpleVSRMatcher.name(),
                        RecordMatcherType.T_SwooshAlgorithm.name());

            }

        });
        Button vsrButton = toolkit.createButton(container, RecordMatcherType.simpleVSRMatcher.getLabel(), SWT.RADIO);
        vsrButton.setSelection(isVSRMode());
        vsrButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                isVSRMode = true;
                matchKeySection.setAddColumn(false);
                noticeOtherSection();
                algorithmName = RecordMatcherType.simpleVSRMatcher.name();
                matchRuleDef.setRecordLinkageAlgorithm(algorithmName);
                matchAndSurvivorKeySection.removeAllSurvivorship();
                // survivorshipDefinitionSection.removeAllSurvivorship();
                listeners.firePropertyChange(MatchAnalysisConstant.ISDIRTY_PROPERTY, RecordMatcherType.T_SwooshAlgorithm.name(),
                        RecordMatcherType.simpleVSRMatcher.name());
            }

        });
        section.setClient(mainComp);

    }

    /**
     * DOC zshen Comment method "noticeOtherSection".
     */
    private void noticeOtherSection() {
        // matchKeySection.redrawnContent();
        matchKeySection.changeSectionDisStatus(isVSRMode);
        blockKeySection.changeSectionDisStatus(isVSRMode);
        // survivorshipDefinitionSection.changeSectionDisStatus(!isVSRMode);
        defaultSurvivorshipDefinitionSection.changeSectionDisStatus(!isVSRMode);
        matchAndSurvivorKeySection.changeSectionDisStatus(!isVSRMode);
        if (!isVSRMode) {
            matchAndSurvivorKeySection.initTableInput(Boolean.TRUE);
        } else {
            matchKeySection.redrawnContent();
        }
    }

    public boolean isVSRMode() {
        return RecordMatcherType.simpleVSRMatcher.name().equals(algorithmName);
    }

    /**
     * Sets the blockKeySection.
     * 
     * @param blockKeySection the blockKeySection to set
     */
    public void setBlockkeySection(BlockingKeyDefinitionSection blockKeySection) {
        this.blockKeySection = blockKeySection;
    }

    /**
     * Sets the matchKeySection.
     * 
     * @param matchKeySection the matchKeySection to set
     */
    public void setMatchKeySection(MatchKeyDefinitionSection matchKeySection) {
        this.matchKeySection = matchKeySection;
    }

    public void setMatchAndSurvivorKeySection(MatchAndSurvivorKeySection matchAndSurvivorKeySection) {
        this.matchAndSurvivorKeySection = matchAndSurvivorKeySection;
    }

    /**
     * Getter for algorithmName.
     * 
     * @return the algorithmName
     */
    public String getAlgorithmName() {
        return this.algorithmName;
    }

    /**
     * Sets the matchRuleDef.
     * 
     * @param matchRuleDef the matchRuleDef to set
     */
    public void setMatchRuleDef(MatchRuleDefinition matchRuleDef) {
        this.matchRuleDef = matchRuleDef;
        this.algorithmName = this.matchRuleDef.getRecordLinkageAlgorithm();
    }

    // /**
    // * Sets the survivorshipDefinitionSection.
    // *
    // * @param survivorshipDefinitionSection the survivorshipDefinitionSection to set
    // */
    // public void setSurvivorshipDefinitionSection(SurvivorshipDefinitionSection survivorshipDefinitionSection) {
    // this.survivorshipDefinitionSection = survivorshipDefinitionSection;
    // }

    /**
     * Sets the defaultSurvivorshipDefinitionSection.
     * 
     * @param defaultSurvivorshipDefinitionSection the defaultSurvivorshipDefinitionSection to set
     */
    public void setDefaultSurvivorshipDefinitionSection(DefaultSurvivorshipDefinitionSection defaultSurvivorshipDefinitionSection) {
        this.defaultSurvivorshipDefinitionSection = defaultSurvivorshipDefinitionSection;
    }

}
