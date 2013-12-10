// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.talend.commons.utils.StringUtils;
import org.talend.core.language.LanguageManager;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.fileprocess.FileInputDelimited;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentPackage;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

import com.csvreader.CsvReader;

/**
 * DOC yyin class global comment. Detailled comment
 */
public final class AnalysisExecutorHelper {

    private static Logger log = Logger.getLogger(AnalysisExecutorHelper.class);

    /**
     * get full name as: db.catalog.table, if has catalog/schema
     * 
     * @param analyzedColumns
     * @param dbmsLanguage
     * @return
     */
    public static String getTableName(ModelElement analyzedColumn, DbmsLanguage dbmsLanguage) {
        ModelElement columnSetOwner = findColumnSetOwner(analyzedColumn);
        String tableName = columnSetOwner.getName();

        String schemaName = getQuotedSchemaName(columnSetOwner, dbmsLanguage);
        String catalogName = getQuotedCatalogName(columnSetOwner, dbmsLanguage);
        if (catalogName == null && schemaName != null) {
            // try to get catalog above schema
            final Schema parentSchema = SchemaHelper.getParentSchema(columnSetOwner);
            final Catalog parentCatalog = CatalogHelper.getParentCatalog(parentSchema);
            catalogName = parentCatalog != null ? parentCatalog.getName() : null;
        }
        // MOD by zshen: change schemaName of sybase database to Table's owner.
        if (ConnectionUtils.isSybaseeDBProducts(dbmsLanguage.getDbmsName())) {
            schemaName = ColumnSetHelper.getTableOwner(columnSetOwner);
        }
        // ~11934
        return dbmsLanguage.toQualifiedName(catalogName, schemaName, tableName);
    }

    public static ModelElement findColumnSetOwner(ModelElement column) {
        EObject owner = column.eContainer();
        ColumnSet set = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(owner);
        MetadataTable mdColumn = SwitchHelpers.METADATA_TABLE_SWITCH.doSwitch(owner);

        if (null == set && mdColumn != null) {
            return mdColumn;
        } else if (null != set) {
            return set;
        }
        return column;
    }

    public static String getQuotedCatalogName(ModelElement analyzedElement, DbmsLanguage dbmsLanguage) {
        final Catalog parentCatalog = CatalogHelper.getParentCatalog(analyzedElement);
        return parentCatalog == null ? null : dbmsLanguage.quote(parentCatalog.getName());
    }

    public static String getQuotedSchemaName(ModelElement columnSetOwner, DbmsLanguage dbmsLanguage) {
        final Schema parentSchema = SchemaHelper.getParentSchema(columnSetOwner);
        return (parentSchema == null) ? null : dbmsLanguage.quote(parentSchema.getName());
    }

    public static CsvReader createCsvReader(File file, DelimitedFileConnection delimitedFileconnection)
            throws UnsupportedEncodingException, FileNotFoundException {
        String separator = delimitedFileconnection.getFieldSeparatorValue();
        String encoding = delimitedFileconnection.getEncoding();
        if (delimitedFileconnection.isContextMode()) {
            separator = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, separator);
            encoding = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, encoding);
        }

        return new CsvReader(new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file),
                encoding == null ? "UTF-8" : encoding)), ParameterUtil //$NON-NLS-1$
                .trimParameter(separator).charAt(0));
    }

    /**
     * 
     * DOC qiongli Comment method "initializeCsvReader".
     * 
     * @param csvReader
     * @param connection
     */
    public static void initializeCsvReader(DelimitedFileConnection delimitedFileconnection, CsvReader csvReader) {
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

    public static FileInputDelimited createFileInputDelimited(DelimitedFileConnection delimitedFileconnection) throws IOException {
        String rowSeparator = delimitedFileconnection.getRowSeparatorValue();
        String encoding = delimitedFileconnection.getEncoding();
        String fieldSeparatorValue = delimitedFileconnection.getFieldSeparatorValue();
        if (delimitedFileconnection.isContextMode()) {
            rowSeparator = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, rowSeparator);
            encoding = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, encoding);
            fieldSeparatorValue = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, fieldSeparatorValue);
        }
        boolean isSpliteRecord = delimitedFileconnection.isSplitRecord();
        boolean isSkipeEmptyRow = delimitedFileconnection.isRemoveEmptyRow();
        String languageName = LanguageManager.getCurrentLanguage().getName();

        String zero = "0"; //$NON-NLS-1$
        int limitValue = AnalysisExecutorHelper.getLimitValue(delimitedFileconnection);
        int headValue = AnalysisExecutorHelper.getHeadValue(delimitedFileconnection);
        int footValue = 0;
        String footing = delimitedFileconnection.getFooterValue();
        String path = AnalysisExecutorHelper.getFilePath(delimitedFileconnection);
        if (delimitedFileconnection.isContextMode()) {
            footing = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, footing);
            footValue = Integer.parseInt(footing == PluginConstant.EMPTY_STRING ? zero : footing);
        } else {
            footValue = Integer.parseInt(footing == null || PluginConstant.EMPTY_STRING.equals(footing) ? zero : footing);
        }

        return new FileInputDelimited(ParameterUtil.trimParameter(path), ParameterUtil.trimParameter(encoding),
                ParameterUtil.trimParameter(StringUtils.loadConvert(fieldSeparatorValue, languageName)),
                ParameterUtil.trimParameter(StringUtils.loadConvert(rowSeparator, languageName)), isSkipeEmptyRow, headValue,
                footValue, limitValue, -1, isSpliteRecord);
    }

    public static int getHeadValue(DelimitedFileConnection fileconnection) {
        String zero = "0"; //$NON-NLS-1$
        String heading = fileconnection.getHeaderValue();
        if (fileconnection.isContextMode()) {
            heading = ConnectionUtils.getOriginalConntextValue(fileconnection, heading);
            return Integer.parseInt(heading == PluginConstant.EMPTY_STRING ? zero : heading);
        } else {
            return Integer.parseInt(heading == null || PluginConstant.EMPTY_STRING.equals(heading) ? zero : heading);
        }
    }

    public static int getLimitValue(DelimitedFileConnection delimitedFileconnection) {
        String zero = "0"; //$NON-NLS-1$
        String limiting = delimitedFileconnection.getLimitValue();
        if (delimitedFileconnection.isContextMode()) {
            limiting = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, limiting);
            return Integer.parseInt(PluginConstant.EMPTY_STRING.equals(limiting) || zero.equals(limiting) ? "-1" : limiting); //$NON-NLS-1$
        } else {// ~ 5346
            // MOD qionlgi 2011-5-12,bug 21115.
            if (limiting == null || PluginConstant.EMPTY_STRING.equals(limiting) || zero.equals(limiting)) {
                limiting = "-1"; //$NON-NLS-1$
            }
            return Integer.parseInt(limiting);
        }
    }

    public static String getFilePath(DelimitedFileConnection delimitedFileconnection) {
        String path = delimitedFileconnection.getFilePath();
        if (delimitedFileconnection.isContextMode()) {
            path = ConnectionUtils.getOriginalConntextValue(delimitedFileconnection, path);
        }
        return path;
    }

    /**
     * Method "check" checks that the analysis can be run.
     * 
     * @param analysis the analysis to prepare
     * @return true if ok.
     */
    public static ReturnCode check(Analysis analysis) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);

        // --- check existence of context
        AnalysisContext context = analysis.getContext();
        if (context == null) {
            rc.setMessage(Messages.getString("AnalysisExecutor.ContextNull", analysis.getName())); //$NON-NLS-1$
            rc.setOk(Boolean.FALSE);
            return rc;
        }
        // --- check that there exists at least on element to analyze
        if (context.getAnalysedElements().size() == 0) {
            rc.setMessage(Messages.getString("ColumnAnalysisExecutor.AnalysisHaveAtLeastOneColumn")); //$NON-NLS-1$
            rc.setOk(Boolean.FALSE);
            return rc;
        }
        // --- check that the connection has been set
        DataManager connection = context.getConnection();
        if (connection == null) {
            rc.setMessage(Messages.getString("AnalysisExecutor.NoConnectionFound", analysis.getName())); //$NON-NLS-1$
            rc.setOk(Boolean.FALSE);
            return rc;
        }
        if (log.isInfoEnabled()) {
            if (SoftwaredeploymentPackage.eINSTANCE.getDataProvider().isInstance(connection)) {
                // MOD 20130225 TDQ-6632 the name of the item should be given (not the pathname)
                log.info(Messages.getString("AnalysisExecutor.CONNECTIONTO", connection.getName()));//$NON-NLS-1$
            }
        }

        AnalysisResult results = analysis.getResults();
        if (results == null) {
            rc.setMessage(Messages.getString("AnalysisExecutor.AnalysisnotNotPrepareCorrect", analysis.getName())); //$NON-NLS-1$
            rc.setOk(Boolean.FALSE);
            return rc;
        }

        return rc;
    }

    public static long setExecutionDateInAnalysisResult(Analysis analysis) {
        final ExecutionInformations resultMetadata = analysis.getResults().getResultMetadata();
        final long startime = System.currentTimeMillis();
        resultMetadata.setExecutionDate(new Date(startime));
        return startime;
    }

    /**
     * set the execution number of the analysis, if the running result is ok
     * 
     * @param analysis - which need to update the execution number
     * @param isRunAnaResultok - the running result of the analysis is ok or not
     */
    public static void setExecutionNumberInAnalysisResult(Analysis analysis, boolean isRunAnaResultok) {
        final ExecutionInformations resultMetadata = analysis.getResults().getResultMetadata();
        resultMetadata.setLastRunOk(isRunAnaResultok);
        int executionNumber = resultMetadata.getExecutionNumber() + 1;
        resultMetadata.setExecutionNumber(executionNumber);
        if (isRunAnaResultok) {
            resultMetadata.setLastExecutionNumberOk(executionNumber);
        }
    }

    public static void setExecuteErrorMessage(Analysis analysis, String errorMessage) {
        analysis.getResults().getResultMetadata().setMessage(errorMessage);
    }
}
