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
package org.talend.dataprofiler.core.ui.editor.preview.model.states;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.jfree.chart.JFreeChart;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.ICustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerDefaultCategoryDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.entity.TableStructureEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.BaseChartTableLabelProvider;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.CommonContenteProvider;
import org.talend.dq.analysis.explore.DataExplorer;
import org.talend.dq.analysis.explore.PhoneNumbStatisticsExplorer;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class PhoneNumbStatisticsState extends AbstractChartTypeStates {

    /**
     * DOC qiongli PhoneNumbStatisticsState constructor comment.
     * 
     * @param units
     */
    public PhoneNumbStatisticsState(List<IndicatorUnit> units) {
        super(units);
        // TODO Auto-generated constructor stub
    }

    /* (non-Jsdoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getCustomerDataset()
     */
    public ICustomerDataset getCustomerDataset() {

        CustomerDefaultCategoryDataset customerdataset = new CustomerDefaultCategoryDataset();
        for (IndicatorUnit unit : units) {
            final Object unitValue = unit.getValue();
            double value = unitValue != null ? Double.parseDouble(unitValue.toString()) : Double.NaN;
            String label = unit.getIndicatorName();

            customerdataset.addValue(value, label, label); //$NON-NLS-1$

            ChartDataEntity entity = new ChartDataEntity();
            entity.setIndicator(unit.getIndicator());
            entity.setLabel(label);
            entity.setValue(String.valueOf(value));
            entity.setPercent(value / unit.getIndicator().getCount());

            customerdataset.addDataEntity(entity);
        }
        return customerdataset;

    }

    /* (non-Jsdoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getChart()
     */
    public JFreeChart getChart() {
        return TopChartFactory.createBarChart(
                DefaultMessagesImpl.getString("PhoneNumbStatisticsState.PhoneNumbStatistics"), getDataset(), false); //$NON-NLS-1$
    }

    /* (non-Jsdoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getExampleChart()
     */
    public JFreeChart getExampleChart() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Jsdoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getDataExplorer()
     */
    public DataExplorer getDataExplorer() {
        return new PhoneNumbStatisticsExplorer();
    }

    /* (non-Jsdoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getReferenceLink()
     */
    public String getReferenceLink() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Jsdoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.AbstractChartTypeStates#getTableStructure()
     */
    @Override
    protected TableStructureEntity getTableStructure() {
        TableStructureEntity entity = new TableStructureEntity();
        entity.setFieldNames(new String[] {
                DefaultMessagesImpl.getString("PhoneNumbStatisticsState.Count"), DefaultMessagesImpl.getString("PhoneNumbStatisticsState.Count"), "%" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        entity.setFieldWidths(new Integer[] { 200, 150, 150 });
        return entity;
    }

    /* (non-Jsdoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.AbstractChartTypeStates#getLabelProvider()
     */
    @Override
    protected ITableLabelProvider getLabelProvider() {
        return new BaseChartTableLabelProvider();
    }

    /* (non-Jsdoc)
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.AbstractChartTypeStates#getContentProvider()
     */
    @Override
    protected IStructuredContentProvider getContentProvider() {
        return new CommonContenteProvider();
    }

}
