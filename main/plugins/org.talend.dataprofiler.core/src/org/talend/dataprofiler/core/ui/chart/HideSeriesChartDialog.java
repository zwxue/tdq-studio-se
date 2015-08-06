// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.chart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class HideSeriesChartDialog extends FullScreenChartDialog {

    private Indicator indicator;

    private static final String SERIES_KEY_ID = "SERIES_KEY"; //$NON-NLS-1$

    public HideSeriesChartDialog(Shell shell, JFreeChart chart, Indicator indicator) {
        super(shell, chart);

        this.indicator = indicator;
    }

    @Override
    protected Control createButtonBar(Composite parent) {

        return createUtilityControl(parent);
    }

    private Composite createUtilityControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.BORDER);
        comp.setLayout(new RowLayout());
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

        return comp;
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
}
