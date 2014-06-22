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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.helper.WorkspaceResourceHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dq.helper.ReportUtils;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.JrxmlTempSubFolderNode;
import org.talend.dq.nodes.ReportSubFolderRepNode;
import org.talend.dq.nodes.SourceFileSubFolderNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.RepositoryNodeUtilities;
import org.talend.repository.ui.actions.AContextualAction;
import org.talend.utils.string.StringUtilities;
import org.talend.utils.sugars.ReturnCode;

/**
 * rename tdq folder action.
 */
public class RenameTdqFolderAction extends AContextualAction {

    protected static Logger log = Logger.getLogger(RenameTdqFolderAction.class);

    private RepositoryNode repositoryNode;

    /**
     * @param node a folder
     */
    public RenameTdqFolderAction(RepositoryNode node) {
        this.repositoryNode = node;
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
        if (this.repositoryNode instanceof JrxmlTempSubFolderNode) {
            MessageUI.openWarning(DefaultMessagesImpl.getString("JrxmlFileAction.forbiddenOperation")); //$NON-NLS-1$
            return;
        }
        // ~ TDQ-4831
        // Added yyin 20120712 TDQ-5721 when rename the sql file folder with file opening, should inform
        if (this.repositoryNode instanceof SourceFileSubFolderNode) {
            ReturnCode rc = WorkspaceResourceHelper.checkSourceFileSubFolderNodeOpening((SourceFileSubFolderNode) repositoryNode);
            if (rc.isOk()) {
                WorkspaceResourceHelper.showSourceFilesOpeningWarnMessages(rc.getMessage());
                return;
            }
        }// ~

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
                List<IRepositoryNode> openRepNodes = getOpenRepNodeForReName(this.repositoryNode, true);
                RepNodeUtils.closeModelElementEditor(openRepNodes, true);

                // deal with ReportSubFolderRepNode
                boolean isReportSubFolderRepNode = this.repositoryNode instanceof ReportSubFolderRepNode;
                IFolder folder = null; // source folder
                File tarFile = null; // temp folder
                if (isReportSubFolderRepNode) {
                    String tempFolderName = StringUtilities.getRandomString(8);
                    folder = RepositoryNodeHelper.getIFolder(this.repositoryNode);
                    File srcFile = WorkspaceUtils.ifolderToFile(folder);
                    tarFile = WorkspaceUtils.ifolderToFile(folder.getParent().getFolder(new Path(tempFolderName)));
                    if (!tarFile.exists()) {
                        tarFile.mkdirs();
                    }
                    if (srcFile.exists() && tarFile.exists()) {
                        FilesUtils.copyDirectory(srcFile, tarFile);
                    }
                }

                // MOD sizhaoliu TDQ-5613 When rename a folder containing a locked analysis, the analysis disappears
                // RepositoryNodeDorpAdapterAssistant dndAsistant = new RepositoryNodeDorpAdapterAssistant();
                // dndAsistant.renameFolderRepNode(node, value2);
                // MOD qiongli TDQ-6237. Using relative path; replace 'getObjectType()' with 'getContentType()'.
                IPath path = RepositoryNodeUtilities.getPath(repositoryNode);
                ProxyRepositoryFactory.getInstance().renameFolder(this.repositoryNode.getContentType(), path, value2);
                // ~ TDQ-5613

                if (isReportSubFolderRepNode && folder != null && tarFile != null) {
                    ReportUtils.copyAndUpdateRepGenDocFileInfo(folder.getParent().getFolder(new Path(value2)), tarFile,
                            folder.getName());
                }

                // refresh the dq repository view
                if (this.repositoryNode != null && this.repositoryNode.getParent() != null) {
                    CorePlugin.getDefault().refreshDQView(this.repositoryNode.getParent());
                }
            } catch (PersistenceException e) {
                log.error(e.getMessage());
                MessageDialog
                        .openError(
                                PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                DefaultMessagesImpl.getString("RepositoyNodeDropAdapterAssistant.error.renameError"), DefaultMessagesImpl.getString("RepositoyNodeDropAdapterAssistant.error.renameFolderLocked")); //$NON-NLS-1$ 
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
    }
}
