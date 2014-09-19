// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.talend.core.model.metadata.builder.connection.ConnectionPackage;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.grouping.IRecordGrouping;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;

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
    public static MetadataColumn[] getCompleteColumnSchema(Map<MetadataColumn, String> columnMap) {
        List<MetadataColumn> columnNameList = new ArrayList<MetadataColumn>();
        if (columnMap == null) {
            return new MetadataColumn[0];
        }
        for (MetadataColumn columnName : columnMap.keySet()) {
            columnNameList.add(columnName);
        }
        MetadataColumn dummyBlockKeyColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyBlockKeyColumn.setName(MatchAnalysisConstant.BLOCK_KEY);
        columnNameList.add(dummyBlockKeyColumn);
        MetadataColumn dummyGIDColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyGIDColumn.setName(MatchAnalysisConstant.GID);
        columnNameList.add(dummyGIDColumn);
        MetadataColumn dummyGSizeColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyGSizeColumn.setName(MatchAnalysisConstant.GRP_SIZE);
        columnNameList.add(dummyGSizeColumn);
        MetadataColumn dummyMasterColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyMasterColumn.setName(MatchAnalysisConstant.MASTER);
        columnNameList.add(dummyMasterColumn);
        MetadataColumn dummyScoreColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyScoreColumn.setName(MatchAnalysisConstant.SCORE);
        columnNameList.add(dummyScoreColumn);
        MetadataColumn dummyGRPQualityColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyGRPQualityColumn.setName(MatchAnalysisConstant.GRP_QUALITY);
        columnNameList.add(dummyGRPQualityColumn);
        MetadataColumn dummyAtrrScoresColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyAtrrScoresColumn.setName(MatchAnalysisConstant.ATTRIBUTE_SCORES);
        columnNameList.add(dummyAtrrScoresColumn);
        return columnNameList.toArray(new MetadataColumn[columnNameList.size()]);
    }

    /**
     * getCompleteColumnSchema: when refresh match chart by click refresh button
     * 
     * @return
     */
    public static MetadataColumn[] getCompleteColumnSchemaWithoutBlockKey(Map<MetadataColumn, String> columnMap) {
        List<MetadataColumn> columnNameList = new ArrayList<MetadataColumn>();
        if (columnMap == null) {
            return new MetadataColumn[0];
        }
        for (MetadataColumn columnName : columnMap.keySet()) {
            columnNameList.add(columnName);
        }
        MetadataColumn dummyGIDColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyGIDColumn.setName(MatchAnalysisConstant.GID);
        columnNameList.add(dummyGIDColumn);
        MetadataColumn dummyGSizeColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyGSizeColumn.setName(MatchAnalysisConstant.GRP_SIZE);
        columnNameList.add(dummyGSizeColumn);
        MetadataColumn dummyMasterColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyMasterColumn.setName(MatchAnalysisConstant.MASTER);
        columnNameList.add(dummyMasterColumn);
        MetadataColumn dummyScoreColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyScoreColumn.setName(MatchAnalysisConstant.SCORE);
        columnNameList.add(dummyScoreColumn);
        MetadataColumn dummyGRPQualityColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyGRPQualityColumn.setName(MatchAnalysisConstant.GRP_QUALITY);
        columnNameList.add(dummyGRPQualityColumn);
        MetadataColumn dummyAtrrScoresColumn = ConnectionPackage.eINSTANCE.getConnectionFactory().createMetadataColumn();
        dummyAtrrScoresColumn.setName(MatchAnalysisConstant.ATTRIBUTE_SCORES);
        columnNameList.add(dummyAtrrScoresColumn);
        return columnNameList.toArray(new MetadataColumn[columnNameList.size()]);
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
            double attrThreshold, Map<MetadataColumn, String> columnIndexMap, double matchInterval, String attributeName,
            String handleNull) {
        return getMatchKeyMap(column, algoType, algoParameter, confidentWeight, attrThreshold, columnIndexMap, matchInterval,
                attributeName, handleNull, null);// The jar path is null when the matcher's algorithm is not a type of
                                                 // "custom"
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
            double attrThreshold, Map<MetadataColumn, String> columnIndexMap, double matchInterval, String attributeName,
            String handleNull, String jarPath) {
        Map<String, String> matchKeyMap = getMatchKeyMap(column, algoType, algoParameter, confidentWeight, attrThreshold,
                columnIndexMap, matchInterval, attributeName, null, handleNull, jarPath);
        return matchKeyMap;
    }

    /**
     * 
     * Create match key map plus an additional "match key name" .
     * 
     * @param column
     * @param algoType
     * @param algoParameter
     * @param confidentWeight
     * @param columnIndexMap
     * @param matchInterval
     * @param attributeName
     * @param matchKeyName
     * @param handleNull
     * @param jarPath
     * @return
     */
    public static Map<String, String> getMatchKeyMap(String column, String algoType, String algoParameter, int confidentWeight,
            double attrThreshold, Map<MetadataColumn, String> columnIndexMap, double matchInterval, String attributeName,
            String matchKeyName, String handleNull, String jarPath) {
        Map<String, String> matchKeyMap = new HashMap<String, String>();
        for (MetadataColumn metaCol : columnIndexMap.keySet()) {
            if (metaCol.getName().equals(column)) {
                matchKeyMap.put(IRecordGrouping.COLUMN_IDX, columnIndexMap.get(metaCol));
                break;
            }
        }
        matchKeyMap.put(IRecordGrouping.MATCHING_TYPE, AttributeMatcherType.valueOf(algoType).name());
        matchKeyMap.put(IRecordGrouping.CUSTOMER_MATCH_CLASS, algoParameter);
        matchKeyMap.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(confidentWeight));
        matchKeyMap.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(attrThreshold));
        matchKeyMap.put(IRecordGrouping.RECORD_MATCH_THRESHOLD, String.valueOf(matchInterval));
        matchKeyMap.put(IRecordGrouping.ATTRIBUTE_NAME, attributeName);
        matchKeyMap.put(IRecordGrouping.MATCH_KEY_NAME, matchKeyName);
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
