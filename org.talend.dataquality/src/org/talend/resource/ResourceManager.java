// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.resource.xml.TdqPropertieManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class ResourceManager {

    private static Logger log = Logger.getLogger(ResourceManager.class);

    private ResourceManager() {
    }

    /**
     * DOC bZhou Comment method "getRootProject".
     * 
     * @return
     */
    public static IProject getRootProject() {

        return ReponsitoryContextBridge.getRootProject();
    }

    /**
     * DOC bZhou Comment method "getRootProjectName".
     * 
     * @return
     */
    public static String getRootProjectName() {
        return ReponsitoryContextBridge.getProjectName();
    }

    /**
     * DOC bZhou Comment method "getDataProfilingFolder".
     * 
     * @return
     */
    public static IFolder getDataProfilingFolder() {
        return getOneFolder(EResourceConstant.DATA_PROFILING);
    }

    /**
     * DOC bZhou Comment method "getLibrariesFolder".
     * 
     * @return
     */
    public static IFolder getLibrariesFolder() {
        return getOneFolder(EResourceConstant.LIBRARIES);
    }

    /**
     * DOC bZhou Comment method "getMetadataFolder".
     * 
     * @return
     */
    public static IFolder getMetadataFolder() {
        return getOneFolder(EResourceConstant.METADATA);
    }

    /**
     * DOC bZhou Comment method "getAnalysisFolder".
     * 
     * @return
     */
    public static IFolder getAnalysisFolder() {
        return getOneFolder(EResourceConstant.ANALYSIS);
    }

    /**
     * DOC bZhou Comment method "getReportsFolder".
     * 
     * @return
     */
    public static IFolder getReportsFolder() {
        return getOneFolder(EResourceConstant.REPORTS);
    }

    /**
     * DOC bZhou Comment method "getReportingDBFolder".
     * 
     * @return
     */
    public static IFolder getReportDBFolder() {
        return getOneFolder(EResourceConstant.REPORTING_DB);
    }

    /**
     * DOC bZhou Comment method "getRulesFolder".
     * 
     * @return
     */
    public static IFolder getRulesFolder() {
        return getOneFolder(EResourceConstant.RULES);
    }

    /**
     * DOC bZhou Comment method "getRulesSQLFolder".
     * 
     * @return
     */
    public static IFolder getRulesSQLFolder() {
        return getRulesFolder().getFolder(EResourceConstant.RULES_SQL.getName());
    }

    /**
     * DOC bZhou Comment method "getPatternFolder".
     * 
     * @return
     */
    public static IFolder getPatternFolder() {
        return getOneFolder(EResourceConstant.PATTERNS);
    }

    /**
     * DOC bZhou Comment method "getPatternSQLFolder".
     * 
     * @return
     */
    public static IFolder getPatternSQLFolder() {
        return getPatternFolder().getFolder(EResourceConstant.PATTERN_SQL.getName());
    }

    /**
     * DOC bZhou Comment method "getPatternRegexFolder".
     * 
     * @return
     */
    public static IFolder getPatternRegexFolder() {
        return getOneFolder(EResourceConstant.PATTERN_REGEX);
    }

    /**
     * DOC bZhou Comment method "getIndicatorFolder".
     * 
     * @return
     */
    public static IFolder getIndicatorFolder() {
        return getOneFolder(EResourceConstant.INDICATORS);
    }

    /**
     * DOC bZhou Comment method "getUDIFolder".
     * 
     * @return
     */
    public static IFolder getUDIFolder() {
        return getOneFolder(EResourceConstant.USER_DEFINED_INDICATORS);
    }

    /**
     * DOC bZhou Comment method "getJRXMLFolder".
     * 
     * @return
     */
    public static IFolder getJRXMLFolder() {
        return getOneFolder(EResourceConstant.JRXML_TEMPLATE);
    }

    /**
     * DOC bZhou Comment method "getConnectionFolder".
     * 
     * @return
     */
    public static IFolder getConnectionFolder() {
        return getOneFolder(EResourceConstant.DB_CONNECTIONS);
    }

    /**
     * DOC bZhou Comment method "getExchangeFolder".
     * 
     * @return
     */
    public static IFolder getExchangeFolder() {
        return getOneFolder(EResourceConstant.EXCHANGE);
    }

    /**
     * DOC bZhou Comment method "getSourceFileFolder".
     * 
     * @return
     */
    public static IFolder getSourceFileFolder() {
        return getOneFolder(EResourceConstant.SOURCE_FILES);
    }

    /**
     * DOC bZhou Comment method "isDataProfilingFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isDataProfilingFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.DATA_PROFILING);
    }

    /**
     * DOC bZhou Comment method "isLibrariesFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isLibrariesFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.LIBRARIES);
    }

    /**
     * DOC bZhou Comment method "isMetadataFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isMetadataFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.METADATA);
    }

    /**
     * DOC bZhou Comment method "isAnalysisFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isAnalysisFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.ANALYSIS);
    }

    /**
     * DOC bZhou Comment method "isReportsFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isReportsFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.REPORTS);
    }

    /**
     * DOC bZhou Comment method "isReportDBFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isReportDBFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.REPORTING_DB);
    }

    /**
     * DOC bZhou Comment method "isRulesFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isRulesFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.RULES);
    }

    /**
     * DOC bZhou Comment method "isRulesSQLFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isRulesSQLFolder(IResource resource) {
        return getRulesSQLFolder().getProjectRelativePath().equals(resource.getProjectRelativePath());
    }

    /**
     * DOC bZhou Comment method "isPatternFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isPatternFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.PATTERNS);
    }

    /**
     * DOC bZhou Comment method "isPatternSQLFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isPatternSQLFolder(IResource resource) {
        return getPatternSQLFolder().getProjectRelativePath().equals(resource.getProjectRelativePath());
    }

    /**
     * DOC bZhou Comment method "isPatternRegex".
     * 
     * @param resource
     * @return
     */
    public static boolean isPatternRegex(IResource resource) {
        return isOneFolder(resource, EResourceConstant.PATTERN_REGEX);
    }

    /**
     * DOC bZhou Comment method "isIndicatorFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isIndicatorFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.INDICATORS);
    }

    /**
     * DOC bZhou Comment method "isUDIFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isUDIFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.USER_DEFINED_INDICATORS);
    }

    /**
     * DOC bZhou Comment method "isJRXMLFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isJRXMLFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.JRXML_TEMPLATE);
    }

    /**
     * DOC bZhou Comment method "isConnectionFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isConnectionFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.DB_CONNECTIONS);
    }

    /**
     * DOC bZhou Comment method "isExchangeFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isExchangeFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.EXCHANGE);
    }

    /**
     * DOC bZhou Comment method "isSourceFileFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isSourceFileFolder(IResource resource) {
        return isOneFolder(resource, EResourceConstant.SOURCE_FILES);
    }

    /**
     * DOC bZhou Comment method "isFolder".
     * 
     * @param resource
     * @param constant
     * @return
     */
    public static boolean isOneFolder(IResource resource, EResourceConstant constant) {
        assert resource != null;

        String path = resource.getProjectRelativePath().toString();
        String compPath = getOneFolder(constant).getProjectRelativePath().toString();

        return path.equalsIgnoreCase(compPath);
    }

    /**
     * DOC bZhou Comment method "getFolder".
     * 
     * @param name
     * @return
     */
    public static IFolder getOneFolder(EResourceConstant constant) {
        Object value = TdqPropertieManager.getInstance().getFolderPropertyValue(constant.getName(),
                ResourceConstant.FOLDER_CLASSIFY_KEY);
        if (value == null) {
            return getRootProject().getFolder(constant.getName());
        }
        return getRootProject().getFolder(new Path(value.toString()));
    }

    /**
     * DOC bZhou Comment method "setClassifyProperty".
     * 
     * @param resource
     */
    public static void setClassifyProperty(IResource resource) {
        assert resource != null;

        String relativePath = resource.getProjectRelativePath().toString();
        TdqPropertieManager.getInstance().addFolderProperties(resource, ResourceConstant.FOLDER_CLASSIFY_KEY, relativePath);
    }

    /**
     * DOC bZhou Comment method "setReadOnlyProperty".
     * 
     * @param resource
     */
    public static void setReadOnlyProperty(IResource resource) {
        assert resource != null;

        TdqPropertieManager.getInstance().addFolderProperties(resource, ResourceConstant.FOLDER_READONLY_KEY,
                ResourceConstant.FOLDER_READONLY_PROPERTY);
    }

    /**
     * DOC bZhou Comment method "isReadOnlyFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isReadOnlyFolder(IResource resource) {
        assert resource != null;

        Object property = TdqPropertieManager.getInstance()
                .getFolderPropertyValue(resource, ResourceConstant.FOLDER_READONLY_KEY);
        return property != null && !StringUtils.isEmpty(property.toString());
    }

    /**
     * DOC bZhou Comment method "setNoSubFolderProperty".
     * 
     * @param resource
     */
    public static void setNoSubFolderProperty(IResource resource) {
        assert resource != null;

        TdqPropertieManager.getInstance().addFolderProperties(resource, ResourceConstant.NO_SUBFOLDER_KEY,
                ResourceConstant.NO_SUBFOLDER_PROPERTY);
    }

    /**
     * DOC bZhou Comment method "isNoSubFolder".
     * 
     * @param resource
     * @return
     */
    public static boolean isNoSubFolder(IResource resource) {
        assert resource != null;

        Object property = TdqPropertieManager.getInstance().getFolderPropertyValue(resource, ResourceConstant.NO_SUBFOLDER_KEY);
        return property != null && !StringUtils.isEmpty(property.toString());
    }

    /**
     * DOC bZhou Comment method "refreshStructure".
     */
    public static void refreshStructure() {
        try {
            getRootProject().refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * DOC bZhou Comment method "isSubFolder".
     * 
     * @param parentFolder
     * @param subFolder
     * @return
     */
    public static boolean isSubFolder(IFolder parentFolder, IFolder subFolder) {
        assert parentFolder != null;
        assert subFolder != null;

        return parentFolder.getFullPath().isPrefixOf(subFolder.getFullPath());
    }

    /**
     * DOC bZhou Comment method "isSubFolder".
     * 
     * @param parentFolder
     * @param subFolders
     * @return
     */
    public static boolean isSubFolder(IFolder parentFolder, IFolder... subFolders) {
        assert parentFolder != null;
        assert subFolders != null;

        for (IFolder subFolder : subFolders) {
            if (!isSubFolder(parentFolder, subFolder)) {
                return false;
            }
        }

        return true;
    }
}
