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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.analysis.connpool.TdqAnalysisConnectionPool;
import org.talend.utils.collections.MultiMapHelper;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class ColumnAnalysisSqlParallelExecutor extends ColumnAnalysisSqlExecutor {

    protected Connection connection;

    protected Map<ModelElement, List<Indicator>> elementToIndicator;

    protected Indicator indicator;

    protected boolean ok = true;

    private Exception exception;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    private ColumnAnalysisSqlParallelExecutor() {
        super();
    }

    public static ColumnAnalysisSqlParallelExecutor createInstance(ColumnAnalysisSqlExecutor parent) {
        ColumnAnalysisSqlParallelExecutor inst = new ColumnAnalysisSqlParallelExecutor();
        if (parent != null) {
            inst.errorMessage = parent.errorMessage;
            // MOD scorreia 2009-08-20 line commented out: use protected method dbms() instead
            // inst.dbmsLanguage = parent.dbmsLanguage;
            inst.cachedAnalysis = parent.cachedAnalysis;
            inst.schemata = parent.schemata;
        }
        return inst;
    }

    public static ColumnAnalysisSqlParallelExecutor createInstance(ColumnAnalysisSqlExecutor parent, Connection connection,
            Map<ModelElement, List<Indicator>> elementToIndicator, Indicator indicator) {
        ColumnAnalysisSqlParallelExecutor inst = createInstance(parent);
        if (inst != null) {
            inst.connection = connection;
            inst.elementToIndicator = elementToIndicator;
            inst.indicator = indicator;
        }
        return inst;
    }

    /**
     * run analysis when SqlParallelExecutor.
     */
    public void run() {
        try {
            // skip composite indicators that do not require a sql execution
            if (indicator instanceof CompositeIndicator) {
                // options of composite indicators are handled elsewhere
                return;
            }

            synchronized (schemata) {
                // set the connection's catalog
                String catalogName = getCatalogOrSchemaName(indicator.getAnalyzedElement());
                if (catalogName != null) { // check whether null argument can be given
                    changeCatalog(catalogName, connection);
                }
            }

            Expression query = dbms().getInstantiatedExpression(indicator);
            if (query == null || !executeQuery(indicator, connection, query.getBody())) {
                ok = traceError("Query not executed for indicator: \"" + indicator.getName() + "\" "//$NON-NLS-1$//$NON-NLS-2$
                        + ((query == null) ? "query is null" : "SQL query: " + query.getBody()));//$NON-NLS-1$//$NON-NLS-2$
            } else {
                // set computation done
                indicator.setComputed(true);
            }

            synchronized (elementToIndicator) {
                // add mapping of analyzed elements to their indicators
                MultiMapHelper.addUniqueObjectToListMap(indicator.getAnalyzedElement(), indicator, elementToIndicator);
            }
        } catch (SQLException e) {
            this.setException(e);
            // Added TDQ-8468 20140103 yyin: when got exception, should change the ok status to false.
            ok = false;
        } finally {
            // return the connection after run
            if (POOLED_CONNECTION) {
                TdqAnalysisConnectionPool.returnPooledConnection(cachedAnalysis, connection);
            }
        }
    }
}
