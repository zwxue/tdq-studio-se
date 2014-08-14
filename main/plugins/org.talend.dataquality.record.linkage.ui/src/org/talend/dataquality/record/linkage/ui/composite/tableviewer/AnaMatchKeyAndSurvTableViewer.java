// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.cellEditor.jarFileCellEditor;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition.MatchKeyAndSurvivorDefinition;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition.MatchKeyAndSurvivorshipTableViewer;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.AnaMatchKeyAndSurvLabelProvider;
import org.talend.dataquality.record.linkage.utils.HandleNullEnum;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.MatchRuleDefinition;

/**
 * The implementation specific to analysis editor.
 */
public class AnaMatchKeyAndSurvTableViewer extends MatchKeyAndSurvivorshipTableViewer {

    private MatchRule matchRule = null;

    /**
     * DOC zhao AnaMatchKeyAndSurvTableViewer constructor comment.
     * 
     * @param parent
     * @param style
     * @param isAddColumn
     */
    public AnaMatchKeyAndSurvTableViewer(Composite parent, int style, boolean isAddColumn) {
        super(parent, style, isAddColumn);
    }

    public AnaMatchKeyAndSurvTableViewer(Composite parent, int style, boolean isAddColumn, MatchRule matchRule) {
        this(parent, style, isAddColumn);
        this.matchRule = matchRule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition.MatchKeyAndSurvivorshipTableViewer#
     * getTableLabelProvider()
     */
    @Override
    protected IBaseLabelProvider getTableLabelProvider() {
        return new AnaMatchKeyAndSurvLabelProvider(isAddColumn());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition.MatchKeyAndSurvivorshipTableViewer#
     * getCellEditor(java.util.List, java.util.List)
     */
    @Override
    protected CellEditor[] getCellEditor(List<String> headers, List<MetadataColumn> columnList) {
        CellEditor[] editors = new CellEditor[headers.size()];
        for (int i = 0; i < editors.length; ++i) {
            // used for MDM T-swoosh
            switch (i) {
            case 1:
                String[] colArray = new String[columnList.size()];
                int idx = 0;
                for (MetadataColumn metaCol : columnList) {
                    colArray[idx++] = metaCol.getName();
                }
                editors[i] = new ComboBoxCellEditor(innerTable, colArray, SWT.READ_ONLY);
                if (colArray.length > 0) {
                    ((ComboBoxCellEditor) editors[i]).setValue(0);
                }
                break;
            case 2:
                editors[i] = new ComboBoxCellEditor(innerTable, AttributeMatcherType.getAllTypes(), SWT.READ_ONLY);
                break;
            case 3:
                editors[i] = new jarFileCellEditor(innerTable, SWT.READ_ONLY);
                break;
            case 6:
                editors[i] = new ComboBoxCellEditor(innerTable, HandleNullEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            case 7:
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
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition.MatchKeyAndSurvivorshipTableViewer#
     * createNewKeyDefinition(java.lang.String)
     */
    @Override
    protected MatchKeyAndSurvivorDefinition createNewKeyDefinition(String columnName) {
        MatchKeyAndSurvivorDefinition matchKeySurvDef = super.createNewKeyDefinition(columnName);
        matchRule.getMatchKeys().add(matchKeySurvDef.getMatchKey());
        ((MatchRuleDefinition) matchRule.eContainer()).getSurvivorshipKeys().add(matchKeySurvDef.getSurvivorShipKey());
        return matchKeySurvDef;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#moveUpFromModel
     * (java.lang.Object, java.util.List, int)
     */
    @Override
    protected void moveUpFromModel(MatchKeyAndSurvivorDefinition keyDef, List<MatchKeyAndSurvivorDefinition> keyList,
            int indexForElement) {
        super.moveUpFromModel(keyDef, keyList, indexForElement);
        // Move up Match key
        matchRule.getMatchKeys().remove(keyDef.getMatchKey());
        matchRule.getMatchKeys().add(indexForElement - 2, keyDef.getMatchKey());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#moveDownFromModel
     * (java.lang.Object, java.util.List, int)
     */
    @Override
    protected void moveDownFromModel(MatchKeyAndSurvivorDefinition keyDef, List<MatchKeyAndSurvivorDefinition> keyList,
            int indexForElement) {
        super.moveDownFromModel(keyDef, keyList, indexForElement);
        // modify model for match key.
        matchRule.getMatchKeys().remove(keyDef.getMatchKey());
        if (indexForElement == keyList.size()) {
            matchRule.getMatchKeys().add(keyDef.getMatchKey());
        } else {
            matchRule.getMatchKeys().add(indexForElement, keyDef.getMatchKey());
        }

    }

}
