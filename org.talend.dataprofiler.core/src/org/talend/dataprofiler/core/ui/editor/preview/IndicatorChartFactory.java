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

import org.eclipse.jface.resource.ImageDescriptor;
import org.jfree.data.category.CategoryDataset;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.ext.FrequencyExt;
import org.talend.dataprofiler.core.ui.editor.preview.ext.PatternMatchingExt;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartDataEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartWithData;
import org.talend.dataprofiler.core.ui.editor.preview.model.PatternChartDataEntity;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;

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
                CategoryDataset dataset = isCreate ? ChartDatasetFactory.createDataset(chartType, list) : ChartDatasetFactory
                        .createExampleDataset(chartType);
                ImageDescriptor imageDesc = ChartImageFactory.createImage(chartType, dataset);

                ChartWithData chart = new ChartWithData(chartType, imageDesc, getDataEnityFromUnits(list));
                returnFiles.add(chart);
            }
        }

        return returnFiles;
    }

    private static ChartDataEntity[] getDataEnityFromUnits(List<IndicatorUnit> unitList) {

        List<ChartDataEntity> list = new ArrayList<ChartDataEntity>();

        for (IndicatorUnit unit : unitList) {

            if (unit.isExcuted()) {

                ChartDataEntity entity;

                switch (unit.getType()) {
                case FrequencyIndicatorEnum:
                    FrequencyExt[] freqExt = (FrequencyExt[]) unit.getValue();
                    for (FrequencyExt one : freqExt) {
                        entity = new ChartDataEntity();
                        // MOD scorreia 2008-08-20 handle case when key is null -> replace by "null"
                        entity.setLabel(String.valueOf(one.getKey()));
                        entity.setValue(String.valueOf(one.getValue()));
                        entity.setPercent(String.valueOf(one.getFrequency()));
                        entity.setIndicator(unit.getIndicator());
                        list.add(entity);
                    }

                    // add Frequency.OTHER
                    entity = new ChartDataEntity();
                    FrequencyIndicator freqIndicator = (FrequencyIndicator) unit.getIndicator();
                    entity.setLabel(FrequencyIndicator.OTHER);
                    entity.setValue(freqIndicator.getCount(FrequencyIndicator.OTHER).toString());
                    entity.setPercent(freqIndicator.getFrequency(FrequencyIndicator.OTHER).toString());
                    entity.setIndicator(freqIndicator);

                    list.add(entity);

                    break;
                case RegexpMatchingIndicatorEnum:
                case SqlPatternMatchingIndicatorEnum:
                    PatternMatchingExt patnExt = (PatternMatchingExt) unit.getValue();
                    PatternMatchingIndicator patternIndicator = (PatternMatchingIndicator) unit.getIndicator();
                    PatternChartDataEntity patternEntity = new PatternChartDataEntity();
                    patternEntity.setLabel(unit.getIndicatorName());
                    patternEntity.setNumMatch(String.valueOf(patnExt.getMatchingValueCount()));
                    patternEntity.setNumNoMatch(String.valueOf(patnExt.getNotMatchingValueCount()));
                    // MOD scorreia 2008-08-22 setValue() because used in thresholds validation
                    Double total = patternIndicator.getMatchingValueCount().doubleValue()
                            + patternIndicator.getNotMatchingValueCount().doubleValue();
                    Double percentMatch = total > 0 ? patternIndicator.getMatchingValueCount().doubleValue() * 100 / total
                            : Double.NaN;
                    patternEntity.setValue(String.valueOf(percentMatch));
                    patternEntity.setIndicator(patternIndicator);
                    list.add(patternEntity);

                    break;

                default:
                    entity = new ChartDataEntity();
                    entity.setLabel(unit.getIndicatorName());
                    entity.setValue(String.valueOf(unit.getValue()));
                    entity.setIndicator(unit.getIndicator());
                    list.add(entity);
                }
            }
        }

        return list.toArray(new ChartDataEntity[list.size()]);
    }
}
