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
 * DOC Zqin class global comment. Detailled comment
 */
public class FreqTextLengthForm extends TextLengthForm {

    public FreqTextLengthForm(Composite parent, int style, AbstractIndicatorParameter parameter) {
        super(parent, style, parameter);
    }

    @Override
    protected void addFields() {
        super.addFields();

        this.blankBtn.setText("aggregate blanks");
        this.nullBtn.setText("aggregate nulls with blanks");
    }

    @Override
    public FormEnum getFormEnum() {
        return FormEnum.FreqTextLengthForm;
    }
}
