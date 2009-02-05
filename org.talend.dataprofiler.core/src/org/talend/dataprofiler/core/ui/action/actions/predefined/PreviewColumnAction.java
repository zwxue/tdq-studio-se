// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions.predefined;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dq.helper.ColumnSetNameHelper;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class PreviewColumnAction extends Action {

    private TdColumn[] columns;

    public PreviewColumnAction(TdColumn[] columns) {
        super("Preview");
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EXPLORE_IMAGE));
        this.columns = columns;
    }

    @Override
    public void run() {

        if (isSelectedSameDataProvider()) {
            TdColumn oneColumn = columns[0];
            TdDataProvider dataprovider = DataProviderHelper.getTdDataProvider(oneColumn);
            ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(oneColumn);
            String tableName = ColumnSetNameHelper.getColumnSetQualifiedName(dataprovider, columnSetOwner);

            String columnClause = "";
            for (TdColumn column : columns) {
                String columnName = ColumnHelper.getFullName(column);
                columnClause += columnName + ",";
            }
            columnClause = columnClause.substring(0, columnClause.length() - 1);

            String query = "select " + columnClause + " from " + tableName;
            CorePlugin.getDefault().runInDQViewer(dataprovider, query, tableName);
        } else {
            MessageDialogWithToggle.openWarning(null, "Warning", "\r\nYou must preview columns from one table.");
        }
    }

    private boolean isSelectedSameDataProvider() {
        assert columns != null;

        Set<ColumnSet> columnSets = new HashSet<ColumnSet>();
        for (TdColumn column : columns) {
            ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(column);
            columnSets.add(columnSetOwner);
        }
        return columnSets.size() == 1;
    }
}
