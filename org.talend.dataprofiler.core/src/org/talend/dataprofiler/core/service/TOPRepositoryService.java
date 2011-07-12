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

import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.PartListener;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionItemEditorInput;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
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

    /**
     * Fill MDM connection only.
     */
    public void fillMetadata(ConnectionItem connItem) {
        MetadataConnectionUtils.fillConnectionInformation(connItem);
        // MOD gdbu 2011-7-12 bug : 22598
        MDMConnection mdmConnection = (MDMConnection) connItem.getConnection();
        mdmConnection.setLabel(connItem.getProperty().getLabel() + "");
        mdmConnection.setName(connItem.getProperty().getLabel() + "");
        ConnectionUtils.fillMdmConnectionInformation(mdmConnection);
        ElementWriterFactory.getInstance().createDataProviderWriter().save(mdmConnection);
        // ~22598
    }

    public void refresh() {
        CorePlugin.getDefault().refreshWorkSpace();
        CorePlugin.getDefault().refreshDQView();
    }

    public void initProxyRepository() {
        CorePlugin.getDefault().initProxyRepository();
    }

    public void addPartListener() {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        // Calculate the extensions to register partListener.
        IPartListener listener = PartListener.getPartListener();
        if (listener != null) {
            activePage.addPartListener(listener);
        }
    }

    public boolean removeAliasInSQLExplorer(IRepositoryNode children) {
        boolean hasDependencyItem = true;
        //MOD klliu 2011-04-28 bug 20204 removing connection is synced to the connection view of SQL explore 
        Item item = children.getObject().getProperty().getItem();
        // MOD mzhao filter the connections which is not a type of database.
        if (item != null && item instanceof ConnectionItem
                && ((ConnectionItem) item).getConnection() instanceof DatabaseConnection) {
            Connection connection = ((ConnectionItem) item).getConnection();
            List<ModelElement> dependencyClients = EObjectHelper.getDependencyClients(connection);
            if (!(dependencyClients == null || dependencyClients.isEmpty())) {
                hasDependencyItem = false;
            } else {
                CWMPlugin.getDefault().removeAliasInSQLExplorer(connection);
            }
        }

        return hasDependencyItem;
    }
}
