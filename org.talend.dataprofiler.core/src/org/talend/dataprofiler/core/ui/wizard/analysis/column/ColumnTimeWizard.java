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

import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author zqin
 * 
 */
public class ColumnTimeWizard extends ColumnWizard {

    public ColumnTimeWizard(AnalysisParameter parameter) {
        super(parameter);
    }

    @Override
    public ModelElement initCWMResourceBuilder() {
        Analysis analysis = (Analysis) super.initCWMResourceBuilder();

        ColumnsetFactory columnsetFactory = ColumnsetFactory.eINSTANCE;
        ColumnSetMultiValueIndicator minMaxDateIndicator = columnsetFactory.createMinMaxDateIndicator();

        analysis.getResults().getIndicators().add(minMaxDateIndicator);

        return analysis;
    }
}
