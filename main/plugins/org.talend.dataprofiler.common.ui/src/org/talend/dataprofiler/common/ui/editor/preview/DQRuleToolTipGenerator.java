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
package org.talend.dataprofiler.common.ui.editor.preview;

import java.text.NumberFormat;

import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC xqliu class global comment. ADD xqliu 2010-03-11 feature 10834
 */
public class DQRuleToolTipGenerator extends StandardCategoryToolTipGenerator {

    private static final long serialVersionUID = -1596365023643143834L;

    public DQRuleToolTipGenerator(String labelFormat, NumberFormat formatter) {
        super(labelFormat, formatter);
    }

    @Override
    public String generateToolTip(CategoryDataset dataset, int row, int column) {
        String result = generateLabelString(dataset, row, column);
        if (dataset instanceof CustomerDefaultCategoryDataset) {
            CustomerDefaultCategoryDataset customerDataset = (CustomerDefaultCategoryDataset) dataset;
            ChartDataEntity[] dataEntities = customerDataset.getDataEntities();
            String temp = dataEntities[column].getRangeAsString();
            if (temp != null && !"".equals(temp.trim())) {//$NON-NLS-1$ 
                result = temp + "\n" + result;//$NON-NLS-1$ 
            }
        }
        return result;
    }
}
