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
package org.talend.dataprofiler.core.ui.views.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.jfree.util.Log;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.FolderHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.helper.WorkspaceResourceHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataprofiler.core.ui.views.provider.RepositoryNodeBuilder;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.helpers.ReportHelper.ReportType;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.helper.ReportUtils;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.AnalysisSubFolderRepNode;
import org.talend.dq.nodes.ConnectionRepNode;
import org.talend.dq.nodes.DBConnectionFolderRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DFConnectionFolderRepNode;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.dq.nodes.JrxmlTempSubFolderNode;
import org.talend.dq.nodes.JrxmlTempleteRepNode;
import org.talend.dq.nodes.MDMConnectionFolderRepNode;
import org.talend.dq.nodes.MDMConnectionSubFolderRepNode;
import org.talend.dq.nodes.PatternRepNode;
import org.talend.dq.nodes.ReportAnalysisRepNode;
import org.talend.dq.nodes.ReportFileRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.ReportSubFolderRepNode;
import org.talend.dq.nodes.SourceFileRepNode;
import org.talend.dq.nodes.SourceFileSubFolderNode;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.utils.string.StringUtilities;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC mzhao Handle drop event of repositoryNode on DQ repository viewer.
 */
public class RepositoryNodeDorpAdapterAssistant extends CommonDropAdapterAssistant {

    protected static Logger log = Logger.getLogger(RepositoryNodeDorpAdapterAssistant.class);

    private static final IRepositoryNode[] NO_RESOURCES = new IRepositoryNode[0];

    private IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    public RepositoryNodeDorpAdapterAssistant() {
    }

    @Override
    public IStatus validateDrop(Object target, int operation, TransferData transferType) {
        if (!(target instanceof IRepositoryNode)) {
            return Status.CANCEL_STATUS;
        }
        IRepositoryNode targetNode = (IRepositoryNode) target;
        for (IRepositoryNode res : getSelectedRepositoryNodes()) {
            switch (targetNode.getType()) {
            case SYSTEM_FOLDER:
            case SIMPLE_FOLDER:
                if (allowDND(res, targetNode)) {
                    return Status.OK_STATUS;
                }
                break;
            case REPOSITORY_ELEMENT:
                if (res instanceof AnalysisRepNode && targetNode instanceof ReportRepNode) {
                    return Status.OK_STATUS;
                }
                break;

            default:
                break;
            }
        }
        return Status.CANCEL_STATUS;
    }

    private boolean allowDND(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        // MOD klliu Bug TDQ-4330 if targetCount's lenth is 1,that means targetNode is the root and system node.
        // so there is not any operations on it,then the operation of DND is not allowed.
        IPath sourcePath = WorkbenchUtils.getPath(sourceNode);
        IPath targetPath = WorkbenchUtils.getPath(targetNode);
        int sourceCount = sourcePath.segmentCount();
        int targetCount = targetPath.segmentCount();
        if (sourceCount == 1 || targetCount == 1) {
            return false;
        }
        // MOD qiongli 2012-4-24 TDQ-5127
        if (isForbidNode(sourceNode)) {
            return false;
        }
        // MOD klliu Bug TDQ-4444 2012-01-09
        // This need check the object type of soruce node is sub type of targetNode's
        // if it is,that is not allowed to drop.
        if (isSubObjectType(sourceNode, targetNode)) {
            return false;
        }
        // ~

        // ADD msjian 2012-2-29 TDQ-4545: when the node editor is open, make it can not move.
        IRepositoryViewObject object = sourceNode.getObject();
        if (object != null) {
            // when it is locked, can not move.
            if (object.getProperty().getItem().getState().isLocked()) {
                return false;
            }
        }
        // TDQ-4545 ~

        return true;
    }

    @Override
    public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget) {
        IRepositoryNode targetNode = null;
        try {
            IRepositoryNode[] selectedRepositoryNodes = null;

            targetNode = (IRepositoryNode) aTarget;
            if (aDropAdapter.getCurrentTarget() == null || aDropTargetEvent.data == null) {
                return Status.CANCEL_STATUS;
            }
            TransferData currentTransfer = aDropAdapter.getCurrentTransfer();
            if (LocalSelectionTransfer.getTransfer().isSupportedType(currentTransfer)) {
                selectedRepositoryNodes = getSelectedRepositoryNodes();
            }
            moveRepositoryNodes(selectedRepositoryNodes, targetNode);

            // MOD gdbu 2011-11-17 TDQ-3969 : after move folder or items re-filter the tree , to create a new list .
            if (DQRepositoryNode.isOnFilterring()) {
                RepositoryNodeHelper.fillTreeList(null);
                RepositoryNodeHelper.setFilteredNode(RepositoryNodeHelper.getRootNode(ERepositoryObjectType.TDQ_DATA_PROFILING,
                        true));
            }

        } catch (PersistenceException e) {
            if (log.isInfoEnabled()) {
                // e.printStackTrace();
                log.info(e.toString());
            }
            MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                    DefaultMessagesImpl.getString("RepositoyNodeDropAdapterAssistant.error.moveError"), e.getMessage()); //$NON-NLS-1$

        }
        // MOD gdbu TDQ-3546 unload resource after move item.
        ProxyRepositoryManager.getInstance().refresh();
        return Status.OK_STATUS;
    }

    private IRepositoryNode[] getSelectedRepositoryNodes() {
        ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
        if (selection instanceof IStructuredSelection) {
            return getSelectedRepositoryNodes((IStructuredSelection) selection);
        }
        return NO_RESOURCES;
    }

    private IRepositoryNode[] getSelectedRepositoryNodes(IStructuredSelection selection) {
        ArrayList<IRepositoryNode> selectedRepositoryNodes = new ArrayList<IRepositoryNode>();
        List list = selection.toList();
        for (Object o : list) {
            if (o instanceof IRepositoryNode) {
                selectedRepositoryNodes.add((IRepositoryNode) o);
            }
        }
        return selectedRepositoryNodes.toArray(new IRepositoryNode[selectedRepositoryNodes.size()]);
    }

    /**
     * move RepositoryNode to the target RepositoryNode.
     * 
     * @param repositoryNodes
     * @param targetNode
     * @throws PersistenceException
     */
    public void moveRepositoryNodes(IRepositoryNode[] repositoryNodes, IRepositoryNode targetNode) throws PersistenceException {
        if (repositoryNodes != null) {
            for (IRepositoryNode sourceNode : repositoryNodes) {
                if (targetNode == sourceNode.getParent()) {
                    continue;
                }
                if (isSameType(sourceNode, targetNode)) {
                    if (sourceNode.getType() == ENodeType.REPOSITORY_ELEMENT) {
                        if (sourceNode instanceof AnalysisRepNode) {
                            moveAnalysisRepNode(sourceNode, targetNode);
                        } else if (sourceNode instanceof ReportRepNode) {
                            moveReportRepNode(sourceNode, targetNode);
                        } else if (sourceNode instanceof DBConnectionRepNode) {
                            moveConnectionRepNode(sourceNode, targetNode);
                        } else if (sourceNode instanceof SysIndicatorDefinitionRepNode) {
                            // SystemIndicatorDefinition can't be moved
                            if (!((SysIndicatorDefinitionRepNode) sourceNode).isSystemIndicator()) {
                                moveUDIRepNode(sourceNode, targetNode);
                            }
                        } else if (sourceNode instanceof PatternRepNode) {
                            movePatternRepNode(sourceNode, targetNode);
                        } else if (sourceNode instanceof JrxmlTempleteRepNode) {
                            moveJrxmlFileRepNode(sourceNode, targetNode);
                        } else if (sourceNode instanceof SourceFileRepNode) {
                            moveSourceFileRepNode(sourceNode, targetNode);
                        } else {
                            moveCommonRepNode(sourceNode, targetNode);
                        }
                    } else if (sourceNode.getType() == ENodeType.SIMPLE_FOLDER) {
                        moveFolderRepNode(sourceNode, targetNode);
                    }
                    closeEditorIfOpened(sourceNode);
                    // MOD qiongli 2012-4-23,only refresh the parent of source node at here.
                    CorePlugin.getDefault().refreshDQView(sourceNode.getParent());
                }
            }
        }
    }

    /**
     * move Jrxml File.
     * 
     * @param sourceNode
     * @param targetNode
     * @throws PersistenceException
     */
    public void moveJrxmlFileRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        MessageUI.openWarning(DefaultMessagesImpl.getString("JrxmlFileAction.forbiddenOperation")); //$NON-NLS-1$
    }

    /**
     * move Source File(close the Source File editor when it's open).
     * 
     * @param sourceNode
     * @param targetNode
     * @throws PersistenceException
     */
    public void moveSourceFileRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        if (WorkspaceResourceHelper.sourceFileHasBeenOpened(sourceNode)) {
            MessageUI.openWarning(DefaultMessagesImpl.getString("SourceFileAction.sourceFileOpening", sourceNode.getLabel())); //$NON-NLS-1$
        } else {
            // move the source file item
            IRepositoryViewObject objectToMove = sourceNode.getObject();
            IPath fullPath = ResourceManager.getSourceFileFolder().getFullPath();
            IPath makeRelativeTo = fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
            ENodeType type = targetNode.getType();
            if (ENodeType.SIMPLE_FOLDER == type || ENodeType.SYSTEM_FOLDER == type) {
                moveObject(objectToMove, sourceNode, targetNode, makeRelativeTo);
            }
            CorePlugin.getDefault().refreshDQView(targetNode.getParent());
        }
    }

    private void moveAnalysisRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        IPath fullPath = ResourceManager.getAnalysisFolder().getFullPath();
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode,
                    fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, Path.EMPTY);
        }
        // refresh the dq repository tree view
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
    }

    /**
     * remove the report node (remove the report generate doc folder also).
     * 
     * @param sourceNode
     * @param targetNode
     * @throws PersistenceException
     */
    private void moveReportRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        // MOD yyi 2012-02-22:TDQ-4545 Update user define jrxml template path.
        relocateJrxmlTemplates(sourceNode, targetNode);

        // get the original source node and folder
        IFolder outputFolder = ReportUtils.getOutputFolder((ReportRepNode) sourceNode);
        File sourceFile = WorkspaceUtils.ifolderToFile(outputFolder);

        // move the ReportRepNode
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        IPath fullPath = ResourceManager.getReportsFolder().getFullPath();
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode,
                    fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, Path.EMPTY);
        }

        // get the target node and folder
        IFolder targetFolder = RepositoryNodeHelper.getIFolder(targetNode);
        if (targetFolder != null) {
            File targetFile = WorkspaceUtils.ifolderToFile(targetFolder);

            // move the report generate doc folder
            if (sourceFile.exists()) {
                if (targetFile.exists()) {
                    FilesUtils.copyDirectory(sourceFile, targetFile);
                }
                FilesUtils.deleteFile(sourceFile, true);
            }

            // update the file .report.list
            ReportUtils.updateReportListFile(outputFolder, targetFolder);

            // refresh the dq repository tree view
            CorePlugin.getDefault().refreshDQView(targetNode.getParent());
        }
    }

    /**
     * Update user define jrxml template path.
     * 
     * @param sourceNode
     * @param targetNode
     * @link {@link #moveReportRepNode(IRepositoryNode, IRepositoryNode)}
     */
    private void relocateJrxmlTemplates(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        IPath targetPath = WorkbenchUtils.getPath(targetNode);
        IFolder targetFolder = ResourceManager.getRootProject().getFolder(targetPath);

        TdReport rep = (TdReport) RepositoryNodeHelper.getModelElementFromRepositoryNode(sourceNode);
        IPath jrxmlPath = null;
        IPath relativePath = null;
        ReportType reportType = null;
        for (AnalysisMap anaMap : rep.getAnalysisMap()) {
            reportType = ReportHelper.ReportType.getReportType(anaMap.getAnalysis(), anaMap.getReportType());
            // Relocate the jrxml template path for user define type.
            if (ReportHelper.ReportType.USER_MADE.equals(reportType)) {
                jrxmlPath = RepResourceFileHelper.findCorrespondingFile(rep).getParent().getLocation()
                        .append(anaMap.getJrxmlSource());
                relativePath = jrxmlPath.makeRelativeTo(targetFolder.getLocation().append("/")); //$NON-NLS-1$
                anaMap.setJrxmlSource(relativePath.toString());
            }
        }
    }

    private void movePatternRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        ERepositoryObjectType contentType = sourceNode.getContentType();
        IPath fullPath = this.getNodeFullPath(contentType);
        IPath makeRelativeTo = fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        IPath removeLastSegments = makeRelativeTo.removeLastSegments(1);
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        }
        // refresh the dq repository tree view
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
    }

    private void moveConnectionRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        IRepositoryViewObject objectToMove = sourceNode.getObject();

        IPath fullPath = Path.EMPTY;
        if (targetNode.getParent() instanceof DBConnectionFolderRepNode) {
            fullPath = ResourceManager.getConnectionFolder().getFullPath();
        } else if (targetNode.getParent() instanceof DFConnectionFolderRepNode) {
            fullPath = ResourceManager.getFileDelimitedFolder().getFullPath();
        } else if (targetNode.getParent() instanceof MDMConnectionFolderRepNode
                || targetNode.getParent() instanceof MDMConnectionSubFolderRepNode) {
            fullPath = ResourceManager.getMDMConnectionFolder().getFullPath();
        }

        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode,
                    fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, Path.EMPTY);
        }
        // refresh the dq repository tree view
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
    }

    private void moveUDIRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        ERepositoryObjectType contentType = sourceNode.getContentType();
        IPath fullPath = this.getNodeFullPath(contentType);
        IPath makeRelativeTo = fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        IPath removeLastSegments = makeRelativeTo.removeLastSegments(1);
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        }
        // refresh the dq repository tree view
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
    }

    private void moveCommonRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        ERepositoryObjectType targetObjectType = targetNode.getContentType();
        IPath fullPath = getNodeFullPath(targetObjectType);
        IPath makeRelativeTo = fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        IPath removeLastSegments = makeRelativeTo.removeLastSegments(1);
        if (ERepositoryObjectType.TDQ_RULES_SQL.equals(targetObjectType)
                || ERepositoryObjectType.TDQ_RULES_PARSER.equals(targetObjectType)
                || ERepositoryObjectType.METADATA_FILE_DELIMITED.equals(targetObjectType)
                || ERepositoryObjectType.METADATA_MDMCONNECTION.equals(targetObjectType)) {
            removeLastSegments = makeRelativeTo;
        }
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        }
        // refresh the dq repository tree view
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
    }

    /**
     * MOD bu gdbu 2011-4-2 bug : 19537
     * 
     * Add the restrictions when Move the folder.
     * 
     * @param sourceNode
     * @param targetNode
     * @return
     */
    private boolean canMoveNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        String sourceNodeRelPath = RepositoryNodeHelper.getPath(sourceNode).toString().trim();
        String targetNodeRelPath = RepositoryNodeHelper.getPath(targetNode).toString().trim();
        // MOD gdbu 2011-4-25 bug : 19537
        if (sourceNode.getParent().getId().equals(targetNode.getId())) {
            return false;
        }
        // ~19537
        if (sourceNodeRelPath.length() > targetNodeRelPath.length()) {
            // Move a child node to parent node or child node to move to other branches, allowing moving.
            return true;
        }
        if (sourceNodeRelPath.equals(targetNodeRelPath.substring(0, sourceNodeRelPath.length()))) {
            // Move a child node to parent node is not allowed.
            return false;
        }
        return true;
    }

    public void moveFolderRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        // ADD xqliu 2012-05-24 TDQ-4831
        if (sourceNode instanceof JrxmlTempSubFolderNode) {
            MessageUI.openWarning(DefaultMessagesImpl.getString("JrxmlFileAction.forbiddenOperation")); //$NON-NLS-1$
            return;
        }
        if (sourceNode instanceof SourceFileSubFolderNode) {
            SourceFileSubFolderNode folderNode = (SourceFileSubFolderNode) sourceNode;
            ReturnCode rc = WorkspaceResourceHelper.checkSourceFileSubFolderNodeOpening(folderNode);
            if (rc.isOk()) {
                WorkspaceResourceHelper.showSourceFilesOpeningWarnMessages(rc.getMessage());
                return;
            }
        }
        // ~ TDQ-4831
        // MOD bu gdbu 2011-4-2 bug : 19537
        if (!canMoveNode(sourceNode, targetNode)) {
            // Doesn't allow the parent node moved to the child node
            return;
        }
        // ~19537

        // deal with ReportSubFolderRepNode
        boolean isReportSubFolderRepNode = sourceNode instanceof ReportSubFolderRepNode;
        Map<IFolder, IFolder> reportGenDocInfoMap = new HashMap<IFolder, IFolder>();
        IFolder folder = null; // source folder
        File tarFile = null; // temp folder
        if (isReportSubFolderRepNode) {
            String tempFolderName = StringUtilities.getRandomString(8);
            folder = RepositoryNodeHelper.getIFolder(sourceNode);
            File srcFile = WorkspaceUtils.ifolderToFile(folder);
            tarFile = WorkspaceUtils.ifolderToFile(folder.getParent().getFolder(new Path(tempFolderName)));
            if (!tarFile.exists()) {
                tarFile.mkdirs();
            }
            if (srcFile.exists() && tarFile.exists()) {
                FilesUtils.copyDirectory(srcFile, tarFile);
            }

            // get the source folder
            IFolder sourceFolder = RepositoryNodeHelper.getIFolder(sourceNode);
            // get the target folder
            IFolder targetFolder = RepositoryNodeHelper.getIFolder(targetNode);

            List<ReportRepNode> reportRepNodeList = new ArrayList<ReportRepNode>();
            getAllReportRepNode((ReportSubFolderRepNode) sourceNode, reportRepNodeList);
            if (!reportRepNodeList.isEmpty()) {
                buildReportGenDocInfoMap(reportRepNodeList, sourceFolder, targetFolder, reportGenDocInfoMap);
            }
        }

        RepositoryNodeBuilder instance = RepositoryNodeBuilder.getInstance();
        FolderHelper folderHelper = instance.getFolderHelper();
        IPath sourcePath = WorkbenchUtils.getPath(sourceNode);
        IPath targetPath = WorkbenchUtils.getPath(targetNode);
        ERepositoryObjectType objectType = targetNode.getContentType();
        IPath nodeFullPath = this.getNodeFullPath(objectType);
        IPath makeRelativeTo = nodeFullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        computePath(folderHelper, sourcePath, targetPath, makeRelativeTo, objectType, sourceNode, targetNode);

        if (isReportSubFolderRepNode) {
            if (tarFile != null) {
                File file1 = new File(tarFile.getAbsolutePath() + IPath.SEPARATOR + sourceNode.getLabel());
                File file2 = WorkspaceUtils.ifolderToFile(RepositoryNodeHelper.getIFolder(targetNode).getFolder(
                        new Path(sourceNode.getLabel())));

                ReportUtils.moveHiddenFolders(file1, file2);
                // delete temp folder
                FilesUtils.deleteFile(tarFile, Boolean.TRUE);
            }
            if (!reportGenDocInfoMap.isEmpty()) {
                for (IFolder outputFolder : reportGenDocInfoMap.keySet()) {
                    ReportUtils.updateReportListFile(outputFolder, reportGenDocInfoMap.get(outputFolder));
                }
            }
        }
    }

    /**
     * build the map which contain the report generated doc folder's update information.
     * 
     * @param reportRepNodeList
     * @param sourceFolder
     * @param targetFolder
     * @param reportGenDocInfoMap
     */
    private void buildReportGenDocInfoMap(List<ReportRepNode> reportRepNodeList, IFolder sourceFolder, IFolder targetFolder,
            Map<IFolder, IFolder> reportGenDocInfoMap) {
        for (ReportRepNode repNode : reportRepNodeList) {
            IFolder outputFolder = ReportUtils.getOutputFolder(RepositoryNodeHelper.getIFile(repNode));
            IPath relativePath = outputFolder.getFullPath().removeLastSegments(1)
                    .makeRelativeTo(sourceFolder.getParent().getFullPath());
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IFolder valueFolder = root.getFolder(targetFolder.getFullPath().append(relativePath));
            reportGenDocInfoMap.put(outputFolder, valueFolder);
        }
    }

    /**
     * get all ReportRepNode under the folder.
     * 
     * @param sourceNode a ReportSubFolderRepNode
     * @param reportRepNodeList the ReportRepNode list
     */
    private void getAllReportRepNode(ReportSubFolderRepNode sourceNode, List<ReportRepNode> reportRepNodeList) {
        List<IRepositoryNode> children = sourceNode.getChildren();
        for (IRepositoryNode node : children) {
            if (node instanceof ReportRepNode) {
                reportRepNodeList.add((ReportRepNode) node);
            } else if (node instanceof ReportSubFolderRepNode) {
                getAllReportRepNode((ReportSubFolderRepNode) node, reportRepNodeList);
            }
        }
    }

    /**
     * rename the RepositoryNode's folder name (the RepositoryNode must be a folder).
     * 
     * @param folderNode
     * @param label
     * @throws PersistenceException
     * @deprecated use ProxyRepositoryFactory.getInstance().renameFolder() instead
     */
    @Deprecated
    public void renameFolderRepNode(IRepositoryNode folderNode, String label) throws PersistenceException {
        if (folderNode == null || label == null || "".equals(label)) { //$NON-NLS-1$
            return;
        }
        IRepositoryViewObject object = folderNode.getObject();
        if (object == null || !(object instanceof Folder)) {
            return;
        } else {
            if (WorkbenchUtils.equalsOS(label, ((Folder) object).getLabel())) {
                // the new lable is same with old, so do nothing
                return;
            }
        }

        String path = null;
        IRepositoryNode parentNode = folderNode.getParent();
        ERepositoryObjectType objectType = folderNode.getContentType();
        try {
            path = WorkbenchUtils.getPath(folderNode).makeRelativeTo(new Path(ERepositoryObjectType.getFolderName(objectType)))
                    .removeLastSegments(1).toString();
        } catch (Exception e) {
            log.warn(e, e);
        }
        path = path == null ? "" : path; //$NON-NLS-1$
        path = path.startsWith(String.valueOf(IPath.SEPARATOR)) ? path : IPath.SEPARATOR + path;

        // create target folder
        Folder targetFoler = ProxyRepositoryFactory.getInstance()
                .createFolder(folderNode.getContentType(), new Path(path), label);
        RepositoryNode targetNode = new RepositoryNode(targetFoler, folderNode.getParent(), ENodeType.SIMPLE_FOLDER);
        targetNode.setParent(folderNode.getParent());

        // refresh the dq view (if the rename folder havs sub folders, must to refresh before move these sub folders)
        CorePlugin.getDefault().refreshDQView(parentNode);

        IPath sourcePath = WorkbenchUtils.getPath(folderNode);

        // move children from source folder to target folder
        List<IRepositoryNode> children = folderNode.getChildren();
        if (children != null) {
            IRepositoryNode[] array = children.toArray(new IRepositoryNode[children.size()]);
            for (IRepositoryNode inode : array) {
                if (inode != null) {
                    if (inode instanceof ConnectionRepNode) {
                        moveConnectionRepNode(inode, targetNode);
                    } else if (inode instanceof AnalysisRepNode) {
                        moveAnalysisRepNode(inode, targetNode);
                    } else if (inode instanceof ReportRepNode) {
                        moveReportRepNode(inode, targetNode);
                    } else if (inode instanceof SysIndicatorDefinitionRepNode) {
                        moveUDIRepNode(inode, targetNode);
                    } else if (inode instanceof PatternRepNode) {
                        movePatternRepNode(inode, targetNode);
                    } else if (inode instanceof SourceFileRepNode) {// Added yyin 20120705 TDQ-5716 when rename
                                                                    // sourcefile folder, the sql file in it is lost.
                        moveSourceFileRepNode(inode, targetNode);
                    } else {
                        IRepositoryViewObject viewObj = inode.getObject();
                        if (viewObj != null) {
                            if (viewObj instanceof Folder) {
                                moveFolderRepNode(inode, targetNode);
                            } else {
                                moveCommonRepNode(inode, targetNode);
                            }
                        }
                    }
                }
            }
        }

        // delete source folder
        try {
            IFolder folder = ResourceManager.getRootProject().getProject().getFolder(sourcePath);
            if (folder != null && folder.exists()) {
                folder.delete(true, null);
            }
        } catch (CoreException e) {
            log.error(e, e);
        }

        // refresh the dq view again(refresh before compute dependencies of TDQ Elements)
        CorePlugin.getDefault().refreshDQView(parentNode);
    }

    private boolean isSameType(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        // check root node
        IPath sourcePath = WorkbenchUtils.getPath(sourceNode);
        IPath targetPath = WorkbenchUtils.getPath(targetNode);
        int sourceCount = sourcePath.segmentCount();
        int targetCount = targetPath.segmentCount();
        String sourceString = sourcePath.removeLastSegments(sourceCount - 2).toOSString();
        String targetString = targetPath.removeLastSegments(targetCount - 2).toOSString();
        // MOD klliu Bug TDQ-4444 2012-01-09
        // if sourceNode and targetNode have a same root node, also need to check the node's object type is same.
        // MOD qiongli TDQ-5127 avoid NPE
        ERepositoryObjectType sourceType = sourceNode.getContentType();
        ERepositoryObjectType targetentType = targetNode.getContentType();
        if (sourceType == null || targetentType == null || !sourceType.equals(targetentType)) {
            return false;
        }
        // ~
        return sourceString.equals(targetString);
    }

    private IPath getNodeFullPath(ERepositoryObjectType objectType) {
        IPath fullPath = null;
        if (objectType == ERepositoryObjectType.TDQ_JRAXML_ELEMENT) {
            fullPath = ResourceManager.getJRXMLFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.TDQ_PATTERN_REGEX) {
            fullPath = ResourceManager.getPatternRegexFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.TDQ_PATTERN_SQL) {
            fullPath = ResourceManager.getPatternSQLFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT) {
            fullPath = ResourceManager.getSourceFileFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.TDQ_RULES_SQL) {
            fullPath = ResourceManager.getRulesSQLFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS) {
            fullPath = ResourceManager.getUDIFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.METADATA_CONNECTIONS) {
            fullPath = ResourceManager.getConnectionFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT) { // gdbu 2011-3-16 bug 19537
            fullPath = ResourceManager.getAnalysisFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.TDQ_REPORT_ELEMENT) {
            fullPath = ResourceManager.getReportsFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.METADATA_FILE_DELIMITED) {
            fullPath = ResourceManager.getFileDelimitedFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.METADATA_MDMCONNECTION) {
            fullPath = ResourceManager.getMDMConnectionFolder().getFullPath();
        } else if (objectType == ERepositoryObjectType.TDQ_RULES_PARSER) {
            fullPath = ResourceManager.getRulesParserFolder().getFullPath();
        }
        return fullPath;
    }

    private void computePath(FolderHelper folderHelper, IPath sourcePath, IPath targetPath, IPath makeRelativeTo,
            ERepositoryObjectType type, IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        IPath sourceMakeRelativeTo = sourcePath.makeRelativeTo(makeRelativeTo);
        IPath targetMakeRelativeTo = targetPath.makeRelativeTo(makeRelativeTo);
        factory.moveFolder(type, sourceMakeRelativeTo, targetMakeRelativeTo);
        RepositoryNode sourceParent = sourceNode.getParent();
        RepositoryNode targetParent = targetNode.getParent();
        CorePlugin.getDefault().refreshDQView(sourceParent);
        CorePlugin.getDefault().refreshDQView(targetParent);
    }

    /**
     * move the IRepositoryViewObject from the sourceNode to targetNode, don't refresh the source and target node, user
     * need to refresh the dq repository view tree by hand.
     * 
     * @param objectToMove
     * @param sourceNode
     * @param targetNode
     * @param basePath
     */
    public void moveObject(IRepositoryViewObject objectToMove, IRepositoryNode sourceNode, IRepositoryNode targetNode,
            IPath basePath) {
        IPath targetPath = WorkbenchUtils.getPath(targetNode);
        try {
            IPath makeRelativeTo = Path.EMPTY;
            if (!basePath.isEmpty()) {
                makeRelativeTo = targetPath.makeRelativeTo(basePath);
            }
            factory.moveObject(objectToMove, makeRelativeTo, Path.EMPTY);
        } catch (PersistenceException e) {
            Log.error(sourceNode, e);
        } catch (BusinessException e) {
            Log.error(sourceNode, e);
        }
    }

    private void closeEditorIfOpened(IRepositoryNode fileNode) {
        IWorkbenchPage activePage = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] editorReferences = activePage.getEditorReferences();
        for (IEditorReference reference : editorReferences) {
            try {
                IEditorInput editorInput = reference.getEditorInput();
                if (editorInput instanceof AbstractItemEditorInput) {
                    AbstractItemEditorInput fileInput = (AbstractItemEditorInput) editorInput;

                    if (fileNode.getObject().getLabel().equals(fileInput.getItem().getProperty().getLabel())) {
                        activePage.closeEditor(reference.getEditor(false), false);
                        break;
                    }
                }
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isSubObjectType(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        ERepositoryObjectType sourceObjectType = sourceNode.getContentType();
        ERepositoryObjectType targetObjectType = targetNode.getContentType();
        List<ERepositoryObjectType> childObjecetType = getChildObjecetType(targetObjectType);
        boolean contains = childObjecetType.contains(sourceObjectType);
        return contains;
    }

    private List<ERepositoryObjectType> getChildObjecetType(ERepositoryObjectType targetObjectType) {
        List<ERepositoryObjectType> childObjectType = new ArrayList<ERepositoryObjectType>();
        // get Target child object type
        if (targetObjectType.equals(ERepositoryObjectType.TDQ_DATA_PROFILING)) {
            childObjectType.add(ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
            childObjectType.add(ERepositoryObjectType.TDQ_REPORT_ELEMENT);
        } else if (targetObjectType.equals(ERepositoryObjectType.TDQ_LIBRARIES)) {
            childObjectType.add(ERepositoryObjectType.TDQ_EXCHANGE);
            childObjectType.add(ERepositoryObjectType.TDQ_INDICATOR_ELEMENT);
            childObjectType.add(ERepositoryObjectType.TDQ_JRAXML_ELEMENT);
            childObjectType.add(ERepositoryObjectType.TDQ_PATTERN_ELEMENT);
            childObjectType.add(ERepositoryObjectType.TDQ_RULES);
            childObjectType.add(ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT);
        } else if (targetObjectType.equals(ERepositoryObjectType.TDQ_INDICATOR_ELEMENT)) {
            childObjectType.add(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS);
            childObjectType.add(ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS);
        } else if (targetObjectType.equals(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS)) {
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_ADVANCED_STATISTICS);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_BUSINESS_RULES);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_CORRELATION);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_FUNCTIONAL_DEPENDENCY);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_OVERVIEW);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_FINDER);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_MATCHING);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_ROW_COMPARISON);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_SIMPLE_STATISTICS);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_SOUNDEX);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_SUMMARY_STATISTICS);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_TEXT_STATISTICS);
        } else if (targetObjectType.equals(ERepositoryObjectType.TDQ_PATTERN_ELEMENT)) {
            childObjectType.add(ERepositoryObjectType.TDQ_PATTERN_REGEX);
            childObjectType.add(ERepositoryObjectType.TDQ_PATTERN_SQL);
        } else if (targetObjectType.equals(ERepositoryObjectType.TDQ_RULES)) {
            childObjectType.add(ERepositoryObjectType.TDQ_RULES_SQL);
        } else if (targetObjectType.equals(ERepositoryObjectType.METADATA)) {
            childObjectType.add(ERepositoryObjectType.METADATA_CONNECTIONS);
            childObjectType.add(ERepositoryObjectType.METADATA_MDMCONNECTION);
            childObjectType.add(ERepositoryObjectType.METADATA_FILE_DELIMITED);
        }
        return childObjectType;
    }

    /**
     * forbid some nodes to drag.
     * 
     * @param sourceNode
     * @return
     */
    private boolean isForbidNode(IRepositoryNode sourceNode) {
        ENodeType type = sourceNode.getType();
        // MOD xqliu 2012-05-22 TDQ-4831 allow user to drag Jrxml file
        boolean flag = (type != null && type == ENodeType.SYSTEM_FOLDER) || sourceNode instanceof ReportFileRepNode
                || sourceNode instanceof ReportAnalysisRepNode;
        // ~ TDQ-4831
        if (!flag) {
            RepositoryNode parent = sourceNode.getParent();
            if (parent != null) {
                flag = parent instanceof AnalysisRepNode
                        || parent instanceof ReportRepNode
                        || (parent instanceof AnalysisSubFolderRepNode && ((AnalysisSubFolderRepNode) parent).getObject() == null);
            }
        }
        return flag;
    }
}
