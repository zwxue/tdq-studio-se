// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.events;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.talend.dataprofiler.common.ui.editor.preview.chart.ChartDecorator;
import org.talend.dataprofiler.core.ui.chart.TalendChartComposite;
import org.talend.dataprofiler.core.ui.editor.preview.ColumnIndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerDefaultBAWDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.SummaryStatisticsState;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * For Summary statistics indicators: when select all
 */
public class DynamicBAWChartEventReceiver extends DynamicChartEventReceiver {

    private CustomerDefaultBAWDataset bawDataset;

    private TalendChartComposite BAWparentComposite = null;

    private List<IndicatorUnit> indicators = new ArrayList<IndicatorUnit>();

    Map<IndicatorEnum, Double> summaryValues = new HashMap<IndicatorEnum, Double>();

    public DynamicChartEventReceiver createEventReceiver(IndicatorEnum type, Indicator oneIndicator) {
        // the receiver for each summary indicator, just need to put the indicator name and its related value into the
        // map, and call the method which judge the total
        DynamicChartEventReceiver eReceiver = new DynamicChartEventReceiver() {

            @Override
            public boolean handle(Object value) {
                addToSummaryMap(getIndicatorType(), value);
                return true;
            }
        };
        eReceiver.setIndicatorType(type);
        indicators.add(new ColumnIndicatorUnit(type, oneIndicator, null));
        // oneIndicator.getAnalyzedElement().get
        return eReceiver;
    }

    private void addToSummaryMap(IndicatorEnum indicatorType, Object value) {
        Object indValue = value;
        if (value == null) {
            indValue = 0;
        }
        summaryValues.put(indicatorType, Double.parseDouble(String.valueOf(indValue)));

        refreshChart();
    }

    // judge if all summary indicator has its result, if yes, create a item and refresh the BAW chart
    private void refreshChart() {
        if (summaryValues.size() == SummaryStatisticsState.FULL_CHART) {
            // The BAW chart doesnot support dynamic, so only can create a new one after all finished.
            SummaryStatisticsState state = new SummaryStatisticsState(indicators);
            state.setSqltype(Types.DOUBLE);
            JFreeChart chart = state.getChart();
            ChartDecorator.decorate(chart, null);
            if (BAWparentComposite != null) {
                BAWparentComposite.setChart(chart);
                BAWparentComposite.forceRedraw();
            }

            EventManager.getInstance().publish(chartComposite, EventEnum.DQ_DYNAMIC_REFRESH_DYNAMIC_CHART, null);

            // reset the values
            summaryValues.clear();
            indicators.clear();
        }
    }

    @Override
    public boolean handle(Object value) {
        return true;
    }

    @Override
    public void clearValue() {
        if (bawDataset != null && bawDataset.getRowCount() == 7) {
            for (int i = 0; i < 7; i++) {
                bawDataset.remove(String.valueOf(i), "");//$NON-NLS-1$ 
            }
        }
    }

    public CustomerDefaultBAWDataset getBawDataset() {
        return bawDataset;
    }

    public void setBawDataset(CustomerDefaultBAWDataset bawDataset) {
        this.bawDataset = bawDataset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.events.DynamicChartEventReceiver#clear()
     */
    @Override
    public void clear() {
        super.clear();
        // reset the values
        summaryValues.clear();
        indicators.clear();
        BAWparentComposite = null;
        bawDataset = null;
    }

    /**
     * Sets the bAWparentComposite.
     * 
     * @param bAWparentComposite the bAWparentComposite to set
     */
    public void setBAWparentComposite(TalendChartComposite bAWparentComposite) {
        this.BAWparentComposite = bAWparentComposite;
    }
}
