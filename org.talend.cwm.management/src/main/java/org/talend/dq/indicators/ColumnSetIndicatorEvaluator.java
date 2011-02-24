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
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.utils.sql.TalendTypeConvert;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

import com.csvreader.CsvReader;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class ColumnSetIndicatorEvaluator extends Evaluator<String> {

    private static Logger log = Logger.getLogger(ColumnSetIndicatorEvaluator.class);

    protected Analysis analysis = null;

    // MOD yyi 2011-02-22 17871:delimitefile
    protected boolean isDelimitedFile = false;

    public ColumnSetIndicatorEvaluator(Analysis analysis) {
        this.analysis = analysis;
        this.isDelimitedFile = analysis.getContext().getConnection() instanceof DelimitedFileConnection;
    }

    @Override
    protected ReturnCode executeSqlQuery(String sqlStatement) throws SQLException {
        ReturnCode ok = new ReturnCode(true);
        AnalysisResult anaResult = analysis.getResults();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = anaResult.getIndicToRowMap();
        indicToRowMap.clear();
        if (isDelimitedFile) {
            ok = evaluateByDelimitedFile(sqlStatement, ok);
        } else {
            ok = evaluateBySql(sqlStatement, ok);
            Statement statement = null;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setFetchSize(fetchSize);
            if (continueRun()) {
                if (log.isInfoEnabled()) {
                    log.info("Executing query: " + sqlStatement);
                }
                statement.execute(sqlStatement);
            }
        }

        return ok;
    }

    /**
     * 
     * DOC qiongli Comment method "getAnalyzedElementsName".
     * 
     * @return
     */
    private Set<String> getAnalyzedElementsName() {
        Set<String> columnsName = new HashSet<String>();
        List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
        for (ModelElement me : analysisElementList) {
            String name = ModelElementHelper.getName(me);
            if (name != null) {
                columnsName.add(name);
            }

        }
        return columnsName;
    }

    /**
     * 
     * orgnize EList<Object> 'objectLs' by SQL.
     * 
     * @param sqlStatement
     * @param ok
     * @return
     * @throws SQLException
     */
    private ReturnCode evaluateBySql(String sqlStatement, ReturnCode ok) throws SQLException {
        Statement statement = null;
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        statement.setFetchSize(fetchSize);
        if (continueRun()) {
            if (log.isInfoEnabled()) {
                log.info("Executing query: " + sqlStatement);
            }
            statement.execute(sqlStatement);
        }
        // get the results
        ResultSet resultSet = statement.getResultSet();
        Set<String> columnNames = getAnalyzedElementsName();

        if (resultSet == null) {
            String mess = "No result set for this statement: " + sqlStatement;
            log.warn(mess);
            ok.setReturnCode(mess, false);
            return ok;
        }

        while (resultSet.next()) {
            EList<Object> objectLs = new BasicEList<Object>();
            Iterator<String> it = columnNames.iterator();
            while (it.hasNext()) {
                objectLs.add(resultSet.getObject(it.next()));
            }
            if (objectLs.size() == 0) {
                continue;
            }
            handleObjects(objectLs);
        }
        return ok;
    }

    /**
     * 
     * orgnize EList<Object> 'objectLs' for DelimitedFile connection.
     * 
     * @param sqlStatement
     * @param returnCode
     * @return
     */
    private ReturnCode evaluateByDelimitedFile(String sqlStatement, ReturnCode returnCode) {
        DelimitedFileConnection con = (DelimitedFileConnection) analysis.getContext().getConnection();
        String path = con.getFilePath();
        IPath iPath = new Path(path);
        File file = iPath.toFile();
        String separator = con.getFieldSeparatorValue();
        String encoding = con.getEncoding();
        if (!file.exists()) {
            returnCode.setReturnCode(Messages.getString("System can not find the file specified"), false);
            return returnCode;
        }
        CsvReader csvReader = null;
        try {
            csvReader = new CsvReader(new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file),
                    encoding == null ? encoding : encoding)), separator.charAt(1));

            csvReader.setRecordDelimiter('\n');
            csvReader.setSkipEmptyRecords(true);
            csvReader.setTextQualifier('"');
            csvReader.setEscapeMode(com.csvreader.CsvReader.ESCAPE_MODE_DOUBLED);

            List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
            EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
            indicToRowMap.clear();
            while (csvReader.readRecord()) {
                String[] rowValues = csvReader.getValues();
                Object object = null;
                EList<Object> objectLs = new BasicEList<Object>();
                for (int i = 0; i < analysisElementList.size(); i++) {
                    MetadataColumn mColumn = (MetadataColumn) analysisElementList.get(i);
                    // List<Indicator> indicators = getIndicators(mColumn.getLabel());
                    Integer position = ColumnHelper.getColumnIndex(mColumn);
                    if (position == null || position >= rowValues.length)
                        continue;
                    if (position >= rowValues.length) {
                        object = PluginConstant.EMPTY_STRING;
                    } else {
                        object = TalendTypeConvert.convertToObject(mColumn.getTalendType(), rowValues[position]);
                    }
                    // if (object == null) {
                    // continue;
                    // }
                    objectLs.add(object);

                }
                handleObjects(objectLs);
            }
        } catch (Exception e) {
            log.error(e, e);
            returnCode.setReturnCode(e.getMessage(), false);
        }

        return returnCode;
    }

    /**
     * 
     * DOC qiongli Comment method "handleObjects".
     * 
     * @param objectLs
     */
    private void handleObjects(EList<Object> objectLs) {
        if (objectLs.size() == 0)
            return;
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
                indicator.handle(objectLs);
            }
        }
    }

    /*
     * ADD yyi 2011-02-22 17871:delimitefile
     * 
     * @see org.talend.dq.indicators.Evaluator#checkConnection()
     */
    @Override
    protected ReturnCode checkConnection() {
        if (isDelimitedFile) {
            return new ReturnCode();
        }
        return super.checkConnection();
    }

    /*
     * ADD yyi 2011-02-24 17871:delimitefile
     * 
     * @see org.talend.dq.indicators.Evaluator#closeConnection()
     */
    @Override
    protected ReturnCode closeConnection() {
        if (isDelimitedFile) {
            return new ReturnCode();
        }
        return super.closeConnection();
    }
}
