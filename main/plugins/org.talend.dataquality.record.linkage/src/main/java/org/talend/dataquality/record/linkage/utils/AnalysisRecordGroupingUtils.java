// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.grouping.IRecordGrouping;

/**
 * used for some utility functions
 */
public class AnalysisRecordGroupingUtils {

    public static final String ESCAPE_CHARACTER = "\\"; //$NON-NLS-1$

    /**
     * get Complete Column Schema.
     * 
     * @return
     */
    public static String[] getCompleteColumnSchema(Map<String, String> columnMap) {
        List<String> columnNameList = new ArrayList<String>();
        if (columnMap == null) {
            return new String[0];
        }
        for (String columnName : columnMap.keySet()) {
            columnNameList.add(columnName);
        }
        columnNameList.add(MatchAnalysisConstant.BLOCK_KEY);
        columnNameList.add(MatchAnalysisConstant.GID);
        columnNameList.add(MatchAnalysisConstant.GRP_SIZE);
        columnNameList.add(MatchAnalysisConstant.MASTER);
        columnNameList.add(MatchAnalysisConstant.SCORE);
        columnNameList.add(MatchAnalysisConstant.GRP_QUALITY);
        columnNameList.add(MatchAnalysisConstant.ATTRIBUTE_SCORES);
        return columnNameList.toArray(new String[columnNameList.size()]);
    }

    /**
     * getCompleteColumnSchema: when refresh match chart by click refresh button
     * 
     * @return
     */
    public static String[] getCompleteColumnSchemaWithoutBlockKey(Map<String, String> columnMap) {
        List<String> columnNameList = new ArrayList<String>();
        if (columnMap == null) {
            return new String[0];
        }
        for (String columnName : columnMap.keySet()) {
            columnNameList.add(columnName);
        }
        columnNameList.add(MatchAnalysisConstant.GID);
        columnNameList.add(MatchAnalysisConstant.GRP_SIZE);
        columnNameList.add(MatchAnalysisConstant.MASTER);
        columnNameList.add(MatchAnalysisConstant.SCORE);
        columnNameList.add(MatchAnalysisConstant.GRP_QUALITY);
        columnNameList.add(MatchAnalysisConstant.ATTRIBUTE_SCORES);
        return columnNameList.toArray(new String[columnNameList.size()]);
    }

    /**
     * get the key map of the match table's columns(<column, index>)
     * 
     * @param column
     * @param algoType
     * @param algoParameter
     * @param confidentWeight
     * @param columnIndexMap
     * @param matchInterval
     * @param attributeName
     * @return
     */
    public static Map<String, String> getMatchKeyMap(String column, String algoType, String algoParameter, int confidentWeight,
            Map<String, String> columnIndexMap, double matchInterval, String attributeName, String handleNull) {
        return getMatchKeyMap(column, algoType, algoParameter, confidentWeight, columnIndexMap, matchInterval, attributeName,
                handleNull, null);// The jar path is null when the matcher's algorithm is not a type of "custom"
    }

    /**
     * get the key map of the match table's columns(<column, index>)
     * 
     * @param column
     * @param algoType
     * @param algoParameter
     * @param confidentWeight
     * @param columnIndexMap
     * @param matchInterval
     * @param attributeName
     * @param handleNull
     * @param jarPath
     * @return
     */
    public static Map<String, String> getMatchKeyMap(String column, String algoType, String algoParameter, int confidentWeight,
            Map<String, String> columnIndexMap, double matchInterval, String attributeName, String handleNull, String jarPath) {
        Map<String, String> matchKeyMap = new HashMap<String, String>();
        matchKeyMap.put(IRecordGrouping.COLUMN_IDX, columnIndexMap.get(column));
        matchKeyMap.put(IRecordGrouping.MATCHING_TYPE, AttributeMatcherType.valueOf(algoType).name());
        matchKeyMap.put(IRecordGrouping.CUSTOMER_MATCH_CLASS, algoParameter);
        matchKeyMap.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(confidentWeight));
        matchKeyMap.put(IRecordGrouping.RECORD_MATCH_THRESHOLD, String.valueOf(matchInterval));
        matchKeyMap.put(IRecordGrouping.ATTRIBUTE_NAME, attributeName);
        matchKeyMap.put(IRecordGrouping.HANDLE_NULL, handleNull);
        matchKeyMap.put(IRecordGrouping.JAR_PATH, jarPath);
        return matchKeyMap;
    }

    /**
     * Get blocking key map
     * 
     * @return
     */
    public static Map<String, String> getBlockingKeyMap(String column, String preAlgo, String preAlgValue, String algorithm,
            String algorithmValue, String postAlgo, String postAlgoValue) {
        Map<String, String> blockKeyDefMap = new HashMap<String, String>();
        blockKeyDefMap.put(MatchAnalysisConstant.PRECOLUMN, column);
        blockKeyDefMap.put(MatchAnalysisConstant.PRE_ALGO, preAlgo);
        blockKeyDefMap.put(MatchAnalysisConstant.PRE_VALUE, preAlgValue);
        blockKeyDefMap.put(MatchAnalysisConstant.KEY_ALGO, algorithm);
        blockKeyDefMap.put(MatchAnalysisConstant.KEY_VALUE, algorithmValue);
        blockKeyDefMap.put(MatchAnalysisConstant.POST_ALGO, postAlgo);
        blockKeyDefMap.put(MatchAnalysisConstant.POST_VALUE, postAlgoValue);
        return blockKeyDefMap;
    }

    /**
     * join the string array to a single string, use escapeCharacter to escape the separator. MUST call
     * {@link #split(String, String, String)} to split the joined string. (if the string end with escapeCharacter, there
     * will join to the next column!!!)
     * 
     * @param array
     * @param separator recommend to use |
     * @param escapeCharacter recommend to use \
     * @return
     */
    public static String join(String[] array, String separator, String escapeCharacter) {
        String doubleEscapeCharacter = escapeCharacter + escapeCharacter;
        String escapeCharacterSeparator = escapeCharacter + separator;
        StringBuilder sr = new StringBuilder();
        for (String str : array) {
            String temp = StringUtils.replace(str, escapeCharacter, doubleEscapeCharacter);
            temp = StringUtils.replace(temp, separator, escapeCharacterSeparator);
            sr.append(temp + separator);
        }
        return StringUtils.removeEnd(sr.toString(), separator);
    }

    /**
     * split the string into a string array, use escapeCharacter to escape the separator. the string MUST be generated
     * by {@link #join(String[], String, String)}.(if the string end with escapeCharacter, there will join to the next
     * column!!!)
     * 
     * @param string
     * @param separator recommend to use |
     * @param escapeCharacter recommend to use \
     * @return
     */
    public static String[] split(String string, String separator, String escapeCharacter) {
        String doubleEscapeCharacter = escapeCharacter + escapeCharacter;
        String escapeCharacterSeparator = escapeCharacter + separator;
        String regex = "(?<!" + Pattern.quote(escapeCharacter) + ")" + Pattern.quote(separator); //$NON-NLS-1$ //$NON-NLS-2$
        ArrayList<String> strs = new ArrayList<String>();
        for (String s : string.split(regex)) {
            String temp = StringUtils.replace(s, escapeCharacterSeparator, separator);
            temp = StringUtils.replace(temp, doubleEscapeCharacter, escapeCharacter);
            strs.add(temp);
        }
        return strs.toArray(new String[strs.size()]);
    }
}
