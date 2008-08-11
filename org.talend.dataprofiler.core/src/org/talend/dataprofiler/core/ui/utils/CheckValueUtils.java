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

        return Pattern.matches(regex, inputString);
    }

    public static boolean isStringValue(String inputString) {

        String regex = "^([a-z]|[A-Z])\\w*";
        return getCheckValue(regex, inputString);
    }

    public static boolean isNumberValue(String inputString) {

        String regex = "\\d+";
        return getCheckValue(regex, inputString);
    }

    public static boolean isRealNumberValue(String inputString) {
        String regex = "-*\\d+\\.\\d+";
        return getCheckValue(regex, inputString);
    }

    public static boolean isNumberValue(String[] inputString) {

        boolean flag = true;

        for (String str : inputString) {
            flag = flag && isNumberValue(str);
        }

        return flag;
    }

    public static boolean isRealNumberValue(String[] inputString) {
        boolean flag = true;

        for (String str : inputString) {
            flag = flag && isRealNumberValue(str);
        }

        return flag;
    }

    public static boolean isNumberOfShownValue(String inputString) {

        String regex = "(^[^0])\\d*";
        return getCheckValue(regex, inputString);
    }

    public static boolean isNumberWithNegativeValue(String inputString) {

        String regex = "-?\\d+";
        return getCheckValue(regex, inputString);
    }

    public static boolean isPatternValue(String inputString) {

        String regex = "'.*'";
        return getCheckValue(regex, inputString);
    }

    /**
     * DOC zqin Comment method "isDateValue".
     * 
     * @param inputString
     * @return
     */
    public static boolean isDateValue(String inputString) {

        String regex1 = "\\b(0?[1-9]|[12][0-9]|3[01])[- /.](0?[1-9]|1[012])[- /.](19|20)?[0-9]{2}\\b";
        String regex2 = "(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)[0-9]{2}";
        String regex3 = "\\b(0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.](19|20)?[0-9]{2}\\b";
        String regex4 = "(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)[0-9]{2}";
        String regex5 = "\\b(19|20)?[0-9]{2}[- /.](0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])\\b";
        String regex6 = "(19|20)[0-9]{2}[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])";

        System.out.println(regex1);

        return getCheckValue(regex1, inputString) || getCheckValue(regex2, inputString) || getCheckValue(regex3, inputString)
                || getCheckValue(regex4, inputString) || getCheckValue(regex5, inputString) || getCheckValue(regex6, inputString);
    }

}
