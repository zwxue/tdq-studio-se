// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.composite.chart;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.dataprofiler.common.ui.editor.preview.chart.TopChartFactory;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.DuplicateStatisticsRow;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;

/**
 * created by zhao on Aug 19, 2013 Detailled comment
 * 
 */
public class DuplicateRecordPieChart {

    private Composite parent = null;

    private ChartComposite chartComposite = null;

    /**
     * DOC zhao DuplicateRecordPieChart constructor comment.
     * 
     * @param parent
     * @param style
     */
    public DuplicateRecordPieChart(Composite parent) {
        this.parent = parent;
    }

    /**
     * Getter for chartComposite.
     * 
     * @return the chartComposite
     */
    public ChartComposite getChartComposite() {
        return this.chartComposite;
    }

    public void createPieChart(List<DuplicateStatisticsRow> dupStats) {
        JFreeChart pieChart = TopChartFactory.createDuplicateRecordPieChart(
                DefaultMessagesImpl.getString("DuplicateRecordPieChart.PieChart.Title"), //$NON-NLS-1$
                getPieDataset(dupStats), true, true, false);
        chartComposite = new ChartComposite(parent, SWT.NONE, pieChart, true);
        GridData gd = new GridData();
        gd.widthHint = CHART_STANDARD_WIDHT;
        gd.heightHint = CHART_STANDARD_HEIGHT;
        chartComposite.setLayoutData(gd);
    }

    private PieDataset getPieDataset(List<DuplicateStatisticsRow> dupStats) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (DuplicateStatisticsRow dupStatRow : dupStats) {
            if (!dupStatRow.getIsRowCount()) {
                String label = dupStatRow.getLabel();
                dataset.setValue(label, dupStatRow.getCount());
            }
        }
        return dataset;
    }

    public static final int CHART_STANDARD_WIDHT = 600;

    public static final int CHART_STANDARD_HEIGHT = 275;
}
