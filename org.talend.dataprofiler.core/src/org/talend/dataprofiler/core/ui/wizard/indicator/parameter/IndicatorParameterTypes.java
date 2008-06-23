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

package org.talend.dataprofiler.core.ui.wizard.indicator.parameter;

import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public enum IndicatorParameterTypes {
    // the Context config from Plugin:org.talend.dataprofiler.help, HelpContext.xml file.
    TYPE_BINS_DESIGNER("Bins designer", "html/wizard/indicator/BinsDesigner.html"),
    TYPE_TIME_SLICE("Time slice", "html/wizard/indicator/TimeSlice.html"),
    TYPE_TEXT_PARAMETER("Text Parameter", "html/wizard/indicator/TextParameter.html"),
    TYPE_TEXT_LENGTH("Text Length", "html/wizard/indicator/TextLength.html"),
    TYPE_DATA_THRESHOLD("Data Threshold", "html/wizard/indicator/DataThreshold.html");

    private String label, href;

    private static String pid = "org.talend.dataprofiler.help";

    IndicatorParameterTypes(String label, String href) {
        this.label = label;
        this.href = href;
    }

    public static String getHref(IndicatorUnit indicator) {
        String href = null;
        ColumnIndicator columnIndicator = indicator.getParentColumn();
        int sqlType = columnIndicator.getTdColumn().getJavaType();
        DataminingType dataminingType = MetadataHelper.getDataminingType(columnIndicator.getTdColumn());
        if (dataminingType == null) {
            dataminingType = MetadataHelper.getDefaultDataminingType(columnIndicator.getTdColumn().getJavaType());
        }
        switch (indicator.getType()) {

        case DistinctCountIndicatorEnum:
        case UniqueIndicatorEnum:
        case DuplicateCountIndicatorEnum:

            if (Java2SqlType.isTextInSQL(sqlType)) {
                href = IndicatorParameterTypes.TYPE_TEXT_PARAMETER.href;
            }
            break;
        case MinLengthIndicatorEnum:
        case MaxLengthIndicatorEnum:
        case AverageLengthIndicatorEnum:
            href = IndicatorParameterTypes.TYPE_TEXT_LENGTH.href;

            break;
        case FrequencyIndicatorEnum:
            if (dataminingType == DataminingType.INTERVAL) {
                if (Java2SqlType.isNumbericInSQL(sqlType)) {
                    href = IndicatorParameterTypes.TYPE_BINS_DESIGNER.href;
                }

                if (Java2SqlType.isDateInSQL(sqlType)) {
                    href = IndicatorParameterTypes.TYPE_TIME_SLICE.href;
                }
            } else if (Java2SqlType.isTextInSQL(sqlType)) {
                href = IndicatorParameterTypes.TYPE_TEXT_PARAMETER.href;
            }

            break;
        case ModeIndicatorEnum:
            if (dataminingType == DataminingType.INTERVAL) {
                if (Java2SqlType.isNumbericInSQL(sqlType)) {
                    href = IndicatorParameterTypes.TYPE_BINS_DESIGNER.href;
                }
            } else if (Java2SqlType.isTextInSQL(sqlType)) {
                href = IndicatorParameterTypes.TYPE_TEXT_PARAMETER.href;
            }

            break;
        case BoxIIndicatorEnum:
            href = IndicatorParameterTypes.TYPE_DATA_THRESHOLD.href;
            break;
        default:
            return href;
        }
        href = "/" + pid + "/" + href;
        return href;
    }
}
