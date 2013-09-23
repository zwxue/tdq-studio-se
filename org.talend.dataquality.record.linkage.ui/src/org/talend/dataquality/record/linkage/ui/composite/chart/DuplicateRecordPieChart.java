// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.DuplicateStatisticsRow;

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
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        JFreeChart pieChart = ChartFactory.createPieChart("Duplicate Record Statistics", getPieDataset(dupStats), true, true,
                false);
        decoratePiePlot(pieChart);
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

    /**
     * 
     * 
     * @param chart
     */
    private static void decoratePiePlot(JFreeChart chart) {

        Font font = new Font("sans-serif", Font.BOLD, BASE_TITLE_LABEL_SIZE);//$NON-NLS-1$
        TextTitle textTitle = chart.getTitle();
        if (textTitle != null) {
            textTitle.setFont(font);
        }
        font = new Font("Tahoma", Font.PLAIN, BASE_ITEM_LABEL_SIZE);//$NON-NLS-1$
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(font);
        }
        PiePlot plot = (PiePlot) chart.getPlot();
        font = new Font("Monospaced", Font.PLAIN, 10);//$NON-NLS-1$
        plot.setLabelFont(font);
        plot.setNoDataMessage("No data available"); //$NON-NLS-1$
        StandardPieSectionLabelGenerator standardPieSectionLabelGenerator = new StandardPieSectionLabelGenerator(("{0}:{2}"),//$NON-NLS-1$
                NumberFormat.getNumberInstance(), new DecimalFormat("0.00%")); //$NON-NLS-1$
        plot.setLabelGenerator(standardPieSectionLabelGenerator);
        plot.setLabelLinkPaint(Color.GRAY);
        plot.setLabelOutlinePaint(Color.WHITE);
        plot.setLabelGap(0.02D);
        plot.setOutlineVisible(false);
        plot.setMaximumLabelWidth(0.2D);
        plot.setCircular(false);
        // remove the shadow of the pie chart
        plot.setShadowXOffset(0);
        plot.setShadowYOffset(0);
    }

    private static final int BASE_ITEM_LABEL_SIZE = 10;

    private static final int BASE_TITLE_LABEL_SIZE = 14;

    public static final int CHART_STANDARD_WIDHT = 600;

    public static final int CHART_STANDARD_HEIGHT = 275;
}
