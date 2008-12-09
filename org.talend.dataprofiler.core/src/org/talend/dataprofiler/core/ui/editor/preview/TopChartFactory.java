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
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.urls.StandardXYZURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.ChartDatasetUtils;
import org.talend.dataprofiler.core.ui.utils.ChartDatasetUtils.DateValueAggregate;
import org.talend.dataprofiler.core.ui.utils.ChartDatasetUtils.ValueAggregator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import orgomg.cwm.resource.relational.Column;

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
    public static JFreeChart createBubbleChart(final ColumnSetMultiValueIndicator indic, Column numericColumn) {
        final Map<String, ValueAggregator> createXYZDatasets = ChartDatasetUtils.createXYZDatasets(indic, numericColumn);

        DefaultXYZDataset dataset = new DefaultXYZDataset();
        final Iterator<String> iterator = createXYZDatasets.keySet().iterator();
        while (iterator.hasNext()) {
            final String next = iterator.next();
            createXYZDatasets.get(next).addSeriesToXYZDataset(dataset, next);
        }
        String chartName = "Average of '" + numericColumn.getName() + "' versus count";
        JFreeChart chart = TopChartFactory.createBubbleChart(chartName,
                "average", "count", dataset, PlotOrientation.HORIZONTAL, true, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                true, true);
        final XYPlot plot = (XYPlot) chart.getPlot();
        final XYItemRenderer renderer = plot.getRenderer();
        renderer.setBaseToolTipGenerator(new StandardXYZToolTipGenerator() {

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

    public static JFreeChart createGanttChart(final ColumnSetMultiValueIndicator indic, Column dateColumn) {
        final Map<String, DateValueAggregate> createGannttDatasets = ChartDatasetUtils.createGanttDatasets(indic, dateColumn);

        TaskSeriesCollection ganttDataset = new TaskSeriesCollection();
        final Iterator<String> iterator = createGannttDatasets.keySet().iterator();
        while (iterator.hasNext()) {
            final String next = iterator.next();
            createGannttDatasets.get(next).addSeriesToGanttDataset(ganttDataset, next);
        }
        String chartAxies = "'" + dateColumn.getName() + "' Range per nominal values";
        JFreeChart chart = ChartFactory.createGanttChart("", // chart title
                "Task", // domain axis label
                chartAxies, // range axis label
                ganttDataset, // data
                true, // include legend
                true, // tooltips
                false // urls
                );
        return chart;
    }

    /**
     * DOC Zqin Comment method "create3DBarChart".
     * 
     * @param titile
     * @param dataset
     * @param showLegend
     * @return
     */
    public static JFreeChart create3DBarChart(String titile, CategoryDataset dataset, boolean showLegend) {

        JFreeChart chart = ChartFactory.createBarChart3D(null, titile, "Value", dataset, PlotOrientation.VERTICAL, showLegend, //$NON-NLS-1$
                false, true);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinesVisible(true);

        BarRenderer3D renderer3d = (BarRenderer3D) plot.getRenderer();

        renderer3d.setBaseItemLabelsVisible(true);
        renderer3d.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer3d.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer3d.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer3d.setBaseItemLabelFont(new Font("SansSerif", Font.BOLD, 12)); //$NON-NLS-1$
        renderer3d.setItemMargin(0.2);
        plot.setForegroundAlpha(0.50f);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        return chart;
    }

    /**
     * DOC Zqin Comment method "createBarChart".
     * 
     * @param titile
     * @param dataset
     * @return
     */
    public static JFreeChart createBarChart(String titile, CategoryDataset dataset) {

        return ChartFactory.createBarChart(null, titile, "Value", dataset, PlotOrientation.HORIZONTAL, false, false, false); //$NON-NLS-1$
    }

    /**
     * DOC Zqin Comment method "createBoxAndWhiskerChart".
     * 
     * @param title
     * @param dataset
     * @return
     */
    public static JFreeChart createBoxAndWhiskerChart(String title, BoxAndWhiskerCategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(null, title, "value", dataset, false);
        CategoryPlot plot = chart.getCategoryPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(false);

        double min = dataset.getMinRegularValue("0", "").doubleValue();
        double max = dataset.getMaxRegularValue("0", "").doubleValue();

        double unit = (max - min) / 10;
        rangeAxis.setRange(min - unit, max + unit);
        rangeAxis.setTickUnit(new NumberTickUnit(unit));
        return chart;
    }

    /**
     * DOC Zqin Comment method "createStacked3DBarChart".
     * 
     * @param titile
     * @param dataset
     * @param orientation
     * @return
     */
    public static JFreeChart createStacked3DBarChart(String titile, CategoryDataset dataset, PlotOrientation orientation) {

        JFreeChart chart = ChartFactory.createStackedBarChart3D(null, null, "Value", dataset, orientation, true, //$NON-NLS-1$
                false, false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinesVisible(true);

        StackedBarRenderer3D renderer = (StackedBarRenderer3D) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setRenderAsPercentages(true);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{3}", NumberFormat.getIntegerInstance(), //$NON-NLS-1$
                new DecimalFormat("0.0%"))); //$NON-NLS-1$
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));

        NumberAxis axis = (NumberAxis) plot.getRangeAxis();
        axis.setNumberFormatOverride(NumberFormat.getPercentInstance());
        axis.setUpperMargin(0.05f);
        axis.setLowerMargin(0.01f);

        return chart;
    }
}
