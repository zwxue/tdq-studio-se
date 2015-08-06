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
package org.talend.dataprofiler.core.ui.views.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.FolderHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataprofiler.core.ui.exchange.ExchangeFolderRepNode;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.analysis.category.CategoryHandler;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.AnalysisFolderRepNode;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DBConnectionFolderRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DFConnectionFolderRepNode;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.dq.nodes.IndicatorFolderRepNode;
import org.talend.dq.nodes.JrxmlTempFolderRepNode;
import org.talend.dq.nodes.MDMConnectionFolderRepNode;
import org.talend.dq.nodes.PatternFolderRepNode;
import org.talend.dq.nodes.ReportFolderRepNode;
import org.talend.dq.nodes.RulesFolderRepNode;
import org.talend.dq.nodes.SourceFileFolderRepNode;
import org.talend.repository.localprovider.model.LocalFolderHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;

/**
 * DOC klliu class global comment. Detailled comment
 */
public final class RepositoryNodeBuilder {

    private static RepositoryNodeBuilder instance;

    /**
     * DOC klliu RepositoryNodeBuilder constructor comment.
     */
    public RepositoryNodeBuilder() {

    }

    /**
     * DOC klliu Comment method "retrieveRepObjectType".
     * 
     * @param path
     * @return
     */
    public ERepositoryObjectType retrieveRepObjectTypeByPath(String path) {
        return RepositoryNodeHelper.retrieveRepObjectTypeByPath(path);
    }

    /**
     * DOC klliu Comment method "createSystemFolder".
     * 
     * @param node
     * @param resConstant
     * @return
     * @throws PersistenceException
     */
    public RepositoryNode createRepositoryNodeSystemFolder(FolderHelper folderHelper, RepositoryNode node,
            EResourceConstant resConstant) throws PersistenceException {
        IRepositoryViewObject folder = null;
        DQRepositoryNode subFolderNode = null;
        if (folderHelper != null) {
            FolderItem folder2 = folderHelper.getFolder(resConstant.getPath());
            folder = new Folder(folder2.getProperty(), retrieveRepObjectTypeByPath(resConstant.getPath()));
        } else {
            folder = ProxyRepositoryFactory.getInstance().createFolder(retrieveRepObjectTypeByPath(resConstant.getPath()),
                    Path.EMPTY, resConstant.getName());
        }
        switch (resConstant) {
        case ANALYSIS:
            AnalysisFolderRepNode anaFolderNode = new AnalysisFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(anaFolderNode);
            node.getChildren().add(anaFolderNode);
            return anaFolderNode;
        case REPORTS:
            ReportFolderRepNode repFolderNode = new ReportFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(repFolderNode);
            node.getChildren().add(repFolderNode);
            return repFolderNode;
            // case SYSTEM_INDICATORS:
            // SysIndicatorFolderRepNode systemIndicatorFolderNode = new SysIndicatorFolderRepNode(folder, node,
            // ENodeType.SYSTEM_FOLDER);
            // folder.setRepositoryNode(systemIndicatorFolderNode);
            // node.getChildren().add(systemIndicatorFolderNode);
            // return systemIndicatorFolderNode;
        case INDICATORS:
            IndicatorFolderRepNode indicatorFolderRepNode = new IndicatorFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(indicatorFolderRepNode);
            node.getChildren().add(indicatorFolderRepNode);
            return indicatorFolderRepNode;
            // case USER_DEFINED_INDICATORS:
            // UserDefIndicatorFolderRepNode userDefIndicatorFolderNode = new UserDefIndicatorFolderRepNode(folder,
            // node,
            // ENodeType.SYSTEM_FOLDER);
            // folder.setRepositoryNode(userDefIndicatorFolderNode);
            // node.getChildren().add(userDefIndicatorFolderNode);
            // return userDefIndicatorFolderNode;
        case JRXML_TEMPLATE:
            JrxmlTempFolderRepNode jrxmlFolderNode = new JrxmlTempFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(jrxmlFolderNode);
            node.getChildren().add(jrxmlFolderNode);
            return jrxmlFolderNode;
        case SOURCE_FILES:
            SourceFileFolderRepNode sourceFileFolder = new SourceFileFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(sourceFileFolder);
            node.getChildren().add(sourceFileFolder);
            return sourceFileFolder;
            // case PATTERN_REGEX:
            // PatternRegexFolderRepNode regexFolder = new PatternRegexFolderRepNode(folder, node,
            // ENodeType.SYSTEM_FOLDER);
            // folder.setRepositoryNode(regexFolder);
            // node.getChildren().add(regexFolder);
            // return regexFolder;
        case PATTERNS:
            PatternFolderRepNode regexFolder2 = new PatternFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(regexFolder2);
            node.getChildren().add(regexFolder2);
            return regexFolder2;
            // case PATTERN_SQL:
            // PatternSqlFolderRepNode sqlFolder = new PatternSqlFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            // folder.setRepositoryNode(sqlFolder);
            // node.getChildren().add(sqlFolder);
            // return sqlFolder;
        case RULES:
            RulesFolderRepNode ruleFolder = new RulesFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(ruleFolder);
            node.getChildren().add(ruleFolder);
            return ruleFolder;
        case DB_CONNECTIONS:
            DBConnectionFolderRepNode dbFolder = new DBConnectionFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(dbFolder);
            node.getChildren().add(dbFolder);
            return dbFolder;
        case MDM_CONNECTIONS:
            MDMConnectionFolderRepNode mdmFolder = new MDMConnectionFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(mdmFolder);
            node.getChildren().add(mdmFolder);
            return mdmFolder;
        case FILEDELIMITED:
            DFConnectionFolderRepNode dfmFolder = new DFConnectionFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(dfmFolder);
            node.getChildren().add(dfmFolder);
            return dfmFolder;
        case EXCHANGE:
            ExchangeFolderRepNode exchangeFolder = new ExchangeFolderRepNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(exchangeFolder);
            node.getChildren().add(exchangeFolder);
            return exchangeFolder;
        default:
            subFolderNode = new DQRepositoryNode(folder, node, ENodeType.SYSTEM_FOLDER);
            folder.setRepositoryNode(subFolderNode);
            node.getChildren().add(subFolderNode);
            if (resConstant.equals(EResourceConstant.PATTERNS)) {
                // MOD gdbu 2011-8-26 bug 23303 : initialization regex and sql folder when initialization pattern folder
                createRepositoryNodeSystemFolder(folderHelper, subFolderNode, EResourceConstant.PATTERN_REGEX);
                createRepositoryNodeSystemFolder(folderHelper, subFolderNode, EResourceConstant.PATTERN_SQL);
            }
            break;
        }
        return subFolderNode;
    }

    /**
     * DOC klliu Comment method "createRepositoryNodeSystemFolders".
     * 
     * @param node
     * @param resConstants
     * @return
     * @throws PersistenceException
     */
    public List<RepositoryNode> createRepositoryNodeSystemFolders(FolderHelper folderHelper, RepositoryNode node,
            List<EResourceConstant> resConstants) throws PersistenceException {
        List<RepositoryNode> repositoryNodes = new ArrayList<RepositoryNode>();
        for (EResourceConstant resConstant : resConstants) {
            repositoryNodes.add(createRepositoryNodeSystemFolder(folderHelper, node, resConstant));
        }
        return repositoryNodes;
    }

    /**
     * DOC klliu Comment method "getRepositoryViewObjectChildren".
     * 
     * @param repositoryViewObjects
     * @param node
     * @param withDelete
     * @return
     */
    public List<IRepositoryNode> getRepositoryViewObjectChildren(List<IRepositoryViewObject> repositoryViewObjects,
            RepositoryNode node, boolean withDelete) {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        Iterator<IRepositoryViewObject> iterator = repositoryViewObjects.iterator();
        while (iterator.hasNext()) {
            IRepositoryViewObject viewObject = iterator.next();
            if (viewObject.isDeleted() && !withDelete) {
                iterator.remove();
            } else {
                rectiveNodebyFolderNode(list, viewObject, node);
            }
        }
        return list;
    }

    /**
     * DOC klliu Comment method "rectiveNodebyFolderNode".
     * 
     * @param list
     * @param folderNode
     * @return
     */
    public RepositoryNode rectiveNodebyFolderNode(List<IRepositoryNode> list, IRepositoryViewObject viewObject,
            RepositoryNode folderNode) {
        if (folderNode instanceof AnalysisFolderRepNode) {
            AnalysisRepNode anaNode = new AnalysisRepNode(viewObject, folderNode, ENodeType.REPOSITORY_ELEMENT);
            viewObject.setRepositoryNode(anaNode);
            list.add(anaNode);
            return anaNode;
        } else if (folderNode instanceof DBConnectionFolderRepNode) {
            RepositoryNode connNode = new DBConnectionRepNode(viewObject, folderNode, ENodeType.REPOSITORY_ELEMENT);
            viewObject.setRepositoryNode(connNode);
            list.add(connNode);
            return connNode;
        }
        return null;
    }

    /**
     * DOC klliu Comment method "getIndicatorsChildren".
     * 
     * @param conList
     * @return
     */
    public Collection<? extends IRepositoryNode> getIndicatorsChildren(List<IRepositoryViewObject> conList) {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        Iterator<IRepositoryViewObject> iterator = conList.iterator();
        while (iterator.hasNext()) {
            IRepositoryViewObject indicatorFolder = iterator.next();
            list.add(new RepositoryNode(indicatorFolder, null, ENodeType.SYSTEM_FOLDER));
        }
        return list;
    }

    public FolderHelper getFolderHelper() {
        RepositoryContext repositoryContext = (RepositoryContext) CoreRuntimePlugin.getInstance().getContext()
                .getProperty(Context.REPOSITORY_CONTEXT_KEY);
        Project project = null;
        FolderHelper folderHelper = null;
        if (repositoryContext != null) {
            project = repositoryContext.getProject();
            if (project != null) {
                folderHelper = LocalFolderHelper.createInstance(project.getEmfProject(), repositoryContext.getUser());
            }
        }
        return folderHelper;
    }

    /**
     * DOC xqliu Comment method "getIndicatorsChildren".
     * 
     * @param category
     * @return
     */
    public Object[] getIndicatorsChildren(IndicatorCategory category) {
        List<IndicatorDefinition> indicatorDefinitionList = CategoryHandler.getIndicatorDefinitionList(category);
        if (indicatorDefinitionList == null) {
            indicatorDefinitionList = new ArrayList<IndicatorDefinition>();
        }
        return indicatorDefinitionList.toArray();
    }

    /**
     * DOC xqliu Comment method "getIndicatorChildren".
     * 
     * @return
     */
    public Object[] getIndicatorsChildren(IFolder folder) {
        List<Object> list = new ArrayList<Object>();
        // MOD mzhao feature 13676, split system indicators. 2010-07-08
        // list.add(new IndicatorFolderNode("System"));
        try {
            list.addAll(Arrays.asList(folder.members()));
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return list.toArray();
    }

    /**
     * DOC klliu Comment method "getInstance".
     * 
     * @return
     */
    public static RepositoryNodeBuilder getInstance() {
        if (instance == null) {
            instance = new RepositoryNodeBuilder();
        }
        return instance;
    }

    public Folder getObjectFolder(EResourceConstant resConstant) {
        FolderHelper folderHelper = getFolderHelper();
        FolderItem folder2 = folderHelper.getFolder(resConstant.getPath());
        Folder folder = new Folder(folder2.getProperty(), retrieveRepObjectTypeByPath(resConstant.getPath()));
        return folder;
    }
}
