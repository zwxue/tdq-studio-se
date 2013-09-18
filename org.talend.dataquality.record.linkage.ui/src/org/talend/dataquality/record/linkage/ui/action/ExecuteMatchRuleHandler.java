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
package org.talend.dataquality.record.linkage.ui.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.talend.commons.exception.BusinessException;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.columnset.BlockKeyIndicator;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.record.linkage.genkey.BlockingKeyHandler;
import org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.record.linkage.utils.AnalysisRecordGroupingUtils;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.AppliedBlockKey;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.KeyDefinition;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.RulesPackage;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * created by zshen on Sep 16, 2013 Detailled comment
 * 
 */
public class ExecuteMatchRuleHandler {

    public static TypedReturnCode<MatchGroupResultConsumer> execute(Analysis analysis, Map<String, String> columnMap,
            RecordMatchingIndicator recordMatchingIndicator, List<Object[]> matchRows, BlockKeyIndicator blockKeyIndicator) {
        TypedReturnCode<MatchGroupResultConsumer> returnCode = new TypedReturnCode<MatchGroupResultConsumer>(false);
        MatchGroupResultConsumer matchResultConsumer = createMatchGroupResultConsumer(columnMap, recordMatchingIndicator);
        returnCode.setObject(matchResultConsumer);
        List<AppliedBlockKey> appliedBlockKeys = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getAppliedBlockKeys();
        appliedBlockKeys.clear();
        // By default for analysis, the applied blocking key will be the key from key generation definition. This
        // will be refined when there is a need to define the applied blocking key manually by user later.
        createAppliedBlockKeyByGenKey(recordMatchingIndicator, appliedBlockKeys);

        // Blocking key specified.
        ReturnCode computeMatchGroupReturnCode = computeMatchGroupWithBlockKey(analysis, recordMatchingIndicator,
                blockKeyIndicator, columnMap, matchResultConsumer, matchRows);
        returnCode.setOk(computeMatchGroupReturnCode.isOk());
        returnCode.setMessage(computeMatchGroupReturnCode.getMessage());

        return returnCode;
    }

    /**
     * DOC zhao Comment method "initRecordMatchIndicator".
     * 
     * @param columnMap
     * @return
     */
    private static MatchGroupResultConsumer createMatchGroupResultConsumer(Map<String, String> columnMap,
            final RecordMatchingIndicator recordMatchingIndicator) {
        recordMatchingIndicator.setMatchRowSchema(AnalysisRecordGroupingUtils.getCompleteColumnSchema(columnMap));
        recordMatchingIndicator.reset();
        MatchGroupResultConsumer matchResultConsumer = new MatchGroupResultConsumer() {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer#handle(java.lang.Object)
             */
            @Override
            public void handle(Object row) {
                recordMatchingIndicator.handle(row);
                addOneRowOfResult(row);
            }
        };
        return matchResultConsumer;
    }

    /**
     * DOC zhao Comment method "createAppliedBlockKeyByGenKey".
     * 
     * @param recordMatchingIndicator
     * @param appliedBlockKeys
     */
    private static void createAppliedBlockKeyByGenKey(RecordMatchingIndicator recordMatchingIndicator,
            List<AppliedBlockKey> appliedBlockKeys) {
        List<BlockKeyDefinition> blockKeyDefs = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getBlockKeys();
        if (blockKeyDefs != null && blockKeyDefs.size() > 0) {
            AppliedBlockKey appliedBlockKey = RulesPackage.eINSTANCE.getRulesFactory().createAppliedBlockKey();
            appliedBlockKey.setColumn(PluginConstant.BLOCK_KEY);
            appliedBlockKey.setName(PluginConstant.BLOCK_KEY);
            appliedBlockKeys.add(appliedBlockKey);
        }
    }

    /**
     * DOC zhao Comment method "computeMatchGroupWithBlockKey".
     * 
     * @param analysis
     * @param recordMatchingIndicator
     * @param blockKeyIndicator
     * @param columnMap
     * @param matchResultConsumer
     * @param matchRows
     */
    private static ReturnCode computeMatchGroupWithBlockKey(Analysis analysis, RecordMatchingIndicator recordMatchingIndicator,
            BlockKeyIndicator blockKeyIndicator, Map<String, String> columnMap, MatchGroupResultConsumer matchResultConsumer,
            List<Object[]> matchRows) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);

        Map<String, List<String[]>> resultWithBlockKey = computeBlockingKey(analysis, columnMap, matchRows,
                recordMatchingIndicator);
        Iterator<String> keyIterator = resultWithBlockKey.keySet().iterator();

        TreeMap<Object, Long> blockSize2Freq = new TreeMap<Object, Long>();
        while (keyIterator.hasNext()) {
            // Match group with in each block
            List<String[]> matchRowsInBlock = resultWithBlockKey.get(keyIterator.next());
            List<Object[]> objList = new ArrayList<Object[]>();
            objList.addAll(matchRowsInBlock);
            // Add check match key
            try {
                computeMatchGroupResult(columnMap, matchResultConsumer, objList, recordMatchingIndicator);
            } catch (BusinessException e) {
                rc.setOk(Boolean.FALSE);
                rc.setMessage(e.getAdditonalMessage());
                return rc;
            }

            // Store indicator
            Integer blockSize = matchRowsInBlock.size();
            if (blockSize == null) { // should not happen
                blockSize = 0;
            }
            Long freq = blockSize2Freq.get(Long.valueOf(blockSize));
            if (freq == null) {
                freq = 0l;
            }
            blockSize2Freq.put(Long.valueOf(blockSize), freq + 1);
        }
        blockKeyIndicator.setBlockSize2frequency(blockSize2Freq);
        return rc;
    }

    private static Map<String, List<String[]>> computeBlockingKey(Analysis analysis, Map<String, String> columnMap,
            List<Object[]> matchRows, RecordMatchingIndicator recordMatchingIndicator) {
        List<AppliedBlockKey> appliedBlockKeys = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getAppliedBlockKeys();

        List<Map<String, String>> blockKeySchema = new ArrayList<Map<String, String>>();
        for (KeyDefinition keyDef : appliedBlockKeys) {
            AppliedBlockKey appliedKeyDefinition = (AppliedBlockKey) keyDef;
            String column = appliedKeyDefinition.getColumn();

            if (StringUtils.equals(PluginConstant.BLOCK_KEY, column)) {
                // If there exist customized block key defined, get the key parameters.
                List<BlockKeyDefinition> blockKeyDefs = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getBlockKeys();
                for (BlockKeyDefinition blockKeyDef : blockKeyDefs) {
                    Map<String, String> blockKeyDefMap = new HashMap<String, String>();
                    blockKeyDefMap.putAll(getCustomizedBlockKeyParameter(blockKeyDef, blockKeyDef.getColumn()));
                    blockKeySchema.add(blockKeyDefMap);
                }
            } else {
                Map<String, String> blockKeyDefMap = new HashMap<String, String>();
                blockKeyDefMap.put(MatchAnalysisConstant.COLUMN, column);
                blockKeySchema.add(blockKeyDefMap);
            }
        }

        BlockingKeyHandler blockKeyHandler = new BlockingKeyHandler(blockKeySchema, columnMap);
        blockKeyHandler.setInputData(matchRows);
        blockKeyHandler.run();
        Map<String, List<String[]>> resultData = blockKeyHandler.getResultDatas();

        return resultData;

    }

    /**
     * DOC zhao Comment method "getCustomizedBlockKeyParameter".
     * 
     * @param appliedKeyDefinition
     * @param column
     * @return
     */
    private static Map<String, String> getCustomizedBlockKeyParameter(BlockKeyDefinition blockKeydef, String column) {
        String preAlgo = blockKeydef.getPreAlgorithm().getAlgorithmType();
        String preAlgoValue = blockKeydef.getPreAlgorithm().getAlgorithmParameters();
        String algorithm = blockKeydef.getAlgorithm().getAlgorithmType();
        String algorithmValue = blockKeydef.getAlgorithm().getAlgorithmParameters();
        String postAlgo = blockKeydef.getPostAlgorithm().getAlgorithmType();
        String postAlgValue = blockKeydef.getPostAlgorithm().getAlgorithmParameters();
        Map<String, String> blockKeyDefMap = AnalysisRecordGroupingUtils.getBlockingKeyMap(column, preAlgo, preAlgoValue,
                algorithm, algorithmValue, postAlgo, postAlgValue);
        return blockKeyDefMap;
    }

    /**
     * DOC zhao Comment method "computeMatchGroupResult".
     * 
     * @param columnMap
     * @param matchResultConsumer
     * @param matchRows
     * @return
     */
    private static void computeMatchGroupResult(Map<String, String> columnMap, MatchGroupResultConsumer matchResultConsumer,
            List<Object[]> matchRows, RecordMatchingIndicator recordMatchingIndicator) throws BusinessException {
        boolean isOpenWarningDialog = false;
        AnalysisMatchRecordGrouping analysisMatchRecordGrouping = new AnalysisMatchRecordGrouping(matchResultConsumer);
        List<MatchRule> matchRules = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getMatchRules();
        for (MatchRule matcher : matchRules) {
            List<Map<String, String>> ruleMatcherConvertResult = new ArrayList<Map<String, String>>();
            if (matcher == null) {
                continue;
            }
            for (MatchKeyDefinition matchDef : matcher.getMatchKeys()) {
                // check if the current match key does not contain any column,throw exception, do not continue
                if (matchDef.getColumn() == null || StringUtils.EMPTY.equals(matchDef.getColumn())) {
                    BusinessException businessException = new BusinessException();
                    businessException.setAdditonalMessage(DefaultMessagesImpl.getString(
                            "MatchAnalysisExecutor.NoColumnInMatchKey", //$NON-NLS-1$
                            matchDef.getName()));
                    throw businessException;
                }
                Map<String, String> matchKeyMap = AnalysisRecordGroupingUtils.getMatchKeyMap(matchDef.getColumn(), matchDef
                        .getAlgorithm().getAlgorithmType(), matchDef.getAlgorithm().getAlgorithmParameters(), matchDef
                        .getConfidenceWeight(), columnMap, matcher.getMatchInterval(), matchDef.getColumn());
                ruleMatcherConvertResult.add(matchKeyMap);
            }
            analysisMatchRecordGrouping.addRuleMatcher(ruleMatcherConvertResult);
        }
        analysisMatchRecordGrouping.setMatchRows(matchRows);
        // the case for matching key custom Algorithm can not be loaded normal.
        try {
            analysisMatchRecordGrouping.run();
        } catch (InstantiationException e1) {
            isOpenWarningDialog = true;
        } catch (IllegalAccessException e1) {
            isOpenWarningDialog = true;
        } catch (ClassNotFoundException e1) {
            isOpenWarningDialog = true;
        } finally {
            if (isOpenWarningDialog) {
                // Display.getDefault().syncExec(new Runnable() {
                //
                // @Override
                // public void run() {
                // MessageDialog.openError(Display.getCurrent().getActiveShell(),
                //                                DefaultMessagesImpl.getString("ExecuteMatchRuleHandler.run_Error_Titile"), //$NON-NLS-1$
                //                                DefaultMessagesImpl.getString("ExecuteMatchRuleHandler.run_Error_Content")); //$NON-NLS-1$
                // }
                // });
                BusinessException businessException = new BusinessException();
                businessException.setAdditonalMessage(DefaultMessagesImpl.getString("ExecuteMatchRuleHandler.run_Error_Content")); //$NON-NLS-1$
                throw businessException;

            }
        }

    }
}
