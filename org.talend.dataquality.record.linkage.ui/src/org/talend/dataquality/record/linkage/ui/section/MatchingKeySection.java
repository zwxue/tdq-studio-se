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
import org.talend.dataquality.record.linkage.ui.action.ExecuteMatchRuleDoGroupAction;
import org.talend.dataquality.record.linkage.ui.composite.MatchRuleTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.chart.MatchRuleDataChart;
import org.talend.dataquality.record.linkage.ui.composite.utils.ImageLib;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.RulesFactory;


/**
 * created by zshen on Jul 31, 2013
 * Detailled comment
 *
 */
public class MatchingKeySection extends AbstractMatchTableSection {

    private final String SECTION_NAME = "Matching Key"; //$NON-NLS-1$

    private CTabFolder ruleFolder;

    private static final Image ADD_IMG = ImageLib.getImage(ImageLib.ADD_ACTION);

    private int tabCount = 1;

    private MatchRuleTableComposite matchRuleComposite = null;

    private List<MatchRule> matcherList = new ArrayList<MatchRule>();

    private MatchRuleDataChart matchRuleChartComp = null;

    /**
     * DOC zshen MatchingKeySection constructor comment.
     *
     * @param parent
     * @param style
     * @param toolkit
     */
    public MatchingKeySection(final ScrolledForm form, Composite parent, int style, FormToolkit toolkit) {
        super(form, parent, style, toolkit);
    }

    public List<MatchRule> getMatcherList() {
        return this.matcherList;
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

    /**
     * Getter for ruleFolder.
     *
     * @return the ruleFolder
     */
    public CTabFolder getRuleFolder() {
        return this.ruleFolder;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.section.AbstractMatchTableSection#createSubContext(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
    protected void createSubContext(Composite sectionClient) {

        Composite tableComposite = toolkit.createComposite(sectionClient);
        GridLayout tableLayout = new GridLayout(1, Boolean.TRUE);
        tableComposite.setLayout(tableLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        tableComposite.setLayoutData(gridData);

        ruleFolder = new CTabFolder(tableComposite, SWT.MULTI);
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
                CTabFolder tabFolder = (CTabFolder) event.getSource();
                CTabItem tabItem = (CTabItem) event.item;
                int index = tabFolder.indexOf(tabItem);
                matcherList.remove(index);
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
                addRuleTab(true);
            }
        });
        addRuleTab(false);
        createRefrshButton(tableComposite);

    }



    /**
     * add properties tab
     */
    public CTabItem addRuleTab(boolean reComputeMatchRule) {
        String tabName = "Match Rule " + tabCount++; //$NON-NLS-1$
        return createPropertyTab(tabName, reComputeMatchRule);
    }

    /**
     * DOC zshen Comment method "createPropertyTab".
     *
     * @param tabName
     * @param reComputeMatchRule
     */
    private CTabItem createPropertyTab(String tabName, boolean reComputeMatchRule) {
        final CTabItem tabItem = new CTabItem(ruleFolder, SWT.CLOSE);
        tabItem.setText(tabName);

        Composite ruleComp = toolkit.createComposite(ruleFolder, SWT.NONE);
        GridData data = new GridData(GridData.FILL_BOTH);
        ruleComp.setLayoutData(data);

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        ruleComp.setLayout(gridLayout);
        matchRuleComposite = new MatchRuleTableComposite(ruleComp, SWT.NO_FOCUS);
        matchRuleComposite.setLayout(gridLayout);
        matchRuleComposite.setLayoutData(data);

        matchRuleComposite.setInputData(getNewRuleMatcher());
        tabItem.setControl(ruleComp);

        tabItem.setData(MatchAnalysisConstant.MATCH_RULE_TABLE_COMPOSITE, matchRuleComposite);

        ruleFolder.setSelection(tabItem);
        return tabItem;
    }

    /**
     * DOC yyin Comment method "getNewRuleMatcher".
     */
    private MatchRule getNewRuleMatcher() {
        MatchRule createRuleMatcher = RulesFactory.eINSTANCE.createMatchRule();
        this.matcherList.add(createRuleMatcher);
        return createRuleMatcher;
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
        // if (matcherList == null || matcherList.size() < 1) {
        // // when there are no match key, should not create the chart
        // return;
        // }
        // matchRuleComposite.setInputData(matcherList);

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
            for (MatchRule matcher : matcherList) {
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
        for (MatchRule matcher : matcherList) {
            if (matcher.getMatchKeys().size() > 0) {
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
    public void RefreshChart() {
        matchRuleChartComp.refresh(computeMatchResult(tableData));
    }
}
