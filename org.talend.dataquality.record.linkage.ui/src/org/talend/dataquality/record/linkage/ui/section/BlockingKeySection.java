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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.record.linkage.ui.action.ExecuteGenerateBlockingAction;
import org.talend.dataquality.record.linkage.ui.composite.AbsMatchAnalysisTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.BlockingKeyTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.chart.BlockingKeyDataChart;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.KeyDefinition;

/**
 * created by zshen on Aug 6, 2013 Detailled comment
 * 
 */
public class BlockingKeySection extends AbstractMatchAnaysisTableSection {

    // TODO zshen externalize the blocking key constant.
    private final String SECTION_NAME = "Blocking Key";

    private BlockingKeyDataChart blockingKeyDataChart = null;

    private AbsMatchAnalysisTableComposite tableComposite = null;

    /**
     * DOC zshen BlockingKeySection constructor comment.
     * 
     * @param parent
     * @param style
     * @param toolkit
     */
    public BlockingKeySection(final ScrolledForm form, Composite parent, int style, FormToolkit toolkit, Analysis analysis) {
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

    @Override
    protected void createSubContent(Composite sectionClient) {
        Composite ruleComp = toolkit.createComposite(sectionClient, SWT.NONE);
        GridData data = new GridData(GridData.FILL_BOTH);
        ruleComp.setLayoutData(data);

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        ruleComp.setLayout(gridLayout);
        tableComposite = new BlockingKeyTableComposite(ruleComp, SWT.NO_FOCUS);
        tableComposite.setLayout(gridLayout);
        tableComposite.setLayoutData(data);
        initTableInput();
        createRefreshButton(ruleComp);
    }

    /**
     * DOC zhao Comment method "initTableInput".
     */
    private void initTableInput() {
        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        List<BlockKeyDefinition> keyDefs = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getBlockKeys();
        List<KeyDefinition> keyDefsCopy = new ArrayList<KeyDefinition>(keyDefs.size());
        keyDefsCopy.addAll(keyDefs);
        tableComposite.setInput(keyDefsCopy);
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
        Composite blockComp = toolkit.createComposite(sectionClient, SWT.NONE);
        GridLayout tableLayout = new GridLayout(1, Boolean.TRUE);
        blockComp.setLayout(tableLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        blockComp.setLayoutData(gridData);
        ExecuteGenerateBlockingAction executeGenerateBlockingAction = computeResult();
        blockingKeyDataChart = new BlockingKeyDataChart(blockComp, executeGenerateBlockingAction.getResultDatas());

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchTableSection#RefreshChart()
     */
    @Override
    public void refreshChart() {
        ExecuteGenerateBlockingAction executeGenerateBlockingAction = computeResult();
        blockingKeyDataChart.refresh(executeGenerateBlockingAction.getResultDatas());
    }

    /**
     * DOC zshen Comment method "computeRusult".
     * 
     * @return
     */
    protected ExecuteGenerateBlockingAction computeResult() {
        List<Map<String, String>> blockingKeyData = MatchRuleAnlaysisUtils
                .blockingKeyDataConvert((List<KeyDefinition>) tableComposite.getInput());
        ExecuteGenerateBlockingAction executeGenerateBlockingAction = new ExecuteGenerateBlockingAction(blockingKeyData,
                columnMap);
        if (hasBlockingKey()) {
            executeGenerateBlockingAction.setInputData(tableData);
            executeGenerateBlockingAction.run();
        }
        return executeGenerateBlockingAction;
    }

    /**
     * DOC zshen Comment method "hasBlockingKey".
     * 
     * @return
     */
    private boolean hasBlockingKey() {
        List<KeyDefinition> inputElement = (List<KeyDefinition>) tableComposite.getInput();
        if (inputElement.size() > 0) {
            return true;
        }
        return false;
    }

    public boolean createBlockingKey(String columnName) {
        return tableComposite.addKeyDefinition(columnName, analysis);
    }

    public void removeBlockingKey(String columnName) {
        tableComposite.removeKeyDefinition(columnName, analysis);
    }

    /**
     * TODO yyin remove the mothod??<br>
     * DOC yyin Comment method "hasBlockKey".
     * 
     * @return
     */
    private boolean hasBlockKey() {
        if (((List<KeyDefinition>) tableComposite.getInput()).size() <= 0) {
            return false;
        }
        return true;
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
        Boolean isAdded = Boolean.FALSE;
        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        List<BlockKeyDefinition> keyDefs = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getBlockKeys();
        for (KeyDefinition keyDef : keyDefs) {
            if (StringUtils.equals(columnName, keyDef.getColumn())) {
                isAdded = Boolean.TRUE;
                break;
            }
        }
        return isAdded;
    }
}
