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


/**
 * DOC zqin  class global comment. Detailled comment
 * <br/>
 *
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 *
 */
public class TextLengthParameter extends AbstractIndicatorParameter {

    private boolean isUseBlank;
    
    private boolean isUseNull;

    
    /**
     * Getter for isUseBlank.
     * @return the isUseBlank
     */
    public boolean isUseBlank() {
        return this.isUseBlank;
    }

    
    /**
     * Sets the isUseBlank.
     * @param isUseBlank the isUseBlank to set
     */
    public void setUseBlank(boolean isUseBlank) {
        this.isUseBlank = isUseBlank;
    }

    
    /**
     * Getter for isUseNull.
     * @return the isUseNull
     */
    public boolean isUseNull() {
        return this.isUseNull;
    }

    
    /**
     * Sets the isUseNull.
     * @param isUseNull the isUseNull to set
     */
    public void setUseNull(boolean isUseNull) {
        this.isUseNull = isUseNull;
    }
}
