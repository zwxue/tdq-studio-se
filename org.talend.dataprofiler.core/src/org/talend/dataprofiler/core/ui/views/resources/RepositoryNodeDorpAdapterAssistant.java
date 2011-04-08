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
package org.talend.dataprofiler.core.ui.views.resources;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.jfree.util.Log;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.FolderHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataprofiler.core.ui.views.provider.RepositoryNodeBuilder;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.reports.TdReport;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.ConnectionRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.PatternRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.ReportWriter;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * 
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
        // if (isSameType(sourceNode, targetNode)) {
        // if (sourceNode instanceof AnalysisSubFolderRepNode || sourceNode instanceof ReportSubFolderRepNode
        // || sourceNode instanceof UserDefIndicatorSubFolderRepNode
        // || sourceNode instanceof DBConnectionSubFolderRepNode) {
        // return true;
        // }
        // }
        return true;
    }

    @Override
    public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget) {
        try {
            IRepositoryNode[] selectedRepositoryNodes = null;

            IRepositoryNode targetNode = (IRepositoryNode) aTarget;
            if (aDropAdapter.getCurrentTarget() == null || aDropTargetEvent.data == null) {
                return Status.CANCEL_STATUS;
            }
            TransferData currentTransfer = aDropAdapter.getCurrentTransfer();
            if (LocalSelectionTransfer.getTransfer().isSupportedType(currentTransfer)) {
                selectedRepositoryNodes = getSelectedRepositoryNodes();
            }
            computeRepNodeType(selectedRepositoryNodes, targetNode);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        CorePlugin.getDefault().refreshDQView();
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

    private void computeRepNodeType(IRepositoryNode[] selectedRepositoryNodes, IRepositoryNode targetNode)
            throws PersistenceException {
        if (selectedRepositoryNodes != null) {
            for (IRepositoryNode sourceNode : selectedRepositoryNodes) {
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
                        } else {
                            moveCommonRepNode(sourceNode, targetNode);
                        }
                    } else if (sourceNode.getType() == ENodeType.SIMPLE_FOLDER) {
                        moveFolderRepNode(sourceNode, targetNode);
                    }
                    closeEditorIfOpened(sourceNode);
                }
            }
        }
    }

    private void moveAnalysisRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        TDQAnalysisItem item = (TDQAnalysisItem) objectToMove.getProperty().getItem();
        IPath fullPath = ResourceManager.getAnalysisFolder().getFullPath();
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode,
                    fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, Path.EMPTY);
        }
        // refresh the dq repository tree view
        // CorePlugin.getDefault().refreshDQView(sourceNode.getParent());
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
        // update the dependencies of analysis
        this.updateAnalysisDependency(item);
    }

    private void moveReportRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        TDQReportItem item = (TDQReportItem) objectToMove.getProperty().getItem();
        IPath fullPath = ResourceManager.getReportsFolder().getFullPath();
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode,
                    fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, Path.EMPTY);
        }
        // refresh the dq repository tree view
        // CorePlugin.getDefault().refreshDQView(sourceNode.getParent());
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
        // modifyAnaDependency(analysisList, report);
        this.updateReportDependency(item);
    }

    private void movePatternRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        ERepositoryObjectType contentType = sourceNode.getContentType();
        TDQPatternItem item = (TDQPatternItem) objectToMove.getProperty().getItem();
        IPath fullPath = this.getNodeFullPath(contentType);
        IPath makeRelativeTo = fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        IPath removeLastSegments = makeRelativeTo.removeLastSegments(1);
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        }
        // refresh the dq repository tree view
        // CorePlugin.getDefault().refreshDQView(sourceNode.getParent());
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
        // modifyAnaDependency(analysisList, indicatorDefinition);
        this.updatePatternDependency(item);
    }

    private void moveConnectionRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        ConnectionItem item = (ConnectionItem) objectToMove.getProperty().getItem();
        IPath fullPath = ResourceManager.getReportsFolder().getFullPath();
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode,
                    fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, Path.EMPTY);
        }
        // refresh the dq repository tree view
        // CorePlugin.getDefault().refreshDQView(sourceNode.getParent());
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
        // modifyAnaDependency(analysisList, connection);
        this.updateConnectionDependency(item);
    }

    private void moveUDIRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        ERepositoryObjectType contentType = sourceNode.getContentType();
        TDQIndicatorDefinitionItem item = (TDQIndicatorDefinitionItem) objectToMove.getProperty().getItem();
        IPath fullPath = this.getNodeFullPath(contentType);
        IPath makeRelativeTo = fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        IPath removeLastSegments = makeRelativeTo.removeLastSegments(1);
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        }
        // refresh the dq repository tree view
        // CorePlugin.getDefault().refreshDQView(sourceNode.getParent());
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
        // modifyAnaDependency(analysisList, indicatorDefinition);
        this.updateIndicatorDefinitionDependency(item);
    }

    private void moveCommonRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        ERepositoryObjectType targetObjectType = targetNode.getContentType();
        IPath fullPath = getNodeFullPath(targetObjectType);
        IPath makeRelativeTo = fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        IPath removeLastSegments = makeRelativeTo.removeLastSegments(1);
        if (ERepositoryObjectType.TDQ_RULES_SQL.equals(targetObjectType)
                || ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT.equals(targetObjectType)
                || ERepositoryObjectType.TDQ_REPORT_ELEMENT.equals(targetObjectType)
                || ERepositoryObjectType.METADATA_CONNECTIONS.equals(targetObjectType)
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
        // CorePlugin.getDefault().refreshDQView(sourceNode.getParent());
        CorePlugin.getDefault().refreshDQView(targetNode.getParent());
    }

    /**
     * MOD bu gdbu 2011-4-2 bug : 19537
     * 
     * DOC gdbu Comment method "canMoveNode".
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
        // MOD bu gdbu 2011-4-2 bug : 19537
        if (!canMoveNode(sourceNode, targetNode)) {
            // Doesn't allow the parent node moved to the child node
            return;
        }
        // ~19537
        RepositoryNodeBuilder instance = RepositoryNodeBuilder.getInstance();
        FolderHelper folderHelper = instance.getFolderHelper();
        IPath sourcePath = WorkbenchUtils.getPath((RepositoryNode) sourceNode);
        IPath targetPath = WorkbenchUtils.getPath((RepositoryNode) targetNode);
        ERepositoryObjectType objectType = targetNode.getContentType();
        IPath nodeFullPath = this.getNodeFullPath(objectType);
        IPath makeRelativeTo = nodeFullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        computePath(folderHelper, sourcePath, targetPath, makeRelativeTo, objectType);
    }

    /**
     * rename the RepositoryNode's folder name (the RepositoryNode must be a folder).
     * 
     * @param folderNode
     * @param label
     * @throws PersistenceException
     */
    public void renameFolderRepNode(IRepositoryNode folderNode, String label) throws PersistenceException {
        if (folderNode == null) {
            return;
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

        RepositoryNodeBuilder instance = RepositoryNodeBuilder.getInstance();
        FolderHelper folderHelper = instance.getFolderHelper();
        IPath sourcePath = WorkbenchUtils.getPath((RepositoryNode) folderNode);
        String completeNewPath = ERepositoryObjectType.getFolderName(objectType) + path + IPath.SEPARATOR + label;

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

        // update the dependencies
        FolderItem emfFolder = folderHelper.getFolder(completeNewPath);
        computeDependcy(emfFolder);
    }

    private boolean isSameType(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        IPath sourcePath = WorkbenchUtils.getPath((RepositoryNode) sourceNode);
        IPath targetPath = WorkbenchUtils.getPath((RepositoryNode) targetNode);
        int sourceCount = sourcePath.segmentCount();
        int targetCount = targetPath.segmentCount();
        String sourceString = sourcePath.removeLastSegments(sourceCount - 2).toOSString();
        String targetString = targetPath.removeLastSegments(targetCount - 2).toOSString();
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
        }
        return fullPath;
    }

    private void computePath(FolderHelper folderHelper, IPath sourcePath, IPath targetPath, IPath makeRelativeTo,
            ERepositoryObjectType type) throws PersistenceException {
        String completeNewPath = null;
        IPath sourceMakeRelativeTo = sourcePath.makeRelativeTo(makeRelativeTo);
        IPath targetMakeRelativeTo = targetPath.makeRelativeTo(makeRelativeTo);
        factory.moveFolder(type, sourceMakeRelativeTo, targetMakeRelativeTo);
        if (targetMakeRelativeTo.isEmpty()) {
            completeNewPath = ERepositoryObjectType.getFolderName(type) + IPath.SEPARATOR + sourceMakeRelativeTo.lastSegment();
        } else {
            completeNewPath = ERepositoryObjectType.getFolderName(type) + IPath.SEPARATOR + targetMakeRelativeTo.toString()
                    + IPath.SEPARATOR + sourceMakeRelativeTo.lastSegment();
        }
        CorePlugin.getDefault().refreshDQView(); // TODO find the node to refresh
        FolderItem emfFolder = folderHelper.getFolder(completeNewPath);
        computeDependcy(emfFolder);
    }

    private void computeDependcy(FolderItem emfFolder) {
        try {
            if (emfFolder != null) {
                Item[] childrens = (Item[]) emfFolder.getChildren().toArray();
                for (int i = 0; i < childrens.length; i++) {
                    if (childrens[i] instanceof FolderItem) {
                        FolderItem children = (FolderItem) childrens[i];
                        computeDependcy(children);
                    } else {
                        if (childrens[i] instanceof TDQAnalysisItem) {
                            this.updateAnalysisDependency((TDQAnalysisItem) childrens[i]);
                            // List<TdReport> tdRports = this.getTdRports((TDQAnalysisItem) childrens[i]);
                            // List<ModelElement> elements = this.getModelElement((TDQAnalysisItem) childrens[i]);
                            // this.modifyRepDependency(tdRports);
                            // this.modifyConnAndIndicatorDependency(elements);
                        } else if (childrens[i] instanceof TDQReportItem) {
                            this.updateReportDependency((TDQReportItem) childrens[i]);
                            // TDQReportItem repItem = (TDQReportItem) childrens[i];
                            // TdReport report = (TdReport) repItem.getReport();
                            // List<Analysis> analysises = this.getAnalysises(report);
                            // this.modifyAnaDependency(analysises, report);
                        } else if (childrens[i] instanceof TDQIndicatorDefinitionItem) {
                            this.updateIndicatorDefinitionDependency((TDQIndicatorDefinitionItem) childrens[i]);
                            // TDQIndicatorDefinitionItem defItem = (TDQIndicatorDefinitionItem) childrens[i];
                            // IndicatorDefinition indicatorDefinition = defItem.getIndicatorDefinition();
                            // List<Analysis> analysisList = getAnalysises(indicatorDefinition);
                            // this.modifyAnaDependency(analysisList, indicatorDefinition);
                        } else if (childrens[i] instanceof TDQBusinessRuleItem) {
                            this.updateBusinessRuleDependency((TDQBusinessRuleItem) childrens[i]);
                            // TDQBusinessRuleItem ruleItem = (TDQBusinessRuleItem) childrens[i];
                            // DQRule dqrule = ruleItem.getDqrule();
                            // List<Analysis> analysisList = getAnalysises(dqrule);
                            // this.modifyAnaDependency(analysisList, dqrule);
                        } else if (childrens[i] instanceof TDQPatternItem) {
                            this.updatePatternDependency((TDQPatternItem) childrens[i]);
                            // TDQPatternItem patternItem = (TDQPatternItem) childrens[i];
                            // Pattern pattern = patternItem.getPattern();
                            // List<Analysis> analysisList = getAnalysises(pattern);
                            // this.modifyAnaDependency(analysisList, pattern);
                        } else if (childrens[i] instanceof ConnectionItem) {
                            this.updateConnectionDependency((ConnectionItem) childrens[i]);
                            // ConnectionItem connItem = (ConnectionItem) childrens[i];
                            // Connection connection = connItem.getConnection();
                            // List<Analysis> analysisList = getAnalysises(connection);
                            // this.modifyAnaDependency(analysisList, connection);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /**
     * update the dependencies of Connection.
     * 
     * @param connectionItem
     * @throws PersistenceException
     */
    private void updateConnectionDependency(ConnectionItem connectionItem) throws PersistenceException {
        if (connectionItem == null) {
            return;
        }
        Connection connection = connectionItem.getConnection();
        if (connection != null) {
            updateDependency(connection);
        }
    }

    /**
     * update the dependencies of Pattern.
     * 
     * @param tdqPatternItem
     * @throws PersistenceException
     */
    private void updatePatternDependency(TDQPatternItem tdqPatternItem) throws PersistenceException {
        if (tdqPatternItem == null) {
            return;
        }
        Pattern pattern = tdqPatternItem.getPattern();
        if (pattern != null) {
            updateDependency(pattern);
        }
    }

    /**
     * update the dependencies of BusinessRule.
     * 
     * @param tdqBusinessRuleItem
     * @throws PersistenceException
     */
    private void updateBusinessRuleDependency(TDQBusinessRuleItem tdqBusinessRuleItem) throws PersistenceException {
        if (tdqBusinessRuleItem == null) {
            return;
        }
        DQRule dqrule = tdqBusinessRuleItem.getDqrule();
        if (dqrule != null) {
            updateDependency(dqrule);
        }
    }

    /**
     * update the dependencies of IndicatorDefinition.
     * 
     * @param tdqIndicatorDefinitionItem
     * @throws PersistenceException
     */
    private void updateIndicatorDefinitionDependency(TDQIndicatorDefinitionItem tdqIndicatorDefinitionItem)
            throws PersistenceException {
        if (tdqIndicatorDefinitionItem == null) {
            return;
        }
        IndicatorDefinition indicatorDefinition = tdqIndicatorDefinitionItem.getIndicatorDefinition();
        if (indicatorDefinition != null) {
            updateDependency(indicatorDefinition);
        }
    }

    /**
     * update the dependencies of Report.
     * 
     * @param tdqReportItem
     * @throws PersistenceException
     */
    private void updateReportDependency(TDQReportItem tdqReportItem) throws PersistenceException {
        if (tdqReportItem == null) {
            return;
        }
        Report report = tdqReportItem.getReport();
        if (report != null) {
            updateDependency(report);
        }
    }

    /**
     * update the dependencies of Analysis.
     * 
     * @param tdqAnalysisItem
     * @throws PersistenceException
     */
    private void updateAnalysisDependency(TDQAnalysisItem tdqAnalysisItem) throws PersistenceException {
        if (tdqAnalysisItem == null) {
            return;
        }
        Analysis analysis = tdqAnalysisItem.getAnalysis();
        if (analysis != null) {
            updateDependency(analysis);
        }
    }

    /**
     * update the dependencies of ModelElement.
     * 
     * @param modelElement
     * @throws PersistenceException
     */
    private void updateDependency(ModelElement modelElement) throws PersistenceException {
        // update client dependencies(depencencies which's client equals modelElement)
        EList<Dependency> analysisClientDependencies = modelElement.getClientDependency();
        if (analysisClientDependencies != null && !analysisClientDependencies.isEmpty()) {

            Dependency[] arrayAnaClientDeps = analysisClientDependencies
                    .toArray(new Dependency[analysisClientDependencies.size()]);

            modelElement.getClientDependency().clear();

            for (Dependency anaClientDep : arrayAnaClientDeps) {
                EList<ModelElement> suppliers = anaClientDep.getSupplier();
                if (suppliers != null && !suppliers.isEmpty()) {
                    ModelElement[] arraySuppliers = suppliers.toArray(new ModelElement[suppliers.size()]);
                    for (ModelElement supplier : arraySuppliers) {
                        TypedReturnCode<Dependency> trcDep = DependenciesHandler.getInstance().setUsageDependencyOn(modelElement,
                                supplier);
                        if (trcDep != null && trcDep.isOk()) {
                            Property property = PropertyHelper.getProperty(supplier);
                            Item item = property == null ? null : property.getItem();
                            if (item != null) {
                                cleanUpSupplierDependencies(supplier);
                                factory.save(item, null);
                            }
                        }
                    }
                }
            }
        }
        // update supplier dependencies(depencencies which's supplier equals modelElement)
        EList<Dependency> analysisSupplierDependencies = modelElement.getSupplierDependency();
        if (analysisSupplierDependencies != null && !analysisSupplierDependencies.isEmpty()) {

            Dependency[] arrayAnaSupplierDeps = analysisSupplierDependencies.toArray(new Dependency[analysisSupplierDependencies
                    .size()]);

            modelElement.getSupplierDependency().clear();

            for (Dependency anaSupplierDep : arrayAnaSupplierDeps) {
                EList<ModelElement> clients = anaSupplierDep.getClient();
                if (clients != null && !clients.isEmpty()) {
                    ModelElement[] arrayClients = clients.toArray(new ModelElement[clients.size()]);
                    for (ModelElement client : arrayClients) {
                        TypedReturnCode<Dependency> trcDep = DependenciesHandler.getInstance().setUsageDependencyOn(client,
                                modelElement);
                        if (trcDep != null && trcDep.isOk()) {
                            Property property = PropertyHelper.getProperty(client);
                            Item item = property == null ? null : property.getItem();
                            if (item != null) {
                                cleanUpClientDependencies(client);
                                factory.save(item, null);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * clean up the dependencies which's client equals client and supplier is null or proxy..
     * 
     * @param client
     */
    private void cleanUpClientDependencies(ModelElement client) {
        List<Dependency> tempDepList = new ArrayList<Dependency>();
        if (client != null) {
            EList<Dependency> clientDependency = client.getClientDependency();
            if (clientDependency != null && !clientDependency.isEmpty()) {
                for (Dependency dependency : clientDependency) {
                    EList<ModelElement> supplier = dependency.getSupplier();
                    if (supplier != null && !supplier.isEmpty()) {
                        List<ModelElement> tempMeList = new ArrayList<ModelElement>();
                        for (ModelElement me : supplier) {
                            if (!me.eIsProxy()) {
                                tempMeList.add(me);
                            }
                        }
                        if (!tempMeList.isEmpty()) {
                            supplier.clear();
                            supplier.addAll(tempMeList);
                            tempDepList.add(dependency);
                        }
                    }
                }
                clientDependency.clear();
                clientDependency.addAll(tempDepList);
            }
        }
    }

    /**
     * clean up the dependencies which's supplier equals supplier and client is null or proxy.
     * 
     * @param supplier
     */
    private void cleanUpSupplierDependencies(ModelElement supplier) {
        List<Dependency> tempDepList = new ArrayList<Dependency>();
        if (supplier != null) {
            EList<Dependency> supplierDependency = supplier.getSupplierDependency();
            if (supplierDependency != null && !supplierDependency.isEmpty()) {
                for (Dependency dependency : supplierDependency) {
                    EList<ModelElement> client = dependency.getClient();
                    if (client != null && !client.isEmpty()) {
                        List<ModelElement> tempMeList = new ArrayList<ModelElement>();
                        for (ModelElement me : client) {
                            if (!me.eIsProxy()) {
                                tempMeList.add(me);
                            }
                        }
                        if (!tempMeList.isEmpty()) {
                            client.clear();
                            client.addAll(tempMeList);
                            tempDepList.add(dependency);
                        }
                    }
                }
                supplierDependency.clear();
                supplierDependency.addAll(tempDepList);
            }
        }
    }

    private List<Analysis> getAnalysises(ModelElement element) {
        List<Analysis> analysisList = new ArrayList<Analysis>();
        // EList<Dependency> clientDependency = element.getClientDependency();
        EList<Dependency> dependencyList = null;
        if (element instanceof TdReport) {
            dependencyList = element.getClientDependency();
        } else if (element instanceof IndicatorDefinition) {
            dependencyList = element.getSupplierDependency();
        } else if (element instanceof Connection) {
            dependencyList = ((Connection) element).getSupplierDependency();
        } else if (element instanceof Pattern) {
            dependencyList = ((Pattern) element).getSupplierDependency();
        }
        for (Dependency dep : dependencyList) {
            EList<ModelElement> client = null;
            if (element instanceof TdReport) {
                client = dep.getSupplier();
            } else if (element instanceof IndicatorDefinition) {
                client = dep.getClient();
            } else if (element instanceof Connection) {
                client = dep.getClient();
            } else if (element instanceof Pattern) {
                client = dep.getClient();
            }
            for (ModelElement ele : client) {
                if (ele != null && (ele instanceof Analysis)) {
                    analysisList.add((Analysis) ele);
                }
            }
        }
        return analysisList;
    }

    /**
     * DOC klliu Comment method "getConnection".
     * 
     * @param item
     * @return
     */
    private List<ModelElement> getModelElement(TDQAnalysisItem item) {
        List<ModelElement> element = new ArrayList<ModelElement>();
        Analysis analysis = item.getAnalysis();
        EList<Dependency> supplierDependency = analysis.getClientDependency();
        for (Dependency dep : supplierDependency) {
            EList<ModelElement> supplier = dep.getSupplier();
            for (ModelElement ele : supplier) {
                // if (ele != null && (ele instanceof )) {
                element.add(ele);
                // }
            }
        }
        return element;
    }

    private List<TdReport> getTdRports(TDQAnalysisItem item) {
        List<TdReport> reports = new ArrayList<TdReport>();
        Analysis analysis = item.getAnalysis();
        EList<Dependency> supplierDependency = analysis.getSupplierDependency();
        for (Dependency dep : supplierDependency) {
            EList<ModelElement> client = dep.getClient();
            for (ModelElement ele : client) {
                if (ele != null && (ele instanceof TdReport)) {
                    reports.add((TdReport) ele);
                }
            }
        }
        return reports;
    }

    private void modifyAnaDependency(List<Analysis> analysisList, ModelElement element) {
        try {
            if (analysisList.size() > 0) {
                for (Analysis analysis : analysisList) {
                    RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(analysis);
                    if (recursiveFind == null) {
                        continue;
                    }
                    Property property = recursiveFind.getObject().getProperty();
                    TDQAnalysisItem anaItem = (TDQAnalysisItem) property.getItem();
                    // Property property = PropertyHelper.getProperty(analysis);
                    // TDQAnalysisItem anaItem = (TDQAnalysisItem) property.getItem();
                    TypedReturnCode<Dependency> dependencyReturn = null;
                    if (element instanceof TdReport) {
                        dependencyReturn = DependenciesHandler.getInstance().setDependencyOn((TdReport) element,
                                anaItem.getAnalysis());
                    } else if (element instanceof IndicatorDefinition) {
                        dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(anaItem.getAnalysis(),
                                (IndicatorDefinition) element);
                    } else if (element instanceof DataManager) {
                        dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(anaItem.getAnalysis(),
                                (DataManager) element);
                    } else if (element instanceof Pattern) {
                        dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(anaItem.getAnalysis(),
                                (Pattern) element);
                    }
                    if (dependencyReturn.isOk()) {
                        factory.save(anaItem, null);
                    }
                }
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOC klliu Comment method "modifyConnAndIndicatorDependency".
     * 
     * @param elements
     * @throws PersistenceException
     */
    private void modifyConnAndIndicatorDependency(List<ModelElement> elements) throws PersistenceException {
        if (elements.size() > 0) {
            for (ModelElement element : elements) {
                Property property = PropertyHelper.getProperty(element);
                Item item = property.getItem();
                List<ModelElement> dependencyClients = EObjectHelper.getDependencyClients(element);
                TypedReturnCode<Dependency> dependencyReturn = null;
                for (ModelElement dep : dependencyClients) {
                    dependencyReturn = DependenciesHandler.getInstance().setUsageDependencyOn((Analysis) dep, element);
                }
                if (dependencyReturn.isOk()) {
                    // if(item instanceof DatabaseConnectionItem){
                    //
                    // }
                    factory.save(item, null);

                }
            }
        }
    }

    private void modifyRepDependency(List<TdReport> reports) throws PersistenceException {
        if (reports.size() > 0) {
            for (TdReport rep : reports) {
                Property property = PropertyHelper.getProperty(rep);
                TDQReportItem reportItem = (TDQReportItem) property.getItem();
                ReportWriter createReportWriter = ElementWriterFactory.getInstance().createReportWriter();
                createReportWriter.addDependencies(reportItem.getReport());
                factory.save(reportItem, null);
            }
        }
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
    private void moveObject(IRepositoryViewObject objectToMove, IRepositoryNode sourceNode, IRepositoryNode targetNode,
            IPath basePath) {
        // IPath sourcePath = WorkbenchUtils.getPath((RepositoryNode) sourceNode);
        IPath targetPath = WorkbenchUtils.getPath((RepositoryNode) targetNode);
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
}
