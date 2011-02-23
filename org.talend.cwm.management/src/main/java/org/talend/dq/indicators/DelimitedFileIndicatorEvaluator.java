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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.utils.sql.TalendTypeConvert;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

import com.csvreader.CsvReader;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DelimitedFileIndicatorEvaluator extends IndicatorEvaluator {

    private Analysis analysis = null;

    private DelimitedFileConnection delimitedFileconnection = null;

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
        DelimitedFileConnection con = (DelimitedFileConnection) analysis.getContext().getConnection();
        String path = con.getFilePath();
        IPath iPath = new Path(path);
        // IPath outPut = buildTempCSVFilename(iPath);
        // CsvArray csvArray = new CsvArray();
        // List<String[]> rows = null;

        CsvReader csvReader = null;
        try {
            // File csvFile = outPut.toFile();
            File file = iPath.toFile();
            String separator = con.getFieldSeparatorValue();
            String encoding = con.getEncoding();
            if (!file.exists()) {
                returnCode.setReturnCode(Messages.getString("System can not find the file specified"), false);
                return returnCode;
            }
            csvReader = new CsvReader(new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file),
                    encoding == null ? encoding : encoding)), separator.charAt(1));
            csvReader.setRecordDelimiter('\n');
            csvReader.setSkipEmptyRecords(true);
            csvReader.setTextQualifier('"');
            csvReader.setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_DOUBLED);

            // csvArray.setEncoding(con.getEncoding());
            // csvArray = csvArray.createFrom(file, con.getEncoding(), separator.charAt(1));
            //
            // rows = csvArray.getRows();
            // } catch (IOException e) {
            // e.printStackTrace();
            // }

            List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
            EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
            int recordIncrement = 0;
            indicToRowMap.clear();
            // MetadataTable mTable = null;
            // label: for (String rowValues[] : rows) {
            label: while (csvReader.readRecord()) {
                if (con.isFirstLineCaption() && csvReader.getCurrentRecord() == Long.valueOf("0")) {
                    continue;
                }
                String[] rowValues = csvReader.getValues();
                Object object = null;
                element: for (int i = 0; i < analysisElementList.size(); i++) {
                    MetadataColumn mColumn = (MetadataColumn) analysisElementList.get(i);
                    List<Indicator> indicators = getIndicators(mColumn.getLabel());
                    MetadataTable mTable = ColumnHelper.getColumnOwnerAsMetadataTable(mColumn);
                    Integer position = ColumnHelper.getColumnIndex(mColumn);
                    if (position == null || position >= rowValues.length)
                        continue;
                    if (position >= rowValues.length) {
                        object = PluginConstant.EMPTY_STRING;
                    } else {
                        object = TalendTypeConvert.convertToObject(mColumn.getTalendType(), rowValues[position]);
                    }

                    for (Indicator indicator : indicators) {
                        if (!continueRun()) {
                            break label;
                        }
                        // bug 19036,to irregularly data,still compute for RowCountIndicator
                        if (object == null && !(indicator instanceof RowCountIndicator)) {
                            continue element;
                        }
                        indicator.handle(object);
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
                            List<Object[]> removeValueObjectList = analysis.getResults().getIndicToRowMap().get(indicator)
                                    .getData();
                            if (mTable == null) {
                                continue;
                            }
                            List<MetadataColumn> columnElementList = mTable.getColumns();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnCode;
    }

    @Override
    protected ReturnCode closeConnection() {
        return new ReturnCode(true); //$NON-NLS-1$

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

}
