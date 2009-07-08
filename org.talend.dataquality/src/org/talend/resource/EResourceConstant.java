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

/**
 * DOC bZhou class global comment. Detailled comment
 */
public enum EResourceConstant {

    TDQ_DATA_PROFILING("TDQ_Data Profiling", "", "", ""),
    TDQ_LIBRARIES("TDQ_Libraries", "", "", ""),
    TDQ_METADATA("TDQ_Metadata", "", "", ""),
    TDQ_REPORTING_DB("TDQ_reporting_db", "", "", ""),

    // Folder in data profiling
    ANALYSES("Analyses", "FOLDER_ANALYSIS_PROPERTY", "TDQ_Data Profiling", ""),
    REPORTS("Reports", "FOLDER_REPORT_PROPERTY", "TDQ_Data Profiling", ""),

    // Folder in libraries
    DQ_RULES("DQ Rules", "DQRULES_FOLDER_PROPERTY", "TDQ_Libraries", "/dqrules"),
    EXCHANGE("Exchange", "FOLDER_EXCHANGE_PROPERTY", "TDQ_Libraries", ""),
    JRXML_REPORTS("JRXML Template", "JRXML_FOLDER_PROPERTY", "TDQ_Libraries", ""),
    PATTERNS("Patterns", "FOLDER_PATTERNS_PROPERTY", "TDQ_Libraries", "/patterns"),
    SOURCE_FILES("Source Files", "SOURCEFILES_FOLDER_PROPERTY", "TDQ_Libraries", "/demo"),
    SQL_PATTERNS("SQL Patterns", "SQLPATTERNS_FOLDER_PROPERTY", "TDQ_Libraries", "/sql_like"),

    // Folder in metadata
    DB_CONNECTIONS("DB Connections", "DBCONNECTION_FOLDER_PROPERTY", "TDQ_Metadata", "");

    private String folderName;

    private String folderProperty;

    private String parentFolderName;

    private String dataSourcePath;

    private EResourceConstant(String folderName, String folderProperty, String parentFolderName, String dataSourcePath) {
        this.folderName = folderName;
        this.folderProperty = folderProperty;
        this.parentFolderName = parentFolderName;
        this.dataSourcePath = dataSourcePath;
    }

    /**
     * Getter for dataSourcePath.
     * 
     * @return the dataSourcePath
     */
    public String getDataSourcePath() {
        return dataSourcePath;
    }

    /**
     * Getter for folderName.
     * 
     * @return the folderName
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * Getter for folderProperty.
     * 
     * @return the folderProperty
     */
    public String getFolderProperty() {
        return folderProperty;
    }

    /**
     * Getter for parentFolderName.
     * 
     * @return the parentFolderName
     */
    public String getParentFolderName() {
        if (parentFolderName == null || "".equals(parentFolderName)) {
            return ResourceManager.getRootProjectName();
        }

        return parentFolderName;
    }
}
