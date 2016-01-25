// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.utils.StringUtils;
import org.talend.core.language.LanguageManager;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.Escape;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.db.connection.MdmStatement;
import org.talend.cwm.db.connection.MdmWebserviceConnection;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.columnset.AllMatchIndicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.dbms.HiveDbmsLanguage;
import org.talend.dq.dbms.MSSqlDbmsLanguage;
import org.talend.dq.dbms.SQLiteDbmsLanguage;
import org.talend.dq.helper.ParameterUtil;
import org.talend.fileprocess.FileInputDelimited;
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

    protected boolean isMdm = false;

    protected TdXmlSchema tdXmlDocument;

    protected MdmWebserviceConnection mdmWebserviceConn;

    private boolean isBadlyFormFlatFile = false;

    public ColumnSetIndicatorEvaluator(Analysis analysis) {
        this.analysis = analysis;
        this.isDelimitedFile = analysis.getContext().getConnection() instanceof DelimitedFileConnection;
        this.isMdm = analysis.getContext().getConnection() instanceof MDMConnection;
    }

    @Override
    protected ReturnCode executeSqlQuery(String sqlStatement) throws SQLException {
        ReturnCode ok = new ReturnCode(true);
        AnalysisResult anaResult = analysis.getResults();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = anaResult.getIndicToRowMap();
        indicToRowMap.clear();
        if (isDelimitedFile) {
            ok = evaluateByDelimitedFile(sqlStatement, ok);
        } else if (isMdm) {
            ok = evaluateByMDM(sqlStatement, ok);
        } else {
            ok = evaluateBySql(sqlStatement, ok);
        }

        return ok;
    }

    /**
     * 
     * DOC qiongli Comment method "getAnalyzedElementsName".
     * 
     * @return
     */
    private List<String> getAnalyzedElementsName() {
        List<String> columnsName = new ArrayList<String>();
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
     * orgnize EList 'objectLs' by SQL.
     * 
     * @param sqlStatement
     * @param ok
     * @return
     * @throws SQLException
     */
    private ReturnCode evaluateBySql(String sqlStatement, ReturnCode ok) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // MOD qiongli 2011-7-8 bug 22520,statement for sqlLite
            Connection dataManager = (Connection) analysis.getContext().getConnection();
            // MOD msjian TDQ-5503 2012-6-12: fixed SQLServerException: make the same with IndicatorEvaluator
            DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dataManager);
            boolean isHiveDbms = dbmsLanguage instanceof HiveDbmsLanguage;
            if (dbmsLanguage instanceof MSSqlDbmsLanguage || dbmsLanguage instanceof SQLiteDbmsLanguage || isHiveDbms) {
                statement = getConnection().createStatement();
            } else {
                statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }
            // TDQ-5503~
            if (!isHiveDbms) {
                statement.setFetchSize(fetchSize);
            }
            if (continueRun()) {
                if (log.isInfoEnabled()) {
                    log.info("Executing query: " + sqlStatement); //$NON-NLS-1$
                }
                statement.execute(sqlStatement);
            }
            // get the results
            resultSet = statement.getResultSet();
            List<String> columnNames = getAnalyzedElementsName();

            if (resultSet == null) {
                String mess = Messages.getString("Evaluator.NoResultSet", sqlStatement); //$NON-NLS-1$
                log.warn(mess);
                ok.setReturnCode(mess, false);
                return ok;
            }
            EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
            indicToRowMap.clear();
            while (resultSet.next()) {
                // MOD yyi 2012-04-11 TDQ-4916:Add memory control for java analysis.
                if (!continueRun()) {
                    break;
                }
                EList<Object> objectLs = new BasicEList<Object>();
                Iterator<String> it = columnNames.iterator();
                while (it.hasNext()) {
                    Object obj = resultSet.getObject(it.next());
                    objectLs.add(obj);
                }
                if (objectLs.size() == 0) {
                    continue;
                }
                handleObjects(objectLs, resultSet);
            }
        } catch (Exception exc) {
            log.error(exc, exc);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            getConnection().close();
        }

        return ok;
    }

    /**
     * 
     * orgnize EList 'objectLs' for DelimitedFile connection.
     * 
     * @param sqlStatement
     * @param returnCode
     * @return
     */
    private ReturnCode evaluateByDelimitedFile(String sqlStatement, ReturnCode returnCode) {
        DelimitedFileConnection con = (DelimitedFileConnection) analysis.getContext().getConnection();
        String path = con.getFilePath();
        // MOD msjian TDQ-5851: we should consider the context mode
        String separator = con.getFieldSeparatorValue();
        String encoding = con.getEncoding();
        boolean isContextMode = con.isContextMode();
        if (isContextMode) {
            path = ConnectionUtils.getOriginalConntextValue(con, path);
            separator = ConnectionUtils.getOriginalConntextValue(con, separator);
            encoding = ConnectionUtils.getOriginalConntextValue(con, encoding);
        }
        IPath iPath = new Path(path);
        File file = iPath.toFile();
        if (!file.exists()) {
            returnCode.setReturnCode(Messages.getString("ColumnSetIndicatorEvaluator.FileNotFound", file.getName()), false); //$NON-NLS-1$ 
            return returnCode;
        }

        try {
            List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
            EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
            indicToRowMap.clear();
            String zero = "0"; //$NON-NLS-1$
            int headValue = 0;
            int footValue = 0;
            int limitValue = 0;
            String heading = con.getHeaderValue();
            String footing = con.getFooterValue();
            String limiting = con.getLimitValue();
            if (isContextMode) {
                heading = ConnectionUtils.getOriginalConntextValue(con, heading);
                footing = ConnectionUtils.getOriginalConntextValue(con, footing);
                limiting = ConnectionUtils.getOriginalConntextValue(con, limiting);
                headValue = Integer.parseInt(heading == PluginConstant.EMPTY_STRING ? zero : heading);
                footValue = Integer.parseInt(footing == PluginConstant.EMPTY_STRING ? zero : footing);
                limitValue = Integer
                        .parseInt(PluginConstant.EMPTY_STRING.equals(limiting) || zero.equals(limiting) ? "-1" : limiting); //$NON-NLS-1$
            } else {
                headValue = Integer.parseInt(heading == null || PluginConstant.EMPTY_STRING.equals(heading) ? zero : heading);
                footValue = Integer.parseInt(footing == null || PluginConstant.EMPTY_STRING.equals(footing) ? zero : footing);
                if (limiting == null || PluginConstant.EMPTY_STRING.equals(limiting) || zero.equals(limiting)) {
                    limiting = "-1"; //$NON-NLS-1$
                }
                limitValue = Integer.parseInt(limiting);
            }
            if (Escape.CSV.equals(con.getEscapeType())) {
                // use CsvReader to parse.
                this.useCsvReader(file, con, analysisElementList);
            } else {
                // use TOSDelimitedReader in FileInputDelimited to parse.
                String rowSeparator = con.getRowSeparatorValue();
                if (isContextMode) {
                    rowSeparator = ConnectionUtils.getOriginalConntextValue(con, rowSeparator);
                }
                boolean isSpliteRecord = con.isSplitRecord();
                boolean isSkipeEmptyRow = con.isRemoveEmptyRow();
                String languageName = LanguageManager.getCurrentLanguage().getName();
                FileInputDelimited fileInputDelimited = new FileInputDelimited(ParameterUtil.trimParameter(path),
                        ParameterUtil.trimParameter(encoding), ParameterUtil.trimParameter(StringUtils.loadConvert(separator,
                                languageName)), ParameterUtil.trimParameter(StringUtils.loadConvert(rowSeparator, languageName)),
                        isSkipeEmptyRow, headValue, footValue, limitValue, -1, isSpliteRecord);
                long currentRow = headValue;
                int columsCount = 0;
                while (fileInputDelimited.nextRecord()) {
                    if (!continueRun()) {
                        break;
                    }
                    currentRow++;
                    if (columsCount == 0) {
                        columsCount = fileInputDelimited.getColumnsCountOfCurrentRow();
                    }
                    String[] rowValues = new String[columsCount];
                    for (int i = 0; i < columsCount; i++) {
                        rowValues[i] = fileInputDelimited.get(i);
                    }
                    orgnizeObjectsToHandel(path, rowValues, currentRow, analysisElementList, rowSeparator);
                }
                // TDQ-5851~
                fileInputDelimited.close();
            }
        } catch (Exception e) {
            log.error(e, e);
            returnCode.setReturnCode(e.getMessage(), false);
        }

        return returnCode;
    }

    private void useCsvReader(File file, DelimitedFileConnection dfCon, List<ModelElement> analysisElementList) throws Exception {
        String encoding = dfCon.getEncoding();
        String fieldSeparator = dfCon.getFieldSeparatorValue();
        CsvReader csvReader = new CsvReader(new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file),
                encoding == null ? "UTF-8" : encoding)), ParameterUtil.trimParameter(fieldSeparator).charAt(0));

        String rowSep = dfCon.getRowSeparatorValue();
        if (!rowSep.equals("\"\\n\"") && !rowSep.equals("\"\\r\"")) { //$NON-NLS-1$ //$NON-NLS-2$
            csvReader.setRecordDelimiter(ParameterUtil.trimParameter(rowSep).charAt(0));
        }
        csvReader.setSkipEmptyRecords(true);
        String textEnclosure = dfCon.getTextEnclosure();
        if (textEnclosure != null && textEnclosure.length() > 0) {
            csvReader.setTextQualifier(ParameterUtil.trimParameter(textEnclosure).charAt(0));
        } else {
            csvReader.setUseTextQualifier(false);
        }
        String escapeChar = dfCon.getEscapeChar();
        if (escapeChar == null || escapeChar.equals("\"\\\\\"") || escapeChar.equals("\"\"")) { //$NON-NLS-1$ //$NON-NLS-2$
            csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
        } else {
            csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);
        }

        long currentRecord = 0;
        String limitStr = dfCon.getLimitValue();
        if (limitStr == null) {
            limitStr = "0"; //$NON-NLS-1$
        }
        int limitValue = Integer.parseInt(limitStr);
        while (csvReader.readRecord()) {
            currentRecord = csvReader.getCurrentRecord();
            if (!continueRun() || limitValue != 0 && currentRecord > limitValue - 1) {
                break;
            }
            if (dfCon.isFirstLineCaption() && currentRecord == 0) {
                continue;
            }
            String[] rowValues = csvReader.getValues();
            this.orgnizeObjectsToHandel(dfCon.getFilePath(), rowValues, currentRecord + 1, analysisElementList, fieldSeparator);

        }
    }

    /**
     * 
     * orgnize a List by a row,then call 'handleObjects(...)'.
     * 
     * @param rowValues
     * @param currentRow
     * @param analysisElementList
     * @param separator
     */
    private void orgnizeObjectsToHandel(String fileName, String[] rowValues, long currentRow,
            List<ModelElement> analysisElementList, String separator) {
        EList<Object> objectLs = new BasicEList<Object>();
        MetadataColumn mColumn = null;
        Object object = null;
        for (int i = 0; i < analysisElementList.size(); i++) {
            mColumn = (MetadataColumn) analysisElementList.get(i);
            Integer position = ColumnHelper.getColumnIndex(mColumn);
            // MOD qiongli 2011-4-2,bug 20033,warning with a badly form file
            if (position == null || position >= rowValues.length) {
                log.warn(Messages.getString("DelimitedFileIndicatorEvaluator.incorrectData", //$NON-NLS-1$
                        mColumn.getLabel(), currentRow, fileName));
                if (!isBadlyFormFlatFile) {
                    isBadlyFormFlatFile = true;
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
            objectLs.add(object);

        }
        if (mColumn != null) {
            List<MetadataColumn> columnList = ColumnHelper.getColumnOwnerAsMetadataTable(mColumn).getColumns();
            handleObjects(objectLs, rowValues, columnList);
        }
    }

    /**
     * 
     * DOC qiongli Comment method "evaluateByMDM".
     * 
     * @param sqlStatement
     * @param returnCode
     * @return
     */
    private ReturnCode evaluateByMDM(String sqlStatement, ReturnCode returnCode) {
        if (mdmWebserviceConn == null || tdXmlDocument == null) {
            returnCode.setOk(false);
            return returnCode;
        }
        MdmStatement statement = mdmWebserviceConn.createStatement();
        String[] resultSet = null;
        // sqlStatement = "//" + tdXmlDocument.getName();
        this.getAnalyzedElements();
        if (continueRun()) {
            try {
                returnCode.setOk(true);
                returnCode.setOk(returnCode.isOk() && statement.execute(tdXmlDocument, sqlStatement));
                // resultSet = statement.getResultSet();
                List<String> strResultList = Arrays.asList(statement.getResultSet());
                resultSet = strResultList.toArray(new String[strResultList.size()]);
            } catch (RemoteException e) {
                returnCode.setMessage(e.getMessage());
            } catch (ServiceException e) {
                returnCode.setMessage(e.getMessage());
            }
        }
        if (resultSet == null) {
            String mess = Messages.getString("Evaluator.NoResultSet", sqlStatement); //$NON-NLS-1$ 
            log.warn(mess);
            returnCode.setReturnCode(mess, false);
            return returnCode;
        }
        List<Map<String, String>> resultSetList = new ArrayList<Map<String, String>>();
        List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
        TdXmlElementType parentElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(XmlElementHelper
                .getParentElement(SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(analysisElementList.get(0))));
        List<TdXmlElementType> columnList = org.talend.cwm.db.connection.ConnectionUtils.getXMLElements(parentElement);
        if (analysis.getParameters().isStoreData()) {
            resultSetList = statement.tidyResultSet(columnList.toArray(new ModelElement[columnList.size()]), resultSet);
        } else {
            resultSetList = statement.tidyResultSet(analysisElementList.toArray(new ModelElement[analysisElementList.size()]),
                    resultSet);
        }
        List<String> columnNames = getAnalyzedElementsName();
        for (int i = 0; i < resultSetList.size(); i++) {
            if (!this.continueRun()) {
                break;
            }
            Map<String, String> rowMap = resultSetList.get(i);
            EList<Object> objectLs = new BasicEList<Object>();
            Iterator<String> it = columnNames.iterator();
            while (it.hasNext()) {
                if (!this.continueRun()) {
                    break;
                }
                Object obj = rowMap.get(it.next());
                if (obj != null && (PluginConstant.EMPTY_STRING.equals(obj.toString().trim()))) {
                    obj = obj.toString().trim();
                }
                objectLs.add(obj);
            }
            if (objectLs.size() == 0) {
                continue;
            }
            handleObjects(rowMap, objectLs, columnList);

        }
        return returnCode;
    }

    /**
     * DOC qiongli Comment method "handleObjects".
     * 
     * @param objectLs
     * @throws SQLException
     */
    private void handleObjects(EList<Object> objectLs, ResultSet resultSet) throws SQLException {
        if (objectLs.size() == 0) {
            return;
        }
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
        int recordIncrement = 0;
        for (Indicator indicator : indicators) {
            if (!this.continueRun()) {
                break;
            }
            if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
                indicator.handle(objectLs);
                // feature 19192 ,save data for drill down RowCountIndicator.
                if (indicator instanceof SimpleStatIndicator) {
                    for (Indicator leafIndicator : ((SimpleStatIndicator) indicator).getLeafIndicators()) {
                        if (!(leafIndicator instanceof RowCountIndicator) || !indicator.isStoreData()) {
                            continue;
                        }

                        List<Object[]> valueObjectList = initDataSet(leafIndicator, indicToRowMap);
                        recordIncrement = valueObjectList.size();
                        if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                            // MOD gdbu 2011-5-6 bug : 20927
                            int columnCount = resultSet.getMetaData().getColumnCount();
                            if (0 >= columnCount) {
                                continue;
                            }
                            // MOD klliu 2011-06-30 bug 22523 whichever is Table or View,that finds columns should ues
                            // columnset
                            List<TdColumn> columnList = ColumnSetHelper
                                    .getColumns(SwitchHelpers.COLUMN_SET_SWITCH
                                            .doSwitch(((ColumnSetMultiValueIndicator) indicator).getAnalyzedColumns().get(0)
                                                    .eContainer()));
                            // List<TdColumn> columnList = TableHelper
                            // .getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(((ColumnSetMultiValueIndicator)
                            // indicator)
                            // .getAnalyzedColumns().get(0).eContainer()));
                            for (int j = 0; j < columnCount; j++) {
                                String newcol = null;
                                Object newobject = null;

                                for (TdColumn tdColumn : columnList) {
                                    try {
                                        newcol = tdColumn.getName();
                                        newobject = resultSet.getObject(newcol);
                                    } catch (Exception e) {
                                        continue;
                                    }
                                    columnList.remove(tdColumn);
                                    break;
                                }

                                if (null == newcol || null == newobject) {
                                    continue;
                                }
                                // ~20927
                                if (newobject != null && !(newobject instanceof String)
                                        && newobject.toString().indexOf("TIMESTAMP") > -1) { //$NON-NLS-1$ 
                                    newobject = resultSet.getTimestamp(newcol);
                                }
                                // if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                                if (recordIncrement < valueObjectList.size()) {
                                    valueObjectList.get(recordIncrement)[j] = newobject;
                                } else {
                                    Object[] valueObject = new Object[resultSet.getMetaData().getColumnCount()];
                                    valueObject[j] = newobject;
                                    valueObjectList.add(valueObject);
                                }
                                // }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * handle Objects and store data for delimited file .
     * 
     * @param objectLs
     * @param rowValues
     * @param metadataColumn is one of analysedElements.it is used to get its Table then get the table's columns.
     */
    private void handleObjects(EList<Object> objectLs, String[] rowValues, List<MetadataColumn> columnList) {
        if (objectLs.size() == 0) {
            return;
        }

        EList<Indicator> indicators = analysis.getResults().getIndicators();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
        int recordIncrement = 0;
        if (indicators != null) {
            for (Indicator indicator : indicators) {
                if (!this.continueRun()) {
                    break;
                }
                if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
                    indicator.handle(objectLs);
                    // feature 19192,store all rows value for RowCountIndicator
                    if (indicator instanceof SimpleStatIndicator) {
                        SimpleStatIndicator simpIndi = (SimpleStatIndicator) indicator;
                        for (Indicator leafIndicator : simpIndi.getLeafIndicators()) {
                            if (!this.continueRun()) {
                                break;
                            }
                            // MOD 20130517 yyin: TDQ-7279 Column Set Analysis on FIle/DB - Can't view rows for
                            // (distinct count, unique count, duplicate count, etc).
                            if (!analysis.getParameters().isStoreData()) {// ~
                                continue;
                            }
                            List<Object[]> valueObjectList = initDataSet(leafIndicator, indicToRowMap);

                            recordIncrement = valueObjectList.size();
                            Object[] valueObject = new Object[columnList.size()];
                            if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                                for (int j = 0; j < columnList.size(); j++) {
                                    if (!this.continueRun()) {
                                        break;
                                    }
                                    Object newobject = PluginConstant.EMPTY_STRING;
                                    // if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                                    if (j < rowValues.length) {
                                        newobject = rowValues[j];
                                    }
                                    if (recordIncrement < valueObjectList.size()) {
                                        valueObjectList.get(recordIncrement)[j] = newobject;
                                    } else {
                                        valueObject[j] = newobject;
                                        valueObjectList.add(valueObject);
                                    }
                                    // }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * handle Objects and store data for MDM.
     * 
     * @param rowMap
     * @param objectLs
     * @param columnList
     */
    private void handleObjects(Map<String, String> rowMap, EList<Object> objectLs, List<TdXmlElementType> columnList) {
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
        int recordIncrement = 0;
        for (Indicator indicator : indicators) {
            if (!this.continueRun()) {
                break;
            }
            if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
                indicator.handle(objectLs);
                if (indicator instanceof SimpleStatIndicator) {
                    if (!indicator.isStoreData()) {
                        continue;
                    }
                    SimpleStatIndicator simpIndi = (SimpleStatIndicator) indicator;
                    for (Indicator leafIndicator : simpIndi.getLeafIndicators()) {
                        if (!this.continueRun()) {
                            break;
                        }
                        if (!(leafIndicator instanceof RowCountIndicator)) {
                            continue;
                        }
                        List<Object[]> valueObjectList = initDataSet(leafIndicator, indicToRowMap);
                        recordIncrement = valueObjectList.size();

                        int offset = 0;
                        for (TdXmlElementType columnElement : columnList) {
                            if (!this.continueRun()) {
                                break;
                            }
                            Object newobject = rowMap.get(columnElement.getName());
                            if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                                if (recordIncrement < valueObjectList.size()) {
                                    valueObjectList.get(recordIncrement)[offset] = newobject;
                                } else {
                                    Object[] valueObject = new Object[columnList.size()];
                                    valueObject[offset] = newobject;
                                    valueObjectList.add(valueObject);
                                }
                            }
                            offset++;
                        }
                    }
                }
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
        } else if (isMdm) {
            if (mdmWebserviceConn.checkDatabaseConnection().isOk()) {
                return new ReturnCode(true);
            }
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
        if (isDelimitedFile || isMdm) {
            return new ReturnCode();
        }
        return super.closeConnection();
    }

    protected List<Object[]> initDataSet(Indicator indicator, EMap<Indicator, AnalyzedDataSet> indicToRowMap) {
        AnalyzedDataSet analyzedDataSet = indicToRowMap.get(indicator);
        List<Object[]> valueObjectList = null;
        if (analyzedDataSet == null) {
            analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
            indicToRowMap.put(indicator, analyzedDataSet);
            analyzedDataSet.setDataCount(analysis.getParameters().getMaxNumberRows());
            analyzedDataSet.setRecordSize(0);
        }
        valueObjectList = analyzedDataSet.getData();
        if (valueObjectList == null) {
            valueObjectList = new ArrayList<Object[]>();
            analyzedDataSet.setData(valueObjectList);
        }

        return valueObjectList;
    }

    /**
     * store data which from 'simpleIndicator.getListRows()' except RowCountIndicator.
     * 
     * @param indicToRowMap
     */
    private void storeDataSet() {
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
        for (Indicator indicator : analysis.getResults().getIndicators()) {
            if (indicator instanceof SimpleStatIndicator) {
                SimpleStatIndicator simpleIndicator = (SimpleStatIndicator) indicator;
                List<Object[]> listRows = simpleIndicator.getListRows();
                if (!analysis.getParameters().isStoreData() || listRows == null || listRows.isEmpty()) {
                    break;
                }
                for (Indicator leafIndicator : simpleIndicator.getLeafIndicators()) {
                    if (leafIndicator instanceof RowCountIndicator) {
                        continue;
                    }
                    List<Object[]> dataList = new ArrayList<Object[]>();
                    AnalyzedDataSet analyzedDataSet = indicToRowMap.get(leafIndicator);
                    if (analyzedDataSet == null) {
                        analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
                        indicToRowMap.put(leafIndicator, analyzedDataSet);
                        analyzedDataSet.setDataCount(analysis.getParameters().getMaxNumberRows());
                        analyzedDataSet.setRecordSize(0);
                    }

                    for (int i = 0; i < listRows.size(); i++) {
                        Object[] object = listRows.get(i);
                        // the last element store the count value.
                        Object count = object[object.length > 0 ? object.length - 1 : 0];
                        if (leafIndicator instanceof DistinctCountIndicator) {
                            dataList.add(object);
                        } else if (leafIndicator instanceof UniqueCountIndicator) {
                            if (count != null && NumberUtils.isNumber(count + PluginConstant.EMPTY_STRING)) {
                                if (Long.valueOf(count + PluginConstant.EMPTY_STRING).longValue() == 1) {
                                    dataList.add(object);
                                }
                            }
                        } else if (leafIndicator instanceof DuplicateCountIndicator) {
                            if (count != null && NumberUtils.isNumber(count + PluginConstant.EMPTY_STRING)) {
                                if (Long.valueOf(count + PluginConstant.EMPTY_STRING).longValue() > 1) {
                                    dataList.add(object);
                                }
                            }
                        }
                    }
                    analyzedDataSet.setData(dataList);
                }
                // MOD sizhaoliu TDQ-7144 clear the listRows after usage for drill down
                if (!simpleIndicator.isStoreData()) {
                    simpleIndicator.setListRows(new ArrayList<Object[]>());
                }
            }

            if (indicator instanceof AllMatchIndicator) {
                AllMatchIndicator allMatchIndicator = (AllMatchIndicator) indicator;
                if (!allMatchIndicator.isStoreData()) {
                    allMatchIndicator.setListRows(new ArrayList<Object[]>());
                }
            }
        }
    }

    public TdXmlSchema getTdXmlDocument() {
        return this.tdXmlDocument;
    }

    public void setTdXmlDocument(TdXmlSchema tdXmlDocument) {
        this.tdXmlDocument = tdXmlDocument;
    }

    public MdmWebserviceConnection getMdmWebserviceConn() {
        return this.mdmWebserviceConn;
    }

    public void setMdmWebserviceConn(MdmWebserviceConnection mdmWebserviceConn) {
        this.mdmWebserviceConn = mdmWebserviceConn;
    }

    @Override
    public ReturnCode evaluateIndicators(String sqlStatement, boolean closeConnection) {
        ReturnCode returnCode = super.evaluateIndicators(sqlStatement, closeConnection);
        storeDataSet();
        return returnCode;
    }
}
