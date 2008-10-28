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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dq.indicators.preview.EIndicatorChartType;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class CompositeIndicator {

    private IndicatorUnit[] indicatorUnits;

    private Map<EIndicatorChartType, List<IndicatorUnit>> separatedMap;

    private List<IndicatorUnit> tempList = new ArrayList<IndicatorUnit>();

    public CompositeIndicator(ColumnIndicator columnIndicator) {

        this.separatedMap = new HashMap<EIndicatorChartType, List<IndicatorUnit>>();
        this.indicatorUnits = initIndicatorUnits(columnIndicator.getIndicatorUnits());
    }

    private IndicatorUnit[] initIndicatorUnits(IndicatorUnit[] indicatorUnits) {

        for (IndicatorUnit unit : indicatorUnits) {
            if (unit.getChildren() != null) {
                initIndicatorUnits(unit.getChildren());
            } else {
                tempList.add(unit);
            }
        }

        return tempList.toArray(new IndicatorUnit[tempList.size()]);
    }

    public Map<EIndicatorChartType, List<IndicatorUnit>> getIndicatorComposite() {

        List<IndicatorUnit> simpleList = new ArrayList<IndicatorUnit>();
        List<IndicatorUnit> textList = new ArrayList<IndicatorUnit>();
        List<IndicatorUnit> frequencyList = new ArrayList<IndicatorUnit>();
        List<IndicatorUnit> lowFrequencyList = new ArrayList<IndicatorUnit>();
        List<IndicatorUnit> summaryList = new ArrayList<IndicatorUnit>();
        List<IndicatorUnit> patternList = new ArrayList<IndicatorUnit>();
        List<IndicatorUnit> sqlPatternList = new ArrayList<IndicatorUnit>();
        List<IndicatorUnit> modelIndicatorList = new ArrayList<IndicatorUnit>();

        for (IndicatorUnit one : indicatorUnits) {

            switch (one.getType()) {
            case RowCountIndicatorEnum:
            case NullCountIndicatorEnum:
            case DistinctCountIndicatorEnum:
            case UniqueIndicatorEnum:
            case DuplicateCountIndicatorEnum:
            case BlankCountIndicatorEnum:
                simpleList.add(one);
                break;
            case MinLengthIndicatorEnum:
            case MaxLengthIndicatorEnum:
            case AverageLengthIndicatorEnum:
                textList.add(one);
                break;
            case FrequencyIndicatorEnum:
                frequencyList.add(one);
                break;
            case LowFrequencyIndicatorEnum:
                lowFrequencyList.add(one);
                break;
            case MeanIndicatorEnum:
            case MinValueIndicatorEnum:
            case MaxValueIndicatorEnum:
            case MedianIndicatorEnum:
            case LowerQuartileIndicatorEnum:
            case UpperQuartileIndicatorEnum:
                summaryList.add(one);
                break;
            case RegexpMatchingIndicatorEnum:
                patternList.add(one);
                break;

            case SqlPatternMatchingIndicatorEnum:
                sqlPatternList.add(one);
                break;

            case ModeIndicatorEnum:
                modelIndicatorList.add(one);
                break;

            default:
            }
        }

        separatedMap.put(EIndicatorChartType.SIMPLE_STATISTICS, simpleList);
        separatedMap.put(EIndicatorChartType.TEXT_STATISTICS, textList);
        separatedMap.put(EIndicatorChartType.FREQUENCE_STATISTICS, frequencyList);
        separatedMap.put(EIndicatorChartType.LOW_FREQUENCE_STATISTICS, lowFrequencyList);
        separatedMap.put(EIndicatorChartType.SUMMARY_STATISTICS, summaryList);
        separatedMap.put(EIndicatorChartType.PATTERN_MATCHING, patternList);
        separatedMap.put(EIndicatorChartType.SQL_PATTERN_MATCHING, sqlPatternList);
        separatedMap.put(EIndicatorChartType.MODE_INDICATOR, modelIndicatorList);

        return separatedMap;
    }

}
