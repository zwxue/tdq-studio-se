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
package org.talend.dataprofiler.core.ui.editor.preview.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.analysis.explore.FrequencyStatisticsExplorer;
import org.talend.dq.analysis.explore.IDataExplorer;
import org.talend.dq.analysis.explore.PatternExplorer;
import org.talend.dq.analysis.explore.SimpleStatisticsExplorer;
import org.talend.dq.analysis.explore.SummaryStastictisExplorer;
import org.talend.dq.analysis.explore.TextStatisticsExplorer;
import org.talend.dq.indicators.preview.EIndicatorChartType;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class ChartTableMenuGenerator {

    public static MenuItemEntity[] generate(EIndicatorChartType chartTableType, Analysis analysis, ChartDataEntity entity) {

        IDataExplorer explorer = null;

        switch (chartTableType) {
        case FREQUENCE_STATISTICS:
            explorer = new FrequencyStatisticsExplorer();
            break;
        case SQL_PATTERN_MATCHING:
        case PATTERN_MATCHING:
            explorer = new PatternExplorer();
            break;
        case SIMPLE_STATISTICS:
            explorer = new SimpleStatisticsExplorer();
            break;
        case TEXT_STATISTICS:
            explorer = new TextStatisticsExplorer();
            break;
        case SUMMARY_STATISTICS:
            explorer = new SummaryStastictisExplorer();
            break;
        default:

        }

        explorer.setAnalysis(analysis);
        explorer.setEnitty(entity);

        Map<String, String> queryMap = explorer.getQueryMap();
        List<MenuItemEntity> retrunList = new ArrayList<MenuItemEntity>();
        for (String key : queryMap.keySet()) {
            MenuItemEntity itemEntity = new MenuItemEntity(key, null, queryMap.get(key));

            retrunList.add(itemEntity);
        }

        return retrunList.toArray(new MenuItemEntity[queryMap.size()]);
    }
}
