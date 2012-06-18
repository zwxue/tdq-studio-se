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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dq.nodes.JrxmlTempSubFolderNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.AContextualAction;

/**
 * rename tdq folder action.
 */
public class RenameTdqFolderAction extends AContextualAction {

    protected static Logger log = Logger.getLogger(RenameTdqFolderAction.class);

    private IFolder obj;

    private RepositoryNode node;

    /**
     * @param node a folder
     */
    public RenameTdqFolderAction(RepositoryNode node) {
        this.node = node;
        this.obj = WorkbenchUtils.getFolder(node);
        setText(DefaultMessagesImpl.getString("RenameTdqFolderAction.renameFolder")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.AContextualAction#doRun()
     */
    @Override
    protected void doRun() {
        // ADD xqliu 2012-05-24 TDQ-4831
        if (this.node instanceof JrxmlTempSubFolderNode) {
            MessageUI.openWarning(DefaultMessagesImpl.getString("JrxmlFileAction.forbiddenOperation")); //$NON-NLS-1$
            return;
        }
        // ~ TDQ-4831
        InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(),
                DefaultMessagesImpl.getString("RenameTdqFolderAction.renameFolderName"), //$NON-NLS-1$
                DefaultMessagesImpl.getString("RenameTdqFolderAction.inputNewFolderName"), null, new IInputValidator() { //$NON-NLS-1$

                    public String isValid(String newText) {
                        return null;
                    }

                });
        if (dialog.open() == InputDialog.OK) {
            String value2 = dialog.getValue();
            try {
                // close opend editors
                List<IRepositoryNode> openRepNodes = getOpenRepNodeForReName(node, true);
                RepNodeUtils.closeModelElementEditor(openRepNodes, true);

                // MOD sizhaoliu TDQ-5613 When rename a folder containing a locked analysis, the analysis disappears
                // RepositoryNodeDorpAdapterAssistant dndAsistant = new RepositoryNodeDorpAdapterAssistant();
                // dndAsistant.renameFolderRepNode(node, value2);
                IPath path = WorkbenchUtils.getPath(node);
                ProxyRepositoryFactory.getInstance().renameFolder(node.getObjectType(), path, value2);
                // ~ TDQ-5613

                // refresh the dq repository view
                if (node != null && node.getParent() != null) {
                    CorePlugin.getDefault().refreshDQView(node.getParent());
                }
            } catch (PersistenceException e) {
                log.error(e, e);
            }
        }
    }

    private List<IRepositoryNode> getOpenRepNodeForReName(IRepositoryNode parentNode, boolean recursive) {
        List<IRepositoryNode> result = new ArrayList<IRepositoryNode>();
        List<IRepositoryNode> children = parentNode.getChildren();

        for (IRepositoryNode node : children) {
            ENodeType type = node.getType();
            if (type.equals(ENodeType.SIMPLE_FOLDER)) {
                if (recursive) {
                    result.addAll(getOpenRepNodeForReName(node, recursive));
                }
            } else {
                result.add(node);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.commons.ui.swt.actions.ITreeContextualAction#init(org.eclipse.jface.viewers.TreeViewer,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        // TODO Auto-generated method stub

    }
}
