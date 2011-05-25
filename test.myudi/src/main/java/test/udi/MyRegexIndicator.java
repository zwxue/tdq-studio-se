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

import java.util.regex.Pattern;

import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.JavaUDIIndicatorParameter;
import org.talend.dataquality.indicators.sql.impl.UserDefIndicatorImpl;

/**
 * @author sizhaoliu
 * 
 * Java implementation of a user defined indicator who returns matching count of a defined regex pattern.
 * 
 * This example shows how to retrieve parameters from user interface of the indicator.
 */
public class MyRegexIndicator extends UserDefIndicatorImpl {

    private Pattern pattern;

    @Override
    public boolean prepare() {
        String paramValue = "";
        // if (parameters != null) {
        final Domain dataValidDomain = parameters.getIndicatorValidDomain();
        if (dataValidDomain != null) {
            for (JavaUDIIndicatorParameter param : dataValidDomain.getJavaUDIIndicatorParameter()) {
                if (param != null) {
                    if ("pattern".equals(param.getKey())) {
                        paramValue = param.getValue();
                        pattern = Pattern.compile(paramValue);
                        System.err.println("pattern: " + paramValue);
                    }
                }
            }
        }
        // } else {
        // System.err.println("parameters is null");
        // }
        return super.prepare();
    }

    @Override
    public boolean reset() {
        super.reset();
        this.matchingValueCount = 0L;
        this.notMatchingValueCount = NOT_MATCHING_VALUE_COUNT_EDEFAULT;
        return true;
    }

    @Override
    public boolean handle(Object data) {
        super.handle(data);
        if (data != null && pattern.matcher(data.toString()).matches()) {
            matchingValueCount++;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.Indicator#finalizeComputation()
     */
    @Override
    public boolean finalizeComputation() {
        // compute non matching value
        System.err.println(matchingValueCount + " records matched.");
        this.notMatchingValueCount = count - matchingValueCount;
        System.err.println(notMatchingValueCount + " records unmatched.");
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.sql.impl.UserDefIndicatorImpl#getUserCount()
     */
    @Override
    public Long getUserCount() {
        return this.matchingValueCount;
    }

}
