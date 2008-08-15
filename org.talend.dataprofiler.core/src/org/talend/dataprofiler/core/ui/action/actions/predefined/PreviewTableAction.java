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

import java.util.Collection;

import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sqleditor.actions.ExecSQLAction;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;
import org.talend.utils.sugars.TypedReturnCode;

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
        super("Preview Table");
        this.table = table;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        ChangePerspectiveAction perspectiveAction = new ChangePerspectiveAction(PluginConstant.SE_ID);
        perspectiveAction.run();
        SQLExplorerPlugin default1 = SQLExplorerPlugin.getDefault();
        Collection<Alias> aliases = default1.getAliasManager().getAliases();
        TdDataProvider tdDataProvider = DataProviderHelper.getDataProvider(table);
        TypedReturnCode<TdProviderConnection> tdPc = DataProviderHelper.getTdProviderConnection(tdDataProvider);
        TdProviderConnection providerConnection = tdPc.getObject();
        String url = providerConnection.getConnectionString();
        for (Alias alias : aliases) {
            if (alias.getUrl().equals(url)) {
                String query = "select * from " + table.getName();
                SQLEditorInput input = new SQLEditorInput("SQL Editor (" + SQLExplorerPlugin.getDefault().getEditorSerialNo()
                        + ").sql");
                input.setUser(alias.getDefaultUser());
                try {
                    IWorkbenchPage page = SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
                            .getActivePage();
                    SQLEditor editorPart = (SQLEditor) page.openEditor((IEditorInput) input, SQLEditor.class.getName());
                    editorPart.setText(query);
                    ExecSQLAction execSQLAction = new ExecSQLAction(editorPart);
                    execSQLAction.run();
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
