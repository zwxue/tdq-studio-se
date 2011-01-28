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
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.repository.ProjectManager;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * 
 * DOC zshen class global comment. Detailled comment Move Connection file form TDQ_Metadata to metadata
 */
public class UpdateFileAfterMergeConnectionTask extends AbstractWorksapceUpdateTask {

    private static Logger log = Logger.getLogger(UpdateFileAfterMergeConnectionTask.class);

    private Map<String, String> replaceStringMap;

    private ResourceSet resourceSet;

    private FilenameFilter nonPropertyFileFilter = new FilenameFilter() {

        public boolean accept(File dir, String name) {
            return name.endsWith(FactoriesUtil.PROPERTIES_EXTENSION);
        }
    };

    public Map<String, String> getReplaceStringMap() {
        if (this.replaceStringMap == null) {
            this.replaceStringMap = initReplaceStringMap();
        }
        return this.replaceStringMap;
    }

    public UpdateFileAfterMergeConnectionTask() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean doExecute() throws Exception {
        boolean result = true;

        Map<File, File> folderMap = initStructure();

        for (File folder : folderMap.keySet()) {
            try {
                // Move the content of connection folder
                tansferFile(folder, folderMap);
            } catch (Exception e) {
                log.error(e, e);
            }

        }

        // ADD xqliu 2010-12-21 bug 17704
        // copy .xsd folder to new location
        File srcMdmXsdFolder = getWorkspacePath().append(
                ExchangeFileNameToReferenceTask.MDM_CONNECTION + IPath.SEPARATOR + ".xsd").toFile();//$NON-NLS-1$
        File tarMdmXsdFolder = getWorkspacePath().append(
                ERepositoryObjectType.getFolderName(ERepositoryObjectType.METADATA_MDMCONNECTION) + IPath.SEPARATOR + ".xsd")//$NON-NLS-1$
                .toFile();
        FilesUtils.copyFolder(srcMdmXsdFolder, tarMdmXsdFolder, true, null, null, true, null);
        // update MDMConnection XSDPath
        File mdmConnectionFolder = new File(ResourceManager.getMDMConnectionFolder().getRawLocationURI());
        final String[] metadataFileExtentionNames = { ".item" };//$NON-NLS-1$
        UpdateMDMConnectionXSDPathTask.doUpdate(true, true, mdmConnectionFolder, metadataFileExtentionNames);
        // reload the mdm connection
        ArrayList<File> fileList = new ArrayList<File>();
        FilesUtils.getAllFilesFromFolder(mdmConnectionFolder, fileList, new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (name.endsWith("item")) {//$NON-NLS-1$
                    return true;
                }
                return false;
            }
        });
        for (File file : fileList) {
            TypedReturnCode<Connection> findProvider = PrvResourceFileHelper.getInstance().findProvider(
                    WorkspaceUtils.fileToIFile(file));
            Connection connection = findProvider.getObject();
            connection.eResource().unload();
            EObjectHelper.resolveObject(connection);
        }
        // ~ 17704

        File tdqMetadataFile = getWorkspacePath().append(OLD_MEATADATA_FOLDER_NAME).toFile();
        if (tdqMetadataFile.exists()) {
            FileUtils.deleteDirectory(tdqMetadataFile);
        }

        // change the path which conation in analysis and dqrule.
        File fileAnalysis = getWorkspacePath().append(EResourceConstant.ANALYSIS.getPath()).toFile();
        File fileRule = getWorkspacePath().append(EResourceConstant.RULES.getPath()).toFile();
        try {
            String[] anaFileExtentionNames = { FactoriesUtil.ANA };
            String[] rulesFileEctentionNames = { FactoriesUtil.DQRULE };
            result &= FilesUtils.migrateFolder(fileAnalysis, anaFileExtentionNames, this.getReplaceStringMap(), log)
                    && FilesUtils.migrateFolder(fileRule, rulesFileEctentionNames, this.getReplaceStringMap(), log);

            // AnaResourceFileHelper.getInstance().clear();
            // AnaResourceFileHelper.getInstance().getAllAnalysis();
            // ResourceService.refreshStructure();
        } catch (Exception e) {
            result = false;
            log.error(e, e);
        }

        return result;

    }

    private Map<File, File> initStructure() throws Exception {
        Map<File, File> folderMap = new HashMap<File, File>();

        File srcDBFolder = getWorkspacePath().append(ExchangeFileNameToReferenceTask.DB_CONNECTION).toFile();
        File srcMDMFolder = getWorkspacePath().append(ExchangeFileNameToReferenceTask.MDM_CONNECTION).toFile();

        if (isWorksapcePath()) {
            if (!ResourceManager.getConnectionFolder().exists()) {
                ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA, Path.EMPTY,
                        EResourceConstant.DB_CONNECTIONS.getName());
            }
            if (!ResourceManager.getMDMConnectionFolder().exists()) {
                ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA, Path.EMPTY,
                        EResourceConstant.MDM_CONNECTIONS.getName());
            }
        }
        File file = getWorkspacePath().append(EResourceConstant.METADATA.getPath()).toFile();
        if (!file.exists()) {
            file.mkdir();
        }

        file = getWorkspacePath().append(EResourceConstant.DB_CONNECTIONS.getPath()).toFile();
        if (!file.exists()) {
            file.mkdir();
        }
        folderMap.put(srcDBFolder, file);

        file = getWorkspacePath().append(EResourceConstant.MDM_CONNECTIONS.getPath()).toFile();
        if (!file.exists()) {
            file.mkdir();
        }
        folderMap.put(srcMDMFolder, file);

        return folderMap;
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    public Date getOrder() {
        return createDate(2010, 8, 16);
    }

    private Map<String, String> initReplaceStringMap() {
        Map<String, String> result = new HashMap<String, String>();
        result.put("TDQ_Metadata/DB Connections", EResourceConstant.DB_CONNECTIONS.getPath());
        result.put("TDQ_Metadata/MDM Connections", EResourceConstant.MDM_CONNECTIONS.getPath());
        result.put(".prv", ".item");

        return result;
    }

    private void tansferFile(File parentFolder, Map<File, File> folderMap) throws Exception {
        resourceSet = new ResourceSetImpl();

        if (!parentFolder.exists()) {
            return;
        }

        List<File> fileList = new ArrayList<File>();

        FilesUtils.getAllFilesFromFolder(parentFolder, fileList, nonPropertyFileFilter);

        for (File propFile : fileList) {
            try {
                handlePropertiesFile(propFile, folderMap, parentFolder);
            } catch (Exception e) {
                log.warn(e, e);
            }
        }

    }

    private void handlePropertiesFile(File propFile, Map<File, File> folderMap, File parentFolder) throws PersistenceException,
            IOException {
        URI uri = URI.createFileURI(propFile.getAbsolutePath());

        Resource resource = resourceSet.getResource(uri, true);

        Property property = (Property) EcoreUtil.getObjectByType(resource.getContents(), PropertiesPackage.eINSTANCE
                .getProperty());

        if (property != null) {
            File targetFolder = folderMap.get(parentFolder);
            Item item = property.getItem();
            String connNameBofore = null;
            String connNameAfter = null;

            IPath path = new Path(item.getState().getPath());
            if (ProxyRepositoryFactory.getInstance().getFolderItem(ProjectManager.getInstance().getCurrentProject(),
                    ERepositoryObjectType.getItemType(item), path) == null) {
                ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.getItemType(item),
                        path.removeLastSegments(1), path.lastSegment());
            }
            if (path == null) {
                path = Path.EMPTY;
            }
            String connName = null;

            if (item instanceof ConnectionItem) {
                Connection conn = ((ConnectionItem) item).getConnection();
                connNameBofore = conn.eResource().getURI().trimFileExtension().lastSegment();

                String version = property.getVersion();
                if (version == null) {
                    version = "0.1";
                }

                String label = conn.getName() + "_" + version;
                connNameAfter = label;

                File destItemFile = new Path(targetFolder.getAbsolutePath()).append(path).append(connNameAfter)
                        .addFileExtension(FactoriesUtil.ITEM_EXTENSION).toFile();
                File destPropFile = new Path(targetFolder.getAbsolutePath()).append(path).append(connNameAfter)
                        .addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION).toFile();
                if (destItemFile.exists()) {
                    int num = 0;

                    // ~
                    while (destItemFile.exists() || destPropFile.exists()) {

                        connName = conn.getName() + String.valueOf(++num);
                        connNameAfter = connName + "_" + version;
                        destItemFile = new Path(destItemFile.getPath()).removeLastSegments(1).append(connNameAfter)
                                .addFileExtension(FactoriesUtil.ITEM_EXTENSION).toFile();
                        destPropFile = new Path(destPropFile.getPath()).removeLastSegments(1).append(connNameAfter)
                                .addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION).toFile();
                    }
                    conn.setName(connName);
                    conn.setLabel(connName);
                    property.setLabel(connName);

                    // EMFUtil.saveResource(itemResource);
                }
                String relationPropPath = ReponsitoryContextBridge.getRootProject().getFullPath()
                        .append(new Path(destPropFile.getPath()).makeRelativeTo(this.getWorkspacePath())).toOSString();
                MetadataHelper.setPropertyPath(relationPropPath, conn);
            }

            if (isWorksapcePath()) {

                ProxyRepositoryFactory.getInstance().create(item, path, true);

            } else {
                connNameAfter = copyFile(targetFolder, propFile, property, path, connNameAfter, folderMap);
            }

            if (connNameBofore != null && connNameAfter != null) {
                getReplaceStringMap().put(connNameBofore, connNameAfter);
            }
        }
    }

    private String copyFile(File targetFolder, File propFile, Property property, IPath path, String connNameAfter,
            Map<File, File> folderMap) throws IOException {

        String connName = null;

        File destItemFile = new Path(targetFolder.getAbsolutePath()).append(path).append(connNameAfter).addFileExtension(
                FactoriesUtil.ITEM_EXTENSION).toFile();
        File destPropFile = new Path(targetFolder.getAbsolutePath()).append(path).append(connNameAfter).addFileExtension(
                FactoriesUtil.PROPERTIES_EXTENSION).toFile();

        File srcItemFile = new Path(propFile.getAbsolutePath()).removeFileExtension().addFileExtension(
                FactoriesUtil.ITEM_EXTENSION).toFile();
        File srcPropFile = propFile;
        Item item = property.getItem();
        // MOD by zshen to resolve repetitious name question(any time it should't come in here)
        if (destItemFile.exists()) {
            int num = 0;

            if (item instanceof ConnectionItem) {

                Resource itemResource = getResource(srcItemFile.getAbsolutePath());
                Connection conn = null;
                for (EObject object : itemResource.getContents()) {
                    if (object instanceof Connection) {
                        conn = (Connection) object;
                        connName = conn.getName();

                    }
                }

                // ~
                while (destItemFile.exists() || destPropFile.exists()) {
                    String version = property.getVersion();
                    if (version == null) {
                        version = "0.1";
                    }

                    connName = connName + String.valueOf(++num);
                    conn.setName(connName);
                    connNameAfter = connName + "_" + version;
                    destItemFile = new Path(destItemFile.getPath()).removeLastSegments(1).append(connNameAfter)
                            .addFileExtension(FactoriesUtil.ITEM_EXTENSION).toFile();
                    destPropFile = new Path(destPropFile.getPath()).removeLastSegments(1).append(connNameAfter)
                            .addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION).toFile();
                    // TODO change connection property path

                }
                // EMFUtil.saveResource(itemResource);
            }
        }
        FileUtils.copyFile(srcItemFile, destItemFile);
        FileUtils.copyFile(srcPropFile, destPropFile);

        if (item instanceof ConnectionItem) {
            ConnectionItem connectionItem = (ConnectionItem) item;
            Resource itemResource = getResource(destItemFile.getAbsolutePath());
            Connection conn = null;
            for (EObject object : itemResource.getContents()) {
                if (object instanceof Connection) {
                    conn = (Connection) object;
                    if (connName != null) {
                        conn.setName(connName);
                        conn.setLabel(connName);
                    }
                    String relationPropPath = ReponsitoryContextBridge.getRootProject().getFullPath()
                            .append(new Path(destPropFile.getPath()).makeRelativeTo(this.getWorkspacePath())).toOSString();
                    MetadataHelper.setPropertyPath(relationPropPath, conn);
                    connectionItem.setConnection(conn);

                }
            }
            EMFUtil.saveResource(itemResource);

            Resource propResource = getResource(destPropFile.getAbsolutePath());

            Property newProperty = (Property) EcoreUtil.getObjectByType(propResource.getContents(),
                    PropertiesPackage.eINSTANCE
                    .getProperty());
            newProperty.setAuthor(property.getAuthor());
            newProperty.setLabel(connectionItem.getConnection().getName());
            newProperty.setItem(item);
            // String propertyPath = String.valueOf(Path.SEPARATOR)
            // + (new
            // Path(destPropFile.getPath()).makeRelativeTo(this.getWorkspacePath().removeLastSegments(1)).toString());
            // MetadataHelper.setPropertyPath(propertyPath, connectionItem.getConnection());
            item.setProperty(newProperty);

            propResource.getContents().clear();

            propResource.getContents().add(newProperty);
            propResource.getContents().add(item);
            propResource.getContents().add(item.getState());

            EMFUtil.saveResource(propResource);

        }
        return connNameAfter;
    }

    private Resource getResource(String filePath) {
        URI uri = URI.createFileURI(filePath);

        Resource resource = resourceSet.getResource(uri, true);

        return resource;
    }
}
