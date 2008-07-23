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
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class IndicatorThresholdsForm extends DataThresholdsForm {

    public IndicatorThresholdsForm(Composite parent, int style, AbstractIndicatorParameter parameter) {
        super(parent, style, parameter);
    }

    @Override
    public String getFormName() {

        return FormEnum.IndicatorThresholdsForm.getFormName();
    }

}
