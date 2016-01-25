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
package org.talend.dq.helper;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.talend.commons.utils.StringUtils;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.language.LanguageManager;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
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

/**
 * DOC yyin class global comment. Detailled comment
 */
public final class AnalysisExecutorHelper {

    private static Logger log = Logger.getLogger(AnalysisExecutorHelper.class);

    private static ITDQRepositoryService tdqRepositoryService = null;

    public static ITDQRepositoryService getTDQService() {
        if (tdqRepositoryService == null) {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(ITDQRepositoryService.class)) {
                tdqRepositoryService = (ITDQRepositoryService) org.talend.core.GlobalServiceRegister.getDefault().getService(
                        ITDQRepositoryService.class);
            }
        }
        return tdqRepositoryService;
    }

    /**
     * get full name as: db.catalog.table, if has catalog/schema
     * 
     * @param analyzedElement only for TdColumn and ColumnSet.
     * @param dbmsLanguage
     * @return
     * @deprecated instead of it by {@link DbmsLanguage#getQueryColumnSetWithPrefix(ColumnSet) or
     * DbmsLanguage#getQueryColumnSetWithPrefix(TdColumn)}
     */
    @Deprecated
    public static String getTableName(ModelElement analyzedElement, DbmsLanguage dbmsLanguage) {
        TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(analyzedElement);
        if (tdColumn != null) {
            return dbmsLanguage.getQueryColumnSetWithPrefix(tdColumn);
        }
        ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(analyzedElement);
        if (columnSet != null) {
            return dbmsLanguage.getQueryColumnSetWithPrefix(columnSet);
        }
        log.error(Messages.getString("AnalysisExecutorHelper.TableEmpty")); //$NON-NLS-1$
        return PluginConstant.EMPTY_STRING;
    }

    /**
     * 
     * DOC qiongli Comment method "findColumnSetOwner".
     * 
     * @param column
     * @return
     * @deprecated instead of it by {@link ColumnHelper#getColumnOwnerAsColumnSet(ModelElement)}
     */
    @Deprecated
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

    public static FileInputDelimited createFileInputDelimited(DelimitedFileConnection delimitedFileconnection) throws IOException {
        String rowSeparator = JavaSqlFactory.getRowSeparatorValue(delimitedFileconnection);
        String encoding = JavaSqlFactory.getEncoding(delimitedFileconnection);
        String fieldSeparatorValue = JavaSqlFactory.getFieldSeparatorValue(delimitedFileconnection);

        boolean isSpliteRecord = delimitedFileconnection.isSplitRecord();
        boolean isSkipeEmptyRow = delimitedFileconnection.isRemoveEmptyRow();
        String languageName = LanguageManager.getCurrentLanguage().getName();

        int limitValue = JavaSqlFactory.getLimitValue(delimitedFileconnection);
        int headValue = JavaSqlFactory.getHeadValue(delimitedFileconnection);
        int footValue = JavaSqlFactory.getFooterValue(delimitedFileconnection);
        String path = JavaSqlFactory.getURL(delimitedFileconnection);

        return new FileInputDelimited(ParameterUtil.trimParameter(path), ParameterUtil.trimParameter(encoding),
                ParameterUtil.trimParameter(StringUtils.loadConvert(fieldSeparatorValue, languageName)),
                ParameterUtil.trimParameter(StringUtils.loadConvert(rowSeparator, languageName)), isSkipeEmptyRow, headValue,
                footValue, limitValue, -1, isSpliteRecord);
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

        resultMetadata.setExecutionNumber(resultMetadata.getExecutionNumber() + 1);
        if (isRunAnaResultok) {
            resultMetadata.setLastExecutionNumberOk(resultMetadata.getLastExecutionNumberOk() + 1);
        }
    }

    public static void setExecuteErrorMessage(Analysis analysis, String errorMessage) {
        analysis.getResults().getResultMetadata().setMessage(errorMessage);
    }
}
