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
package org.talend.dataprofiler.core.ui.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.xy.DefaultXYZDataset;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.utils.collections.DoubleValueAggregate;
import org.talend.utils.collections.MultiMapHelper;
import org.talend.utils.collections.MultipleKey;
import org.talend.utils.collections.ValueAggregate;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public final class ChartDatasetUtils {

    private static final double OUTLIER_FACTOR = 3;

    private ChartDatasetUtils() {

    }

    public static boolean isDatasetEmpty(CategoryDataset dataset) {
        return dataset.getColumnCount() == 0 && dataset.getRowCount() == 0;
    }

    public static DefaultCategoryDataset createDefaultDataset() {
        return new DefaultCategoryDataset();
    }

    public static DefaultBoxAndWhiskerCategoryDataset createBoxAndWhiskerDataset() {
        return new DefaultBoxAndWhiskerCategoryDataset();
    }

    public static BoxAndWhiskerItem createBoxAndWhiskerItem(Double mean, Double median, Double q1, Double q3,
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

    /**
     * DOC scorreia Comment method "createXYZDatasets".
     * 
     * @param indicator
     * @param numericColumn a numeric column which is in the list of numeric column of the indicator
     * @return a series of datasets for the given numeric column
     */
    public static Map<String, ValueAggregator> createXYZDatasets(ColumnSetMultiValueIndicator indicator, Column numericColumn) {

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

    public static Map<String, DateValueAggregate> createGanttDatasets(ColumnSetMultiValueIndicator indicator, Column dateColumn) {

        final EList<Column> nominalColumns = indicator.getNominalColumns();
        final EList<Column> dateColumns = indicator.getDateColumns();
        final EList<String> dateFunctions = indicator.getDateFunctions();
        final int indexOfDateCol = dateColumns.indexOf(dateColumn);
        assert indexOfDateCol != -1;

        final int nbDateFunctions = dateFunctions.size();
        assert nbDateFunctions >= 2 : DefaultMessagesImpl.getString("ChartDatasetFactory.expect"); //$NON-NLS-1$

        final List<Object[]> listRows = indicator.getListRows();

        final int nbNominalColumns = nominalColumns.size();

        return fillGanttDataset(nominalColumns, listRows, nbNominalColumns + nbDateFunctions * indexOfDateCol);
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
     * 
     */
    private static Map<String, DateValueAggregate> fillGanttDataset(final EList<Column> nominalColumns,
            final List<Object[]> listRows, final int firstNumericColumnIdx) {
        Map<String, DateValueAggregate> valueAggregators = new HashMap<String, DateValueAggregate>();

        int minPos = firstNumericColumnIdx;
        int maxPos = firstNumericColumnIdx + 1;
        for (int i = nominalColumns.size(); i > 0; i--) {
            String key = createKey(nominalColumns, i);
            for (Object[] row : listRows) {
                final Object minObj = row[minPos];
                final Object maxobj = row[maxPos];

                DateValueAggregate valueAggregator = valueAggregators.get(key);
                if (valueAggregator == null) {
                    valueAggregator = new DateValueAggregate();
                    valueAggregators.put(key, valueAggregator);
                }
                MultipleKey multipleKey = new MultipleKey(row, i);
                valueAggregator.addValue(multipleKey, new Date[] { (Date) minObj, (Date) maxobj });
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
                xDouble[i] = doubles[0]; 
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
     * 
     */
    public static class DateValueAggregate<T> extends ValueAggregate<T, Date> {

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
         * 
         */
        public void addValue(T key, Date[] values) {
            Date[] dates = keyToVal.get(key);
            if (dates == null) {
                dates = new Date[values.length];
                Arrays.fill(dates, new Date());
            }

            for (int i = 0; i < values.length; i++) {
                Date d = values[i];
                if (d == null) {
                    nullResults.add(key);
                    return;
                }
                dates[i] = d;
            }
            keyToVal.put(key, dates);
        }

        /**
         * Method "addSeriesToXYZDataset" adds a new series of data to the given dataset.
         * 
         * @param dataset a dataset
         * @param keyOfDataset the series key of the data series
         */
        public void addSeriesToGanttDataset(TaskSeriesCollection ganttDataset, String keyOfDataset) {
            // System.out.println(keyOfDataset);
            TaskSeries series = new TaskSeries(keyOfDataset);
            for (T key : keyToVal.keySet()) {
                final Date[] date = keyToVal.get(key);
                series.add(new Task(((MultipleKey) key).toString(), new SimpleTimePeriod(date[0], date[1])));
                MultiMapHelper.addUniqueObjectToListMap(keyOfDataset, key.toString(), this.seriesKeyToLabel);
            }
            ganttDataset.add(series);
            // dataset.addSeries(keyOfDataset, date);
        }

    }
}
