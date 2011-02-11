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
package org.talend.dataprofiler.core.ui.views.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.platform.PluginChecker;
import org.talend.core.model.metadata.IMetadataXmlElementType;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.FolderHelper;
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.recycle.impl.RecycleBinManager;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.RecycleBinRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

import common.Logger;

/**
 * DOC klliu Reconstructure the ResourceViewContentProvider for using DI's API.
 */
public class ResourceViewContentProvider extends WorkbenchContentProvider {

    private static Logger log = Logger.getLogger(ResourceViewContentProvider.class);

    /**
     * DOC rli ResourceViewContentProvider constructor comment.
     */
    public ResourceViewContentProvider() {
        super();
    }

    @Override
    public Object[] getChildren(Object element) {
        RepositoryNodeBuilder instance = RepositoryNodeBuilder.getInstance();
        FolderHelper folderHelper = instance.getFolderHelper();
        try {
            if (element instanceof IWorkspaceRoot) {
                Object currentOpenProject = null;
                for (Object child : super.getChildren(element)) {
                    if (child instanceof IProject) {
                        if (((IProject) child).getName().equals(ResourceManager.getRootProjectName())) {
                            currentOpenProject = child;
                            break;
                        }
                    }
                }
                List<Object> folders = new ArrayList<Object>();

                Object[] rootFolders = new Object[0];
                rootFolders = ((IProject) currentOpenProject).members(false);
                for (Object folder : rootFolders) {
                    if (folder instanceof IFolder && ((IFolder) folder).getName().startsWith(DQStructureManager.PREFIX_TDQ)) {
                        IFolder iFolder = (IFolder) folder;
                        if (((IFolder) folder).getName().trim().equals("TDQ_reporting_db")) { //$NON-NLS-1$
                            continue;
                        }
                        IRepositoryViewObject viewObject = null;
                        if (folderHelper != null) {
                            IPath relativePath = iFolder.getFullPath().makeRelativeTo(
                                    ((IProject) currentOpenProject).getFullPath());
                            FolderItem folder2 = folderHelper.getFolder(relativePath);
                            if (folder2 != null && relativePath != null) {
                                viewObject = new Folder(folder2.getProperty(), instance.retrieveRepObjectTypeByPath(relativePath
                                        .toOSString()));
                            }
                        } else {
                            viewObject = new Folder(((IFolder) folder).getName(), ((IFolder) folder).getName());
                        }
                        IRepositoryNode node = new RepositoryNode(viewObject, null, ENodeType.SYSTEM_FOLDER);
                        viewObject.setRepositoryNode(node);
                        folders.add(node);
                    }
                    // MOD mzhao for metadata folder
                    if (folder instanceof IFolder && ((IFolder) folder).getName().equals(EResourceConstant.METADATA.getName())) {
                        IFolder iFolder = (IFolder) folder;
                        IRepositoryViewObject viewObject = null;
                        if (folderHelper != null) {
                            IPath relativePath = iFolder.getFullPath().makeRelativeTo(
                                    ((IProject) currentOpenProject).getFullPath());
                            FolderItem folder2 = folderHelper.getFolder(relativePath);
                            viewObject = new Folder(folder2.getProperty(), instance.retrieveRepObjectTypeByPath(relativePath
                                    .toOSString()));
                        } else {
                            viewObject = new Folder(((IFolder) folder).getName(), ((IFolder) folder).getName());
                        }
                        IRepositoryNode node = new RepositoryNode(viewObject, null, ENodeType.SYSTEM_FOLDER);
                        viewObject.setRepositoryNode(node);
                        folders.add(node);
                    }
                }

                RecycleBinRepNode recycleBin = new RecycleBinRepNode(DefaultMessagesImpl.getString("RecycleBin.resBinName"));
                folders.add(recycleBin);
                return folders.toArray();
            } else if (element instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) element;
                if (node.getChildren().size() <= 0) {
                    IRepositoryViewObject viewObject = node.getObject();
                    String label = viewObject == null ? null : viewObject.getLabel();
                    if (EResourceConstant.DATA_PROFILING.getName().equals(label)) {
                        List<EResourceConstant> resContants = new ArrayList<EResourceConstant>();
                        resContants.add(EResourceConstant.ANALYSIS);
                        if (!ReponsitoryContextBridge.isDefautProject()) {
                            resContants.add(EResourceConstant.REPORTS);
                        }
                        instance.createRepositoryNodeSystemFolders(folderHelper, node, resContants);
                    } else if (EResourceConstant.LIBRARIES.getName().equals(label)) {
                        List<EResourceConstant> resContants = new ArrayList<EResourceConstant>();
                        resContants.add(EResourceConstant.EXCHANGE);
                        resContants.add(EResourceConstant.INDICATORS);
                        if (!PluginChecker.isOnlyTopLoaded()) {
                            resContants.add(EResourceConstant.JRXML_TEMPLATE);
                        }
                        resContants.add(EResourceConstant.PATTERNS);
                        resContants.add(EResourceConstant.RULES);
                        resContants.add(EResourceConstant.SOURCE_FILES);
                        instance.createRepositoryNodeSystemFolders(folderHelper, node, resContants);
                    } else if (EResourceConstant.INDICATORS.getName().equals(label)) {
                        List<EResourceConstant> resContants = new ArrayList<EResourceConstant>();
                        resContants.add(EResourceConstant.SYSTEM_INDICATORS);
                        resContants.add(EResourceConstant.USER_DEFINED_INDICATORS);
                        instance.createRepositoryNodeSystemFolders(folderHelper, node, resContants);
                    } else if (EResourceConstant.PATTERNS.getName().equals(label)) {
                        List<EResourceConstant> resContants = new ArrayList<EResourceConstant>();
                        resContants.add(EResourceConstant.PATTERN_REGEX);
                        resContants.add(EResourceConstant.PATTERN_SQL);
                        instance.createRepositoryNodeSystemFolders(folderHelper, node, resContants);
                    } else if (EResourceConstant.RULES.getName().equals(label)) {
                        instance.createRepositoryNodeSystemFolder(folderHelper, node, EResourceConstant.RULES_SQL);
                    } else if (EResourceConstant.METADATA.getName().equals(label)) {
                        List<EResourceConstant> resContants = new ArrayList<EResourceConstant>();
                        resContants.add(EResourceConstant.DB_CONNECTIONS);
                        resContants.add(EResourceConstant.MDM_CONNECTIONS);
                        if (!PluginChecker.isOnlyTopLoaded()) {
                            resContants.add(EResourceConstant.FILEDELIMITED);
                        }
                        instance.createRepositoryNodeSystemFolders(folderHelper, node, resContants);
                    } else if (node instanceof RecycleBinRepNode) {
                        return RecycleBinManager.getInstance().getChildren((RecycleBinRepNode) node).toArray();
                    }
                }
                return sortRepositoryNode(node.getChildren().toArray());
            }
        } catch (CoreException e) {
            log.error(e);
        } catch (PersistenceException e) {
            log.error(e);
        }
        return sortRepositoryNode(super.getChildren(element));
    }

    /**
     * sort element on the tree.
     * 
     * @param array
     * @return
     */
    private Object[] sortRepositoryNode(Object[] array) {
        if (array != null && array.length > 0) {
            List<IRepositoryNode> repositoryNodeList = RepositoryNodeHelper.getRepositoryNodeList(array);
            return ComparatorsFactory.sort(repositoryNodeList.toArray(), ComparatorsFactory.REPOSITORY_NODE_COMPARATOR_ID);
        }
        return new Object[] {};
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) element;
            IRepositoryViewObject viewObject = node.getObject();
            if (viewObject instanceof MetadataColumnRepositoryObject) {
                return false;
            } else if (viewObject instanceof IMetadataXmlElementType) {
                MetadataXmlElementTypeRepositoryObject metadataXmlElementType = (MetadataXmlElementTypeRepositoryObject) viewObject;
                List<TdXmlElementType> xmlElements = org.talend.cwm.db.connection.ConnectionUtils
                        .getXMLElements(metadataXmlElementType.getTdXmlElementType());
                return xmlElements.size() > 0;
            }
        }
        if (element instanceof IEcosCategory) {
            return true;
        }
        // // MOD qiongli feature 9486
        // if (element instanceof IFolder) {
        // // MOD yyi 2010-09-30 15271: svn project can't load exchange nodes
        // if (ResourceManager.isExchangeFolder((IFolder) element)) {
        // return true;
        // }
        // // ~15271
        // List<Object> obsLs = Arrays.asList(super.getChildren(element));
        // if (obsLs.size() == 1) {
        // Object obj = (Object) obsLs.get(0);
        // if (obj instanceof IFolder && ((IFolder) obj).getName().equals(PluginConstant.SVN_SUFFIX))
        // return false;
        // }
        // } else if (element instanceof DQRecycleBinNode) {
        // DQRecycleBinNode rbn = (DQRecycleBinNode) element;
        // Object obj = rbn.getObject();
        // if (obj instanceof IFolder) {
        // try {
        // return ((IFolder) obj).members().length > 0;
        // } catch (CoreException e) {
        // log.error(e);
        // }
        // }
        // return false;
        // }
        return super.hasChildren(element);
    }

}
