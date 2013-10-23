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
package org.talend.dq.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.talend.commons.exception.BusinessException;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.indicators.columnset.BlockKeyIndicator;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.record.linkage.genkey.BlockingKeyHandler;
import org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;
import org.talend.dataquality.record.linkage.utils.AnalysisRecordGroupingUtils;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.AppliedBlockKey;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.KeyDefinition;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.RulesPackage;
import org.talend.designer.components.lookup.persistent.IPersistentLookupManager;
import org.talend.dq.analysis.persistent.BlockKey;
import org.talend.dq.analysis.persistent.MatchRow;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * created by zshen on Sep 16, 2013 Detailled comment
 * 
 */
public class ExecuteMatchRuleHandler {

    public static TypedReturnCode<MatchGroupResultConsumer> execute(Map<String, String> columnMap,
            RecordMatchingIndicator recordMatchingIndicator, List<Object[]> matchRows, BlockKeyIndicator blockKeyIndicator) {
        TypedReturnCode<MatchGroupResultConsumer> returnCode = new TypedReturnCode<MatchGroupResultConsumer>(false);
        MatchGroupResultConsumer matchResultConsumer = createMatchGroupResultConsumer(columnMap, recordMatchingIndicator,
                Boolean.TRUE);
        returnCode.setObject(matchResultConsumer);

        // By default for analysis, the applied blocking key will be the key from key generation definition. This
        // will be refined when there is a need to define the applied blocking key manually by user later.
        createAppliedBlockKeyByGenKey(recordMatchingIndicator);

        // Blocking key specified.
        ReturnCode computeMatchGroupReturnCode = computeMatchGroupWithBlockKey(recordMatchingIndicator, blockKeyIndicator,
                columnMap, matchResultConsumer, matchRows);
        returnCode.setOk(computeMatchGroupReturnCode.isOk());
        returnCode.setMessage(computeMatchGroupReturnCode.getMessage());

        return returnCode;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static TypedReturnCode<MatchGroupResultConsumer> executeWithStoreOnDisk(Map<String, String> columnMap,
            RecordMatchingIndicator recordMatchingIndicator, BlockKeyIndicator blockKeyIndicator,
            IPersistentLookupManager persistentLookupManager, Map<BlockKey, String> blockKeys) throws Exception {
        TypedReturnCode<MatchGroupResultConsumer> returnCode = new TypedReturnCode<MatchGroupResultConsumer>(false);
        // The parameter of "isKeepDataInMemory" should be false in "store on disk" option.
        MatchGroupResultConsumer matchResultConsumer = createMatchGroupResultConsumer(columnMap, recordMatchingIndicator,
                Boolean.FALSE);
        returnCode.setObject(matchResultConsumer);

        // By default for analysis, the applied blocking key will be the key from key generation definition. This
        // will be refined when there is a need to define the applied blocking key manually by user later.
        createAppliedBlockKeyByGenKey(recordMatchingIndicator);

        persistentLookupManager.initGet();
        TreeMap<Object, Long> blockSize2Freq = new TreeMap<Object, Long>();

        MatchRow matchRow = null;
        boolean isBlockKeyEmpty = blockKeys.isEmpty();
        Iterator<BlockKey> blockKeyIterator = blockKeys.keySet().iterator();
        while (blockKeyIterator.hasNext() || isBlockKeyEmpty) {
            if (isBlockKeyEmpty) {
                // If the block key is empty, only handle the data in one loop. Treat it in one and only one block.
                isBlockKeyEmpty = Boolean.FALSE;
                matchRow = new MatchRow(columnMap.size(), 0);
            } else {
                // Lookup rows given blocking key
                BlockKey blockKey = blockKeyIterator.next();
                if (matchRow == null) {
                    matchRow = new MatchRow(columnMap.size(), blockKey.getBlockKey().size());
                }
                matchRow.setKey(blockKey.getBlockKey());
                matchRow.hashCodeDirty = true;
            }
            AnalysisMatchRecordGrouping analysisMatchRecordGrouping = new AnalysisMatchRecordGrouping(matchResultConsumer);
            // Set rule matcher for record grouping API.
            setRuleMatcher(columnMap, recordMatchingIndicator, analysisMatchRecordGrouping);
            analysisMatchRecordGrouping.initialize();

            persistentLookupManager.lookup(matchRow);
            Integer blockSize = 0;
            while (persistentLookupManager.hasNext()) {
                // Match within one block
                MatchRow row = (MatchRow) persistentLookupManager.next();
                List<String> rowWithBlockKey = row.getRowWithBlockKey();
                analysisMatchRecordGrouping.doGroup(rowWithBlockKey.toArray(new String[rowWithBlockKey.size()]));
                blockSize++;
            }
            analysisMatchRecordGrouping.end();

            // Store indicator
            Long freq = blockSize2Freq.get(Long.valueOf(blockSize));
            if (freq == null) {
                freq = 0l;
            }
            blockSize2Freq.put(Long.valueOf(blockSize), freq + 1);
        }
        persistentLookupManager.endGet();
        blockKeyIndicator.setBlockSize2frequency(blockSize2Freq);

        return returnCode;
    }

    /**
     * DOC zhao Comment method "initRecordMatchIndicator".
     * 
     * @param columnMap
     * @return
     */
    private static MatchGroupResultConsumer createMatchGroupResultConsumer(Map<String, String> columnMap,
            final RecordMatchingIndicator recordMatchingIndicator, Boolean isKeepDataInMemory) {
        recordMatchingIndicator.setMatchRowSchema(AnalysisRecordGroupingUtils.getCompleteColumnSchema(columnMap));
        recordMatchingIndicator.reset();
        MatchGroupResultConsumer matchResultConsumer = new MatchGroupResultConsumer(isKeepDataInMemory) {

            /*
             * (non-Javadoc)
             * 
             * @see org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer#handle(java.lang.Object)
             */
            @Override
            public void handle(Object row) {
                recordMatchingIndicator.handle(row);
                if (this.isKeepDataInMemory) {
                    addOneRowOfResult(row);
                }
            }
        };
        return matchResultConsumer;
    }

    /**
     * DOC zhao Comment method "createAppliedBlockKeyByGenKey".
     * 
     * @param recordMatchingIndicator
     */
    private static void createAppliedBlockKeyByGenKey(RecordMatchingIndicator recordMatchingIndicator) {
        List<AppliedBlockKey> appliedBlockKeys = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getAppliedBlockKeys();
        appliedBlockKeys.clear();
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
     * @param recordMatchingIndicator
     * @param blockKeyIndicator
     * @param columnMap
     * @param matchResultConsumer
     * @param matchRows
     */
    private static ReturnCode computeMatchGroupWithBlockKey(RecordMatchingIndicator recordMatchingIndicator,
            BlockKeyIndicator blockKeyIndicator, Map<String, String> columnMap, MatchGroupResultConsumer matchResultConsumer,
            List<Object[]> matchRows) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);

        Map<String, List<String[]>> resultWithBlockKey = computeBlockingKey(columnMap, matchRows, recordMatchingIndicator);
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

    private static Map<String, List<String[]>> computeBlockingKey(Map<String, String> columnMap, List<Object[]> matchRows,
            RecordMatchingIndicator recordMatchingIndicator) {
        List<Map<String, String>> blockKeySchema = getBlockKeySchema(recordMatchingIndicator);

        BlockingKeyHandler blockKeyHandler = new BlockingKeyHandler(blockKeySchema, columnMap);
        blockKeyHandler.setInputData(matchRows);
        blockKeyHandler.run();
        Map<String, List<String[]>> resultData = blockKeyHandler.getResultDatas();

        return resultData;

    }

    /**
     * mzhao Get block key schema given the record matching indicator.
     * 
     * @param recordMatchingIndicator
     * @return
     */
    public static List<Map<String, String>> getBlockKeySchema(RecordMatchingIndicator recordMatchingIndicator) {
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
                blockKeyDefMap.put(MatchAnalysisConstant.PRECOLUMN, column);
                blockKeySchema.add(blockKeyDefMap);
            }
        }
        return blockKeySchema;
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
        setRuleMatcher(columnMap, recordMatchingIndicator, analysisMatchRecordGrouping);
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
                BusinessException businessException = new BusinessException();
                throw businessException;

            }
        }

    }

    /**
     * DOC zhao Comment method "setRuleMatcher".
     * 
     * @param columnMap
     * @param recordMatchingIndicator
     * @param analysisMatchRecordGrouping
     * @throws BusinessException
     */
    private static void setRuleMatcher(Map<String, String> columnMap, RecordMatchingIndicator recordMatchingIndicator,
            AnalysisMatchRecordGrouping analysisMatchRecordGrouping) throws BusinessException {
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
                    businessException.setAdditonalMessage(Messages.getString("MatchAnalysisExecutor.NoColumnInMatchKey", //$NON-NLS-1$
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
    }
}
