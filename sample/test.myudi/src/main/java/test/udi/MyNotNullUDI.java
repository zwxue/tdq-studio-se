// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package test.udi;

import org.talend.dataquality.indicators.sql.impl.UserDefIndicatorImpl;

/**
 * @author scorreia
 * 
 * A very simple example of a java implementation of a user defined indicator. This indicator returns a count and must
 * belong to the "user defined count" category defined in the Indicator editor. It implements the minimum number of
 * required methods.
 */
public class MyNotNullUDI extends UserDefIndicatorImpl {

    private long myCount = 0;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.Indicator#finalizeComputation()
     */
    public boolean finalizeComputation() {
        // nothing to do
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.Indicator#handle(java.lang.Object)
     */
    public boolean handle(Object data) {
        if (data != null) {
            this.myCount++;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.Indicator#reset()
     */
    public boolean reset() {
        this.myCount = 0;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.sql.impl.UserDefIndicatorImpl#getUserCount()
     */
    @Override
    public Long getUserCount() {
        return this.myCount;
    }

}
