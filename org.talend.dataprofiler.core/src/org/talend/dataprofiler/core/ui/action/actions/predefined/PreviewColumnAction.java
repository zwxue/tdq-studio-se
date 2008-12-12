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

import org.eclipse.jface.action.Action;
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

    private TdColumn column;

    public PreviewColumnAction(TdColumn column) {
        super("Preview");
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EXPLORE_IMAGE));
        this.column = column;
    }

    @Override
    public void run() {
        if (column != null) {
            TdDataProvider dataprovider = DataProviderHelper.getTdDataProvider(column);
            ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(column);
            String tableName = ColumnSetNameHelper.getColumnSetQualifiedName(dataprovider, columnSetOwner);
            String columnName = ColumnHelper.getFullName(column);
            String query = "select " + columnName + " from " + tableName;
            CorePlugin.getDefault().runInDQViewer(dataprovider, query, null);
        }
    }
}
