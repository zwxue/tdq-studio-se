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
package org.talend.dq.indicators;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.utils.StringUtils;
import org.talend.core.language.LanguageManager;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.Escape;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dq.helper.ParameterUtil;
import org.talend.fileprocess.FileInputDelimited;
import org.talend.utils.sql.TalendTypeConvert;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

import com.csvreader.CsvReader;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DelimitedFileIndicatorEvaluator extends IndicatorEvaluator {

    private Analysis analysis = null; // FIXME do we need to hide the same field from the parent class?

    private DelimitedFileConnection delimitedFileconnection = null;

    private Logger log = Logger.getLogger(DelimitedFileIndicatorEvaluator.class);

    private boolean isBablyForm = false;

    /**
     * DOC qiongli DelimitedFileIndicatorEvaluator constructor comment.
     * 
     * @param analysis
     */
    public DelimitedFileIndicatorEvaluator(Analysis analysis) {
        super(analysis);
        this.analysis = analysis;
    }

    @Override
    protected ReturnCode executeSqlQuery(String sqlStatement) {
        ReturnCode returnCode = new ReturnCode(true);
        if (delimitedFileconnection == null) {
            delimitedFileconnection = (DelimitedFileConnection) analysis.getContext().getConnection();
        }
        boolean isContextMode = delimitedFileconnection.isContextMode();
        String path = delimitedFileconnection.getFilePath();
        if (isContextMode) {
            path = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, path);
        }
        IPath iPath = new Path(path);

        CsvReader csvReader = null;
        try {
            // File csvFile = outPut.toFile();
            File file = iPath.toFile();
            String separator = delimitedFileconnection.getFieldSeparatorValue();
            String encoding = delimitedFileconnection.getEncoding();
            if (isContextMode) {
                separator = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, separator);
                encoding = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, encoding);
            }
            if (!file.exists()) {
                returnCode.setReturnCode(Messages.getString("System can not find the file specified"), false); //$NON-NLS-1$
                return returnCode;
            }

            List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
            EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
            // int recordIncrement = 0;
            indicToRowMap.clear();

            // MOD qiongli 2011-4-2,bug 20033

            List<MetadataColumn> columnElementList = new ArrayList<MetadataColumn>();
            for (int i = 0; i < analysisElementList.size(); i++) {
                MetadataColumn mColumn = (MetadataColumn) analysisElementList.get(i);
                MetadataTable mTable = ColumnHelper.getColumnOwnerAsMetadataTable(mColumn);
                columnElementList = mTable == null ? columnElementList : mTable.getColumns();
                if (!columnElementList.isEmpty()) {
                    break;
                }
            }
            // Added yyin 20120611 TDQ-5346
            String zero = "0"; //$NON-NLS-1$
            int headValue = 0;
            int footValue = 0;
            int limitValue = 0;
            String heading = delimitedFileconnection.getHeaderValue();
            String footing = delimitedFileconnection.getFooterValue();
            String limiting = delimitedFileconnection.getLimitValue();
            if (isContextMode) {
                heading = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, heading);
                footing = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, footing);
                limiting = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, limiting);
                headValue = Integer.parseInt(heading == PluginConstant.EMPTY_STRING ? zero : heading);
                footValue = Integer.parseInt(footing == PluginConstant.EMPTY_STRING ? zero : footing);
                limitValue = Integer
                        .parseInt(PluginConstant.EMPTY_STRING.equals(limiting) || zero.equals(limiting) ? "-1" : limiting); //$NON-NLS-1$
            } else {// ~ 5346
                // MOD qionlgi 2011-5-12,bug 21115.
                headValue = Integer.parseInt(heading == null || PluginConstant.EMPTY_STRING.equals(heading) ? zero : heading);
                footValue = Integer.parseInt(footing == null || PluginConstant.EMPTY_STRING.equals(footing) ? zero : footing);
                if (limiting == null || PluginConstant.EMPTY_STRING.equals(limiting) || zero.equals(limiting)) {
                    limiting = "-1"; //$NON-NLS-1$
                }
                limitValue = Integer.parseInt(limiting);
            }
            // use CsvReader to parse.
            if (Escape.CSV.equals(delimitedFileconnection.getEscapeType())) {
                csvReader = new CsvReader(new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file),
                        encoding == null ? "UTF-8" : encoding)), ParameterUtil //$NON-NLS-1$
                        .trimParameter(separator).charAt(0));

                initializeCsvReader(csvReader);
                for (int i = 0; i < headValue && csvReader.readRecord(); i++) {
                    // do nothing, just ignore the header part
                }
                String[] rowValues = null;
                long currentRecord = 0;
                while (csvReader.readRecord()) {
                    currentRecord = csvReader.getCurrentRecord();
                    if (!continueRun() || limitValue != -1 && currentRecord > limitValue - 1) {
                        break;
                    }

                    if (delimitedFileconnection.isFirstLineCaption() && currentRecord == 0) {
                        continue;
                    }
                    rowValues = csvReader.getValues();
                    handleByARow(rowValues, currentRecord + 1, analysisElementList, columnElementList, indicToRowMap);
                }
            } else {
                // use TOSDelimitedReader in FileInputDelimited to parse.
                String rowSeparator = delimitedFileconnection.getRowSeparatorValue();
                if (isContextMode) {
                    rowSeparator = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, rowSeparator);
                }
                boolean isSpliteRecord = delimitedFileconnection.isSplitRecord();
                boolean isSkipeEmptyRow = delimitedFileconnection.isRemoveEmptyRow();
                String languageName = LanguageManager.getCurrentLanguage().getName();

                FileInputDelimited fileInputDelimited = new FileInputDelimited(ParameterUtil.trimParameter(path),
                        ParameterUtil.trimParameter(encoding), ParameterUtil.trimParameter(StringUtils.loadConvert(separator,
                                languageName)), ParameterUtil.trimParameter(StringUtils.loadConvert(rowSeparator, languageName)),
                        isSkipeEmptyRow, headValue, footValue, limitValue, -1, isSpliteRecord);
                long currentRow = headValue;
                while (fileInputDelimited.nextRecord()) {
                    if (!continueRun()) {
                        break;
                    }
                    currentRow++;
                    int columsCount = fileInputDelimited.getColumnsCountOfCurrentRow();
                    String[] rowValues = new String[columsCount];
                    for (int i = 0; i < columsCount; i++) {
                        rowValues[i] = fileInputDelimited.get(i);
                    }
                    handleByARow(rowValues, currentRow, analysisElementList, columnElementList, indicToRowMap);
                }
                fileInputDelimited.close();
            }
            // Added yyin 20120608 TDQ-3589
            for (MetadataColumn col : columnElementList) {
                List<Indicator> indicators = getIndicators(col.getLabel());
                for (Indicator indicator : indicators) {
                    if (indicator instanceof DuplicateCountIndicator) {
                        AnalyzedDataSet analyzedDataSet = indicToRowMap.get(indicator);
                        if (analyzedDataSet == null) {
                            analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
                            indicToRowMap.put(indicator, analyzedDataSet);
                            analyzedDataSet.setDataCount(analysis.getParameters().getMaxNumberRows());
                            analyzedDataSet.setRecordSize(0);
                        }
                        // indicator.finalizeComputation();
                        addResultToIndicatorToRowMap(indicator, indicToRowMap);
                    }
                }
            }// ~

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (csvReader != null) {
                csvReader.close();
            }
        }
        return returnCode;
    }

    /**
     * DOC yyin Comment method "addResultToIndicatorToRowMap".
     * 
     * @param indicator
     * @param indicToRowMap
     */
    private void addResultToIndicatorToRowMap(Indicator indicator, EMap<Indicator, AnalyzedDataSet> indicToRowMap) {
        Map<Object, List<Object[]>> dupMap = ((DuplicateCountIndicator) indicator).getDuplicateMap();

        Iterator<Object> iterator = dupMap.keySet().iterator();
        int maxNumberRows = analysis.getParameters().getMaxNumberRows();

        while (iterator.hasNext()) {
            Object key = iterator.next();

            List<Object[]> valuelist = dupMap.get(key);
            if (valuelist.size() > 1) {
                List<Object[]> valueObjectList = initDataSet(indicator, indicToRowMap, key);
                // MOD zshen add another loop to insert all of columnValue on the row into indicator.
                int recordIncrement = valueObjectList.size();

                for (Object[] row : valuelist) {
                    if (recordIncrement < maxNumberRows) {
                        valueObjectList.add(row);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected ReturnCode closeConnection() {
        return new ReturnCode(true);

    }

    public DelimitedFileConnection getDelimitedFileconnection() {
        return this.delimitedFileconnection;
    }

    public void setDelimitedFileconnection(DelimitedFileConnection delimitedFileconnection) {
        this.delimitedFileconnection = delimitedFileconnection;
    }

    @Override
    protected ReturnCode checkConnection() {
        if (delimitedFileconnection == null) {
            return new ReturnCode(Messages.getString("Evaluator.openNullConnection"), false); //$NON-NLS-1$
        }
        return new ReturnCode(true);
    }

    /**
     * 
     * DOC qiongli Comment method "initializeCsvReader".
     * 
     * @param csvReader
     * @param connection
     */
    private void initializeCsvReader(CsvReader csvReader) {
        String rowSep = delimitedFileconnection.getRowSeparatorValue();
        if (delimitedFileconnection.isContextMode()) {
            rowSep = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, rowSep);
        }
        if (!rowSep.equals("\"\\n\"") && !rowSep.equals("\"\\r\"")) { //$NON-NLS-1$ //$NON-NLS-2$
            csvReader.setRecordDelimiter(ParameterUtil.trimParameter(rowSep).charAt(0));
        }

        csvReader.setSkipEmptyRecords(true);
        String textEnclosure = delimitedFileconnection.getTextEnclosure();
        if (textEnclosure != null && textEnclosure.length() > 0) {
            csvReader.setTextQualifier(ParameterUtil.trimParameter(textEnclosure).charAt(0));
        } else {
            csvReader.setUseTextQualifier(false);
        }
        String escapeChar = delimitedFileconnection.getEscapeChar();
        if (escapeChar == null || escapeChar.equals("\"\\\\\"") || escapeChar.equals("\"\"")) { //$NON-NLS-1$ //$NON-NLS-2$
            csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
        } else {
            csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);
        }

    }

    private void handleByARow(String[] rowValues, long currentRow, List<ModelElement> analysisElementList,
            List<MetadataColumn> columnElementList, EMap<Indicator, AnalyzedDataSet> indicToRowMap) {
        Object object = null;
        int recordIncrement = 0;
        element: for (int i = 0; i < analysisElementList.size(); i++) {
            MetadataColumn mColumn = (MetadataColumn) analysisElementList.get(i);
            Integer position = ColumnHelper.getColumnIndex(mColumn);
            // warning with a file of badly form
            if (position == null || position >= rowValues.length) {
                log.warn(Messages.getString("DelimitedFileIndicatorEvaluator.incorrectData", //$NON-NLS-1$
                        mColumn.getLabel(), currentRow, delimitedFileconnection.getFilePath()));
                if (!isBablyForm) {
                    isBablyForm = true;
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            MessageDialog.openWarning(null,
                                    Messages.getString("DelimitedFileIndicatorEvaluator.badlyForm.Title"), //$NON-NLS-1$
                                    Messages.getString("DelimitedFileIndicatorEvaluator.badlyForm.Message")); //$NON-NLS-1$
                        }
                    });
                }
                continue;
            }

            object = TalendTypeConvert.convertToObject(mColumn.getTalendType(), rowValues[position], mColumn.getPattern());
            List<Indicator> indicators = getIndicators(mColumn.getLabel());
            for (Indicator indicator : indicators) {
                if (!continueRun()) {
                    break element;
                }
                // bug 19036,to irregularly data,still compute for RowCountIndicator
                if (object == null && !(indicator instanceof RowCountIndicator)) {
                    continue element;
                }
                // Added yyin 20120608 TDQ-3589
                if (indicator instanceof DuplicateCountIndicator) {
                    ((DuplicateCountIndicator) indicator).handle(object, rowValues);
                } else { // ~
                    indicator.handle(object);
                }
                AnalyzedDataSet analyzedDataSet = indicToRowMap.get(indicator);
                if (analyzedDataSet == null) {
                    analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
                    indicToRowMap.put(indicator, analyzedDataSet);
                    analyzedDataSet.setDataCount(analysis.getParameters().getMaxNumberRows());
                    analyzedDataSet.setRecordSize(0);
                }
                if (analysis.getParameters().isStoreData() && indicator.mustStoreRow()) {
                    List<Object[]> valueObjectList = initDataSet(indicator, indicToRowMap, object);
                    recordIncrement = valueObjectList.size();
                    Object[] valueObject = new Object[rowValues.length];
                    for (int j = 0; j < rowValues.length; j++) {
                        Object newobject = rowValues[j];
                        if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                            if (recordIncrement < valueObjectList.size()) {
                                valueObjectList.get(recordIncrement)[j] = newobject;
                            } else {
                                valueObject[j] = newobject;
                                valueObjectList.add(valueObject);
                            }
                        }
                    }
                } else if (indicator instanceof UniqueCountIndicator
                        && analysis.getResults().getIndicToRowMap().get(indicator).getData() != null) {
                    List<Object[]> removeValueObjectList = analysis.getResults().getIndicToRowMap().get(indicator).getData();
                    if (columnElementList.size() == 0) {
                        continue;
                    }
                    int offsetting = columnElementList.indexOf(indicator.getAnalyzedElement());
                    for (Object[] dataObject : removeValueObjectList) {
                        // Added yyin 20120611 TDQ5279
                        if (object instanceof Integer) {
                            if (object.equals(Integer.parseInt((String) dataObject[offsetting]))) {
                                removeValueObjectList.remove(dataObject);
                                break;
                            }
                        }// ~
                        if (dataObject[offsetting].equals(object)) {
                            removeValueObjectList.remove(dataObject);
                            break;
                        }
                    }

                }
            }

        }
    }

}
