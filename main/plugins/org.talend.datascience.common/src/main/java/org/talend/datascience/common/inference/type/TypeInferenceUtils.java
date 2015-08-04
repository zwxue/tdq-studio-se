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
package org.talend.datascience.common.inference.type;

import java.util.regex.Pattern;

/**
 * Utility class refering data types given single value
 * 
 * @author zhao
 *
 */
public class TypeInferenceUtils {

    private static final Pattern patternAlphString = Pattern.compile("[a-z|A-Z]+\\s*[a-z|A-Z]*");

    private static final Pattern patternDouble = Pattern.compile("^[-+]?\\d*\\.\\d*$"); // \\d*(\\.?\\d+)

    private static final Pattern patternInteger = Pattern.compile("^(\\+|-)?\\d+$");

    private static final Pattern patternNoneDigit = Pattern.compile("\\D");

    /**
     * Detect if the given value is a double type. Note that 3,4 is not valid double with the rule of this method.
     * 
     * @param value the value to be detected.
     * @return true if the value is a double type, false otherwise.
     */
    public static boolean isDouble(String value) {
        if (value != null) {
            try {
                if (patternDouble.matcher(value).find()) {
                    return true;
                } else {
                    Double.valueOf(value);
                    return true;
                }
            } catch (Throwable e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Detect if the given value is a integer type.
     * 
     * @param value the value to be detected.
     * @return true if the value is a integer type, false otherwise.
     */
    public static boolean isInteger(String value) {
        if (value != null) {
            try {
                if (patternInteger.matcher(value).find()) {
                    return true;
                }
            } catch (Throwable e) {
                return false;
            }
        }
        return false;
    }

    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }

    /**
     * Detect if the given value is a date type. <br>
     * Date regex used to match: http://regexlib.com/REDetails.aspx?regexp_id=361 ,and regex matching yyy-MM-dd
     * HH:mm:ss.SSS
     * 
     * @param value the value to be detected.
     * @return true if the value is a date type, false otherwise.
     */
    public static boolean isDate(String value) {
        if (value != null) {
            // 1. The length of date characters should not exceed 30.
            if (value.trim().length() > 30) {
                return false;
            }
            // 2. Check it by list of patterns
            return DatePatternUtils.getInstance().isDate(value);
        } else {
            return false;
        }
    }

    /**
     * Detect if the given value is a boolean type.
     * 
     * @param value the value to be detected.
     * @return true if the value is a boolean type, false otherwise.
     */
    public static boolean isBoolean(String value) {
        if (value != null && (value.length() == 4 || value.length() == 5)) {
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detect if the given value is a char type.
     * 
     * @param value the value to be detected.
     * @return true if the value is a char type, false otherwise.
     */
    public static boolean isChar(String value) {
        if (value != null && value.length() == 1) {
            if (patternNoneDigit.matcher(value).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detect if the given value is a empty type.
     * 
     * @param value the value to be detected.
     * @return true if the value is a empty type, false otherwise.
     */
    public static boolean isEmpty(String value) {
        if (value == null || value.length() == 0) {
            return true;
        }
        if (value.trim().length() == 0) {
            return true;
        }
        return false;
    }

}
