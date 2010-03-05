// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import org.talend.dataprofiler.core.ui.action.actions.TableViewFilterAction;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableViewFilterActionProvider extends CommonActionProvider {

    public TableViewFilterActionProvider() {
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());
        Object obj = currentSelection.getFirstElement();
        if (obj instanceof Catalog || obj instanceof Schema) {
            TableViewFilterAction tvfAction = new TableViewFilterAction((Package) obj);
            menu.add(tvfAction);
        }
    }
}
