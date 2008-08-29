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
import java.text.ParseException;
import java.util.Locale;

/**
 * DOC zqin class global comment. Detailled comment
 */
public final class TextFormatFactory {

    private TextFormatFactory() {
    }

    public static String createStandardPercent(Object input) {
        Double dbl = new Double(input.toString());
        if (dbl.equals(Double.NaN)) {
            return String.valueOf(dbl);
        }
        // MOD scorreia 2008-08-29 use English Locale to display percentage in English format for everybody (otherwise
        // we should handle user's locale everywhere in the platform
        DecimalFormat format = (DecimalFormat) DecimalFormat.getPercentInstance(Locale.ENGLISH);
        format.applyPattern("0.00%");

        try {
            return format.format(dbl);
        } catch (Exception ne) {
            return "";
        }
    }

    public static Number createStandardNumber(Object input) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance();
        format.applyPattern("0.00");

        try {
            return Double.parseDouble(input.toString());
        } catch (Exception ne) {
            return 0;
        }
    }

    public static Number createLocalFormatValue(Object input) throws ParseException {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());

        return format.parse(input.toString());

    }
}
