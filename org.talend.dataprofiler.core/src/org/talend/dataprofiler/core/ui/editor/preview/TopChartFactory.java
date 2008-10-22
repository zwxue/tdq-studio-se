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

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.urls.StandardXYZURLGenerator;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.talend.dataprofiler.core.ui.editor.preview.ChartDatasetFactory.ValueAggregator;
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
            throw new IllegalArgumentException("Null 'orientation' argument.");
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
                    // MOD scorreia +2L avoid points: minimal size of circle must be 1
                    z += 1;
                    
                    RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
                    RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
                    double transX = domainAxis.valueToJava2D(x, dataArea, domainAxisLocation);
                    double transY = rangeAxis.valueToJava2D(y, dataArea, rangeAxisLocation);

                    double transDomain = 0.0;
                    double transRange = 0.0;
                    double zero;

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
     * DOC scorreia Comment method "createBubbleChart".
     * 
     * @param indic
     * @param numericColumn TODO
     * @return
     */
    public static JFreeChart createBubbleChart(final ColumnSetMultiValueIndicator indic, Column numericColumn) {
        final Map<String, ValueAggregator> createXYZDatasets = ChartDatasetFactory.createXYZDatasets(indic, numericColumn);

        DefaultXYZDataset dataset = new DefaultXYZDataset();
        final Iterator<String> iterator = createXYZDatasets.keySet().iterator();
        while (iterator.hasNext()) {
            final String next = iterator.next();
            createXYZDatasets.get(next).addSeriesToXYZDataset(dataset, next);
        }
        JFreeChart chart = TopChartFactory.createBubbleChart("THE title", "x", "y", dataset, PlotOrientation.HORIZONTAL, true,
                false, true);
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
}
