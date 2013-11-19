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
package org.talend.dataquality.record.linkage.utils;

/**
 * created by zshen on Nov 14, 2013 Detailled comment
 * 
 */
public class CustomAttributeMatcherClassNameConvert {

    public static final String REGEXKEY = "\\|\\|"; //$NON-NLS-1$

    public static final String QUTO = "\""; //$NON-NLS-1$

    /**
     * DOC zshen Comment method "getClassName".
     * 
     * @param classPathParameter
     * @return
     */
    public static String getClassName(String classPathParameter) {
        String[] allElements = classPathParameter.split(REGEXKEY);
        if (allElements.length > 0) {
            return allElements[allElements.length - 1];
        } else {
            return classPathParameter;
        }
    }

    /**
     * DOC zshen Comment method "getClassName".
     * 
     * @param classPathParameter
     * @return
     */
    public static String getClassNameAndAddQuot(String classPathParameter) {
        String className = getClassName(classPathParameter);
        return addQuotationMarks(className);
    }

    /**
     * DOC zshen Comment method "addQuotationMarks".
     * 
     * @param className
     */
    private static String addQuotationMarks(String className) {
        if (className == null || className.isEmpty()) {
            return className;
        }
        String result = className;
        if (!QUTO.equals(className.indexOf(0))) {
            result = QUTO + className;
        }
        if (!QUTO.equals(className.indexOf(className.lastIndexOf(0)))) {
            result += QUTO;
        }
        return result;
    }

}
