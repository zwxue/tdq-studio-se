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
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;
import org.talend.dq.helper.ColumnSetNameHelper;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class PreviewTableAction extends Action {

    private TdTable table;

    /**
     * DOC qzhang PreviewTableAction constructor comment.
     * 
     * @param table
     */
    public PreviewTableAction(TdTable table) {
        super(DefaultMessagesImpl.getString("PreviewTableAction.previewTable")); //$NON-NLS-1$
        this.table = table;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        new ChangePerspectiveAction(PluginConstant.SE_ID).run();

        TdDataProvider tdDataProvider = DataProviderHelper.getDataProvider(table);
        String qualifiedName = ColumnSetNameHelper.getColumnSetQualifiedName(tdDataProvider, table);
        String query = "select * from " + qualifiedName; //$NON-NLS-1$
        CorePlugin.getDefault().runInDQViewer(tdDataProvider, query, null);
    }
}
