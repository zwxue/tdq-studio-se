// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.talend.commons.utils.SpecialValueDisplay;
import org.talend.dataprofiler.common.ui.editor.preview.CustomerDefaultCategoryDataset;
import org.talend.dataprofiler.common.ui.editor.preview.ICustomerDataset;
import org.talend.dataprofiler.common.ui.editor.preview.chart.TopChartFactory;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.model.entity.TableStructureEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.CommonContenteProvider;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.FrequencyLabelProvider;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dq.analysis.explore.DataExplorer;
import org.talend.dq.analysis.explore.PieStatisticsExplorer;
import org.talend.dq.indicators.ext.FrequencyExt;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class PieStatisticsState extends AbstractChartTypeStates {

    private String title = PluginConstant.EMPTY_STRING;

    public PieStatisticsState(List<IndicatorUnit> units) {
        super(units);
    }

    public PieStatisticsState(List<IndicatorUnit> units, String title) {
        super(units);
        this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getChart()
     */
    public JFreeChart getChart() {
        return TopChartFactory.createPieChart(getTitle(), getPieDataset(), true, true, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getExampleChart()
     */
    public JFreeChart getExampleChart() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getDataExplorer()
     */
    public DataExplorer getDataExplorer() {
        return new PieStatisticsExplorer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates#getReferenceLink()
     */
    public String getReferenceLink() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.AbstractChartTypeStates#getTableStructure()
     */
    @Override
    protected TableStructureEntity getTableStructure() {
        TableStructureEntity entity = new TableStructureEntity();
        entity.setFieldNames(new String[] {
                DefaultMessagesImpl.getString("FrequencyTypeStates.value"), DefaultMessagesImpl.getString("FrequencyTypeStates.count"), "%" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        entity.setFieldWidths(new Integer[] { 200, 150, 150 });
        return entity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.AbstractChartTypeStates#getLabelProvider()
     */
    @Override
    protected ITableLabelProvider getLabelProvider() {
        return new FrequencyLabelProvider();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.preview.model.states.AbstractChartTypeStates#getContentProvider()
     */
    @Override
    protected IStructuredContentProvider getContentProvider() {
        return new CommonContenteProvider();
    }

    @Override
    public PieDataset getPieDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (IndicatorUnit unit : units) {
            if (unit.isExcuted()) {
                FrequencyExt[] frequencyExt = (FrequencyExt[]) unit.getValue();
                ComparatorsFactory.sort(frequencyExt, ComparatorsFactory.FREQUENCY_COMPARATOR_ID);
                int numOfShown = frequencyExt.length;
                for (int i = 0; i < numOfShown; i++) {
                    FrequencyExt freqExt = frequencyExt[i];
                    String keyLabel = String.valueOf(freqExt.getKey());
                    if ("null".equals(keyLabel)) { //$NON-NLS-1$
                        keyLabel = SpecialValueDisplay.NULL_FIELD;
                    }
                    if (PluginConstant.EMPTY_STRING.equals(keyLabel)) {
                        keyLabel = SpecialValueDisplay.EMPTY_FIELD;
                    }
                    Double percent = freqExt.getFrequency();
                    dataset.setValue(keyLabel, percent);
                }
            }
        }
        return dataset;
    }

    public ICustomerDataset getCustomerDataset() {
        CustomerDefaultCategoryDataset customerdataset = new CustomerDefaultCategoryDataset();

        for (IndicatorUnit unit : units) {
            if (unit.isExcuted()) {
                FrequencyExt[] frequencyExt = (FrequencyExt[]) unit.getValue();

                int numOfShown = frequencyExt.length;
                IndicatorParameters parameters = unit.getIndicator().getParameters();
                if (parameters != null) {
                    if (parameters.getTopN() < numOfShown) {
                        numOfShown = parameters.getTopN();
                    }
                }

                for (int i = 0; i < numOfShown; i++) {
                    FrequencyExt freqExt = frequencyExt[i];
                    String keyLabel = String.valueOf(freqExt.getKey());
                    if ("null".equals(keyLabel)) { //$NON-NLS-1$
                        keyLabel = SpecialValueDisplay.NULL_FIELD;
                    }
                    if (PluginConstant.EMPTY_STRING.equals(keyLabel)) {
                        keyLabel = SpecialValueDisplay.EMPTY_FIELD;
                    }

                    customerdataset.addValue(freqExt.getValue(), PluginConstant.EMPTY_STRING, keyLabel);
                    ChartDataEntity entity = new ChartDataEntity();
                    entity.setIndicator(unit.getIndicator());
                    entity.setKey(freqExt.getKey());
                    entity.setLabelNull(freqExt.getKey() == null);
                    entity.setLabel(keyLabel);
                    entity.setValue(String.valueOf(freqExt.getValue()));

                    Double percent = freqExt.getFrequency();
                    entity.setPercent(percent);

                    customerdataset.addDataEntity(entity);
                }
            }
        }
        return customerdataset;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
