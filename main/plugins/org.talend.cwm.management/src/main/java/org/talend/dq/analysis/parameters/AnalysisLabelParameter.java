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
package org.talend.dq.analysis.parameters;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class AnalysisLabelParameter extends AnalysisParameter {

    public static final String NUMBERIC_CORRELATION = "Numerical Correlation Analysis"; //$NON-NLS-1$

    public static final String DATE_CORRELATION = "Time Correlation Analysis"; //$NON-NLS-1$

    public static final String NOMINAL_CORRELATION = "Nominal Correlation Analysis"; //$NON-NLS-1$

    protected String categoryLabel;

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    public boolean isNumbericCorrelation() {
        return NUMBERIC_CORRELATION.equals(categoryLabel);
    }

    public boolean isDateCorrelation() {
        return DATE_CORRELATION.equals(categoryLabel);
    }

    public boolean isNominalCorrelation() {
        return NOMINAL_CORRELATION.equals(categoryLabel);
    }
}
