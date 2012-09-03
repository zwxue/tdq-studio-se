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
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
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

    /**
     * DOC yyin BenfordLawFrequencyState constructor comment.
     * 
     * @param units
     */
    public BenfordLawFrequencyState(List<IndicatorUnit> units) {
        super(units);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getDataExplorer()
     */
    public DataExplorer getDataExplorer() {
        return new BenfordLawFrequencyExplorer();
    }


    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.freq.FrequencyTypeStates#sortIndicator(org.talend.dq.indicators.ext.FrequencyExt[])
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
    protected void setValueToDataset(CustomerDefaultCategoryDataset customerdataset, FrequencyExt freqExt, String keyLabel) {
        customerdataset.addValue(freqExt.getFrequency(), "", keyLabel); //$NON-NLS-1$
    }

    /**
     * create a bar chart with points(formalValues) on each bar.
     */
    @SuppressWarnings("deprecation")
    @Override
    public JFreeChart getChart() {
        CategoryDataset dataset = getDataset();

        JFreeChart barChart = TopChartFactory.createBarChart(getTitle(), dataset, false); //$NON-NLS-1$
        JFreeChart lineChart = ChartFactory.createLineChart(null, getTitle(),
                DefaultMessagesImpl.getString("TopChartFactory.Value"), getLineDataset(dataset.getColumnCount()),
 PlotOrientation.VERTICAL, false, false,
                false);
        CategoryPlot plot = lineChart.getCategoryPlot();
        // show the value on the right axis of the chart(keep the comment)
        // NumberAxis numberaxis = new NumberAxis(DefaultMessagesImpl.getString("TopChartFactory.Value"));
        // plot.setRangeAxis(10, numberaxis);

        NumberAxis vn = (NumberAxis) plot.getRangeAxis();
        // display the percentage on Y axis
        DecimalFormat df = new DecimalFormat("0.0%");
        vn.setNumberFormatOverride(df);
        // set points format
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setPaint(Color.BLUE);
        renderer.setShape(new Rectangle2D.Double(-1.5, -1.5, 3, 3));
        renderer.setShapesVisible(true); // show the point shape
        renderer.setBaseLinesVisible(false);// do not show the line
        // show the point's value on the line(keep the comment)
        // renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        // renderer.setItemLabelsVisible(false);
        renderer.setSeriesPaint(0, Color.BLUE);

        CategoryPlot barplot = barChart.getCategoryPlot();
        // display percentage on top of the bar
        barplot.getRenderer().setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", df));
        // the invalid bar should be RED
        // Paint lastPaint = barplot.getRenderer().getSeriesPaint(9);
        // if (lastPaint != null) {
            barplot.getRenderer().setSeriesPaint(9, Color.RED);
        // }
        // add the bar chart into the line chart
        plot.setDataset(1, getDataset());
        plot.setRenderer(1, barplot.getRenderer());

        return lineChart;
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

        for (int i = 0; i < 9; i++) {
            linedataset.addValue(formalValues[i], "", String.valueOf(i + 1)); //$NON-NLS-1$
        }
        if (points == 10) {
            linedataset.addValue(formalValues[9], "", "0");
        }
        if (points == 11) {
            linedataset.addValue(formalValues[10], "", "invalid");
        }

        return linedataset;
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.freq.FrequencyTypeStates#getTitle()
     */
    @Override
    protected String getTitle() {
        return DefaultMessagesImpl.getString("FrequencyTypeStates.BenfordLawFrequencyStatistics");
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
