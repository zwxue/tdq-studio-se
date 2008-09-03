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

import org.talend.cwm.exception.TalendException;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.PatternMatchingIndicator;

/**
 * @author scorreia
 * 
 * This class explores the data that matched or did not match a pattern indicator.
 */
public class PatternExplorer extends DataExplorer {

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

}
