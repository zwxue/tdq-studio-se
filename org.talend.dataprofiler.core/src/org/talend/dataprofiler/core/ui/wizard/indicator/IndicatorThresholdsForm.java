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

import org.eclipse.swt.widgets.Composite;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataquality.helpers.IndicatorHelper;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class IndicatorThresholdsForm extends DataThresholdsForm {

    public IndicatorThresholdsForm(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    protected void initialize() {
        String[] indicatorThreshold = IndicatorHelper.getIndicatorThreshold(parameters);
        if (indicatorThreshold != null) {
            lowerText.setText(indicatorThreshold[0]);

            higherText.setText(indicatorThreshold[1]);
        }
    }

    @Override
    public FormEnum getFormEnum() {
        return FormEnum.IndicatorThresholdsForm;
    }

    @Override
    public boolean performFinish() {
        String min = lowerText.getText();
        String max = higherText.getText();

        if ("".equals(min) && "".equals(max)) {
            parameters.setIndicatorValidDomain(null);
        } else {
            IndicatorHelper.setIndicatorThreshold(parameters, min, max);
        }

        return true;
    }

    @Override
    protected void addFields() {
        super.addFields();
        group.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.setThresholds")); //$NON-NLS-1$
    }

}
