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
package org.talend.dq.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import org.talend.utils.ProductVersion;

/**
 * update the ItemStatePath of Pattern and UDI, make sure they are correct.
 */
public final class UpdatePatternUDIItemStatePathHelper {

    private static Logger log = Logger.getLogger(UpdatePatternUDIItemStatePathHelper.class);

    public static final String VERSION_FILE_PATH = ".version.txt"; //$NON-NLS-1$

    public static final String VERSION = "version"; //$NON-NLS-1$

    private static IPath workspacePath = ResourceManager.getRootProject().getLocation();

    private static ResourceSet tempResourceSet = new ResourceSetImpl();

    private static final String FLAG_FILE_NAME = ".path_updated"; //$NON-NLS-1$

    private static final ProductVersion REFERENCE_VERSION = ProductVersion.fromString("5.1.2"); //$NON-NLS-1$

    private UpdatePatternUDIItemStatePathHelper() {
    }

    /**
     * do the update action if it hasn't be executed yet.
     */
    public static void doUpdate() {
        if (checkVersion()) {
            if (!updated(ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS)) {
                update(ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS);
            }
            if (!updated(ERepositoryObjectType.TDQ_PATTERN_REGEX)) {
                update(ERepositoryObjectType.TDQ_PATTERN_REGEX);
            }
            if (!updated(ERepositoryObjectType.TDQ_PATTERN_SQL)) {
                update(ERepositoryObjectType.TDQ_PATTERN_SQL);
            }
        }
    }

    /**
     * check the current workspace version, decide to execute update action or not.
     * 
     * @return
     */
    private static boolean checkVersion() {
        return REFERENCE_VERSION.compareTo(getWorkspaceVesion()) > 0;
    }

    /**
     * have executed the update action or not.
     * 
     * @param type
     * @return
     */
    private static boolean updated(ERepositoryObjectType type) {
        boolean updated = false;
        IFolder folder = getFolder(type);
        if (folder == null) {
            updated = true;
        } else {
            File flagFile = WorkspaceUtils.ifileToFile(folder.getFile(FLAG_FILE_NAME));
            if (flagFile.exists()) {
                updated = true;
            }
        }
        return updated;
    }

    /**
     * execute the update action.
     * 
     * @param type
     */
    private static void update(ERepositoryObjectType type) {
        IFolder folder = getFolder(type);

        if (folder != null) {
            if (updateItemStatePath(folder, type)) {
                createFlagFile(folder);
            }
        }
    }

    /**
     * DOC xqliu Comment method "getFolder".
     * 
     * @param type
     * @return
     */
    private static IFolder getFolder(ERepositoryObjectType type) {
        IFolder folder = null;
        if (ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS.equals(type)) {
            folder = ResourceManager.getUDIFolder();
        } else if (ERepositoryObjectType.TDQ_PATTERN_REGEX.equals(type)) {
            folder = ResourceManager.getPatternRegexFolder();
        } else if (ERepositoryObjectType.TDQ_PATTERN_SQL.equals(type)) {
            folder = ResourceManager.getPatternSQLFolder();
        }
        return folder;
    }

    /**
     * create flag file under the folder.
     * 
     * @param folder
     * @return
     */
    private static boolean createFlagFile(IFolder folder) {
        boolean created = false;
        try {
            File flagFile = WorkspaceUtils.ifileToFile(folder.getFile(FLAG_FILE_NAME));
            if (!flagFile.exists()) {
                created = flagFile.createNewFile();
            }
        } catch (IOException e) {
            log.error(e, e);
            created = false;
        }
        return created;
    }

    /**
     * DOC xqliu Comment method "updateItemStatePath".
     * 
     * @param folder
     * @param type
     * @return
     */
    private static boolean updateItemStatePath(IFolder folder, ERepositoryObjectType type) {
        boolean result = true;
        try {
            for (IResource resource : folder.members()) {
                if (resource instanceof IFolder) {
                    IFolder subFolder = (IFolder) resource;
                    updateItemStatePath(subFolder, type);
                }

                if (resource instanceof IFile) {
                    IFile propFile = (IFile) resource;
                    if (FactoriesUtil.PROPERTIES_EXTENSION.equals(propFile.getFileExtension())) {
                        Property property = PropertyHelper.getProperty(propFile);
                        if (property != null) {
                            ItemState itemState = property.getItem().getState();
                            if (itemState != null) {
                                String itemStatePath1 = itemState.getPath();
                                String itemStatePath2 = getItemStatePath(propFile, type);
                                if (!itemStatePath1.equals(itemStatePath2)) {
                                    itemState.setPath(itemStatePath2);
                                    Resource propResource = getResource(WorkspaceUtils.ifileToFile(propFile));
                                    EList<EObject> contents = propResource.getContents();
                                    for (EObject eObject : contents) {
                                        if (eObject instanceof ItemState) {
                                            eObject = itemState;
                                        }
                                    }
                                    EMFUtil.saveResource(propResource);
                                }
                            }
                        }
                    }
                }
            }
        } catch (CoreException e) {
            log.error(e, e);
            result = false;
        }
        return result;
    }

    /**
     * DOC xqliu Comment method "getItemStatePath".
     * 
     * @param propFile
     * @param type
     * @return
     */
    private static String getItemStatePath(IFile propFile, ERepositoryObjectType type) {
        IPath basePath = Path.fromOSString(type.getFolder()).removeLastSegments(1);
        IPath folderPath = propFile.getParent().getFullPath().removeFirstSegments(1);
        return folderPath.removeFirstSegments(basePath.segmentCount()).toString();
    }

    /**
     * DOC xqliu Comment method "getResource".
     * 
     * @param file
     * @return
     */
    private static Resource getResource(File file) {
        URI uri = createURI(file);
        Resource resource = getResourceSet().getResource(uri, true);
        return resource;
    }

    /**
     * DOC xqliu Comment method "createURI".
     * 
     * @param file
     * @return
     */
    private static URI createURI(File file) {
        IFile ifile = WorkspaceUtils.fileToIFile(file);
        if (ifile != null && ifile.exists()) {
            return URI.createPlatformResourceURI(ifile.getFullPath().toString(), false);
        } else {
            return URI.createFileURI(file.getAbsolutePath());
        }
    }

    /**
     * DOC xqliu Comment method "getResourceSet".
     * 
     * @return
     */
    private static ResourceSet getResourceSet() {
        return isWorksapcePath() ? EMFSharedResources.getInstance().getResourceSet() : tempResourceSet;
    }

    /**
     * DOC xqliu Comment method "isWorksapcePath".
     * 
     * @return
     */
    private static boolean isWorksapcePath() {
        return workspacePath.equals(ResourceManager.getRootProject().getLocation());
    }

    /**
     * DOC xqliu Comment method "getWorkspaceVesion".
     * 
     * @return
     */
    public static ProductVersion getWorkspaceVesion() {
        ProductVersion pVersion = null;

        File versionFile = WorkspaceUtils.ifileToFile(ResourceManager.getLibrariesFolder().getFile(VERSION_FILE_PATH));

        try {
            if (versionFile != null && versionFile.exists()) {
                FileInputStream inStream = new FileInputStream(versionFile);

                Properties pros = new Properties();
                pros.load(inStream);

                String version = pros.getProperty(VERSION);
                if (version != null && !"".equals(version)) { //$NON-NLS-1$
                    pVersion = ProductVersion.fromString(version);
                }

                inStream.close();
            } else {
                pVersion = new ProductVersion(0, 0, 0);
            }
        } catch (Exception e) {
            log.error(e, e);
        }

        return pVersion;
    }
}
