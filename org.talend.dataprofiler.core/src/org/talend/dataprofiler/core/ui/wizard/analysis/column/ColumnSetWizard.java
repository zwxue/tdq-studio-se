// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dq.analysis.parameters.AnalysisParameter;

/**
 * DOC yyi class global comment. Detailled comment
 */
public class ColumnSetWizard extends ColumnWizard {

    public ColumnSetWizard(AnalysisParameter parameter) {
        super(parameter);
    }

    @Override
    protected Indicator getMatchedIndicator() {
        SimpleStatIndicator ssIndicator = ColumnsetFactory.eINSTANCE.createSimpleStatIndicator();

        ssIndicator.setRowCountIndicator(IndicatorsFactory.eINSTANCE.createRowCountIndicator());
        ssIndicator.setDistinctCountIndicator(IndicatorsFactory.eINSTANCE.createDistinctCountIndicator());
        ssIndicator.setDuplicateCountIndicator(IndicatorsFactory.eINSTANCE.createDuplicateCountIndicator());
        ssIndicator.setUniqueCountIndicator(IndicatorsFactory.eINSTANCE.createUniqueCountIndicator());

        return ssIndicator;
    }
}
