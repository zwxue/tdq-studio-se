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
package org.talend.dataprofiler.core.ui.editor.preview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ui.editor.preview.ChartDatasetFactory.DateValueAggregate;
import org.talend.dataprofiler.core.ui.editor.preview.ChartDatasetFactory.ValueAggregator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;

/**
 * 
 * DOC xZhao class global comment. Detailled comment
 */
public class HideSeriesPanel extends JPanel implements ActionListener {

    private ColumnSetMultiValueIndicator countIndicator;

    private TdColumn tdColumn;

    private XYItemRenderer xyRenderer;

    private CategoryItemRenderer ganttRenderer;

    public void actionPerformed(ActionEvent actionevent) {
        Iterator<String> iterator = null;
        if (countIndicator instanceof CountAvgNullIndicator) {
            Map<String, ValueAggregator> createXYZDatasets = ChartDatasetFactory.createXYZDatasets(countIndicator, tdColumn);
            iterator = createXYZDatasets.keySet().iterator();
            int byte0 = -1;
            int i = 1;
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if (actionevent.getActionCommand().equals(key)) {
                    byte0 = i - 1;
                }
                i++;
            }
            if (byte0 >= 0) {
                boolean flag = xyRenderer.getItemVisible(byte0, 0);
                xyRenderer.setSeriesVisible(byte0, new Boolean(!flag));
            }
        } else {
            Map<String, DateValueAggregate> createGanttDatasets = ChartDatasetFactory.createGanttDatasets(countIndicator,
                    tdColumn);
            iterator = createGanttDatasets.keySet().iterator();
            int byte0 = -1;
            int i = 1;
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if (actionevent.getActionCommand().equals(key)) {
                    byte0 = i - 1;
                }
                i++;
            }
            if (byte0 >= 0) {
                boolean flag = ganttRenderer.getItemVisible(byte0, 0);
                ganttRenderer.setSeriesVisible(byte0, new Boolean(!flag));
            }
        }

    }

    public HideSeriesPanel(ColumnSetMultiValueIndicator columnMultiIndicator, TdColumn columnPara) {
        super(new BorderLayout());
        this.countIndicator = columnMultiIndicator;
        this.tdColumn = columnPara;
        JFreeChart chart = null;
        Iterator<String> iterator;
        if (columnMultiIndicator instanceof CountAvgNullIndicator) {
            Map<String, ValueAggregator> createXYZDatasets = ChartDatasetFactory
                    .createXYZDatasets(columnMultiIndicator, tdColumn);

            iterator = createXYZDatasets.keySet().iterator();
            chart = TopChartFactory.createBubbleChart(columnMultiIndicator, columnPara);
            XYPlot plot = chart.getXYPlot();
            xyRenderer = plot.getRenderer();
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            int minYValue = (int) getMinYValue(plot, true);
            rangeAxis.setAutoRange(false);
            rangeAxis.setRange(0, minYValue * 100);
            rangeAxis.setTickUnit(new NumberTickUnit(minYValue * 5));
        } else {
            Map<String, DateValueAggregate> createGanttDatasets = ChartDatasetFactory.createGanttDatasets(columnMultiIndicator,
                    tdColumn);
            iterator = createGanttDatasets.keySet().iterator();
            chart = TopChartFactory.createGanttChart(columnMultiIndicator, columnPara);
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setRenderer(new HideSeriesGanttRenderer());
            plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(10.0f);
            ganttRenderer = plot.getRenderer();
            // ganttenderer.setDrawBarOutline(false);
        }

        ChartPanel chartpanel = new ChartPanel(chart);
        chartpanel.setPreferredSize(new Dimension(1000, 500));
        JPanel jpanel = new JPanel();
        while (iterator.hasNext()) {
            String next = iterator.next();
            JCheckBox jcheckbox = new JCheckBox(next);
            jcheckbox.setActionCommand(next);
            jcheckbox.addActionListener(this);
            jcheckbox.setSelected(true);
            jpanel.add(jcheckbox);
        }
        add(chartpanel);
        add(jpanel, "South");
    }

    private double getMinYValue(XYPlot plot, boolean isY) {
        double minValue = 0d;
        int seriesCount = plot.getDataset().getSeriesCount();
        List<Double> yList = new ArrayList<Double>();
        for (int i = 0; i < seriesCount; i++) {
            for (int j = 0; j < plot.getDataset().getItemCount(i); j++) {
                double yValue = plot.getDataset().getYValue(i, j);
                yList.add(yValue);
            }
        }
        minValue = yList.get(0);
        for (double d : yList) {
            if (d > 0 && minValue > d) {
                minValue = d;
            }
        }
        if (minValue == 0) {
            minValue = 1d;
        }
        return minValue;
    }
}
