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
public class DataThresholdsParameter extends AbstractIndicatorParameter {

    private String minThreshold;
    
    private String maxThreshold;

    
    /**
     * Getter for minThreshold.
     * @return the minThreshold
     */
    public String getMinThreshold() {
        return this.minThreshold;
    }

    
    /**
     * Sets the minThreshold.
     * @param minThreshold the minThreshold to set
     */
    public void setMinThreshold(String minThreshold) {
        this.minThreshold = minThreshold;
    }

    
    /**
     * Getter for maxThreshold.
     * @return the maxThreshold
     */
    public String getMaxThreshold() {
        return this.maxThreshold;
    }

    
    /**
     * Sets the maxThreshold.
     * @param maxThreshold the maxThreshold to set
     */
    public void setMaxThreshold(String maxThreshold) {
        this.maxThreshold = maxThreshold;
    }
    
    
}
