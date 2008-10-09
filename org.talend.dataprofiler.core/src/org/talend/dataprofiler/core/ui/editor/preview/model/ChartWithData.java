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

import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.dataprofiler.core.ui.editor.preview.EIndicatorChartType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartWithData {

    private ImageDescriptor imageDescriptor;

    private ChartDataEntity[] enity;

    private EIndicatorChartType chartType;

    public ChartWithData(EIndicatorChartType chartType, ImageDescriptor imageDescriptor, ChartDataEntity[] enity) {
        this.chartType = chartType;
        this.imageDescriptor = imageDescriptor;
        this.enity = enity;
    }

    public ImageDescriptor getImageDescriptor() {
        return imageDescriptor;
    }

    public ChartDataEntity[] getEnity() {
        return enity;
    }

    public EIndicatorChartType getChartType() {
        return chartType;
    }

}
