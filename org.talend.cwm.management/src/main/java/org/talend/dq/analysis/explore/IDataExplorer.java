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
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;

/**
 * @author scorreia
 * 
 * Interface for exploring data from a specific indicator.
 */
public interface IDataExplorer {

    /**
     * Method "setAnalysis".
     * 
     * @param analysis the analysis from which the exploration must be done.
     * @return false if the given analysis cannot be explored
     */
    public boolean setAnalysis(Analysis analysis);

    /**
     * Method "setIndicator".
     * 
     * @param indicator the indicator from which the exploration must be done.
     * @return false if the given indicator cannot be explored
     */
    public boolean setIndicator(Indicator indicator);

    /**
     * Method "getInvalidRowsStatement".
     * 
     * @return a SQL statement which will return the invalid rows.
     */
    public String getInvalidRowsStatement();

    /**
     * Method "getValidRowsStatement".
     * 
     * @return a SQL statement which will return the valid rows.
     * @throws TalendException
     */
    public String getValidRowsStatement() throws TalendException;
}
