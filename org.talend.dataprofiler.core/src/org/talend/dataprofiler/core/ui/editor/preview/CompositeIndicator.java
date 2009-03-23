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
import org.talend.dataprofiler.core.model.TableIndicator;
import org.talend.dq.indicators.preview.EIndicatorChartType;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public final class CompositeIndicator {

    private IndicatorUnit[] indicatorUnits;

    private TableIndicatorUnit[] tableIndicatorUnits;

    private Map<EIndicatorChartType, List<IndicatorUnit>> separatedMap;

    private Map<EIndicatorChartType, List<TableIndicatorUnit>> tableSeparatedMap;

    private List<IndicatorUnit> simpleList, textList, frequencyList, lowFrequencyList, soundexFrequencyList,
            soundexLowFrequencyList, patternFrequencylist, patternLowFrequencyList, summaryList, patternList, sqlPatternList,
            modelIndicatorList;

    private List<TableIndicatorUnit> tableSimpleList, tableWhereRuleList;

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
        soundexFrequencyList = new ArrayList<IndicatorUnit>();
        soundexLowFrequencyList = new ArrayList<IndicatorUnit>();
        patternFrequencylist = new ArrayList<IndicatorUnit>();
        patternLowFrequencyList = new ArrayList<IndicatorUnit>();
        summaryList = new ArrayList<IndicatorUnit>();
        patternList = new ArrayList<IndicatorUnit>();
        sqlPatternList = new ArrayList<IndicatorUnit>();
        modelIndicatorList = new ArrayList<IndicatorUnit>();
        separatedMap = new HashMap<EIndicatorChartType, List<IndicatorUnit>>();
        // MOD xqliu 2009-02-25 feature 6015
        tableSimpleList = new ArrayList<TableIndicatorUnit>();
        tableWhereRuleList = new ArrayList<TableIndicatorUnit>();
        tableSeparatedMap = new HashMap<EIndicatorChartType, List<TableIndicatorUnit>>();
    }

    private void clear() {
        simpleList.clear();
        textList.clear();
        frequencyList.clear();
        lowFrequencyList.clear();
        soundexFrequencyList.clear();
        soundexLowFrequencyList.clear();
        patternFrequencylist.clear();
        patternLowFrequencyList.clear();
        summaryList.clear();
        patternList.clear();
        sqlPatternList.clear();
        modelIndicatorList.clear();
        separatedMap.clear();
        // MOD xqliu 2009-02-25 feature 6015
        tableSimpleList.clear();
        tableWhereRuleList.clear();
        tableSeparatedMap.clear();
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
            case DefValueCountIndicatorEnum:
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
            // ~ MOD mzhao 2009-03-23 Feature 6307
            case SoundexIndicatorEnum:
                soundexFrequencyList.add(one);
                break;
            case SoundexLowIndicatorEnum:
                soundexLowFrequencyList.add(one);
                break;
            // ~
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
        separatedMap.put(EIndicatorChartType.SOUNDEX_FREQUENCY_TABLE, soundexFrequencyList);
        separatedMap.put(EIndicatorChartType.SOUNDEX_LOW_FREQUENCY_TABLE, soundexLowFrequencyList);
        separatedMap.put(EIndicatorChartType.PATTERN_FREQUENCE_STATISTICS, patternFrequencylist);
        separatedMap.put(EIndicatorChartType.PATTERN_LOW_FREQUENCE_STATISTICS, patternLowFrequencyList);
        separatedMap.put(EIndicatorChartType.SUMMARY_STATISTICS, summaryList);
        separatedMap.put(EIndicatorChartType.PATTERN_MATCHING, patternList);
        separatedMap.put(EIndicatorChartType.SQL_PATTERN_MATCHING, sqlPatternList);
        separatedMap.put(EIndicatorChartType.MODE_INDICATOR, modelIndicatorList);

        return separatedMap;
    }

    public Map<EIndicatorChartType, List<TableIndicatorUnit>> getTableIndicatorComposite(TableIndicator tableIndicator) {
        this.clear();
        List<TableIndicatorUnit> tempList = new ArrayList<TableIndicatorUnit>();
        this.tableIndicatorUnits = initChildTableIndicatorUnits(tempList, tableIndicator.getIndicatorUnits());
        for (TableIndicatorUnit one : tableIndicatorUnits) {
            switch (one.getType()) {
            case WhereRuleIndicatorEnum:
                tableWhereRuleList.add(one);
                break;
            default:
            }
        }

        tableSeparatedMap.put(EIndicatorChartType.SIMPLE_STATISTICS, tableSimpleList);
        tableSeparatedMap.put(EIndicatorChartType.WHERERULE_INDICATOR, tableWhereRuleList);

        return tableSeparatedMap;
    }

    private TableIndicatorUnit[] initChildTableIndicatorUnits(List<TableIndicatorUnit> tempList,
            TableIndicatorUnit[] indicatorUnits) {
        for (TableIndicatorUnit unit : indicatorUnits) {
            if (unit.getChildren() != null) {
                initChildTableIndicatorUnits(tempList, unit.getChildren());
            } else {
                tempList.add(unit);
            }
        }
        return tempList.toArray(new TableIndicatorUnit[tempList.size()]);
    }

}
