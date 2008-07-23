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
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class TextParameter extends AbstractIndicatorParameter {

    private boolean isIngoreCase;

    private int numOfShown;

    /**
     * Getter for isIngoreCase.
     * 
     * @return the isIngoreCase
     */
    public boolean isIngoreCase() {
        return this.isIngoreCase;
    }

    /**
     * Sets the isIngoreCase.
     * 
     * @param isIngoreCase the isIngoreCase to set
     */
    public void setIngoreCase(boolean isIngoreCase) {
        this.isIngoreCase = isIngoreCase;
    }

    public int getNumOfShown() {
        return numOfShown;
    }

    public void setNumOfShown(int numOfShown) {
        this.numOfShown = numOfShown;
    }

    @Override
    public FormEnum getFormEnum() {

        return FormEnum.TextParametersForm;
    }
}
