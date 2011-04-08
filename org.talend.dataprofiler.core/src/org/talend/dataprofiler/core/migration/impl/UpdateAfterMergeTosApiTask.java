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
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.commons.utils.platform.PluginChecker;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataprofiler.core.ui.action.actions.handle.JrxmlHandle;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.utils.io.FilesUtils;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * update TDQ_Data Profiling and TDQ_Libraries folders after use same api with TOS(metadata folder has been updated
 * before).
 */
public class UpdateAfterMergeTosApiTask extends AbstractWorksapceUpdateTask {

    private static Logger log = Logger.getLogger(UpdateAfterMergeTosApiTask.class);

    private static final String MIGRATION_FILE_EXT = ".mig";//$NON-NLS-1$

    private static final String MIGRATION_FOLDER_EXT = "_mig";//$NON-NLS-1$

    private static final String TDQ_DATAPROFILING = "TDQ_Data Profiling";//$NON-NLS-1$

    private static final String TDQ_LIBRARIES = "TDQ_Libraries";//$NON-NLS-1$

    private static final String SOFTWARE_SYSTEM_FILE = ".softwaresystem.softwaredeployment";//$NON-NLS-1$

    private static final String DOT = ".";//$NON-NLS-1$

    private ProxyRepositoryFactory prfInstance;

    private File systemIndicatorMigFolder;

    private File userDefinedIndicatorMigFolder;

    private Map<String, String> replaceStringMapIndicators;

    private File patternRegexMigFolder;

    private File patternSqlMigFolder;

    private Map<String, String> replaceStringMapPatterns;

    private File ruleMigFolder;

    private Map<String, String> replaceStringMapRules;

    private File sourceFileMigFolder;

    private File jrxmlTemplateMigFolder;

    private Map<String, String> replaceStringMapJrxmlTemplates;

    private File analysisMigFolder;

    private Map<String, String> replaceStringMapAnalysis;

    private File reportMigFolder;

    private Map<String, String> replaceStringMapReports;

    private Map<String, String> replaceStringMapCommonFolders;

    // MOD klliu 2011-03-17 bug 19085
    private Map<String, String> replaceDefnitionNameInAnaMap;

    // MOD zshen 2011-03-29 to resolve windows filename can't contain "< > : " ? | \ /" charactor.
    private static final boolean WINDOWS = java.io.File.separatorChar == '\\';

    public UpdateAfterMergeTosApiTask() {
        init();
    }

    private void init() {
        if (!CorePlugin.getDefault().isRepositoryInitialized()) {
            CorePlugin.getDefault().initProxyRepository();
        }
        this.prfInstance = ProxyRepositoryFactory.getInstance();

        this.replaceStringMapIndicators = new HashMap<String, String>();
        this.replaceStringMapPatterns = new HashMap<String, String>();
        this.replaceStringMapRules = new HashMap<String, String>();
        this.replaceStringMapJrxmlTemplates = new HashMap<String, String>();
        this.replaceStringMapAnalysis = new HashMap<String, String>();
        this.replaceDefnitionNameInAnaMap = new HashMap<String, String>();
        this.replaceStringMapReports = new HashMap<String, String>();
        // common folder(TDQ_DATAPROFILING, TDQ_LIBRARIES) need to be replace also
        this.replaceStringMapCommonFolders = new HashMap<String, String>();
        replaceStringMapCommonFolders.put(TDQ_DATAPROFILING + MIGRATION_FILE_EXT, TDQ_DATAPROFILING);
        replaceStringMapCommonFolders.put(TDQ_LIBRARIES + MIGRATION_FILE_EXT, TDQ_LIBRARIES);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2011, 1, 20);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        boolean b1 = renameFolders(getWorkspacePath());
        boolean b2 = createNewFolders();
        boolean b3 = updateLibrariesFolder();
        boolean b4 = updateDataProfilingFolder();
        boolean b5 = replaceFileName();
        boolean b6 = deleteMigFolders(getWorkspacePath());
        boolean b7 = reloadModelElementCache();
        return b1 && b2 && b3 && b4 && b5 && b6 && b7;
    }

    /**
     * clear the cache of ModelElement.
     * 
     * @return
     */
    private boolean reloadModelElementCache() {
        try {
            // 1) clear and unload element
            AnaResourceFileHelper.getInstance().clear();
            RepResourceFileHelper.getInstance().clear();
            IndicatorResourceFileHelper.getInstance().clear();
            PatternResourceFileHelper.getInstance().clear();
            DQRuleResourceFileHelper.getInstance().clear();
            PrvResourceFileHelper.getInstance().clear();
            // EMFSharedResources.getInstance().unloadResources();
            // 2) reload from file
            ResourceFileMap.getAll();
        } catch (Exception e) {
            log.error(e, e);
            return false;
        }
        return true;
    }

    /**
     * delete mig folders.
     * 
     * @param basePath
     * @return
     */
    private boolean deleteMigFolders(IPath basePath) {
        IPath librariesPath = basePath.append(ResourceManager.getLibrariesFolder().getFullPath()
                .makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        IPath dataProfilingPath = basePath.append(ResourceManager.getDataProfilingFolder().getFullPath()
                .makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        File librariesFile = new File(librariesPath.toOSString() + MIGRATION_FOLDER_EXT);
        File dataProfilingFile = new File(dataProfilingPath.toOSString() + MIGRATION_FOLDER_EXT);
        boolean delete = true;
        boolean delete2 = true;
        if (librariesFile != null) {
            delete = FilesUtils.removeFolder(librariesFile, true);
        }
        if (dataProfilingFile != null) {
            delete2 = FilesUtils.removeFolder(dataProfilingFile, true);
        }
        ResourceService.refreshStructure();
        return delete && delete2;
    }

    /**
     * replace the file name which has been changed.
     * 
     * @return
     */
    private boolean replaceFileName() {
        boolean b1 = replaceFileName4Analysis();
        boolean b2 = replaceFileName4Report();
        boolean b3 = replaceFileName4Indicator();
        boolean b4 = replaceFileName4Pattern();
        boolean b5 = replaceFileName4Rule();
        ResourceService.refreshStructure();
        return b1 && b2 && b3 && b4 && b5;
    }

    /**
     * replace the Rule's file.
     * 
     * @return
     */
    private boolean replaceFileName4Rule() {
        // replace map
        Map<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.putAll(this.replaceStringMapCommonFolders);
        replaceMap.putAll(this.replaceStringMapIndicators);
        replaceMap.putAll(this.replaceStringMapAnalysis);
        replaceMap.putAll(this.replaceStringMapRules); // really need this ???
        // accept file extention name
        String[] acceptFileExtentionNames = { FactoriesUtil.DQRULE };

        return org.talend.commons.utils.io.FilesUtils.migrateFolder(new File(ResourceManager.getRulesFolder().getLocation()
                .toOSString()), acceptFileExtentionNames, replaceMap, log);

    }

    /**
     * replace the Pattern's file.
     * 
     * @return
     */
    private boolean replaceFileName4Pattern() {
        // replace map
        Map<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.putAll(this.replaceStringMapCommonFolders);
        replaceMap.putAll(this.replaceStringMapIndicators);
        replaceMap.putAll(this.replaceStringMapAnalysis);
        replaceMap.putAll(this.replaceStringMapPatterns); // really need this ???
        // accept file extention name
        String[] acceptFileExtentionNames = { FactoriesUtil.PATTERN };

        return org.talend.commons.utils.io.FilesUtils.migrateFolder(new File(ResourceManager.getPatternFolder().getLocation()
                .toOSString()), acceptFileExtentionNames, replaceMap, log);

    }

    /**
     * replace the Indicator's file.
     * 
     * @return
     */
    private boolean replaceFileName4Indicator() {
        // replace map
        Map<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.putAll(this.replaceStringMapCommonFolders);
        replaceMap.putAll(this.replaceStringMapAnalysis);
        replaceMap.putAll(this.replaceStringMapRules);
        replaceMap.putAll(this.replaceStringMapPatterns);
        replaceMap.putAll(this.replaceStringMapIndicators); // really need this ???
        // accept file extention name
        String[] acceptFileExtentionNames = { FactoriesUtil.DEFINITION };

        return org.talend.commons.utils.io.FilesUtils.migrateFolder(new File(ResourceManager.getIndicatorFolder().getLocation()
                .toOSString()), acceptFileExtentionNames, replaceMap, log);

    }

    /**
     * report the Report's file.
     * 
     * @return
     */
    private boolean replaceFileName4Report() {
        if (!PluginChecker.isOnlyTopLoaded()) {
            // replace map
            Map<String, String> replaceMap = new HashMap<String, String>();
            replaceMap.putAll(this.replaceStringMapCommonFolders);
            replaceMap.putAll(this.replaceStringMapAnalysis);
            replaceMap.putAll(this.replaceStringMapJrxmlTemplates);
            replaceMap.putAll(this.replaceStringMapReports); // really need this ???
            // accept file extention name
            String[] acceptFileExtentionNames = { FactoriesUtil.REP };

            return org.talend.commons.utils.io.FilesUtils.migrateFolder(new File(ResourceManager.getReportsFolder().getLocation()
                    .toOSString()), acceptFileExtentionNames, replaceMap, log);

        }
        return true;
    }

    /**
     * replace the Analysis's file.
     * 
     * @return
     */
    private boolean replaceFileName4Analysis() {
        // replace map
        Map<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.putAll(this.replaceStringMapCommonFolders);
        // MOD klliu 2011-03-17 bug 19085
        replaceMap.putAll(this.replaceDefnitionNameInAnaMap);
        replaceMap.putAll(this.replaceStringMapIndicators);
        replaceMap.putAll(this.replaceStringMapPatterns);
        replaceMap.putAll(this.replaceStringMapRules);
        replaceMap.putAll(this.replaceStringMapReports);
        replaceMap.putAll(this.replaceStringMapAnalysis); // really need this ???
        // accept file extention name
        String[] acceptFileExtentionNames = { FactoriesUtil.ANA };

        return org.talend.commons.utils.io.FilesUtils.migrateFolder(new File(ResourceManager.getAnalysisFolder().getLocation()
                .toOSString()), acceptFileExtentionNames, replaceMap, log);
    }

    /**
     * update files under TDQ_Data Profiling.
     * 
     * @return
     */
    private boolean updateDataProfilingFolder() {
        boolean result = true;
        try {
            analysisMigFolder = getMigSubFolder(TDQ_DATAPROFILING + MIGRATION_FOLDER_EXT, ResourceManager.getAnalysisFolder()
                    .getFullPath());
            updateModelElements(analysisMigFolder, FactoriesUtil.ANA, ResourceManager.getAnalysisFolder(), analysisMigFolder,
                    true, replaceStringMapAnalysis);

            if (!PluginChecker.isOnlyTopLoaded()) {
                reportMigFolder = getMigSubFolder(TDQ_DATAPROFILING + MIGRATION_FOLDER_EXT, ResourceManager.getReportsFolder()
                        .getFullPath());
                updateModelElements(reportMigFolder, FactoriesUtil.REP, ResourceManager.getReportsFolder(), reportMigFolder,
                        true, replaceStringMapReports);

                copyReportFolders(reportMigFolder,
                        getMigSubFolder(TDQ_DATAPROFILING, ResourceManager.getReportsFolder().getFullPath()));
            }
        } catch (Exception e) {
            log.error(e, e);
            result = false;
        } finally {
            ResourceService.refreshStructure();
        }
        return result;
    }

    /**
     * copy .xxx folders (which contains the generated docs for reports) from TDQ_Data Profiling_mig to TDQ_Data
     * Profiling.
     * 
     * @param source
     * @param target
     * @return
     */
    private boolean copyReportFolders(File source, File target) {
        boolean result = true;
        File[] listFiles = source.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                if (file.getName().startsWith(DOT)) {
                    try {
                        FilesUtils.copyFolder(file, getTargetFile(file), true, null, null, true);
                    } catch (IOException e) {
                        log.error(e, e);
                        result = false;
                    }
                } else {
                    copyReportFolders(file, getTargetFile(file));
                }
            }
        }
        return result;
    }

    /**
     * get the .xxx folder's target file.
     * 
     * @param file
     * @return
     */
    private File getTargetFile(File file) {
        return new File(file.toString().replaceAll(TDQ_DATAPROFILING + MIGRATION_FOLDER_EXT, TDQ_DATAPROFILING));
    }

    /**
     * update files under TDQ_Libraries.
     * 
     * @return
     */
    private boolean updateLibrariesFolder() {
        boolean result = true;
        try {
            systemIndicatorMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager
                    .getSystemIndicatorFolder().getFullPath());
            userDefinedIndicatorMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getUDIFolder()
                    .getFullPath());
            updateModelElements(systemIndicatorMigFolder, FactoriesUtil.DEFINITION, ResourceManager.getSystemIndicatorFolder(),
                    systemIndicatorMigFolder, false, replaceStringMapIndicators);
            updateModelElements(userDefinedIndicatorMigFolder, FactoriesUtil.DEFINITION, ResourceManager.getUDIFolder(),
                    userDefinedIndicatorMigFolder, true, replaceStringMapIndicators);

            patternRegexMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getPatternRegexFolder()
                    .getFullPath());
            patternSqlMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getPatternSQLFolder()
                    .getFullPath());
            updateModelElements(patternRegexMigFolder, FactoriesUtil.PATTERN, ResourceManager.getPatternRegexFolder(),
                    patternRegexMigFolder, true, replaceStringMapPatterns);
            updateModelElements(patternSqlMigFolder, FactoriesUtil.PATTERN, ResourceManager.getPatternSQLFolder(),
                    patternSqlMigFolder, true, replaceStringMapPatterns);

            ruleMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getRulesFolder().getFullPath());
            updateModelElements(ruleMigFolder, FactoriesUtil.DQRULE, ResourceManager.getRulesFolder(), ruleMigFolder, true,
                    replaceStringMapRules);

            sourceFileMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getSourceFileFolder()
                    .getFullPath());
            updateFiles(sourceFileMigFolder, FactoriesUtil.SQL, ResourceManager.getSourceFileFolder(), sourceFileMigFolder, true,
                    null);

            if (!PluginChecker.isOnlyTopLoaded()) {
                jrxmlTemplateMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getJRXMLFolder()
                        .getFullPath());
                updateFiles(jrxmlTemplateMigFolder, FactoriesUtil.JRXML, ResourceManager.getJRXMLFolder(),
                        jrxmlTemplateMigFolder, true, replaceStringMapJrxmlTemplates);
            }

            copySoftwareSystemFile();
        } catch (Exception e) {
            log.error(e, e);
            result = false;
        } finally {
            ResourceService.refreshStructure();
        }
        return result;
    }

    /**
     * copy .softwaresystem.softwaredeployment file from TDQ_Libraries_mig folder to new TDQ_Libraries folder..
     * 
     * @return
     */
    private boolean copySoftwareSystemFile() {
        try {
            File sourceFile = WorkspaceUtils.ifileToFile(ResourceManager.getRootProject()
                    .getFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT).getFile(SOFTWARE_SYSTEM_FILE));
            File targetFile = WorkspaceUtils.ifileToFile(ResourceManager.getLibrariesFolder().getFile(SOFTWARE_SYSTEM_FILE));
            FilesUtils.copyFile(sourceFile, targetFile);
            ResourceService.refreshStructure();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * update files (SourceFiles and JrxmlTemplates).
     * 
     * @param parentFile
     * @param fileExtension
     * @param baseFolder
     * @param baseFile
     * @param createSubFolder
     * @param replaceStringMap
     * @return
     */
    private boolean updateFiles(File parentFile, String fileExtension, IFolder baseFolder, File baseFile,
            boolean createSubFolder, Map<String, String> replaceStringMap) {
        try {
            if (!parentFile.isFile()) {
                boolean updateSubFolders = true;
                if (createSubFolder) {
                    // create sub folder
                    updateSubFolders = createSubFolder(parentFile, baseFolder);
                }
                if (updateSubFolders) {
                    File[] listFiles = parentFile.listFiles();
                    for (File file : listFiles) {
                        updateFiles(file, fileExtension, baseFolder, baseFile, createSubFolder, replaceStringMap);
                    }
                }
            } else {
                if (parentFile.getName().endsWith(fileExtension)) {
                    // get new file's full path folder
                    IFolder fullPathFolder = getFullPathFolder(baseFolder, baseFile, parentFile);
                    IPath filePath = getFileRelativePath(fullPathFolder.getFullPath(), fileExtension);
                    String label = parentFile.getName().substring(0, parentFile.getName().length() - fileExtension.length() - 1);
                    // create new file
                    createFile(parentFile, fileExtension, filePath, label);
                    // record the replace information
                    if (replaceStringMap != null) {
                        IPath replatePrefixPath = fullPathFolder.getFullPath().removeFirstSegments(1);
                        // replace name should begin with TDQ_Libraries (for element under TDQ_Libraries)
                        String oldFileNameFull = replatePrefixPath.append(parentFile.getName()).toString();
                        String newFileNameFull = replatePrefixPath.append(getDefaultNewFileName(label, fileExtension)).toString();
                        replaceStringMap.put(oldFileNameFull, newFileNameFull);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e, e);
            return false;
        }
        return true;
    }

    /**
     * create sub folder(ENodeType.SIMPLE_FOLDER).
     * 
     * @param parentFile
     * @param baseFolder
     * @return
     */
    private boolean createSubFolder(File parentFile, IFolder baseFolder) {
        boolean updateSubFolders = true;
        try {
            File rawFile = getRawFile(parentFile);
            String pathFolderName = rawFile.toString().substring(baseFolder.getLocation().toOSString().length());
            if (pathFolderName.length() > 0) {
                IPath path = Path.EMPTY;
                String folderName = ""; //$NON-NLS-1$

                IPath makeRelativeTo = baseFolder.getFolder(pathFolderName).getFullPath()
                        .makeRelativeTo(baseFolder.getFullPath());
                if (makeRelativeTo.segmentCount() == 1) {
                    folderName = pathFolderName;
                } else {
                    path = makeRelativeTo.removeLastSegments(1);
                    folderName = makeRelativeTo.segment(makeRelativeTo.segmentCount() - 1);
                }
                try {
                    this.prfInstance.createFolder(retrieveRepositoryObjectType(baseFolder), path, folderName);
                } catch (PersistenceException e) {
                    log.warn(e, e);
                    updateSubFolders = false;
                }
            }
        } catch (Exception e) {
            log.error(e, e);
            updateSubFolders = false;
        }
        return updateSubFolders;
    }

    /**
     * create file (SourceFile and JrxmlTemplate).
     * 
     * @param parentFile
     * @param fileExtension
     * @param filePath
     * @param label
     * @return
     */
    private boolean createFile(File parentFile, String fileExtension, IPath filePath, String label) {
        try {
            if (FactoriesUtil.JRXML.equals(fileExtension)) {
                JrxmlHandle.createJrxml(filePath, label, parentFile, fileExtension);
            } else if (FactoriesUtil.SQL.equals(fileExtension)) {
                DQStructureManager.getInstance().createSourceFileItem(parentFile, filePath, label, fileExtension);
            }
        } catch (Exception e) {
            log.error(e, e);
            return false;
        }
        return true;
    }

    /**
     * get the realteive path of SourceFile or JrxmlTemplate.
     * 
     * @param rawPath
     * @param fileExtension
     * @return
     */
    private IPath getFileRelativePath(IPath rawPath, String fileExtension) {
        IPath path = Path.EMPTY;
        try {
            if (FactoriesUtil.JRXML.equals(fileExtension)) {
                path = rawPath.makeRelativeTo(ResourceManager.getJRXMLFolder().getFullPath());
            } else if (FactoriesUtil.SQL.equals(fileExtension)) {
                path = rawPath.makeRelativeTo(ResourceManager.getSourceFileFolder().getFullPath());
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        return path;
    }

    /**
     * update ModelElement files (Analysis, Report, IndicatorDefinition, Pattern, Rule).
     * 
     * @param parentFile
     * @param fileExtension
     * @param baseFolder
     * @param baseFile
     * @param createSubFolder
     * @param replaceStringMap
     * @return
     */
    private boolean updateModelElements(File parentFile, String fileExtension, IFolder baseFolder, File baseFile,
            boolean createSubFolder, Map<String, String> replaceStringMap) {
        boolean isImport = true;
        try {
            if (!parentFile.isFile()) {
                boolean updateSubFolders = true;
                if (createSubFolder) {
                    updateSubFolders = createSubFolder(parentFile, baseFolder);
                }
                if (updateSubFolders) {
                    File[] listFiles = parentFile.listFiles();
                    for (File file : listFiles) {
                        updateModelElements(file, fileExtension, baseFolder, baseFile, createSubFolder, replaceStringMap);
                    }
                }
            } else {
                // if parentFile is file as ana/rep
                if (parentFile.getName().endsWith(fileExtension)) {
                    // get the ModelElement
                    ModelElement modelElement = getModelElement(parentFile);
                    if (modelElement != null) {
                        // get new file's full path folder
                        IFolder fullPathFolder = getFullPathFolder(baseFolder, baseFile, parentFile);
                        // MOD klliu 2011-03-17 bug 19085
                        // check is ana file ,if the definition's name is old,then replace it
                        if (fileExtension.equals(FactoriesUtil.ANA)) {
                            setReplaceChildNameForAna(modelElement);
                        }
                        // ~
                        String modelName = checkName(modelElement, fullPathFolder);
                        modelElement.setName(modelName);

                        // create new file
                        ElementWriterFactory.getInstance().createPatternWriter().create(modelElement, fullPathFolder, isImport);
                        // record the replace information
                        IPath replatePrefixPath = fullPathFolder.getFullPath().removeFirstSegments(2);
                        String oldFileNameFull = replatePrefixPath.append(parentFile.getName()).toString();
                        String newFileNameFull = replatePrefixPath.append(
                                getDefaultNewFileName(modelElement.getName(), fileExtension)).toString();
                        replaceStringMap.put(oldFileNameFull, newFileNameFull);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e, e);
            return false;
        }
        return true;
    }

    /**
     * 
     * zshen Comment method "checkName".
     * 
     * @param fileName
     * @return valid name under windows OS. to check whether fileName is adapt to windows OS and change add one after
     * the name if the name has been exist.
     */
    private String checkName(ModelElement modelElement, IFolder baseFolder) {

        if (modelElement == null || modelElement.eIsProxy() || modelElement.getName() == null) {
            return "";
        }
        IPath filepath = new Path(modelElement.eResource().getURI().toPlatformString(false));
        String fileName = modelElement.getName().toLowerCase();
        String[] arrayStr = filepath.toOSString().split("_");
        if(WINDOWS){
            char[] characterStrArray = { '<', '>', ':', '"', '?', '|', '\\', '/', ' ' };
            for (char replaceChar : characterStrArray) {
                fileName = fileName.replace(replaceChar, '_');
            }
        }
        if (fileName.equalsIgnoreCase("Column_Analysis")) {
            System.out.println("");
        }
        while (baseFolder.findMember(fileName + "_" + arrayStr[arrayStr.length - 1]) != null) {
            int len = fileName.length();
            String lastChar = fileName.substring(len - 1);
            // String lastSegmentStr = filepath.lastSegment();

            try {

                // fileName.substring(0, len - 1) + (++num);
                int num = Integer.valueOf(lastChar);
                // lastSegmentStr.replaceFirst(fileName, fileName.substring(0, len - 1) + (++num));
                fileName = fileName.substring(0, len - 1) + (++num);
            } catch (NumberFormatException e) {
                // lastSegmentStr.replaceFirst(fileName, fileName + 1);
                fileName = fileName + 1;
            }
        }
        return fileName;
    }

    // MOD klliu 2011-03-17 bug 19085
    private void setReplaceChildNameForAna(ModelElement modelElement) {
        Analysis analysis = (Analysis) modelElement;
        AnalysisResult results = analysis.getResults();
        assert results != null;
        Collection<Indicator> indicators = IndicatorHelper.getIndicators(results);
        for (Indicator indicator : indicators) {
            String fragment = null;
            IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
            if (indicatorDefinition != null) {
                if (indicatorDefinition.eIsProxy()) {
                    URI uri = ((InternalEObject) indicatorDefinition).eProxyURI();
                    fragment = uri.lastSegment();
                } else {
                    fragment = indicatorDefinition.eResource().getURI().lastSegment();
                }

                if (fragment != null) {

                    String replace = fragment.replace(" ", "_").replace(".", "_0.1.");//$NON-NLS-2$
                    replaceDefnitionNameInAnaMap.put(fragment, replace);
                }
            }
        }
    }

    /**
     * get the ModelElement from the file.
     * 
     * @param parentFile
     * @return
     */
    private ModelElement getModelElement(File parentFile) {
        ModelElement result = null;
        try {
            if (parentFile.getName().endsWith(FactoriesUtil.DEFINITION)) {
                result = IndicatorResourceFileHelper.getInstance().findIndDefinition(WorkspaceUtils.fileToIFile(parentFile));
            } else if (parentFile.getName().endsWith(FactoriesUtil.PATTERN)) {
                result = PatternResourceFileHelper.getInstance().findPattern(WorkspaceUtils.fileToIFile(parentFile));
            } else if (parentFile.getName().endsWith(FactoriesUtil.DQRULE)) {
                result = DQRuleResourceFileHelper.getInstance().findWhereRule(WorkspaceUtils.fileToIFile(parentFile));
            } else if (parentFile.getName().endsWith(FactoriesUtil.ANA)) {
                result = AnaResourceFileHelper.getInstance().findAnalysis(WorkspaceUtils.fileToIFile(parentFile));
            } else if (parentFile.getName().endsWith(FactoriesUtil.REP)) {
                result = RepResourceFileHelper.getInstance().findReport(WorkspaceUtils.fileToIFile(parentFile));
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        return result;
    }

    /**
     * return the ERepositoryObjectType according to the folder.
     * 
     * @param baseFolder
     * @return
     */
    private ERepositoryObjectType retrieveRepositoryObjectType(IFolder baseFolder) {
        if (ResourceManager.getPatternRegexFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_PATTERN_REGEX;
        } else if (ResourceManager.getPatternSQLFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_PATTERN_SQL;
        } else if (ResourceManager.getUDIFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS;
        } else if (ResourceManager.getRulesFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_RULES;
        } else if (ResourceManager.getRulesSQLFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_RULES_SQL;
        } else if (ResourceManager.getAnalysisFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT;
        } else if (ResourceManager.getReportsFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_REPORT_ELEMENT;
        } else if (ResourceManager.getSourceFileFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT;
        } else if (ResourceManager.getJRXMLFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_JRAXML_ELEMENT;
        }
        return null;
    }

    /**
     * get the raw file corresponds with the mig file.
     * 
     * @param migFile
     * @return
     */
    private File getRawFile(File migFile) {
        String migFilePathName = migFile.toString();
        migFilePathName = migFilePathName.replaceAll(TDQ_DATAPROFILING + MIGRATION_FOLDER_EXT, TDQ_DATAPROFILING);
        migFilePathName = migFilePathName.replaceAll(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, TDQ_LIBRARIES);
        return new File(migFilePathName);
    }

    /**
     * get the new element's default(version=0.1) file name.
     * 
     * @param element
     * @param fileExtention
     * @return
     */
    private String getDefaultNewFileName(String label, String fileExtention) {
        return label.replace(' ', '_') + "_0.1." + fileExtention; //$NON-NLS-1$
    }

    /**
     * get the full path of the new file(begin of the project).
     * 
     * @param baseFolder
     * @param baseFile
     * @param file
     * @return
     */
    private IFolder getFullPathFolder(IFolder baseFolder, File baseFile, File file) {
        IFile tempFile = baseFolder.getFile(file.toString().substring(baseFile.toString().length()));
        IPath tempPath = tempFile.getFullPath().removeLastSegments(1).removeFirstSegments(1);
        return ResourceManager.getRootProject().getFolder(tempPath);
    }

    /**
     * rename TDQ_Data Profiling and TDQ_Libraries folders.
     * 
     * @param basePath
     * @return
     */
    private boolean renameFolders(IPath basePath) {
        IPath librariesPath = basePath.append(ResourceManager.getLibrariesFolder().getFullPath()
                .makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        IPath dataProfilingPath = basePath.append(ResourceManager.getDataProfilingFolder().getFullPath()
                .makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        boolean renameTo = librariesPath.toFile().renameTo(new File(librariesPath.toOSString() + MIGRATION_FOLDER_EXT));
        boolean renameTo2 = dataProfilingPath.toFile().renameTo(new File(dataProfilingPath.toOSString() + MIGRATION_FOLDER_EXT));
        ResourceService.refreshStructure();
        return renameTo && renameTo2;
    }

    /**
     * get the mig folder path(not the raw folder path).
     * 
     * @param migFolderName
     * @param path
     * @return
     */
    private File getMigSubFolder(String migFolderName, IPath path) {
        return new File(ResourceManager.getRootProject().getLocation().toOSString().concat(String.valueOf(IPath.SEPARATOR))
                .concat(migFolderName).concat(String.valueOf(IPath.SEPARATOR)).concat(path.removeFirstSegments(2).toString()));
    }

    /**
     * create the structure of TDQ_DATA_PROFILING and TDQ_LIBRARIES.
     * 
     * @return
     */
    private boolean createNewFolders() {
        try {
            // create folders
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_DATA_PROFILING, Path.EMPTY,
                    EResourceConstant.ANALYSIS.getName());

            if (!PluginChecker.isOnlyTopLoaded()) {
                this.prfInstance.createFolder(ERepositoryObjectType.TDQ_DATA_PROFILING, Path.EMPTY,
                        EResourceConstant.REPORTS.getName());
            }

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_LIBRARIES, Path.EMPTY, EResourceConstant.PATTERNS.getName());

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_LIBRARIES, Path.EMPTY, EResourceConstant.RULES.getName());

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_RULES, Path.EMPTY, EResourceConstant.RULES_SQL.getName());

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_LIBRARIES, Path.EMPTY, EResourceConstant.EXCHANGE.getName());

            this.prfInstance
                    .createFolder(ERepositoryObjectType.TDQ_LIBRARIES, Path.EMPTY, EResourceConstant.INDICATORS.getName());

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_INDICATOR_ELEMENT, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_ADVANCED_STATISTICS.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_BUSINESS_RULES.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_CORRELATION.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_FUNCTIONAL_DEPENDENCY.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_OVERVIEW.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_PATTERN_FINDER.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_PATTERN_MATCHING.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_ROW_COMPARISON.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_SIMPLE_STATISTICS.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_SOUNDEX.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_SUMMARY_STATISTICS.getName());
            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS, Path.EMPTY,
                    EResourceConstant.SYSTEM_INDICATORS_TEXT_STATISTICS.getName());

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_INDICATOR_ELEMENT, Path.EMPTY,
                    EResourceConstant.USER_DEFINED_INDICATORS.getName());

            if (!PluginChecker.isOnlyTopLoaded()) {
                this.prfInstance.createFolder(ERepositoryObjectType.TDQ_LIBRARIES, Path.EMPTY,
                        EResourceConstant.JRXML_TEMPLATE.getName());
            }

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_PATTERN_ELEMENT, Path.EMPTY,
                    EResourceConstant.PATTERN_REGEX.getName());

            this.prfInstance
.createFolder(ERepositoryObjectType.TDQ_PATTERN_ELEMENT, Path.EMPTY,
                    EResourceConstant.PATTERN_SQL.getName());

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_LIBRARIES, Path.EMPTY,
                    EResourceConstant.SOURCE_FILES.getName());

            // refresh the project last
            ResourceService.refreshStructure();

            return true;
        } catch (Exception e) {
            log.error(e, e);
            return false;
        }
    }
}
