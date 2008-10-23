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
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartWithData;
import org.talend.dataprofiler.core.ui.editor.preview.model.IDataEntity;
import org.talend.dq.indicators.preview.EIndicatorChartType;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class IndicatorChartFactory {

    public static List<ChartWithData> createChart(ColumnIndicator column, boolean isCreate) {

        CompositeIndicator compositeIndicator = new CompositeIndicator(column);
        Map<EIndicatorChartType, List<IndicatorUnit>> separatedMap = compositeIndicator.getIndicatorComposite();
        List<ChartWithData> returnFiles = new ArrayList<ChartWithData>();

        for (EIndicatorChartType chartType : separatedMap.keySet()) {
            List<IndicatorUnit> list = separatedMap.get(chartType);
            if (!list.isEmpty()) {
                IDataEntity dataset = isCreate ? ChartDatasetFactory.createDataset(chartType, list) : ChartDatasetFactory
                        .createExampleDataset(chartType);
                JFreeChart chart = ChartImageFactory.createChart(chartType, dataset);

                ChartWithData chartData = new ChartWithData(chartType, chart, dataset.getDataEntities());
                returnFiles.add(chartData);
            }
        }

        return returnFiles;
    }
}
