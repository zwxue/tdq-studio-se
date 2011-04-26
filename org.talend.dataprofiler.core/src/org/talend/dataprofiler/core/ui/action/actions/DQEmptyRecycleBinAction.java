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

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.impl.RecycleBinManager;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.EmptyRecycleBinAction;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DQEmptyRecycleBinAction extends EmptyRecycleBinAction {

    public DQEmptyRecycleBinAction() {
        super();
        setText(super.getText());
        setImageDescriptor(super.getImageDescriptor());
    }

    @Override
    public ISelection getSelection() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        ISelection selection = findView.getCommonViewer().getSelection();
        return selection;
    }

    @Override
    public void run() {
        // if these items in recycle bin are depended by others,show a warning dialog and return.
        boolean hasDependencyItem = false;
        List<IRepositoryNode> children = RecycleBinManager.getInstance().getRecycleBinChildren();
        for (IRepositoryNode obj : children) {
            if (RepositoryNodeHelper.hasDependencyClients(obj)) {
                hasDependencyItem = true;
                break;
            }
        }
        if (hasDependencyItem) {
            DeleteModelElementConfirmDialog.showDialog(null, children,
                    DefaultMessagesImpl.getString("DQEmptyRecycleBinAction.allDependencies"));//$NON-NLS-1$
            return;
        }

        super.run();

        CorePlugin.getDefault().refreshDQView();
        CorePlugin.getDefault().refreshWorkSpace();
    }

    @Override
    /**
     * override the method so as to removing dependencies
     */
    protected void doRun() {
        ISelection selection = getSelection();
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        final RepositoryNode node = (RepositoryNode) obj;

        final String title = DefaultMessagesImpl.getString("DQEmptyRecycleBinAction.dialog.title"); //$NON-NLS-1$
        String message = null;

        if (node.getChildren().size() == 0) {
            return;
        } else if (node.getChildren().size() >= 1) {
            message = DefaultMessagesImpl.getString("DQEmptyRecycleBinAction.dialog.messageAllElements") + "\n" + //$NON-NLS-1$ //$NON-NLS-2$
                    DefaultMessagesImpl.getString("DQEmptyRecycleBinAction.dialog.message1"); //$NON-NLS-1$;
        }
        final Shell shell = super.getShell();
        if (!(MessageDialog.openQuestion(shell, title, message))) {
            return;
        }

        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        for (IRepositoryNode child : node.getChildren()) {
            try {
                if (child.getType() == ENodeType.REPOSITORY_ELEMENT) {
                    // remove client dependcy for supplier, .eg. delete analysis,should remove client dependecy in
                    // connection
                    ModelElement modelEle = RepositoryNodeHelper.getModelElementFromRepositoryNode(child);
                    EObjectHelper.removeDependencys(modelEle);
                }
                // MOD klliu 2010-04-21 bug 20204 remove SQL Exploer node before phisical delete
                Item item = child.getObject().getProperty().getItem();
                if (item instanceof DatabaseConnectionItem) {
                    DatabaseConnection databaseConnection = (DatabaseConnection) ((DatabaseConnectionItem) item).getConnection();
                    CWMPlugin.getDefault().removeAliasInSQLExplorer(databaseConnection);
                }
                deleteElements(factory, (RepositoryNode) child);
            } catch (Exception e) {
                MessageBoxExceptionHandler.process(e);
            }
        }
        try {
            factory.saveProject(ProjectManager.getInstance().getCurrentProject());
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
    }
}
