// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.nodes.ConnectionRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DFConnectionRepNode;
import org.talend.dq.nodes.MDMConnectionRepNode;
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
     * MOD yyin 20120917 TDQ-5362, do NOT reload DB when switching context.
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        // MOD msjian 2012-2-13 TDQ-4559: make it support file/mdm connection
        if (selectedObject instanceof ConnectionRepNode) {
            ConnectionRepNode conRepNode = (ConnectionRepNode) selectedObject;
            Item item = conRepNode.getObject().getProperty().getItem();
            if (item instanceof ConnectionItem) {
                ConnectionItem connItem = (ConnectionItem) item;
                boolean isUpdated = SwitchContextGroupNameImpl.getInstance().updateContextGroup(connItem);

                // get contextid and file name
                String fileStr = PluginConstant.EMPTY_STRING;
                Connection dbcon = null;
                if (conRepNode instanceof DBConnectionRepNode) {
                    dbcon = ((DBConnectionRepNode) conRepNode).getDatabaseConnection();
                } else if (conRepNode instanceof DFConnectionRepNode) {
                    dbcon = ((DFConnectionRepNode) conRepNode).getDfConnection();
                } else if (conRepNode instanceof MDMConnectionRepNode) {
                    dbcon = ((MDMConnectionRepNode) conRepNode).getMdmConnection();
                }
                if (dbcon != null) {
                    fileStr = dbcon.eResource().getURI().toFileString();
                }

                if (isUpdated) {
                    if (log.isDebugEnabled()) {
                        log.debug(DefaultMessagesImpl.getString("SwitchContextAction.saveMessage", fileStr, "successful"));//$NON-NLS-1$ //$NON-NLS-2$
                    }
                } else {
                    log.error(DefaultMessagesImpl.getString("SwitchContextAction.saveMessage", fileStr, "failed"));//$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
     // TDQ-4559~
    }


}
