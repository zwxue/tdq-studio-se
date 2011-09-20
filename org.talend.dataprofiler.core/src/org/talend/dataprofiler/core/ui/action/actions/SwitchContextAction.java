// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.model.context.ContextUtils;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.repository.ui.utils.SwitchContextGroupNameImpl;
/**
 * DOC msjian class global comment. Detailled comment
 */
public class SwitchContextAction extends Action {

    private static Logger log = Logger.getLogger(SwitchContextAction.class);

    private Object selectedObject;

    public SwitchContextAction(Object selectedNode, String menuText) {
        super(menuText);
        this.selectedObject = selectedNode;
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.SWITCH_IMAGE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        if (selectedObject instanceof DBConnectionRepNode) {
            DBConnectionRepNode dbConnectionRepNode = (DBConnectionRepNode) selectedObject;
            Item item = dbConnectionRepNode.getObject().getProperty().getItem();
            if (item instanceof ConnectionItem) {
                ConnectionItem connItem = (ConnectionItem) item;
                boolean isUpdated = SwitchContextGroupNameImpl.getInstance().updateContextGroup(connItem);
                DatabaseConnection dbcon = dbConnectionRepNode.getDatabaseConnection();
                String fileStr = dbcon.eResource().getURI().toFileString();
                if (isUpdated) {
                    if (log.isDebugEnabled()) {
                        log.debug(DefaultMessagesImpl.getString("SwitchContextAction.saveMessage", fileStr, "successful"));//$NON-NLS-1$ //$NON-NLS-2$
                    }

                    ITDQRepositoryService tdqRepService = null;
                    if (GlobalServiceRegister.getDefault().isServiceRegistered(ITDQRepositoryService.class)) {
                        tdqRepService = (ITDQRepositoryService) GlobalServiceRegister.getDefault().getService(
                                ITDQRepositoryService.class);
                    }
                    if (tdqRepService != null) {
                        String contextId = dbConnectionRepNode.getDatabaseConnection().getContextId();
                        tdqRepService.reloadDatabase(ContextUtils.getContextItemById2(contextId));
                    }
                } else {
                    log.error(DefaultMessagesImpl.getString("SwitchContextAction.saveMessage", fileStr, "failed"));//$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }
}
