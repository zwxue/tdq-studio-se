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
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataprofiler.core.ui.utils.AbstractForm.ICheckListener;
import org.talend.dataprofiler.core.ui.wizard.indicator.BinsDesignerForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.DataThresholdsForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.ExpectedValueForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.FreqBinsDesignerForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.FreqTextParametersForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.FreqTimeSliceForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.IndicatorThresholdsForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.NumbericNominalForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.TextLengthForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.TextParametersForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.TimeSlicesForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.BinsDesignerParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.DataThresholdsParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.ExpectedValueParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.IndicatorThresholdsParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.NumbericNominalParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TextLengthParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TextParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TimeSlicesParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public final class FormFactory {

    private FormFactory() {

    }

    public static AbstractIndicatorForm[] createForm(Composite parent, ICheckListener listener, FormEnum[] formTypes,
            Map<FormEnum, AbstractIndicatorParameter> paramMap) {

        List<AbstractIndicatorForm> list = new ArrayList<AbstractIndicatorForm>();
        AbstractIndicatorForm form = null;
        AbstractIndicatorParameter parameter = null;

        for (FormEnum oneType : formTypes) {

            switch (oneType) {
            case BinsDesignerForm:
                parameter = paramMap.get(FormEnum.BinsDesignerForm);
                if (parameter == null) {
                    parameter = new BinsDesignerParameter();
                }
                form = new BinsDesignerForm(parent, SWT.NONE, parameter);

                break;
            case TextLengthForm:
                parameter = paramMap.get(FormEnum.TextLengthForm);
                if (parameter == null) {
                    parameter = new TextLengthParameter();
                }
                form = new TextLengthForm(parent, SWT.NONE, parameter);

                break;
            case TextParametersForm:
                parameter = paramMap.get(FormEnum.TextParametersForm);
                if (parameter == null) {
                    parameter = new TextParameter();
                }

                form = new TextParametersForm(parent, SWT.NONE, parameter);

                break;
            case DataThresholdsForm:
                parameter = paramMap.get(FormEnum.DataThresholdsForm);
                if (parameter == null) {
                    parameter = new DataThresholdsParameter();
                }

                form = new DataThresholdsForm(parent, SWT.NONE, parameter);

                break;
            case TimeSlicesForm:
                parameter = paramMap.get(FormEnum.TimeSlicesForm);
                if (parameter == null) {
                    parameter = new TimeSlicesParameter();
                }

                form = new TimeSlicesForm(parent, SWT.NONE, parameter);

                break;
            case FreqBinsDesignerForm:
                parameter = paramMap.get(FormEnum.BinsDesignerForm);
                if (parameter == null) {
                    parameter = new BinsDesignerParameter();
                }

                form = new FreqBinsDesignerForm(parent, SWT.NONE, parameter);

                break;
            case FreqTextParametersForm:
                parameter = paramMap.get(FormEnum.TextParametersForm);
                if (parameter == null) {
                    parameter = new TextParameter();
                }

                form = new FreqTextParametersForm(parent, SWT.NONE, parameter);

                break;
            case FreqTimeSliceForm:
                parameter = paramMap.get(FormEnum.TimeSlicesForm);
                if (parameter == null) {
                    parameter = new TimeSlicesParameter();
                }

                form = new FreqTimeSliceForm(parent, SWT.NONE, parameter);

                break;

            case IndicatorThresholdsForm:
                parameter = paramMap.get(FormEnum.IndicatorThresholdsForm);
                if (parameter == null) {
                    parameter = new IndicatorThresholdsParameter();
                }

                form = new IndicatorThresholdsForm(parent, SWT.NONE, parameter);

                break;

            case NumbericNominalForm:
                parameter = paramMap.get(FormEnum.NumbericNominalForm);
                if (parameter == null) {
                    parameter = new NumbericNominalParameter();
                }

                form = new NumbericNominalForm(parent, SWT.NONE, parameter);
                break;

            case ExpectedValueForm:
                parameter = paramMap.get(FormEnum.ExpectedValueForm);
                if (parameter == null) {
                    parameter = new ExpectedValueParameter();
                }

                form = new ExpectedValueForm(parent, SWT.NONE, parameter);
                break;

            default:
            }

            form.setListener(listener);
            list.add(form);
        }

        return list.toArray(new AbstractIndicatorForm[list.size()]);
    }
}
