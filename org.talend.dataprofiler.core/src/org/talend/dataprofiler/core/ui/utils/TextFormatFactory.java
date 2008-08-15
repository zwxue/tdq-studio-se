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

import java.text.DecimalFormat;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class TextFormatFactory {

    public static String createStandardPercent(Object input) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getPercentInstance();
        format.applyPattern("0.00%");

        try {
            return format.format(new Double(input.toString()));
        } catch (Exception ne) {
            return "";
        }
    }

    public static String createStandardNumber(Object input) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance();
        format.applyPattern("0.00");

        try {
            return format.format(new Double(input.toString()));
        } catch (Exception ne) {
            return "";
        }
    }
}
