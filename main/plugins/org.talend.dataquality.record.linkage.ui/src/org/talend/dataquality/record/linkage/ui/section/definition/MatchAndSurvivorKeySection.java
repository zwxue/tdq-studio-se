// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.record.linkage.ui.composite.MatchKeyAndSurvivorTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition.MatchKeyAndSurvivorDefinition;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.sorter.KeyDefinitionTableViewerSorter;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.record.linkage.ui.section.AbstractMatchKeyWithChartTableSection;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;
import org.talend.dataquality.rules.AlgorithmDefinition;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dataquality.rules.RulesFactory;
import org.talend.dataquality.rules.SurvivorshipKeyDefinition;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class MatchAndSurvivorKeySection extends AbstractMatchKeyWithChartTableSection {

    private MatchKeyAndSurvivorTableComposite tableComposite = null;

    private MatchRuleDefinition matchRuleDef = null;

    private Text groupQualityThresholdText = null;

    private List<MatchKeyAndSurvivorDefinition> matchAndSurvivorKeyList = new ArrayList<MatchKeyAndSurvivorDefinition>();

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
     * @see
     * org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#createSubChart(org.eclipse.
     * swt.widgets.Composite)
     */
    @Override
    protected void createSubChart(Composite sectionClient) {
        // don't need the chart so do nothing at here
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

    public void removeAllSurvivorship() {
        matchRuleDef.getSurvivorshipKeys().clear();
        redrawnSubTableContent();
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

    private List<MatchKeyAndSurvivorDefinition> getKeyList(boolean isClearSurvivor) {

        EList<MatchRule> matchRules = matchRuleDef.getMatchRules();
        if (matchRules.isEmpty()) {
            return matchAndSurvivorKeyList;
        }
        EList<MatchKeyDefinition> matchKeys = matchRules.get(0).getMatchKeys();
        int index = 0;
        for (MatchKeyDefinition matchKey : matchKeys) {
            // first, find the current matchKey in MatchAndSurvivorKeyList
            if (this.matchAndSurvivorKeyList.size() > index) {
                MatchKeyAndSurvivorDefinition definition = matchAndSurvivorKeyList.get(index);
                // check if the position of the match key moved or not
                if (StringUtils.equals(matchKey.getName(), definition.getMatchKey().getName())) {
                    // update the current match key
                    definition.setMatchKey(matchKey);
                    updateSurvivorKey(isClearSurvivor, matchKey.getName(), definition, index);
                } else {
                    // the position of the current match key moved, need to find its related mAndS key in list,
                    MatchKeyAndSurvivorDefinition oldDefinition = findPositionOfCurrentMatchkey(matchKey);
                    // if can't find, means that it is a new one
                    if (oldDefinition == null) {
                        createMatchAndSurvivorKey(matchKey, isClearSurvivor, index);
                    } else {
                        // delete the old definition in current list
                        matchAndSurvivorKeyList.remove(oldDefinition);
                        // set new match key to it
                        oldDefinition.setMatchKey(matchKey);
                        updateSurvivorKey(isClearSurvivor, matchKey.getName(), oldDefinition, index);
                        // insert it in the new position
                        matchAndSurvivorKeyList.add(index, oldDefinition);
                    }
                }

            } else {
                // need to create a MatchAndSurvivorKey
                createMatchAndSurvivorKey(matchKey, isClearSurvivor, index);
            }
            index++;
        }
        return matchAndSurvivorKeyList;
    }

    private void updateSurvivorKey(boolean isCreateNewOne, String name, MatchKeyAndSurvivorDefinition definition, int index) {
        EList<SurvivorshipKeyDefinition> survivorshipKeys = matchRuleDef.getSurvivorshipKeys();

        // set the survivor key: if clear, use a new one , if not clear, use the one in model
        if (!isCreateNewOne && survivorshipKeys.size() > index) {
            definition.setSurvivorShipKey(survivorshipKeys.get(index));
        } else {
            SurvivorshipKeyDefinition survivorshipKeyDefinition = createNewSurvivorshipKeyDefinition(name);
            definition.setSurvivorShipKey(survivorshipKeyDefinition);
            survivorshipKeys.add(survivorshipKeyDefinition);
        }
    }

    /**
     * DOC yyin Comment method "createMatchAndSurvivorKey".
     * 
     * @param matchKey
     * @param isClearSurvivor
     * @param isClearSurvivor
     * @param index
     */
    private void createMatchAndSurvivorKey(MatchKeyDefinition matchKey, boolean isClearSurvivor, int index) {
        MatchKeyAndSurvivorDefinition mAnds = new MatchKeyAndSurvivorDefinition();
        mAnds.setMatchKey(matchKey);
        // create a new survivor key
        updateSurvivorKey(isClearSurvivor, matchKey.getName(), mAnds, index);
        this.matchAndSurvivorKeyList.add(mAnds);
    }

    /**
     * DOC yyin Comment method "findPositionOfCurrentMatchkey".
     * 
     * @param matchKey
     * @return
     */
    private MatchKeyAndSurvivorDefinition findPositionOfCurrentMatchkey(MatchKeyDefinition matchKey) {
        // find the definition by match key's name
        for (MatchKeyAndSurvivorDefinition definition : this.matchAndSurvivorKeyList) {
            if (StringUtils.equals(matchKey.getName(), definition.getMatchKey().getName())) {
                return definition;
            }
        }
        return null;
    }

    private SurvivorshipKeyDefinition createNewSurvivorshipKeyDefinition(String matchKeyName) {
        SurvivorshipKeyDefinition skd = RulesFactory.eINSTANCE.createSurvivorshipKeyDefinition();
        skd.setName(matchKeyName);
        AlgorithmDefinition createAlgorithmDefinition = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition.setAlgorithmType(SurvivorShipAlgorithmEnum.getTypeByIndex(0).getValue());
        createAlgorithmDefinition.setAlgorithmParameters(StringUtils.EMPTY);
        skd.setFunction(createAlgorithmDefinition);
        skd.setAllowManualResolution(true);
        return skd;
    }

    public void setMatchRuleDef(MatchRuleDefinition matchRuleDef) {
        this.matchRuleDef = matchRuleDef;
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
        tableComposite = new MatchKeyAndSurvivorTableComposite(ruleComp, SWT.NO_FOCUS, matchRuleDef.getMatchRules().get(0));
        tableComposite.addPropertyChangeListener(this);
        // tableComposite.setAddColumn(isAddColumn());
        tableComposite.setLayout(gridLayout);
        tableComposite.setLayoutData(data);
        if (columnMap != null) {
            ArrayList<String> columnList = new ArrayList<String>();
            columnList.addAll(columnMap.keySet());
            tableComposite.setColumnList(columnList);
        }
        tableComposite.createContent();
        List<MatchKeyAndSurvivorDefinition> keyList = getKeyList(Boolean.FALSE);
        tableComposite.serViewerSorter(new KeyDefinitionTableViewerSorter<MatchKeyAndSurvivorDefinition>(keyList));
        tableComposite.setInput(keyList);

        createGroupQualityThreshold(ruleComp);

        return ruleComp;
    }

    /**
     * DOC zhao Comment method "createGroupQualityThreshold".
     * 
     * @param parent
     */
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

    // when switch to t_swoosh, call this method, to recompute the input
    public void initTableInput(boolean isClearSurvivor) {
        tableComposite.setInput(getKeyList(isClearSurvivor));
        EList<MatchRule> matchRules = matchRuleDef.getMatchRules();
        if (!matchRules.isEmpty()) {
            tableComposite.setMatchIntervalText(String.valueOf(matchRules.get(0).getMatchInterval()));
        }
        groupQualityThresholdText.setText(String.valueOf(this.matchRuleDef.getMatchGroupQualityThreshold()));
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
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#refreshChart()
     */
    @Override
    public void refreshChart() {
        // until now, do nothing
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

}
