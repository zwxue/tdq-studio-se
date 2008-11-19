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
package org.talend.dataprofiler.core.ui.utils;

import java.util.regex.Pattern;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class CheckValueUtils {

    public static boolean getCheckValue(String regex, String inputString) {

        if (isEmpty(inputString)) {
            return false;
        }

        return Pattern.matches(regex, inputString);
    }

    public static boolean isStringValue(String inputString) {

        String regex = "^([a-z]|[A-Z])\\w*"; //$NON-NLS-1$
        return getCheckValue(regex, inputString);
    }

    public static boolean isNumberValue(String inputString) {

        String regex = "\\d+"; //$NON-NLS-1$
        return getCheckValue(regex, inputString);
    }

    public static boolean isRealNumberValue(String inputString) {
        String regex = "-?\\d+(\\.\\d+)?"; //$NON-NLS-1$
        return getCheckValue(regex, inputString);
    }

    public static boolean isNumberValue(String... strs) {
        for (String str : strs) {
            if (!isNumberValue(str)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isRealNumberValue(String... strs) {
        for (String str : strs) {
            if (!isRealNumberValue(str)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isNumberOfShownValue(String inputString) {

        String regex = "^[1-9]\\d*"; //$NON-NLS-1$
        return getCheckValue(regex, inputString);
    }

    public static boolean isNumberWithNegativeValue(String inputString) {

        String regex = "-?\\d+"; //$NON-NLS-1$
        return getCheckValue(regex, inputString);
    }

    public static boolean isPatternValue(String inputString) {

        String regex = "'.*'"; //$NON-NLS-1$
        return getCheckValue(regex, inputString);
    }

    /**
     * DOC Zqin Comment method "isAoverB".
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean isAoverB(String a, String b) {
        if (!isEmpty(a, b) && (isNumberValue(a, b) || isRealNumberValue(a, b))) {
            Double da = new Double(a);
            Double db = new Double(b);
            return da > db;
        } else {
            return false;
        }
    }

    /**
     * DOC Zqin Comment method "isEmpty".
     * 
     * @param strs
     * @return
     */
    public static boolean isEmpty(String... strs) {
        if (strs == null) {
            return false;
        }

        for (String str : strs) {
            if ("".equals(str.trim())) {
                return true;
            }
        }

        return false;
    }

    /**
     * DOC Zqin Comment method "isOutRange".
     * 
     * @param min the min value of this range, and min <= max.
     * @param max the max value of this range, and max >= min
     * @param strs
     * @return
     */
    public static boolean isOutRange(double min, double max, String... strs) {

        for (String str : strs) {
            if (isNumberValue(str) || isRealNumberValue(str)) {
                Double db = new Double(str);
                if (db > max || db < min) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * DOC zqin Comment method "isDateValue".
     * 
     * @param inputString
     * @return
     */
    public static boolean isDateValue(String inputString) {

        String regex1 = "\\b(0?[1-9]|[12][0-9]|3[01])[- /.](0?[1-9]|1[012])[- /.](19|20)?[0-9]{2}\\b"; //$NON-NLS-1$
        String regex2 = "(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)[0-9]{2}"; //$NON-NLS-1$
        String regex3 = "\\b(0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.](19|20)?[0-9]{2}\\b"; //$NON-NLS-1$
        String regex4 = "(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)[0-9]{2}"; //$NON-NLS-1$
        String regex5 = "\\b(19|20)?[0-9]{2}[- /.](0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])\\b"; //$NON-NLS-1$
        String regex6 = "(19|20)[0-9]{2}[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])"; //$NON-NLS-1$

        System.out.println(regex1);

        return getCheckValue(regex1, inputString) || getCheckValue(regex2, inputString) || getCheckValue(regex3, inputString)
                || getCheckValue(regex4, inputString) || getCheckValue(regex5, inputString) || getCheckValue(regex6, inputString);
    }

}
