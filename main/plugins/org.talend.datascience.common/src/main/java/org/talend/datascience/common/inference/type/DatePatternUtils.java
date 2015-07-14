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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhao Date pattern utils.
 */
public class DatePatternUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatePatternUtils.class);

    private Pattern digits = Pattern.compile("[0-9]");

    private Pattern lowerAlph = Pattern.compile("[a-z]");

    private Pattern lowerAlphSpec = Pattern.compile("çâêîôûéèùïöü");

    private Pattern upperAlph = Pattern.compile("[A-Z]");

    private Pattern upperAlphSpec = Pattern.compile("ÇÂÊÎÔÛÉÈÙÏÖÜ");

    private Map<Pattern, String> dateParsers = new LinkedHashMap<Pattern, String>();

    private static DatePatternUtils instance = null;

    private static String[] PATTERN_NAMES = new String[0];

    private DatePatternUtils() {
        try {
            final InputStream stream = TypeInferenceUtils.class.getResourceAsStream("datePatterns.txt");
            final List<String> lines = IOUtils.readLines(stream);
            PATTERN_NAMES = new String[lines.size()];
            int idx = 0;
            for (String line : lines) {
                if (!"".equals(line.trim())) {
                    String[] lineArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "=");
                    String patternName = StringUtils.removeEnd(StringUtils.removeStart(lineArray[0], "\""), "\"");
                    dateParsers.put(Pattern.compile(StringUtils.removeEnd(StringUtils.removeStart(lineArray[1], "\""), "\"")),
                            patternName);
                    PATTERN_NAMES[idx++] = patternName;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Unable to get date patterns.", e);
        }
    }

    public static DatePatternUtils getInstance() {
        if (instance == null) {
            instance = new DatePatternUtils();
        }
        return instance;
    }

    /**
     * Whether the given string pattern a date pattern or not.
     * 
     * @param pattern
     * @return true if the pattern string is a date pattern.
     */
    public boolean isDatePattern(String pattern) {
        return ArrayUtils.contains(PATTERN_NAMES, pattern);
    }

    /**
     * Whether the given string value is a date or not.
     * 
     * @param value
     * @return true if the value is a date.
     */
    public boolean isDate(String value) {
        for (Pattern parser : dateParsers.keySet()) {
            try {
                if (parser.matcher(value).find()) {
                    return true;
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return false;
    }

    /**
     * Replace the value with date pattern string.
     * 
     * @param value
     * @return date pattern string.
     */
    public String datePatternReplace(String value) {
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }
        // Parse the value given list of date regex in pattern file.
        String patternToFind = "";
        for (Pattern parser : dateParsers.keySet()) {
            try {
                if (parser.matcher(value).find()) {
                    return dateParsers.get(parser);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        if (patternToFind.equals("")) {
            patternToFind = patternReplace(value);
        }
        return patternToFind;
    }

    /**
     * Replace the character in value with the predefined pattern character.
     * 
     * @param value
     * @return pattern string.
     */
    public String patternReplace(String value) {
        String replacedValue = digits.matcher(value).replaceAll("9");
        replacedValue = lowerAlph.matcher(replacedValue).replaceAll("a");
        replacedValue = upperAlph.matcher(replacedValue).replaceAll("A");
        replacedValue = lowerAlphSpec.matcher(replacedValue).replaceAll("a");
        replacedValue = upperAlphSpec.matcher(replacedValue).replaceAll("A");
        return replacedValue;
    }

    /**
     * Whether the patternString contains the predefined alpha character.
     * 
     * @param patternString
     * @return
     */
    public boolean containsAlphabetic(String patternString) {
        boolean containsLowerAhpa = lowerAlph.matcher(patternString).find();
        if (!containsLowerAhpa) {
            containsLowerAhpa = upperAlph.matcher(patternString).find();
            if (!containsLowerAhpa) {
                containsLowerAhpa = lowerAlphSpec.matcher(patternString).find();
                if (!containsLowerAhpa) {
                    containsLowerAhpa = upperAlphSpec.matcher(patternString).find();
                }
            }
        }
        return containsLowerAhpa;
    }

}
