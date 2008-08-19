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

import java.util.ArrayList;
import java.util.List;

import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum FormEnum {

    BinsDesignerForm("Bins Designer", "html/wizard/indicator/BinsDesigner.html"),
    FreqBinsDesignerForm("Bins Designer", "html/wizard/indicator/BinsDesigner.html"),
    TextParametersForm("Text Parameter", "html/wizard/indicator/TextParameter.html"),
    FreqTextParametersForm("Text Parameter", "html/wizard/indicator/TextParameter.html"),
    TimeSlicesForm("Time Slices", "html/wizard/indicator/TimeSlice.html"),
    FreqTimeSliceForm("Time Slices", "html/wizard/indicator/TimeSlice.html"),
    DataThresholdsForm("Data Thresholds", "html/wizard/indicator/DataThreshold.html"),
    IndicatorThresholdsForm("Indicator Thresholds", "html/wizard/indicator/IndicatorThresholds.html"),
    TextLengthForm("Text Length", "html/wizard/indicator/TextLength.html"),
    NumbericNominalForm("Numberic Nominal", "");

    private String formName;

    private String helpHref;

    private static String pid = "org.talend.dataprofiler.help";

    FormEnum(String formName, String helpHref) {
        this.formName = formName;
        this.helpHref = helpHref;
    }

    public String getFormName() {
        return formName;
    }

    public String getHelpHref() {
        return "/" + pid + "/" + helpHref;
    }

    public static String[] getHelpHref(IndicatorUnit indicatorUnit) {
        List<String> tempList = new ArrayList<String>();
        for (FormEnum oneForm : getForms(indicatorUnit)) {
            tempList.add(oneForm.getHelpHref());
        }

        return tempList.toArray(new String[tempList.size()]);
    }

    public static String getFirstFormHelpHref(IndicatorUnit indicatorUnit) {
        return getHelpHref(indicatorUnit)[0];
    }

    /**
     * DOC zqin Comment method "getForms".
     * 
     * @param dataminingType
     * @param sqlType
     */
    public static FormEnum[] getForms(IndicatorUnit indicatorUnit) {
        ColumnIndicator columnIndicator = indicatorUnit.getParentColumn();
        int sqlType = columnIndicator.getTdColumn().getJavaType();
        DataminingType dataminingType = MetadataHelper.getDataminingType(columnIndicator.getTdColumn());
        if (dataminingType == null) {
            dataminingType = MetadataHelper.getDefaultDataminingType(columnIndicator.getTdColumn().getJavaType());
        }
        FormEnum[] forms = null;

        switch (indicatorUnit.getType()) {

        case RowCountIndicatorEnum:
        case NullCountIndicatorEnum:
        case BlankCountIndicatorEnum:
            forms = new FormEnum[] { FormEnum.IndicatorThresholdsForm };

            break;
        case DistinctCountIndicatorEnum:
        case UniqueIndicatorEnum:
        case DuplicateCountIndicatorEnum:

            forms = new FormEnum[] { FormEnum.IndicatorThresholdsForm };

            if (Java2SqlType.isTextInSQL(sqlType)) {
                forms = new FormEnum[] { FormEnum.TextParametersForm, FormEnum.IndicatorThresholdsForm };
            }

            break;
        case MinLengthIndicatorEnum:
        case MaxLengthIndicatorEnum:
        case AverageLengthIndicatorEnum:

            forms = new FormEnum[] { FormEnum.TextLengthForm, FormEnum.IndicatorThresholdsForm };

            break;
        case FrequencyIndicatorEnum:
            if (dataminingType == DataminingType.INTERVAL) {
                if (Java2SqlType.isNumbericInSQL(sqlType)) {

                    forms = new FormEnum[] { FormEnum.FreqBinsDesignerForm };
                }

                if (Java2SqlType.isDateInSQL(sqlType)) {

                    forms = new FormEnum[] { FormEnum.FreqTimeSliceForm };
                }
            } else if (dataminingType == DataminingType.NOMINAL) {

                if (Java2SqlType.isNumbericInSQL(sqlType)) {

                    forms = new FormEnum[] { FormEnum.NumbericNominalForm };
                }
            } else if (Java2SqlType.isTextInSQL(sqlType)) {

                forms = new FormEnum[] { FormEnum.FreqTextParametersForm, FormEnum.TextLengthForm };
            }

            break;
        case ModeIndicatorEnum:
            if (dataminingType == DataminingType.INTERVAL && Java2SqlType.isNumbericInSQL(sqlType)) {

                forms = new FormEnum[] { FormEnum.BinsDesignerForm };

            } else if (Java2SqlType.isTextInSQL(sqlType)) {

                forms = new FormEnum[] { FormEnum.TextParametersForm };
            }

            break;
        case BoxIIndicatorEnum:
            forms = new FormEnum[] { FormEnum.DataThresholdsForm };

            break;
        case MeanIndicatorEnum:
        case MedianIndicatorEnum:
        case LowerQuartileIndicatorEnum:
        case UpperQuartileIndicatorEnum:
        case MinValueIndicatorEnum:
        case MaxValueIndicatorEnum:
            forms = new FormEnum[] { FormEnum.IndicatorThresholdsForm };

            break;

        case RegexpMatchingIndicatorEnum:
        case SqlPatternMatchingIndicatorEnum:
            forms = new FormEnum[] { FormEnum.IndicatorThresholdsForm };

            break;

        default:

        }

        return forms;
    }

    public static boolean isExsitingForm(IndicatorUnit indicatorUnit) {
        if (getForms(indicatorUnit) != null) {
            return true;
        }

        return false;
    }
}
