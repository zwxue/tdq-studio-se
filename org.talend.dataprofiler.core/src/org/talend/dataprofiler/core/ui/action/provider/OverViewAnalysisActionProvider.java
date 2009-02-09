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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.ui.action.actions.OverviewAnalysisAction;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC rli class global comment. Detailled comment
 */
public class OverViewAnalysisActionProvider extends CommonActionProvider {

    /**
     * DOC rli OverViewAnalysisActionProvider constructor comment.
     */
    public OverViewAnalysisActionProvider() {
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());
        List list = currentSelection.toList();
        int catlogNumber = 0;
        int schemaNumber = 0;
        List<Package> packageList = new ArrayList<Package>();
        for (Object obj : list) {
            if (obj instanceof Catalog) {
                packageList.add((Catalog) obj);
                catlogNumber++;
            } else {
                packageList.add((Schema) obj);
                schemaNumber++;
            }
            if (catlogNumber != 0 && schemaNumber != 0) {
                return;
            }
        }
        OverviewAnalysisAction overviewAnalysisAction = new OverviewAnalysisAction(packageList.toArray(new Package[packageList
                .size()]));

        menu.add(overviewAnalysisAction);
    }

}
