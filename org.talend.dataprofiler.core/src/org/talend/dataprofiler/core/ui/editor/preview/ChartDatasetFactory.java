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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.xy.DefaultXYZDataset;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.ext.FrequencyExt;
import org.talend.dataprofiler.core.ui.editor.preview.ext.PatternMatchingExt;
import org.talend.dataprofiler.core.ui.editor.preview.model.CustomerBoxDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.CustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.IDataEntity;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dq.indicators.preview.EIndicatorChartType;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.indicators.preview.table.PatternChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.collections.DoubleValueAggregate;
import org.talend.utils.collections.MultiMapHelper;
import org.talend.utils.collections.MultipleKey;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartDatasetFactory {

    private static final double OUTLIER_FACTOR = 3;

    static IDataEntity createDataset(EIndicatorChartType chartType, List<IndicatorUnit> indicatorUnitList) {

        CustomerDataset dataset = new CustomerDataset();

        for (int index = 0; index < indicatorUnitList.size(); index++) {
            IndicatorUnit unit = indicatorUnitList.get(index);
            IndicatorCommonUtil.compositeIndicatorMap(unit);
        }

        switch (chartType) {
        case FREQUENCE_STATISTICS:
        case LOW_FREQUENCE_STATISTICS:
        case PATTERN_FREQUENCE_STATISTICS:
        case PATTERN_LOW_FREQUENCE_STATISTICS:
            for (IndicatorUnit unit : indicatorUnitList) {
                if (unit.isExcuted()) {
                    FrequencyExt[] frequencyExt = (FrequencyExt[]) unit.getValue();

                    if (chartType == EIndicatorChartType.FREQUENCE_STATISTICS
                            || chartType == EIndicatorChartType.PATTERN_FREQUENCE_STATISTICS) {

                        ComparatorsFactory.sort(frequencyExt, ComparatorsFactory.FREQUENCY_COMPARATOR_ID);
                    }

                    if (chartType == EIndicatorChartType.LOW_FREQUENCE_STATISTICS
                            || chartType == EIndicatorChartType.PATTERN_LOW_FREQUENCE_STATISTICS) {

                        ComparatorsFactory.sort(frequencyExt, ComparatorsFactory.LOW_FREQUENCY_COMPARATOR_ID);
                    }

                    int numOfShown = frequencyExt.length;
                    IndicatorParameters parameters = unit.getIndicator().getParameters();
                    if (parameters != null) {
                        if (parameters.getTopN() < frequencyExt.length) {
                            numOfShown = parameters.getTopN();
                        }
                    }

                    for (int i = 0; i < numOfShown; i++) {
                        final FrequencyExt freqExt = frequencyExt[i];
                        final String keyLabel = String.valueOf(freqExt.getKey());
                        dataset.addValue(freqExt.getValue(), "", keyLabel); //$NON-NLS-1$

                        ChartDataEntity entity = new ChartDataEntity();
                        entity.setIndicator(unit.getIndicator());

                        entity.setLabelNull(freqExt.getKey() == null);
                        entity.setLabel(keyLabel);
                        entity.setValue(String.valueOf(freqExt.getValue()));
                        entity.setPercent(String.valueOf(freqExt.getFrequency()));

                        dataset.addDataEntity(entity);
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

                    dataset.addValue(machCount, "matching", label); //$NON-NLS-1$
                    dataset.addValue(notMathCount, "not matching", label); //$NON-NLS-1$

                    PatternChartDataEntity patternEntity = new PatternChartDataEntity();
                    patternEntity.setIndicator(unit.getIndicator());
                    patternEntity.setLabel(unit.getIndicatorName());
                    patternEntity.setNumMatch(String.valueOf(machCount));
                    patternEntity.setNumNoMatch(String.valueOf(notMathCount));

                    dataset.addDataEntity(patternEntity);
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

                    dataset.addValue(value, label, ""); //$NON-NLS-1$

                    ChartDataEntity entity = new ChartDataEntity();
                    entity.setIndicator(unit.getIndicator());
                    entity.setLabel(label);
                    entity.setValue(String.valueOf(value));
                    entity.setPercent(String.valueOf(value / unit.getIndicator().getCount()));

                    dataset.addDataEntity(entity);
                }
            }
            break;
        case MODE_INDICATOR:
            for (IndicatorUnit unit : indicatorUnitList) {
                if (unit.isExcuted()) {
                    String label = unit.getIndicatorName();

                    ChartDataEntity entity = new ChartDataEntity();
                    entity.setIndicator(unit.getIndicator());
                    entity.setLabel(label);
                    entity.setValue(unit.getValue().toString());

                    dataset.addDataEntity(entity);
                }
            }
            break;
        case SUMMARY_STATISTICS:
            CustomerBoxDataset defaultDataset = new CustomerBoxDataset();
            Map<IndicatorEnum, Double> map = new HashMap<IndicatorEnum, Double>();

            for (IndicatorUnit unit : indicatorUnitList) {
                if (unit.isExcuted()) {
                    double doubleValue = Double.parseDouble(unit.getValue().toString());
                    map.put(unit.getType(), doubleValue);

                    ChartDataEntity entity = new ChartDataEntity();
                    entity.setIndicator(unit.getIndicator());
                    entity.setLabel(unit.getIndicatorName());
                    entity.setValue(String.valueOf(unit.getValue()));

                    defaultDataset.addDataEntity(entity);
                    dataset.addDataEntity(entity);
                }
            }

            // add more data entity for sumary
            if (map.containsKey(IndicatorEnum.MaxValueIndicatorEnum) && map.containsKey(IndicatorEnum.MinValueIndicatorEnum)) {
                Double range = map.get(IndicatorEnum.MaxValueIndicatorEnum) - map.get(IndicatorEnum.MinValueIndicatorEnum);
                ChartDataEntity entity = new ChartDataEntity(null, IndicatorEnum.RangeIndicatorEnum.getLabel(), range.toString());
                defaultDataset.addDataEntity(entity);
                dataset.addDataEntity(entity);
            }

            if (map.containsKey(IndicatorEnum.UpperQuartileIndicatorEnum)
                    && map.containsKey(IndicatorEnum.LowerQuartileIndicatorEnum)) {
                Double quartile = map.get(IndicatorEnum.UpperQuartileIndicatorEnum)
                        - map.get(IndicatorEnum.LowerQuartileIndicatorEnum);
                ChartDataEntity entity = new ChartDataEntity(null, IndicatorEnum.IQRIndicatorEnum.getLabel(), quartile.toString());
                defaultDataset.addDataEntity(entity);
                dataset.addDataEntity(entity);
            }

            if (map.size() == 6) {

                dataset = null;

                BoxAndWhiskerItem item = createBoxAndWhiskerItem(map.get(IndicatorEnum.MeanIndicatorEnum), map
                        .get(IndicatorEnum.MedianIndicatorEnum), map.get(IndicatorEnum.LowerQuartileIndicatorEnum), map
                        .get(IndicatorEnum.UpperQuartileIndicatorEnum), map.get(IndicatorEnum.MinValueIndicatorEnum), map
                        .get(IndicatorEnum.MaxValueIndicatorEnum), null);

                defaultDataset.add(item, "0", ""); //$NON-NLS-1$ //$NON-NLS-2$

                List zerolist = new ArrayList();
                defaultDataset.add(zerolist, "1", "");
                defaultDataset.add(zerolist, "2", "");
                defaultDataset.add(zerolist, "3", "");
                defaultDataset.add(zerolist, "4", "");
                defaultDataset.add(zerolist, "5", "");
                defaultDataset.add(zerolist, "6", "");

                return defaultDataset;

            } else {
                defaultDataset = null;

                for (IndicatorEnum indicatorEnum : map.keySet()) {
                    dataset.addValue(map.get(indicatorEnum), "", indicatorEnum.getLabel()); //$NON-NLS-1$
                }
            }

            break;
        default:

            return null;
        }

        return dataset;
    }

    static IDataEntity createExampleDataset(EIndicatorChartType chartType) {
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

        return null;
    }

    /**
     * DOC scorreia Comment method "createXYZDatasets".
     * 
     * @param indicator
     * @param numericColumn a numeric column which is in the list of numeric column of the indicator
     * @return a series of datasets for the given numeric column
     */
    static Map<String, ValueAggregator> createXYZDatasets(ColumnSetMultiValueIndicator indicator, Column numericColumn) {

        final EList<Column> nominalColumns = indicator.getNominalColumns();
        final EList<Column> numericColumns = indicator.getNumericColumns();
        final EList<String> numericFunctions = indicator.getNumericFunctions();

        final int indexOfNumericCol = numericColumns.indexOf(numericColumn);
        assert indexOfNumericCol != -1;

        final int nbNumericFunctions = numericFunctions.size();
        assert nbNumericFunctions == 3 : DefaultMessagesImpl.getString("ChartDatasetFactory.expect"); //$NON-NLS-1$

        final List<Object[]> listRows = indicator.getListRows();

        final int nbNominalColumns = nominalColumns.size();

        return fillDataset(nominalColumns, listRows, nbNominalColumns + indexOfNumericCol);
    }

    /**
     * DOC scorreia ValueAggregator class global comment. Detailled comment
     */
    public static class ValueAggregator extends DoubleValueAggregate<MultipleKey> {

        private Map<String, List<String>> seriesKeyToLabel = new HashMap<String, List<String>>();

        /**
         * Method "getLabels". Must not be called before the {@link #addSeriesToXYZDataset(DefaultXYZDataset, String)}
         * method.
         * 
         * @param seriesKey
         * @return the label for each item of the dataset
         */
        public List<String> getLabels(String seriesKey) {
            return seriesKeyToLabel.get(seriesKey);
        }

        /**
         * Method "addSeriesToXYZDataset" adds a new series of data to the given dataset.
         * 
         * @param dataset a dataset
         * @param keyOfDataset the series key of the data series
         */
        public void addSeriesToXYZDataset(DefaultXYZDataset dataset, String keyOfDataset) {

            final int size = keyToVal.size();
            double[] xDouble = new double[size];
            double[] yDouble = new double[size];
            double[] zDouble = new double[size];

            int i = 0;
            for (MultipleKey key : keyToVal.keySet()) {
                final Double[] doubles = keyToVal.get(key);
                xDouble[i] = doubles[0] / doubles[1]; // FIXME how to know this is the avg !!
                yDouble[i] = doubles[1];
                zDouble[i] = doubles[2];
                MultiMapHelper.addUniqueObjectToListMap(keyOfDataset, key.toString(), this.seriesKeyToLabel);
                i++;
            }

            double[][] data = new double[][] { xDouble, yDouble, zDouble };
            // System.out.println(Arrays.deepToString(data));
            dataset.addSeries(keyOfDataset, data);
        }

    }

    /**
     * DOC scorreia Comment method "fillDataset".
     * 
     * @param nominalColumns
     * @param listRows
     * @param firstNumericColumnIdx
     * @param dataset
     */
    private static Map<String, ValueAggregator> fillDataset(final EList<Column> nominalColumns, final List<Object[]> listRows,
            final int firstNumericColumnIdx) {
        Map<String, ValueAggregator> valueAggregators = new HashMap<String, ValueAggregator>();

        int xPos = firstNumericColumnIdx;
        int yPos = firstNumericColumnIdx + 1;
        int zPos = firstNumericColumnIdx + 2;

        for (int i = nominalColumns.size(); i > 0; i--) {
            String key = createKey(nominalColumns, i);
            for (Object[] row : listRows) {
                
                final Object xobj = row[xPos];
                final Double xValue = xobj != null ? Double.valueOf(String.valueOf(xobj)) : null;
                final Object yobj = row[yPos];
                final Double yValue = yobj != null ? Double.valueOf(String.valueOf(yobj)) : null;
                final Object zobj = row[zPos];
                final Double zValue = zobj != null ? Double.valueOf(String.valueOf(zobj)) : null;

                ValueAggregator valueAggregator = valueAggregators.get(key);
                if (valueAggregator == null) {
                    valueAggregator = new ValueAggregator();
                    valueAggregators.put(key, valueAggregator);
                }
                MultipleKey multipleKey = new MultipleKey(row, i);
                valueAggregator.addValue(multipleKey, new Double[] { xValue, yValue, zValue });
            }

        }

        return valueAggregators;

    }

    /**
     * DOC scorreia Comment method "createKey".
     * 
     * @param nominalColumns
     * @return
     */
    private static String createKey(EList<Column> nominalColumns, int idx) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < idx; i++) {
            builder.append(nominalColumns.get(i).getName()).append(" "); //$NON-NLS-1$
        }
        return builder.toString();
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
