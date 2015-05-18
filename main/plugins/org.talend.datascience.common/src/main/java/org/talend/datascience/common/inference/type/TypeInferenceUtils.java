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

import org.pojava.datetime.DateTime;

/**
 * Utility class refering data types given single value
 * 
 * @author zhao
 *
 */
public final class TypeInferenceUtils {

	private static Pattern patternAlphString = Pattern
			.compile("[a-z|A-Z]+\\s*[a-z|A-Z]*");
	private static Pattern patternDouble = Pattern
			.compile("^[-+]?\\d*\\.\\d*$"); // \\d*(\\.?\\d+)
	private static Pattern patternInteger = Pattern.compile("^(\\+|-)?\\d+$");
	//private static Pattern patternDateA = Pattern
	//		.compile("^(((((0[13578])|([13578])|(1[02]))[\\-\\/\\s]?((0[1-9])|([1-9])|([1-2][0-9])|(3[01])))|((([469])|(11))[\\-\\/\\s]?((0[1-9])|([1-9])|([1-2][0-9])|(30)))|((02|2)[\\-\\/\\s]?((0[1-9])|([1-9])|([1-2][0-9]))))[\\-\\/\\s]?\\d{4})(\\s(((0[1-9])|([1-9])|(1[0-2]))\\:([0-5][0-9])((\\s)|(\\:([0-5][0-9])\\s))([AM|PM|am|pm]{2,2})))?$");
	//private static Pattern patternDateB = Pattern
	//		.compile("(?<Year>(19|20)[0-9][0-9])-(?<Month>0[1-9]|1[0-2])-(?<Day>0[1-9]|[12][0-9]|3[01])(T|\\s)(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])(\\.[0-9]*)?");
	private static Pattern patternNoneDigit = Pattern.compile("\\D");

	/**
	 * Detect if the given value is a string type.
	 * 
	 * @param value
	 *            the value to be detected.
	 * @return true if the value is a string type, false otherwise.
	 */
	public static boolean isAlphString(String value) {
		if (value != null) {
			if (patternAlphString.matcher(value).find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if the given value is a double type.
	 * Note that 3,4 is not valid double with the rule of this method. 
	 * @param value
	 *            the value to be detected.
	 * @return true if the value is a double type, false otherwise.
	 */
	public static boolean isDouble(String value) {
		if (value != null) {
			if (patternDouble.matcher(value).find()) {
				return true;
			} else {
				try {
					Double.valueOf(value);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Detect if the given value is a integer type.
	 * 
	 * @param value
	 *            the value to be detected.
	 * @return true if the value is a integer type, false otherwise.
	 */
	public static boolean isInteger(String value) {
		if (value != null) {
			if (patternInteger.matcher(value).find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if the given value is a date type. <br>
	 * Date regex used to match:
	 * http://regexlib.com/REDetails.aspx?regexp_id=361 ,and regex matching
	 * yyy-MM-dd HH:mm:ss.SSS
	 * 
	 * @param value
	 *            the value to be detected.
	 * @return true if the value is a date type, false otherwise.
	 */
	public static boolean isDate(String value) {
		if (value != null) {
			try {
				new DateTime(value);
				//TODO Need to test if this lib can match all the pattern in PatternsNameAndRegularExpressions.txt.
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Detect if the given value is a boolean type.
	 * 
	 * @param value
	 *            the value to be detected.
	 * @return true if the value is a boolean type, false otherwise.
	 */
	public static boolean isBoolean(String value) {
		if (value != null && (value.length() == 4 || value.length() == 5)) {
			if (value.equalsIgnoreCase("true")
					|| value.equalsIgnoreCase("false")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if the given value is a char type.
	 * 
	 * @param value
	 *            the value to be detected.
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
	 * @param value
	 *            the value to be detected.
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

	public static final String TYPE_BOOL = "boolean";
	public static final String TYPE_EMPTY = "empty";
	public static final String TYPE_CHAR = "char";
	public static final String TYPE_INTEGER = "integer";
	public static final String TYPE_DOUBLE = "double";
	public static final String TYPE_STRING = "string";
	public static final String TYPE_DATE = "date";
	public static final String[] TYPES = new String[] { TYPE_BOOL, TYPE_CHAR,
			TYPE_EMPTY, TYPE_INTEGER, TYPE_DOUBLE, TYPE_STRING, TYPE_DATE };

}
