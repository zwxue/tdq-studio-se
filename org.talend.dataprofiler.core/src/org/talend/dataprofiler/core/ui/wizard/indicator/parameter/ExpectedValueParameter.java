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

import org.talend.dataprofiler.core.ui.utils.FormEnum;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ExpectedValueParameter extends AbstractIndicatorParameter {

    private String expectedValue;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter#getFormEnum()
     */
    @Override
    public FormEnum getFormEnum() {
        // TODO Auto-generated method stub
        return FormEnum.ExpectedValueForm;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

}
