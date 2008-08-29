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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
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
import org.talend.dataprofiler.core.ui.editor.preview.ext.PatternMatchingExt;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartDataEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartWithData;
import org.talend.dataprofiler.core.ui.editor.preview.model.PatternChartDataEntity;
import org.talend.dataprofiler.core.ui.utils.ChartUtils;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.PatternMatchingIndicator;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class IndicatorChartFactory {

    private static final int CHART_WIDTH = 410;

    private static final int CHART_HEIGHT = 230;

    private static final int CHART_WIDTH2 = 100;

    private static final int CHART_HEIGHT2 = 380;

    /**
     * see http://en.wikipedia.org/wiki/Box_plot .
     */
    private static final double OUTLIER_FACTOR = 3;

    private static BarRenderer3D renderer3d = new BarRenderer3D();

    // start to create kinds of chart
    public static ImageDescriptor create3DBarChart(String titile, CategoryDataset dataset, boolean showLegend) {

        JFreeChart chart = ChartFactory.createBarChart3D(null, titile, "Value", dataset, PlotOrientation.VERTICAL, showLegend,
                false, false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinesVisible(true);

        renderer3d.setBaseItemLabelsVisible(true);
        renderer3d.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer3d.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer3d.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        renderer3d.setBaseItemLabelFont(new Font("SansSerif", Font.BOLD, 12));
        renderer3d.setItemMargin(0.2);

        plot.setRenderer(renderer3d);
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

    public static ImageDescriptor createStacked3DBarChart(String titile, CategoryDataset dataset) {

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

        try {

            return ChartUtils.convertToImage(chart, CHART_WIDTH, CHART_HEIGHT);

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

                if (unit.isExcuted()) {
                    double value = Double.parseDouble(unit.getValue().toString());
                    String label = unit.getIndicatorName();

                    renderer3d.setSeriesPaint(index, unit.getColor());
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

    private static CategoryDataset createTextedDataset(List<IndicatorUnit> indicatorUnitList, boolean isCreate) {

        ComparatorsFactory.sort(indicatorUnitList, ComparatorsFactory.TEXT_STATISTICS_COMPARATOR_ID);
        return createSimpleDataset(indicatorUnitList, isCreate);
    }

    private static CategoryDataset createSummaryDataset(List<IndicatorUnit> indicatorUnitList, boolean isCreate) {

        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        if (isCreate) {
            Map<IndicatorEnum, Double> map = new HashMap<IndicatorEnum, Double>();

            for (IndicatorUnit indicatorUnit : indicatorUnitList) {

                IndicatorCommonUtil.compositeIndicatorMap(indicatorUnit);

                if (indicatorUnit.isExcuted()) {
                    double doubleValue = Double.parseDouble(indicatorUnit.getValue().toString());
                    map.put(indicatorUnit.getType(), doubleValue);
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

                renderer3d.setSeriesPaint(0, Color.RED);
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

    private static CategoryDataset createFrequenceDataset(List<IndicatorUnit> indicatorUnitList, boolean isCreate) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (IndicatorUnit indicatorUnit : indicatorUnitList) {
            IndicatorCommonUtil.compositeIndicatorMap(indicatorUnit);

            if (indicatorUnit.isExcuted()) {
                FrequencyExt[] frequencyExt = (FrequencyExt[]) indicatorUnit.getValue();
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

    private static CategoryDataset createPatternMatchDataset(List<IndicatorUnit> indicatorUnitList, boolean isCreate) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (isCreate) {

            int i = 0;
            for (IndicatorUnit unit : indicatorUnitList) {
                IndicatorCommonUtil.compositeIndicatorMap(unit);
                String label = unit.getIndicatorName();

                if (unit.isExcuted()) {
                    PatternMatchingExt patternExt = (PatternMatchingExt) unit.getValue();
                    double notMathCount = patternExt.getNotMatchingValueCount();
                    double machCount = patternExt.getMatchingValueCount();

                    dataset.addValue(machCount, "matching", label);
                    dataset.addValue(notMathCount, "not matching", label);
                }

                i++;
            }
        } else {
            dataset.addValue(0.23, "1", "");
            dataset.addValue(0.77, "2", "");
        }

        return dataset;

    }

    // end create dataset with real value

    public static List<ChartWithData> createChart(ColumnIndicator column, boolean isCreate) {

        CompositeIndicator compositeIndicator = new CompositeIndicator(column);
        Map<String, List<IndicatorUnit>> separatedMap = compositeIndicator.getIndicatorComposite();

        List<IndicatorUnit> simpleUnitList = separatedMap.get(CompositeIndicator.SIMPLE_STATISTICS);
        List<IndicatorUnit> textUnitList = separatedMap.get(CompositeIndicator.TEXT_STATISTICS);
        List<IndicatorUnit> frequencyUnitList = separatedMap.get(CompositeIndicator.FREQUENCE_STATISTICS);
        List<IndicatorUnit> summaryUnitList = separatedMap.get(CompositeIndicator.SUMMARY_STATISTICS);
        List<IndicatorUnit> patternUnitList = separatedMap.get(CompositeIndicator.PATTERN_MATCHING);
        List<IndicatorUnit> sqlPatternUnitList = separatedMap.get(CompositeIndicator.SQL_PATTERN_MATCHING);
        List<IndicatorUnit> modeIndicatorUnitList = separatedMap.get(CompositeIndicator.MODE_INDICATOR);

        List<ChartWithData> returnFiles = new ArrayList<ChartWithData>();

        if (!simpleUnitList.isEmpty()) {

            CategoryDataset dataset = createSimpleDataset(simpleUnitList, isCreate);
            ImageDescriptor imageDescriptor = create3DBarChart(CompositeIndicator.SIMPLE_STATISTICS, dataset, true);
            ChartWithData chart = new ChartWithData(CompositeIndicator.SIMPLE_STATISTICS, imageDescriptor,
                    getDataEnityFromUnits(simpleUnitList));
            returnFiles.add(chart);

        }

        if (!textUnitList.isEmpty()) {

            CategoryDataset dataset = createTextedDataset(textUnitList, isCreate);
            ImageDescriptor imageDescriptor = create3DBarChart(CompositeIndicator.TEXT_STATISTICS, dataset, true);
            ChartWithData chart = new ChartWithData(CompositeIndicator.TEXT_STATISTICS, imageDescriptor,
                    getDataEnityFromUnits(textUnitList));
            returnFiles.add(chart);
        }

        if (!frequencyUnitList.isEmpty()) {

            CategoryDataset dataset = createFrequenceDataset(frequencyUnitList, isCreate);
            ImageDescriptor imageDescriptor = createBarChart(CompositeIndicator.FREQUENCE_STATISTICS, dataset);
            ChartWithData chart = new ChartWithData(CompositeIndicator.FREQUENCE_STATISTICS, imageDescriptor,
                    getDataEnityFromUnits(frequencyUnitList));
            returnFiles.add(chart);
        }

        if (!summaryUnitList.isEmpty()) {

            CategoryDataset dataset = createSummaryDataset(summaryUnitList, isCreate);

            if (dataset instanceof BoxAndWhiskerCategoryDataset) {

                ImageDescriptor imageDescriptor = createBoxAndWhiskerChart(CompositeIndicator.SUMMARY_STATISTICS,
                        (BoxAndWhiskerCategoryDataset) dataset);
                ChartWithData chart = new ChartWithData(CompositeIndicator.SUMMARY_STATISTICS, imageDescriptor,
                        getDataEnityFromUnits(summaryUnitList));
                returnFiles.add(chart);
            } else {

                ImageDescriptor imageDescriptor = create3DBarChart(CompositeIndicator.SUMMARY_STATISTICS, dataset, false);
                ChartWithData chart = new ChartWithData(CompositeIndicator.SUMMARY_STATISTICS, imageDescriptor,
                        getDataEnityFromUnits(summaryUnitList));
                returnFiles.add(chart);
            }

        }

        if (!patternUnitList.isEmpty()) {

            CategoryDataset dataset = createPatternMatchDataset(patternUnitList, isCreate);
            ImageDescriptor imageDescriptor = createStacked3DBarChart(CompositeIndicator.PATTERN_MATCHING, dataset);
            ChartWithData chart = new ChartWithData(CompositeIndicator.PATTERN_MATCHING, imageDescriptor,
                    getDataEnityFromUnits(patternUnitList));
            returnFiles.add(chart);
        }

        if (!sqlPatternUnitList.isEmpty()) {
            CategoryDataset dataset = createPatternMatchDataset(sqlPatternUnitList, isCreate);
            ImageDescriptor imageDescriptor = createStacked3DBarChart(CompositeIndicator.SQL_PATTERN_MATCHING, dataset);
            ChartWithData chart = new ChartWithData(CompositeIndicator.SQL_PATTERN_MATCHING, imageDescriptor,
                    getDataEnityFromUnits(sqlPatternUnitList));
            returnFiles.add(chart);
        }

        if (!modeIndicatorUnitList.isEmpty()) {
            for (IndicatorUnit unit : modeIndicatorUnitList) {
                IndicatorCommonUtil.compositeIndicatorMap(unit);
            }

            ChartWithData chart = new ChartWithData(CompositeIndicator.MODE_INDICATOR, null,
                    getDataEnityFromUnits(modeIndicatorUnitList));
            returnFiles.add(chart);
        }

        return returnFiles;
    }

    private static ChartDataEntity[] getDataEnityFromUnits(List<IndicatorUnit> unitList) {

        List<ChartDataEntity> list = new ArrayList<ChartDataEntity>();

        for (IndicatorUnit unit : unitList) {

            if (unit.isExcuted()) {

                ChartDataEntity entity;

                switch (unit.getType()) {
                case FrequencyIndicatorEnum:
                    FrequencyExt[] freqExt = (FrequencyExt[]) unit.getValue();
                    for (FrequencyExt one : freqExt) {
                        entity = new ChartDataEntity();
                        // MOD scorreia 2008-08-20 handle case when key is null -> replace by "null"
                        entity.setLabel(String.valueOf(one.getKey()));
                        entity.setValue(String.valueOf(one.getValue()));
                        entity.setPercent(String.valueOf(one.getFrequency()));
                        entity.setIndicator(unit.getIndicator());
                        list.add(entity);
                    }

                    // add Frequency.OTHER
                    entity = new ChartDataEntity();
                    FrequencyIndicator freqIndicator = (FrequencyIndicator) unit.getIndicator();
                    entity.setLabel(FrequencyIndicator.OTHER);
                    entity.setValue(freqIndicator.getCount(FrequencyIndicator.OTHER).toString());
                    entity.setPercent(freqIndicator.getFrequency(FrequencyIndicator.OTHER).toString());
                    entity.setIndicator(freqIndicator);

                    list.add(entity);

                    break;
                case RegexpMatchingIndicatorEnum:
                case SqlPatternMatchingIndicatorEnum:
                    PatternMatchingExt patnExt = (PatternMatchingExt) unit.getValue();
                    PatternMatchingIndicator patternIndicator = (PatternMatchingIndicator) unit.getIndicator();
                    PatternChartDataEntity patternEntity = new PatternChartDataEntity();
                    patternEntity.setLabel(unit.getIndicatorName());
                    patternEntity.setNumMatch(String.valueOf(patnExt.getMatchingValueCount()));
                    patternEntity.setNumNoMatch(String.valueOf(patnExt.getNotMatchingValueCount()));
                    // MOD scorreia 2008-08-22 setValue() because used in thresholds validation
                    Double total = patternIndicator.getMatchingValueCount().doubleValue()
                            + patternIndicator.getNotMatchingValueCount().doubleValue();
                    Double percentMatch = total > 0 ? patternIndicator.getMatchingValueCount().doubleValue() * 100 / total
                            : Double.NaN;
                    patternEntity.setValue(String.valueOf(percentMatch));
                    patternEntity.setIndicator(patternIndicator);
                    list.add(patternEntity);

                    break;

                default:
                    entity = new ChartDataEntity();
                    entity.setLabel(unit.getIndicatorName());
                    entity.setValue(unit.getValue().toString());
                    entity.setIndicator(unit.getIndicator());
                    list.add(entity);
                }
            }
        }

        return list.toArray(new ChartDataEntity[list.size()]);
    }
}
