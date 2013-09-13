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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.record.linkage.genkey.BlockingKeyHandler;
import org.talend.dataquality.record.linkage.ui.composite.AbsMatchAnalysisTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.BlockingKeyTableComposite;
import org.talend.dataquality.record.linkage.ui.composite.chart.BlockingKeyDataChart;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.sorter.KeyDefinitionTableViewerSorter;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.KeyDefinition;
import org.talend.dataquality.rules.MatchRuleDefinition;

/**
 * created by zshen on Aug 6, 2013 Detailled comment
 * 
 */
public class BlockingKeySection extends AbstractMatchAnaysisTableSection {

    private BlockingKeyDataChart blockingKeyDataChart = null;

    public AbsMatchAnalysisTableComposite<BlockKeyDefinition> tableComposite = null;

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
        return MatchAnalysisConstant.BlOCKING_KEY_SECTION_NAME;
    }

    @Override
    protected Composite createSubContent(Composite sectionClient) {
        Composite ruleComp = toolkit.createComposite(sectionClient, SWT.NONE);
        GridData data = new GridData(GridData.FILL_BOTH);
        ruleComp.setLayoutData(data);

        GridLayout gridLayout = new GridLayout(1, true);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        ruleComp.setLayout(gridLayout);
        tableComposite = createTableComposite(ruleComp);
        tableComposite.addPropertyChangeListener(this);
        tableComposite.setAddColumn(isAddColumn());
        tableComposite.setLayout(gridLayout);
        tableComposite.setLayoutData(data);
        tableComposite.createContent();
        tableComposite.serViewerSorter(new KeyDefinitionTableViewerSorter<BlockKeyDefinition>(getBlockKeyDefinitionList()));
        initTableInput();
        return ruleComp;
    }

    protected BlockingKeyTableComposite createTableComposite(Composite parent) {
        return new BlockingKeyTableComposite(parent, SWT.NO_FOCUS);
    }

    /**
     * DOC zhao Comment method "initTableInput".
     */
    private void initTableInput() {
        tableComposite.setInput(getBlockKeyDefinitionList());
    }

    protected List<BlockKeyDefinition> getBlockKeyDefinitionList() {
        return getMatchRuleDefinition().getBlockKeys();
    }

    protected MatchRuleDefinition getMatchRuleDefinition() {
        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        return recordMatchingIndicator.getBuiltInMatchRuleDefinition();
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
        // when there are no data, no need to compute.
        if (matchRows == null || matchRows.isEmpty()) {
            blockingKeyDataChart = new BlockingKeyDataChart(blockComp, new HashMap<String, List<String[]>>());
        } else {
            BlockingKeyHandler executeGenerateBlockingAction = computeResult();
            blockingKeyDataChart = new BlockingKeyDataChart(blockComp, executeGenerateBlockingAction.getResultDatas());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchTableSection#RefreshChart()
     */
    @Override
    public void refreshChart() {
        BlockingKeyHandler executeGenerateBlockingAction = computeResult();
        blockingKeyDataChart.refresh(executeGenerateBlockingAction.getResultDatas());
        executeGenerateBlockingAction.getResultDatas().clear();
    }

    /**
     * DOC zshen Comment method "computeRusult". <br>
     * TODO Handle the return value: return the result directly instead of action instance.
     * 
     * @return
     */
    protected BlockingKeyHandler computeResult() {
        List<Map<String, String>> blockingKeyData = MatchRuleAnlaysisUtils
                .blockingKeyDataConvert((List<KeyDefinition>) tableComposite.getInput());
        BlockingKeyHandler executeGenerateBlockingAction = new BlockingKeyHandler(blockingKeyData, columnMap);
        if (hasBlockingKey()) {
            executeGenerateBlockingAction.setInputData(matchRows);
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
        return tableComposite.addKeyDefinition(columnName, getBlockKeyDefinitionList());
    }

    public void removeBlockingKey(String columnName) {
        tableComposite.removeKeyDefinition(columnName, getBlockKeyDefinitionList());
    }

    public void removeBlockingKey(BlockKeyDefinition blockKeyDef) {
        tableComposite.removeKeyDefinition(blockKeyDef, getBlockKeyDefinitionList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#isKeyDefinitionAdded(java.lang
     * .String)
     */
    @Override
    public Boolean isKeyDefinitionAdded(String columnName) {
        Boolean isAdded = Boolean.FALSE;
        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        List<BlockKeyDefinition> keyDefs = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getBlockKeys();
        for (KeyDefinition keyDef : keyDefs) {
            // the key's name can NOT be same, the column can be same
            if (StringUtils.equals(columnName, keyDef.getName())) {
                isAdded = Boolean.TRUE;
                break;
            }
        }
        return isAdded;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#addTableItem()
     */
    @Override
    public void addTableItem() {
        this.createBlockingKey(StringUtils.EMPTY);
        listeners.firePropertyChange(MatchAnalysisConstant.ISDIRTY_PROPERTY, true, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#removeTableItem()
     */
    @Override
    public void removeTableItem() {
        boolean success = false;
        ISelection selectItems = tableComposite.getSelectItems();
        if (selectItems instanceof StructuredSelection) {
            Iterator<BlockKeyDefinition> iterator = ((StructuredSelection) selectItems).iterator();
            while (iterator.hasNext()) {
                BlockKeyDefinition next = iterator.next();
                removeBlockingKey(next);
                success = true;
            }
            if (success) {
                listeners.firePropertyChange(MatchAnalysisConstant.ISDIRTY_PROPERTY, true, false);
            }
        }

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
            List<BlockKeyDefinition> currentElements = getMatchRuleDefinition().getBlockKeys();
            List<BlockKeyDefinition> blockKeyDefinitionlist = ((StructuredSelection) selectItems).toList();
            for (int index = 0; index < blockKeyDefinitionlist.size(); index++) {
                if (!isSameWithCurrentModel(currentElements.get(index), blockKeyDefinitionlist.get(index))) {
                    continue;
                }
                BlockKeyDefinition next = blockKeyDefinitionlist.get(index);
                tableComposite.moveUpKeyDefinition(next, currentElements);
            }
            tableComposite.selectAllItem(((StructuredSelection) selectItems).toList());
        }
    }

    /**
     * /* (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#moveDownTableItem()
     */
    @Override
    public void moveDownTableItem() {
        ISelection selectItems = tableComposite.getSelectItems();
        if (selectItems instanceof StructuredSelection) {
            if (selectItems.isEmpty()) {
                return;
            }
            List<BlockKeyDefinition> currentElements = getMatchRuleDefinition().getBlockKeys();
            List<BlockKeyDefinition> blockKeyDefinitionlist = ((StructuredSelection) selectItems).toList();
            for (int index = blockKeyDefinitionlist.size() - 1; 0 <= index; index--) {
                if (!isSameWithCurrentModel(currentElements.get(currentElements.size() - blockKeyDefinitionlist.size() + index),
                        blockKeyDefinitionlist.get(index))) {
                    continue;
                }
                BlockKeyDefinition next = blockKeyDefinitionlist.get(index);
                tableComposite.moveDownKeyDefinition(next, currentElements);
            }
            tableComposite.selectAllItem(((StructuredSelection) selectItems).toList());
        }
    }

    /**
     * get all columns which is selected as blocking key
     * 
     * @return
     */
    public List<String> getSelectedColumnAsBlockKeys() {
        List<String> keyColumns = new ArrayList<String>();
        RecordMatchingIndicator recordMatchingIndicator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        List<BlockKeyDefinition> keyDefs = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getBlockKeys();
        if (keyDefs.size() > 0) {
            for (KeyDefinition keydef : keyDefs) {
                keyColumns.add(keydef.getColumn());
            }
        }
        return keyColumns;
    }

    /**
     * if overwrite: need to delete all current keyss, and insert the keys according to the parameter:matchRule; else:
     * only add the keys in the parameter matchrule, to the current keys.
     * 
     * @param matchRule
     * @param overwrite
     */
    public void importMatchRule(MatchRuleDefinition matchRule, boolean overwrite) {
        if (overwrite) {
            // clear current keys
            this.getBlockKeyDefinitionList().clear();
            tableComposite.setInput(null);
            this.tableComposite.redraw();
        }
        List<String> conflictKeys = new ArrayList<String>();
        for (BlockKeyDefinition blockKey : matchRule.getBlockKeys()) {
            if (!overwrite && isKeyDefinitionAdded(blockKey.getName())) {
                // if conflict with current ones, do not import
                conflictKeys.add(blockKey.getName());
                continue;
            }

            BlockKeyDefinition blockKeyDefinition = EcoreUtil.copy(blockKey);

            setColumnValueIfMatch(blockKeyDefinition);

            tableComposite.addKeyDefinition(blockKeyDefinition, this.getMatchRuleDefinition().getBlockKeys());
        }
        // if there are some conflict keys, popup to let the user know
        if (conflictKeys.size() > 0) {
            StringBuffer names = new StringBuffer();
            for (String name : conflictKeys) {
                names.append(name);
                names.append(PluginConstant.COMMA_STRING);
            }
            if (names.length() > 0) {
                names.deleteCharAt(names.lastIndexOf(PluginConstant.COMMA_STRING));
            }
            MessageDialog.openWarning(null, DefaultMessagesImpl.getString("BlockingKeySection.conflictImport"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("BlockingKeySection.conflictImportKeys") + names.toString()); //$NON-NLS-1$

        }
    }

}
