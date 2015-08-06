// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.nodes.indicator;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.impl.MedianIndicatorImpl;

/**
 * @author rli
 * 
 */
public class IndicatorsInstanceFactory {

    protected static Logger log = Logger.getLogger(IndicatorsInstanceFactory.class);

    private static Map<String, Indicator> instanceMap = new HashMap<String, Indicator>();

    public static Indicator getIndicatorInstance(Class<? extends Indicator> cla) {
        Indicator indicator = instanceMap.get(cla.getSimpleName());
        if (indicator == null) {
            try {
                indicator = cla.newInstance();
                instanceMap.put(cla.getSimpleName(), indicator);
            } catch (InstantiationException e) {
                log.error(e, e);
            } catch (IllegalAccessException e) {
                log.error(e, e);
            }
        }
        return indicator;
    }

    public static void main(String[] args) {
        IndicatorsInstanceFactory.getIndicatorInstance(MedianIndicatorImpl.class);
    }

}
