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
package org.talend.dataquality.record.linkage.ui.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.record.linkage.ui.action.ExecuteMatchRuleDoGroupAction;
import org.talend.dataquality.record.linkage.ui.composite.MatchRuleTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.chart.MatchRuleDataChart;
import org.talend.dataquality.record.linkage.ui.composite.utils.ImageLib;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.KeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.RulesFactory;

/**
 * 
 * created by zhao on Aug 17, 2013 Detailled comment
 * 
 */
public class MatchingKeySection extends AbstractMatchAnaysisTableSection {

    private static Logger log = Logger.getLogger(MatchingKeySection.class);

    private final String SECTION_NAME = "Matching Key"; //$NON-NLS-1$

    private CTabFolder ruleFolder;

    private static final Image ADD_IMG = ImageLib.getImage(ImageLib.ADD_ACTION);

    private int tabCount = 1;

    private MatchRuleDataChart matchRuleChartComp = null;

    /**
     * DOC zshen MatchingKeySection constructor comment.
     * 
     * @param parent
     * @param style
     * @param toolkit
     */
    public MatchingKeySection(final ScrolledForm form, Composite parent, int style, FormToolkit toolkit, Analysis analysis) {
        super(form, parent, style, toolkit, analysis);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchTableSection#getSectionName()
     */
    @Override
    protected String getSectionName() {
        return SECTION_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.section.AbstractMatchTableSection#createSubContext(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
    protected void createSubContent(Composite sectionClient) {

        Composite parent = toolkit.createComposite(sectionClient);
        GridLayout tableLayout = new GridLayout(1, Boolean.TRUE);
        parent.setLayout(tableLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        parent.setLayoutData(gridData);

        ruleFolder = new CTabFolder(parent, SWT.MULTI);
        ruleFolder.setRenderer(new MatchRuleCTabFolderRenderer(ruleFolder));
        ruleFolder.setMaximizeVisible(false);
        ruleFolder.setMinimizeVisible(false);
        ruleFolder.setTabHeight(28);
        ruleFolder.setSimple(false);
        GridData folderData = new GridData(GridData.FILL_BOTH);
        // folderData.verticalIndent = -26;
        ruleFolder.setLayoutData(folderData);

        ruleFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {

            @Override
            public void close(CTabFolderEvent event) {
                CTabItem tabItem = (CTabItem) event.item;
                deleteMatchRuleTab(tabItem);
            }
        });
        Button button = new Button(ruleFolder, SWT.FLAT | SWT.CENTER);
        button.setImage(ADD_IMG);
        button.pack();
        button.setToolTipText("add"); //$NON-NLS-1$
        ruleFolder.setTopRight(button);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                MatchRule newMatchRule = getNewMatchRule();
                addRuleTab(true, newMatchRule);
                addMatchRuleToAnalysis(newMatchRule);
            }

        });
        initMatchRuleTabs();
        createRefreshButton(parent);

    }

    /**
     * DOC zhao Comment method "addMatchRuleToModel".
     * 
     * @param newMatchRule
     */
    private void addMatchRuleToAnalysis(MatchRule newMatchRule) {
        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        List<MatchRule> matchRules = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getMatchRules();
        matchRules.add(newMatchRule);
    }

    private void deleteMatchRuleTab(CTabItem tabItem) {
        MatchRuleTableComposite matchRuleTableComp = (MatchRuleTableComposite) tabItem
                .getData(MatchAnalysisConstant.MATCH_RULE_TABLE_COMPOSITE);
        MatchRule matchRule = matchRuleTableComp.getMatchRule();
        // Remove it from anaysis.
        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        recordMatchingIndicator.getBuiltInMatchRuleDefinition().getMatchRules().remove(matchRule);
    }

    /**
     * DOC zhao Comment method "initMatchRuleTabs".
     */
    private void initMatchRuleTabs() {
        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        List<MatchRule> matchRules = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getMatchRules();
        for (MatchRule matchRule : matchRules) {
            addRuleTab(false, matchRule);
            tabCount++;
        }
        if (matchRules.isEmpty()) {
            // create one match rule tab when no one exist.
            MatchRule newMatchRule = getNewMatchRule();
            addRuleTab(false, newMatchRule);
            matchRules.add(newMatchRule);
        }
    }

    /**
     * add properties tab
     */
    private void addRuleTab(boolean reComputeMatchRule, MatchRule matchRule) {
        createPropertyTab(matchRule, reComputeMatchRule);
    }

    /**
     * 
     * Add a new key definition on current selected match rule.
     * 
     * @param column
     */
    public void createMatchKeyFromCurrentMatchRule(String column) {
        CTabItem currentTabItem = ruleFolder.getSelection();
        if (currentTabItem == null) {
            log.warn(DefaultMessagesImpl.getString("MatchingKeySection.ONE_MATCH_RULE_REQUIRED")); //$NON-NLS-1$
            return;
        }
        MatchRuleTableComposite matchRuleTableComp = (MatchRuleTableComposite) currentTabItem
                .getData(MatchAnalysisConstant.MATCH_RULE_TABLE_COMPOSITE);
        matchRuleTableComp.addKeyDefinition(column, analysis);
    }

    /**
     * 
     * Remove the match key by column name from current selected match rule tab.
     * 
     * @param column
     */
    public void removeMatchKeyFromCurrentMatchRule(String column) {
        CTabItem currentTabItem = ruleFolder.getSelection();
        if (currentTabItem == null) {
            log.warn(DefaultMessagesImpl.getString("MatchingKeySection.ONE_MATCH_RULE_REQUIRED")); //$NON-NLS-1$
            return;
        }
        MatchRuleTableComposite matchRuleTableComp = (MatchRuleTableComposite) currentTabItem
                .getData(MatchAnalysisConstant.MATCH_RULE_TABLE_COMPOSITE);
        matchRuleTableComp.removeKeyDefinition(column, analysis);
    }

    /**
     * DOC zshen Comment method "createPropertyTab".
     * 
     * @param tabName
     * @param reComputeMatchRule
     */
    private void createPropertyTab(MatchRule matchRule, boolean reComputeMatchRule) {
        final CTabItem tabItem = new CTabItem(ruleFolder, SWT.CLOSE);
        tabItem.setText(matchRule.getName());

        Composite ruleComp = toolkit.createComposite(ruleFolder, SWT.NONE);
        GridData data = new GridData(GridData.FILL_BOTH);
        ruleComp.setLayoutData(data);

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        ruleComp.setLayout(gridLayout);
        MatchRuleTableComposite matchRuleComposite = new MatchRuleTableComposite(ruleComp, SWT.NO_FOCUS, matchRule);
        matchRuleComposite.setLayout(gridLayout);
        matchRuleComposite.setLayoutData(data);
        tabItem.setControl(ruleComp);

        tabItem.setData(MatchAnalysisConstant.MATCH_RULE_TABLE_COMPOSITE, matchRuleComposite);

        ruleFolder.setSelection(tabItem);
    }

    /**
     * DOC yyin Comment method "getNewRuleMatcher".
     */
    private MatchRule getNewMatchRule() {
        MatchRule ruleMatcher = RulesFactory.eINSTANCE.createMatchRule();
        String tabName = "Match Rule " + tabCount++; //$NON-NLS-1$
        ruleMatcher.setName(tabName);
        return ruleMatcher;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.section.AbstractMatchTableSection#createSubChart(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
    protected void createSubChart(Composite sectionClient) {
        List<String[]> viewData = getViewData();
        String[] viewColumn = getViewColumn();
        List<String[]> resultStrList = computeMatchResult(viewData);
        Composite chartComposite = toolkit.createComposite(sectionClient);
        GridLayout tableLayout = new GridLayout(1, Boolean.TRUE);
        chartComposite.setLayout(tableLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        chartComposite.setLayoutData(gridData);

        matchRuleChartComp = new MatchRuleDataChart(chartComposite, resultStrList, viewColumn);

    }

    private List<String[]> computeMatchResult(List<String[]> viewData) {
        ExecuteMatchRuleDoGroupAction executeMatchRuleDoGroupAction = new ExecuteMatchRuleDoGroupAction();
        if (hasMatchKey()) {
            RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
            List<MatchRule> matchRules = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getMatchRules();
            for (MatchRule matcher : matchRules) {
                List<Map<String, String>> ruleMatcherConvertResult = MatchRuleAnlaysisUtils
                        .ruleMatcherConvert(matcher, columnMap);
                executeMatchRuleDoGroupAction.addRuleMatcher(ruleMatcherConvertResult);
            }
            executeMatchRuleDoGroupAction.setInputList(viewData);
            executeMatchRuleDoGroupAction.run();
        }
        return executeMatchRuleDoGroupAction.getResultStrList();
    }

    private boolean hasMatchKey() {
        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        List<MatchRule> matchRules = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getMatchRules();
        for (MatchRule matchRule : matchRules) {
            if (matchRule.getMatchKeys().size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC zshen Comment method "getViewColumn".
     * 
     * @return
     */
    private String[] getViewColumn() {
        List<String> columnNameList = new ArrayList<String>();
        if (this.columnMap == null) {
            return new String[0];
        }
        for (String columnName : columnMap.keySet()) {
            columnNameList.add(columnName);
        }
        // TODO zshen, see with yyin to use the extracted constants.
        columnNameList.add("GID");
        columnNameList.add("GRP_SIZE");
        columnNameList.add("MASTER");
        columnNameList.add("SCORE");
        columnNameList.add("ATTRIBUTE_SCORES");
        return columnNameList.toArray(new String[columnNameList.size()]);
    }

    /**
     * DOC zshen Comment method "getViewData".
     * 
     * @return
     */
    private List<String[]> getViewData() {

        return tableData;
    }

    @Override
    public void refreshChart() {
        matchRuleChartComp.refresh(computeMatchResult(tableData));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#isKeyDefinitionAdded()
     */
    @Override
    public Boolean isKeyDefinitionAdded(String colummName) throws Exception {
        Boolean isAddded = Boolean.FALSE;
        CTabItem currentTabItem = ruleFolder.getSelection();
        if (currentTabItem == null) {
            throw new Exception(DefaultMessagesImpl.getString("MatchingKeySection.ONE_MATCH_RULE_REQUIRED")); //$NON-NLS-1$
        }
        MatchRuleTableComposite matchRuleTableComp = (MatchRuleTableComposite) currentTabItem
                .getData(MatchAnalysisConstant.MATCH_RULE_TABLE_COMPOSITE);
        MatchRule matchRule = matchRuleTableComp.getMatchRule();
        for (KeyDefinition keyDef : matchRule.getMatchKeys()) {
            if (StringUtils.equals(colummName, keyDef.getColumn())) {
                isAddded = Boolean.TRUE;
                break;
            }
        }
        return isAddded;
    }

}
