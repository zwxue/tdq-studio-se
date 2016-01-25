// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.composite.chart;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.dataprofiler.common.ui.editor.preview.chart.TopChartFactory;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;

/**
 * DOC yyi class global comment. Detailled comment
 */
public class MatchRuleDataChart extends Composite {

    public static final int CHART_STANDARD_WIDHT = 600;

    public static final int CHART_STANDARD_HEIGHT = 275;

    private ChartComposite jfreeChartComp;

    private int times = 0;

    private Map<Object, Long> groupSize2GroupFrequency = null;

    /**
     * DOC yyi DataChart constructor comment.
     * 
     * @param parent
     * @param style
     */
    public MatchRuleDataChart(Composite parent, Map<Object, Long> groupSize2GroupFrequency) {
        super(parent, SWT.NONE);
        this.groupSize2GroupFrequency = groupSize2GroupFrequency;
        this.setLayout(new FillLayout());
        // make the size of the chart full fill the area
        if (this.getParent().getLayout() instanceof GridLayout) {
            GridData data = new GridData(GridData.FILL_BOTH);
            this.setLayoutData(data);
        }

        initChartData();
    }

    public boolean initChartData() {
        if (this.jfreeChartComp == null) {
            createChart();
            return true;
        }
        return false;
    }

    /**
     * DOC yyi Comment method "createChart".
     */
    private void createChart() {
        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(new GridLayout(1, true));
        JFreeChart jfreechart = createChart(createDataset());
        this.jfreeChartComp = new ChartComposite(composite, SWT.NONE, jfreechart, true);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = CHART_STANDARD_WIDHT;
        gd.heightHint = CHART_STANDARD_HEIGHT;
        this.jfreeChartComp.setLayoutData(gd);

    }

    private JFreeChart createChart(CategoryDataset categorydataset) {
        JFreeChart localJFreeChart = TopChartFactory.createMatchRuleBarChart(null, DefaultMessagesImpl.getString("DataChart.0"), //$NON-NLS-1$
                "#group", categorydataset, PlotOrientation.VERTICAL, false, true, false); //$NON-NLS-1$
        return localJFreeChart;
    }

    protected double sumItemCount(CategoryDataset categorydataset) {
        double itemCount = 0;
        for (int i = 0; i < categorydataset.getColumnCount(); i++) {
            int columnKey = Integer.valueOf(categorydataset.getColumnKey(i).toString());
            itemCount += categorydataset.getValue(0, i).intValue() * columnKey;
        }
        return itemCount;
    }

    protected double sumGroupCount(CategoryDataset categorydataset) {
        double groupCount = 0.0;
        for (int i = 0; i < categorydataset.getColumnCount(); i++) {
            groupCount += categorydataset.getValue(0, i).doubleValue();
        }
        return groupCount;
    }

    private CategoryDataset createDataset() {
        String s = DefaultMessagesImpl.getString("DataChart.4"); //$NON-NLS-1$
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();

        // sort dataset
        if (groupSize2GroupFrequency == null) {
            return defaultcategorydataset;
        }
        String[] array = groupSize2GroupFrequency.keySet().toArray(new String[0]);
        List<String> groups = Arrays.asList(array);
        Collections.sort(groups, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                // return groupCounts.get(o2).compareTo(groupCounts.get(o1));
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            }
        });
        for (String count : groups) {
            if (Integer.parseInt(count) > times - 1) {
                defaultcategorydataset.addValue(groupSize2GroupFrequency.get(count), s, count);
            }
        }
        return defaultcategorydataset;
    }

    public ChartComposite getChartComp() {
        return jfreeChartComp;
    }

    public void refresh(Map<Object, Long> groupSize2GroupFrequencyNew) {
        this.groupSize2GroupFrequency = groupSize2GroupFrequencyNew;
        refresh();
    }

    /**
     * refresh the chart by old data
     * 
     */
    public void refresh() {
        initChartData();
        jfreeChartComp.setChart(createChart(createDataset()));
        jfreeChartComp.forceRedraw();
    }

    /**
     * DOC set spinner value
     * 
     * @param times
     */
    public void setTimes(int times) {
        this.times = times;
    }

    public int getTimes() {
        return times;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (jfreeChartComp != null) {
            jfreeChartComp.dispose();
        }
    }

    /**
     * Getter for groupSize2GroupFrequency.
     * 
     * @return the groupSize2GroupFrequency
     */
    public Map<Object, Long> getGroupSize2GroupFrequency() {
        return this.groupSize2GroupFrequency;
    }

    /**
     * 
     * create a chart with empty dataset,so as to clear the blocking key chart.
     */
    public void clearChart() {
        JFreeChart jfreechart = createChart(new DefaultCategoryDataset());
        jfreeChartComp.setChart(jfreechart);
        jfreeChartComp.forceRedraw();
    }
}
