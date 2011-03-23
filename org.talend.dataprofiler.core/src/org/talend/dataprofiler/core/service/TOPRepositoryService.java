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
package org.talend.dataprofiler.core.service;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewPart;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionItemEditorInput;
import org.talend.dq.CWMPlugin;


/**
 * DOC bZhou  class global comment. Detailled comment
 */
public class TOPRepositoryService implements ITDQRepositoryService {

    public IViewPart getTDQRespositoryView() {
        return CorePlugin.getDefault().getRepositoryView();
    }

    public void notifySQLExplorer(Item... items) {
        if (items == null) {
            return;
        }

        for (Item item : items) {
            if (item instanceof ConnectionItem) {
                Connection connection = ((ConnectionItem) item).getConnection();
                CWMPlugin.getDefault().addConnetionAliasToSQLPlugin(connection);
            }
        }
    }

    public void openEditor(Item item) {

        Class<?> clazz = null;
        IEditorInput editorInput = null;
        if (item instanceof ConnectionItem) {
            clazz = ConnectionEditor.class;
            editorInput = new ConnectionItemEditorInput(item);
        }

        if (editorInput != null && clazz != null) {
            CorePlugin.getDefault().closeEditorIfOpened(item);
            CorePlugin.getDefault().openEditor(editorInput, clazz.getName());
        }
    }

    public void fillMetadata(Connection connection) {
        ConnectionUtils.fillConnectionInformation(connection);
    }

    public void fillMetadata(ConnectionItem connItem) {
        MetadataConnectionUtils.fillConnectionInformation(connItem);
    }

    public void refresh() {
        CorePlugin.getDefault().refreshWorkSpace();
        CorePlugin.getDefault().refreshDQView();
    }
}
