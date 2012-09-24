// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.preview.model.states.freq;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.TextAnchor;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerDefaultCategoryDataset;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dq.analysis.explore.BenfordLawFrequencyExplorer;
import org.talend.dq.analysis.explore.DataExplorer;
import org.talend.dq.indicators.ext.FrequencyExt;

/**
 * Show the Bar chart with the line chart together. bar: show the user data result, line point: show the benford law
 * standard values (formal value)
 */
public class BenfordLawFrequencyState extends FrequencyTypeStates {

    // 1~9,Numerically, the leading digits have the following distribution in Benford's law, add: 0, invalid with value
    // 0.0
    private double[] formalValues = { 0.301, 0.176, 0.125, 0.097, 0.079, 0.067, 0.058, 0.051, 0.046, 0.0, 0.0 };

    private List<String> dotChartLabels = new ArrayList<String>();

    /**
     * DOC yyin BenfordLawFrequencyState constructor comment.
     * 
     * @param units
     */
    public BenfordLawFrequencyState(List<IndicatorUnit> units) {
        super(units);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getDataExplorer()
     */
    public DataExplorer getDataExplorer() {
        return new BenfordLawFrequencyExplorer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.preview.model.states.freq.FrequencyTypeStates#sortIndicator(org.talend
     * .dq.indicators.ext.FrequencyExt[])
     */
    @Override
    protected void sortIndicator(FrequencyExt[] frequencyExt) {
        ComparatorsFactory.sort(frequencyExt, ComparatorsFactory.BENFORDLAW_FREQUENCY_COMPARATOR_ID);
        // get the sum
        double sum = 0d;
        for (FrequencyExt ext : frequencyExt) {
            sum += ext.getValue();
        }
        // set the values from count to percentage
        for (FrequencyExt ext : frequencyExt) {
            ext.setFrequency(ext.getValue() / sum);
        }
    }

    /**
     * using frequency insteadof value
     */
    @Override
    protected void setValueToDataset(CustomerDefaultCategoryDataset customerdataset, FrequencyExt freqExt, final String keyLabel) {
        customerdataset.addValue(freqExt.getFrequency(), "1", keyLabel); //$NON-NLS-1$
        dotChartLabels.add(keyLabel);
    }

    /**
     * create a bar chart with points(formalValues) on each bar.
     */
    @SuppressWarnings("deprecation")
    @Override
    public JFreeChart getChart() {
        // Clear the dot category label first, so that the new category label will be added into list.
        dotChartLabels.clear();
        CategoryDataset dataset = getDataset();
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        JFreeChart barChart = ChartFactory.createBarChart(null, getTitle(),
                DefaultMessagesImpl.getString("TopChartFactory.Value"), dataset, PlotOrientation.VERTICAL, false, //$NON-NLS-1$
                true, false);
        CategoryPlot barplot = barChart.getCategoryPlot();
        barplot.setRenderer(new BenfordLawLineAndShapeRenderer());
        TopChartFactory.decorateBarChart(barChart);
        // display percentage on top of the bar
        DecimalFormat df = new DecimalFormat("0.0%"); //$NON-NLS-1$
        barplot.getRenderer().setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", df)); //$NON-NLS-1$
        barplot.getRenderer().setBasePositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
        JFreeChart lineChart = ChartFactory.createLineChart(null, getTitle(),
                DefaultMessagesImpl.getString("TopChartFactory.Value"), getLineDataset(dataset.getColumnCount()), //$NON-NLS-1$
                PlotOrientation.VERTICAL, false, false, false);
        CategoryPlot plot = lineChart.getCategoryPlot();
        // show the value on the right axis of the chart(keep the comment)
        // NumberAxis numberaxis = new NumberAxis(DefaultMessagesImpl.getString("TopChartFactory.Value"));
        // plot.setRangeAxis(10, numberaxis);

        NumberAxis vn = (NumberAxis) plot.getRangeAxis();
        vn.setNumberFormatOverride(df);
        // set points format
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setPaint(Color.BLUE);
        renderer.setSeriesShape(1, new Rectangle2D.Double(-1.5, -1.5, 3, 3));
        renderer.setShapesVisible(true); // show the point shape
        renderer.setBaseLinesVisible(false);// do not show the line

        // add the bar chart into the line chart
        plot.setDataset(1, getDataset());
        plot.setRenderer(1, barplot.getRenderer());

        return lineChart;
    }

    /**
     * 
     * created by mzhao on 2012-9-21 The customer render to paint bar color.
     * 
     */
    private static class BenfordLawLineAndShapeRenderer extends BarRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Paint getItemPaint(final int row, final int column) {
            return (column > 8) ? Color.RED : new Color(193, 216, 047);
        }
    }

    /**
     * get the dataset of standard points.
     * 
     * @param j
     * 
     * @return
     */
    private CategoryDataset getLineDataset(int points) {
        CustomerDefaultCategoryDataset linedataset = new CustomerDefaultCategoryDataset();
        for (int i = 0; i < dotChartLabels.size(); i++) {
            linedataset.addValue(formalValues[i], "threshold", dotChartLabels.get(i)); //$NON-NLS-1$
        }
        return linedataset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.freq.FrequencyTypeStates#getTitle()
     */
    @Override
    protected String getTitle() {
        return DefaultMessagesImpl.getString("FrequencyTypeStates.BenfordLawFrequencyStatistics"); //$NON-NLS-1$
    }

    /**
     * make it return true to show the frequency in the table. because this indicator has the frequency, no matter with
     * row count or not
     */
    @Override
    protected boolean isWithRowCountIndicator() {
        return true;
    }

}
