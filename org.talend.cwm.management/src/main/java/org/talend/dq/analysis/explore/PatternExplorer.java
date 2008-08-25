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
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.management.api.DbmsLanguage;
import org.talend.cwm.management.api.DbmsLanguageFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * @author scorreia
 * 
 * This class explores the data that matched or did not match a pattern indicator.
 */
public class PatternExplorer implements IDataExplorer {

    private static Logger log = Logger.getLogger(PatternExplorer.class);

    private static final String SELECT = "SELECT * ";

    private Analysis analysis;

    private Indicator indicator;

    private DbmsLanguage dbmsLanguage;

    /**
     * DOC scorreia PatternExplorer constructor comment.
     * 
     * @param dbmsLang
     */
    public PatternExplorer() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getInvalidRowsStatement()
     */
    public String getInvalidRowsStatement() {
        String regexPatternString = IndicatorHelper.getRegexPatternString((PatternMatchingIndicator) this.indicator);
        String regexCmp = dbmsLanguage.regexNotLike(indicator.getAnalyzedElement().getName(), regexPatternString);
        return getRowsStatement(regexCmp);
    }

    private String getRowsStatement(String regexpCmp) {
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getValidRowsStatement()
     */
    public String getValidRowsStatement() throws TalendException {
        if (this.indicator == null || !(this.indicator instanceof PatternMatchingIndicator)) {
            throw new TalendException("No indicator exist in analysis " + analysis.getName());
        }
        String regexPatternString = IndicatorHelper.getRegexPatternString((PatternMatchingIndicator) this.indicator);
        String regexCmp = dbmsLanguage.regexLike(indicator.getAnalyzedElement().getName(), regexPatternString);
        return getRowsStatement(regexCmp);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#setAnalysis(org.talend.dataquality.analysis.Analysis)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#setIndicator(org.talend.dataquality.indicators.Indicator)
     */
    public boolean setIndicator(Indicator indicator) {
        this.indicator = indicator;
        return indicator instanceof PatternMatchingIndicator;
    }

}
