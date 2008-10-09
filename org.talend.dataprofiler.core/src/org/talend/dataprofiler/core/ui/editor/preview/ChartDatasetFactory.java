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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.editor.preview.ext.FrequencyExt;
import org.talend.dataprofiler.core.ui.editor.preview.ext.PatternMatchingExt;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataquality.indicators.IndicatorParameters;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartDatasetFactory {

    private static final double OUTLIER_FACTOR = 3;

    static CategoryDataset createDataset(EIndicatorChartType chartType, List<IndicatorUnit> indicatorUnitList) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int index = 0; index < indicatorUnitList.size(); index++) {
            IndicatorUnit unit = indicatorUnitList.get(index);
            IndicatorCommonUtil.compositeIndicatorMap(unit);
        }

        switch (chartType) {
        case FREQUENCE_STATISTICS:
            for (IndicatorUnit unit : indicatorUnitList) {
                if (unit.isExcuted()) {
                    FrequencyExt[] frequencyExt = (FrequencyExt[]) unit.getValue();
                    Arrays.sort(frequencyExt);

                    int numOfShown = frequencyExt.length;
                    IndicatorParameters parameters = unit.getIndicator().getParameters();
                    if (parameters != null) {
                        if (parameters.getTopN() < frequencyExt.length) {
                            numOfShown = parameters.getTopN();
                        }
                    }

                    for (int i = 0; i < numOfShown; i++) {
                        dataset.addValue(frequencyExt[i].getValue(), "", String.valueOf(frequencyExt[i].getKey()));
                    }
                }
            }
            break;

        case SQL_PATTERN_MATCHING:
        case PATTERN_MATCHING:
            for (IndicatorUnit unit : indicatorUnitList) {
                if (unit.isExcuted()) {
                    String label = unit.getIndicatorName();
                    PatternMatchingExt patternExt = (PatternMatchingExt) unit.getValue();
                    double notMathCount = patternExt.getNotMatchingValueCount();
                    double machCount = patternExt.getMatchingValueCount();

                    dataset.addValue(machCount, "matching", label);
                    dataset.addValue(notMathCount, "not matching", label);
                }
            }
            break;

        case TEXT_STATISTICS:
            ComparatorsFactory.sort(indicatorUnitList, ComparatorsFactory.TEXT_STATISTICS_COMPARATOR_ID);
        case SIMPLE_STATISTICS:
            for (IndicatorUnit unit : indicatorUnitList) {
                if (unit.isExcuted()) {
                    double value = Double.parseDouble(unit.getValue().toString());
                    String label = unit.getIndicatorName();
                    // renderer3d.setSeriesPaint(index, unit.getColor());
                    dataset.addValue(value, label, "");
                }
            }
            break;
        case SUMMARY_STATISTICS:
            DefaultBoxAndWhiskerCategoryDataset defaultDataset = new DefaultBoxAndWhiskerCategoryDataset();
            Map<IndicatorEnum, Double> map = new HashMap<IndicatorEnum, Double>();

            for (IndicatorUnit unit : indicatorUnitList) {
                if (unit.isExcuted()) {
                    double doubleValue = Double.parseDouble(unit.getValue().toString());
                    map.put(unit.getType(), doubleValue);

                }
            }

            if (map.size() != 6) {

                for (IndicatorEnum indicatorEnum : map.keySet()) {
                    dataset.addValue(map.get(indicatorEnum), "", indicatorEnum.getLabel());
                }

                // renderer3d.setSeriesPaint(0, Color.RED);
                break;
            } else {
                BoxAndWhiskerItem item = createBoxAndWhiskerItem(map.get(IndicatorEnum.MeanIndicatorEnum), map
                        .get(IndicatorEnum.MedianIndicatorEnum), map.get(IndicatorEnum.LowerQuartileIndicatorEnum), map
                        .get(IndicatorEnum.UpperQuartileIndicatorEnum), map.get(IndicatorEnum.MinValueIndicatorEnum), map
                        .get(IndicatorEnum.MaxValueIndicatorEnum), null);

                defaultDataset.add(item, "", "");

                return defaultDataset;
            }

        default:

            return null;
        }

        return dataset;
    }

    static CategoryDataset createExampleDataset(EIndicatorChartType chartType) {
        CategoryDataset dataset = new DefaultCategoryDataset();

        switch (chartType) {
        case FREQUENCE_STATISTICS:
            break;
        case MODE_INDICATOR:
            break;
        case PATTERN_MATCHING:
            break;
        case SQL_PATTERN_MATCHING:
            break;
        case SIMPLE_STATISTICS:
            break;
        case SUMMARY_STATISTICS:
            break;
        case TEXT_STATISTICS:
            break;
        default:
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
}
