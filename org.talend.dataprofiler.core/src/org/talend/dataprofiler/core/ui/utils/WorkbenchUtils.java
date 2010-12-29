// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
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

    public static IFolder folder2IFolder(Folder folder) {
        IProject rootProject = ResourceManager.getRootProject();
        return rootProject.getFolder(folder.getPath());
    }

    public static IPath getPath(RepositoryNode node) {
        if (node == null) {
            return null;
        }
        if (node.isBin()) {
            return new Path(""); //$NON-NLS-1$
        }
        switch (node.getType()) {
        case SYSTEM_FOLDER:
            ERepositoryObjectType contentType = node.getContentType();
            if (contentType == null) {
                Item item = node.getObject().getProperty().getItem();
                contentType = ERepositoryObjectType.getItemType(item);
            }
            return new Path(ERepositoryObjectType.getFolderName(contentType)); //$NON-NLS-1$
        case SIMPLE_FOLDER:
            String label = node.getObject().getProperty().getLabel();
            return getPath(node.getParent()).append(label);
        default:
        }
        return getPath(node.getParent());
    }

    public static IPath getPath(RepositoryViewObject viewObject) {
        return getPath((RepositoryNode) viewObject.getRepositoryNode());
    }

    public static IFolder getFolder(RepositoryNode node) {
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
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_PATTERN_ITEM:
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

    public static IPath getFilePath(RepositoryNode node) {
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
}
