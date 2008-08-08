// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.indicators.ConnectionEvaluator;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class ConnectionAnalysisExecutor extends AnalysisExecutor {

    private static Logger log = Logger.getLogger(ConnectionAnalysisExecutor.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)
     */
    @Override
    protected String createSqlStatement(Analysis analysis) {
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#runAnalysis(org.talend.dataquality.analysis.Analysis,
     * java.lang.String)
     */
    @Override
    protected boolean runAnalysis(Analysis analysis, String sqlStatement) {

        ConnectionEvaluator eval = new ConnectionEvaluator();
        // // --- add indicators
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            ModelElement analyzedElement = indicator.getAnalyzedElement();
            if (analyzedElement == null) {
                continue;
            }
            TdDataProvider dataProvider = SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(analyzedElement);
            if (dataProvider == null) {
                continue;
            }
            eval.storeIndicator(dataProvider, indicator);
            // ADDED rli 2008-07-10 fixed for the SchemaIndicator will increased after connection analysis running.
            indicator.reset();
            //
            // TdCatalog cat = SwitchHelpers.CATALOG_SWITCH.doSwitch(indicator.getAnalyzedElement());
            // if (cat == null) {
            // continue;
        }
        //
        // }

        // open a connection
        TypedReturnCode<Connection> connection = getConnection(analysis);
        if (!connection.isOk()) {
            log.error(connection.getMessage());
            this.errorMessage = connection.getMessage();
            return false;
        }

        // set it into the evaluator
        eval.setConnection(connection.getObject());
        // when to close connection
        boolean closeAtTheEnd = true;
        ReturnCode rc = eval.evaluateIndicators(sqlStatement, closeAtTheEnd);
        if (!rc.isOk()) {
            log.warn(rc.getMessage());
            this.errorMessage = rc.getMessage();
        }
        return rc.isOk();
    }
}
