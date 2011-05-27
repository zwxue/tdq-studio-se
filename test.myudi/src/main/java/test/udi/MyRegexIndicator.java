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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.JavaUDIIndicatorParameter;
import org.talend.dataquality.indicators.definition.IndicatorDefinitionParameter;
import org.talend.dataquality.indicators.sql.impl.JavaUserDefIndicatorImpl;

/**
 * @author sizhaoliu
 * 
 * Java implementation of a user defined indicator who returns matching count of a defined regex pattern.
 * 
 * This example shows how to retrieve parameters from user interface of the indicator.
 */
public class MyRegexIndicator extends JavaUserDefIndicatorImpl {

    private Pattern pattern;

    @Override
    public boolean prepare() {
        String paramValue = "";
        List<Pattern> isSuitPatten = new ArrayList<Pattern>();
        if (parameters != null) {
            final Domain dataValidDomain = parameters.getIndicatorValidDomain();
            if (dataValidDomain != null) {
                for (JavaUDIIndicatorParameter param : dataValidDomain.getJavaUDIIndicatorParameter()) {
                    if (param != null) {
                        if ("index_path".equals(param.getKey())) {
                            paramValue = param.getValue();
                            pattern = Pattern.compile(paramValue);
                            isSuitPatten.add(pattern);
                        }
                    }
                }
            }
        } else {
            EList<IndicatorDefinitionParameter> indicatorDefParameters = this.getIndicatorDefinition()
                    .getIndicatorDefinitionParameter();
            for (IndicatorDefinitionParameter indicatorDefinitionParameter : indicatorDefParameters) {
                String key = indicatorDefinitionParameter.getKey();
                if ("index_path".equals(key)) {
                    paramValue = indicatorDefinitionParameter.getValue();
                    pattern = Pattern.compile(paramValue);
                    isSuitPatten.add(pattern);
                }
            }

        }
        this.reset();
        return !isSuitPatten.isEmpty();
    }

    @Override
    public boolean reset() {
        this.matchingValueCount = 0L;
        this.notMatchingValueCount = NOT_MATCHING_VALUE_COUNT_EDEFAULT;
        return true;
    }

    @Override
    public boolean handle(Object data) {
        if (data != null && pattern.matcher(data.toString()).matches()) {
            // Fixme if the matchingValueCount is null, how to do this.
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.sql.impl.JavaUserDefIndicatorImpl#getCount()
     */
    @Override
    public Long getCount() {
        return this.matchingValueCount;
    }

}
