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
package org.talend.dq.analysis.explore;

import org.apache.log4j.Logger;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author scorreia
 * 
 * Abstract class to be used by data explorer subclasses.
 */
public abstract class DataExplorer implements IDataExplorer {

    protected static final String VIEW_ROWS = "View rows";

    private static Logger log = Logger.getLogger(PatternExplorer.class);

    private static final String SELECT = "SELECT * ";

    private static final String VALUE_SELECT = "SELECT ";

    protected Analysis analysis;

    protected Indicator indicator;

    protected DbmsLanguage dbmsLanguage;

    protected IndicatorEnum indicatorEnum;

    /**
     * Column name with quotes.
     */
    protected String columnName;

    protected ChartDataEntity entity;

    /**
     * DOC scorreia DataExplorer constructor comment.
     */
    public DataExplorer() {
        super();
    }

    /**
     * Method "getRowsStatement".
     * 
     * @param whereClause the WHERE clause of the statement
     * @return the full SELECT statement with the WHERE clause
     */
    protected String getRowsStatement(String whereClause) {
        String fromClause = getFromClause();
        if (whereClause != null) {
            String where = fromClause.contains(dbmsLanguage.where()) ? dbmsLanguage.and() : dbmsLanguage.where();
            return SELECT + fromClause + where + whereClause;
        } else {
            return SELECT + fromClause;
        }
    }

    protected String getRowsStatement() {
        return getRowsStatement(null);
    }

    protected String getValuesStatement(String columnName) {
        String fromClause = getFromClause();

        return VALUE_SELECT + columnName + fromClause;
    }

    /**
     * DOC zqin Comment method "getFromClause".
     * 
     * @return
     */
    private String getFromClause() {
        String lang = dbmsLanguage.getDbmsName();
        Expression instantiatedExpression = this.indicator.getInstantiatedExpressions(lang);
        String instantiatedSQL = instantiatedExpression.getBody();
        if (instantiatedSQL == null) {
            log.error("No instantiated SQL expression found for " + indicator.getName() + " in analysis " + analysis.getName());
            return null;
        }
        int b = instantiatedSQL.indexOf(this.dbmsLanguage.from());
        String fromClause = instantiatedSQL.substring(b);
        return fromClause;
    }

    public boolean setAnalysis(Analysis analysis) {
        this.analysis = analysis;
        AnalysisContext context = this.analysis.getContext();
        if (context == null) {
            log.error("Context of analysis " + analysis.getName() + " is null.");
            return false;
        }
        DataManager dataManager = context.getConnection();
        if (dataManager == null) {
            log.error("No connection found in context of analysis " + analysis.getName());
            return false;
        }
        this.dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dataManager);
        return true;
    }

    public void setEnitty(ChartDataEntity entity) {
        this.entity = entity;
        this.indicator = entity.getIndicator();
        this.indicatorEnum = IndicatorEnum.findIndicatorEnum(indicator.eClass());
        this.columnName = dbmsLanguage.quote(indicator.getAnalyzedElement().getName());
    }

    protected String getFullyQualifiedTableName(Column column) {
        final ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(column);
        return dbmsLanguage.toQualifiedName(null, dbmsLanguage.quote(ColumnSetHelper.getParentCatalogOrSchema(columnSetOwner)
                .getName()), dbmsLanguage.quote(columnSetOwner.getName()));
    }
}
