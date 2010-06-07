// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.indicators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EMap;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.analysis.impl.AnalyzedDataSetImpl;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.LowFrequencyIndicator;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dataquality.indicators.SoundexLowFreqIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.utils.collections.MultiMapHelper;
import org.talend.utils.sugars.ReturnCode;

/**
 * @author scorreia
 * 
 * Computes indicators on columns with java engine. It means that we call indicator.handle(object) method.
 */
public class IndicatorEvaluator extends Evaluator<String> {

    private static Logger log = Logger.getLogger(IndicatorEvaluator.class);

    private Analysis analysis = null;

    public IndicatorEvaluator(Analysis analysis) {
        this.analysis = analysis;
    }

    protected ReturnCode executeSqlQuery(String sqlStatement) throws SQLException {
        ReturnCode ok = new ReturnCode(true);
        // check analyzed columns
        Set<String> columns = getAnalyzedElements();
        // feature 0010630 zshen:Make order unify which columns and columnName in the sqlStatement.mssqlOdbc need do
        // this
        List<String> columnlist = sortColumnName(columns, sqlStatement);
        if (columnlist.isEmpty()) {
            ok.setReturnCode(Messages.getString("IndicatorEvaluator.DefineAnalyzedColumns"), false); //$NON-NLS-1$
            return ok;
        }
        // create query statement
        // feature 0010630 zshen: Tables are not found when using Excel with ODBC connection
        Statement statement = null;
        statement = connection.createStatement();
        // ~10630
        statement.setFetchSize(fetchSize);
        // MOD xqliu 2009-02-09 bug 6237
        if (continueRun()) {
            if (log.isInfoEnabled()) {
                log.info("Executing query: " + sqlStatement);
            }
            statement.execute(sqlStatement);
        }
        // get the results
        ResultSet resultSet = statement.getResultSet();
        if (resultSet == null) {
            String mess = "No result set for this statement: " + sqlStatement;
            log.warn(mess);
            ok.setReturnCode(mess, false);
            return ok;
        }
        // MOD mzhao feature: 12919, add capability to dill down data on Java engine.
        AnalysisResult anaResult = analysis.getResults();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = anaResult.getIndicToRowMap();
        indicToRowMap.clear();
        int recordIncrement = 0;
        // MOD zshen compute the num of data which contain in resultSet.
        // resultSet.last();
        // int totalResultNum = resultSet.getRow();
        // resultSet.beforeFirst();
        // --- for each row
        label: while (resultSet.next()) {
            // --- for each column
            // feature 0010630 zshen: dislodge the Qualifiers from name of the column
            for (int i = 0; i < columnlist.size(); i++) {
                String col = columnlist.get(i);
                List<Indicator> indicators = getIndicators(col);
                int offset = col.lastIndexOf('.') + 1;
                col = col.substring(offset);
                // ~
                // --- get content of column
                Object object = resultSet.getObject(col);
                // MOD zshen, when the type of object is TIMESTAMP then need getTimestamp(col) to get correct value,or
                // the value only is the name of type and can't be match with TIMESTAMP.
                if (object != null && !(object instanceof String) && object.toString().indexOf("TIMESTAMP") > -1) {
                    object = resultSet.getTimestamp(col);
                }
                // --- give row to handle to indicators
                for (Indicator indicator : indicators) {
                    // MOD xqliu 2009-02-09 bug 6237
                    if (!continueRun()) {
                        break label;
                    }
                    indicator.handle(object);
                    // ~MOD mzhao feature: 12919
                    if (analysis.getParameters().isStoreData() && indicator.mustStoreRow()) {
                        List<Object[]> valueObjectList = initDataSet(indicator, indicToRowMap, object);
                        // MOD zshen add another loop to insert all of columnValue on the row into indicator.
                        recordIncrement = valueObjectList.size();
                        for (int j = 0; j < resultSet.getMetaData().getColumnCount(); j++) {
                            List<TdColumn> columnList = TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(indicator
                                    .getAnalyzedElement().eContainer()));
                            String newcol = columnList.get(j).getName();
                            Object newobject = resultSet.getObject(newcol);
                            if (newobject != null && !(newobject instanceof String)
                                    && newobject.toString().indexOf("TIMESTAMP") > -1) {
                                newobject = resultSet.getTimestamp(newcol);
                            }
                            if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                                if (recordIncrement < valueObjectList.size()) {
                                    valueObjectList.get(recordIncrement)[j] = newobject;
                                } else {
                                    Object[] valueObject = new Object[resultSet.getMetaData().getColumnCount()];
                                    valueObject[j] = newobject;
                                    valueObjectList.add(valueObject);
                                }
                            }
                        }
                        // ~
                    } else if (indicator instanceof UniqueCountIndicator
                            && analysis.getResults().getIndicToRowMap().get(indicator) != null) {
                        List<Object[]> removeValueObjectList = analysis.getResults().getIndicToRowMap().get(indicator).getData();
                        List<TdColumn> columnElementList = TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(indicator
                                .getAnalyzedElement().eContainer()));
                        int offsetting = columnElementList.indexOf(indicator.getAnalyzedElement());
                        for (Object[] dataObject : removeValueObjectList) {
                            if (dataObject[offsetting].equals(object)) {
                                removeValueObjectList.remove(dataObject);
                                break;
                            }
                        }
                    }
                }
            }

        }
        // --- release resultset
        resultSet.close();
        // --- close
        connection.close();
        return ok;
    }

    private List<Object[]> initDataSet(Indicator indicator, EMap<Indicator, AnalyzedDataSet> indicToRowMap, Object object) {
        AnalyzedDataSet analyzedDataSet = indicToRowMap.get(indicator);
        List<Object[]> valueObjectList = null;
        if (analyzedDataSet == null) {
            analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
            indicToRowMap.put(indicator, analyzedDataSet);
            analyzedDataSet.setDataCount(analysis.getParameters().getMaxNumberRows());
            analyzedDataSet.setRecordSize(0);
        }

        if (indicator instanceof LowFrequencyIndicator || indicator instanceof SoundexLowFreqIndicator) {
            Map<Object, List<Object[]>> valueObjectListMap = analyzedDataSet.getFrequencyData();
            if (valueObjectListMap == null) {
                valueObjectListMap = new HashMap<Object, List<Object[]>>();
                analyzedDataSet.setFrequencyData(valueObjectListMap);
            }
            String key = null;
            if (object == null) {
                key = "Null field";
            } else if (object.equals("")) {
                key = "Empty field";
            } else if (indicator instanceof PatternLowFreqIndicator) {
                key = ((PatternLowFreqIndicator) indicator).convertCharacters(object.toString());
            } else {
                key = object.toString();
            }
            valueObjectList = valueObjectListMap.get(key);
            if (valueObjectList == null) {
                valueObjectList = new ArrayList<Object[]>();
                valueObjectListMap.put(key, valueObjectList);
            }

        } else if (indicator.isInValidRow() || indicator.isValidRow()) {
            List<Object> patternData = analyzedDataSet.getPatternData();
            if (patternData == null) {
                patternData = new ArrayList<Object>();
                patternData.add(new ArrayList<Object[]>());
                patternData.add(new ArrayList<Object[]>());
                analyzedDataSet.setPatternData(patternData);
            }
            if (indicator.isInValidRow()) {
                if (patternData.get(AnalyzedDataSetImpl.INVALID_VALUE) instanceof List<?>) {
                    valueObjectList = (ArrayList<Object[]>) patternData.get(AnalyzedDataSetImpl.INVALID_VALUE);
                }
            } else {
                valueObjectList = (ArrayList<Object[]>) patternData.get(AnalyzedDataSetImpl.VALID_VALUE);
            }
        } else {
            valueObjectList = analyzedDataSet.getData();
            if (valueObjectList == null) {
                valueObjectList = new ArrayList<Object[]>();
                analyzedDataSet.setData(valueObjectList);
            }
        }
        return valueObjectList;
    }

    /**
     * 
     * @author zshen
     * @param columns
     * @param sqlStatement
     * @return the same order List which columnName in the sqlStatement
     */
    public List<String> sortColumnName(Set<String> columns, String sqlStatement) {
        List<String> columnNameList = new ArrayList<String>();
        Map<Object, Object> offset = new HashMap<Object, Object>();
        for (String col : columns) {
            int offsetCol = col.lastIndexOf('.') + 1;
            String colName = col.substring(offsetCol);

            int location = sqlStatement.indexOf(colName);
            offset.put(location, col);
        }

        Integer[] keyArray = (Integer[]) offset.keySet().toArray(new Integer[offset.keySet().size()]);
        int temp = 0;
        for (int i = 0; i < columns.size(); i++) {
            for (int j = keyArray.length - 1; j > i; j--) {
                if (keyArray[j] < keyArray[j - 1]) {
                    temp = keyArray[j];
                    keyArray[j] = keyArray[j - 1];
                    keyArray[j - 1] = temp;
                }
            }
            columnNameList.add((String) offset.get(keyArray[i]));

        }
        return columnNameList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.indicators.Evaluator#storeIndicator(java.lang.Object,
     * org.talend.dataquality.indicators.Indicator)
     * 
     * MOD scorreia 2009-04-24 overrided to solve bug 7093
     */
    @Override
    public boolean storeIndicator(String elementToAnalyze, Indicator indicator) {
        boolean ok = true;
        final List<Indicator> indicatorLeaves = IndicatorHelper.getIndicatorLeaves(indicator);
        this.allIndicators.addAll(indicatorLeaves);
        for (Indicator leaf : indicatorLeaves) {
            if (!MultiMapHelper.addUniqueObjectToListMap(elementToAnalyze, leaf, elementToIndicators)) {
                ok = false;
            }
        }
        return ok;
    }

}
