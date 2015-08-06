// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jfree.util.Log;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.helper.WorkspaceResourceHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
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
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.dq.nodes.JrxmlTempSubFolderNode;
import org.talend.dq.nodes.JrxmlTempleteRepNode;
import org.talend.dq.nodes.PatternRepNode;
import org.talend.dq.nodes.ReportAnalysisRepNode;
import org.talend.dq.nodes.ReportFileRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.RuleRepNode;
import org.talend.dq.nodes.SourceFileRepNode;
import org.talend.dq.nodes.SourceFileSubFolderNode;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.repository.model.ERepositoryStatus;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.actions.MoveObjectAction;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;

/**
 * Local Repository Object CRUD. only when the project is local use this.
 * 
 */
public class LocalRepositoryObjectCRUD implements IRepositoryObjectCRUD {

    private static final IPath PROJECT_FULL_PATH = ResourceManager.getRootProject().getFullPath();

    protected static Logger log = Logger.getLogger(LocalRepositoryObjectCRUD.class);

    private static final IRepositoryNode[] NO_RESOURCES = new IRepositoryNode[0];

    private IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    public Boolean validateDrop(IRepositoryNode targetNode) {
        Boolean retStatus = Boolean.FALSE;
        for (IRepositoryNode res : getSelectedRepositoryNodes()) {
            switch (targetNode.getType()) {
            case SYSTEM_FOLDER:
            case SIMPLE_FOLDER:
                if (allowDND(res, targetNode)) {
                    return Boolean.TRUE;
                }
                break;
            case REPOSITORY_ELEMENT:
                if (res instanceof AnalysisRepNode && targetNode instanceof ReportRepNode) {
                    return Boolean.TRUE;
                }
                break;
            default:
                break;
            }
        }
        return retStatus;
    }

    /**
     * check whether DND is allowed.
     * 
     * @param sourceNode
     * @param targetNode
     * @return
     */
    private boolean allowDND(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        // MOD klliu Bug TDQ-4330 if targetCount's length is 1,that means targetNode is the root and system node.
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
        // This need check the object type of source node is sub type of targetNode's
        // if it is,that is not allowed to drop.
        if (isSubTypeOfTargetNode(sourceNode, targetNode)) {
            return false;
        }
        // ~

        // can't drag an item in recycle bin
        if (ProxyRepositoryFactory.getInstance().getStatus(sourceNode.getObject()) == ERepositoryStatus.DELETED) {
            return false;
        }
        if (sourceNode.equals(targetNode)) {
            return false;
        }
        return true;
    }

    /**
     * get Selected Repository Nodes.
     * 
     * @return
     */
    protected IRepositoryNode[] getSelectedRepositoryNodes() {
        ISelection selection = getUISelection();
        if (selection == null) {
            return NO_RESOURCES;
        }
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        if (obj == null) {
            return NO_RESOURCES;
        }

        if (selection instanceof IStructuredSelection) {
            return getSelectedRepositoryNodes((IStructuredSelection) selection);
        }
        return NO_RESOURCES;
    }

    /**
     * get Selected Repository Nodes from the selection.
     * 
     * @param selection
     * @return
     */
    private IRepositoryNode[] getSelectedRepositoryNodes(IStructuredSelection selection) {
        ArrayList<IRepositoryNode> selectedRepositoryNodes = new ArrayList<IRepositoryNode>();
        List<?> list = selection.toList();
        for (Object o : list) {
            if (o instanceof IRepositoryNode) {
                selectedRepositoryNodes.add((IRepositoryNode) o);
            }
        }
        return selectedRepositoryNodes.toArray(new IRepositoryNode[selectedRepositoryNodes.size()]);
    }

    /**
     * check sourceNode whether has Locked Items.
     * 
     * @param sourceNode
     * @return boolean
     */
    private boolean haveLockedItems(IRepositoryNode sourceNode) {
        IRepositoryViewObject object = sourceNode.getObject();
        if (object != null) {
            // when it is locked, can not move.
            if (object instanceof Folder) {
                List<IRepositoryNode> children = object.getRepositoryNode().getChildren();
                if (children != null) {
                    for (IRepositoryNode childrenNode : children) {
                        if (haveLockedItems(childrenNode)) {
                            return true;
                        }
                    }
                }
            } else {
                if (MoveObjectAction.getInstance().isLock((RepositoryNode) sourceNode)) {
                    MessageDialog
                            .openWarning(
                                    PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                    DefaultMessagesImpl.getString("RepositoyNodeDropAdapterAssistant.error.moveError"), DefaultMessagesImpl.getString("RepositoyNodeDropAdapterAssistant.error.moveIsLocked")); //$NON-NLS-1$ //$NON-NLS-2$
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * check whether the source node is the subtype of target node.
     * 
     * @param sourceNode
     * @param targetNode
     * @return
     */
    private boolean isSubTypeOfTargetNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        ERepositoryObjectType sourceObjectType = sourceNode.getContentType();
        ERepositoryObjectType targetObjectType = targetNode.getContentType();
        List<ERepositoryObjectType> targetNodeChildTypes = getTargetNodeChildTypes(targetObjectType);
        return targetNodeChildTypes.contains(sourceObjectType);
    }

    /**
     * get Target Node's all Child Types.
     * 
     * @param targetObjectType
     * @return
     */
    private List<ERepositoryObjectType> getTargetNodeChildTypes(ERepositoryObjectType targetObjectType) {
        List<ERepositoryObjectType> childObjectType = new ArrayList<ERepositoryObjectType>();
        // get Target node child types
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
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_FRAUDDETECTION);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_FUNCTIONAL_DEPENDENCY);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_FINDER);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_MATCHING);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_PHONENUMBER_STATISTICS);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_ROW_COMPARISON);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_SIMPLE_STATISTICS);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_SOUNDEX);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_SUMMARY_STATISTICS);
            childObjectType.add(ERepositoryObjectType.SYSTEM_INDICATORS_TEXT_STATISTICS);
        } else if (targetObjectType.equals(ERepositoryObjectType.TDQ_PATTERN_ELEMENT)) {
            childObjectType.add(ERepositoryObjectType.TDQ_PATTERN_REGEX);
            childObjectType.add(ERepositoryObjectType.TDQ_PATTERN_SQL);
        } else if (targetObjectType.equals(ERepositoryObjectType.TDQ_RULES)) {
            childObjectType.add(ERepositoryObjectType.TDQ_RULES_PARSER);
            childObjectType.add(ERepositoryObjectType.TDQ_RULES_SQL);
        } else if (targetObjectType.equals(ERepositoryObjectType.METADATA)) {
            childObjectType.add(ERepositoryObjectType.METADATA_MDMCONNECTION);
            childObjectType.add(ERepositoryObjectType.METADATA_CONNECTIONS);
            childObjectType.add(ERepositoryObjectType.METADATA_FILE_DELIMITED);
        }
        return childObjectType;
    }

    /**
     * check whether the node is forbidden to drag.
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

    public Boolean handleDrop(IRepositoryNode targetNode) {

        Boolean isHandleOK = Boolean.FALSE;

        try {
            IRepositoryNode[] selectedRepositoryNodes = getSelectedRepositoryNodes();

            // do some checks before moving.
            for (IRepositoryNode sourceNode : selectedRepositoryNodes) {
                // MOD msjian 2012-10-23 TDQ-5614: when the node is locked, tell user and make it can not move.
                if (haveLockedItems(sourceNode)) {
                    return isHandleOK;
                }
                if (!isSameType(sourceNode, targetNode)) {
                    MessageDialog
                            .openWarning(
                                    PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                    DefaultMessagesImpl.getString("RepositoyNodeDropAdapterAssistant.error.moveError"), DefaultMessagesImpl.getString("RepositoyNodeDropAdapterAssistant.error.moveNotSameType")); //$NON-NLS-1$ //$NON-NLS-2$
                    return isHandleOK;
                }
                // TDQ-5614 ~
            }

            // do move.
            moveRepositoryNodes(selectedRepositoryNodes, targetNode);

            // MOD gdbu 2011-11-17 TDQ-3969 : after move folder or items re-filter the tree , to create a new list .
            if (DQRepositoryNode.isOnFilterring()) {
                RepositoryNodeHelper.fillTreeList(null);
                RepositoryNodeHelper.setFilteredNode(RepositoryNodeHelper.getRootNode(ERepositoryObjectType.TDQ_DATA_PROFILING,
                        true));
            }
            isHandleOK = Boolean.TRUE;

        } catch (PersistenceException e) {
            if (log.isInfoEnabled()) {
                log.info(e.toString());
            }
            MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                    DefaultMessagesImpl.getString("RepositoyNodeDropAdapterAssistant.error.moveError"), e.getMessage()); //$NON-NLS-1$
            isHandleOK = Boolean.FALSE;

        }
        // MOD gdbu TDQ-3546 unload resource after move item.
        ProxyRepositoryManager.getInstance().refresh();
        return isHandleOK;
    }

    /**
     * move RepositoryNodes to the target RepositoryNode.
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
                if (sourceNode.getType() == ENodeType.REPOSITORY_ELEMENT) {
                    if (sourceNode instanceof AnalysisRepNode || sourceNode instanceof ConnectionRepNode) {
                        moveAnaConNode(sourceNode, targetNode);
                    } else if (sourceNode instanceof ReportRepNode) {
                        moveReportRepNode(sourceNode, targetNode);
                    } else {
                        IPath makeRelativeTo = getMakeRelativeTo(sourceNode);
                        IPath removeLastSegments = makeRelativeTo.removeLastSegments(1);
                        if (sourceNode instanceof SysIndicatorDefinitionRepNode) {
                            // SystemIndicatorDefinition can't be moved
                            if (!((SysIndicatorDefinitionRepNode) sourceNode).isSystemIndicator()) {
                                moveOthersNode(sourceNode, targetNode, removeLastSegments);
                            }
                        } else if (sourceNode instanceof PatternRepNode) {
                            moveOthersNode(sourceNode, targetNode, removeLastSegments);
                        } else if (sourceNode instanceof JrxmlTempleteRepNode) {
                            moveJrxmlFileRepNode(sourceNode, targetNode);
                        } else if (sourceNode instanceof SourceFileRepNode || sourceNode instanceof RuleRepNode) {
                            moveOthersNode(sourceNode, targetNode, makeRelativeTo);
                        } else {
                            moveOthersNode(sourceNode, targetNode, removeLastSegments);
                        }
                    }
                } else if (sourceNode.getType() == ENodeType.SIMPLE_FOLDER) {
                    moveFolderRepNode(sourceNode, targetNode);
                }
                // refresh the dq repository tree view
                CorePlugin.getDefault().refreshDQView(targetNode.getParent());
                // MOD qiongli 2012-4-23,only refresh the parent of source node at here.
                CorePlugin.getDefault().refreshDQView(sourceNode.getParent());
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
     * when move Analysis, Connection node, use this method.
     * 
     * @param sourceNode
     * @param targetNode
     */
    private void moveAnaConNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        // MOD yyin 20121127, TDQ-6302, when back from DI's expanded Metadata, can not move the connections.
        final IRepositoryNode finalSourceNode = sourceNode;
        final IRepositoryNode finalTargetNode = targetNode;
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRunnable operation = new IWorkspaceRunnable() {

            public void run(IProgressMonitor monitor) throws CoreException {
                if (finalTargetNode.getType() == ENodeType.SIMPLE_FOLDER) {
                    moveObject(finalSourceNode, finalTargetNode, getMakeRelativeTo(finalSourceNode));
                } else if (finalTargetNode.getType() == ENodeType.SYSTEM_FOLDER) {
                    moveObject(finalSourceNode, finalTargetNode, Path.EMPTY);
                }
            }
        };
        ISchedulingRule schedulingRule = workspace.getRoot();
        try {
            workspace.run(operation, schedulingRule, IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
        } catch (CoreException e) {
            log.error(e, e);
        }
    }

    /**
     * get Source Node Full path Relative To project full path.
     * 
     * @param sourceNode
     * @return
     */
    private IPath getMakeRelativeTo(IRepositoryNode sourceNode) {
        IPath fullPath = getFullPathFormObjectType(sourceNode.getContentType());
        IPath makeRelativeTo = fullPath.makeRelativeTo(PROJECT_FULL_PATH);
        return makeRelativeTo;
    }

    /**
     * move the report node (move the report generate doc folder also).
     * 
     * @param sourceNode
     * @param targetNode
     * @throws PersistenceException
     */
    private void moveReportRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        // MOD yyi 2012-02-22:TDQ-4545 Update user define jrxml template path.
        relocateJrxmlTemplates(sourceNode, targetNode);

        // get the report generated doc folder
        IFolder outputFolder = ReportUtils.getOutputFolder((ReportRepNode) sourceNode);

        moveAnaConNode(sourceNode, targetNode);

        // move the report generated doc folder
        File srcFolder = WorkspaceUtils.ifolderToFile(outputFolder);
        IFolder targetFolder = RepositoryNodeHelper.getIFolder(targetNode);
        File tarFolder = WorkspaceUtils.ifolderToFile(targetFolder);
        ReportUtils.moveReportGeneratedDocFolder(srcFolder, tarFolder);
    }

    /**
     * update user define jrxml template path.
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

    /**
     * when move Common, Source file, Pattern, UDI, Rule, use this method.
     * 
     * @param sourceNode
     * @param targetNode
     * @param removeLastSegments
     */
    public void moveOthersNode(IRepositoryNode sourceNode, IRepositoryNode targetNode, IPath ipath) {
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER || targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(sourceNode, targetNode, ipath);
        }
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

    /**
     * move Folder Node.
     * 
     * @param sourceNode
     * @param targetNode
     * @throws PersistenceException
     */
    private void moveFolderRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        // ADD xqliu 2012-05-24 TDQ-4831
        if (sourceNode instanceof JrxmlTempSubFolderNode) {
            MessageUI.openWarning(DefaultMessagesImpl.getString("JrxmlFileAction.forbiddenOperation")); //$NON-NLS-1$
            return;
        }
        // ~ TDQ-4831
        // MOD bu gdbu 2011-4-2 bug : 19537
        if (!canMoveNode(sourceNode, targetNode)) {
            // Doesn't allow the parent node moved to the child node
            return;
        }
        // ~19537

        moveFolder(sourceNode, targetNode);
    }

    /**
     * check whether sourceNode and targetNode is the same Type.
     * 
     * @param sourceNode
     * @param targetNode
     * @return
     */
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

    /**
     * get FullPath from objectType.
     * 
     * @param objectType
     * @return
     */
    private IPath getFullPathFormObjectType(ERepositoryObjectType objectType) {
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

    /**
     * move folder.
     * 
     * @param sourceNode
     * @param targetNode
     * @throws PersistenceException
     */
    private void moveFolder(final IRepositoryNode sourceNode, final IRepositoryNode targetNode) throws PersistenceException {
        IPath sourcePath = WorkbenchUtils.getPath(sourceNode);
        IPath targetPath = WorkbenchUtils.getPath(targetNode);
        IPath makeRelativeTo = getMakeRelativeTo(targetNode);
        final IPath sourceMakeRelativeTo = sourcePath.makeRelativeTo(makeRelativeTo);
        final IPath targetMakeRelativeTo = targetPath.makeRelativeTo(makeRelativeTo);

        factory.moveFolder(targetNode.getContentType(), sourceMakeRelativeTo, targetMakeRelativeTo);
    }

    /**
     * move the IRepositoryViewObject from the sourceNode to targetNode, don't refresh the source and target node, user
     * need to refresh the dq repository view tree by hand.
     * 
     * @param sourceNode
     * @param targetNode
     * @param basePath
     */
    public void moveObject(final IRepositoryNode sourceNode, final IRepositoryNode targetNode, final IPath basePath) {
        IPath targetPath = WorkbenchUtils.getPath(targetNode);
        try {
            IPath makeRelativeTo = Path.EMPTY;
            if (!basePath.isEmpty()) {
                makeRelativeTo = targetPath.makeRelativeTo(basePath);
            }
            factory.moveObject(sourceNode.getObject(), makeRelativeTo, Path.EMPTY);
        } catch (PersistenceException e) {
            Log.error(sourceNode, e);
        } catch (BusinessException e) {
            Log.error(sourceNode, e);
        }
    }

    public Boolean handleRenameFolder(IRepositoryNode repositoryNode) {
        // ADD xqliu 2012-05-24 TDQ-4831
        if (repositoryNode instanceof JrxmlTempSubFolderNode) {
            MessageUI.openWarning(DefaultMessagesImpl.getString("JrxmlFileAction.forbiddenOperation")); //$NON-NLS-1$
            return Boolean.FALSE;
        }
        // ~ TDQ-4831
        // Added yyin 20120712 TDQ-5721 when rename the sql file folder with file opening, should inform
        if (repositoryNode instanceof SourceFileSubFolderNode) {
            ReturnCode rc = WorkspaceResourceHelper.checkSourceFileSubFolderNodeOpening((SourceFileSubFolderNode) repositoryNode);
            if (rc.isOk()) {
                WorkspaceResourceHelper.showSourceFilesOpeningWarnMessages(rc.getMessage());
                return Boolean.FALSE;
            }
        }// ~

        return Boolean.TRUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUD#getUISelection()
     */
    public ISelection getUISelection() {
        ISelection sel = null;
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
            if (activePage != null) {
                IWorkbenchPart activePart = activePage.getActivePart();
                if (activePart != null) {
                    if (activePart instanceof DQRespositoryView) {
                        sel = ((DQRespositoryView) activePart).getCommonViewer().getSelection();
                    }
                }
            }
        }
        return sel;
    }

    /**
     * refresh Workspace and DQView.
     */
    public void refreshWorkspaceDQView() {
        IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
        if (activePart instanceof DQRespositoryView) {
            ((DQRespositoryView) activePart).refresh();
        }
        CorePlugin.getDefault().refreshWorkSpace();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUDAction#showWarningDialog()
     */
    public void showWarningDialog() {
        MessageDialog
                .openWarning(
                        PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                        DefaultMessagesImpl.getString("LocalRepositoryObjectCRUD.Title"), DefaultMessagesImpl.getString("LocalRepositoryObjectCRUD.Content")); //$NON-NLS-1$ //$NON-NLS-2$  
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUD#refreshDQViewForRemoteProject()
     */
    public void refreshDQViewForRemoteProject() {
        // in local project, needn't to refresh
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUDAction#isSelectionAvailable()
     */
    public Boolean isSelectionAvailable() {
        return true;
    }
}