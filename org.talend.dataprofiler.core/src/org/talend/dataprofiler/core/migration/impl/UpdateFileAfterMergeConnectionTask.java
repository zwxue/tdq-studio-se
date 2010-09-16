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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.repository.utils.ResourceFilenameHelper;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * 
 * DOC zshen class global comment. Detailled comment Move Connection file form TDQ_Metadata to metadata
 */
public class UpdateFileAfterMergeConnectionTask extends AbstractWorksapceUpdateTask {

    private static Logger log = Logger.getLogger(UpdateFileAfterMergeConnectionTask.class);

    private static final String TDQ_METADATA = "TDQ_Metadata";

    private Map<String, String> replaceStringMap;

    private HashMap<String, String> fileNameMap = new HashMap<String, String>();

    private ResourceSet resourceSet = new ResourceSetImpl();

    private FilenameFilter nonPropertyFileFilter = new FilenameFilter() {

        public boolean accept(File dir, String name) {
            return name.endsWith(FactoriesUtil.PROPERTIES_EXTENSION);
        }
    };

    public Map<String, String> getReplaceStringMap() {
        if (this.replaceStringMap == null) {
            this.replaceStringMap = initReplaceStringMap();
            // this.replaceStringMap.putAll(fileNameMap);
        }
        return this.replaceStringMap;
    }

    public UpdateFileAfterMergeConnectionTask() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean doExecute() throws Exception {
        boolean result = true;

        initStructure();

        List<File> mergeFolders = new ArrayList<File>();
        mergeFolders.add(getWorkspacePath().append(ExchangeFileNameToReferenceTask.DB_CONNECTION).toFile());
        mergeFolders.add(getWorkspacePath().append(ExchangeFileNameToReferenceTask.MDM_CONNECTION).toFile());

        for (File theFile : mergeFolders) {
            try {
                // Move the content of connection folder
                tansferFile(theFile);
            } catch (Exception e) {
                log.error(e, e);
            }

        }

        File tdqMetadataFile = getWorkspacePath().append(TDQ_METADATA).toFile();
        if (tdqMetadataFile.exists()) {
            FileUtils.deleteDirectory(tdqMetadataFile);
        }

        // change the path which conation in analysis and dqrule.
        File fileAnalysis = new File(ResourceManager.getAnalysisFolder().getRawLocationURI());
        File fileRule = new File(ResourceManager.getRulesFolder().getRawLocationURI());
        try {
            String[] anaFileExtentionNames = { FactoriesUtil.ANA };
            String[] rulesFileEctentionNames = { FactoriesUtil.DQRULE };
            result &= FilesUtils.migrateFolder(fileAnalysis, anaFileExtentionNames, this.getReplaceStringMap(), log)
                    && FilesUtils.migrateFolder(fileRule, rulesFileEctentionNames, this.getReplaceStringMap(), log);

            AnaResourceFileHelper.getInstance().clear();
        } catch (Exception e) {
            result = false;
            log.error(e, e);
        }

        return result;

    }

    private void initStructure() throws Exception {
        ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA, Path.EMPTY,
                EResourceConstant.DB_CONNECTIONS.getName());
        ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA, Path.EMPTY,
                EResourceConstant.MDM_CONNECTIONS.getName());

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
                property.setLabel(uri.lastSegment().split("_")[0]);
                IPath path = new Path(item.getState().getPath());
                ProxyRepositoryFactory.getInstance().create(item, path, true);
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
