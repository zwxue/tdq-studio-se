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

package org.talend.dataprofiler.core.ui;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.talend.cwm.exception.TalendException;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class BubbleChartMain {

    protected static Logger log = Logger.getLogger(BubbleChartMain.class);

    /**
     * DOC scorreia Comment method "main".
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            MultiColAnalysisCreationMain myTest = new MultiColAnalysisCreationMain();
            final ColumnSetMultiValueIndicator indic = myTest.run();
            JFreeChart chart = TopChartFactory.createBubbleChart(indic, indic.getNumericColumns().get(0));

            ChartFrame frame = new ChartFrame("TEST", chart);
            frame.pack();
            frame.setVisible(true);
        } catch (TalendException e) {
            log.error(e, e);
        }

    }

}
