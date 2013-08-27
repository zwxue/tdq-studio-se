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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.cwm.db.connection.DatabaseSQLExecutor;
import org.talend.cwm.db.connection.DelimitedFileSQLExecutor;
import org.talend.cwm.db.connection.ISQLExecutor;
import org.talend.cwm.db.connection.MDMSQLExecutor;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.record.linkage.grouping.AnalysisMatchRecordGrouping;
import org.talend.dataquality.record.linkage.grouping.MatchGroupResultConsumer;
import org.talend.dataquality.record.linkage.utils.AnalysisRecordGroupingUtils;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * created by zhao on Aug 20, 2013 Detailled comment
 * 
 */
public class MatchAnalysisExecutor implements IAnalysisExecutor {

    private static Logger log = Logger.getLogger(MatchAnalysisExecutor.class);

    // TODO yyin set the analysis running monitoring.
    private IProgressMonitor monitor;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.IAnalysisExecutor#execute(org.talend.dataquality.analysis.Analysis)
     */
    public ReturnCode execute(Analysis analysis) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        RecordMatchingIndicator recordMatchingIndicator = null;
        for (Indicator ind : indicators) {
            if (ind instanceof RecordMatchingIndicator) {
                recordMatchingIndicator = (RecordMatchingIndicator) ind;
            }
        }
        if (recordMatchingIndicator == null) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }

        List<ModelElement> anlayzedElements = analysis.getContext().getAnalysedElements();
        if (anlayzedElements == null || anlayzedElements.size() == 0) {
            rc.setOk(Boolean.FALSE);
            // TODO yyin popup message to notify empty analyzed element
            return rc;
        }
        Map<String, String> columnMap = getColumn2IndexMap(anlayzedElements);

        MatchGroupResultConsumer matchResultConsumer = createMatchGroupResultConsumer(columnMap, recordMatchingIndicator);

        ISQLExecutor sqlExecutor = getSQLExectutor(anlayzedElements);
        if (sqlExecutor == null) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }
        List<Object[]> matchRows = new ArrayList<Object[]>();
        try {
            matchRows = sqlExecutor.executeQuery(analysis);
        } catch (SQLException e) {
            log.error(e, e);
            rc.setOk(Boolean.FALSE);
            return rc;
        }
        computeMatchGroupResult(columnMap, matchResultConsumer, matchRows, recordMatchingIndicator);

        return rc;
    }

    /**
     * DOC zhao Comment method "getColumn2IndexMap".
     * 
     * @param anlayzedElements
     * @return
     */
    private Map<String, String> getColumn2IndexMap(List<ModelElement> anlayzedElements) {
        Map<String, String> columnMap = new HashMap<String, String>();
        int index = 0;
        for (ModelElement column : anlayzedElements) {
            columnMap.put(column.getName(), String.valueOf(index++));
        }
        return columnMap;
    }

    /**
     * DOC zhao Comment method "computeMatchGroupResult".
     * 
     * @param columnMap
     * @param matchResultConsumer
     * @param matchRows
     */
    private void computeMatchGroupResult(Map<String, String> columnMap, MatchGroupResultConsumer matchResultConsumer,
            List<Object[]> matchRows, RecordMatchingIndicator recordMatchingIndicator) {
        AnalysisMatchRecordGrouping analysisMatchRecordGrouping = new AnalysisMatchRecordGrouping(matchResultConsumer);
        List<MatchRule> matchRules = recordMatchingIndicator.getBuiltInMatchRuleDefinition().getMatchRules();
        for (MatchRule matcher : matchRules) {
            List<Map<String, String>> ruleMatcherConvertResult = new ArrayList<Map<String, String>>();
            if (matcher == null) {
                continue;
            }
            for (MatchKeyDefinition matchDef : matcher.getMatchKeys()) {
                Map<String, String> matchKeyMap = AnalysisRecordGroupingUtils
                        .getMatchKeyMap(matchDef.getColumn(), matchDef.getAlgorithm().getAlgorithmType(),
                                matchDef.getConfidenceWeight(), columnMap, matcher.getMatchInterval());
                ruleMatcherConvertResult.add(matchKeyMap);
            }
            analysisMatchRecordGrouping.addRuleMatcher(ruleMatcherConvertResult);
        }
        analysisMatchRecordGrouping.setMatchRows(matchRows);
        analysisMatchRecordGrouping.run();

    }

    /**
     * DOC zhao Comment method "initRecordMatchIndicator".
     * 
     * @param columnMap
     * @return
     */
    private MatchGroupResultConsumer createMatchGroupResultConsumer(Map<String, String> columnMap,
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

            }
        };
        return matchResultConsumer;
    }

    /**
     * DOC zhao Comment method "getSQLExectutor".
     * 
     * @param modelElement
     * @return
     */
    private ISQLExecutor getSQLExectutor(List<ModelElement> anlayzedElements) {
        ModelElement modelElement = anlayzedElements.get(0);
        ISQLExecutor sqlExecutor = null;
        if (modelElement instanceof TdColumn) {
            sqlExecutor = new DatabaseSQLExecutor();
        } else if (modelElement instanceof TdXmlElementType) {
            sqlExecutor = new MDMSQLExecutor();
        } else if (modelElement instanceof MetadataColumn) {
            sqlExecutor = new DelimitedFileSQLExecutor();
        }
        return sqlExecutor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.IAnalysisExecutor#setMonitor(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }
}
