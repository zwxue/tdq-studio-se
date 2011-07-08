// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.talend.commons.utils.SpecialValueDisplay;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.analysis.impl.AnalyzedDataSetImpl;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.MaxLengthIndicator;
import org.talend.dataquality.indicators.MinLengthIndicator;
import org.talend.dataquality.indicators.PatternFreqIndicator;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.utils.collections.MultiMapHelper;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author scorreia
 * 
 * Computes indicators on columns with java engine. It means that we call indicator.handle(object) method.
 */
public class IndicatorEvaluator extends Evaluator<String> {

    private static Logger log = Logger.getLogger(IndicatorEvaluator.class);

    protected Analysis analysis = null;

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
        // ADD xqliu 2010-07-27 bug 13826
        Map<String, String> columnlistMap = buildColumnListMap(columnlist);
        // ~ 13826
        // create query statement
        // feature 0010630 zshen: Tables are not found when using Excel with ODBC connection
        Statement statement = null;
        // MOD qiongli 2011-6-28 bug 22520,statement for sqlLite
        Connection dataManager = (Connection) analysis.getContext().getConnection();
        if (ConnectionUtils.isSqlite(dataManager)) {
            statement = connection.createStatement();
        } else {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        }
        // ~10630
        statement.setFetchSize(fetchSize);
        // MOD xqliu 2009-02-09 bug 6237
        if (continueRun()) {
            if (log.isInfoEnabled()) {
                log.info("Executing query: " + sqlStatement); //$NON-NLS-1$
            }
            statement.execute(sqlStatement);
        }
        // get the results
        ResultSet resultSet = statement.getResultSet();
        if (resultSet == null) {
            String mess = Messages.getString("Evaluator.NoResultSet", sqlStatement); //$NON-NLS-1$
            log.warn(mess);
            ok.setReturnCode(mess, false);
            return ok;
        }

        int columnCount = resultSet.getMetaData().getColumnCount();
        int maxNumberRows = analysis.getParameters().getMaxNumberRows();

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
        int columnListSize = columnlist.size();
        label: while (resultSet.next()) {
            // --- for each column
            // feature 0010630 zshen: dislodge the Qualifiers from name of the column
            for (int i = 0; i < columnListSize; i++) {
                // MOD xqliu 2010-07-27 bug 13826
                String col = columnlist.get(i);
                List<Indicator> indicators = getIndicators(col);
                col = columnlistMap.get(col);
                // FIXME scorreia this must not be done here!!! but outside the resultset loop
                // int offset = col.lastIndexOf('.') + 1;
                // col = col.substring(offset);
                // ~ 13826

                // --- get content of column
                Object object = null;
                try {
                    object = resultSet.getObject(col);
                } catch (SQLException e) {
                    if (resultSet.getString(col).equals("0000-00-00 00:00:00"))//$NON-NLS-1$  
                        object = null;

                }

                // MOD zshen, when the type of object is TIMESTAMP then need getTimestamp(col) to get correct value,or
                // the value only is the name of type and can't be match with TIMESTAMP.
                // FIXME this will slow down a lot the computation
                if (object != null && !(object instanceof String) && object.toString().indexOf("TIMESTAMP") > -1) { //$NON-NLS-1$
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

                    AnalyzedDataSet analyzedDataSet = indicToRowMap.get(indicator);
                    if (analyzedDataSet == null) {
                        analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
                        indicToRowMap.put(indicator, analyzedDataSet);
                        analyzedDataSet.setDataCount(maxNumberRows);
                        analyzedDataSet.setRecordSize(0);
                    }

                    if (analysis.getParameters().isStoreData() && indicator.mustStoreRow()) {
                        List<Object[]> valueObjectList = initDataSet(indicator, indicToRowMap, object);
                        // MOD zshen add another loop to insert all of columnValue on the row into indicator.
                        recordIncrement = valueObjectList.size();
                        // MOD klliu 2011-06-30 bug 22523 whichever is Table or View,that finds columns should ues
                        // columnset
                        ColumnSet doSwitch = SwitchHelpers.COLUMN_SET_SWITCH
                                .doSwitch(indicator.getAnalyzedElement().eContainer());
                        List<TdColumn> columnList = ColumnSetHelper.getColumns(doSwitch);
                        // List<TdColumn> columnList =
                        // TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(indicator
                        // .getAnalyzedElement().eContainer()));
                        for (int j = 0; j < columnCount; j++) {
                            String newcol = columnList.get(j).getName();
                            Object newobject = null;
                            try {
                                newobject = resultSet.getObject(newcol);
                            } catch (SQLException e) {
                                if (resultSet.getString(newcol).equals("0000-00-00 00:00:00"))//$NON-NLS-1$  
                                    newobject = null;

                            }
                            if (recordIncrement < maxNumberRows) {// decide whether current record is more than max
                                                                  // Number else don't need to record more than data.
                                if (recordIncrement < valueObjectList.size()) {// decide whether need to increase
                                                                               // current array.
                                    valueObjectList.get(recordIncrement)[j] = newobject;

                                } else {
                                    Object[] valueObject = new Object[columnCount];
                                    valueObject[j] = newobject;
                                    valueObjectList.add(valueObject);
                                }
                            } else {
                                break;
                            }
                        }
                        // ~
                    } else if (indicator instanceof UniqueCountIndicator
                            && analysis.getResults().getIndicToRowMap().get(indicator).getData() != null) {
                        List<Object[]> removeValueObjectList = analysis.getResults().getIndicToRowMap().get(indicator).getData();
                        // MOD klliu 2011-06-30 bug 22523 whichever is Table or View,that finds columns should ues
                        // columnset
                        ColumnSet doSwitch = SwitchHelpers.COLUMN_SET_SWITCH
                                .doSwitch(indicator.getAnalyzedElement().eContainer());
                        List<TdColumn> columnElementList = ColumnSetHelper.getColumns(doSwitch);
                        // List<TdColumn> columnElementList =
                        // TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(indicator
                        // .getAnalyzedElement().eContainer()));
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
        // --- release statement
        statement.close();
        // --- close connection
        connection.close();

        return ok;
    }

    /**
     * DOC xqliu Comment method "buildColumnListMap". bug 13826
     * 
     * @param columnlist
     * @return
     */
    private Map<String, String> buildColumnListMap(List<String> columnlist) {
        Map<String, String> result = new HashMap<String, String>();
        for (String col : columnlist) {
            result.put(col, col.substring(col.lastIndexOf('.') + 1));
        }
        return result;
    }

    protected List<Object[]> initDataSet(Indicator indicator, EMap<Indicator, AnalyzedDataSet> indicToRowMap, Object object) {
        AnalyzedDataSet analyzedDataSet = indicToRowMap.get(indicator);
        List<Object[]> valueObjectList = null;
        if (analyzedDataSet == null) {
            analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
            indicToRowMap.put(indicator, analyzedDataSet);
            analyzedDataSet.setDataCount(analysis.getParameters().getMaxNumberRows());
            analyzedDataSet.setRecordSize(0);
        }

        if (indicator instanceof FrequencyIndicator || indicator instanceof MinLengthIndicator
                || indicator instanceof MaxLengthIndicator) {
            Map<Object, List<Object[]>> valueObjectListMap = analyzedDataSet.getFrequencyData();
            if (valueObjectListMap == null) {
                valueObjectListMap = new HashMap<Object, List<Object[]>>();
                analyzedDataSet.setFrequencyData(valueObjectListMap);
            }
            String key = null;
            if (object == null) {
                key = SpecialValueDisplay.NULL_FIELD;
            } else if (indicator instanceof MinLengthIndicator || indicator instanceof MaxLengthIndicator) {
                key = String.valueOf(object.toString().length());
            } else if (object.equals(PluginConstant.EMPTY_STRING)) {
                key = SpecialValueDisplay.EMPTY_FIELD;
            } else if (indicator instanceof PatternLowFreqIndicator) {
                key = ((PatternLowFreqIndicator) indicator).convertCharacters(object.toString());
            } else if (indicator instanceof PatternFreqIndicator) {
                key = ((PatternFreqIndicator) indicator).convertCharacters(object.toString());
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
                patternData.add(new ArrayList<Object[]>());// mapping with AnalyzedDataSetImpl.VALID_VALUE
                patternData.add(new ArrayList<Object[]>());// mapping with AnalyzedDataSetImpl.INVALID_VALUE
                analyzedDataSet.setPatternData(patternData);
            }
            Object listObject = indicator.isInValidRow() ? patternData.get(AnalyzedDataSetImpl.INVALID_VALUE) : patternData
                    .get(AnalyzedDataSetImpl.VALID_VALUE);
            if (listObject instanceof ArrayList<?>) {
                valueObjectList = (ArrayList<Object[]>) listObject;
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
        Map<Integer, String> offset = new HashMap<Integer, String>();
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
