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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.record.linkage.ui.action.MatchRuleActionGroup;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchAnalysisTableContentProvider;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.SurvivorshipLabelProvider;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;
import org.talend.dataquality.rules.AlgorithmDefinition;
import org.talend.dataquality.rules.KeyDefinition;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dataquality.rules.RulesFactory;
import org.talend.dataquality.rules.SurvivorshipKeyDefinition;

/**
 * created by HHB on 2013-8-23 Detailled comment
 * 
 */
public class SurvivorShipTableViewer extends AbstractMatchAnalysisTableViewer {

    /**
     * DOC HHB SurvivorShipTableViewer constructor comment.
     * 
     * @param parent
     * @param style
     * @param isAddColumn
     */
    public SurvivorShipTableViewer(Composite parent, int style, boolean isAddColumn) {
        super(parent, style, isAddColumn);
    }

    private static Logger log = Logger.getLogger(SurvivorShipTableViewer.class);

    private final String KEY_DEFAULT_VALUE = "key name"; //$NON-NLS-1$

    @Override
    protected int getHeaderDisplayWeight() {
        return 10;
    }

    @Override
    public void addContextMenu() {
        MatchRuleActionGroup actionGroup = new MatchRuleActionGroup(this);
        actionGroup.fillContextMenu(new MenuManager());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#getTableLabelProvider
     * ()
     */
    @Override
    protected IBaseLabelProvider getTableLabelProvider() {
        return new SurvivorshipLabelProvider();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#
     * getTableContentProvider()
     */
    @Override
    protected IContentProvider getTableContentProvider() {
        return new MatchAnalysisTableContentProvider();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#getTableCellModifier
     * ()
     */
    @Override
    protected ICellModifier getTableCellModifier() {
        return new SurvivorShipCellModifier(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#getCellEditor
     * (java.util.List)
     */
    @Override
    protected CellEditor[] getCellEditor(List<String> headers) {
        CellEditor[] editors = new CellEditor[headers.size()];
        for (int i = 0; i < editors.length; ++i) {
            if (isAddColumn() && i == 1) {
                editors[i] = new TextCellEditor(innerTable);

            } else {
                switch (i) {

                case 0:
                    editors[i] = new TextCellEditor(innerTable);
                    break;
                case 2:
                    editors[i] = new ComboBoxCellEditor(innerTable, SurvivorShipAlgorithmEnum.getAllTypes(), SWT.READ_ONLY);
                    break;
                case 3:
                    editors[i] = new CheckboxCellEditor(innerTable);
                    break;
                default:

                }
            }
        }
        return editors;
    }

    @Override
    public boolean addElement(String columnName, Analysis analysis) {
        return false;
    }

    @Override
    public boolean addElement(String columnName, MatchRuleDefinition matchRuleDef) {
        List<SurvivorshipKeyDefinition> skdList = matchRuleDef.getSurvivorshipKeys();
        SurvivorshipKeyDefinition keyDef = createNewSurvivorshipDefinition(columnName);
        skdList.add(keyDef);
        add(keyDef);
        return true;
    }

    private SurvivorshipKeyDefinition createNewSurvivorshipDefinition(String columnName) {
        SurvivorshipKeyDefinition skd = RulesFactory.eINSTANCE.createSurvivorshipKeyDefinition();
        skd.setName(KEY_DEFAULT_VALUE);
        skd.setColumn(columnName);
        AlgorithmDefinition createAlgorithmDefinition = RulesFactory.eINSTANCE.createAlgorithmDefinition();
        createAlgorithmDefinition.setAlgorithmType(SurvivorShipAlgorithmEnum.getTypeByIndex(0).getValue());
        createAlgorithmDefinition.setAlgorithmParameters("");
        skd.setFunction(createAlgorithmDefinition);
        skd.setAllowManualResolution(true);
        return skd;
    }

    @Override
    public void removeElement(String columnName, Analysis analysis) {
    }

    @Override
    public void removeElement(KeyDefinition keyDef, Analysis analysis) {
    }

    @Override
    public void removeElement(KeyDefinition keyDef, MatchRuleDefinition matchRuleDef) {
        List<SurvivorshipKeyDefinition> skdList = matchRuleDef.getSurvivorshipKeys();
        Iterator<SurvivorshipKeyDefinition> keyIterator = skdList.iterator();
        while (keyIterator.hasNext()) {
            KeyDefinition tmpKeyDef = keyIterator.next();
            if (StringUtils.equals(keyDef.getName(), tmpKeyDef.getName())) {
                skdList.remove(keyDef);
                // Update table view.
                remove(keyDef);
                break;
            }
        }
    }

}
