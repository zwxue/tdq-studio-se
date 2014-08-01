// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.section.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.record.linkage.ui.composite.AbsMatchAnalysisTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.MatchKeyAndSurvivorTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition.MatchKeyAndSurvivorDefinition;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.sorter.KeyDefinitionTableViewerSorter;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.record.linkage.ui.section.AnaMatchSurvivorSection;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.MatchRule;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class MatchAndSurvivorKeySection extends AnaMatchSurvivorSection {

    private MatchKeyAndSurvivorTableComposite tableComposite = null;

    List<MatchKeyAndSurvivorDefinition> matchAndSurvivorKeyList = new ArrayList<MatchKeyAndSurvivorDefinition>();

    private Text groupQualityThresholdText = null;

    /**
     * DOC yyin MatchAndSurvivorKeySection constructor comment.
     * 
     * @param form
     * @param parent
     * @param style
     * @param toolkit
     * @param analysis
     */
    public MatchAndSurvivorKeySection(ScrolledForm form, Composite parent, int style, FormToolkit toolkit, Analysis analysis) {
        super(form, parent, style, toolkit, analysis);
        super.setIsNeedSubChart(false);
    }

    /**
     * DOC zshen BlockingKeyDefinitionSection constructor comment.
     * 
     * @param form
     * @param parent
     * @param style
     * @param toolkit
     * @param analysis
     */
    public MatchAndSurvivorKeySection(ScrolledForm form, Composite parent, FormToolkit toolkit) {
        super(form, parent, Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED, toolkit, null);
        super.setIsNeedSubChart(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#addTableItem()
     */
    @Override
    public void addTableItem() {
        tableComposite.addKeyDefinition(StringUtils.EMPTY, matchAndSurvivorKeyList);
        // link the added MatchKeyAndSurvivorDefinition's match and survivor key with matchRuleDef's matchkey and
        // survivorkey list;
        MatchKeyAndSurvivorDefinition definition = matchAndSurvivorKeyList.get(matchAndSurvivorKeyList.size() - 1);
        matchRuleDef.getMatchRules().get(0).getMatchKeys().add(definition.getMatchKey());
        matchRuleDef.getSurvivorshipKeys().add(definition.getSurvivorShipKey());

        listeners.firePropertyChange(MatchAnalysisConstant.ISDIRTY_PROPERTY, true, false);
    }

    @Override
    public void removeTableItem() {
        boolean success = false;
        ISelection selectItems = tableComposite.getSelectItems();
        if (selectItems instanceof IStructuredSelection) {
            Iterator<MatchKeyAndSurvivorDefinition> iterator = ((IStructuredSelection) selectItems).iterator();
            while (iterator.hasNext()) {
                MatchKeyAndSurvivorDefinition next = iterator.next();
                removeMatchAndSurvivorKey(next);
                success = true;
            }
            if (success) {
                listeners.firePropertyChange(MatchAnalysisConstant.ISDIRTY_PROPERTY, true, false);
            }
        }
    }

    /**
     * DOC yyin Comment method "removeMatchAndSurvivorKey".
     * 
     * @param next
     */
    private void removeMatchAndSurvivorKey(MatchKeyAndSurvivorDefinition definition) {
        tableComposite.removeKeyDefinition(definition, matchAndSurvivorKeyList);
    }

    @Override
    public void moveDownTableItem() {
        ISelection selectItems = tableComposite.getSelectItems();
        if (selectItems instanceof StructuredSelection) {
            if (selectItems.isEmpty()) {
                return;
            }
            Iterator<MatchKeyAndSurvivorDefinition> iterator = ((StructuredSelection) selectItems).iterator();
            while (iterator.hasNext()) {
                MatchKeyAndSurvivorDefinition next = iterator.next();
                tableComposite.moveDownKeyDefinition(next, matchAndSurvivorKeyList);
                moveMatchKey(next, matchAndSurvivorKeyList.indexOf(next));
            }
            tableComposite.selectAllItem(((StructuredSelection) selectItems).toList());
        }
    }

    /**
     * move related match key and survivor key in model.
     * 
     * @param next
     * @param indexOf
     */
    private void moveMatchKey(MatchKeyAndSurvivorDefinition next, int newIndex) {
        // move the related match key in model
        EList<MatchKeyDefinition> matchKeys = matchRuleDef.getMatchRules().get(0).getMatchKeys();
        matchKeys.remove(next.getMatchKey());
        matchKeys.add(newIndex, next.getMatchKey());

        // move the related survivor keys
        matchRuleDef.getSurvivorshipKeys().remove(next.getSurvivorShipKey());
        matchRuleDef.getSurvivorshipKeys().add(newIndex, next.getSurvivorShipKey());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#moveUpTableItem()
     */
    @Override
    public void moveUpTableItem() {
        ISelection selectItems = tableComposite.getSelectItems();
        if (selectItems instanceof StructuredSelection) {
            if (selectItems.isEmpty()) {
                return;
            }
            Iterator<MatchKeyAndSurvivorDefinition> iterator = ((StructuredSelection) selectItems).iterator();
            while (iterator.hasNext()) {
                MatchKeyAndSurvivorDefinition next = iterator.next();
                tableComposite.moveUpKeyDefinition(next, matchAndSurvivorKeyList);
                moveMatchKey(next, matchAndSurvivorKeyList.indexOf(next));
            }
            tableComposite.selectAllItem(((StructuredSelection) selectItems).toList());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#createSubContent(org.eclipse
     * .swt.widgets.Composite)
     */
    @Override
    protected Composite createSubContent(Composite sectionClient) {
        Composite ruleComp = toolkit.createComposite(sectionClient, SWT.NONE);
        GridData data = new GridData(GridData.FILL_BOTH);
        ruleComp.setLayoutData(data);

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        ruleComp.setLayout(gridLayout);
        tableComposite = (MatchKeyAndSurvivorTableComposite) createTableComposite(ruleComp, matchRuleDef.getMatchRules().get(0));
        tableComposite.addPropertyChangeListener(this);
        tableComposite.setAddColumn(isAddColumn());
        tableComposite.setLayout(gridLayout);
        tableComposite.setLayoutData(data);
        if (columnMap != null) {
            ArrayList<MetadataColumn> columnList = new ArrayList<MetadataColumn>();
            columnList.addAll(columnMap.keySet());
            tableComposite.setColumnList(columnList);
        }
        tableComposite.createContent();

        List<MatchKeyAndSurvivorDefinition> keyList = new ArrayList<MatchKeyAndSurvivorDefinition>();
        EList<MatchRule> matchRules = matchRuleDef.getMatchRules();
        if (!matchRules.isEmpty()) {
            keyList = getKeyList(matchRules.get(0), Boolean.FALSE);
            matchAndSurvivorKeyList = matchRuleWithSurvMap.get(matchRules.get(0));
        }

        tableComposite.serViewerSorter(new KeyDefinitionTableViewerSorter<MatchKeyAndSurvivorDefinition>(keyList));
        tableComposite.setInput(keyList);

        createGroupQualityThreshold(ruleComp);

        return ruleComp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.section.AnaMatchSurvivorSection#createTableComposite(org.eclipse.swt
     * .widgets.Composite, org.talend.dataquality.rules.MatchRule)
     */
    @Override
    protected AbsMatchAnalysisTableComposite<?> createTableComposite(Composite ruleComp, MatchRule matchRule) {
        MatchKeyAndSurvivorTableComposite tableComp = new MatchKeyAndSurvivorTableComposite(ruleComp, SWT.NO_FOCUS, matchRule);
        return tableComp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.section.MatchingKeySection#getMatchRuleComposite(org.eclipse.swt.custom
     * .CTabItem)
     */
    @Override
    public AbsMatchAnalysisTableComposite<?> getMatchRuleComposite(CTabItem currentTabItem) {
        return (MatchKeyAndSurvivorTableComposite) currentTabItem.getData(MatchAnalysisConstant.MATCH_RULE_TABLE_COMPOSITE);
    }

    /**
     * DOC zhao Comment method "createGroupQualityThreshold".
     * 
     * @param parent
     */
    @Override
    protected void createGroupQualityThreshold(Composite parent) {

        Composite groupQualityThresholdComposite = new Composite(parent, SWT.NONE);
        groupQualityThresholdComposite.setLayout(new GridLayout(2, Boolean.TRUE));
        Label groupQualityTresholdLabel = new Label(groupQualityThresholdComposite, SWT.NONE);
        groupQualityTresholdLabel.setText(DefaultMessagesImpl.getString("MatchRuleTableComposite.GROUP_QUALITY_THRESHOLD")); //$NON-NLS-1$
        groupQualityThresholdText = new Text(groupQualityThresholdComposite, SWT.BORDER);
        GridData layoutData = new GridData();
        layoutData.widthHint = 80;
        groupQualityThresholdText.setLayoutData(layoutData);
        groupQualityThresholdText.setText(String.valueOf(this.matchRuleDef.getMatchGroupQualityThreshold()));
        groupQualityThresholdText.addModifyListener(new ModifyListener() {

            Double oldValue = matchRuleDef.getMatchGroupQualityThreshold();

            @Override
            public void modifyText(ModifyEvent e) {
                try {
                    String newValue = groupQualityThresholdText.getText();
                    Double value = Double.valueOf(newValue);
                    if (value != oldValue) {
                        matchRuleDef.setMatchGroupQualityThreshold(value);
                        listeners.firePropertyChange(MatchAnalysisConstant.ISDIRTY_PROPERTY, oldValue, value);
                        oldValue = value;
                    }
                } catch (Exception exc) {
                    // Invalid input
                }
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#getSectionName()
     */
    @Override
    protected String getSectionName() {
        return MatchAnalysisConstant.MATCHING_KEY_AND_SURVIVOR_DEFINITION_SECTION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#isKeyDefinitionAdded(java.lang
     * .String)
     */
    @Override
    public Boolean isKeyDefinitionAdded(String columnName) throws Exception {
        return Boolean.FALSE;

    }

    // when switch to t_swoosh, call this method, to recompute the input
    public void initTableInput(boolean isClearSurvivor) {
        List<MatchKeyAndSurvivorDefinition> keyList = new ArrayList<MatchKeyAndSurvivorDefinition>();
        EList<MatchRule> matchRules = matchRuleDef.getMatchRules();
        if (!matchRules.isEmpty()) {
            keyList = getKeyList(matchRules.get(0), isClearSurvivor);
            matchAndSurvivorKeyList = matchRuleWithSurvMap.get(matchRules.get(0));
        }
        tableComposite.setInput(keyList);
        if (!matchRules.isEmpty()) {
            tableComposite.setMatchIntervalText(String.valueOf(matchRules.get(0).getMatchInterval()));
        }
        groupQualityThresholdText.setText(String.valueOf(this.matchRuleDef.getMatchGroupQualityThreshold()));
    }

}
