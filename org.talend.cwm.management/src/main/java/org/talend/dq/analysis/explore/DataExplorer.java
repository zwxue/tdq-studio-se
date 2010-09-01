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
package org.talend.dq.analysis.explore;

import org.apache.log4j.Logger;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * @author scorreia
 * 
 * Abstract class to be used by data explorer subclasses.
 */
public abstract class DataExplorer implements IDataExplorer {

    private static Logger log = Logger.getLogger(PatternExplorer.class);

    public static final String MENU_VIEW_VALUES = Messages.getString("DataExplorer.ViewValues"); //$NON-NLS-1$

    public static final String MENU_VIEW_ROWS = Messages.getString("DataExplorer.ViewRows"); //$NON-NLS-1$

    public static final String MENU_VIEW_INVALID_ROWS = Messages.getString("DataExplorer.ViewInvalidRows"); //$NON-NLS-1$

    public static final String MENU_VIEW_VALID_ROWS = Messages.getString("DataExplorer.ViewValidRows"); //$NON-NLS-1$

    public static final String MENU_VIEW_VALID_VALUES = Messages.getString("DataExplorer.ViewValidValues"); //$NON-NLS-1$

    public static final String MENU_VIEW_DETAILED_VALID_VALUES = Messages.getString("DataExplorer.ViewDetailedValidValues"); //$NON-NLS-1$

    public static final String MENU_VIEW_DETAILED_INVALID_VALUES = Messages.getString("DataExplorer.ViewDetailedInvalidValues"); //$NON-NLS-1$

    public static final String MENU_VIEW_INVALID_VALUES = Messages.getString("DataExplorer.ViewInvalidValues"); //$NON-NLS-1$

    public static final String MENU_VIEW_MATCH_ROWS = Messages.getString("DataExplorer.ViewMatchRows"); //$NON-NLS-1$

    public static final String MENU_VIEW_NOT_MATCH_ROWS = Messages.getString("DataExplorer.ViewNotMatchRows"); //$NON-NLS-1$

    public static final String MENU_ROWS_IN_RANGE = Messages.getString("DataExplorer.RowsInRange"); //$NON-NLS-1$

    public static final String MENU_ROWS_OUTSIDE_RANGE = Messages.getString("DataExplorer.RowsOutsideRange"); //$NON-NLS-1$

    protected static final String SELECT_ALL = "SELECT * "; //$NON-NLS-1$

    private static final String SELECT = "SELECT "; //$NON-NLS-1$

    private static final String SELECT_DISTINCT = "SELECT DISTINCT "; //$NON-NLS-1$

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
            return SELECT_ALL + fromClause + where + whereClause;
        } else {
            return SELECT_ALL + fromClause;
        }
    }

    /**
     * Method "getRowsStatement".
     * 
     * @param whereClause the WHERE clause of the statement
     * @param colName the name of select column
     * @return the SELECT statement(only select column) with the WHERE clause
     */
    protected String getValuesStatement(String colName, String whereClause) {
        String fromClause = getFromClause();
        if (whereClause != null) {
            String where = fromClause.contains(dbmsLanguage.where()) ? dbmsLanguage.and() : dbmsLanguage.where();
            return SELECT + columnName + fromClause + where + whereClause;
        } else {
            return SELECT + columnName + fromClause;
        }
    }

    protected String getRowsStatement() {
        return getRowsStatement(null);
    }

    protected String getValuesStatement(String columnName) {
        String fromClause = getFromClause();

        return SELECT + columnName + fromClause;
    }

    /**
     * DOC zqin Comment method "getFromClause".
     * 
     * @return
     */
    protected String getFromClause() {
        Expression instantiatedExpression = dbmsLanguage.getInstantiatedExpression(indicator);
        String instantiatedSQL = instantiatedExpression == null ? null : instantiatedExpression.getBody();
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

    /**
     * DOC bZhou Comment method "getFullyQualifiedTableName".
     * 
     * @param column
     * @return
     */
    protected String getFullyQualifiedTableName(TdColumn column) {
        final ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(column);
        return getFullyQualifiedTableName(columnSetOwner);
    }

    /**
     * DOC bZhou Comment method "getFullyQualifiedTableName".
     * 
     * @param set
     * @return
     */
    protected String getFullyQualifiedTableName(ColumnSet set) {
        Schema parentSchema = SchemaHelper.getParentSchema(set);
        Catalog parentCatalog = CatalogHelper.getParentCatalog(set);
        if (parentSchema != null) {
            parentCatalog = CatalogHelper.getParentCatalog(parentSchema);
        }

        String schemaName = parentSchema == null ? null : parentSchema.getName();
        String catalogName = parentCatalog == null ? null : parentCatalog.getName();
        // MOD by zshen: change schemaName of sybase database to Table's owner.
        if (ConnectionUtils.isSybaseeDBProducts(dbmsLanguage.getDbmsName())) {
            schemaName = ColumnSetHelper.getTableOwner(set);
        }
        // ~11934
        return dbmsLanguage.toQualifiedName(catalogName, schemaName, set.getName());
    }

    /**
     * DOC scorreia Comment method "getRowsStatementWithSubQuery".
     * 
     * @return
     */
    protected String getRowsStatementWithSubQuery() {
        String fromClause = getFromClause();
        TdColumn column = (TdColumn) indicator.getAnalyzedElement();
        String table = getFullyQualifiedTableName(column);
        return " SELECT * FROM " + table + dbmsLanguage.where() + columnName + dbmsLanguage.in() //$NON-NLS-1$
                + inBrackets("SELECT " + columnName + fromClause); //$NON-NLS-1$
    }

    protected String getDistinctValuesStatement(String columnName) {
        String fromClause = getFromClause();

        return SELECT_DISTINCT + columnName + fromClause;
    }

    /**
     * Method "getDataFilterClause".
     * 
     * @return the data filter string which represents a where clause (without the where keyword)
     */
    protected String getDataFilterClause() {
        String where = AnalysisHelper.getStringDataFilter(analysis);
        return where != null ? where : ""; //$NON-NLS-1$
    }

    /**
     * Method "andDataFilterClause".
     * 
     * @return the data filter string to add to a where clause. The returned string starts with AND if a clause exists.
     * Otherwise it is empty.
     */
    protected String andDataFilterClause() {
        String dataFilter = getDataFilterClause();
        if (dataFilter.length() == 0) {
            return ""; //$NON-NLS-1$
        }
        return dbmsLanguage.and() + inBrackets(dataFilter);
    }

    /**
     * Method "inBrackets".
     * 
     * @param clause
     * @return the given clause surrounded by parenthesis.
     */
    public String inBrackets(String clause) {
        return " (" + clause + ") "; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * DOC xqliu Comment method "isXml".
     * 
     * @return
     */
    public boolean isXml() {
        if (this.indicator != null) {
            return this.indicator.getAnalyzedElement() instanceof TdXmlElementType;
        }
        return false;
    }

}
