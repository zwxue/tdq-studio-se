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
package org.talend.dataprofiler.core.recycle.impl;

import java.util.ArrayList;
import java.util.List;

import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.dq.nodes.RecycleBinRepNode;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.IRepositoryNode.EProperties;

/**
 * bZhou Comment : This class is to manage the recycle bin.
 */
public final class RecycleBinManager {

    private static RecycleBinManager instance;

    // private List<IRepositoryNode> recycleBinChildren = null;

    private RecycleBinRepNode rcBinNode = null;

    private RecycleBinManager() {

    }

    /**
     * DOC bZhou Comment method "getInstance".
     * 
     * @return
     */
    public static RecycleBinManager getInstance() {
        if (instance == null) {
            instance = new RecycleBinManager();
        }

        return instance;
    }

    /*
     * public IRecycleBin getRecycleBin() { return recycleBin; }
     */

    /**
     * 
     * DOC qiongli Comment method "getChildren".
     * 
     * @param rcBinNode
     * @return
     */
    public void loadChildren(RecycleBinRepNode rcBinNode) {
        this.rcBinNode = rcBinNode;
        List<DQRepositoryNode> foldersList = new ArrayList<DQRepositoryNode>();
        Project newProject = ProjectManager.getInstance().getCurrentProject();
        List<FolderItem> folderItems = ProjectManager.getInstance().getFolders(newProject.getEmfProject());
        for (FolderItem folder : new ArrayList<FolderItem>(folderItems)) {
            if (WorkbenchUtils.isTDQOrMetadataRootFolder(folder)) {
                addItemToRecycleBin(this.rcBinNode, folder, foldersList);
            }
        }

        // recycleBinChildren = rcBinNode.getChildren();
    }

    /**
     * 
     * DOC qiongli Comment method "addItemToRecycleBin".
     * 
     * @param parentNode
     * @param item
     * @param foldersList
     */
    private void addItemToRecycleBin(DQRepositoryNode parentNode, Item item, List<DQRepositoryNode> foldersList) {
        ERepositoryObjectType itemType = ERepositoryObjectType.getItemType(item);
        DQRepositoryNode currentParentNode = parentNode;
        if (item instanceof FolderItem) {
            itemType = getFolderContentType((FolderItem) item);
            if (item.getState() != null && item.getState().isDeleted()) {
                // need to display this folder in the recycle bin.
                Folder folder = new Folder(item.getProperty(), itemType);
                DQRepositoryNode folderNode = null;
                for (DQRepositoryNode existingFolder : foldersList) {
                    if (existingFolder.getContentType() == null) {
                        // this can appear temporary when another user has deleted a folder
                        break;
                    }
                    if (existingFolder.getContentType().equals(folder.getRepositoryObjectType())
                            && existingFolder.getProperties(EProperties.LABEL).equals(folder.getLabel())) {
                        folderNode = existingFolder;
                        break;
                    }
                }
                if (folderNode == null) {
                    folderNode = new DQRepositoryNode(folder, parentNode, ENodeType.SIMPLE_FOLDER);
                    folderNode.setProperties(EProperties.CONTENT_TYPE, itemType);
                    folderNode.setProperties(EProperties.LABEL, folder.getLabel());
                    foldersList.add(folderNode);
                    parentNode.getChildren().add(folderNode);
                    folderNode.setParent(parentNode);
                }

                for (Item curItem : (List<Item>) new ArrayList(((FolderItem) item).getChildren())) {
                    addItemToRecycleBin(folderNode, curItem, foldersList);
                }
                currentParentNode = folderNode;
            } else {
                for (Item curItem : (List<Item>) new ArrayList(((FolderItem) item).getChildren())) {
                    addItemToRecycleBin(parentNode, curItem, foldersList);
                }
            }
        } else if (item.getState() != null && item.getState().isDeleted()) {
            try {
                if (item.getProperty().getVersion()
                        .equals(ProxyRepositoryFactory.getInstance().getLastVersion(item.getProperty().getId()).getVersion())) {
                    DQRepositoryNode repNode = new DQRepositoryNode(new RepositoryViewObject(item.getProperty()),
                            currentParentNode,
                            ENodeType.REPOSITORY_ELEMENT);
                    repNode.setProperties(EProperties.CONTENT_TYPE, itemType);
                    repNode.setProperties(EProperties.LABEL, item.getProperty().getLabel());
                    currentParentNode.getChildren().add(repNode);
                    repNode.setParent(currentParentNode);
                }
            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
            }
        }
    }

    private ERepositoryObjectType getFolderContentType(FolderItem folderItem) {
        if (!folderItem.getType().equals(FolderType.SYSTEM_FOLDER_LITERAL)) {
            if (!(folderItem.getParent() instanceof FolderItem)) {
                return null; // appears only for a folder for expression builder !
            }
            return getFolderContentType((FolderItem) folderItem.getParent());
        }
        for (ERepositoryObjectType objectType : (ERepositoryObjectType[]) ERepositoryObjectType.values()) {
            String folderName;
            try {
                folderName = ERepositoryObjectType.getFolderName(objectType);
            } catch (Exception e) {
                // just catch exception to avoid the types who don't have folders
                continue;
            }
            if (folderName.contains("/")) { //$NON-NLS-1$
                String[] folders = folderName.split("/"); //$NON-NLS-1$
                FolderItem currentFolderItem = folderItem;
                boolean found = true;
                for (int i = folders.length - 1; i >= 0; i--) {
                    if (!currentFolderItem.getProperty().getLabel().equals(folders[i])) {
                        found = false;
                        break;
                    }
                    if (i > 0) {
                        if (!(currentFolderItem.getParent() instanceof FolderItem)) {
                            found = false;
                            break;
                        }
                        currentFolderItem = (FolderItem) currentFolderItem.getParent();
                    }
                }
                if (found) {
                    return objectType;
                }
            } else {
                if (folderName.equals(folderItem.getProperty().getLabel())) {
                    return objectType;
                }
            }
        }
        if (folderItem.getParent() instanceof FolderItem) {
            return getFolderContentType((FolderItem) folderItem.getParent());
        }
        return null;
    }

    public List<IRepositoryNode> getRecycleBinChildren() {
        // MOD gdbu 2011-7-15 bug : 23161
        List<IRepositoryNode> filterResultsIfAny = new ArrayList<IRepositoryNode>();
        filterResultsIfAny.addAll(rcBinNode.filterResultsIfAny(rcBinNode.getChildren()));
        rcBinNode.getChildren().clear();
        rcBinNode.getChildren().addAll(filterResultsIfAny);
        return rcBinNode.getChildren();
        // ~23161
    }
}
