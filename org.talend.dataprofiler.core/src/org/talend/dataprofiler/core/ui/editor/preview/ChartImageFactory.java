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

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.talend.dataprofiler.core.ui.editor.preview.model.IDataEntity;
import org.talend.dataprofiler.core.ui.utils.ChartUtils;
import org.talend.dq.indicators.preview.EIndicatorChartType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartImageFactory {

    private static Logger log = Logger.getLogger(ChartImageFactory.class);

    private static final int CHART_WIDTH = 460;

    private static final int CHART_HEIGHT = 230;

    private static final int CHART_WIDTH2 = 100;

    private static final int CHART_HEIGHT2 = 380;

    static ImageDescriptor createImage(EIndicatorChartType chartType, IDataEntity dataset) {

        try {
            JFreeChart chart = createChart(chartType, dataset);

            if (chart != null) {
                if (chartType == EIndicatorChartType.SUMMARY_STATISTICS) {
                    return ChartUtils.convertToImage(chart, CHART_WIDTH2, CHART_HEIGHT2);
                }

                return ChartUtils.convertToImage(chart, CHART_WIDTH, CHART_HEIGHT);
            }
        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    static JFreeChart createChart(EIndicatorChartType chartType, IDataEntity dataset) {

        CategoryDataset cDataset = (CategoryDataset) dataset;

        switch (chartType) {
        case FREQUENCE_STATISTICS:
            return createBarChart(chartType.getLiteral(), cDataset);

        case PATTERN_MATCHING:
        case SQL_PATTERN_MATCHING:
            return createStacked3DBarChart(chartType.getLiteral(), cDataset);

        case SUMMARY_STATISTICS:
            if (dataset instanceof BoxAndWhiskerCategoryDataset) {
                BoxAndWhiskerCategoryDataset bDataset = (BoxAndWhiskerCategoryDataset) dataset;
                return createBoxAndWhiskerChart(chartType.getLiteral(), bDataset);
            }

            return create3DBarChart(chartType.getLiteral(), cDataset, true);

        case SIMPLE_STATISTICS:
        case TEXT_STATISTICS:
            return create3DBarChart(chartType.getLiteral(), cDataset, true);
        default:

            return null;
        }
    }

    private static JFreeChart create3DBarChart(String titile, CategoryDataset dataset, boolean showLegend) {

        JFreeChart chart = ChartFactory.createBarChart3D(null, titile, "Value", dataset, PlotOrientation.VERTICAL, showLegend,
                false, true);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinesVisible(true);

        BarRenderer3D renderer3d = (BarRenderer3D) plot.getRenderer();

        renderer3d.setBaseItemLabelsVisible(true);
        renderer3d.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer3d.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer3d.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer3d.setBaseItemLabelFont(new Font("SansSerif", Font.BOLD, 12));
        renderer3d.setItemMargin(0.2);
        plot.setForegroundAlpha(0.50f);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        return chart;
    }

    private static JFreeChart createBarChart(String titile, CategoryDataset dataset) {

        return ChartFactory.createBarChart(null, titile, "Value", dataset, PlotOrientation.HORIZONTAL, false, false, false);
    }

    private static JFreeChart createBoxAndWhiskerChart(String title, BoxAndWhiskerCategoryDataset dataset) {

        return ChartFactory.createBoxAndWhiskerChart(null, null, "value", dataset, false);
    }

    private static JFreeChart createStacked3DBarChart(String titile, CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createStackedBarChart3D(null, null, "Value", dataset, PlotOrientation.VERTICAL, true,
                false, false);

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{3}", NumberFormat.getIntegerInstance(),
                new DecimalFormat("0.0%")));
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));

        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setNumberFormatOverride(NumberFormat.getPercentInstance());
        axis.setUpperMargin(0.05f);
        axis.setLowerMargin(0.01f);

        return chart;
    }
}
