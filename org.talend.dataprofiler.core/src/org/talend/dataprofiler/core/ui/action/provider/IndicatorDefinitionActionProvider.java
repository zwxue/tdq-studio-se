// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.ui.action.actions.OpenIndicatorDefinitionAction;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class IndicatorDefinitionActionProvider extends CommonActionProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void fillContextMenu(IMenuManager menu) {
        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());
        IndicatorDefinition[] definitiones = new IndicatorDefinition[currentSelection.size()];
        Object[] objs = currentSelection.toArray();

        for (int i = 0; i < objs.length; i++) {
            if (objs[i] instanceof IndicatorDefinition) {
                definitiones[i] = (IndicatorDefinition) objs[i];
            }
        }

        menu.add(new OpenIndicatorDefinitionAction(definitiones));
    }
}
