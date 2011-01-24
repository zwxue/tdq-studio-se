package org.talend.dataprofiler.core.ui.views.resources;

import java.util.ArrayList;
import java.util.List;

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
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.FolderHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataprofiler.core.ui.views.provider.RepositoryNodeBuilder;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
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

/**
 * 
 * DOC mzhao Handle drop event of repositoryNode on DQ repository viewer.
 */
public class RepositoryNodeDorpAdapterAssistant extends CommonDropAdapterAssistant {

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
                            if (((SysIndicatorDefinitionRepNode) sourceNode).isSystemIndicator()) {
                                moveUDIRepNode(sourceNode, targetNode);
                            }
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
        List<ModelElement> elements = getModelElement(item);
        List<TdReport> reports = getTdRports(item);
        IPath fullPath = ResourceManager.getAnalysisFolder().getFullPath();
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode,
                    fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, Path.EMPTY);
        }
        if (reports.size() > 0) {
            modifyRepDependency(reports);
        }
        if (elements.size() > 0) {
            modifyConnAndIndicatorDependency(elements);
        }

    }

    private void moveReportRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) {

        IRepositoryViewObject objectToMove = sourceNode.getObject();
        TDQReportItem item = (TDQReportItem) objectToMove.getProperty().getItem();
        TdReport report = (TdReport) item.getReport();
        List<Analysis> analysisList = getAnalysises(report);
        IPath fullPath = ResourceManager.getReportsFolder().getFullPath();
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode,
                    fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, Path.EMPTY);
        }
        modifyAnaDependency(analysisList, report);

    }

    private void moveConnectionRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        ConnectionItem item = (ConnectionItem) objectToMove.getProperty().getItem();
        DatabaseConnection connection = (DatabaseConnection) item.getConnection();
        List<Analysis> analysisList = getAnalysises(connection);
        IPath fullPath = ResourceManager.getReportsFolder().getFullPath();
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode,
                    fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, Path.EMPTY);
        }
        modifyAnaDependency(analysisList, connection);
    }

    private void moveUDIRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        ERepositoryObjectType contentType = sourceNode.getContentType();
        TDQIndicatorDefinitionItem item = (TDQIndicatorDefinitionItem) objectToMove.getProperty().getItem();
        IndicatorDefinition indicatorDefinition = item.getIndicatorDefinition();
        List<Analysis> analysisList = getAnalysises(indicatorDefinition);
        IPath fullPath = this.getNodeFullPath(contentType);
        IPath makeRelativeTo = fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        IPath removeLastSegments = makeRelativeTo.removeLastSegments(1);
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        }
        modifyAnaDependency(analysisList, indicatorDefinition);
    }

    private void moveCommonRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) {
        IRepositoryViewObject objectToMove = sourceNode.getObject();
        ERepositoryObjectType objectType = targetNode.getContentType();
        IPath fullPath = getNodeFullPath(objectType);
        IPath makeRelativeTo = fullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        IPath removeLastSegments = makeRelativeTo.removeLastSegments(1);
        if (targetNode.getType() == ENodeType.SIMPLE_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        } else if (targetNode.getType() == ENodeType.SYSTEM_FOLDER) {
            moveObject(objectToMove, sourceNode, targetNode, removeLastSegments);
        }
    }

    private void moveFolderRepNode(IRepositoryNode sourceNode, IRepositoryNode targetNode) throws PersistenceException {
        RepositoryNodeBuilder instance = RepositoryNodeBuilder.getInstance();
        FolderHelper folderHelper = instance.getFolderHelper();
        IPath sourcePath = WorkbenchUtils.getPath((RepositoryNode) sourceNode);
        IPath targetPath = WorkbenchUtils.getPath((RepositoryNode) targetNode);
        ERepositoryObjectType objectType = targetNode.getContentType();
        IPath nodeFullPath = this.getNodeFullPath(objectType);
        IPath makeRelativeTo = nodeFullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        // ERepositoryObjectType sourceType = ERepositoryObjectType.TDQ_ANALYSIS;
        // if (sourceNode instanceof AnalysisSubFolderRepNode) {
        // IPath makeRelativeTo = ResourceManager.getAnalysisFolder().getFullPath().makeRelativeTo(
        // ResourceManager.getRootProject().getFullPath());
        // computePath(folderHelper, sourcePath, targetPath, makeRelativeTo, ERepositoryObjectType.TDQ_ANALYSIS);
        // } else if (sourceNode instanceof ReportSubFolderRepNode) {
        // IPath makeRelativeTo = ResourceManager.getReportsFolder().getFullPath().makeRelativeTo(
        // ResourceManager.getRootProject().getFullPath());
        // computePath(folderHelper, sourcePath, targetPath, makeRelativeTo, ERepositoryObjectType.TDQ_REPORTS);
        // } else if (sourceNode instanceof UserDefIndicatorSubFolderRepNode) {
        // // FIXME this need to refactor udi
        // IPath makeRelativeTo = ResourceManager.getReportsFolder().getFullPath().makeRelativeTo(
        // ResourceManager.getRootProject().getFullPath());
        // computePath(folderHelper, sourcePath, targetPath, makeRelativeTo,
        // ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS);
        // } else if (sourceNode instanceof DBConnectionSubFolderRepNode) {
        // // FIXME this need to refactor connection
        // IPath makeRelativeTo = ResourceManager.getReportsFolder().getFullPath().makeRelativeTo(
        // ResourceManager.getRootProject().getFullPath());
        // computePath(folderHelper, sourcePath, targetPath, makeRelativeTo,
        // ERepositoryObjectType.METADATA_CONNECTIONS);
        // } else {
        // // FIXME this need to refactor td libary
        // ERepositoryObjectType objectType = targetNode.getContentType();
        // IPath nodeFullPath = this.getNodeFullPath(objectType);
        // IPath makeRelativeTo = nodeFullPath.makeRelativeTo(ResourceManager.getRootProject().getFullPath());
        // computePath(folderHelper, sourcePath, targetPath, makeRelativeTo, objectType);
        // }
        computePath(folderHelper, sourcePath, targetPath, makeRelativeTo, objectType);
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
        switch (objectType) {
        case TDQ_JRXMLTEMPLATE:
            fullPath = ResourceManager.getJRXMLFolder().getFullPath();
            break;
        case TDQ_PATTERN_REGEX:
            fullPath = ResourceManager.getPatternRegexFolder().getFullPath();
            break;
        case TDQ_PATTERN_SQL:
            fullPath = ResourceManager.getPatternSQLFolder().getFullPath();
            break;
        case TDQ_SOURCE_FILES:
            fullPath = ResourceManager.getSourceFileFolder().getFullPath();
            break;
        case TDQ_RULES_SQL:
            fullPath = ResourceManager.getRulesSQLFolder().getFullPath();
            break;
        case TDQ_USERDEFINE_INDICATORS:
            fullPath = ResourceManager.getUDIFolder().getFullPath();
            break;
        case METADATA_CONNECTIONS:
            fullPath = ResourceManager.getConnectionFolder().getFullPath();
            break;
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
                            List<TdReport> tdRports = this.getTdRports((TDQAnalysisItem) childrens[i]);
                            List<ModelElement> elements = this.getModelElement((TDQAnalysisItem) childrens[i]);
                            this.modifyRepDependency(tdRports);
                            this.modifyConnAndIndicatorDependency(elements);

                        } else if (childrens[i] instanceof TDQReportItem) {
                            TDQReportItem repItem = (TDQReportItem) childrens[i];
                            TdReport report = (TdReport) repItem.getReport();
                            List<Analysis> analysises = this.getAnalysises(report);
                            this.modifyAnaDependency(analysises, report);
                        } else if (childrens[i] instanceof TDQIndicatorDefinitionItem) {
                            TDQIndicatorDefinitionItem defItem = (TDQIndicatorDefinitionItem) childrens[i];
                            IndicatorDefinition indicatorDefinition = defItem.getIndicatorDefinition();
                            List<Analysis> analysisList = getAnalysises(indicatorDefinition);
                            this.modifyAnaDependency(analysisList, indicatorDefinition);
                        }
                        // else if (childrens[i] instanceof DatabaseConnectionItem) {
                        // DatabaseConnectionItem connItem = (DatabaseConnectionItem) childrens[i];
                        // DatabaseConnection connection = (DatabaseConnection) connItem.getConnection();
                        // List<Analysis> analysisList = getAnalysises(connection);
                        // this.modifyAnaDependency(analysisList, connection);
                        // }
                    }
                }
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
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
        } else if (element instanceof DatabaseConnection) {
            dependencyList = ((DatabaseConnection) element).getSupplierDependency();
        }
        for (Dependency dep : dependencyList) {
            EList<ModelElement> client = null;
            if (element instanceof TdReport) {
                client = dep.getSupplier();
            } else if (element instanceof IndicatorDefinition) {
                client = dep.getClient();
            } else if (element instanceof DatabaseConnection) {
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
                        anaItem.getAnalysis();
                        dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(anaItem.getAnalysis(),
                                (DataManager) element);
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
