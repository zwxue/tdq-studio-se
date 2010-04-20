// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.ext.FrequencyExt;
import org.talend.dataprofiler.core.ui.editor.preview.model.ICustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerDefaultCategoryDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.entity.TableStructureEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.AbstractChartTypeStates;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.CommonContenteProvider;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.FrequencyLabelProvider;
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
    }

    public JFreeChart getChart() {
        // TODO Auto-generated method stub
        return TopChartFactory.createBarChart(getTitle(), getDataset()); //$NON-NLS-1$
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
                    if ("null".equals(keyLabel)) { //$NON-NLS-1$
                        keyLabel = PluginConstant.NULL_FIELD;
                    }
                    if ("".equals(keyLabel)) { //$NON-NLS-1$
                        keyLabel = PluginConstant.EMPTY_FIELD;
                    }

                    customerdataset.addValue(freqExt.getValue(), "", keyLabel); //$NON-NLS-1$

                    ChartDataEntity entity = new ChartDataEntity();
                    entity.setIndicator(unit.getIndicator());
                    // MOD mzhao feature:6307 display soundex distinct count and real count.
                    entity.setKey(freqExt.getKey());
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

    public JFreeChart getExampleChart() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getReferenceLink() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected TableStructureEntity getTableStructure() {
        TableStructureEntity entity = new TableStructureEntity();
        entity
                .setFieldNames(new String[] {
                        DefaultMessagesImpl.getString("FrequencyTypeStates.value"), DefaultMessagesImpl.getString("FrequencyTypeStates.count"), "%" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        entity.setFieldWidths(new Integer[] { 200, 150, 150 });
        return entity;
    }

    @Override
    protected ITableLabelProvider getLabelProvider() {
        // TODO Auto-generated method stub
        return new FrequencyLabelProvider();
    }

    @Override
    protected IStructuredContentProvider getContentProvider() {
        // TODO Auto-generated method stub
        return new CommonContenteProvider();
    }

    protected abstract void sortIndicator(FrequencyExt[] frequencyExt);

    protected abstract String getTitle();


}
