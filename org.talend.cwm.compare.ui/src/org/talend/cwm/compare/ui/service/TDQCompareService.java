package org.talend.cwm.compare.ui.service;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.talend.core.ITDQCompareService;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.cwm.compare.ui.actions.ReloadDatabaseAction;
import org.talend.cwm.compare.ui.actions.provider.ReloadDatabaseProvider;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

public class TDQCompareService implements ITDQCompareService {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ITDQCompareService#reloadDatabase(org.talend.core.model.properties.ConnectionItem)
     */
    /**
     * 
     * Comment method "reloadDatabase".
     * 
     * @param connectionItem
     * 
     */
    public ReturnCode reloadDatabase(ConnectionItem connectionItem) {
        ReturnCode retCode = new ReturnCode(Boolean.TRUE);
        Connection conn = connectionItem.getConnection();
        if (conn instanceof DatabaseConnection) {
            List<ModelElement> dependencyClients = EObjectHelper.getDependencyClients(conn);
            if (!(dependencyClients == null || dependencyClients.isEmpty())) {
                int isOk = DeleteModelElementConfirmDialog.showElementImpactConfirmDialog(null, new ModelElement[] { conn },
                        DefaultMessagesImpl.getString("TOPRepositoryService.dependcyTile"), //$NON-NLS-1$
                        DefaultMessagesImpl.getString("TOPRepositoryService.dependcyMessage", conn.getLabel())); //$NON-NLS-1$
                if (isOk != Dialog.OK) {
                    retCode.setOk(Boolean.FALSE);
                    retCode.setMessage("The user canceled the operation!"); //$NON-NLS-1$
                    return retCode;
                }
            }

            RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(conn);
            ReloadDatabaseAction reloadDatabaseAction = new ReloadDatabaseAction(recursiveFind,
                    ReloadDatabaseProvider.RELOADDATABASE_MENUTEXT);
            reloadDatabaseAction.run();
            retCode = reloadDatabaseAction.getReturnCode();

        }
        return retCode;
    }

}
