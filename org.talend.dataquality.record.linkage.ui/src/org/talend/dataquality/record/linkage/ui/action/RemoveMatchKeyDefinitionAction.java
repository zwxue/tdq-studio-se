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
package org.talend.dataquality.record.linkage.ui.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.DefaultSurvivorshipDefinition;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dataquality.rules.SurvivorshipKeyDefinition;

/**
 * created by zshen on Aug 2, 2013 Detailled comment
 *
 */
public class RemoveMatchKeyDefinitionAction<T> extends Action {

    private AbstractMatchAnalysisTableViewer<T> tableViewer = null;

    public RemoveMatchKeyDefinitionAction(AbstractMatchAnalysisTableViewer<T> tableViewer) {
        setText(DefaultMessagesImpl.getString("RemoveMatchKeyDefinitionAction.delete")); //$NON-NLS-1$
        this.tableViewer = tableViewer;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IStructuredSelection structuredSelection = (IStructuredSelection) tableViewer.getSelection();
        List<T> selections = structuredSelection.toList();
        for (T selection : selections) {
            T keyDef =  selection;
            tableViewer.removeElement(keyDef, getNeedKeyDefinitionList(keyDef));
        }
    }

    /**
     * DOC zshen Comment method "getMatchRuleDefinition".
     *
     * @param keyDef
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<T> getNeedKeyDefinitionList(T keyDef) {
        List<T> returnList = new ArrayList<T>();
        if (keyDef == null) {
            // don't need to do anything at here
        } else if (keyDef instanceof MatchKeyDefinition) {
            // keyDef is MatchKeyDefiniton case
            for (MatchKeyDefinition tempKeyDef : ((MatchRule) ((MatchKeyDefinition) keyDef).eContainer()).getMatchKeys()) {
                returnList.add((T) tempKeyDef);
            }
        } else if (keyDef instanceof BlockKeyDefinition) {
            // keyDef is BlockKeyDefiniton case
            for (BlockKeyDefinition tempKeyDef : ((MatchRuleDefinition) ((BlockKeyDefinition) keyDef).eContainer())
                    .getBlockKeys()) {
                returnList.add((T) tempKeyDef);
            }
        } else if (keyDef instanceof SurvivorshipKeyDefinition) {
            // keyDef is BlockKeyDefiniton case
            for (SurvivorshipKeyDefinition tempKeyDef : ((MatchRuleDefinition) ((SurvivorshipKeyDefinition) keyDef).eContainer())
                    .getSurvivorshipKeys()) {
                returnList.add((T) tempKeyDef);
        }
        } else if (keyDef instanceof DefaultSurvivorshipDefinition) {
            // keyDef is BlockKeyDefiniton case
            for (DefaultSurvivorshipDefinition tempKeyDef : ((MatchRuleDefinition) ((DefaultSurvivorshipDefinition) keyDef)
                    .eContainer())
                    .getDefaultSurvivorshipDefinitions()) {
                returnList.add((T) tempKeyDef);
            }
        }
        return returnList;
    }

}
