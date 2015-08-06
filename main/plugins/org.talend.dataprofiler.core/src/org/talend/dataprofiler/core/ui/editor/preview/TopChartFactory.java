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
package org.talend.dataprofiler.core.ui.editor.preview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardXYZURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.talend.commons.utils.SpecialValueDisplay;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.chart.ChartDatasetUtils;
import org.talend.dataprofiler.core.ui.chart.ChartDatasetUtils.DateValueAggregate;
import org.talend.dataprofiler.core.ui.chart.ChartDatasetUtils.ValueAggregator;
import org.talend.dataprofiler.core.ui.chart.ChartDecorator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author scorreia
 * 
 * Chart factory adapted for TOP.
 */
public final class TopChartFactory {

    private TopChartFactory() {
    }

    /**
     * Creates a bubble chart with default settings. The chart is composed of an {@link XYPlot}, with a
     * {@link NumberAxis} for the domain axis, a {@link NumberAxis} for the range axis, and an {@link XYBubbleRenderer}
     * to draw the data items.
     * 
     * This method is copied from
     * {@link org.jfree.chart.ChartFactory#createBubbleChart(String, String, String, XYZDataset, PlotOrientation, boolean, boolean, boolean)}
     * 
     * @param title the chart title (<code>null</code> permitted).
     * @param xAxisLabel a label for the X-axis (<code>null</code> permitted).
     * @param yAxisLabel a label for the Y-axis (<code>null</code> permitted).
     * @param dataset the dataset for the chart (<code>null</code> permitted).
     * @param orientation the orientation (horizontal or vertical) (<code>null</code> NOT permitted).
     * @param legend a flag specifying whether or not a legend is required.
     * @param tooltips configure chart to generate tool tips?
     * @param urls configure chart to generate URLs?
     * 
     * @return A bubble chart.
     */
    public static JFreeChart createBubbleChart(String title, String xAxisLabel, String yAxisLabel, XYZDataset dataset,
            PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {

        if (orientation == null) {
            throw new IllegalArgumentException(DefaultMessagesImpl.getString("TopChartFactory.argument")); //$NON-NLS-1$
        }
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(false);

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);

        XYItemRenderer renderer = new XYBubbleRenderer(XYBubbleRenderer.SCALE_ON_RANGE_AXIS) {

            @Override
            public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info,
                    XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item,
                    CrosshairState crosshairState, int pass) {

                // return straight away if the item is not visible
                if (!getItemVisible(series, item)) {
                    return;
                }

                PlotOrientation orientation = plot.getOrientation();

                // get the data point...
                double x = dataset.getXValue(series, item);
                double y = dataset.getYValue(series, item);
                double z = Double.NaN;
                if (dataset instanceof XYZDataset) {
                    XYZDataset xyzData = (XYZDataset) dataset;
                    z = xyzData.getZValue(series, item);
                }
                if (!Double.isNaN(z)) {
                    RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
                    RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
                    double transX = domainAxis.valueToJava2D(x, dataArea, domainAxisLocation);
                    double transY = rangeAxis.valueToJava2D(y, dataArea, rangeAxisLocation);

                    double transDomain = 0.0;
                    double transRange = 0.0;
                    double zero;

                    // MOD scorreia +2L avoid points: minimal size of circle must be 1
                    // z = z * transX + 1;

                    // ADD xqliu 2009-07-06 bug 8035
                    double zSize = getBubbleSize(z); // calculate the multiple of bubble's default size
                    z = 0; // use bubble's default size
                    // ~

                    switch (getScaleType()) {
                    case SCALE_ON_DOMAIN_AXIS:
                        zero = domainAxis.valueToJava2D(0.0, dataArea, domainAxisLocation);
                        transDomain = domainAxis.valueToJava2D(z, dataArea, domainAxisLocation) - zero;
                        transRange = transDomain;
                        break;
                    case SCALE_ON_RANGE_AXIS:
                        zero = rangeAxis.valueToJava2D(0.0, dataArea, rangeAxisLocation);
                        transRange = zero - rangeAxis.valueToJava2D(z, dataArea, rangeAxisLocation);
                        transDomain = transRange;
                        break;
                    default:
                        double zero1 = domainAxis.valueToJava2D(0.0, dataArea, domainAxisLocation);
                        double zero2 = rangeAxis.valueToJava2D(0.0, dataArea, rangeAxisLocation);
                        transDomain = domainAxis.valueToJava2D(z, dataArea, domainAxisLocation) - zero1;
                        transRange = zero2 - rangeAxis.valueToJava2D(z, dataArea, rangeAxisLocation);
                    }
                    transDomain = Math.abs(transDomain);
                    transRange = Math.abs(transRange);

                    // MODSCA 2008-11-27 enlarge ellipse by diag% of the total diagonal
                    double diag = Math.sqrt(dataArea.getHeight() * dataArea.getHeight() + dataArea.getWidth()
                            * dataArea.getWidth());
                    transDomain += diag / 100;
                    transRange += diag / 100;

                    Ellipse2D circle = null;

                    // ADD xqliu 2009-07-06 bug 8035
                    transDomain *= zSize;
                    transRange *= zSize;
                    // ~

                    if (orientation == PlotOrientation.VERTICAL) {
                        circle = new Ellipse2D.Double(transX - transDomain / 2.0, transY - transRange / 2.0, transDomain,
                                transRange);
                    } else if (orientation == PlotOrientation.HORIZONTAL) {
                        circle = new Ellipse2D.Double(transY - transRange / 2.0, transX - transDomain / 2.0, transRange,
                                transDomain);
                    }
                    g2.setPaint(getItemPaint(series, item));
                    g2.fill(circle);
                    g2.setStroke(getItemOutlineStroke(series, item));
                    g2.setPaint(getItemOutlinePaint(series, item));
                    g2.draw(circle);

                    if (isItemLabelVisible(series, item)) {
                        if (orientation == PlotOrientation.VERTICAL) {
                            drawItemLabel(g2, orientation, dataset, series, item, transX, transY, false);
                        } else if (orientation == PlotOrientation.HORIZONTAL) {
                            drawItemLabel(g2, orientation, dataset, series, item, transY, transX, false);
                        }
                    }

                    // add an entity if this info is being collected
                    EntityCollection entities = null;
                    if (info != null) {
                        entities = info.getOwner().getEntityCollection();
                        if (entities != null && circle.intersects(dataArea)) {
                            addEntity(entities, circle, dataset, series, item, circle.getCenterX(), circle.getCenterY());
                        }
                    }

                    int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
                    int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
                    updateCrosshairValues(crosshairState, x, y, domainAxisIndex, rangeAxisIndex, transX, transY, orientation);
                }

            }

            /**
             * DOC xqliu : calculate the size of bubble. for bug 8035 2009-07-06.
             * 
             * @param z multiple of bubble's default size
             * @return
             */
            private double getBubbleSize(double z) {
                if (z > 0 && z <= 10) {
                    return 2;
                } else if (z > 10 && z <= 100) {
                    return 3;
                } else if (z > 100) {
                    return 4;
                }
                return 1;
            }

        };
        if (tooltips) {
            renderer.setBaseToolTipGenerator(new StandardXYZToolTipGenerator());
        }
        if (urls) {
            renderer.setURLGenerator(new StandardXYZURLGenerator());
        }
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

    /**
     * Method "createBubbleChart".
     * 
     * @param indic the indicator
     * @param numericColumn the analyzed numeric column
     * @return the bubble chart
     */
    public static JFreeChart createBubbleChart(final ColumnSetMultiValueIndicator indic, ModelElement numericColumn) {
        final Map<String, ValueAggregator> createXYZDatasets = ChartDatasetUtils.createXYZDatasets(indic, numericColumn);

        DefaultXYZDataset dataset = new DefaultXYZDataset();
        final Iterator<String> iterator = createXYZDatasets.keySet().iterator();
        while (iterator.hasNext()) {
            final String next = iterator.next();
            createXYZDatasets.get(next).addSeriesToXYZDataset(dataset, next);
        }
        String chartName = DefaultMessagesImpl.getString("TopChartFactory.ChartName", numericColumn.getName()); //$NON-NLS-1$
        JFreeChart chart = TopChartFactory
                .createBubbleChart(
                        chartName,
                        DefaultMessagesImpl.getString("TopChartFactory.average"), DefaultMessagesImpl.getString("TopChartFactory.count"), dataset, PlotOrientation.HORIZONTAL, //$NON-NLS-1$ //$NON-NLS-2$
                        true, true, true);
        final XYPlot plot = (XYPlot) chart.getPlot();
        final XYItemRenderer renderer = plot.getRenderer();
        renderer.setBaseToolTipGenerator(new StandardXYZToolTipGenerator() {

            private static final long serialVersionUID = 1L;

            /*
             * (non-Javadoc)
             * 
             * @see org.jfree.chart.labels.StandardXYZToolTipGenerator#createItemArray(org.jfree.data.xy.XYZDataset,
             * int, int)
             */
            @Override
            protected Object[] createItemArray(XYZDataset dset, int series, int item) {
                final Comparable<?> seriesKey = dset.getSeriesKey(series);
                final String seriesK = String.valueOf(seriesKey);
                final ValueAggregator valueAggregator = createXYZDatasets.get(seriesKey);
                String label = valueAggregator.getLabels(seriesK).get(item);
                final Object[] itemArray = super.createItemArray(dset, series, item);
                itemArray[0] = label;
                itemArray[1] = "avg=" + itemArray[1]; //$NON-NLS-1$
                itemArray[2] = "record count=" + itemArray[2]; //$NON-NLS-1$
                itemArray[3] = "null count=" + itemArray[3]; //$NON-NLS-1$
                return itemArray;
            }

        });
        return chart;
    }

    /**
     * 
     * DOC zhaoxinyi Comment method "createGanttChart".
     * 
     * @param indic
     * @param dateColumn
     * @return
     */

    public static JFreeChart createGanttChart(final ColumnSetMultiValueIndicator indic, ModelElement dateColumn) {
        final Map<String, DateValueAggregate> createGannttDatasets = ChartDatasetUtils.createGanttDatasets(indic, dateColumn);

        TaskSeriesCollection ganttDataset = new TaskSeriesCollection();
        final Iterator<String> iterator = createGannttDatasets.keySet().iterator();
        while (iterator.hasNext()) {
            final String next = iterator.next();
            createGannttDatasets.get(next).addSeriesToGanttDataset(ganttDataset, next);
        }
        String chartAxies = DefaultMessagesImpl.getString("TopChartFactory.chartAxies", dateColumn.getName()); //$NON-NLS-1$
        // ADD msjian TDQ-5112 2012-4-10: after upgrate to jfreechart-1.0.12.jar, change the default chart wallPaint
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        // TDQ-5112~
        JFreeChart chart = ChartFactory.createGanttChart("", // chart title //$NON-NLS-1$
                DefaultMessagesImpl.getString("TopChartFactory.Categories"), // domain axis label //$NON-NLS-1$
                chartAxies, // range axis label
                ganttDataset, // data
                true, // include legend
                true, // tooltips
                false // urls
                );

        // ADD TDQ-5251 msjian 2012-7-31: do not display the shadow
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        // TDQ-5251~

        return chart;
    }

    /**
     * 
     * mzhao create bar chart with default bar render class.
     * 
     * @param titile
     * @param dataset
     * @param showLegend
     * @return
     */
    public static JFreeChart createBarChart(String titile, CategoryDataset dataset, boolean showLegend) {
        // MOD hcheng for 6965,Use 2D bar charts instead of 3D bar charts
        // ADD msjian TDQ-5112 2012-4-10: after upgrate to jfreechart-1.0.12.jar, change the default chart wallPaint
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        // TDQ-5112~
        JFreeChart chart = ChartFactory.createBarChart(null, titile,
                DefaultMessagesImpl.getString("TopChartFactory.count"), dataset, PlotOrientation.VERTICAL, showLegend, //$NON-NLS-1$
                true, false);
        ChartDecorator.decorateBarChart(chart);
        return chart;
    }

    /**
     * create bar chart.
     * 
     * @param titile
     * @param dataset
     * @return
     */
    public static JFreeChart createBarChart(String title, CategoryDataset dataset) {
        // ADD msjian TDQ-5112 2012-4-10: after upgrate to jfreechart-1.0.12.jar, change the default chart wallPaint
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        // TDQ-5112~
        JFreeChart createBarChart = ChartFactory
                .createBarChart(
                        null,
                        DefaultMessagesImpl.getString("TopChartFactory.value"), title, dataset, PlotOrientation.HORIZONTAL, false, false, false); //$NON-NLS-1$

        CategoryPlot plot = createBarChart.getCategoryPlot();

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelPaint(SpecialValueDisplay.NULL_FIELD, Color.RED);
        domainAxis.setTickLabelPaint(SpecialValueDisplay.EMPTY_FIELD, Color.RED);

        // ADD TDQ-5251 msjian 2012-7-31: do not display the shadow
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        // TDQ-5251~

        return createBarChart;
    }

    /**
     * DOC Zqin Comment method "createBoxAndWhiskerChart".
     * 
     * @param title
     * @param dataset
     * @return
     */
    public static JFreeChart createBoxAndWhiskerChart(String title, BoxAndWhiskerCategoryDataset dataset) {
        // ADD msjian TDQ-5112 2012-4-10: after upgrate to jfreechart-1.0.12.jar, change the default chart wallPaint
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        // TDQ-5112~
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(null, title,
                DefaultMessagesImpl.getString("TopChartFactory.Value"), dataset, false); //$NON-NLS-1$
        CategoryPlot plot = chart.getCategoryPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(false);

        double min = dataset.getMinRegularValue("0", "").doubleValue(); //$NON-NLS-1$ //$NON-NLS-2$
        double max = dataset.getMaxRegularValue("0", "").doubleValue(); //$NON-NLS-1$ //$NON-NLS-2$

        double unit = (max - min) / 10;
        rangeAxis.setRange(min - unit, max + unit);
        rangeAxis.setTickUnit(new NumberTickUnit(unit));
        return chart;
    }

    /**
     * DOC Zqin Comment method "createStackedBarChart".
     * 
     * @param titile
     * @param dataset
     * @param orientation
     * @return
     */
    public static JFreeChart createStackedBarChart(String titile, CategoryDataset dataset, PlotOrientation orientation) {
        return createStackedBarChart(null, null, DefaultMessagesImpl.getString("TopChartFactory.Value"), dataset, orientation, //$NON-NLS-1$
                true, false, false);
    }

    /**
     * DOC xqliu Comment method "createStackedBarChart".
     * 
     * @param title
     * @param dataset
     * @param showLegend
     * @return
     */
    public static JFreeChart createStackedBarChart(String title, CategoryDataset dataset, boolean showLegend) {
        return createStackedBarChart(null, title, DefaultMessagesImpl.getString("TopChartFactory.value"), dataset, //$NON-NLS-1$
                PlotOrientation.VERTICAL, showLegend, true, false);
    }

    /**
     * DOC xqliu Comment method "createStackedBarChart".
     * 
     * @param title
     * @param dataset
     * @param orientation
     * @param showLegend
     * @return
     */
    public static JFreeChart createStackedBarChart(String title, CategoryDataset dataset, PlotOrientation orientation,
            boolean showLegend) {
        return createStackedBarChart(null, null, title, dataset, orientation, showLegend, false, false);
    }

    /**
     * DOC xqliu Comment method "createStackedBarChart".
     * 
     * @param title
     * @param domainAxisLabel
     * @param rangeAxisLabel
     * @param dataset
     * @param orientation
     * @param legend
     * @param tooltips
     * @param urls
     * @return
     */
    public static JFreeChart createStackedBarChart(String title, String domainAxisLabel, String rangeAxisLabel,
            CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        // ADD msjian TDQ-5112 2012-4-10: after upgrate to jfreechart-1.0.12.jar, change the default chart wallPaint
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        // TDQ-5112~
        final JFreeChart chart = ChartFactory.createStackedBarChart(title, domainAxisLabel, rangeAxisLabel, dataset, orientation,
                legend, tooltips, urls);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setRangeGridlinesVisible(true);

        StackedBarRenderer sbr = (StackedBarRenderer) plot.getRenderer();
        sbr.setBaseItemLabelsVisible(true);
        sbr.setRenderAsPercentages(true);
        sbr.setBaseItemLabelGenerator(new DQRuleItemLabelGenerator("{3}", NumberFormat.getIntegerInstance())); //$NON-NLS-1$
        sbr.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        // ADD xqliu 2010-03-10 feature 10834
        sbr.setBaseToolTipGenerator(new DQRuleToolTipGenerator(ChartDecorator.NEW_TOOL_TIP_FORMAT_STRING, NumberFormat
                .getInstance()));
        // ~10834

        // ADD TDQ-5251 msjian 2012-7-31: do not display the shadow
        sbr.setShadowVisible(false);
        // TDQ-5251~

        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setNumberFormatOverride(NumberFormat.getPercentInstance());
        axis.setUpperMargin(0.05f);
        axis.setLowerMargin(0.01f);

        return chart;
    }

    /**
     * DOC xqliu Comment method "createLineChart".
     * 
     * @param title
     * @param dataset
     * @param showLegend
     * @return
     */
    public static JFreeChart createLineChart(String title, XYDataset dataset, boolean showLegend) {
        // ADD msjian TDQ-5112 2012-4-10: after upgrate to jfreechart-1.0.12.jar, change the default chart wallPaint
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        // TDQ-5112~
        final JFreeChart chart = ChartFactory.createXYLineChart(null, title,
                DefaultMessagesImpl.getString("TopChartFactory.Percent"), dataset, PlotOrientation.VERTICAL, //$NON-NLS-1$
                showLegend, false, false);

        final XYPlot plot = chart.getXYPlot();

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return chart;
    }

    /**
     * 
     * DOC qiongli Comment method "createPieChart".
     * 
     * @param title
     * @param dataset
     * @param showLegend
     * @param toolTips,if show the toolTips.
     * @param urls
     * @return
     */
    public static JFreeChart createPieChart(String title, PieDataset dataset, boolean showLegend, boolean toolTips, boolean urls) {
        // ADD msjian TDQ-5112 2012-4-10: after upgrate to jfreechart-1.0.12.jar, change the default chart wallPaint
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        // TDQ-5112~
        return ChartFactory.createPieChart(title, dataset, showLegend, toolTips, urls);
    }
}
