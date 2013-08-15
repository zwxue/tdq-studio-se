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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.BlockingKeyPostAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.BlockingKeyPreAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.windowkey.AlgoBox;

/**
 * created by zshen on Aug 7, 2013
 * Detailled comment
 *
 */
public class AbstractGenerateKey {

    private static Logger log = Logger.getLogger(AbstractGenerateKey.class);

    public static List<String> listNoParamAlgo = new ArrayList<String>();

    public static String TGENKEY_ALL_COLUMN_NAMES = "tgenkey_all_column_names";//$NON-NLS-1$

    public static final String ALGO_KEY_PREFIX = "algo";//$NON-NLS-1$

    private List<String[]> resultList = new ArrayList<String[]>();

    {
        listNoParamAlgo
                .addAll(java.util.Arrays
                        .asList("first_Char_EW", "soundex", "metaphone", "doublemetaphone", "exact", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                                "lowerCase", "upperCase", "removeDiacriticalMarks", "removeDMAndLowerCase", "removeDMAndUpperCase", "fingerPrintKey", "nGramKey", "colognePhonetic")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    }

    public void generateKey(List<Map<String, String>> BlockKeyDefinitions, Map<String, String> dataMap, String[] inputString) {
        String genKey = getGenKey(BlockKeyDefinitions, dataMap);
        String[] resultArray = new String[inputString.length + 1];
        for (int index = 0; index < inputString.length; index++) {
            resultArray[index] = inputString[index];
        }
        resultArray[inputString.length] = genKey;
        resultList.add(resultArray);
        // if (genKey.length() == 0) {
        //            log.warn("can not generate the key for the data: " + value + " and column: " + key);//$NON-NLS-1$//$NON-NLS-2$
        // }
    }

    /**
     * DOC zshen Comment method "getGenKey".
     *
     * @param value
     */
    private String getGenKey(List<Map<String, String>> BlockKeyDefinitions, Map<String, String> dataMap) {
        StringBuffer winGenKey = new StringBuffer();
        String tempVar = null;
        // get parameter from job
        // String[] allColumnNames = allColumns.toArray(new String[allColumns.size()]);
        // List<String> genKeySelectColumns = getGenKeySelectColumns(BlockKeyDefinitions);

        // get algos for each columns
        for (Map<String, String> BlockKey : BlockKeyDefinitions) {
            //            algoKey = "algo" + col;//$NON-NLS-1$
            // Map<String, String> listKeyAlgo = context.getConfiguration().getValByRegex(algoKey);
            //            String colName = listKeyAlgo.get(algoKey + "PRECOLUMN");//$NON-NLS-1$
            //            if (colName == null || colName.equals("")) { //$NON-NLS-1$
            // continue;
            // }
            String colName = BlockKey.get(MatchAnalysisConstant.COLUMN);
            String preAlgoName = BlockKey.get(MatchAnalysisConstant.PRE_ALGORITHM);
            String preAlgoPara = BlockKey.get(MatchAnalysisConstant.PRE_VALUE);
            String keyAlgoName = BlockKey.get(MatchAnalysisConstant.ALGORITHM);
            String keyAlgoPara = BlockKey.get(MatchAnalysisConstant.VALUE);
            String postAlgoName = BlockKey.get(MatchAnalysisConstant.POST_ALGORITHM);
            String postAlgoPara = BlockKey.get(MatchAnalysisConstant.POST_VALUE);
            String colValue = dataMap.get(colName);
            if (colValue == null) {
                return winGenKey.toString();
            }

            tempVar = getAlgoResult(BlockingKeyPreAlgorithmEnum.getTypeByValue(preAlgoName).getComponentValueName(), preAlgoPara,
                    colValue).toString();
            if (StringUtils.isNotEmpty(tempVar)) {
                colValue = tempVar;
            }

            tempVar = getAlgoResult(BlockingKeyAlgorithmEnum.getTypeByValue(keyAlgoName).getComponentValueName(), keyAlgoPara,
                    colValue).toString();
            if (StringUtils.isNotEmpty(tempVar)) {
                colValue = tempVar;
            }

            tempVar = getAlgoResult(BlockingKeyPostAlgorithmEnum.getTypeByValue(postAlgoName).getComponentValueName(),
                    postAlgoPara, colValue).toString();
            if (StringUtils.isNotEmpty(tempVar)) {
                colValue = tempVar;
            }

            winGenKey.append(colValue);

        }
        return winGenKey.toString();

    }


    /**
     * DOC zshen Comment method "getAlgoResult".
     *
     * @return
     */
    private Object getAlgoResult(String algoName, String algoPara, String colValue) {
        List<Class> paraClassArray = new ArrayList<Class>();
        List<Object> paraArray = new ArrayList<Object>();
        String tempAlgoPara = algoPara;
        // default parameter
        paraArray.add(colValue);
        paraClassArray.add(String.class);

        if (!"NON_ALGO".equals(algoName)) {//$NON-NLS-1$
            if (!listNoParamAlgo.contains(algoName)) {
                if (tempAlgoPara == null || tempAlgoPara.isEmpty()) {
                    log.error("there is something wrong with the parameter."); //$NON-NLS-1$
                    return ""; //$NON-NLS-1$
                }
                try {
                    Integer algoParaInt = Integer.parseInt(tempAlgoPara);
                    paraClassArray.add(int.class);
                } catch (NumberFormatException nfe) {
                    paraClassArray.add(String.class);
                    if (!tempAlgoPara.contains("\"")) { //$NON-NLS-1$
                        tempAlgoPara += "\"" + tempAlgoPara + "\""; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                paraArray.add(tempAlgoPara);
            }

            try {
                Method algoMethod = AlgoBox.class.getMethod(algoName,
                        paraClassArray.toArray(new Class[paraClassArray.size()]));
                AlgoBox algoBoxInstance = new AlgoBox();
                if (paraArray.size() == 2) {
                    if (paraClassArray.get(1).equals(int.class)) {
                        return algoMethod
                                .invoke(algoBoxInstance, paraArray.get(0), Integer.parseInt(paraArray.get(1).toString()));
                    } else {
                        return algoMethod.invoke(algoBoxInstance, paraArray.toArray(new String[2]));
                    }
                } else {
                    return algoMethod.invoke(algoBoxInstance, paraArray.get(0));
                }
            } catch (SecurityException e) {
                log.error(e, e);
            } catch (NoSuchMethodException e) {
                log.error(e, e);
            } catch (IllegalArgumentException e) {
                log.error(e, e);
            } catch (IllegalAccessException e) {
                log.error(e, e);
            } catch (InvocationTargetException e) {
                log.error(e, e);
            }
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Getter for resultList.
     *
     * @return the resultList
     */
    public List<String[]> getResultList() {
        return this.resultList;
    }

}
