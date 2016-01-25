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
package org.talend.dq.analysis;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.db.connection.MdmWebserviceConnection;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.columnset.AllMatchIndicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dq.dbms.GenericSQLHandler;
import org.talend.dq.helper.AnalysisExecutorHelper;
import org.talend.dq.indicators.ColumnSetIndicatorEvaluator;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Classifier;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class ColumnSetAnalysisExecutor extends AnalysisExecutor {

    private static Logger log = Logger.getLogger(ColumnSetAnalysisExecutor.class);

    protected boolean isDelimitedFile = false;

    protected boolean isMdm = false;

    /**
     * DOC yyi 2011-02-24 17871:delimitefile.
     */
    public ColumnSetAnalysisExecutor(boolean isDelimitedFile, boolean isMdm) {
        super();
        this.isDelimitedFile = isDelimitedFile;
        this.isMdm = isMdm;
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
        // MOD qiongli 2011-3-11 featue 17869.consider of mdm connection
        for (Indicator indicator : indicators) {
            if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
                ColumnSetMultiValueIndicator colSetMultValIndicator = (ColumnSetMultiValueIndicator) indicator;
                colSetMultValIndicator.prepare();
                eval.storeIndicator(indicator.getName(), colSetMultValIndicator);
                if (isMdm) {
                    EList<ModelElement> modelElementLs = colSetMultValIndicator.getAnalyzedColumns();
                    for (ModelElement mod : modelElementLs) {
                        TdXmlElementType tdXmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(mod);
                        if (tdXmlElement != null) {
                            eval.setTdXmlDocument(tdXmlElement.getOwnedDocument());
                            break;
                        }
                    }
                }
            }
        }

        TypedReturnCode<java.sql.Connection> connection = null;
        // MOD yyi 2011-02-22 17871:delimitefile
        if (isMdm) {
            TypedReturnCode<MdmWebserviceConnection> mdmReturnObj = getMdmConnection(analysis);
            if (!mdmReturnObj.isOk()) {
                log.error(mdmReturnObj.getMessage());
                return false;
            }
            eval.setMdmWebserviceConn(mdmReturnObj.getObject());
        } else if (!isDelimitedFile) {
            connection = getConnectionBeforeRun(analysis);
            if (!connection.isOk()) {
                return this.traceError(connection.getMessage());
            }

            // set it into the evaluator
            eval.setConnection(connection.getObject());
            // use pooled connection
            eval.setPooledConnection(POOLED_CONNECTION);

        }

        // when to close connection
        boolean closeAtTheEnd = true;
        ReturnCode rc = eval.evaluateIndicators(sqlStatement, closeAtTheEnd);

        // MOD gdbu 2011-8-12 : file delimited connection is null
        // close connection
        if (connection != null) {
            if (POOLED_CONNECTION) {
                // release the pooled connection
                resetConnectionPool(analysis);
            } else {
                ConnectionUtils.closeConnection(connection.getObject());
            }
        }
        if (!rc.isOk()) {
            traceError(rc.getMessage());
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
            return PluginConstant.EMPTY_STRING;
        } else if (isMdm) {
            EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();
            if (analysedElements.isEmpty()) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.CannotCreateSQLStatement",//$NON-NLS-1$
                        analysis.getName());
                return null;
            }
            StringBuilder sql = new StringBuilder("//");//$NON-NLS-1$
            for (ModelElement modelElement : analysedElements) {
                TdXmlElementType tdXmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(modelElement);
                if (tdXmlElement == null) {
                    this.errorMessage = "given element can't be used.";//$NON-NLS-1$
                    return null;
                }
                ModelElement parentElement = XmlElementHelper.getParentElement(tdXmlElement);
                if (parentElement == null) {
                    this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoOwnerFound", tdXmlElement.getName()); //$NON-NLS-1$
                }
                TdXmlElementType parentXmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(parentElement);
                if (parentXmlElement == null) {
                    this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoContainerFound", parentElement.getName()); //$NON-NLS-1$
                    return null;
                }
                sql.append(parentXmlElement.getName());
                break;
            }
            return sql.toString();

        }
        // ~
        this.cachedAnalysis = analysis;
        StringBuilder sql = new StringBuilder("SELECT ");//$NON-NLS-1$
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        // MOD yyi 2011-02-22 17871:delimitefile
        EList<ModelElement> analysedElements = null;
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
        final Iterator<ModelElement> iterator = analysedElements.iterator();
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
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoContainerFound", col.getName()); //$NON-NLS-1$
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
            log.error(Messages.getString("ColumnSetAnalysisExecutor.JAVAANALYSISMUSTBERUNONONETABLE", fromPart.size()));//$NON-NLS-1$
            this.errorMessage = Messages.getString("ColumnSetAnalysisExecutor.CANNOTRUNONSEVERALTABLES");//$NON-NLS-1$
            return null;
        }
        // select all the column to be prepare for drill down.
        if (analysis.getParameters().isStoreData()) {
            // MOD klliu 2011-06-30 bug 22523 whichever is Table or View,that finds columns should ues columnset
            EObject eContainer = analysedElements.get(0).eContainer();
            List<TdColumn> columnList = ColumnSetHelper.getColumns(SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(eContainer));
            // ~
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
        sql.append(AnalysisExecutorHelper.getTableName(fromPart.iterator().next(), dbms()));

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
        String patternNames = PluginConstant.EMPTY_STRING;
        for (RegexpMatchingIndicator rmi : indicators) {
            if (null == rmi.getRegex()) {

                patternNames += System.getProperty("line.separator") + "\"" + rmi.getName() + "\"";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            } else if (rmi.getRegex().equals(rmi.getName())) {
                patternNames += System.getProperty("line.separator") + "\"" + rmi.getName() + "\"";//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            }
        }
        if (PluginConstant.EMPTY_STRING != patternNames) {
            this.errorMessage = Messages.getString("MultiColumnAnalysisExecutor.checkAllMatchIndicatorForDbType", patternNames);//$NON-NLS-1$
            return false;
        }
        return true;
    }

    protected TypedReturnCode<MdmWebserviceConnection> getMdmConnection(Analysis analysis) {
        TypedReturnCode<MdmWebserviceConnection> rc = new TypedReturnCode<MdmWebserviceConnection>(true);
        MDMConnection dataProvider = (MDMConnection) analysis.getContext().getConnection();
        Properties props = new Properties();
        props.setProperty(TaggedValueHelper.USER, dataProvider.getUsername());
        props.setProperty(TaggedValueHelper.PASSWORD, dataProvider.getPassword());
        props.setProperty(TaggedValueHelper.UNIVERSE, dataProvider.getUniverse() == null ? PluginConstant.EMPTY_STRING
                : dataProvider.getUniverse());
        props.setProperty(TaggedValueHelper.DATA_FILTER, ConnectionHelper.getDataFilter(dataProvider));
        MdmWebserviceConnection tempMdmConnection = new MdmWebserviceConnection(dataProvider.getPathname(), props);
        rc.setObject(tempMdmConnection);
        rc.setOk(tempMdmConnection.checkDatabaseConnection().isOk());
        rc.setMessage(dataProvider.getPathname());
        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#evaluate(org.talend.dataquality.analysis.Analysis,
     * java.sql.Connection, java.lang.String)
     */
    @Override
    protected ReturnCode evaluate(Analysis analysis, Connection connection, String sqlStatement) {
        // no need to implement
        return null;
    }
}
