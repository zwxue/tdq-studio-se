// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
     * @param contexts
     * @param defaultContextName
     * @param str
     * @return
     */
    public static String getContextValue(List<ContextType> contexts, String defaultContextName, String str) {
        String value = EMPTY_STRING;
        if (!StringUtils.isEmpty(str)) {
            boolean findContext = false;
            if (str.startsWith(CONTEXT_PREFFIX)) {
                String contextName = str.substring(CONTEXT_PREFFIX.length(), str.length());
                for (ContextType ct : contexts) {
                    if (ct.getName().equals(defaultContextName)) {
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
                value = str;
            }
        }
        return value;
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
                String ofName = ReportHelper.getOutputFolderNameAssinged(reports.get(0));
                if (StringUtils.isNotBlank(ofName)) {
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
                    if (StringUtils.isBlank(ofNameAssinged)) {
                        return null;
                    }
                    contextVar = ofNameAssinged;
                    if (isContextMode) {
                        isContextMode = ContextHelper.isContextVar(ofNameAssinged);
                    }
                    String ofName = ContextHelper
                            .getContextValue(report.getContext(), report.getDefaultContext(), ofNameAssinged);
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
}
