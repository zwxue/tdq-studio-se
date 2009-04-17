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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.ICustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerDefaultBAWDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerDefaultCategoryDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.entity.TableStructureEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.CommonContenteProvider;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.SummaryLabelProvider;
import org.talend.dataprofiler.core.ui.utils.ChartDatasetUtils;
import org.talend.dq.analysis.explore.DataExplorer;
import org.talend.dq.analysis.explore.SummaryStastictisExplorer;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class SummaryStatisticsState extends AbstractChartTypeStates {

    public static final int FULL_FLAG = 6;

    private int sqltype;

    public SummaryStatisticsState(List<IndicatorUnit> units) {
        super(units);

        if (units != null && !units.isEmpty()) {
            sqltype = units.get(0).getParentColumn().getTdColumn().getJavaType();
        }
    }

    public JFreeChart getChart() {
        if (Java2SqlType.isDateInSQL(sqltype)) {
            return null;
        } else {
            if (isIntact()) {
                BoxAndWhiskerCategoryDataset dataset = (BoxAndWhiskerCategoryDataset) getDataset();
                return TopChartFactory.createBoxAndWhiskerChart(DefaultMessagesImpl
                        .getString("SummaryStatisticsState.SummaryStatistics"), dataset); //$NON-NLS-1$
            } else {
                return TopChartFactory.createBarChart(
                        DefaultMessagesImpl.getString("SummaryStatisticsState.Summary_Statistics"), getDataset(), false); //$NON-NLS-1$
            }
        }
    }

    public ICustomerDataset getCustomerDataset() {
        Map<IndicatorEnum, Double> map = new HashMap<IndicatorEnum, Double>();
        CustomerDefaultCategoryDataset customerdataset = new CustomerDefaultCategoryDataset();

        for (IndicatorUnit unit : units) {
            if (Java2SqlType.isNumbericInSQL(sqltype)) {
                String value = unit.getValue().toString();
                map.put(unit.getType(), Double.parseDouble(value));
            }

            ChartDataEntity entity = new ChartDataEntity();
            entity.setIndicator(unit.getIndicator());
            entity.setLabel(unit.getIndicatorName());
            entity.setValue(String.valueOf(unit.getValue()));

            customerdataset.addDataEntity(entity);
        }

        // add more data entity for summary
        if (map.containsKey(IndicatorEnum.MaxValueIndicatorEnum) && map.containsKey(IndicatorEnum.MinValueIndicatorEnum)) {
            Double range = map.get(IndicatorEnum.MaxValueIndicatorEnum) - map.get(IndicatorEnum.MinValueIndicatorEnum);
            ChartDataEntity entity = new ChartDataEntity(null, IndicatorEnum.RangeIndicatorEnum.getLabel(), range.toString());
            customerdataset.addDataEntity(entity);
        }

        if (map.containsKey(IndicatorEnum.UpperQuartileIndicatorEnum)
                && map.containsKey(IndicatorEnum.LowerQuartileIndicatorEnum)) {
            Double quartile = map.get(IndicatorEnum.UpperQuartileIndicatorEnum)
                    - map.get(IndicatorEnum.LowerQuartileIndicatorEnum);
            ChartDataEntity entity = new ChartDataEntity(null, IndicatorEnum.IQRIndicatorEnum.getLabel(), quartile.toString());
            customerdataset.addDataEntity(entity);
        }

        if (isIntact()) {
            CustomerDefaultBAWDataset dataset = new CustomerDefaultBAWDataset();
            BoxAndWhiskerItem item = ChartDatasetUtils.createBoxAndWhiskerItem(map.get(IndicatorEnum.MeanIndicatorEnum), map
                    .get(IndicatorEnum.MedianIndicatorEnum), map.get(IndicatorEnum.LowerQuartileIndicatorEnum), map
                    .get(IndicatorEnum.UpperQuartileIndicatorEnum), map.get(IndicatorEnum.MinValueIndicatorEnum), map
                    .get(IndicatorEnum.MaxValueIndicatorEnum), null);

            dataset.add(item, "0", ""); //$NON-NLS-1$ //$NON-NLS-2$

            List zerolist = new ArrayList();
            dataset.add(zerolist, "1", ""); //$NON-NLS-1$ //$NON-NLS-2$
            dataset.add(zerolist, "2", ""); //$NON-NLS-1$ //$NON-NLS-2$
            dataset.add(zerolist, "3", ""); //$NON-NLS-1$ //$NON-NLS-2$
            dataset.add(zerolist, "4", ""); //$NON-NLS-1$ //$NON-NLS-2$
            dataset.add(zerolist, "5", ""); //$NON-NLS-1$ //$NON-NLS-2$
            dataset.add(zerolist, "6", ""); //$NON-NLS-1$ //$NON-NLS-2$

            dataset.addDataEntity(customerdataset.getDataEntities());
            return dataset;
        } else {
            for (IndicatorEnum indicatorEnum : map.keySet()) {
                customerdataset.addValue(map.get(indicatorEnum), "", indicatorEnum.getLabel()); //$NON-NLS-1$
            }
            return customerdataset;
        }
    }

    public DataExplorer getDataExplorer() {
        if (Java2SqlType.isDateInSQL(sqltype)) {
            return null;
        }
        return new SummaryStastictisExplorer();
    }

    public JFreeChart getExampleChart() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected TableStructureEntity getTableStructure() {
        TableStructureEntity entity = new TableStructureEntity();
        entity
                .setFieldNames(new String[] {
                        DefaultMessagesImpl.getString("SummaryStatisticsState.Label"), DefaultMessagesImpl.getString("SummaryStatisticsState.Count") }); //$NON-NLS-1$ //$NON-NLS-2$
        entity.setFieldWidths(new Integer[] { 200, 300 });
        return entity;
    }

    @Override
    protected ITableLabelProvider getLabelProvider() {
        // TODO Auto-generated method stub
        return new SummaryLabelProvider();
    }

    @Override
    protected IStructuredContentProvider getContentProvider() {
        // TODO Auto-generated method stub
        return new CommonContenteProvider();
    }

    private boolean isIntact() {
        return units.size() == FULL_FLAG;
    }

    public String getReferenceLink() {
        String url = null;

        if (getDataset() instanceof BoxAndWhiskerCategoryDataset) {
            url = "http://en.wikipedia.org/wiki/Box_plot"; //$NON-NLS-1$
        } else {
            url = "http://en.wikipedia.org/wiki/Histogram"; //$NON-NLS-1$
        }
        return url;
    }
}
