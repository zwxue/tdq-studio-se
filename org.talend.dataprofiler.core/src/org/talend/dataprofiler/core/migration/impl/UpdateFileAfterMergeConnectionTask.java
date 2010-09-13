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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dq.helper.PropertyHelper;
import org.talend.repository.utils.ResourceFilenameHelper;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * 
 * DOC zshen class global comment. Detailled comment Move Connection file form TDQ_Metadata to metadata
 */
public class UpdateFileAfterMergeConnectionTask extends AWorkspaceTask {

    private static Logger log = Logger.getLogger(UpdateFileAfterMergeConnectionTask.class);

    private Map<String, String> replaceStringMap;

    private IFolder dbFolder = ResourceManager.getConnectionFolder();

    private IFolder mdmFolder = ResourceManager.getMDMConnectionFolder();

    private HashMap<String, String> fileNameMap = new HashMap<String, String>();

    private List<File> mergeFolders = new ArrayList<File>();

    public Map<String, String> getReplaceStringMap() {
        if (this.replaceStringMap == null) {
            this.replaceStringMap = initReplaceStringMap();
            this.replaceStringMap.putAll(fileNameMap);
        }
        return this.replaceStringMap;
    }

    /**
     * DOC bZhou Comment method "addMergeFolder".
     * 
     * Add a folder to list to do merge.
     * 
     * @param file
     */
    public void addMergeFolder(File file) {
        mergeFolders.add(file);
    }

    public UpdateFileAfterMergeConnectionTask() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean doExecute() throws Exception {
        boolean result = true;
        // Move the content of connection folder

        IFolder tDQDbFolder = ResourceManager.getRootProject().getFolder(new Path(ExchangeFileNameToReferenceTask.DB_CONNECTION));
        IFolder tDQMdmFolder = ResourceManager.getRootProject().getFolder(
                new Path(ExchangeFileNameToReferenceTask.MDM_CONNECTION));
        if (mergeFolders.isEmpty()) {
            mergeFolders.add(ResourceManager.getRootProject().getFolder(new Path(ExchangeFileNameToReferenceTask.DB_CONNECTION))
                    .getLocation().toFile());
            mergeFolders.add(ResourceManager.getRootProject().getFolder(new Path(ExchangeFileNameToReferenceTask.MDM_CONNECTION))
                    .getLocation().toFile());
        }
        for (File theFile : mergeFolders) {
            try {
                tansferFile(theFile);
            } catch (CoreException e) {
                log.error(e, e);
            } catch (IOException e) {
                log.error(e, e);
            }

        }

        if (tDQDbFolder.exists() || tDQMdmFolder.exists()) {
            tDQDbFolder.getParent().delete(true, null);

        }
        // change the path which conation in analysis and dqrule.
        File fileAnalysis = new File(ResourceManager.getAnalysisFolder().getRawLocationURI());
        File fileRule = new File(ResourceManager.getRulesFolder().getRawLocationURI());
        try {
            String[] anaFileExtentionNames = { "." + FactoriesUtil.ANA };
            String[] rulesFileEctentionNames = { "." + FactoriesUtil.DQRULE };
            result &= FilesUtils.migrateFolder(fileAnalysis, anaFileExtentionNames, this.getReplaceStringMap(), log)
                    && FilesUtils.migrateFolder(fileRule, rulesFileEctentionNames, this.getReplaceStringMap(), log);

        } catch (Exception e) {
            result = false;
            log.error(e, e);
        }

        return result;

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

        return result;
    }

    private boolean deleteOldFile(File theResource) throws CoreException {
        boolean result = false;

        if (theResource.getName().endsWith(FactoriesUtil.PROV)) {
            result = true;// theResource is prv file delete it and return true.
        } else if (theResource.getName().endsWith(FactoriesUtil.PROPERTIES_EXTENSION)) {

            Property property = PropertyHelper.getProperty(theResource);
            if (property.getItem() instanceof TDQItem) {

                this.fileNameMap.put(((TDQItem) property.getItem()).getFilename() + "#" + property.getId(),
                        ResourceFilenameHelper.getExpectedFileName(property.getLabel(), property.getVersion()) + "#/0");
                result = true;// theResource is old properties file delete it and return true.
            }

        } else {
            // theResource isn't prv or old properties file,so don't delete it and return false.
        }
        if (result) {
            theResource.delete();
        }
        return result;
    }

    /**
     * This method is used for coping file from one place to the other.
     * 
     * @param srcFilePath
     * @param destFilePath
     * @throws IOException
     * @throws IOException in case some problems occured
     */
    private void copyFiles(String srcFilePath, String destFilePath) throws IOException {
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new FileInputStream(srcFilePath);
            output = new FileOutputStream(destFilePath);
            byte[] bytearray = new byte[512];
            int len = 0;
            while ((len = input.read(bytearray)) != -1) {
                output.write(bytearray, 0, len);
            }
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }

    private List<File> iteratorResource(File theFolder) {
        List<File> fileList = new ArrayList<File>();
        for (File subFile : theFolder.listFiles()) {
            if (subFile.isFile() && isPropertyFile(subFile)) {
                fileList.add(subFile);
            } else if (subFile.isDirectory()) {
                fileList.addAll(iteratorResource(subFile));
            }
        }
        return fileList;
    }

    private void tansferFile(File currentFile) throws CoreException, IOException {
        for (File theResource : currentFile.listFiles()) {
            if (theResource.isFile()) {
                File desFile = null;
                if (theResource == null || deleteOldFile(theResource)) {
                    continue;
                }
                if (dbFolder.exists()
                        && theResource.getAbsolutePath().contains(
                                ExchangeFileNameToReferenceTask.DB_CONNECTION.replace('/', '\\'))) {
                    String relationName = ExchangeFileNameToReferenceTask.DB_CONNECTION.split("/")[1];
                    desFile = dbFolder.getLocation().append(
                            theResource.getAbsolutePath().substring(
                                    theResource.getAbsolutePath().indexOf(relationName) + relationName.length())).toFile();

                } else if (mdmFolder.exists()
                        && theResource.getAbsolutePath().contains(
                                ExchangeFileNameToReferenceTask.MDM_CONNECTION.replace('/', '\\'))) {
                    String relationName = ExchangeFileNameToReferenceTask.MDM_CONNECTION.split("/")[1];
                    desFile = mdmFolder.getLocation().append(
                            theResource.getAbsolutePath().substring(
                                    theResource.getAbsolutePath().indexOf(relationName) + relationName.length())).toFile();
                }
                if (desFile == null) {
                    continue;
                }
                if (!desFile.exists()) {

                    if (desFile.getParentFile().exists() || desFile.getParentFile().mkdirs()) {
                        desFile.createNewFile();
                    }
                }
                copyFiles(theResource.getAbsolutePath(), desFile.getAbsolutePath());
                theResource.delete();
            } else if (theResource.isDirectory()) {
                tansferFile(theResource);
                theResource.delete();
            }

        }

    }

    private boolean isPropertyFile(File propertyFile) {
        if (propertyFile.isFile()
                && propertyFile.getName().toLowerCase().endsWith("." + FactoriesUtil.PROPERTIES_EXTENSION.toLowerCase())) {
            return true;
        }
        return false;
    }
}
