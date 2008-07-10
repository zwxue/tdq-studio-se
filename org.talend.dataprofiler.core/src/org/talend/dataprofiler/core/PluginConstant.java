// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core;

import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.manager.DQStructureManager;

/**
 * This class store all the constant of current plugin.
 * 
 */
public final class PluginConstant {

    public static final String SE_ID = "net.sourceforge.sqlexplorer.plugin.perspectives.SQLExplorerPluginPerspective";

    public static final String CHEAT_SHEET_VIEW = "org.eclipse.ui.cheatsheets.views.CheatSheetView";

    public static final String PERSPECTIVE_ID = "org.talend.dataprofiler.DataProfilingPerspective";

    public static final String EMPTY_STRING = "";

    public static final int DEFAULT_INT_VALUE = 0;

    public static final String DEFAULT_PARAMETERS = "noDatetimeStringSync=true";

    public static final String SPACE_STRING = " ";

    public static final String PROJECTCREATED_FLAG = "PROJECTCREATED_FLAG";

    public static final String CONNECTION_URL_PROPERTY = "CONNECTION_URL_PROPERTY";

    public static final String CONNECTION_DBNAME_PROPERTY = "CONNECTION_DBNAME_PROPERTY";

    public static final String PRV_SUFFIX = "." + FactoriesUtil.PROV;

    public static final String ANA_SUFFIX = "." + FactoriesUtil.ANA;

    public static final String REP_SUFFIX = "." + FactoriesUtil.REP;

    public static final String PATTERN_SUFFIX = "*.pattern";

    public static final String SQL_SUFFIX = "*.sql";

    public static final String METADATA_PROJECTNAME = DQStructureManager.METADATA;

    public static final String DATA_PROFILING_PROJECTNAME = DQStructureManager.DATA_PROFILING;

    public static final String FILE_SUFFIX = "FILE_SUFFIX";

    public static final String ISDIRTY_PROPERTY = "ISDIRTY_PROPERTY";

    public static final String DATAFILTER_PROPERTY = "DATAFILTER_PROPERTY";

    public static final String REPORT_FOLDER_PATH = "/Data Profiling/Reports";

    public static final String DQ_VIEW_ID = "org.talend.dataprofiler.core.ui.views.DQRespositoryView";

    public static final String PARENTHESIS_LEFT = "(";

    public static final String PARENTHESIS_RIGHT = ")";

    public static final String PATTERN_EDITOR = "org.talend.dataprofiler.core.ui.editor.pattern.PatternEditor";

    public static final String MAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)"; //$NON-NLS-1$

    public static final String USER_PROPERTY = "user";

    public static final String PASSWORD_PROPERTY = "password";

    public static final String HOSTNAME_PROPERTY = "hostname";

    public static final String PORT_PROPERTY = "port";

    public static final String DBTYPE_PROPERTY = "dbtype";

}
