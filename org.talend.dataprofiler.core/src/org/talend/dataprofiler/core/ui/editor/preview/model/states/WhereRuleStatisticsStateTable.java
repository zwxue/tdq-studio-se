// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.TableIndicator;
import org.talend.dataprofiler.core.ui.chart.ChartDecorator;
import org.talend.dataprofiler.core.ui.editor.preview.TableIndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.ICustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerDefaultCategoryDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerXYSeriesCollection;
import org.talend.dataprofiler.core.ui.editor.preview.model.entity.TableStructureEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.CommonContenteProvider;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.PatternLabelProvider;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dq.analysis.explore.DQRuleExplorer;
import org.talend.dq.analysis.explore.DataExplorer;
import org.talend.dq.indicators.preview.table.WhereRuleChartDataEntity;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class WhereRuleStatisticsStateTable extends AbstractChartTypeStatesTable {

    private static final String ROW_KEY_PASS = DefaultMessagesImpl.getString("WhereRuleStatisticsStateTable.match"); //$NON-NLS-1$

    private static final String ROW_KEY_NOT_PASS = DefaultMessagesImpl.getString("WhereRuleStatisticsStateTable.notMatch"); //$NON-NLS-1$

    private TableIndicator tableIndicator;

    private double rowCount;

    public TableIndicator getTableIndicator() {
        return tableIndicator;
    }

    public double getRowCount() {
        return rowCount;
    }

    public WhereRuleStatisticsStateTable(List<TableIndicatorUnit> units, TableIndicator tableIndicator) {
        super(units);
        this.tableIndicator = tableIndicator;
        this.rowCount = initRowCount(tableIndicator);
    }

    @Override
    public JFreeChart getChart() {
        List<JFreeChart> chartList = getChartList();
        if (chartList != null && chartList.size() > 0) {
            return chartList.get(0);
        }
        return null;
    }

    private double initRowCount(TableIndicator tableIndicator) {
        double rowCount = 0;
        if (tableIndicator != null) {
            TableIndicatorUnit[] tius = tableIndicator.getIndicatorUnits();
            for (TableIndicatorUnit tiu : tius) {
                if (tiu.getIndicator() instanceof RowCountIndicator) {
                    RowCountIndicator rci = (RowCountIndicator) tiu.getIndicator();
                    rowCount = rci.getCount();
                    break;
                }
            }
        }
        return rowCount;
    }

    public ICustomerDataset getCustomerDataset() {
        CustomerDefaultCategoryDataset customerdataset = new CustomerDefaultCategoryDataset();
        for (TableIndicatorUnit unit : units) {
            String columnKey = unit.getIndicatorName();
            double value = Double.parseDouble(unit.getValue().toString());
            customerdataset.addValue(getRowCount() - value, ROW_KEY_NOT_PASS, columnKey);
            customerdataset.addValue(value, ROW_KEY_PASS, columnKey);

            WhereRuleChartDataEntity entity = new WhereRuleChartDataEntity();
            entity.setRowCount(getRowCount());
            entity.setIndicator(unit.getIndicator());
            entity.setLabel(columnKey);
            entity.setNumMatch(String.valueOf(value));
            entity.setNumNoMatch(String.valueOf(getRowCount() - value));

            customerdataset.addDataEntity(entity);
        }
        return customerdataset;
    }

    @Override
    public ICustomerDataset getCustomerXYDataset() {
        final CustomerXYSeriesCollection dataset = new CustomerXYSeriesCollection();
        final XYSeries series = new XYSeries("Rules");
        int x = 0;
        for (TableIndicatorUnit unit : units) {
            x++;
            double y = 100 * (1 - (Double.parseDouble(unit.getValue().toString()) / getRowCount()));
            series.add(x, y);
            dataset.addSeries(series);
        }
        return dataset;
    }

    public DataExplorer getDataExplorer() {
        return new DQRuleExplorer();
    }

    public JFreeChart getExampleChart() {
        return null;
    }

    @Override
    protected TableStructureEntity getTableStructure() {
        TableStructureEntity entity = new TableStructureEntity();
        entity
                .setFieldNames(new String[] {
                        DefaultMessagesImpl.getString("WhereRuleStatisticsStateTable.Label"), DefaultMessagesImpl.getString("WhereRuleStatisticsStateTable.Match"), DefaultMessagesImpl.getString("WhereRuleStatisticsStateTable.NoMatch"), DefaultMessagesImpl.getString("WhereRuleStatisticsStateTable.Match_"), DefaultMessagesImpl.getString("WhereRuleStatisticsStateTable.NoMatch_") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        entity.setFieldWidths(new Integer[] { 200, 75, 75, 75, 75 });
        return entity;
    }

    @Override
    protected ITableLabelProvider getLabelProvider() {
        return new PatternLabelProvider();
    }

    @Override
    protected IStructuredContentProvider getContentProvider() {
        return new CommonContenteProvider();
    }

    public String getReferenceLink() {
        return null;
    }

    @Override
    public List<JFreeChart> getChartList() {
        List<JFreeChart> ret = new ArrayList<JFreeChart>();
        JFreeChart stackChart = TopChartFactory.createStackedBarChart(DefaultMessagesImpl
                .getString("WhereRuleStatisticsStateTable.WhereRuleStatistics"), getDataset(), true); //$NON-NLS-1$
        ChartDecorator.decorate(stackChart);
        ret.add(stackChart); //$NON-NLS-1$
        if (false) { // show line chart only in TDQ!!!
            JFreeChart lineChart = TopChartFactory.createLineChart(DefaultMessagesImpl
                    .getString("WhereRuleStatisticsStateTable.WhereRuleStatistics"), getXYDataset(), false); //$NON-NLS-1$
            ChartDecorator.decorate(lineChart);
            ret.add(lineChart); //$NON-NLS-1$
        }
        return ret;
    }
}
