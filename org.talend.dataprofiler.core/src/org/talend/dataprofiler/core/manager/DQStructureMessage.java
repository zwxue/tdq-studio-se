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
package org.talend.dataprofiler.core.manager;

import java.util.HashMap;

import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.resource.ResourceManager;

/**
 * Restore the relationship between folder name and message key. yyi 2009-10-16 Feature 9481
 */
public class DQStructureMessage {

    private static final HashMap<String, String> MESSAGEMAP = new HashMap<String, String>();

    private static final String KEY_NOT_FOUND_PREFIX = "!!!"; //$NON-NLS-1$

    private static final String KEY_NOT_FOUND_SUFFIX = "!!!"; //$NON-NLS-1$

    public static String getString(String key) {

        String message = key;
        String messageKey = MESSAGEMAP.get(key);

        if (null != messageKey && !DefaultMessagesImpl.getString(messageKey).startsWith(KEY_NOT_FOUND_PREFIX)
                && !DefaultMessagesImpl.getString(messageKey).endsWith(KEY_NOT_FOUND_SUFFIX)) {

            message = DefaultMessagesImpl.getString(messageKey);
        }
        return message;
    }

    static {
        MESSAGEMAP.put(DQStructureManager.ANALYSIS, "DQStructureManager.analyses");

        MESSAGEMAP.put(ResourceManager.DATA_PROFILING_FOLDER_NAME, "DQStructureManager.data_Profiling");

        MESSAGEMAP.put("DB Connections", "DQStructureManager.dbConnections");

        MESSAGEMAP.put(DQStructureManager.DQ_RULES, "DQStructureManager.dqRules");

        MESSAGEMAP.put(ResourceManager.LIBRARIES_FOLDER_NAME, "DQStructureManager.libraries");

        MESSAGEMAP.put(ResourceManager.METADATA_FOLDER_NAME, "DQStructureManager.metadata");

        MESSAGEMAP.put(DQStructureManager.PATTERNS, "DQStructureManager.patterns");

        MESSAGEMAP.put(DQStructureManager.REGEX, "DQStructureManager.regex");

        MESSAGEMAP.put(DQStructureManager.REPORTS, "DQStructureManager.reports");

        MESSAGEMAP.put(DQStructureManager.RULES, "DQStructureManager.rules");

        MESSAGEMAP.put(DQStructureManager.SOURCE_FILES, "DQStructureManager.sourceFiles");

        MESSAGEMAP.put(DQStructureManager.SQL_PATTERNS, "DQStructureManager.sqlPatterns");

        MESSAGEMAP.put(DQStructureManager.SQL, "DQStructureManager.sqls");

        MESSAGEMAP.put(DQStructureManager.USER_DEFINED_INDICATORS, "DQStructureManager.userDefinedIndicators");
    }

}
