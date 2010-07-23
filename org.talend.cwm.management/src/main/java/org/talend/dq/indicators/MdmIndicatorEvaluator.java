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
package org.talend.dq.indicators;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EMap;
import org.talend.cwm.db.connection.MdmStatement;
import org.talend.cwm.db.connection.MdmWebserviceConnection;
import org.talend.cwm.db.connection.XQueryExpressionUtil;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class MdmIndicatorEvaluator extends IndicatorEvaluator {

    protected MdmWebserviceConnection mdmconn;

    private static Logger log = Logger.getLogger(MdmIndicatorEvaluator.class);

    protected TdXMLDocument tdXmlDocument;

    /**
     * DOC zshen MdmIndicatorEvaluator constructor comment.
     * 
     * @param analysis
     */
    public MdmIndicatorEvaluator(Analysis analysis) {
        super(analysis);
    }

    @Override
    protected ReturnCode executeSqlQuery(String sqlStatement) throws SQLException {
        ReturnCode returnCode = new ReturnCode(false);
        if (mdmconn == null) {
            return returnCode;
        }
        if (tdXmlDocument == null) {
            return returnCode;
        }
        MdmStatement statement = null;
        statement = mdmconn.createStatement();
        String[] resultSet = null;
        if (continueRun()) {

            try {
                List<String> strResultList = new ArrayList<String>();
                int totalcount = 0;
                returnCode.setOk(true);
                // init xQuery expression.
                XQueryExpressionUtil.toParseXquery(sqlStatement);
                do {
                    if (log.isInfoEnabled()) {
                        log.info("Executing query: " + XQueryExpressionUtil.getExpression());
                    }
                    returnCode.setOk(returnCode.isOk() && statement.execute(tdXmlDocument, XQueryExpressionUtil.getExpression()));
                    List<String> strResultListTemp = Arrays.asList(statement.getResultSet());
                    String totalNum = statement.getXmlNodeValue(strResultListTemp.get(0), "totalCount");
                    totalcount = totalNum == null ? 0 : Integer.parseInt(totalNum);
                    strResultList.addAll(strResultListTemp.subList(1, strResultListTemp.size()));
                    XQueryExpressionUtil.increaseVernier();
                } while (totalcount > strResultList.size());
                resultSet = strResultList.toArray(new String[strResultList.size()]);

            } catch (RemoteException e) {
                returnCode.setMessage(e.getMessage());
            } catch (ServiceException e) {
                returnCode.setMessage(e.getMessage());
            }
        }
        if (resultSet == null) {
            String mess = "No result set for this statement: " + sqlStatement;
            log.warn(mess);
            returnCode.setReturnCode(mess, false);
            return returnCode;
        }
        // 12919
        AnalysisResult anaResult = analysis.getResults();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = anaResult.getIndicToRowMap();
        indicToRowMap.clear();
        int recordIncrement = 0;
        // ~
        // the column by user choice
        List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
        TdXMLElement parentElement = SwitchHelpers.XMLELEMENT_SWITCH.doSwitch(XmlElementHelper
                .getParentElement(SwitchHelpers.XMLELEMENT_SWITCH.doSwitch(analysisElementList.get(0))));
        // all the column under the parentElement
        List<TdXMLElement> columnList = DqRepositoryViewService.getXMLElements(parentElement);
        List<Map<String, String>> resultSetList = new ArrayList<Map<String, String>>();
        if (analysis.getParameters().isStoreData()) {
            resultSetList = statement.tidyResultSet(columnList.toArray(new ModelElement[columnList.size()]), resultSet);
        } else {
            resultSetList = statement.tidyResultSet(analysisElementList.toArray(new ModelElement[analysisElementList.size()]),
                    resultSet);
        }
        label: for (Map<String, String> rowMap : resultSetList) {
            for (int i = 0; i < analysisElementList.size(); i++) {
                String col = analysisElementList.get(i).getName();// the name of column
                List<Indicator> indicators = getIndicators(col);
                String object = rowMap.get(col);// the value of column
                // --- give row to handle to indicators
                for (Indicator indicator : indicators) {
                    if (!continueRun()) {
                        break label;
                    }
                    indicator.handle(object);
                    // 12919
                    AnalyzedDataSet analyzedDataSet = indicToRowMap.get(indicator);
                    if (analyzedDataSet == null) {
                        analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
                        indicToRowMap.put(indicator, analyzedDataSet);
                        analyzedDataSet.setDataCount(analysis.getParameters().getMaxNumberRows());
                        analyzedDataSet.setRecordSize(0);
                    }

                    if (analysis.getParameters().isStoreData() && indicator.mustStoreRow()) {
                        List<Object[]> valueObjectList = initDataSet(indicator, indicToRowMap, object);
                        // MOD zshen add another loop to insert all of columnValue on the row into indicator.
                        recordIncrement = valueObjectList.size();

                        int offset = 0;
                        for (TdXMLElement columnElement : columnList) {
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
                        // ~
                    } else if (indicator instanceof UniqueCountIndicator
                            && analysis.getResults().getIndicToRowMap().get(indicator).getData() != null) {
                        List<Object[]> removeValueObjectList = analysis.getResults().getIndicToRowMap().get(indicator).getData();
                        // List<TdColumn> columnElementList =
                        // TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(indicator
                        // .getAnalyzedElement().eContainer()));
                        int offsetting = columnList.indexOf(indicator.getAnalyzedElement());
                        for (Object[] dataObject : removeValueObjectList) {
                            if (dataObject[offsetting].equals(object)) {
                                removeValueObjectList.remove(dataObject);
                                break;
                            }
                        }

                    }
                    // 12919~
                }
            }
        }

        return returnCode;

    }

    public MdmWebserviceConnection getMdmconn() {
        return mdmconn;
    }

    public void setMdmconn(MdmWebserviceConnection mdmconn) {
        this.mdmconn = mdmconn;
    }

    @Override
    protected ReturnCode checkConnection() {
        if (mdmconn.checkDatabaseConnection().isOk()) {
            return new ReturnCode(true);
        }
        return super.checkConnection();
    }

    public TdXMLDocument getTdXmlDocument() {
        return tdXmlDocument;
    }

    public void setTdXmlDocument(TdXMLDocument tdXmlDocument) {
        this.tdXmlDocument = tdXmlDocument;
    }

    @Override
    protected ReturnCode closeConnection() {
        // if (mdmconn != null) {
        // return ConnectionUtils.closeConnection((Connection) mdmconn.);
        // }
        return new ReturnCode(true); //$NON-NLS-1$

    }

}
