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
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
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
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EXPLORE_IMAGE));
        this.table = table;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        // MOD scorreia 2008-12-12 do not switch perspective for preview
        // new ChangePerspectiveAction(PluginConstant.SE_ID).run();

        TdDataProvider tdDataProvider = DataProviderHelper.getDataProvider(table);
        String qualifiedName = ColumnSetNameHelper.getColumnSetQualifiedName(tdDataProvider, table);
        String query = "select * from " + qualifiedName; //$NON-NLS-1$
        CorePlugin.getDefault().runInDQViewer(tdDataProvider, query, null);
    }
}
