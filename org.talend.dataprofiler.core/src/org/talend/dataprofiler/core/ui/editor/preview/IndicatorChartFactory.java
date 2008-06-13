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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.editor.preview.ext.FrequencyExt;
import org.talend.dataprofiler.core.ui.utils.ChartUtils;
import org.talend.dataquality.indicators.IndicatorParameters;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class IndicatorChartFactory {

    private static final int CHART_WIDTH = 400;

    private static final int CHART_HEIGHT = 230;

    private static final int CHART_WIDTH2 = 100;

    private static final int CHART_HEIGHT2 = 380;

    /**
     * see http://en.wikipedia.org/wiki/Box_plot .
     */
    private static final double OUTLIER_FACTOR = 3;

    private static BarRenderer3D renderer = new BarRenderer3D();

    // start to create kinds of chart
    public static ImageDescriptor create3DBarChart(String titile, CategoryDataset dataset, boolean showLegend) {

        JFreeChart chart = ChartFactory.createBarChart3D(null, titile, "Value", dataset, PlotOrientation.VERTICAL, showLegend,
                false, false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinesVisible(true);

        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer.setBaseItemLabelFont(new Font("SansSerif", Font.BOLD, 12));
        renderer.setItemMargin(0.2);

        plot.setRenderer(renderer);
        plot.setForegroundAlpha(0.50f);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

        try {

            return ChartUtils.convertToImage(chart, CHART_WIDTH, CHART_HEIGHT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ImageDescriptor createBarChart(String titile, CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createBarChart(null, titile, "Value", dataset, PlotOrientation.HORIZONTAL, false, false,
                false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setForegroundAlpha(0.50f);

        try {

            return ChartUtils.convertToImage(chart, CHART_WIDTH, CHART_HEIGHT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ImageDescriptor createBoxAndWhiskerChart(String title, BoxAndWhiskerCategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(null, null, "value", dataset, false);

        try {

            return ChartUtils.convertToImage(chart, CHART_WIDTH2, CHART_HEIGHT2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // end creating kinds of chart

    // start to create kinds of dataset with real value

    private static CategoryDataset createSimpleDataset(List<IndicatorUnit> indicatorUnitList, boolean isCreate) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (isCreate) {
            for (int index = 0; index < indicatorUnitList.size(); index++) {
                IndicatorUnit unit = indicatorUnitList.get(index);
                IndicatorCommonUtil.compositeIndicatorMap(unit);
                Object object = unit.getValue();
                String label = unit.getIndicatorName();

                if (object == null) {
                    dataset.addValue(0, label, "");
                } else {
                    String valueStr = String.valueOf(object);
                    double value = Double.parseDouble(valueStr);

                    renderer.setSeriesPaint(index, unit.getColor());
                    dataset.addValue(value, label, "");
                }
            }
        } else {
            for (IndicatorUnit indicator : indicatorUnitList) {

                dataset.addValue(150, indicator.getIndicatorName(), "");
            }
        }

        return dataset;

    }

    private static CategoryDataset createTextedDataset(List<IndicatorUnit> indicatorTypeMapping, boolean isCreate) {

        return createSimpleDataset(indicatorTypeMapping, isCreate);
    }

    private static CategoryDataset createSummaryDataset(List<IndicatorUnit> indicatorUnits, boolean isCreate) {

        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        if (isCreate) {
            Map<IndicatorEnum, Double> map = new HashMap<IndicatorEnum, Double>();

            for (IndicatorUnit indicatorUnit : indicatorUnits) {

                Object object = null;
                try {
                    IndicatorCommonUtil.compositeIndicatorMap(indicatorUnit);
                    object = indicatorUnit.getValue();
                } catch (Exception ue) {
                    ue.printStackTrace();
                    object = null;
                }

                if (object != null) {
                    String strValue = String.valueOf(object);
                    double doubleValue = Double.valueOf(strValue);
                    map.put(indicatorUnit.getType(), doubleValue);
                } else {
                    map.put(indicatorUnit.getType(), Double.valueOf(0));
                }
            }

            if (map.size() != 6) {
                DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

                if (map.get(IndicatorEnum.MinValueIndicatorEnum) != null) {
                    barDataset.addValue(map.get(IndicatorEnum.MinValueIndicatorEnum), "", IndicatorEnum.MinValueIndicatorEnum
                            .getLabel());
                }

                if (map.get(IndicatorEnum.LowerQuartileIndicatorEnum) != null) {
                    barDataset.addValue(map.get(IndicatorEnum.LowerQuartileIndicatorEnum), "",
                            IndicatorEnum.LowerQuartileIndicatorEnum.getLabel());
                }

                if (map.get(IndicatorEnum.MeanIndicatorEnum) != null) {
                    barDataset.addValue(map.get(IndicatorEnum.MeanIndicatorEnum), "", IndicatorEnum.MeanIndicatorEnum.getLabel());
                }

                if (map.get(IndicatorEnum.MedianIndicatorEnum) != null) {
                    barDataset.addValue(map.get(IndicatorEnum.MedianIndicatorEnum), "", IndicatorEnum.MedianIndicatorEnum
                            .getLabel());
                }

                if (map.get(IndicatorEnum.UpperQuartileIndicatorEnum) != null) {
                    barDataset.addValue(map.get(IndicatorEnum.UpperQuartileIndicatorEnum), "",
                            IndicatorEnum.UpperQuartileIndicatorEnum.getLabel());
                }

                if (map.get(IndicatorEnum.MaxValueIndicatorEnum) != null) {
                    barDataset.addValue(map.get(IndicatorEnum.MaxValueIndicatorEnum), "", IndicatorEnum.MaxValueIndicatorEnum
                            .getLabel());
                }

                renderer.setSeriesPaint(0, Color.RED);
                return barDataset;
            }

            // MOD scorreia 2008-06-05 use the same method to create the item.
            BoxAndWhiskerItem item = createBoxAndWhiskerItem(map.get(IndicatorEnum.MeanIndicatorEnum), map
                    .get(IndicatorEnum.MedianIndicatorEnum), map.get(IndicatorEnum.LowerQuartileIndicatorEnum), map
                    .get(IndicatorEnum.UpperQuartileIndicatorEnum), map.get(IndicatorEnum.MinValueIndicatorEnum), map
                    .get(IndicatorEnum.MaxValueIndicatorEnum), null);

            dataset.add(item, "", "");
        } else {
            BoxAndWhiskerItem item = createBoxAndWhiskerItem(40.0, 45.0, 30.0, 60.0, 15.0, 75.0, null);
            dataset.add(item, "", "");
        }

        return dataset;

    }

    private static BoxAndWhiskerItem createBoxAndWhiskerItem(Double mean, Double median, Double q1, Double q3,
            Double minRegularValue, Double maxRegularValue, List outliers) {
        // MOD scorreia 2008-06-05 automatic computation of outliers limits
        // see http://en.wikipedia.org/wiki/Box_plot
        Double xIQR = (q1 != null && q3 != null) ? OUTLIER_FACTOR * (q3 - q1) : null;
        Double minOutlier = xIQR != null ? q1 - xIQR : null;
        Double maxOutlier = xIQR != null ? q3 + xIQR : null;
        // enhance bounds of graphics when needed
        if (minOutlier != null && minRegularValue != null) {
            minOutlier = Math.min(minOutlier, minRegularValue);
        }
        if (maxOutlier != null && maxRegularValue != null) {
            maxOutlier = Math.max(maxOutlier, maxRegularValue);
        }

        BoxAndWhiskerItem item = new BoxAndWhiskerItem(mean, median, q1, q3, minRegularValue, maxRegularValue, minOutlier,
                maxOutlier, outliers);
        return item;
    }

    private static CategoryDataset createFrequenceDataset(List<IndicatorUnit> indicatorTypeMapping, boolean isCreate) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (IndicatorUnit indicatorUnit : indicatorTypeMapping) {
            Object object = null;
            try {
                IndicatorCommonUtil.compositeIndicatorMap(indicatorUnit);
                object = indicatorUnit.getValue();

            } catch (UnsupportedOperationException ue) {
                object = null;
            }

            if (object != null) {
                FrequencyExt[] frequencyExt = (FrequencyExt[]) object;
                Arrays.sort(frequencyExt);

                if (isCreate) {
                    int numOfShown = frequencyExt.length;

                    IndicatorParameters parameters = indicatorUnit.getIndicator().getParameters();
                    if (parameters != null) {
                        if (parameters.getTopN() < frequencyExt.length) {
                            numOfShown = parameters.getTopN();
                        }
                    }

                    for (int i = 0; i < numOfShown; i++) {
                        dataset.addValue(frequencyExt[i].getValue(), "", String.valueOf(frequencyExt[i].getKey()));
                    }
                } else {

                    dataset.addValue(70, "", "one");
                    dataset.addValue(150, "", "two");
                    dataset.addValue(180, "", "three");
                    dataset.addValue(185, "", "four");
                    dataset.addValue(224, "", "five");
                }
            }
        }

        return dataset;
    }

    // end create dataset with real value

    public static List<ImageDescriptor> createChart(ColumnIndicator column, boolean isCreate) {

        CompositeIndicator compositeIndicator = new CompositeIndicator(column);
        Map<String, List<IndicatorUnit>> separatedMap = compositeIndicator.getIndicatorComposite();
        List<ImageDescriptor> returnFiles = new ArrayList<ImageDescriptor>();

        if (separatedMap.get(CompositeIndicator.SIMPLE_STATISTICS).size() != 0) {

            CategoryDataset dataset = createSimpleDataset(separatedMap.get(CompositeIndicator.SIMPLE_STATISTICS), isCreate);

            returnFiles.add(create3DBarChart(CompositeIndicator.SIMPLE_STATISTICS, dataset, true));

        }

        if (separatedMap.get(CompositeIndicator.TEXT_STATISTICS).size() != 0) {

            CategoryDataset dataset = createTextedDataset(separatedMap.get(CompositeIndicator.TEXT_STATISTICS), isCreate);

            returnFiles.add(create3DBarChart(CompositeIndicator.TEXT_STATISTICS, dataset, true));
        }

        if (separatedMap.get(CompositeIndicator.FREQUENCE_STATISTICS).size() != 0) {

            CategoryDataset dataset = createFrequenceDataset(separatedMap.get(CompositeIndicator.FREQUENCE_STATISTICS), isCreate);

            returnFiles.add(createBarChart(CompositeIndicator.FREQUENCE_STATISTICS, dataset));
        }

        if (separatedMap.get(CompositeIndicator.SUMMARY_STATISTICS).size() != 0) {

            CategoryDataset dataset = createSummaryDataset(separatedMap.get(CompositeIndicator.SUMMARY_STATISTICS), isCreate);

            if (dataset instanceof BoxAndWhiskerCategoryDataset) {
                returnFiles.add(createBoxAndWhiskerChart(CompositeIndicator.SUMMARY_STATISTICS,
                        (BoxAndWhiskerCategoryDataset) dataset));
            } else {
                returnFiles.add(create3DBarChart(CompositeIndicator.SUMMARY_STATISTICS, dataset, false));
            }

        }

        return returnFiles;
    }

}
