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
package org.talend.dataprofiler.core.ui.utils;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

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
    public static void createAWTSWTComp(final Composite composite, GridData gd, ChartPanel chartPanel) {
        Composite frameComp = new Composite(composite, SWT.EMBEDDED);
        frameComp.setLayout(new GridLayout());
        frameComp.setLayoutData(gd);

        Frame frame = SWT_AWT.new_Frame(frameComp);
        frame.setLayout(new java.awt.GridLayout());

        frame.add(chartPanel);
        frame.pack();
        frame.validate();
    }
}
