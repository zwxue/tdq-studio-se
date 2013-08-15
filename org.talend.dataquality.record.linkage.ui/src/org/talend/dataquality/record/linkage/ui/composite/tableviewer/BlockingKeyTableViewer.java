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
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.BlockingKeyTableContentProvider;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.BlockingKeyTableLabelProvider;
import org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.BlockingKeyPostAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.BlockingKeyPreAlgorithmEnum;
import org.talend.dataquality.rules.AlgorithmDefinition;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.RulesFactory;


/**
 * created by zshen on Aug 6, 2013
 * Detailled comment
 *
 */
public class BlockingKeyTableViewer extends AbstractMatchAnalysisTabveViewer {

    private final String BLOCK_KEY_DEFAULT_VALUE = "blocking key name"; //$NON-NLS-1$

    private List<BlockKeyDefinition> inputElements = new ArrayList<BlockKeyDefinition>();
    /**
     * DOC zshen BlockingKeyTableViewer constructor comment.
     *
     * @param parent
     * @param style
     */
    public BlockingKeyTableViewer(Composite parent, int style) {
        super(parent, style);
    }

    public List<BlockKeyDefinition> getInputElements() {
        return this.inputElements;
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
                editors[i] = new ComboBoxCellEditor(MatchTable, BlockingKeyPreAlgorithmEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            case 4:
                editors[i] = new ComboBoxCellEditor(MatchTable, BlockingKeyAlgorithmEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            case 6:
                editors[i] = new ComboBoxCellEditor(MatchTable, BlockingKeyPostAlgorithmEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            default:
                editors[i] = new TextCellEditor(MatchTable);
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
        return new BlockingKeyTableContentProvider();
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
    protected int getDisplayWeight() {
        return 8;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTabveViewer#getTestData()
     */
    @Override
    protected Object getinputData() {
        if (inputElements.size() == 0) {
            return null;
        }
        return inputElements.toArray(new BlockKeyDefinition[inputElements.size()]);
    }

    public void setInputData(List<BlockKeyDefinition> inputMatcher) {
        this.inputElements = inputMatcher;
        this.setInput(getinputData());
        this.refresh();

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

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTabveViewer#addElement(java
     * .lang.String)
     */
    @Override
    public boolean addElement(String columnName) {
        if (isAddedAlready(columnName)) {
            return false;
        }
        List<BlockKeyDefinition> bkdList = null;
        if (this.getInput() == null) {
            bkdList = new ArrayList<BlockKeyDefinition>();
        } else if (this.getInput() instanceof BlockKeyDefinition[] && ((BlockKeyDefinition[]) this.getInput()).length > 0) {
            bkdList = convertToList((BlockKeyDefinition[]) this.getInput());

        }
        bkdList.add(createDefaultRow(columnName));
        setInputData(bkdList);
        return true;
    }

    /**
     * DOC zshen Comment method "createDefaultRow".
     *
     * @param columnName
     * @return
     */
    private BlockKeyDefinition createDefaultRow(String columnName) {
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

    private boolean isAddedAlready(String columnName) {
        if (this.getInput() == null) {
            return false;
        }
        if (this.getInput() instanceof BlockKeyDefinition[] && ((BlockKeyDefinition[]) this.getInput()).length > 0) {
            BlockKeyDefinition[] inputElements = ((BlockKeyDefinition[]) this.getInput());
            for (BlockKeyDefinition element : inputElements) {
                if (element.getColumn().equals(columnName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTabveViewer#removeElement
     * (java.lang.String)
     */
    @Override
    public void removeElement(String columnName) {
        if (this.getInput() instanceof BlockKeyDefinition[] && ((BlockKeyDefinition[]) this.getInput()).length > 0) {
            List<BlockKeyDefinition> bkdList = convertToList(((BlockKeyDefinition[]) this.getInput()));
            List<BlockKeyDefinition> tempList = new ArrayList<BlockKeyDefinition>();
            tempList.addAll(bkdList);
            for (BlockKeyDefinition element : tempList) {
                if (element.getColumn().equals(columnName)) {
                    bkdList.remove(element);
                }
            }
            if (bkdList.size() != 0) {
                this.setInput(bkdList.toArray(new BlockKeyDefinition[bkdList.size()]));
            } else {
                this.setInput(null);
            }
            this.refresh();
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

}
