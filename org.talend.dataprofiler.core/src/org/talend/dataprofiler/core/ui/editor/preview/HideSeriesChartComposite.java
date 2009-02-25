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

import java.awt.Color;
import java.awt.Paint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.TextAnchor;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ui.utils.ChartUtils;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class HideSeriesChartComposite extends ChartComposite {

    private ColumnSetMultiValueIndicator indicator;

    private TdColumn column;

    private JFreeChart chart;

    private boolean isNeedUtility;

    private static final String SERIES_KEY_ID = "SERIES_KEY";

    private Map<String, RowColumPair> hightlightSeriesMap = new HashMap<String, RowColumPair>();

    public HideSeriesChartComposite(Composite comp, ColumnSetMultiValueIndicator indicator, TdColumn column, boolean isNeedUtility) {
        super(comp, SWT.NONE);

        this.indicator = indicator;
        this.column = column;
        this.isNeedUtility = isNeedUtility;
        this.setCursor(new Cursor(Display.getDefault(), SWT.CURSOR_HAND));
        this.setToolTipText("sdfsdf");

        createHideSeriesArea();

        addSpecifiedListeners();
    }

    private void addSpecifiedListeners() {

        this.addSWTListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                ChartUtils.showChartInFillScreen(createChart(), indicator);
            }

            @Override
            public void mouseDown(MouseEvent e) {

                setRangeZoomable(e.button == 1);
                setDomainZoomable(e.button == 1);

                if (e.button == 3) {
                    Menu menu = new Menu(getShell(), SWT.POP_UP);
                    setMenu(menu);
                    MenuItem item = new MenuItem(menu, SWT.PUSH);
                    item.setText("Show in full screen");

                    item.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            Display.getDefault().asyncExec(new Runnable() {

                                public void run() {
                                    ChartUtils.showChartInFillScreen(createChart(), indicator);
                                }
                            });
                        }
                    });

                    menu.setVisible(true);
                }
            }
        });
    }

    private void createHideSeriesArea() {
        this.chart = createChart();

        if (chart != null) {
            setChart(chart);

            if (isNeedUtility) {

                createUtilityControl(this);
            }
        }
    }

    /**
     * DOC bzhou Comment method "createChart".
     * 
     * @return
     */
    private JFreeChart createChart() {
        JFreeChart jchart = null;

        if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator().equals(indicator.eClass())) {
            jchart = TopChartFactory.createBubbleChart(indicator, column);
        }

        if (ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator().equals(indicator.eClass())) {
            final int nbNominalColumns = indicator.getNominalColumns().size();
            final int nbDateFunctions = indicator.getDateFunctions().size();
            final int indexOfDateCol = indicator.getDateColumns().indexOf(column);
            assert indexOfDateCol != -1;

            jchart = TopChartFactory.createGanttChart(indicator, column);

            createAnnotOnGantt(jchart, indicator.getListRows(), nbNominalColumns + nbDateFunctions * indexOfDateCol + 3,
                    nbNominalColumns);

            CategoryPlot plot = (CategoryPlot) jchart.getPlot();
            CustomHideSeriesGanttRender renderer = new CustomHideSeriesGanttRender();
            renderer.setBaseToolTipGenerator(toolTipGenerator);
            plot.setRenderer(renderer);
            plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(10.0f);
        }

        return jchart;
    }

    private void createUtilityControl(Composite parent) {
        Composite comp = new Composite(getParent(), SWT.BORDER);
        comp.setLayout(new GridLayout());
        comp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
        comp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));

        if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator().equals(indicator.eClass())) {
            XYDataset dataset = chart.getXYPlot().getDataset();
            int count = dataset.getSeriesCount();

            for (int i = 0; i < count; i++) {

                Button checkBtn = new Button(comp, SWT.CHECK);
                checkBtn.setText(dataset.getSeriesKey(i).toString());
                checkBtn.setSelection(true);
                checkBtn.addSelectionListener(listener);
                checkBtn.setData(SERIES_KEY_ID, i);
            }
        }

        if (ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator().equals(indicator.eClass())) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            CategoryDataset dataset = plot.getDataset();
            int count = dataset.getRowCount();

            for (int i = 0; i < count; i++) {

                Button checkBtn = new Button(comp, SWT.CHECK);
                checkBtn.setText(dataset.getRowKey(i).toString());
                checkBtn.setSelection(true);
                checkBtn.addSelectionListener(listener);
                checkBtn.setData(SERIES_KEY_ID, i);
            }
        }

    }

    SelectionAdapter listener = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {

            Button checkBtn = (Button) e.getSource();
            int seriesid = (Integer) checkBtn.getData(SERIES_KEY_ID);

            if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator().equals(indicator.eClass())) {
                XYPlot plot = chart.getXYPlot();
                XYItemRenderer xyRenderer = plot.getRenderer();
                xyRenderer.setSeriesVisible(seriesid, checkBtn.getSelection());
            }

            if (ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator().equals(indicator.eClass())) {
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
                CategoryItemRenderer render = plot.getRenderer();
                render.setSeriesVisible(seriesid, checkBtn.getSelection());
            }
        }
    };

    CategoryToolTipGenerator toolTipGenerator = new CategoryToolTipGenerator() {

        public String generateToolTip(CategoryDataset dataset, int row, int column) {
            TaskSeriesCollection taskSeriesColl = (TaskSeriesCollection) dataset;
            List<Task> taskList = new ArrayList<Task>();
            for (int i = 0; i < taskSeriesColl.getSeriesCount(); i++) {
                for (int j = 0; j < taskSeriesColl.getSeries(i).getItemCount(); j++) {
                    taskList.add(taskSeriesColl.getSeries(i).get(j));
                }
            }
            Task task = taskList.get(column);
            // Task task = taskSeriesColl.getSeries(row).get(column);
            String taskDescription = task.getDescription();
            Date startDate = task.getDuration().getStart();
            Date endDate = task.getDuration().getEnd();
            return taskDescription + ",     " + startDate + "---->" + endDate; //$NON-NLS-1$ //$NON-NLS-2$
            // return "this is a tooltip";
        }
    };

    /**
     * 
     * DOC zhaoxinyi Comment method "createAnnotOnGantt".
     * 
     * @param chart
     * @param rowList
     * @param multiDateColumn
     */
    public void createAnnotOnGantt(JFreeChart chart, List<Object[]> rowList, int multiDateColumn, int nominal) {
        CategoryPlot xyplot = (CategoryPlot) chart.getPlot();
        CategoryTextAnnotation an;
        for (int seriesCount = 0; seriesCount < ((TaskSeriesCollection) xyplot.getDataset()).getSeriesCount(); seriesCount++) {
            int indexOfRow = 0;
            int columnCount = 0;
            for (int itemCount = 0; itemCount < ((TaskSeriesCollection) xyplot.getDataset()).getSeries(seriesCount)
                    .getItemCount(); itemCount++, columnCount++) {
                Task task = ((TaskSeriesCollection) xyplot.getDataset()).getSeries(seriesCount).get(itemCount);
                String taskDescription = task.getDescription();
                String[] taskArray = taskDescription.split("\\|"); //$NON-NLS-1$
                boolean isSameTime = task.getDuration().getStart().getTime() == task.getDuration().getEnd().getTime();
                if (!isSameTime && (rowList.get(indexOfRow))[multiDateColumn - 3] != null
                        && (rowList.get(indexOfRow))[multiDateColumn - 2] != null
                        && !((rowList.get(indexOfRow))[multiDateColumn]).equals(new BigDecimal(0L))) {
                    RowColumPair pair = new RowColumPair();
                    pair.setRow(seriesCount);
                    pair.setColumn(columnCount);
                    hightlightSeriesMap.put(String.valueOf(seriesCount) + String.valueOf(columnCount), pair);
                    an = new CategoryTextAnnotation("#nulls = " + (rowList.get(indexOfRow))[multiDateColumn], //$NON-NLS-1$
                            (Comparable<String>) taskDescription, task.getDuration().getStart().getTime());
                    an.setTextAnchor(TextAnchor.CENTER_LEFT);
                    an.setCategoryAnchor(CategoryAnchor.MIDDLE);
                    xyplot.addAnnotation(an);
                }
                if (taskArray.length == nominal) {
                    indexOfRow++;

                    if (rowList.size() != indexOfRow
                            && ((rowList.get(indexOfRow))[multiDateColumn - 3] == null || (rowList.get(indexOfRow))[multiDateColumn - 2] == null)) {
                        indexOfRow++;
                    }
                }
            }
        }
    }

    /**
     * 
     * DOC zhaoxinyi HideSeriesPanel class global comment. Detailled comment
     */
    class CustomHideSeriesGanttRender extends HideSeriesGanttRenderer {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * 
         * DOC zhaoxinyi CustomHideSeriesGantt constructor comment.
         * 
         * @param colors
         */
        public Paint getItemPaint(int row, int column) {
            Paint itemPaint = super.getItemPaint(row, column);
            String key = String.valueOf(row) + String.valueOf(column);
            if (hightlightSeriesMap.get(key) != null && hightlightSeriesMap.get(key).getRow() == row
                    && hightlightSeriesMap.get(key).getColumn() == column) {
                return ((Color) itemPaint).brighter().brighter();
            }
            return itemPaint;
        }
    }

    /**
     * 
     * DOC zhaoxinyi class global comment. Detailled comment
     */
    class RowColumPair {

        private int row;

        private int column;

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public String toString() {
            return "row = " + row + ", column = " + column; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

}
