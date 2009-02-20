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

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public final class ChartUtils {

    private ChartUtils() {

    }

    public static ImageDescriptor convertToImage(JFreeChart chart, int maxWidth, int maxHeight) throws IOException {
        BufferedImage image = chart.createBufferedImage(maxWidth, maxHeight);
        ImageDescriptor descriptor = bufferedToDescriptorOptimized(image);
        return descriptor;
    }

    public static ImageDescriptor bufferedToDescriptorOptimized(final BufferedImage image) throws IOException {
        final PipedOutputStream output = new PipedOutputStream();
        final PipedInputStream pipedInputStream = new PipedInputStream();
        output.connect(pipedInputStream);

        try {
            new Thread() {

                @Override
                public void run() {
                    try {
                        ChartUtilities.writeBufferedImageAsPNG(output, image, false, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }.start();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
        }

        ImageData img = new ImageData(pipedInputStream);
        ImageDescriptor descriptor = ImageDescriptor.createFromImageData(img);
        return descriptor;
    }

    /**
     * Create a AWT_SWT bridge composite for displaying the <CODE>ChartPanel</CODE>.
     * 
     * @param composite
     * @param gd
     * @param chartPanel
     */
    public static ChartPanel createAWTSWTComp(Composite composite, GridData gd, JFreeChart chart) {

        ChartPanel chartPanel = new ChartPanel(chart);
        Composite frameComp = new Composite(composite, SWT.EMBEDDED);
        frameComp.setLayout(new GridLayout());
        frameComp.setLayoutData(gd);
        frameComp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));

        Frame frame = SWT_AWT.new_Frame(frameComp);
        frame.setLayout(new java.awt.GridLayout());
        frame.add(chartPanel);
        frame.validate();

        return chartPanel;
    }

    /**
     * DOC Administrator Comment method "openReferenceLink".
     * 
     * @param httpurl
     */
    public static void openReferenceLink(String httpurl) {

        if (httpurl != null) {
            try {
                WebBrowserEditor.open(new WebBrowserEditorInput(new URL(httpurl)));
            } catch (MalformedURLException e1) {
                ExceptionHandler.process(e1);
            }
        }
    }

    public static void showChartInFillScreen(JFreeChart chart, Indicator indicator) {
        FullScreenChartDialog dialog = new FullScreenChartDialog(null, chart, indicator);
        dialog.open();
    }

    /**
     * DOC bzhou class global comment. Detailled comment
     */
    private static class FullScreenChartDialog extends TrayDialog {

        private JFreeChart chart;

        private Indicator indicator;

        private static final String SERIES_KEY_ID = "SERIES_KEY";

        protected FullScreenChartDialog(Shell shell, JFreeChart chart, Indicator indicator) {
            super(shell);
            this.chart = chart;
            this.indicator = indicator;
            setShellStyle(SWT.RESIZE | SWT.CLOSE | SWT.MIN | SWT.MAX);
        }

        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);

            newShell.setSize(800, 500);
            newShell.setMaximized(true);
            // newShell.setFullScreen(true);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite comp = new Composite(parent, SWT.NONE);
            comp.setLayout(new GridLayout());
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            createAWTSWTComp(comp, new GridData(GridData.FILL_BOTH), chart);

            return comp;
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
}
