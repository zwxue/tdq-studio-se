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
import org.talend.cwm.management.api.DbmsLanguage;
import org.talend.cwm.management.api.DbmsLanguageFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * @author scorreia
 * 
 * Abstract class to be used by data explorer subclasses.
 */
public abstract class DataExplorer implements IDataExplorer {

    private static Logger log = Logger.getLogger(PatternExplorer.class);

    private static final String SELECT = "SELECT * ";

    protected Analysis analysis;

    protected Indicator indicator;

    protected DbmsLanguage dbmsLanguage;

    /**
     * DOC scorreia DataExplorer constructor comment.
     */
    public DataExplorer() {
        super();
    }

    protected String getRowsStatement(String regexpCmp) {
        String lang = dbmsLanguage.getDbmsName();
        Expression instantiatedExpression = this.indicator.getInstantiatedExpressions(lang);
        String instantiatedSQL = instantiatedExpression.getBody();
        if (instantiatedSQL == null) {
            log.error("No instantiated SQL expression found for " + indicator.getName() + " in analysis " + analysis.getName());
            return null;
        }
        int b = instantiatedSQL.indexOf(this.dbmsLanguage.from());
        String fromClause = instantiatedSQL.substring(b);
        String where = fromClause.contains(dbmsLanguage.where()) ? dbmsLanguage.and() : dbmsLanguage.where();
        return SELECT + fromClause + where + regexpCmp;
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

    public boolean setIndicator(Indicator indicator) {
        this.indicator = indicator;
        return indicator instanceof PatternMatchingIndicator;
    }

}
