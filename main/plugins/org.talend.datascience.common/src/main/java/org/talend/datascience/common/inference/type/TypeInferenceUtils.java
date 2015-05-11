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

import org.pojava.datetime.DateTime;

/**
 * Utility class refering data types given single value
 * 
 * @author zhao
 *
 */
public final class TypeInferenceUtils {
	/**
	 * Detect if the given value is a string type.
	 * 
	 * @param value
	 *            the value to be detected.
	 * @return true if the value is a string type, false otherwise.
	 */
	public static boolean isAlphString(String value) {
		if (value != null) {
			if (value.matches("[a-z|A-Z]+\\s*[a-z|A-Z]*")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if the given value is a double type.
	 * 
	 * @param value
	 *            the value to be detected.
	 * @return true if the value is a double type, false otherwise.
	 */
	public static boolean isDouble(String value) {
		if (value != null) {
			if (value.matches("^[-+]?\\d*\\.\\d*$")) {
				return true;
			}
			//TODO see if it can parse "3.4d" using Double.valueOf()
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
			if (value.matches("^(\\+|-)?\\d+$")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Detect if the given value is a date type.
	 * 
	 * @param value
	 *            the value to be detected.
	 * @return true if the value is a date type, false otherwise.
	 */
	public static boolean isDate(String value) {
		if (value != null) {
			try {
				new DateTime(value);
				return true;
			} catch (Throwable e) {
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
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
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
			if (value.matches("\\D")) {
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
