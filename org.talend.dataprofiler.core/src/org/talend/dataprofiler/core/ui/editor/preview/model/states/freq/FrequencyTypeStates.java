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
package org.talend.dataprofiler.core.ui.editor.preview.model.states.freq;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.jfree.chart.JFreeChart;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.ext.FrequencyExt;
import org.talend.dataprofiler.core.ui.editor.preview.model.ICustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerDefaultCategoryDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.entity.TableStructureEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.AbstractChartTypeStates;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderFactory.BaseChartTableLabelProvider;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderFactory.CommonContenteProvider;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dq.analysis.explore.DataExplorer;
import org.talend.dq.analysis.explore.FrequencyStatisticsExplorer;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public abstract class FrequencyTypeStates extends AbstractChartTypeStates {

    public FrequencyTypeStates(List<IndicatorUnit> units) {
        super(units);
        // TODO Auto-generated constructor stub
    }

    public JFreeChart getChart() {
        // TODO Auto-generated method stub
        return TopChartFactory.createBarChart("Freqyebct Statistics", getDataset());
    }

    public ICustomerDataset getCustomerDataset() {
        CustomerDefaultCategoryDataset customerdataset = new CustomerDefaultCategoryDataset();

        for (IndicatorUnit unit : units) {
            if (unit.isExcuted()) {
                FrequencyExt[] frequencyExt = (FrequencyExt[]) unit.getValue();

                sortIndicator(frequencyExt);

                int numOfShown = frequencyExt.length;
                IndicatorParameters parameters = unit.getIndicator().getParameters();
                if (parameters != null) {
                    if (parameters.getTopN() < frequencyExt.length) {
                        numOfShown = parameters.getTopN();
                    }
                }

                for (int i = 0; i < numOfShown; i++) {
                    FrequencyExt freqExt = frequencyExt[i];
                    String keyLabel = String.valueOf(freqExt.getKey());
                    customerdataset.addValue(freqExt.getValue(), "", keyLabel);

                    ChartDataEntity entity = new ChartDataEntity();
                    entity.setIndicator(unit.getIndicator());

                    entity.setLabelNull(freqExt.getKey() == null);
                    entity.setLabel(keyLabel);
                    entity.setValue(String.valueOf(freqExt.getValue()));
                    entity.setPercent(String.valueOf(freqExt.getFrequency()));

                    customerdataset.addDataEntity(entity);
                }
            }
        }
        return customerdataset;
    }

    public DataExplorer getDataExplorer() {
        // TODO Auto-generated method stub
        return new FrequencyStatisticsExplorer();
    }

    public JFreeChart getExampleChart() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected TableStructureEntity getTableStructure() {
        TableStructureEntity entity = new TableStructureEntity();
        entity.setFieldNames(new String[] { "value", "count", "%" });
        entity.setFieldWidths(new Integer[] { 200, 150, 150 });
        return entity;
    }

    @Override
    protected ITableLabelProvider getLabelProvider() {
        // TODO Auto-generated method stub
        return new BaseChartTableLabelProvider();
    }

    @Override
    protected IStructuredContentProvider getContentProvider() {
        // TODO Auto-generated method stub
        return new CommonContenteProvider();
    }

    protected abstract void sortIndicator(FrequencyExt[] frequencyExt);
}
