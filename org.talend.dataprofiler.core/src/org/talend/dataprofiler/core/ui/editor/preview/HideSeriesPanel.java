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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ui.editor.preview.ChartDatasetFactory.ValueAggregator;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;

/**
 * 
 * DOC xZhao class global comment. Detailled comment
 */
public class HideSeriesPanel extends JPanel implements ActionListener {

    private XYItemRenderer renderer;

    private CountAvgNullIndicator countIndicator;

    private TdColumn tdColumn;

    public void actionPerformed(ActionEvent actionevent) {
        Map<String, ValueAggregator> createXYZDatasets = ChartDatasetFactory.createXYZDatasets(countIndicator, tdColumn);

        Iterator<String> iterator = createXYZDatasets.keySet().iterator();
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
            boolean flag = renderer.getItemVisible(byte0, 0);
            renderer.setSeriesVisible(byte0, new Boolean(!flag));
        }
    }

    public HideSeriesPanel(CountAvgNullIndicator countIndicator, TdColumn columnPara) {
        super(new BorderLayout());
        this.countIndicator = countIndicator;
        this.tdColumn = columnPara;
        Map<String, ValueAggregator> createXYZDatasets = ChartDatasetFactory.createXYZDatasets(countIndicator, tdColumn);

        Iterator<String> iterator = createXYZDatasets.keySet().iterator();
        JFreeChart chart = TopChartFactory.createBubbleChart(countIndicator, columnPara);
        XYPlot plot = chart.getXYPlot();
        renderer = plot.getRenderer();
        // ValueAxis domainAxis = (ValueAxis) plot.getDomainAxis();
        // int minXValue = (int) getMinYValue(plot, false);
        // domainAxis.setAutoRange(false);
        // domainAxis.setRange(0, minXValue * 10);
        // domainAxis.setTickUnit(new NumberTickUnit(minXValue * 5));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        int minYValue = (int) getMinYValue(plot, true);
        rangeAxis.setAutoRange(false);
        rangeAxis.setRange(0, minYValue * 100);
        rangeAxis.setTickUnit(new NumberTickUnit(minYValue * 5));

        ChartPanel chartpanel = new ChartPanel(chart);
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
        double minValue = -1;
        int seriesCount = plot.getDataset().getSeriesCount();
        List<Double> yList = new ArrayList<Double>();
        Double[] yArrayTransfer = new Double[10];
        for (int i = 0; i < yArrayTransfer.length; i++) {
            yArrayTransfer[i] = 0D;
        }
        for (int i = 0; i < seriesCount; i++) {
            if (isY) {
                for (int j = 0; j < plot.getDataset().getItemCount(i); j++) {
                    double yValue = plot.getDataset().getYValue(i, j);
                    yList.add(yValue);
                }
            } else {
                for (int j = 0; j < plot.getDataset().getItemCount(i); j++) {
                    double yValue = plot.getDataset().getXValue(i, j);
                    yList.add(yValue);
                }
            }
        }
        yArrayTransfer = yList.toArray(yArrayTransfer);
        double[] yArray = new double[yArrayTransfer.length];
        for (int k = 0; k < yArrayTransfer.length; k++) {
            if (yArrayTransfer[k] == null) {
                yArrayTransfer[k] = new Double(-1D);
            }
            yArray[k] = yArrayTransfer[k].doubleValue();
        }
        Arrays.sort(yArray);
        for (double d : yArray) {
            if (d > 0) {
                minValue = d;
                break;
            }
        }

        return minValue;
    }
}
