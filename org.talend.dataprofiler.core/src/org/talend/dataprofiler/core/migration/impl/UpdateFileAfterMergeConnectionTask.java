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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * 
 * DOC zshen class global comment. Detailled comment Move Connection file form TDQ_Metadata to metadata
 */
public class UpdateFileAfterMergeConnectionTask extends AbstractWorksapceUpdateTask {

    private static Logger log = Logger.getLogger(UpdateFileAfterMergeConnectionTask.class);

    private Map<String, String> replaceStringMap;

    private ResourceSet resourceSet = new ResourceSetImpl();

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
                if (isWorksapcePath()) {
                    tansferFile(folder);
                } else {
                    if (folder.exists()) {
                        FileUtils.copyDirectory(folder, folderMap.get(folder));
                    }
                }
            } catch (Exception e) {
                log.error(e, e);
            }

        }

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

            AnaResourceFileHelper.getInstance().clear();
            ResourceService.refreshStructure();
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
            folderMap.put(srcDBFolder, null);

            if (!ResourceManager.getMDMConnectionFolder().exists()) {
                ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA, Path.EMPTY,
                        EResourceConstant.MDM_CONNECTIONS.getName());
            }
            folderMap.put(srcMDMFolder, null);
        } else {
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
        }

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

    private void tansferFile(File parentFolder) throws Exception {

        if (!parentFolder.exists()) {
            return;
        }

        List<File> fileList = new ArrayList<File>();

        FilesUtils.getAllFilesFromFolder(parentFolder, fileList, nonPropertyFileFilter);

        for (File propFile : fileList) {

            URI uri = URI.createFileURI(propFile.getAbsolutePath());

            Resource resource = resourceSet.getResource(uri, true);

            Property property = (Property) EcoreUtil.getObjectByType(resource.getContents(), PropertiesPackage.eINSTANCE
                    .getProperty());

            if (property != null) {
                Item item = property.getItem();
                if (item instanceof ConnectionItem) {
                    String fileName = propFile.getName();
                    int lastIndex = fileName.lastIndexOf("_");
                    if (lastIndex > 0) {
                        fileName = fileName.substring(0, lastIndex);
                    } else {
                        fileName = new Path(fileName).removeFileExtension().lastSegment();
                    }

                    property.setLabel(fileName);
                }

                IPath path = new Path(item.getState().getPath());
                if (ProxyRepositoryFactory.getInstance().getFolderItem(ProjectManager.getInstance().getCurrentProject(),
                        ERepositoryObjectType.getItemType(item), path) == null) {
                    ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.getItemType(item),
                            path.removeLastSegments(1), path.lastSegment());
                }
                ProxyRepositoryFactory.getInstance().create(item, path, true);
            }
        }

    }
}
