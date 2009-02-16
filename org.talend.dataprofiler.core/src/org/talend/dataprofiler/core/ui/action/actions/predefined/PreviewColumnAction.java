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
package org.talend.dataprofiler.core.ui.action.actions.predefined;

import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.helper.ColumnSetNameHelper;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class PreviewColumnAction extends Action {

    private TdColumn[] columns;

    public PreviewColumnAction(TdColumn[] columns) {
        super(DefaultMessagesImpl.getString("PreviewColumnAction.Preview")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EXPLORE_IMAGE));
        this.columns = columns;
    }

    @Override
    public void run() {
        if (ColumnHelper.isFromSameTable(Arrays.asList((Column[]) columns))) {
            TdColumn oneColumn = columns[0];
            TdDataProvider dataprovider = DataProviderHelper.getTdDataProvider(oneColumn);
            ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(oneColumn);
            String tableName = ColumnSetNameHelper.getColumnSetQualifiedName(dataprovider, columnSetOwner);
            DbmsLanguage language = DbmsLanguageFactory.createDbmsLanguage(dataprovider);

            String columnClause = ""; //$NON-NLS-1$
            for (TdColumn column : columns) {
                columnClause += language.quote(column.getName()) + ","; //$NON-NLS-1$
            }
            columnClause = columnClause.substring(0, columnClause.length() - 1);

            String query = "select " + columnClause + " from " + tableName; //$NON-NLS-1$ //$NON-NLS-2$
            CorePlugin.getDefault().runInDQViewer(dataprovider, query, tableName);
        } else {
            MessageDialogWithToggle.openWarning(null, DefaultMessagesImpl.getString("PreviewColumnAction.Warning"), DefaultMessagesImpl.getString("PreviewColumnAction.previewColumns")); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
