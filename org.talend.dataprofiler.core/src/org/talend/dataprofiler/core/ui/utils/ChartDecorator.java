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
package org.talend.dataprofiler.core.ui.utils;

import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public final class ChartDecorator {

    private static final int BASE_ITEM_LABEL_SIZE = 12;

    private static final int BASE_LABEL_SIZE = 12;

    private static final int BASE_TICK_LABEL_SIZE = 10;

    private static final int BASE_LEGEND_LABEL_SIZE = 10;

    private static final int BASE_TITLE_LABEL_SIZE = 14;

    private ChartDecorator() {
    }

    public static JFreeChart decorateCategoryPlot(JFreeChart chart) {
        if (chart == null) {
            return null;
        }

        Font font = null;
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryItemRenderer render = plot.getRenderer();
        CategoryAxis domainAxis = plot.getDomainAxis();
        ValueAxis valueAxis = plot.getRangeAxis();

        font = new Font("Tahoma", Font.BOLD, BASE_ITEM_LABEL_SIZE);

        render.setBaseItemLabelFont(font);

        font = new Font("Verdana", Font.BOLD, BASE_LABEL_SIZE);
        domainAxis.setLabelFont(font);
        valueAxis.setLabelFont(font);

        font = new Font("Verdana", Font.PLAIN, BASE_TICK_LABEL_SIZE);
        domainAxis.setTickLabelFont(font);
        valueAxis.setTickLabelFont(font);

        font = new Font("Tahoma", Font.PLAIN, BASE_LEGEND_LABEL_SIZE);
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(font);
        }

        font = new Font("Tahoma", Font.BOLD, BASE_TITLE_LABEL_SIZE);
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(font);
        }

        font = null;

        return chart;
    }

    public static JFreeChart decorateXYPlot(JFreeChart chart) {
        if (chart == null) {
            return null;
        }

        Font font = null;
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer render = plot.getRenderer();
        ValueAxis domainAxis = plot.getDomainAxis();
        ValueAxis valueAxis = plot.getRangeAxis();

        font = new Font("Tahoma", Font.BOLD, BASE_ITEM_LABEL_SIZE);

        render.setBaseItemLabelFont(font);

        font = new Font("Verdana", Font.BOLD, BASE_LABEL_SIZE);
        domainAxis.setLabelFont(font);
        valueAxis.setLabelFont(font);

        font = new Font("Verdana", Font.PLAIN, BASE_TICK_LABEL_SIZE);
        domainAxis.setTickLabelFont(font);
        valueAxis.setTickLabelFont(font);

        font = new Font("Tahoma", Font.PLAIN, BASE_LEGEND_LABEL_SIZE);
        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(font);
        }

        font = new Font("Tahoma", Font.BOLD, BASE_TITLE_LABEL_SIZE);
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(font);
        }

        font = null;

        return chart;
    }
}
