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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.TextAnchor;

/**
 * created by zshen on Aug 7, 2013 Detailled comment
 * 
 */
public class BlockingKeyDataChart extends Composite {

    private static final int BASE_ITEM_LABEL_SIZE = 12;

    private static final int BASE_LABEL_SIZE = 12;

    private static final int BASE_TICK_LABEL_SIZE = 10;

    private static final int BASE_LEGEND_LABEL_SIZE = 10;

    private static final int BASE_TITLE_LABEL_SIZE = 14;

    private ChartComposite jfreeChartComp = null;

    private Map<String, List<String[]>> prviewData = null;

    /**
     * number of bins in the chart.
     */
    private static final int BINS = 50;

    /**
     * DOC zshen BlockingKeyDataChart constructor comment.
     * 
     * @param parent
     * @param style
     */
    public BlockingKeyDataChart(Composite parent, Map<String, List<String[]>> viewData) {
        super(parent, SWT.NONE);
        this.prviewData = viewData;
        this.setLayout(new FillLayout());
        // make the size of the chart full fill the area
        GridData data = new GridData(GridData.FILL_BOTH);
        this.setLayoutData(data);
        initChartData(viewData);
    }

    public static JFreeChart createBarChart(String titile, HistogramDataset dataset, boolean showLegend) {
        JFreeChart chart = ChartFactory.createHistogram(null, titile,
                "Key frequency", dataset, PlotOrientation.VERTICAL, showLegend, //$NON-NLS-1$
                true, false);

        XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setUpperMargin(0.08);
        // plot.getRangeAxis().setLowerBound(-0.08);
        decorateCategoryPlot(chart);
        plot.setRangeGridlinesVisible(true);

        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));

        return chart;
    }

    private static void decorateCategoryPlot(JFreeChart chart) {

        XYPlot plot = chart.getXYPlot();
        XYItemRenderer render = plot.getRenderer();
        ValueAxis domainAxis = plot.getDomainAxis();
        ValueAxis valueAxis = plot.getRangeAxis();

        Font font = new Font("Tahoma", Font.BOLD, BASE_ITEM_LABEL_SIZE); //$NON-NLS-1$

        render.setBaseItemLabelFont(font);
        font = new Font("sans-serif", Font.BOLD, BASE_LABEL_SIZE); //$NON-NLS-1$
        domainAxis.setLabelFont(font);
        font = new Font("sans-serif", Font.BOLD, BASE_LABEL_SIZE); //$NON-NLS-1$
        valueAxis.setLabelFont(font);
        font = new Font("sans-serif", Font.PLAIN, BASE_TICK_LABEL_SIZE); //$NON-NLS-1$
        domainAxis.setTickLabelFont(font);
        valueAxis.setTickLabelFont(font);
        font = new Font("Tahoma", Font.PLAIN, BASE_LEGEND_LABEL_SIZE); //$NON-NLS-1$
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(font);
        }
        font = new Font("sans-serif", Font.BOLD, BASE_TITLE_LABEL_SIZE); //$NON-NLS-1$
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(font);
        }
        font = null;
        if (render instanceof BarRenderer) {
            int rowCount = chart.getCategoryPlot().getDataset().getRowCount();
            domainAxis.setUpperMargin(0.1);
            // domainAxis.setMaximumCategoryLabelLines(10);
            ((BarRenderer) render).setItemMargin(-0.40 * rowCount);
        }
        // set color
        int rowCount = chart.getXYPlot().getDataset().getSeriesCount();
        for (int i = 0; i < rowCount; i++) {
            plot.getRenderer().setSeriesPaint(i, Color.RED);
        }

    }

    public void refresh(Map<String, List<String[]>> viewData) {
        this.prviewData = viewData;
        initChartData(viewData);
        jfreeChartComp.setChart(computeChart());
        jfreeChartComp.forceRedraw();
    }

    protected boolean initChartData(Map<String, List<String[]>> viewData) {
        if (viewData != null && this.jfreeChartComp == null) {
            this.prviewData = viewData;
            createJFreeChartComposite();
            return true;
        }
        return false;
    }

    private JFreeChart computeChart() {
        HistogramDataset defaultcategorydataset = new HistogramDataset();
        fillCategorySet(defaultcategorydataset);
        JFreeChart chart = createBarChart("Number of rows", defaultcategorydataset, //$NON-NLS-1$
                false);
        return chart;

    }

    private void fillCategorySet(HistogramDataset defaultcategorydataset) {

        // MOD scorreia 2011-02-10 code simplified in order to avoid unnecessary aggregation (it is now done in the
        // histogram dataset automatically)
        final double minValue = 0; // lower value of the x-axis of the chart
        double maxValue = 0; // higher value of the x-axis of the chart
        List<Double> blockSizeValueList = new ArrayList<Double>();
        // add each block size (number of rows of the block) to the list
        Iterator<String> iterator = prviewData.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Integer blockSize = prviewData.get(key).size();
            assert blockSize != null : "no row found for block " + key;
            if (blockSize == null) { // should not happen
                blockSize = 0;
            }
            blockSizeValueList.add(blockSize.doubleValue());
            // evaluate the maximum number of rows
            if (maxValue < blockSize) {
                maxValue = blockSize;
            }
        }

        Double[] valueArray = new Double[blockSizeValueList.size()];
        blockSizeValueList.toArray(valueArray);
        defaultcategorydataset.addSeries("Key distribution", ArrayUtils.toPrimitive(valueArray), BINS, minValue, maxValue); //$NON-NLS-1$

    }

    private void createJFreeChartComposite() {

        final ScrolledComposite scrollComp = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_FOCUS);
        scrollComp.setLayout(new GridLayout());

        // GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        // scrollComp.setLayoutData(layoutData);

        Composite innerComp = new Composite(scrollComp, SWT.NONE);
        GridData innerGD = new GridData();
        innerGD.grabExcessVerticalSpace = true;
        // innerGD.minimumHeight = 250;
        innerComp.setLayoutData(innerGD);
        innerComp.setLayout(new FillLayout());
        scrollComp.setContent(innerComp);
        scrollComp.setExpandVertical(true);
        scrollComp.setExpandHorizontal(true);
        jfreeChartComp = new ChartComposite(innerComp, SWT.NONE, computeChart(), true);
        // layoutData.minimumHeight = 250;
        // jfreeChartComp.setLayoutData(layoutData);
    }

}
