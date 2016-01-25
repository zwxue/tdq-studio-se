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
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.dataprofiler.common.ui.editor.preview.chart.TopChartFactory;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;

/**
 * created by zshen on Aug 7, 2013 Detailled comment
 * 
 */
public class BlockingKeyDataChart extends Composite {

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
        JFreeChart chart = TopChartFactory.createBlockingBarChart(null, titile,
                DefaultMessagesImpl.getString("BlockingKeyDataChart.Axis.Label"), dataset, //$NON-NLS-1$
                PlotOrientation.VERTICAL, showLegend, true, false);
        return chart;
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
            assert blockSize != null : "no row found for block " + key; //$NON-NLS-1$
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

        Composite innerComp = new Composite(scrollComp, SWT.NONE);
        GridData innerGD = new GridData();
        innerGD.grabExcessVerticalSpace = true;
        innerComp.setLayoutData(innerGD);
        innerComp.setLayout(new FillLayout());
        scrollComp.setContent(innerComp);
        scrollComp.setExpandVertical(true);
        scrollComp.setExpandHorizontal(true);
        jfreeChartComp = new ChartComposite(innerComp, SWT.NONE, computeChart(), true);
    }

    /**
     * 
     * create a chart with empty dataset,so as to clear the blocking key chart.
     */
    public void clearChart() {
        JFreeChart jfreechart = createBarChart("Number of rows", new HistogramDataset(), false); //$NON-NLS-1$
        jfreeChartComp.setChart(jfreechart);
        jfreeChartComp.forceRedraw();
    }

}
