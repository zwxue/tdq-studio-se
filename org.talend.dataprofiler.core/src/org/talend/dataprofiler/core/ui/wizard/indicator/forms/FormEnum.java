// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.indicator.forms;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.TableIndicatorUnit;
import org.talend.dataprofiler.help.HelpPlugin;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dq.helper.UDIHelper;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum FormEnum {

    BinsDesignerForm("Bins Designer", "html/wizard/indicator/BinsDesigner.html"), //$NON-NLS-1$ //$NON-NLS-2$
    FreqBinsDesignerForm("Bins Designer", "html/wizard/indicator/BinsDesigner.html"), //$NON-NLS-1$ //$NON-NLS-2$
    TextParametersForm("Text Parameters", "html/wizard/indicator/TextParameter.html"), //$NON-NLS-1$ //$NON-NLS-2$
    FreqTextParametersForm("Text Parameters", "html/wizard/indicator/TextParameter.html"), //$NON-NLS-1$ //$NON-NLS-2$
    TimeSlicesForm("Time Slices", "html/wizard/indicator/TimeSlice.html"), //$NON-NLS-1$ //$NON-NLS-2$
    FreqTimeSliceForm("Time Slices", "html/wizard/indicator/TimeSlice.html"), //$NON-NLS-1$ //$NON-NLS-2$
    DataThresholdsForm("Data Thresholds", "html/wizard/indicator/DataThreshold.html"), //$NON-NLS-1$ //$NON-NLS-2$
    IndicatorThresholdsForm("Indicator Thresholds", "html/wizard/indicator/IndicatorThresholds.html"), //$NON-NLS-1$ //$NON-NLS-2$
    TextLengthForm("Text Length", "html/wizard/indicator/TextLength.html"), //$NON-NLS-1$ //$NON-NLS-2$
    FreqTextLengthForm("Blank Options", "html/wizard/indicator/BlankOptions.html"), //$NON-NLS-1$ //$NON-NLS-2$
    NumbericNominalForm("Frequency Table Parameters", ""), //$NON-NLS-1$ //$NON-NLS-2$
    JavaUDIParametersForm("Java UDI Parameters", ""), //$NON-NLS-1$ //$NON-NLS-2$
    ExpectedValueForm("Expected Value", "html/wizard/indicator/ExpectedValue.html"), //$NON-NLS-1$ //$NON-NLS-2$
    JavaOptionsForm("Java Options", "html/wizard/indicator/JavaOptions.html"); //$NON-NLS-1$ //$NON-NLS-2$

    private String formName;

    private String helpHref;

    FormEnum(String formName, String helpHref) {
        this.formName = formName;
        this.helpHref = helpHref;
    }

    public String getFormName() {
        return formName;
    }

    public String getHelpHref() {
        return "/" + HelpPlugin.PLUGIN_ID + "/" + helpHref; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static String[] getHelpHref(IndicatorUnit indicatorUnit) {
        List<String> tempList = new ArrayList<String>();
        for (FormEnum oneForm : getForms(indicatorUnit)) {
            tempList.add(oneForm.getHelpHref());
        }

        return tempList.toArray(new String[tempList.size()]);
    }

    public static String[] getHelpHref(TableIndicatorUnit indicatorUnit) {
        List<String> tempList = new ArrayList<String>();
        for (FormEnum oneForm : getForms(indicatorUnit)) {
            tempList.add(oneForm.getHelpHref());
        }

        return tempList.toArray(new String[tempList.size()]);
    }

    public static String getFirstFormHelpHref(IndicatorUnit indicatorUnit) {
        return getHelpHref(indicatorUnit)[0];
    }

    public static String getFirstFormHelpHref(TableIndicatorUnit indicatorUnit) {
        return getHelpHref(indicatorUnit)[0];
    }

    /**
     * DOC zqin Comment method "getForms".
     * 
     * @param dataminingType
     * @param sqlType
     */
    public static FormEnum[] getForms(IndicatorUnit indicatorUnit) {
        // ColumnIndicator columnIndicator = indicatorUnit.getParentColumn();
        ModelElementIndicator modelElementIndicator = indicatorUnit.getModelElementIndicator();
        int sqlType = null != modelElementIndicator ? modelElementIndicator.getJavaType() : 0;
        ColumnIndicator columnIndicator = ModelElementIndicatorHelper.switchColumnIndicator(indicatorUnit);
        DataminingType dataminingType = columnIndicator == null ? null : MetadataHelper.getDataminingType(columnIndicator
                .getTdColumn());
        if (dataminingType == null) {
            dataminingType = null != modelElementIndicator ? MetadataHelper.getDefaultDataminingType(modelElementIndicator
                    .getJavaType()) : DataminingType.NOMINAL;
        }
        FormEnum[] forms = null;

        switch (indicatorUnit.getType()) {

        case RowCountIndicatorEnum:
        case NullCountIndicatorEnum:
        case BlankCountIndicatorEnum:
        case DefValueCountIndicatorEnum:

            forms = new FormEnum[] { IndicatorThresholdsForm };

            break;
        case DistinctCountIndicatorEnum:
        case UniqueIndicatorEnum:
        case DuplicateCountIndicatorEnum:

            forms = new FormEnum[] { IndicatorThresholdsForm };

            if (Java2SqlType.isTextInSQL(sqlType)) {
                forms = new FormEnum[] { TextParametersForm, IndicatorThresholdsForm };
            }

            break;
        case MinLengthIndicatorEnum:
        case MaxLengthIndicatorEnum:
        case AverageLengthIndicatorEnum:
            forms = new FormEnum[] { IndicatorThresholdsForm };

            break;
        case MinLengthWithBlankIndicatorEnum:
        case MinLengthWithNullIndicatorEnum:
        case MinLengthWithBlankNullIndicatorEnum:
        case MaxLengthWithBlankIndicatorEnum:
        case MaxLengthWithBlankNullIndicatorEnum:
        case MaxLengthWithNullIndicatorEnum:
        case AverageLengthWithBlankIndicatorEnum:
        case AverageLengthWithNullBlankIndicatorEnum:
        case AverageLengthWithNullIndicatorEnum:

            // forms = new FormEnum[] { TextLengthForm, IndicatorThresholdsForm };

            break;
        case FrequencyIndicatorEnum:
        case LowFrequencyIndicatorEnum:
        case PatternFreqIndicatorEnum:
        case PatternLowFreqIndicatorEnum:
        case DatePatternFreqIndicatorEnum:
            if (dataminingType == DataminingType.INTERVAL) {
                if (Java2SqlType.isNumbericInSQL(sqlType)) {

                    forms = new FormEnum[] { FreqBinsDesignerForm };
                }

                if (Java2SqlType.isDateInSQL(sqlType)) {

                    forms = new FormEnum[] { FreqTimeSliceForm };
                }
            } else if (Java2SqlType.isTextInSQL(sqlType)) {

                forms = new FormEnum[] { FreqTextParametersForm, FreqTextLengthForm, JavaOptionsForm };
            } else if (dataminingType == DataminingType.NOMINAL) {

                if (Java2SqlType.isNumbericInSQL(sqlType)) {

                    forms = new FormEnum[] { NumbericNominalForm };
                }
            }

            break;
        case ModeIndicatorEnum:
            if (dataminingType == DataminingType.INTERVAL && Java2SqlType.isNumbericInSQL(sqlType)) {

                forms = new FormEnum[] { BinsDesignerForm, ExpectedValueForm };

            } else if (Java2SqlType.isTextInSQL(sqlType)) {

                forms = new FormEnum[] { TextParametersForm, ExpectedValueForm };
            }

            break;
        case BoxIIndicatorEnum:
        case RangeIndicatorEnum:
        case IQRIndicatorEnum:
            forms = new FormEnum[] { DataThresholdsForm };

            break;
        case MeanIndicatorEnum:
        case MedianIndicatorEnum:
        case LowerQuartileIndicatorEnum:
        case UpperQuartileIndicatorEnum:
        case MinValueIndicatorEnum:
        case MaxValueIndicatorEnum:
            forms = new FormEnum[] { IndicatorThresholdsForm };

            break;
        case RegexpMatchingIndicatorEnum:
        case SqlPatternMatchingIndicatorEnum:
        case AllMatchIndicatorEnum:
            forms = new FormEnum[] { IndicatorThresholdsForm };

            break;
        case SoundexIndicatorEnum:
        case SoundexLowIndicatorEnum:
            forms = new FormEnum[] { NumbericNominalForm };

            break;

        case UserDefinedIndicatorEnum:
            if (UDIHelper.isFrequency((indicatorUnit.getIndicator()))) {
                forms = new FormEnum[] { NumbericNominalForm };
            }

            break;
        case JavaUserDefinedIndicatorEnum:
            forms = new FormEnum[] { JavaUDIParametersForm };

            break;

        case BinFrequencyIndicatorEnum:
        case BinLowFrequencyIndicatorEnum:
            if (Java2SqlType.isNumbericInSQL(sqlType)) {

                forms = new FormEnum[] { FreqBinsDesignerForm };
            }
            break;

        default:

        }

        return forms;
    }

    /**
     * 
     * DOC xqliu Comment method "getForms".
     * 
     * @param indicatorUnit
     * @return
     */
    public static FormEnum[] getForms(TableIndicatorUnit indicatorUnit) {
        FormEnum[] forms = null;
        switch (indicatorUnit.getType()) {
        case RowCountIndicatorEnum:
            forms = new FormEnum[] { IndicatorThresholdsForm };
            break;
        case WhereRuleIndicatorEnum:
            forms = new FormEnum[] { IndicatorThresholdsForm };
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

    public static boolean isExsitingForm(TableIndicatorUnit indicatorUnit) {
        if (getForms(indicatorUnit) != null) {
            return true;
        }

        return false;
    }
}
