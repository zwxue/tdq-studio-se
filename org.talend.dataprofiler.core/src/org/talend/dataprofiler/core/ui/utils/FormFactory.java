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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataprofiler.core.ui.utils.AbstractForm.ICheckListener;
import org.talend.dataprofiler.core.ui.wizard.indicator.BinsDesignerForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.DataThresholdsForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.FreqBinsDesignerForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.FreqTextParametersForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.FreqTimeSliceForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.TextLengthForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.TextParametersForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.TimeSlicesForm;

/**
 * DOC zqin class global comment. Detailled comment
 */
public final class FormFactory {

    private FormFactory() {

    }

    public static AbstractIndicatorForm[] creaeteForm(Composite parent, ICheckListener listener, FormEnum[] formTypes) {

        List<AbstractIndicatorForm> list = new ArrayList<AbstractIndicatorForm>();
        AbstractIndicatorForm form = null;

        for (FormEnum oneType : formTypes) {

            switch (oneType) {
            case BinsDesignerForm:
                form = new BinsDesignerForm(parent, SWT.NONE);
                form.setListener(listener);
                list.add(form);
                break;
            case TextLengthForm:
                form = new TextLengthForm(parent, SWT.NONE);
                form.setListener(listener);
                list.add(form);
                break;
            case TextParametersForm:
                form = new TextParametersForm(parent, SWT.NONE);
                form.setListener(listener);
                list.add(form);
                break;
            case DataThresholdsForm:
                form = new DataThresholdsForm(parent, SWT.NONE);
                form.setListener(listener);
                list.add(form);
                break;
            case TimeSlicesForm:
                form = new TimeSlicesForm(parent, SWT.NONE);
                form.setListener(listener);
                list.add(form);
                break;
            case FreqBinsDesignerForm:
                form = new FreqBinsDesignerForm(parent, SWT.NONE);
                form.setListener(listener);
                list.add(form);
                break;
            case FreqTextParametersForm:
                form = new FreqTextParametersForm(parent, SWT.NONE);
                form.setListener(listener);
                list.add(form);
                break;
            case FreqTimeSliceForm:
                form = new FreqTimeSliceForm(parent, SWT.NONE);
                form.setListener(listener);
                list.add(form);
                break;

            default:
            }
        }

        return list.toArray(new AbstractIndicatorForm[list.size()]);
    }
}
