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
package org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.ui.action.MatchRuleActionGroup;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchCellModifier;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.MatchRuleTableViewer;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.cellEditor.jarFileCellEditor;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchAnalysisTableContentProvider;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchAndSurvivorCellModifer;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.definition.MatchKeyAndSurvivorLabelProvider;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.record.linkage.utils.HandleNullEnum;
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
public class MatchKeyAndSurvivorshipTableViewer extends AbstractMatchAnalysisTableViewer<MatchKeyAndSurvivorDefinition> {

    private static Logger log = Logger.getLogger(MatchRuleTableViewer.class);

    private MatchRule matchRule = null;

    @Override
    protected IContentProvider getTableContentProvider() {
        return new MatchAnalysisTableContentProvider();
    }

    @Override
    protected AbstractMatchCellModifier<MatchKeyAndSurvivorDefinition> getTableCellModifier() {
        return new MatchAndSurvivorCellModifer(this);
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
        return false;
        // if (matchRule == null) {
        //                log.error(DefaultMessagesImpl.getString("MatchRuleTableViewer.NULL_MATCH_RULE_INSTANCE") + analysis.getName()); //$NON-NLS-1$
        // return false;
        // }
        // return addElement(columnName, matchRule.getMatchKeys());
    }

    @Override
    public void removeElement(String columnName, List<MatchKeyAndSurvivorDefinition> keyList) {
        // no need to implement
    }

    @Override
    public void removeElement(MatchKeyAndSurvivorDefinition msDedefinition, List<MatchKeyAndSurvivorDefinition> matchKeys) {
        Iterator<MatchKeyAndSurvivorDefinition> matchKeyIterator = matchKeys.iterator();
        while (matchKeyIterator.hasNext()) {
            MatchKeyAndSurvivorDefinition keyDef = matchKeyIterator.next();
            if (StringUtils.equals(keyDef.getMatchKey().getName(), msDedefinition.getMatchKey().getName())) {
                super.removeElement(keyDef, matchKeys);
                // link the added MatchKeyAndSurvivorDefinition's match and survivor key with matchRuleDef's matchkey
                // and
                // survivorkey list;
                SurvivorshipKeyDefinition survivorShipKey = msDedefinition.getSurvivorShipKey();
                MatchRuleDefinition matchRuleDef = (MatchRuleDefinition) survivorShipKey.eContainer();
                matchRuleDef.getMatchRules().get(0).getMatchKeys().remove(msDedefinition.getMatchKey());
                matchRuleDef.getSurvivorshipKeys().remove(survivorShipKey);
                break;
            }
        }
    }

    /**
     * use this value to compute the vaule of column width
     * 
     * @return
     */
    @Override
    protected int getHeaderDisplayWeight() {
        return 10;
    }

    public void setMatchRule(MatchRule matchRule) {
        this.matchRule = matchRule;
    }

    @Override
    public void addContextMenu() {
        MatchRuleActionGroup<MatchKeyAndSurvivorDefinition> actionGroup = new MatchRuleActionGroup<MatchKeyAndSurvivorDefinition>(
                this);
        actionGroup.fillContextMenu(new MenuManager());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#
     * createNewKeyDefinition(java.lang.String)
     */
    @Override
    protected MatchKeyAndSurvivorDefinition createNewKeyDefinition(String columnName) {
        MatchKeyDefinition matchKeyDefinition = MatchRuleAnlaysisUtils.createDefaultMatchRow(columnName);
        SurvivorshipKeyDefinition survivorshipKeyDefinition = createNewSurvivorshipKeyDefinition(columnName);
        MatchKeyAndSurvivorDefinition mAnds = new MatchKeyAndSurvivorDefinition();
        mAnds.setMatchKey(matchKeyDefinition);
        mAnds.setSurvivorShipKey(survivorshipKeyDefinition);
        return mAnds;
    }

    private SurvivorshipKeyDefinition createNewSurvivorshipKeyDefinition(String columnName) {
        SurvivorshipKeyDefinition skd = RulesFactory.eINSTANCE.createSurvivorshipKeyDefinition();
        skd.setName(StringUtils.EMPTY);
        AlgorithmDefinition createAlgorithmDefinition = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition.setAlgorithmType(SurvivorShipAlgorithmEnum.getTypeByIndex(0).getValue());
        createAlgorithmDefinition.setAlgorithmParameters(StringUtils.EMPTY);
        skd.setFunction(createAlgorithmDefinition);
        skd.setAllowManualResolution(true);
        return skd;
    }

    public MatchKeyAndSurvivorshipTableViewer(Composite parent, int style, boolean isAddColumn) {
        super(parent, style, isAddColumn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.MatchRuleTableViewer#getCellEditor(java.util.List)
     */
    @Override
    protected CellEditor[] getCellEditor(List<String> headers, List<String> columnList) {
        CellEditor[] editors = new CellEditor[headers.size()];
        for (int i = 0; i < editors.length; ++i) {
            // used for MDM T-swoosh
            switch (i) {
            case 1:
                editors[i] = new ComboBoxCellEditor(innerTable, AttributeMatcherType.getAllTypes(), SWT.READ_ONLY);
                break;
            case 2:
                editors[i] = new jarFileCellEditor(innerTable, SWT.READ_ONLY);
                break;
            case 5:
                editors[i] = new ComboBoxCellEditor(innerTable, HandleNullEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            case 6:
                editors[i] = new ComboBoxCellEditor(innerTable, SurvivorShipAlgorithmEnum.getAllTypes(), SWT.READ_ONLY);
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
        return new MatchKeyAndSurvivorLabelProvider(isAddColumn());
    }

}
