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
import org.talend.resource.EResourceConstant;

/**
 * Restore the relationship between folder name and message key. yyi 2009-10-16 Feature 9481
 */
public final class DQStructureMessage {

    private static final HashMap<String, String> MESSAGEMAP = new HashMap<String, String>();

    private static final String KEY_NOT_FOUND_PREFIX = "!!!"; //$NON-NLS-1$

    private static final String KEY_NOT_FOUND_SUFFIX = "!!!"; //$NON-NLS-1$

    private DQStructureMessage() {

    }

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
        MESSAGEMAP.put(EResourceConstant.ANALYSIS.getName(), "DQStructureManager.analyses");

        MESSAGEMAP.put(EResourceConstant.DATA_PROFILING.getName(), "DQStructureManager.data_Profiling");

        MESSAGEMAP.put(EResourceConstant.DB_CONNECTIONS.getName(), "DQStructureManager.dbConnections");

        MESSAGEMAP.put(EResourceConstant.LIBRARIES.getName(), "DQStructureManager.libraries");

        MESSAGEMAP.put(EResourceConstant.METADATA.getName(), "DQStructureManager.metadata");

        MESSAGEMAP.put(EResourceConstant.PATTERNS.getName(), "DQStructureManager.patterns");

        MESSAGEMAP.put(EResourceConstant.PATTERN_REGEX.getName(), "DQStructureManager.regex");

        MESSAGEMAP.put(EResourceConstant.REPORTS.getName(), "DQStructureManager.reports");

        MESSAGEMAP.put(EResourceConstant.SOURCE_FILES.getName(), "DQStructureManager.sourceFiles");

        MESSAGEMAP.put(EResourceConstant.PATTERN_SQL.getName(), "DQStructureManager.sqlPatterns");

        MESSAGEMAP.put(EResourceConstant.RULES.getName(), "DQStructureManager.rules");

        MESSAGEMAP.put(EResourceConstant.RULES_SQL.getName(), "DQStructureManager.sqls");

        MESSAGEMAP.put(EResourceConstant.USER_DEFINED_INDICATORS.getName(), "DQStructureManager.userDefinedIndicators");

        // MESSAGEMAP.put(EResourceConstant.RULES.getName(), "DQStructureManager.dqRules");
    }

}
