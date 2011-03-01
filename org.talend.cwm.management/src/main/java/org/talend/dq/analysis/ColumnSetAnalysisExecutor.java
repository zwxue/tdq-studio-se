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
package org.talend.dq.analysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.columnset.AllMatchIndicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dq.dbms.GenericSQLHandler;
import org.talend.dq.indicators.ColumnSetIndicatorEvaluator;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Classifier;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class ColumnSetAnalysisExecutor extends AnalysisExecutor {

    private static Logger log = Logger.getLogger(ColumnSetAnalysisExecutor.class);

    protected boolean isDelimitedFile = false;

    /**
     * DOC yyi 2011-02-24 17871:delimitefile
     */
    public ColumnSetAnalysisExecutor(boolean isDelimitedFile) {
        super();
        this.isDelimitedFile = isDelimitedFile;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#runAnalysis(org.talend.dataquality.analysis.Analysis,
     * java.lang.String)
     */
    @Override
    protected boolean runAnalysis(Analysis analysis, String sqlStatement) {
        ColumnSetIndicatorEvaluator eval = new ColumnSetIndicatorEvaluator(analysis);
        eval.setMonitor(getMonitor());
        // --- add indicators
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
                ColumnSetMultiValueIndicator colSetMultValIndicator = (ColumnSetMultiValueIndicator) indicator;
                colSetMultValIndicator.prepare();
                eval.storeIndicator(indicator.getName(), colSetMultValIndicator);
            }
        }

        // MOD yyi 2011-02-22 17871:delimitefile
        if (!isDelimitedFile) {

            TypedReturnCode<java.sql.Connection> connection = getConnection(analysis);
            if (!connection.isOk()) {
                log.error(connection.getMessage());
                this.errorMessage = connection.getMessage();
                return false;
            }

            // set it into the evaluator
            eval.setConnection(connection.getObject());
        }

        // when to close connection
        boolean closeAtTheEnd = true;
        ReturnCode rc = eval.evaluateIndicators(sqlStatement, closeAtTheEnd);
        if (!rc.isOk()) {
            log.warn(rc.getMessage());
            this.errorMessage = rc.getMessage();
        }

        return true;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)
     */
    @Override
    protected String createSqlStatement(Analysis analysis) {

        // MOD yyi 2011-02-22 17871:delimitefile
        if (isDelimitedFile) {
            return "";
        }
        // ~
        this.cachedAnalysis = analysis;
        StringBuilder sql = new StringBuilder("SELECT ");
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        // MOD yyi 2011-02-22 17871:delimitefile
        EList<MetadataColumn> analysedElements = null;
        for (Indicator indicator : indicators) {
            if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
                ColumnSetMultiValueIndicator colSetMultValIndicator = (ColumnSetMultiValueIndicator) indicator;
                if (analysedElements == null) {
                    analysedElements = colSetMultValIndicator.getAnalyzedColumns();
                } else {
                    analysedElements.addAll(colSetMultValIndicator.getAnalyzedColumns());
                }
            }
        }

        if (analysedElements == null || analysedElements.isEmpty()) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.CannotCreateSQLStatement",//$NON-NLS-1$
                    analysis.getName());
            return null;
        }
        Set<ColumnSet> fromPart = new HashSet<ColumnSet>();
        // MOD yyi 2011-02-22 17871:delimitefile, indiactor changed
        final Iterator<MetadataColumn> iterator = analysedElements.iterator();
        while (iterator.hasNext()) { // for (ModelElement modelElement : analysedElements) {
            ModelElement modelElement = iterator.next();
            // --- preconditions
            TdColumn col = SwitchHelpers.COLUMN_SWITCH.doSwitch(modelElement);
            if (col == null) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.GivenElementIsNotColumn", modelElement); //$NON-NLS-1$
                return null;
            }
            Classifier owner = col.getOwner();
            if (owner == null) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoOwnerFound", col.getName()); //$NON-NLS-1$
            }
            ColumnSet colSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(owner);
            if (colSet == null) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoContainerFound", col.getName(), colSet); //$NON-NLS-1$
                return null;
            }
            // else add into select
            // select all the column to be prepare for drill down when user need.
            if (!analysis.getParameters().isStoreData()) {
                sql.append(this.quote(col.getName()));
                // append comma if more columns exist
                if (iterator.hasNext()) {
                    sql.append(',');
                }
            }
            // add from
            fromPart.add(colSet);

        }
        if (fromPart.size() != 1) {
            log.error("Java analysis must be run on only one table. The number of different tables is " + fromPart.size() + ".");
            this.errorMessage = "Cannot run a Java analysis on several tables. Use only columns from one table.";
            return null;
        }
        // select all the column to be prepare for drill down.
        if (analysis.getParameters().isStoreData()) {
            List<TdColumn> columnList = TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(analysedElements.get(0)
                    .eContainer()));
            Iterator<TdColumn> iter = columnList.iterator();
            while (iter.hasNext()) {
                TdColumn column = iter.next();
                sql.append(this.quote(column.getName()));
                // append comma if more columns exist
                if (iter.hasNext()) {
                    sql.append(',');
                }
            }
        }

        // add from clause
        sql.append(dbms().from());
        // if(CatalogHelper.fromPart.iterator().next())
        ModelElement element = fromPart.iterator().next();
        Package parentRelation = PackageHelper.getParentPackage((MetadataTable) fromPart.iterator().next());
        if (parentRelation instanceof Schema) {
            sql.append(dbms().toQualifiedName(null, parentRelation.getName(), element.getName()));
        } else if (parentRelation instanceof Catalog) {
            String ownerUser = null;
            if (ConnectionUtils.isSybaseeDBProducts(dbms().getDbmsName())) {
                ownerUser = ColumnSetHelper.getTableOwner((ModelElement) element);
            }
            sql.append(dbms().toQualifiedName(parentRelation.getName(), ownerUser, element.getName()));
        }

        // add where clause
        // --- get data filter
        ModelElementAnalysisHandler handler = new ModelElementAnalysisHandler();
        handler.setAnalysis(analysis);
        String stringDataFilter = handler.getStringDataFilter();

        sql.append(GenericSQLHandler.WHERE_CLAUSE);

        String sqlStatement = sql.toString();
        sqlStatement = dbms().addWhereToStatement(sqlStatement, stringDataFilter);
        return sqlStatement;
    }

    @Override
    protected boolean check(Analysis analysis) {

        boolean check = super.check(analysis);
        if (!check) {
            return false;
        } else {
            EList<Indicator> indicators = analysis.getResults().getIndicators();
            for (Indicator indicator : indicators) {
                if (indicator instanceof AllMatchIndicator) {
                    if (!checkAllMatchIndicator((AllMatchIndicator) indicator)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected boolean checkAllMatchIndicator(AllMatchIndicator indicator) {
        EList<RegexpMatchingIndicator> indicators = indicator.getCompositeRegexMatchingIndicators();
        String patternNames = "";
        for (RegexpMatchingIndicator rmi : indicators) {
            if (null == rmi.getRegex()) {

                patternNames += System.getProperty("line.separator") + "\"" + rmi.getName() + "\"";
            } else if (rmi.getRegex().equals(rmi.getName())) {
                patternNames += System.getProperty("line.separator") + "\"" + rmi.getName() + "\"";
            }
        }
        if ("" != patternNames) {
            this.errorMessage = Messages.getString("MultiColumnAnalysisExecutor.checkAllMatchIndicatorForDbType", patternNames);
            return false;
        }
        return true;
    }



}
