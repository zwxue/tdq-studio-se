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
package org.talend.dataprofiler.core.ui.utils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.FolderHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.ProjectManager;
import org.talend.repository.localprovider.model.LocalFolderHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class WorkbenchUtils {

    private static Logger log = Logger.getLogger(WorkbenchUtils.class);

    private WorkbenchUtils() {
    }

    /**
     * DOC bZhou Comment method "changePerspective".
     * 
     * @param perspectiveID
     */
    public static void changePerspective(String perspectiveID) {
        IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        try {
            PlatformUI.getWorkbench().showPerspective(perspectiveID, activeWindow);
        } catch (WorkbenchException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void autoChange2DataProfilerPerspective() {
        IPerspectiveDescriptor perspective = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getPerspective();
        if (!PluginConstant.PERSPECTIVE_ID.equals(perspective.getId())) {
            if (MessageUI.openConfirm(DefaultMessagesImpl.getString("WorkbenchUtils.autoChange2DataProfilerPerspective"))) { //$NON-NLS-1$
                changePerspective(PluginConstant.PERSPECTIVE_ID);
            }
        }
    }

    public static IFolder folder2IFolder(Folder folder) {
        IProject rootProject = ResourceManager.getRootProject();
        return rootProject.getFolder(folder.getPath());
    }

    public static IPath getPath(IRepositoryNode node) {
        return RepositoryNodeHelper.getPath(node);
    }

    public static IPath getPath(RepositoryViewObject viewObject) {
        return getPath((RepositoryNode) viewObject.getRepositoryNode());
    }

    public static IFolder getFolder(RepositoryNode node) {
        // MOD qiongli 2011-1-18 if it is recyclebin,return the root folder
        IPath path = getPath(node);
        if (path.toString().equals(PluginConstant.EMPTY_STRING)) {
            return ResourceManager.getRootProject().getFolder(ResourceManager.getRootFolderLocation());
        }
        return ResourceManager.getRootProject().getFolder(getPath(node));
    }

    public static IFolder getFolder(RepositoryViewObject viewObject) {
        return getFolder((RepositoryNode) viewObject.getRepositoryNode());
    }

    public static String getItemExtendtion(int classID) {
        String fileExtension = FileConstants.ITEM_EXTENSION;
        switch (classID) {

        case org.talend.dataquality.properties.PropertiesPackage.TDQ_ANALYSIS_ITEM:
            fileExtension = FileConstants.ANA_EXTENSION;
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_REPORT_ITEM:
            fileExtension = FileConstants.REP_EXTENSION;
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_INDICATOR_DEFINITION_ITEM:
            fileExtension = FileConstants.DEF_EXTENSION;
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_PATTERN_ITEM:
            fileExtension = FileConstants.PAT_EXTENSION;
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_BUSINESS_RULE_ITEM:
            fileExtension = FileConstants.RULE_EXTENSION;

            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_JRXML_ITEM:
            fileExtension = FileConstants.JRXML_EXTENSION;
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_SOURCE_FILE_ITEM:
            fileExtension = FileConstants.SQL_EXTENSION;
            break;
        }
        return fileExtension;

    }

    public static IPath getFilePath(IRepositoryNode node) {
        Item item = node.getObject().getProperty().getItem();
        ERepositoryObjectType itemType = ERepositoryObjectType.getItemType(item);
        IPath folderPath = WorkbenchUtils.getPath(node);
        String name = node
                .getObject()
                .getLabel()
                .concat("_")
                .concat(node.getObject().getProperty().getVersion())
                .concat(".")
                .concat(WorkbenchUtils.getItemExtendtion(item != null ? item.eClass().getClassifierID() : node.getObject()
                        .getProperty().getItem().eClass().getClassifierID()));
        IPath append = folderPath.append(new Path(name));
        return append;
    }

    /**
     * 
     * if it is TDQ_Data Profiling,TDQ_Libraries or metadata.
     * 
     * @param folderItem
     * @return
     */
    public static boolean isTDQOrMetadataRootFolder(FolderItem folderItem) {
        Project newProject = ProjectManager.getInstance().getCurrentProject();

        FolderHelper folderHelper = LocalFolderHelper.createInstance(newProject.getEmfProject(), ProxyRepositoryFactory
                .getInstance().getRepositoryContext().getUser());
        String path = folderHelper.getFullFolderPath(folderItem);
        if (path != null && (path.startsWith("TDQ") || path.startsWith("metadata"))) {
            return true;
        }
        return false;
    }

    /**
     * 
     * Add qiongli: get the detail ERepositoryObjectType of folderItem.
     * 
     * @param folderItem
     * @return
     */
    public static ERepositoryObjectType getFolderContentType(FolderItem folderItem) {
        if (!folderItem.getType().equals(FolderType.SYSTEM_FOLDER_LITERAL)) {
            if (!(folderItem.getParent() instanceof FolderItem)) {
                return null; // appears only for a folder for expression builder !
            }
            return getFolderContentType((FolderItem) folderItem.getParent());
        }
        for (ERepositoryObjectType objectType : ERepositoryObjectType.values()) {
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
}
