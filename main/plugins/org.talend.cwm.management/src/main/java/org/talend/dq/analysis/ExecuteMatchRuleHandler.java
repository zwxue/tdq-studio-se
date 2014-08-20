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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.talend.commons.exception.BusinessException;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.indicators.columnset.BlockKeyIndicator;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.genkey.BlockingKeyHandler;
import org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.IRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;
import org.talend.dataquality.rules.AppliedBlockKey;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.DefaultSurvivorshipDefinition;
import org.talend.dataquality.rules.KeyDefinition;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.RulesPackage;
import org.talend.dataquality.rules.SurvivorshipKeyDefinition;
import org.talend.designer.components.lookup.persistent.IPersistentLookupManager;
import org.talend.dq.analysis.persistent.BlockKey;
import org.talend.dq.analysis.persistent.MatchRow;
import org.talend.dq.helper.CustomAttributeMatcherHelper;
import org.talend.utils.sql.TalendTypeConvert;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * created by zshen on Sep 16, 2013 Detailled comment
 * 
 */
public class ExecuteMatchRuleHandler {

    public TypedReturnCode<MatchGroupResultConsumer> execute(Map<MetadataColumn, String> columnMap,
            RecordMatchingIndicator recordMatchingIndicator, List<Object[]> matchRows, BlockKeyIndicator blockKeyIndicator,
            MatchGroupResultConsumer matchResultConsumer) {

        TypedReturnCode<MatchGroupResultConsumer> returnCode = new TypedReturnCode<MatchGroupResultConsumer>(false);
        returnCode.setObject(matchResultConsumer);

        // By default for analysis, the applied blocking key will be the key
        // from key generation definition. This
        // will be refined when there is a need to define the applied blocking
        // key manually by user later.
        createAppliedBlockKeyByGenKey(recordMatchingIndicator);
        ReturnCode computeMatchGroupReturnCode = null;
        // Blocking key specified.
        computeMatchGroupReturnCode = computeMatchGroupWithBlockKey(recordMatchingIndicator, blockKeyIndicator, columnMap,
                matchResultConsumer, matchRows);
        returnCode.setOk(computeMatchGroupReturnCode.isOk());
        returnCode.setMessage(computeMatchGroupReturnCode.getMessage());
        return returnCode;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public TypedReturnCode<MatchGroupResultConsumer> executeWithStoreOnDisk(Map<MetadataColumn, String> columnMap,
            RecordMatchingIndicator recordMatchingIndicator, BlockKeyIndicator blockKeyIndicator,
            IPersistentLookupManager persistentLookupManager, Map<BlockKey, String> blockKeys,
            MatchGroupResultConsumer matchResultConsumer) throws Exception {

        TypedReturnCode<MatchGroupResultConsumer> returnCode = new TypedReturnCode<MatchGroupResultConsumer>(false);
        // The parameter of "isKeepDataInMemory" should be false in
        // "store on disk" option.
        returnCode.setObject(matchResultConsumer);

        // By default for analysis, the applied blocking key will be the key
        // from key generation definition. This
        // will be refined when there is a need to define the applied blocking
        // key manually by user later.
        createAppliedBlockKeyByGenKey(recordMatchingIndicator);

        persistentLookupManager.initGet();
        TreeMap<Object, Long> blockSize2Freq = new TreeMap<Object, Long>();

        MatchRow matchRow = null;
        boolean isBlockKeyEmpty = blockKeys.isEmpty();
        Iterator<BlockKey> blockKeyIterator = blockKeys.keySet().iterator();
        while (blockKeyIterator.hasNext() || isBlockKeyEmpty) {
            if (isBlockKeyEmpty) {
                // If the block key is empty, only handle the data in one loop.
                // Treat it in one and only one block.
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

            if (recordMatchingIndicator.getBuiltInMatchRuleDefinition().getRecordLinkageAlgorithm()
                    .equals(RecordMatcherType.simpleVSRMatcher.name())) {
                analysisMatchRecordGrouping.setRecordLinkAlgorithm(RecordMatcherType.simpleVSRMatcher);
            } else {
                analysisMatchRecordGrouping.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
                analysisMatchRecordGrouping.setSurvivorShipAlgorithmParams(createSurvivorShipAlgorithmParams(
                        analysisMatchRecordGrouping, recordMatchingIndicator, columnMap));
            }
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
     * DOC zhao Comment method "createAppliedBlockKeyByGenKey".
     * 
     * @param recordMatchingIndicator
     */
    private void createAppliedBlockKeyByGenKey(RecordMatchingIndicator recordMatchingIndicator) {
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
    private ReturnCode computeMatchGroupWithBlockKey(RecordMatchingIndicator recordMatchingIndicator,
            BlockKeyIndicator blockKeyIndicator, Map<MetadataColumn, String> columnMap,
            MatchGroupResultConsumer matchResultConsumer, List<Object[]> matchRows) {
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

    private Map<String, List<String[]>> computeBlockingKey(Map<MetadataColumn, String> columnMap, List<Object[]> matchRows,
            RecordMatchingIndicator recordMatchingIndicator) {
        List<Map<String, String>> blockKeySchema = getBlockKeySchema(recordMatchingIndicator);
        Map<String, String> colName2IndexMap = new HashMap<String, String>();
        for (MetadataColumn metaCol : columnMap.keySet()) {
            colName2IndexMap.put(metaCol.getName(), columnMap.get(metaCol));
        }
        BlockingKeyHandler blockKeyHandler = new BlockingKeyHandler(blockKeySchema, colName2IndexMap);
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
    public List<Map<String, String>> getBlockKeySchema(RecordMatchingIndicator recordMatchingIndicator) {
        List<AppliedBlockKey> appliedBlockKeys = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getAppliedBlockKeys();

        List<Map<String, String>> blockKeySchema = new ArrayList<Map<String, String>>();
        for (KeyDefinition keyDef : appliedBlockKeys) {
            AppliedBlockKey appliedKeyDefinition = (AppliedBlockKey) keyDef;
            String column = appliedKeyDefinition.getColumn();

            if (StringUtils.equals(PluginConstant.BLOCK_KEY, column)) {
                // If there exist customized block key defined, get the key
                // parameters.
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
    private Map<String, String> getCustomizedBlockKeyParameter(BlockKeyDefinition blockKeydef, String column) {
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
    private void computeMatchGroupResult(Map<MetadataColumn, String> columnMap, MatchGroupResultConsumer matchResultConsumer,
            List<Object[]> matchRows, RecordMatchingIndicator recordMatchingIndicator) throws BusinessException {
        boolean isOpenWarningDialog = false;
        AnalysisMatchRecordGrouping analysisMatchRecordGrouping = new AnalysisMatchRecordGrouping(matchResultConsumer);
        setRuleMatcher(columnMap, recordMatchingIndicator, analysisMatchRecordGrouping);
        analysisMatchRecordGrouping.setMatchRows(matchRows);

        try {

            if (recordMatchingIndicator.getBuiltInMatchRuleDefinition().getRecordLinkageAlgorithm()
                    .equals(RecordMatcherType.simpleVSRMatcher.name())) {
                analysisMatchRecordGrouping.setRecordLinkAlgorithm(RecordMatcherType.simpleVSRMatcher);
                analysisMatchRecordGrouping.initialize();
            } else {
                analysisMatchRecordGrouping.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);
                analysisMatchRecordGrouping.initialize();
                SurvivorShipAlgorithmParams survivorShipAlgorithmParams = createSurvivorShipAlgorithmParams(
                        analysisMatchRecordGrouping, recordMatchingIndicator, columnMap);
                analysisMatchRecordGrouping.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);
            }

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

    private SurvivorShipAlgorithmParams createSurvivorShipAlgorithmParams(
            AnalysisMatchRecordGrouping analysisMatchRecordGrouping, RecordMatchingIndicator recordMatchingIndicator,
            Map<MetadataColumn, String> columnMap) {
        SurvivorShipAlgorithmParams survivorShipAlgorithmParams = new SurvivorShipAlgorithmParams();

        // Survivorship functions.
        List<SurvivorshipKeyDefinition> survivorshipKeyDefs = recordMatchingIndicator.getBuiltInMatchRuleDefinition()
                .getSurvivorshipKeys();
        List<SurvivorshipFunction> survFunctions = new ArrayList<SurvivorshipFunction>();
        for (SurvivorshipKeyDefinition survDef : survivorshipKeyDefs) {
            SurvivorshipFunction func = survivorShipAlgorithmParams.new SurvivorshipFunction();
            func.setSurvivorShipKey(survDef.getName());
            func.setParameter(survDef.getFunction().getAlgorithmParameters());
            func.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.getTypeBySavedValue(survDef.getFunction().getAlgorithmType()));
            survFunctions.add(func);
        }
        survivorShipAlgorithmParams
                .setSurviorShipAlgos(survFunctions.toArray(new SurvivorshipFunction[survivorshipKeyDefs.size()]));

        // Set default survivorship functions.
        List<DefaultSurvivorshipDefinition> defSurvDefs = recordMatchingIndicator.getBuiltInMatchRuleDefinition()
                .getDefaultSurvivorshipDefinitions();
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<Integer, SurvivorshipFunction>();

        for (MetadataColumn metaColumn : columnMap.keySet()) {
            String dataTypeName = TalendTypeConvert.convertToJavaType(metaColumn.getTalendType());
            for (DefaultSurvivorshipDefinition defSurvDef : defSurvDefs) {
                if (dataTypeName.equals(defSurvDef.getDataType())) {
                    putNewSurvFunc(columnMap, survivorShipAlgorithmParams, defaultSurvRules, metaColumn, defSurvDef);
                    break;
                } else if (defSurvDef.getDataType().equals("Number") && isNumber(dataTypeName)) { //$NON-NLS-1$
                    putNewSurvFunc(columnMap, survivorShipAlgorithmParams, defaultSurvRules, metaColumn, defSurvDef);
                    break;

                }
            }// End for: if no func defined, then the value will be taken from one of the records in a group (1st
             // one ).
        }

        survivorShipAlgorithmParams.setDefaultSurviorshipRules(defaultSurvRules);

        // Set the record matcher
        CombinedRecordMatcher combinedRecordMatcher = analysisMatchRecordGrouping.getCombinedRecordMatcher();
        survivorShipAlgorithmParams.setRecordMatcher(combinedRecordMatcher);
        Map<IRecordMatcher, SurvivorshipFunction[]> survAlgos = new HashMap<IRecordMatcher, SurvivorshipFunction[]>();
        SurvivorshipFunction[] survFuncs = survivorShipAlgorithmParams.getSurviorShipAlgos();
        Map<Integer, SurvivorshipFunction> colIdx2DefaultSurvFunc = survivorShipAlgorithmParams.getDefaultSurviorshipRules();
        int matchRuleIdx = -1;
        List<List<Map<String, String>>> multiRules = analysisMatchRecordGrouping.getMultiMatchRules();
        for (List<Map<String, String>> matchrule : multiRules) {
            matchRuleIdx++;
            if (matchrule == null) {
                continue;
            }

            SurvivorshipFunction[] surFuncsInMatcher = new SurvivorshipFunction[matchrule.size()];
            int idx = 0;
            for (Map<String, String> mkDef : matchrule) {
                String matcherType = mkDef.get(IRecordGrouping.MATCHING_TYPE);
                if (AttributeMatcherType.DUMMY.name().equals(matcherType)) {
                    // Find the func from default survivorship rule.
                    surFuncsInMatcher[idx] = colIdx2DefaultSurvFunc.get(Integer.valueOf(mkDef.get(IRecordGrouping.COLUMN_IDX)));
                    if (surFuncsInMatcher[idx] == null) {
                        // Use CONCATENATE by default if not specified .
                        surFuncsInMatcher[idx] = survivorShipAlgorithmParams.new SurvivorshipFunction();
                        surFuncsInMatcher[idx].setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.CONCATENATE);
                        surFuncsInMatcher[idx].setParameter(StringUtils.EMPTY);
                    }
                } else {
                    // Find the func from existing survivorship rule list.
                    for (SurvivorshipFunction survFunc : survFuncs) {
                        String keyName = mkDef.get(IRecordGrouping.MATCH_KEY_NAME);
                        if (keyName.equals(survFunc.getSurvivorShipKey())) {
                            surFuncsInMatcher[idx] = survFunc;
                            break;
                        }
                    }

                }
                idx++;
            }

            // Add the funcs to a specific record matcher. NOTE that the index of matcher must be coincidence to the
            // index of match rule.
            survAlgos.put(combinedRecordMatcher.getMatchers().get(matchRuleIdx), surFuncsInMatcher);

        }

        survivorShipAlgorithmParams.setSurvivorshipAlgosMap(survAlgos);

        return survivorShipAlgorithmParams;
    }

    public boolean isNumber(String type) {
        if (ArrayUtils.contains(NUMBERS, type)) {
            return true;
        }
        return false;
    }

    /**
     * Create a new surv function and put it into map given column index as the key.
     * 
     * @param columnMap
     * @param survivorShipAlgorithmParams
     * @param defaultSurvRules
     * @param metaColumn
     * @param defSurvDef
     */
    private void putNewSurvFunc(Map<MetadataColumn, String> columnMap, SurvivorShipAlgorithmParams survivorShipAlgorithmParams,
            Map<Integer, SurvivorshipFunction> defaultSurvRules, MetadataColumn metaColumn,
            DefaultSurvivorshipDefinition defSurvDef) {
        SurvivorshipFunction survFunc = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc.setParameter(defSurvDef.getFunction().getAlgorithmParameters());
        survFunc.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.getTypeBySavedValue(defSurvDef.getFunction()
                .getAlgorithmType()));
        defaultSurvRules.put(Integer.valueOf(columnMap.get(metaColumn)), survFunc);
    }

    /**
     * DOC zhao Comment method "setRuleMatcher".
     * 
     * @param columnMap
     * @param recordMatchingIndicator
     * @param analysisMatchRecordGrouping
     * @throws BusinessException
     */
    private void setRuleMatcher(Map<MetadataColumn, String> columnMap, RecordMatchingIndicator recordMatchingIndicator,
            AnalysisMatchRecordGrouping analysisMatchRecordGrouping) throws BusinessException {
        List<MatchRule> matchRules = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getMatchRules();

        // Column index list store all the indices.
        List<String> allColumnIndice = new ArrayList<String>();
        for (MatchRule matcher : matchRules) {
            List<Map<String, String>> currentRuleMatcher = new ArrayList<Map<String, String>>();
            if (matcher == null) {
                continue;
            }
            List<String> currentColumnIndice = new ArrayList<String>();
            for (MatchKeyDefinition matchDef : matcher.getMatchKeys()) {
                // check if the current match key does not contain any
                // column,throw exception, do not continue
                if (matchDef.getColumn() == null || StringUtils.EMPTY.equals(matchDef.getColumn())) {
                    BusinessException businessException = new BusinessException();
                    businessException.setAdditonalMessage(Messages.getString("MatchAnalysisExecutor.NoColumnInMatchKey", //$NON-NLS-1$
                            matchDef.getName()));
                    throw businessException;
                }
                String algorithmType = matchDef.getAlgorithm().getAlgorithmType();
                Map<String, String> matchKeyMap = getMatchKeyMap(columnMap, matcher, matchDef, algorithmType);
                addMatchKeyOrderbyColumnIdx(currentRuleMatcher, matchKeyMap);
                currentColumnIndice.add(matchKeyMap.get(IRecordGrouping.COLUMN_IDX));
            }

            if (allColumnIndice.isEmpty()) {
                for (Map<String, String> matchKey : currentRuleMatcher) {
                    String colIdx = matchKey.get(IRecordGrouping.COLUMN_IDX);
                    allColumnIndice.add(colIdx);
                }

            } else {
                refineMatcherWithDummy(analysisMatchRecordGrouping, allColumnIndice, currentColumnIndice, currentRuleMatcher);
            }
            analysisMatchRecordGrouping.addRuleMatcher(currentRuleMatcher);
        }
    }

    private void refineMatcherWithDummy(AnalysisMatchRecordGrouping analysisMatchRecordGrouping, List<String> allColumnIndice,
            List<String> currentColumnIndice, List<Map<String, String>> currentRuleMatcher) {
        List<List<Map<String, String>>> multiMatchRules = analysisMatchRecordGrouping.getMultiMatchRules();
        // Refine the other matchers with dummy matcher
        for (Map<String, String> matchKey : currentRuleMatcher) {
            String colIdx = matchKey.get(IRecordGrouping.COLUMN_IDX);
            if (!allColumnIndice.contains(colIdx)) {
                allColumnIndice.add(colIdx);
                // Create dummy matcher
                Map<String, String> dummyMatcherMap = new HashMap<String, String>();
                dummyMatcherMap.put(IRecordGrouping.COLUMN_IDX, colIdx);
                dummyMatcherMap.put(IRecordGrouping.MATCHING_TYPE, AttributeMatcherType.DUMMY.name());

                // Refine the multi match rules with dummy matcher.
                for (List<Map<String, String>> matchRule : multiMatchRules) {
                    addMatchKeyOrderbyColumnIdx(matchRule, dummyMatcherMap);
                }
            }
        }

        // Refine current matcher with dummy matcher by the knowledge of the
        // previous matcher.
        if (multiMatchRules != null && multiMatchRules.size() > 0) {
            // Here the 0 index is safe because all matchers before are
            // same record size with same match key.
            List<Map<String, String>> preMatcher = multiMatchRules.get(0);
            for (Map<String, String> preMatchKey : preMatcher) {
                String colIdx = preMatchKey.get(IRecordGrouping.COLUMN_IDX);
                if (!currentColumnIndice.contains(colIdx)) {
                    // Create dummy matcher
                    Map<String, String> dummyMatcherMap = new HashMap<String, String>();
                    dummyMatcherMap.put(IRecordGrouping.COLUMN_IDX, colIdx);
                    dummyMatcherMap.put(IRecordGrouping.MATCHING_TYPE, AttributeMatcherType.DUMMY.name());
                    addMatchKeyOrderbyColumnIdx(currentRuleMatcher, dummyMatcherMap);
                }
            }
        }
    }

    private void addMatchKeyOrderbyColumnIdx(List<Map<String, String>> currentRuleMatcher, Map<String, String> matchKeyMap) {
        int index = 0;
        for (Map<String, String> currentMatchKey : currentRuleMatcher) {
            int currColIdx = Integer.valueOf(currentMatchKey.get(IRecordGrouping.COLUMN_IDX));
            int toBeInsertColIdx = Integer.valueOf(matchKeyMap.get(IRecordGrouping.COLUMN_IDX));
            if (currColIdx > toBeInsertColIdx) {
                currentRuleMatcher.add(index, matchKeyMap);
                return;
            }
            index++;
        }
        currentRuleMatcher.add(matchKeyMap);
    }

    private Map<String, String> getMatchKeyMap(Map<MetadataColumn, String> columnMap, MatchRule matcher,
            MatchKeyDefinition matchDef, String algorithmType) {
        Map<String, String> matchKeyMap = null;
        if (AttributeMatcherType.get(algorithmType) == AttributeMatcherType.CUSTOM) {
            matchKeyMap = AnalysisRecordGroupingUtils.getMatchKeyMap(matchDef.getColumn(), algorithmType, matchDef.getAlgorithm()
                    .getAlgorithmParameters(), matchDef.getConfidenceWeight(), matchDef.getThreshold(), columnMap, matcher
                    .getMatchInterval(), matchDef.getColumn(), matchDef.getName(), matchDef.getHandleNull(),
                    CustomAttributeMatcherHelper.getFullJarPath(matchDef.getAlgorithm().getAlgorithmParameters()));
        } else {
            matchKeyMap = AnalysisRecordGroupingUtils.getMatchKeyMap(matchDef.getColumn(), algorithmType, matchDef.getAlgorithm()
                    .getAlgorithmParameters(), matchDef.getConfidenceWeight(), matchDef.getThreshold(), columnMap, matcher
                    .getMatchInterval(), matchDef.getColumn(), matchDef.getName(), matchDef.getHandleNull(), null);
        }
        return matchKeyMap;
    }

    private final static String[] NUMBERS = new String[] { "Integer", "Float", "Double", "Number", "Long" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
}
