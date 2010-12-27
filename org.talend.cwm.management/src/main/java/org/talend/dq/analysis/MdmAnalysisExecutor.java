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
package org.talend.dq.analysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.cwm.db.connection.MdmWebserviceConnection;
import org.talend.cwm.db.connection.XQueryExpressionUtil;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.dq.indicators.MdmIndicatorEvaluator;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. TODO 10238
 */
public class MdmAnalysisExecutor extends AnalysisExecutor {

    private Connection dataprovider;

    protected MdmWebserviceConnection mdmConnection;

    private static Logger log = Logger.getLogger(MdmAnalysisExecutor.class);

    protected boolean isAccessWith(Connection dp) {
        if (dataprovider == null) {
            dataprovider = dp;
            return true;
        }
        // else compare
        if (ProxyRepositoryViewObject.areSame(dataprovider, dp)) {
            return true;
        }
        // else
        return false;
    }

    protected boolean runAnalysis(Analysis analysis, String sqlStatement) {
        MdmIndicatorEvaluator eval = new MdmIndicatorEvaluator(analysis);
        eval.setMonitor(getMonitor());
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            assert indicator != null;
            TdXmlElementType tdXmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(indicator.getAnalyzedElement());
            if (tdXmlElement == null) {
                continue;
            }
            // --- get the schema owner
            if (!belongToSameDocument(tdXmlElement)) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.GivenColumn", tdXmlElement.getName()); //$NON-NLS-1$
                return false;
            }
            // String columnName = ColumnHelper.getFullName(tdColumn);
            eval.storeIndicator(tdXmlElement.getName(), indicator);
            eval.setTdXmlDocument(tdXmlElement.getOwnedDocument());
        }
        TypedReturnCode<MdmWebserviceConnection> mdmReturnObj = getMdmConnection(analysis);
        // open a connection
        if (!mdmReturnObj.isOk()) {
            log.error(mdmReturnObj.getMessage());
            this.errorMessage = mdmReturnObj.getMessage();
            return false;
        }
        eval.setMdmconn(mdmReturnObj.getObject());
        boolean closeAtTheEnd = true;
        ReturnCode rc = eval.evaluateIndicators(sqlStatement, closeAtTheEnd);
        if (!rc.isOk()) {
            log.warn(rc.getMessage());
            this.errorMessage = rc.getMessage();
        }
        return true;
    }

    /**
     * DOC xqliu Comment method "belongToSameDocument".
     * 
     * @param xmlElemet
     * @return
     */
    protected boolean belongToSameDocument(final TdXmlElementType xmlElemet) {
        return true;
    }

    @Override
    protected String createSqlStatement(Analysis analysis) {
        this.cachedAnalysis = analysis;
        // int paginationNum = analysis.getParameters().getMaxNumberRows();
        StringBuilder sql = new StringBuilder("let $_leres0_ := //");
        StringBuilder selectElement = new StringBuilder();
        EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();
        if (analysedElements.isEmpty()) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.CannotCreateSQLStatement",//$NON-NLS-1$
                    analysis.getName());
            return null;
        }
        Set<TdXmlSchema> fromPart = new HashSet<TdXmlSchema>();
        final Iterator<ModelElement> iterator = analysedElements.iterator();
        String parentAnalyzedElementName = null;
        TdXmlSchema xmlDocument = null;
        String analyzedElementName = null;
        while (iterator.hasNext()) {
            ModelElement modelElement = iterator.next();
            // --- preconditions
            TdXmlElementType tdXmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(modelElement);
            if (tdXmlElement == null) {
                this.errorMessage = "given element can't be used.";
                return null;
            }
            ModelElement parentElement = XmlElementHelper.getParentElement(tdXmlElement);
            if (parentElement == null) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoOwnerFound", tdXmlElement.getName()); //$NON-NLS-1$
            }
            TdXmlElementType parentXmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(parentElement);
            if (parentXmlElement == null) {
                this.errorMessage = Messages.getString(
                        "ColumnAnalysisExecutor.NoContainerFound", parentElement.getName(), parentXmlElement); //$NON-NLS-1$
                return null;
            }
            xmlDocument = parentXmlElement.getOwnedDocument();
            if (!analysis.getParameters().isStoreData()) {
                parentAnalyzedElementName = parentElement.getName();
                analyzedElementName = tdXmlElement.getName();
                if (selectElement.length() > 0) {
                    selectElement.append("|$");
                    selectElement.append(parentAnalyzedElementName);
                    selectElement.append("/");
                } else {
                    selectElement.append("$");
                    selectElement.append(parentAnalyzedElementName);
                    selectElement.append("/");
                }
                selectElement.append(analyzedElementName);
            }
            fromPart.add(xmlDocument);
        }
        if (fromPart.size() != 1) {
            log.error("Java analysis must be run on only one table. The number of different tables is " + fromPart.size() + ".");
            this.errorMessage = "Cannot run a Java analysis on several tables. Use only columns from one table.";
            return null;
        }
        if (analysis.getParameters().isStoreData()) {
            TdXmlElementType parentElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(XmlElementHelper
                    .getParentElement(SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(analysedElements.get(0))));
            parentAnalyzedElementName = parentElement.getName();
            List<TdXmlElementType> columnList = org.talend.cwm.db.connection.ConnectionUtils.getXMLElements(parentElement);
            Iterator<TdXmlElementType> iter = columnList.iterator();
            while (iter.hasNext()) {
                TdXmlElementType xmlElemenet = iter.next();
                if (DqRepositoryViewService.hasChildren(xmlElemenet)) {
                    continue;
                }
                if (selectElement.length() > 0) {
                    selectElement.append("|$");
                    selectElement.append(parentElement.getName());
                    selectElement.append("/");
                } else {
                    selectElement.append("$");
                    selectElement.append(parentElement.getName());
                    selectElement.append("/");
                }
                selectElement.append(xmlElemenet.getName());
            }
        }

        if (selectElement.length() <= 0) {
            this.errorMessage = "Not any element to be choice";
            return null;
        }
        // for
        sql.append(parentAnalyzedElementName);// remaind on version 2
        sql.append(" let $_page_ := for $");

        sql.append(parentAnalyzedElementName);

        sql.append(" in subsequence($_leres0_,1,");
        sql.append(XQueryExpressionUtil.ROWS_PER_PAGE);
        sql.append(") ");

        // where--- get data filter
        ModelElementAnalysisHandler handler = new ModelElementAnalysisHandler();
        handler.setAnalysis(analysis);
        String stringDataFilter = handler.getStringDataFilter();
        if (stringDataFilter != null && !stringDataFilter.equals("")) {
            // TODO The user will don't know what is our name of variable in xquery,so need us add our name of variable
            // before the name of column by user input.Now our name of variable is parentNode name.
            sql.append(dbms().where().toLowerCase());// All of function in xquery,character is lowercase
            sql.append(stringDataFilter);
        }
        // return
        sql.append("return <result>{if ($");
        sql.append(parentAnalyzedElementName);
        sql.append(") then ");
        sql.append(selectElement);

        sql.append(" else <null/>}</result> return insert-before($_page_,0,<totalCount>{count($_leres0_)}</totalCount>)");
        return sql.toString();
    }

    @Override
    protected boolean check(final Analysis analysis) {
        if (analysis == null) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.AnalysisIsNull"); //$NON-NLS-1$
            return false;
        }
        if (!super.check(analysis)) {
            // error message already set in super method.
            return false;
        }

        // --- check existence of context
        AnalysisContext context = analysis.getContext();
        if (context == null) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoContextSet", analysis.getName()); //$NON-NLS-1$
            return false;
        }

        // --- check that there exists at least on element to analyze
        if (context.getAnalysedElements().size() == 0) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.AnalysisHaveAtLeastOneColumn"); //$NON-NLS-1$
            return false;
        }

        // --- check that the connection has been set
        if (context.getConnection() == null) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoConnectionSet"); //$NON-NLS-1$
            return false;
        }

        return checkAnalyzedElements(analysis, context);
    }

    /**
     * DOC xqliu Comment method "checkAnalyzedElements".
     * 
     * @param analysis
     * @param context
     * @return
     */
    protected boolean checkAnalyzedElements(final Analysis analysis, AnalysisContext context) {
        ModelElementAnalysisHandler analysisHandler = new ModelElementAnalysisHandler();
        analysisHandler.setAnalysis(analysis);

        for (ModelElement node : context.getAnalysedElements()) {
            TdXmlElementType xmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(node);

            // --- Check that each analyzed element has at least one indicator
            if (analysisHandler.getIndicators(xmlElement).size() == 0) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.EachColumnHaveOneIndicator"); //$NON-NLS-1$
                return false;
            }

            // --- get the data provider
            Connection dp = ConnectionHelper.getTdDataProvider(xmlElement.getOwnedDocument());
            if (!isAccessWith(dp)) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.AllColumnsBelongSameConnection", //$NON-NLS-1$
                        xmlElement.getName(), dataprovider.getName());
                return false;
            }
        }
        return true;
    }

    /**
     * DOC xqliu Comment method "getMdmConnection".
     * 
     * @param analysis
     * @return
     */
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

}
