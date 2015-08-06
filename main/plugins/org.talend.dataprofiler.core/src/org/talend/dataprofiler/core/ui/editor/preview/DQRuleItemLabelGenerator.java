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
package org.talend.dataprofiler.core.ui.editor.preview;

import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.DataUtilities;
import org.jfree.data.category.CategoryDataset;
import org.talend.utils.format.StringFormatUtil;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class DQRuleItemLabelGenerator extends StandardCategoryItemLabelGenerator {

    public DQRuleItemLabelGenerator(String labelFormat, NumberFormat formatter) {
        super(labelFormat, formatter);
        // TODO Auto-generated constructor stub
    }

    /**
     * DOC Administrator DQRuleItemLabelGenerator constructor comment.
     * @param labelFormat
     * @param formatter
     */
    public DQRuleItemLabelGenerator(String labelFormat, DateFormat formatter) {
        super(labelFormat, formatter);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Object[] createItemArray(CategoryDataset dataset, int row, int column) {

        Object[] result = new Object[4];
        result[0] = dataset.getRowKey(row).toString();
        result[1] = dataset.getColumnKey(column).toString();
        Number value = dataset.getValue(row, column);
        if (value != null) {
            if (super.getNumberFormat() != null) {
                result[2] = super.getNumberFormat().format(value);
            } else if (super.getDateFormat() != null) {
                result[2] = super.getDateFormat().format(value);
            }
        } else {
            result[2] = "-";
        }
        if (value != null) {
            double total = DataUtilities.calculateColumnTotal(dataset, column);
            double percent = value.doubleValue() / total;
            // MOD qiongli bug 21589,override for just changeing this line.avoid 99.99% to show 100%
            // result[3] = this.percentFormat.format(percent);
            result[3] = StringFormatUtil.format(percent, StringFormatUtil.PERCENT).toString();
        }

        return result;

    }

}
