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
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchRuleContentProvider;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchRuleLabelProvider;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.record.linkage.utils.HandleNullEnum;
import org.talend.dataquality.record.linkage.utils.MatchingTypeEnum;
import org.talend.dataquality.rules.AlgorithmDefinition;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.RulesFactory;

/**
 * created by zshen on Jul 31, 2013 Detailled comment
 * 
 */
public class MatchRuleTableViewer extends AbstractMatchAnalysisTableViewer {

    private MatchRule inputMatcher = null;

    /**
     * DOC zshen MatchRuleTableViewer constructor comment.
     * 
     * @param parent
     * @param style
     */
    public MatchRuleTableViewer(Composite parent, int style) {
        super(parent, style);

    }

    /**
     * DOC zshen Comment method "getCellEditor".
     * 
     * @param headers
     * @return
     */
    @Override
    protected CellEditor[] getCellEditor(List<String> headers) {
        CellEditor[] editors = new CellEditor[headers.size()];
        for (int i = 0; i < editors.length; ++i) {
            switch (i) {

            case 2:
                editors[i] = new ComboBoxCellEditor(MatchTable, MatchingTypeEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            case 5:
                editors[i] = new ComboBoxCellEditor(MatchTable, HandleNullEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            default:
                editors[i] = new TextCellEditor(MatchTable);
            }
        }
        return editors;
    }

    /**
     * DOC zshen Comment method "getTableLabelProvider".
     * 
     * @return
     */
    @Override
    protected IBaseLabelProvider getTableLabelProvider() {
        return new MatchRuleLabelProvider();
    }

    /**
     * DOC zshen Comment method "getTableContentProvider".
     * 
     * @return
     */
    @Override
    protected IContentProvider getTableContentProvider() {
        return new MatchRuleContentProvider();
    }

    /**
     * DOC zshen Comment method "getCellModifier".
     * 
     * @return
     */
    @Override
    protected ICellModifier getTableCellModifier() {
        return new MatchRuleCellModifier(this);
    }

    /**
     * 
     * add new Element
     * 
     * @param columnName the name of column
     */
    @Override
    public boolean addElement(String columnName, Analysis analysis) {
        if (isAddedAlready(columnName)) {
            return false;
        }
        if (this.getInput() instanceof MatchRule) {
            MatchRule inputElement = (MatchRule) this.getInput();
            inputElement.getMatchKeys().add(MatchRuleAnlaysisUtils.createDefaultMatchRow(columnName));
            this.refresh();
        }
        return true;
    }

    @Override
    public void removeElement(String columnName) {
        if (this.getInput() instanceof MatchRule) {
            MatchRule inputElement = (MatchRule) this.getInput();
            List<MatchKeyDefinition> tempList = new ArrayList<MatchKeyDefinition>();
            tempList.addAll(inputElement.getMatchKeys());
            for (MatchKeyDefinition element : tempList) {
                if (element.getColumn().equals(columnName)) {
                    inputElement.getMatchKeys().remove(element);
                }
            }
            this.refresh(inputElement);
        }
    }

    /**
     * use this value to compute the vaule of column width
     * 
     * @return
     */
    @Override
    protected int getDisplayWeight() {
        return 8;
    }

    private boolean isAddedAlready(String columnName) {
        if (this.getInput() instanceof MatchRule) {
            MatchRule inputElement = (MatchRule) this.getInput();
            for (MatchKeyDefinition element : inputElement.getMatchKeys()) {
                if (element.getColumn().equals(columnName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected MatchRule getinputData() {
        if (inputMatcher == null) {
            inputMatcher = RulesFactory.eINSTANCE.createMatchRule();
        }
        return inputMatcher;
    }

    public void setInputData(MatchRule inputMatcher) {
        this.inputMatcher = inputMatcher;
        this.setInput(getinputData());
        this.refresh();

    }

    private void initData(List<MatchRule> tempMatcherList) {
        List<MatchRule> matcherList = new ArrayList<MatchRule>();
        MatchRule createRuleMatcher = RulesFactory.eINSTANCE.createMatchRule();
        MatchKeyDefinition createMatchKeyDefinition1 = RulesFactory.eINSTANCE.createMatchKeyDefinition();
        MatchKeyDefinition createMatchKeyDefinition2 = RulesFactory.eINSTANCE.createMatchKeyDefinition();
        AlgorithmDefinition createAlgorithmDefinition1 = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        AlgorithmDefinition createAlgorithmDefinition2 = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createMatchKeyDefinition1.setName("match name 1");
        createMatchKeyDefinition1.setColumn("column name 1");
        createMatchKeyDefinition1.setConfidenceWeight(1);
        createMatchKeyDefinition1.setHandleNull(HandleNullEnum.NULL_MATCH_NULL.getValue());
        createAlgorithmDefinition1.setAlgorithmParameters("1");
        createAlgorithmDefinition1.setAlgorithmType(MatchingTypeEnum.EXACT.getValue());
        createMatchKeyDefinition1.setAlgorithm(createAlgorithmDefinition1);
        createRuleMatcher.getMatchKeys().add(createMatchKeyDefinition1);
        createMatchKeyDefinition2.setName("match name 2");
        createMatchKeyDefinition2.setColumn("column name 2");
        createMatchKeyDefinition2.setConfidenceWeight(2);
        createMatchKeyDefinition2.setHandleNull(HandleNullEnum.NULL_MATCH_ALL.getValue());
        createAlgorithmDefinition2.setAlgorithmParameters("2");
        createAlgorithmDefinition2.setAlgorithmType(MatchingTypeEnum.CUSTOM.getValue());
        createMatchKeyDefinition2.setAlgorithm(createAlgorithmDefinition2);
        createRuleMatcher.getMatchKeys().add(createMatchKeyDefinition2);
        matcherList.add(createRuleMatcher);

        tempMatcherList.addAll(matcherList);
    }

}
