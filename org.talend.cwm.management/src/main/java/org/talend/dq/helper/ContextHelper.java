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

import java.util.List;

import org.apache.commons.lang.StringUtils;
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
            if (str.startsWith(CONTEXT_PREFFIX)) {
                String contextName = str.substring(CONTEXT_PREFFIX.length(), str.length());
                for (ContextType ct : contexts) {
                    if (ct.getName().equals(defaultContextName)) {
                        for (Object obj : ct.getContextParameter()) {
                            ContextParameterType cpt = (ContextParameterType) obj;
                            if (cpt.getName().equals(contextName)) {
                                value = cpt.getValue();
                                break;
                            }
                        }
                        break;
                    }
                }
            } else {
                value = str;
            }
        }
        return value;
    }
}
