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
import org.talend.dataprofiler.core.ui.editor.preview.CompositeIndicator;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartWithData {

    private ImageDescriptor imageDescriptor;

    private ChartDataEntity[] enity;

    private String chartNamedType;

    public ChartWithData(String chartNamedType, ImageDescriptor imageDescriptor, ChartDataEntity[] enity) {
        this.chartNamedType = chartNamedType;
        this.imageDescriptor = imageDescriptor;
        this.enity = enity;

        /*
         * the concluation of the pattern remain in thire inside.
         */
        if (chartNamedType != CompositeIndicator.PATTERN_MATCHING) {
            this.computered();
        }
    }

    public ImageDescriptor getImageDescriptor() {
        return imageDescriptor;
    }

    public ChartDataEntity[] getEnity() {
        return enity;
    }

    public String getChartNamedType() {
        return chartNamedType;
    }

    private void computered() {
        double sum = 0;
        for (ChartDataEntity oneEntity : this.enity) {
            sum = sum + Double.valueOf(oneEntity.getValue());
        }

        for (ChartDataEntity oneEntity : this.enity) {
            String str = Double.valueOf(oneEntity.getValue()) * 100 / sum + "%";
            oneEntity.setPersent(str);
        }
    }

}
