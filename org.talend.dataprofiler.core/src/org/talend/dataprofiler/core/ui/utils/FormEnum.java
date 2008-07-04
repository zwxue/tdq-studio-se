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

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum FormEnum {

    BinsDesignerForm("Bins Designer"),
    FreqBinsDesignerForm("Bins Designer"),
    TextParametersForm("Text Parameter"),
    FreqTextParametersForm("Text Parameter"),
    TimeSlicesForm("Time Slices"),
    FreqTimeSliceForm("Time Slices"),
    DataThresholdsForm("Data Thresholds"),
    TextLengthForm("Text Length");

    private String formName;

    FormEnum(String formName) {
        this.formName = formName;
    }

    public String getFormName() {
        return formName;
    }
}
