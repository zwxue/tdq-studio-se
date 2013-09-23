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
package org.talend.dataquality.record.linkage.genkey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.record.linkage.utils.AlgorithmSwitch;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;

/**
 * created by zshen on Aug 7, 2013 generating the blocking keys.
 * 
 */
public class AbstractGenerateKey {

    public static String TGENKEY_ALL_COLUMN_NAMES = "tgenkey_all_column_names";//$NON-NLS-1$

    public static final String ALGO_KEY_PREFIX = "algo";//$NON-NLS-1$

    private Map<String, List<String[]>> genKeyToBlockResult = new HashMap<String, List<String[]>>();

    private String[] parameters = { MatchAnalysisConstant.PRE_ALGO, MatchAnalysisConstant.PRE_VALUE,
            MatchAnalysisConstant.KEY_ALGO, MatchAnalysisConstant.KEY_VALUE, MatchAnalysisConstant.POST_ALGO,
            MatchAnalysisConstant.POST_VALUE };

    /**
     * generate the blocking key for each columns
     * 
     * @param BlockKeyDefinitions
     * @param dataMap
     * @param inputString
     */
    public void generateKey(List<Map<String, String>> BlockKeyDefinitions, Map<String, String> dataMap, String[] inputString) {
        String genKey = getGenKey(BlockKeyDefinitions, dataMap);
        String[] resultArray = new String[inputString.length + 1];
        for (int index = 0; index < inputString.length; index++) {
            resultArray[index] = inputString[index];
        }
        resultArray[inputString.length] = genKey;
        if (genKeyToBlockResult.get(genKey) != null) {
            List<String[]> resultInBlock = genKeyToBlockResult.get(genKey);
            resultInBlock.add(resultArray);
        } else {
            // Put the result which has same generating key in one block
            List<String[]> resultInNewBlock = new ArrayList<String[]>();
            resultInNewBlock.add(resultArray);
            genKeyToBlockResult.put(genKey, resultInNewBlock);
        }
    }

    /**
     * get the block key of each column.
     * 
     * @param value
     */
    private String getGenKey(List<Map<String, String>> BlockKeyDefinitions, Map<String, String> dataMap) {
        StringBuffer winGenKey = new StringBuffer();
        // get algos for each columns
        for (Map<String, String> blockKey : BlockKeyDefinitions) {
            String colName = blockKey.get(MatchAnalysisConstant.COLUMN);

            String colValue = getAlgoForEachColumn(dataMap.get(colName), blockKey, parameters);

            winGenKey.append(colValue);

        }
        return winGenKey.toString();

    }

    /**
     * 
     * getthe Algo value For Each Column.
     * 
     * @param originalValue
     * @param blockKey
     * @param parameters
     * @return
     */
    public String getAlgoForEachColumn(String originalValue, Map<String, String> blockKey, String[] parameters) {
        String tempVar = null;
        String colValue = originalValue;
        String preAlgoName = blockKey.get(parameters[0]);
        String preAlgoPara = blockKey.get(parameters[1]);
        String keyAlgoName = blockKey.get(parameters[2]);
        String keyAlgoPara = blockKey.get(parameters[3]);
        String postAlgoName = blockKey.get(parameters[4]);
        String postAlgoPara = blockKey.get(parameters[5]);

        if (colValue == null) {
            colValue = StringUtils.EMPTY;
        }

        tempVar = AlgorithmSwitch.getPreAlgoResult(preAlgoName, preAlgoPara, colValue).toString();
        if (StringUtils.isNotEmpty(tempVar)) {
            colValue = tempVar;
        }

        tempVar = AlgorithmSwitch.getAlgoResult(keyAlgoName, keyAlgoPara, colValue).toString();
        if (StringUtils.isNotEmpty(tempVar)) {
            colValue = tempVar;
        }

        tempVar = AlgorithmSwitch.getPostAlgoResult(postAlgoName, postAlgoPara, colValue).toString();
        if (StringUtils.isNotEmpty(tempVar)) {
            colValue = tempVar;
        }
        return colValue;
    }

    /**
     * Getter for resultList.
     * 
     * @return the resultList
     */
    public Map<String, List<String[]>> getResultList() {
        return this.genKeyToBlockResult;
    }

}
