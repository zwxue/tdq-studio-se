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
public final class CompositeIndicator {

    private IndicatorUnit[] indicatorUnits;

    private Map<EIndicatorChartType, List<IndicatorUnit>> separatedMap;

    private List<IndicatorUnit> simpleList, textList, frequencyList, lowFrequencyList, patternFrequencylist,
            patternLowFrequencyList, summaryList, patternList, sqlPatternList, modelIndicatorList;

    private static CompositeIndicator instance;

    private CompositeIndicator() {
        init();

        // this.separatedMap = new HashMap<EIndicatorChartType, List<IndicatorUnit>>();
        // this.indicatorUnits = initIndicatorUnits(columnIndicator.getIndicatorUnits());
    }

    public static CompositeIndicator getInstance() {
        if (instance == null) {
            instance = new CompositeIndicator();
        }
        return instance;
    }

    private void init() {
        simpleList = new ArrayList<IndicatorUnit>();
        textList = new ArrayList<IndicatorUnit>();
        frequencyList = new ArrayList<IndicatorUnit>();
        lowFrequencyList = new ArrayList<IndicatorUnit>();
        patternFrequencylist = new ArrayList<IndicatorUnit>();
        patternLowFrequencyList = new ArrayList<IndicatorUnit>();
        summaryList = new ArrayList<IndicatorUnit>();
        patternList = new ArrayList<IndicatorUnit>();
        sqlPatternList = new ArrayList<IndicatorUnit>();
        modelIndicatorList = new ArrayList<IndicatorUnit>();
        separatedMap = new HashMap<EIndicatorChartType, List<IndicatorUnit>>();
    }

    private void clear() {
        simpleList.clear();
        textList.clear();
        frequencyList.clear();
        lowFrequencyList.clear();
        patternFrequencylist.clear();
        patternLowFrequencyList.clear();
        summaryList.clear();
        patternList.clear();
        sqlPatternList.clear();
        modelIndicatorList.clear();
        separatedMap.clear();
    }

    private IndicatorUnit[] initChildIndicatorUnits(List<IndicatorUnit> tempList, IndicatorUnit[] indicatorUnits) {
        for (IndicatorUnit unit : indicatorUnits) {
            if (unit.getChildren() != null) {
                initChildIndicatorUnits(tempList, unit.getChildren());
            } else {
                tempList.add(unit);
            }
        }

        return tempList.toArray(new IndicatorUnit[tempList.size()]);
    }

    public Map<EIndicatorChartType, List<IndicatorUnit>> getIndicatorComposite(ColumnIndicator columnIndicator) {
        this.clear();
        List<IndicatorUnit> tempList = new ArrayList<IndicatorUnit>();
        this.indicatorUnits = initChildIndicatorUnits(tempList, columnIndicator.getIndicatorUnits());
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
            case PatternFreqIndicatorEnum:
                patternFrequencylist.add(one);
                break;
            case PatternLowFreqIndicatorEnum:
                patternLowFrequencyList.add(one);
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
        separatedMap.put(EIndicatorChartType.PATTERN_FREQUENCE_STATISTICS, patternFrequencylist);
        separatedMap.put(EIndicatorChartType.PATTERN_LOW_FREQUENCE_STATISTICS, patternLowFrequencyList);
        separatedMap.put(EIndicatorChartType.SUMMARY_STATISTICS, summaryList);
        separatedMap.put(EIndicatorChartType.PATTERN_MATCHING, patternList);
        separatedMap.put(EIndicatorChartType.SQL_PATTERN_MATCHING, sqlPatternList);
        separatedMap.put(EIndicatorChartType.MODE_INDICATOR, modelIndicatorList);

        return separatedMap;
    }

}
