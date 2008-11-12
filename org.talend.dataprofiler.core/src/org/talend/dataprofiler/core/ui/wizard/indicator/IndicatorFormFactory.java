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
package org.talend.dataprofiler.core.ui.wizard.indicator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.utils.FormEnum;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public final class IndicatorFormFactory {

    private IndicatorFormFactory() {

    }

    public static AbstractIndicatorForm createForm(Composite parent, FormEnum formEnum) {
        AbstractIndicatorForm form = null;

        switch (formEnum) {
        case BinsDesignerForm:

            form = new BinsDesignerForm(parent, SWT.NONE);

            break;
        case TextLengthForm:

            form = new TextLengthForm(parent, SWT.NONE);

            break;
        case FreqTextLengthForm:

            form = new FreqTextLengthForm(parent, SWT.NONE);

            break;
        case TextParametersForm:

            form = new TextParametersForm(parent, SWT.NONE);

            break;
        case DataThresholdsForm:

            form = new DataThresholdsForm(parent, SWT.NONE);

            break;
        case TimeSlicesForm:

            form = new TimeSlicesForm(parent, SWT.NONE);

            break;
        case FreqBinsDesignerForm:

            form = new FreqBinsDesignerForm(parent, SWT.NONE);

            break;
        case FreqTextParametersForm:

            form = new FreqTextParametersForm(parent, SWT.NONE);

            break;
        case FreqTimeSliceForm:

            form = new FreqTimeSliceForm(parent, SWT.NONE);

            break;

        case IndicatorThresholdsForm:

            form = new IndicatorThresholdsForm(parent, SWT.NONE);

            break;

        case NumbericNominalForm:

            form = new NumbericNominalForm(parent, SWT.NONE);
            break;

        case ExpectedValueForm:

            form = new ExpectedValueForm(parent, SWT.NONE);
            break;

        default:
        }

        return form;
    }
}
