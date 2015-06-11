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
package org.talend.datascience.common.recordlinkage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.genkey.BlockingKeyHandler;
import org.talend.dataquality.record.linkage.grouping.AbstractRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.IRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.swoosh.DQAttribute;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams;
import org.talend.dataquality.record.linkage.grouping.swoosh.SurvivorShipAlgorithmParams.SurvivorshipFunction;
import org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;
import org.talend.datascience.common.inference.Analyzer;

/**
 * Sting clustering analyzer.
 * 
 * @author zhao
 *
 */
public class StringsClusterAnalyzer implements Analyzer<StringsCluster> {

    private static Logger log = Logger.getLogger(StringsClusterAnalyzer.class);

    private IRecordGrouping<Object> recordGroup = null;

    private BlockingKeyHandler blockKeyHandler = null;

    private List<Map<String, String>> matchingRule = null;

    private List<Object[]> inputList = null;

    private String survivorStr = null;

    private List<String> slavesOfGroup = null;

    private int countOfGroup = 0;

    private StringsCluster stringsCluster = null;

    private BlockingKeyAlgorithmEnum blockingKeyAlgo = BlockingKeyAlgorithmEnum.FINGERPRINTKEY;

    public void setBlockingKeyAlgo(BlockingKeyAlgorithmEnum blockingKeyAlgo) {
        this.blockingKeyAlgo = blockingKeyAlgo;
    }

    public void init() {
        survivorStr = null;
        slavesOfGroup = new ArrayList<String>();
        countOfGroup = 0;
        inputList = new ArrayList<Object[]>();
        stringsCluster = new StringsCluster();
        // Blocking the data given fingerprint key
        String columnName = "NAME";
        List<Map<String, String>> blockKeySchema = new ArrayList<Map<String, String>>();
        Map<String, String> blockKeyDefMap = new HashMap<String, String>();

        blockKeyDefMap.put(MatchAnalysisConstant.PRECOLUMN, columnName);
        blockKeyDefMap.put(MatchAnalysisConstant.KEY_ALGO, blockingKeyAlgo.getValue());
        blockKeySchema.add(blockKeyDefMap);

        Map<String, String> colName2IndexMap = new HashMap<String, String>();
        colName2IndexMap.put(columnName, String.valueOf(0));
        blockKeyHandler = new BlockingKeyHandler(blockKeySchema, colName2IndexMap);
        // Initialize group and match rule instances
        // Do grouping given swoosh algorithm with Dummy matcher.
        recordGroup = new AbstractRecordGrouping<Object>() {

            @Override
            protected boolean isMaster(Object col) {
                return "true".equals(col.toString());
            }

            @Override
            protected Object[] createTYPEArray(int size) {
                return new Object[size];
            }

            @Override
            protected void outputRow(Object[] row) {
                if ("true".equals(row[3])) {
                    countOfGroup = Integer.valueOf(row[row.length - 5].toString());
                    survivorStr = row[0].toString();
                } else {
                    slavesOfGroup.add(row[0].toString());
                }
            }

            @Override
            protected void outputRow(RichRecord row) {
                List<DQAttribute<?>> originRow = row.getOutputRow(swooshGrouping.getOldGID2New());
                String[] strRow = new String[originRow.size()];
                int idx = 0;
                for (DQAttribute<?> attr : originRow) {
                    strRow[idx] = attr.getValue();
                    idx++;
                }
                outputRow(strRow);

            }

            @Override
            protected String incrementGroupSize(Object oldGroupSize) {
                return String.valueOf(Integer.parseInt(String.valueOf(oldGroupSize)) + 1);
            }

            @Override
            protected String castAsType(Object objectValue) {
                return String.valueOf(objectValue);
            }
        };
        recordGroup.setRecordLinkAlgorithm(RecordMatcherType.T_SwooshAlgorithm);

        SurvivorShipAlgorithmParams survivorShipAlgorithmParams = new SurvivorShipAlgorithmParams();
        SurvivorshipFunction func = survivorShipAlgorithmParams.new SurvivorshipFunction();
        func.setParameter(""); //$NON-NLS-1$
        func.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);

        survivorShipAlgorithmParams.setSurviorShipAlgos(new SurvivorshipFunction[]{func});
        recordGroup.setSurvivorShipAlgorithmParams(survivorShipAlgorithmParams);

        // // Set default survivorship functions.
        Map<Integer, SurvivorshipFunction> defaultSurvRules = new HashMap<Integer, SurvivorshipFunction>();
        SurvivorshipFunction survFunc = survivorShipAlgorithmParams.new SurvivorshipFunction();
        survFunc.setParameter(StringUtils.EMPTY);
        survFunc.setSurvivorShipAlgoEnum(SurvivorShipAlgorithmEnum.MOST_COMMON);
        defaultSurvRules.put(0, survFunc);

        survivorShipAlgorithmParams.setDefaultSurviorshipRules(defaultSurvRules);

        // recordGroup.setColumnDelimiter(columnDelimiter);
        recordGroup.setIsLinkToPrevious(Boolean.FALSE);
        matchingRule = new ArrayList<Map<String, String>>();
        Map<String, String> lnameRecords = new HashMap<String, String>();
        lnameRecords.put(IRecordGrouping.COLUMN_IDX, String.valueOf(0));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_NAME, columnName);
        lnameRecords.put(IRecordGrouping.MATCHING_TYPE, AttributeMatcherType.DUMMY.name());
        lnameRecords.put(IRecordGrouping.CONFIDENCE_WEIGHT, String.valueOf(1));
        lnameRecords.put(IRecordGrouping.ATTRIBUTE_THRESHOLD, String.valueOf(0.9));
        lnameRecords.put(IRecordGrouping.RECORD_MATCH_THRESHOLD, String.valueOf(0.95f));
        matchingRule.add(lnameRecords);
        recordGroup.setIsOutputDistDetails(false);
    }

    public boolean analyze(String... record) {
        if (record == null || record.length != 1) {
            return false;
        }
        // FIXME whether multiple fields should be considered ?
        inputList.add(new Object[] { record[0] });
        return false;
    }

    public void end() {
        blockKeyHandler.setInputData(inputList);
        // Run blocking
        blockKeyHandler.run();
        Map<String, List<String[]>> resultData = blockKeyHandler.getResultDatas();
        // Do grouping with dummy matcher
        try {
            // loop on all input rows.
            for (List<String[]> strings : resultData.values()) {
                recordGroup.addMatchRule(matchingRule);
                recordGroup.initialize();
                // for each block
                for (Object[] inputRow : strings) {
                    // Wont pass blocking key name of index 1.
                    recordGroup.doGroup(new Object[]{inputRow[0]});
                }
                recordGroup.end();
                stringsCluster.put(countOfGroup, survivorStr);
                stringsCluster.put(countOfGroup, slavesOfGroup);
                // Clear the strings of this group
                survivorStr = null;
                countOfGroup = 0;
                slavesOfGroup = new ArrayList<String>();
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<StringsCluster> getResult() {
        List<StringsCluster> cluster = new ArrayList<StringsCluster>();
        cluster.add(stringsCluster);
        return cluster;
    }

}
