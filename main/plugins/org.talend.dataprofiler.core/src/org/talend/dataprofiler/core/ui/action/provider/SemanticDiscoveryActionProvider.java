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
package org.talend.dataprofiler.core.ui.action.provider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.model.MetadataTableWithFilter;
import org.talend.dataprofiler.core.ui.action.actions.predefined.SemanticDiscoveryAction;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DBTableRepNode;

public class SemanticDiscoveryActionProvider extends AbstractCommonActionProvider {

    private SemanticDiscoveryAction semanticDiscoveryAction;

    public SemanticDiscoveryActionProvider() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
     */
    @Override
    public void init(ICommonActionExtensionSite site) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());

        Object firstElement = currentSelection.getFirstElement();
        // when the selection is valid, only two possible status: only one columnset is select.
        if (firstElement instanceof DBTableRepNode) {
            DBTableRepNode node = (DBTableRepNode) firstElement;
            semanticDiscoveryAction = new SemanticDiscoveryAction(node.getTdTable());
            // otherwise is some columns in the same columnset are selected.
        } else {
            Set<String> currentTableSet = new HashSet<String>();
            TdTable createTdTable = RelationalFactory.eINSTANCE.createTdTable();
            List<String> filterNames = new ArrayList<String>();
            Iterator<Object> columnIterator = currentSelection.iterator();
            TdTable columnOwnerAsTdTable = null;
            while (columnIterator.hasNext()) {
                Object columnNode = columnIterator.next();
                if (DBColumnRepNode.class.isInstance(columnNode)) {
                    TdColumn tdColumn = ((DBColumnRepNode) columnNode).getTdColumn();
                    columnOwnerAsTdTable = ColumnHelper.getColumnOwnerAsTdTable(tdColumn);
                    currentTableSet.add(ResourceHelper.getUUID(columnOwnerAsTdTable));
                    // all of columns should come from same table
                    if (currentTableSet.size() > 1) {
                        return;
                    } else if (currentTableSet.size() == 1) {
                        createTdTable = columnOwnerAsTdTable;
                    }
                    filterNames.add(tdColumn.getName());
                } else {
                    // If not all of elements which be selected is columns
                    return;
                }
            }
            MetadataTable metadataTableWithFilter = new MetadataTableWithFilter(filterNames, createTdTable);
            semanticDiscoveryAction = new SemanticDiscoveryAction(metadataTableWithFilter);
        }
        menu.add(semanticDiscoveryAction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.provider.AbstractCommonActionProvider#isShowMenu()
     */
    @Override
    public boolean isShowMenu() {
        boolean showMenu = super.isShowMenu();
        if (showMenu && CorePlugin.getDefault().getSemanticStudioService() == null) {
            return false;
        }
        return showMenu;
    }

}
