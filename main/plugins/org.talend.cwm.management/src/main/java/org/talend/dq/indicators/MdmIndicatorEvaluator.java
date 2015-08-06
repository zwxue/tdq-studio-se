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

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EMap;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.cwm.db.connection.MdmStatement;
import org.talend.cwm.db.connection.MdmWebserviceConnection;
import org.talend.cwm.db.connection.XQueryExpressionUtil;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
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

    protected TdXmlSchema tdXmlDocument;

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
                        log.info("Executing query: " + XQueryExpressionUtil.getExpression()); //$NON-NLS-1$
                    }
                    returnCode.setOk(returnCode.isOk() && statement.execute(tdXmlDocument, XQueryExpressionUtil.getExpression()));
                    List<String> strResultListTemp = Arrays.asList(statement.getResultSet());
                    String totalNum = statement.getXmlNodeValue(strResultListTemp.get(0), "totalCount"); //$NON-NLS-1$
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
            String mess = Messages.getString("IndicatorEvaluator.NoResultSet", sqlStatement); //$NON-NLS-1$
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
        TdXmlElementType parentElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(XmlElementHelper
                .getParentElement(SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(analysisElementList.get(0))));
        // all the column under the parentElement
        List<TdXmlElementType> columnListTemp = org.talend.cwm.db.connection.ConnectionUtils.getXMLElements(parentElement);
        // MOD qiongli 2012-6-21 TDQ-5139
        List<TdXmlElementType> columnList = new ArrayList<TdXmlElementType>();
        for (TdXmlElementType tdXmlEle : columnListTemp) {
            if (!DqRepositoryViewService.hasChildren(tdXmlEle)) {
                columnList.add(tdXmlEle);
            }
        }

        List<Map<String, String>> resultSetList = new ArrayList<Map<String, String>>();
        if (analysis.getParameters().isStoreData()) {
            resultSetList = statement.tidyResultSet(columnList.toArray(new ModelElement[columnList.size()]), resultSet);
        } else {
            resultSetList = statement.tidyResultSet(analysisElementList.toArray(new ModelElement[analysisElementList.size()]),
                    resultSet);
        }
        String splitStr = "_//_"; //$NON-NLS-1$
        label: for (Map<String, String> rowMap : resultSetList) {
            for (int i = 0; i < analysisElementList.size(); i++) {
                String col = analysisElementList.get(i).getName();// the name of column
                List<Indicator> indicators = getIndicators(col);
                // String object = rowMap.get(col);// the value of column
                // TDQ-5139 the node maybe have some sub-nodes
                String xmlValues = rowMap.get(col);
                if (xmlValues == null) {
                    continue;
                }
                String[] split = xmlValues.split(splitStr);
                for (int k = 0; k < split.length; k++) {
                    String object = split[k];
                    // --- give row to handle to indicators
                    for (Indicator indicator : indicators) {
                        if (!continueRun()) {
                            break label;
                        }

                        // MOD msjian TDQ-5695 2012-12-7: make the duplicate count value correct
                        if (indicator instanceof DuplicateCountIndicator) {
                            ((DuplicateCountIndicator) indicator).handle(object, split);
                        } else {
                            indicator.handle(object);
                        }
                        // TDQ-5695~

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
                            for (TdXmlElementType columnElement : columnList) {
                                Object newobject = rowMap.get(columnElement.getName());
                                if (split.length > 1) {
                                    newobject = object;
                                }
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
                            List<Object[]> removeValueObjectList = analysis.getResults().getIndicToRowMap().get(indicator)
                                    .getData();
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

    public TdXmlSchema getTdXmlDocument() {
        return tdXmlDocument;
    }

    public void setTdXmlDocument(TdXmlSchema tdXmlDocument) {
        this.tdXmlDocument = tdXmlDocument;
    }

    @Override
    protected ReturnCode closeConnection() {
        // if (mdmconn != null) {
        // return ConnectionUtils.closeConnection((Connection) mdmconn.);
        // }
        return new ReturnCode(true);

    }

}
