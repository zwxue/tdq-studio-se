// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.ContextItem;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;
import org.talend.designer.core.model.utils.emf.talendfile.ContextParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;

/**
 * created by xqliu on Jul 24, 2013 Detailled comment
 * 
 */
public final class ContextHelper {

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private static final String CONTEXT_PREFFIX = "context."; //$NON-NLS-1$

    /**
     * if the str is context variable return true, else return false.
     * 
     * @param str
     * @return
     */
    public static boolean isContextVar(String str) {
        return str == null ? false : str.startsWith(CONTEXT_PREFFIX);
    }

    /**
     * get the context value if pass a context name, otherwise return the str directly.
     * 
     * @param contexts the context list
     * @param contextGroupName the name of the group which be used to get the context value
     * @param contextVarName the context variable's name
     * @return
     */
    public static String getContextValue(List<ContextType> contexts, String contextGroupName, String contextVarName) {
        String value = EMPTY_STRING;
        if (!StringUtils.isEmpty(contextVarName)) {
            boolean findContext = false;
            if (contextVarName.startsWith(CONTEXT_PREFFIX)) {
                String contextName = contextVarName.substring(CONTEXT_PREFFIX.length(), contextVarName.length());
                for (ContextType ct : contexts) {
                    if (ct.getName().equals(contextGroupName)) {
                        for (Object obj : ct.getContextParameter()) {
                            ContextParameterType cpt = (ContextParameterType) obj;
                            if (cpt.getName().equals(contextName)) {
                                value = cpt.getValue();
                                findContext = true;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            if (!findContext) {
                value = contextVarName;
            }
        }
        return value;
    }

    /**
     * get the context's value from specified context group of the report.
     * 
     * @param tdReport the Report
     * @param contextGroupName the name of the context group which be used to get the context value
     * @param contextVarName the context variable's name
     * @return
     */
    public static String getReportContextValue(TdReport tdReport, String contextGroupName, String contextVarName) {
        return getContextValue(tdReport.getContext(), contextGroupName, contextVarName);
    }

    /**
     * get the context's value from the last run context(or default context if last run context is empty) in the report.
     * 
     * @param tdReport the Report
     * @param contextVarName the context variable's name
     * @return
     */
    public static String getReportContextValue(TdReport tdReport, String contextVarName) {
        return getReportContextValue(tdReport, ReportHelper.getContextGroupName(tdReport), contextVarName);
    }

    /**
     * if all the reports have the same output folder return it, else return null.
     * 
     * @param reports
     * @return the context var or real string or null
     */
    public static String getOutputFolderFromReports(List<TdReport> reports) {
        String result = null;
        if (reports != null && !reports.isEmpty()) {
            if (reports.size() == 1) {
                TdReport report = reports.get(0);
                String ofName = ReportHelper.getOutputFolderNameAssinged(report);
                if (StringUtils.isNotBlank(ofName) && StringUtils.isNotBlank(ContextHelper.getReportContextValue(report, ofName))) {
                    result = ofName;
                }
            } else {
                boolean isContextMode = true;
                String contextVar = null;
                Set<String> temp = new HashSet<String>();
                for (TdReport report : reports) {
                    String ofNameAssinged = ReportHelper.getOutputFolderNameAssinged(report);
                    // if there exist the report which don't set the output folder, this mean use default output folder,
                    // so just return null
                    if (StringUtils.isBlank(ofNameAssinged)
                            || StringUtils.isBlank(ContextHelper.getReportContextValue(report, ofNameAssinged))) {
                        return null;
                    }
                    contextVar = ofNameAssinged;
                    if (isContextMode) {
                        isContextMode = ContextHelper.isContextVar(ofNameAssinged);
                    }
                    String ofName = ContextHelper.getReportContextValue(report, ofNameAssinged);
                    if (StringUtils.isNotBlank(ofName)) {
                        temp.add(ofName);
                    }
                }
                if (temp.size() == 1) {
                    if (isContextMode) {
                        result = contextVar;
                    } else {
                        result = temp.iterator().next();
                    }
                }
            }
        }
        return result;
    }

    /**
     * if the report use any context variable return true, else return false.
     * 
     * @param tdReport the report using context or not
     * @return
     */
    public static boolean usingContext(TdReport tdReport) {
        String of = ReportHelper.getOutputFolderNameAssinged(tdReport);
        String logo = tdReport.getLogo();
        String host = ReportHelper.getHost(tdReport);
        String port = ReportHelper.getPort(tdReport);
        String sid = ReportHelper.getDbName(tdReport);
        String schema = ReportHelper.getSchema(tdReport);
        String user = ReportHelper.getUser(tdReport);
        String password = ReportHelper.getPassword(tdReport);
        return isContextVar(of) || isContextVar(logo) || isContextVar(host) || isContextVar(port) || isContextVar(sid)
                || isContextVar(schema) || isContextVar(user) || isContextVar(password);
    }

    /**
     * in ctxString, replace the context variable name with the value from contextValues .
     * 
     * @param ctxString the string which contain several context variable names
     * @param contextValues the context values map, key=ContextScriptCode, value=ContextValue
     * @return
     */
    public static String getContextStringValue(String ctxString, Map<String, String> contextValues) {
        String result = ctxString;
        for (String key : contextValues.keySet()) {
            result = StringUtils.replace(result, key, contextValues.get(key));
        }
        return result;
    }

    /**
     * return default ContextType of the ContextItem, if ContextItem is null return null.
     * 
     * @param contextItem
     * @return the default ContextType of the ContextItem
     */
    public static ContextType getDefaultContextType(ContextItem contextItem) {
        ContextType contextType = null;
        if (contextItem != null) {
            EList<ContextType> contexts = contextItem.getContext();
            for (ContextType ct : contexts) {
                if (ct.getName().equals(contextItem.getDefaultContext())) {
                    contextType = ct;
                    break;
                }
            }
        }
        return contextType;
    }
}
