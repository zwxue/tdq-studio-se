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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * update TDQ_Data Profiling and TDQ_Libraries folders after use same api with TOS(metadata folder has been updated
 * before).
 */
public class UpdateAfterMergeTosApiTask extends AbstractWorksapceUpdateTask {

    private static Logger log = Logger.getLogger(UpdateAfterMergeTosApiTask.class);

    private ProxyRepositoryFactory prfInstance;

    private static final String MIGRATION_FOLDER_EXT = "_mig";

    private static final String TDQ_DATAPROFILING = "TDQ_Data Profiling";

    private static final String TDQ_LIBRARIES = "TDQ_Libraries";

    private File systemIndicatorMigFolder;

    private File userDefinedIndicatorMigFolder;

    private Map<String, String> replaceStringMapIndicators = new HashMap<String, String>();

    private File patternRegexMigFolder;

    private File patternSqlMigFolder;

    private Map<String, String> replaceStringMapPatterns = new HashMap<String, String>();

    private File ruleSqlMigFolder;

    private Map<String, String> replaceStringMapRules = new HashMap<String, String>();

    private File sourceFileMigFolder;

    // source file don't need to record replace information
    // private Map<String, String> replaceStringMapSourceFiles = new HashMap<String, String>();

    private File jrxmlTemplateMigFolder;

    private Map<String, String> replaceStringMapJrxmlTemplates = new HashMap<String, String>();

    private File analysisMigFolder;

    private Map<String, String> replaceStringMapAnalysis = new HashMap<String, String>();

    private File reportMigFolder;

    private Map<String, String> replaceStringMapReports = new HashMap<String, String>();

    public UpdateAfterMergeTosApiTask() {
        init();
    }

    private void init() {
        if (!CorePlugin.getDefault().isRepositoryInitialized()) {
            CorePlugin.getDefault().initProxyRepository();
        }
        this.prfInstance = ProxyRepositoryFactory.getInstance();
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
        return renameFolders(getWorkspacePath()) && createNewFolders() && updateLibrariesFolder() && updateDataProfilingFolder();
    }

    /**
     * update files under TDQ_Data Profiling.
     * 
     * @return
     */
    private boolean updateDataProfilingFolder() {
        try {
            analysisMigFolder = getMigSubFolder(TDQ_DATAPROFILING + MIGRATION_FOLDER_EXT, ResourceManager.getAnalysisFolder()
                    .getFullPath());
            updateModelElements(analysisMigFolder, FactoriesUtil.ANA, ResourceManager.getAnalysisFolder(), analysisMigFolder, true,
                    replaceStringMapAnalysis);
            // System.out.println("AAAAAAAAAAAAAAAAAAAAAAA::" + replaceStringMapAnalysis.toString());

            if (!PluginChecker.isOnlyTopLoaded()) {
                reportMigFolder = getMigSubFolder(TDQ_DATAPROFILING + MIGRATION_FOLDER_EXT, ResourceManager.getReportsFolder()
                        .getFullPath());
                updateModelElements(reportMigFolder, FactoriesUtil.REP, ResourceManager.getReportsFolder(), reportMigFolder, true,
                        replaceStringMapReports);
                // System.out.println("BBBBBBBBBBBBBBBBBBBBBB::" + replaceStringMapReports.toString());
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        return true;
    }

    /**
     * update files under TDQ_Libraries.
     * 
     * @return
     */
    private boolean updateLibrariesFolder() {
        try {
            systemIndicatorMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager
                    .getSystemIndicatorFolder().getFullPath());
            userDefinedIndicatorMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getUDIFolder()
                    .getFullPath());
            updateModelElements(systemIndicatorMigFolder, FactoriesUtil.DEFINITION, ResourceManager.getSystemIndicatorFolder(),
                    systemIndicatorMigFolder, false, replaceStringMapIndicators);
            updateModelElements(userDefinedIndicatorMigFolder, FactoriesUtil.DEFINITION, ResourceManager.getUDIFolder(),
                    userDefinedIndicatorMigFolder, true, replaceStringMapIndicators);
            // System.out.println("11111111111111111111111::" + replaceStringMapIndicators.toString());

            patternRegexMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getPatternRegexFolder()
                    .getFullPath());
            patternSqlMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getPatternSQLFolder()
                    .getFullPath());
            updateModelElements(patternRegexMigFolder, FactoriesUtil.PATTERN, ResourceManager.getPatternRegexFolder(),
                    patternRegexMigFolder, true, replaceStringMapPatterns);
            updateModelElements(patternSqlMigFolder, FactoriesUtil.PATTERN, ResourceManager.getPatternSQLFolder(),
                    patternSqlMigFolder, true, replaceStringMapPatterns);
            // System.out.println("2222222222222222222222222::" + replaceStringMapPatterns.toString());

            ruleSqlMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getRulesSQLFolder()
                    .getFullPath());
            updateModelElements(ruleSqlMigFolder, FactoriesUtil.DQRULE, ResourceManager.getRulesSQLFolder(), ruleSqlMigFolder, true,
                    replaceStringMapRules);
            // System.out.println("333333333333333333333::" + replaceStringMapRules.toString());

            sourceFileMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getSourceFileFolder()
                    .getFullPath());
            updateFiles(sourceFileMigFolder, FactoriesUtil.SQL, ResourceManager.getSourceFileFolder(), sourceFileMigFolder, true,
                    null);
            // System.out.println("4444444444444444444444444::" + replaceStringMapSourceFiles.toString());

            jrxmlTemplateMigFolder = getMigSubFolder(TDQ_LIBRARIES + MIGRATION_FOLDER_EXT, ResourceManager.getJRXMLFolder()
                    .getFullPath());
            updateFiles(jrxmlTemplateMigFolder, FactoriesUtil.JRXML, ResourceManager.getJRXMLFolder(), jrxmlTemplateMigFolder, true,
                    replaceStringMapJrxmlTemplates);
            // System.out.println("5555555555555555555555555::" + jrxmlTemplateMigFolder.toString());
        } catch (Exception e) {
            log.error(e, e);
        }
        return true;
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
     */
    private void updateFiles(File parentFile, String fileExtension, IFolder baseFolder, File baseFile, boolean createSubFolder,
            Map<String, String> replaceStringMap) {
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
                    // System.out.println(">>>>>>>>>>>>>>>>>>>::fullPathFolder=" + fullPathFolder.toString());
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
        }
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
                String folderName = "";

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
     */
    private void createFile(File parentFile, String fileExtension, IPath filePath, String label) {
        try {
            if (FactoriesUtil.JRXML.equals(fileExtension)) {
                JrxmlHandle.createJrxml(filePath, label, parentFile, fileExtension);
            } else if (FactoriesUtil.SQL.equals(fileExtension)) {
                DQStructureManager.getInstance().createSourceFileItem(parentFile, filePath, label, fileExtension);
            }
        } catch (Exception e) {
            log.error(e, e);
        }
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
     */
    private void updateModelElements(File parentFile, String fileExtension, IFolder baseFolder, File baseFile,
            boolean createSubFolder, Map<String, String> replaceStringMap) {
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
                if (parentFile.getName().endsWith(fileExtension)) {
                    // get the ModelElement
                    ModelElement modelElement = getModelElement(parentFile);
                    if (modelElement != null) {
                        // get new file's full path folder
                        IFolder fullPathFolder = getFullPathFolder(baseFolder, baseFile, parentFile);
                        // create new file
                        ElementWriterFactory.getInstance().createPatternWriter().create(modelElement, fullPathFolder);
                        // System.out.println(">>>>>>>>>>>>>>>>>>>::fullPathFolder=" + fullPathFolder.toString());
                        // record the replace information
                        IPath replatePrefixPath = fullPathFolder.getFullPath().removeFirstSegments(1);
                        // replace name should begin with TDQ_Libraries (for element under TDQ_Libraries)
                        String oldFileNameFull = replatePrefixPath.append(parentFile.getName()).toString();
                        String newFileNameFull = replatePrefixPath.append(
                                getDefaultNewFileName(modelElement.getName(), fileExtension)).toString();
                        // replace name should begin with Analyses or Reports (for element under TDQ_Data Profiling),
                        // because report can dependency on analysis and the relaview path begin with Analyses, not
                        // TDQ_Data
                        // Profiling
                        if (FactoriesUtil.REP.equals(fileExtension) || FactoriesUtil.ANA.equals(fileExtension)) {
                            oldFileNameFull = replatePrefixPath.removeFirstSegments(1).append(parentFile.getName()).toString();
                            newFileNameFull = replatePrefixPath.removeFirstSegments(1)
                                    .append(getDefaultNewFileName(modelElement.getName(), fileExtension)).toString();
                        }
                        replaceStringMap.put(oldFileNameFull, newFileNameFull);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e, e);
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
        } else if (ResourceManager.getRulesSQLFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_RULES_SQL;
        } else if (ResourceManager.getAnalysisFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_ANALYSIS;
        } else if (ResourceManager.getReportsFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_REPORTS;
        } else if (ResourceManager.getSourceFileFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_SOURCE_FILES;
        } else if (ResourceManager.getJRXMLFolder().equals(baseFolder)) {
            return ERepositoryObjectType.TDQ_JRXMLTEMPLATE;
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
        return label.replace(' ', '_') + "_0.1." + fileExtention;//$NON-NLS-1$
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
        // System.out.println("::baseFolder=" + baseFolder.toString() + "||baseFile=" + baseFile.toString() + "||file="
        // + file.toString() + "||tempPath=" + tempPath);
        return ResourceManager.getRootProject().getFolder(tempPath);
    }

    /**
     * rename TDQ_Data Profiling and TDQ_Libraries folders.
     * 
     * @return
     */
    private boolean renameFolders(IPath basePath) {
        IPath librariesPath = basePath.append(ResourceManager.getLibrariesFolder().getFullPath()
                .makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        IPath dataProfilingPath = basePath.append(ResourceManager.getDataProfilingFolder().getFullPath()
                .makeRelativeTo(ResourceManager.getRootProject().getFullPath()));
        return librariesPath.toFile().renameTo(new File(librariesPath.toOSString() + MIGRATION_FOLDER_EXT))
                && dataProfilingPath.toFile().renameTo(new File(dataProfilingPath.toOSString() + MIGRATION_FOLDER_EXT));
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
            // refresh the project first
            ResourceService.refreshStructure();

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

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_INDICATORS, Path.EMPTY,
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

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_INDICATORS, Path.EMPTY,
                    EResourceConstant.USER_DEFINED_INDICATORS.getName());

            if (!PluginChecker.isOnlyTopLoaded()) {
                this.prfInstance.createFolder(ERepositoryObjectType.TDQ_LIBRARIES, Path.EMPTY,
                        EResourceConstant.JRXML_TEMPLATE.getName());
            }

            this.prfInstance.createFolder(ERepositoryObjectType.TDQ_PATTERNS, Path.EMPTY,
                    EResourceConstant.PATTERN_REGEX.getName());

            this.prfInstance
                    .createFolder(ERepositoryObjectType.TDQ_PATTERNS, Path.EMPTY, EResourceConstant.PATTERN_SQL.getName());

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
