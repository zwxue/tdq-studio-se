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
package org.talend.dataquality.record.linkage.ui.composite.tableviewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.record.linkage.ui.action.MatchRuleActionGroup;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.BlockingKeyTableLabelProvider;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchAnalysisTableContentProvider;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.BlockingKeyPostAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.BlockingKeyPreAlgorithmEnum;
import org.talend.dataquality.rules.AlgorithmDefinition;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.KeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dataquality.rules.RulesFactory;

/**
 * created by zshen on Aug 6, 2013 Detailled comment
 *
 */
public class BlockingKeyTableViewer extends AbstractMatchAnalysisTableViewer {

    private static Logger log = Logger.getLogger(BlockingKeyTableViewer.class);

    private final String BLOCK_KEY_DEFAULT_VALUE = "blocking key name"; //$NON-NLS-1$

    /**
     * DOC zshen BlockingKeyTableViewer constructor comment.
     *
     * @param parent
     * @param style
     */
    public BlockingKeyTableViewer(Composite parent, int style, boolean isAddColumn) {
        super(parent, style, isAddColumn);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.MatchRuleTableViewer#getCellEditor(java.util.List)
     */
    @Override
    protected CellEditor[] getCellEditor(List<String> headers) {
        CellEditor[] editors = new CellEditor[headers.size()];
        for (int i = 0; i < editors.length; ++i) {
            switch (i) {
            case 2:
                editors[i] = new ComboBoxCellEditor(innerTable, BlockingKeyPreAlgorithmEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            case 4:
                editors[i] = new ComboBoxCellEditor(innerTable, BlockingKeyAlgorithmEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            case 6:
                editors[i] = new ComboBoxCellEditor(innerTable, BlockingKeyPostAlgorithmEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            default:
                editors[i] = new TextCellEditor(innerTable);
            }
        }
        return editors;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataquality.record.linkage.ui.composite.tableviewer.MatchRuleTableViewer#getTableLabelProvider()
     */
    @Override
    protected IBaseLabelProvider getTableLabelProvider() {
        return new BlockingKeyTableLabelProvider();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.MatchRuleTableViewer#getTableContentProvider()
     */
    @Override
    protected IContentProvider getTableContentProvider() {
        return new MatchAnalysisTableContentProvider();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataquality.record.linkage.ui.composite.tableviewer.MatchRuleTableViewer#getTableCellModifier()
     */
    @Override
    protected ICellModifier getTableCellModifier() {
        return new BlockingKeyCellModeifier(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataquality.record.linkage.ui.composite.tableviewer.MatchRuleTableViewer#getDisplayWeight()
     */
    @Override
    protected int getHeaderDisplayWeight() {
        return 8;
    }

    private void initData(List<MatchRule> tempMatcherList) {
        List<BlockKeyDefinition> matcherList = new ArrayList<BlockKeyDefinition>();
        BlockKeyDefinition createBlockKeyDefinition1 = RulesFactory.eINSTANCE.createBlockKeyDefinition();
        createBlockKeyDefinition1.setName("blocking key name 1");
        createBlockKeyDefinition1.setColumn("column namd 1");
        AlgorithmDefinition createAlgorithmDefinition1 = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition1.setAlgorithmType(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getValue());
        createAlgorithmDefinition1.setAlgorithmParameters("1");
        createBlockKeyDefinition1.setPreAlgorithm(createAlgorithmDefinition1);
        createAlgorithmDefinition1 = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition1.setAlgorithmType(BlockingKeyAlgorithmEnum.EXACT.getValue());
        createAlgorithmDefinition1.setAlgorithmParameters("1");
        createBlockKeyDefinition1.setAlgorithm(createAlgorithmDefinition1);
        createAlgorithmDefinition1 = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition1.setAlgorithmType(BlockingKeyPostAlgorithmEnum.LEFT_CHAR.getValue());
        createAlgorithmDefinition1.setAlgorithmParameters("1");
        createBlockKeyDefinition1.setPostAlgorithm(createAlgorithmDefinition1);
        matcherList.add(createBlockKeyDefinition1);

        BlockKeyDefinition createBlockKeyDefinition2 = RulesFactory.eINSTANCE.createBlockKeyDefinition();

        createBlockKeyDefinition2.setName("blocking key name 2");
        createBlockKeyDefinition2.setColumn("column namd 2");
        AlgorithmDefinition createAlgorithmDefinition2 = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition2.setAlgorithmType(BlockingKeyPreAlgorithmEnum.UPPER_CASE.getValue());
        createAlgorithmDefinition2.setAlgorithmParameters("2");
        createBlockKeyDefinition2.setPreAlgorithm(createAlgorithmDefinition2);
        createAlgorithmDefinition2 = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition2.setAlgorithmType(BlockingKeyAlgorithmEnum.EXACT.getValue());
        createAlgorithmDefinition2.setAlgorithmParameters("2");
        createBlockKeyDefinition2.setAlgorithm(createAlgorithmDefinition2);
        createAlgorithmDefinition2 = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition2.setAlgorithmType(BlockingKeyPostAlgorithmEnum.LEFT_CHAR.getValue());
        createAlgorithmDefinition2.setAlgorithmParameters("2");
        createBlockKeyDefinition2.setPostAlgorithm(createAlgorithmDefinition2);
        matcherList.add(createBlockKeyDefinition2);

        // return matcherList.toArray(new BlockKeyDefinition[matcherList.size()]);

    }

    /**
     *
     * add new Element
     *
     * @param columnName the name of column
     * @param analysis the context of this add operation perform on.
     */
    @Override
    public boolean addElement(String columnName, Analysis analysis) {
        RecordMatchingIndicator recordMatchingIndiator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        if (recordMatchingIndiator == null) {
            log.error("null record matching indicator for analysis: " + analysis.getName());
            return Boolean.FALSE;
        }
        return addElement(columnName, recordMatchingIndiator.getBuiltInMatchRuleDefinition());

    }

    /**
     * DOC zshen Comment method "createDefaultRow".
     *
     * @param columnName
     * @return
     */
    private BlockKeyDefinition createNewBlockDefinition(String columnName) {
        BlockKeyDefinition createBlockKeyDefinition = RulesFactory.eINSTANCE.createBlockKeyDefinition();
        createBlockKeyDefinition.setName(BLOCK_KEY_DEFAULT_VALUE);
        createBlockKeyDefinition.setColumn(columnName);
        AlgorithmDefinition createAlgorithmDefinition = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition.setAlgorithmType(BlockingKeyPreAlgorithmEnum.getTypeByIndex(0).getValue());
        createAlgorithmDefinition.setAlgorithmParameters("");
        createBlockKeyDefinition.setPreAlgorithm(createAlgorithmDefinition);

        createAlgorithmDefinition = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition.setAlgorithmType(BlockingKeyAlgorithmEnum.EXACT.getValue());
        createAlgorithmDefinition.setAlgorithmParameters("");
        createBlockKeyDefinition.setAlgorithm(createAlgorithmDefinition);

        createAlgorithmDefinition = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition.setAlgorithmType(BlockingKeyPostAlgorithmEnum.getTypeByIndex(0).getValue());
        createAlgorithmDefinition.setAlgorithmParameters("");
        createBlockKeyDefinition.setPostAlgorithm(createAlgorithmDefinition);
        return createBlockKeyDefinition;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTabveViewer#removeElement
     * (java.lang.String)
     */
    @Override
    public void removeElement(String columnName, Analysis analysis) {
        RecordMatchingIndicator recordMatchingIndiator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        List<BlockKeyDefinition> bkdList = recordMatchingIndiator.getBuiltInMatchRuleDefinition().getBlockKeys();
        Iterator<BlockKeyDefinition> blockKeyIterator = bkdList.iterator();
        while (blockKeyIterator.hasNext()) {
            KeyDefinition keyDef = blockKeyIterator.next();
            if (StringUtils.equals(keyDef.getColumn(), columnName)) {
                bkdList.remove(keyDef);
                // Update table view.
                remove(keyDef);
                break;
            }
        }
    }

    /**
     * DOC yyin Comment method "convertToList".
     *
     * @param asList
     * @return
     */
    private List<BlockKeyDefinition> convertToList(BlockKeyDefinition[] blockArray) {
        List<BlockKeyDefinition> resultList = new ArrayList<BlockKeyDefinition>();
        for (BlockKeyDefinition blocKey : blockArray) {
            resultList.add(blocKey);
        }
        return resultList;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#addContextMenu()
     */
    @Override
    public void addContextMenu() {
        MatchRuleActionGroup actionGroup = new MatchRuleActionGroup(this);
        actionGroup.fillContextMenu(new MenuManager());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#addElement(java
     * .lang.String, org.talend.dataquality.rules.MatchRuleDefinition)
     */
    @Override
    public boolean addElement(String columnName, MatchRuleDefinition matchRuleDef) {
        List<BlockKeyDefinition> bkdList = matchRuleDef.getBlockKeys();
        BlockKeyDefinition blockKeyDef = createNewBlockDefinition(columnName);
        bkdList.add(blockKeyDef);
        add(blockKeyDef);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#removeElement
     * (org.talend.dataquality.rules.KeyDefinition, org.talend.dataquality.analysis.Analysis)
     */
    @Override
    public void removeElement(KeyDefinition keyDef, Analysis analysis) {
        RecordMatchingIndicator recordMatchingIndiator = MatchRuleAnlaysisUtils.getRecordMatchIndicatorFromAna(analysis);
        removeElement(keyDef, recordMatchingIndiator.getBuiltInMatchRuleDefinition());

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#removeElement
     * (org.talend.dataquality.rules.KeyDefinition, org.talend.dataquality.rules.MatchRuleDefinition)
     */
    @Override
    public void removeElement(KeyDefinition keyDef, MatchRuleDefinition matchRuleDef) {
        List<BlockKeyDefinition> bkdList = matchRuleDef.getBlockKeys();
        Iterator<BlockKeyDefinition> blockKeyIterator = bkdList.iterator();
        while (blockKeyIterator.hasNext()) {
            KeyDefinition tmpKeyDef = blockKeyIterator.next();
            if (StringUtils.equals(keyDef.getName(), tmpKeyDef.getName())) {
                bkdList.remove(keyDef);
                // Update table view.
                remove(keyDef);
                break;
            }
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#moveUpElement
     * (org.talend.dataquality.rules.KeyDefinition, org.talend.dataquality.rules.MatchRuleDefinition)
     */
    @Override
    public void moveUpElement(KeyDefinition keyDef, MatchRuleDefinition matchRuleDef) {
        List<BlockKeyDefinition> bkdList = matchRuleDef.getBlockKeys();
        Iterator<BlockKeyDefinition> blockKeyIterator = bkdList.iterator();
        while (blockKeyIterator.hasNext()) {
            KeyDefinition tmpKeyDef = blockKeyIterator.next();
            if (StringUtils.equals(keyDef.getName(), tmpKeyDef.getName())) {
                int indexForElement = indexForElement(tmpKeyDef);
                if(indexForElement - 2>=0) {
                    // modify model
                    bkdList.remove(keyDef);
                    bkdList.add(indexForElement - 2, (BlockKeyDefinition) keyDef);
                    // modify table viewer
                    remove(keyDef);
                    insert(keyDef, indexForElement - 2);
                }
                break;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#moveDownElement
     * (org.talend.dataquality.rules.KeyDefinition, org.talend.dataquality.rules.MatchRuleDefinition)
     */
    @Override
    public void moveDownElement(KeyDefinition keyDef, MatchRuleDefinition matchRuleDef) {
        List<BlockKeyDefinition> bkdList = matchRuleDef.getBlockKeys();
        Iterator<BlockKeyDefinition> blockKeyIterator = bkdList.iterator();
        while (blockKeyIterator.hasNext()) {
            KeyDefinition tmpKeyDef = blockKeyIterator.next();
            if (StringUtils.equals(keyDef.getName(), tmpKeyDef.getName())) {
                int indexForElement = indexForElement(tmpKeyDef);
                if (indexForElement < bkdList.size()) {
                    // modify model
                    bkdList.remove(keyDef);
                    if (indexForElement == bkdList.size()) {
                        bkdList.add((BlockKeyDefinition) keyDef);
                    } else {
                        bkdList.add(indexForElement, (BlockKeyDefinition) keyDef);
                    }
                    // modify table viewer
                    remove(keyDef);
                    insert(keyDef, indexForElement + 1);
                }
                break;
            }
        }

    }


}
