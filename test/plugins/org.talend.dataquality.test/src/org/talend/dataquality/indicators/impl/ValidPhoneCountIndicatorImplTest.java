// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.indicators.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.TextParameters;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class ValidPhoneCountIndicatorImplTest {

    ValidPhoneCountIndicatorImpl validPhoneCountIndicatorImpl = null;

    private Object data_invalid[] = { "+41446681800", "+086 18611281175", "086 18611281175", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
            "08618611281175", "08618611281175", "123", "", null }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 

    private Object data_valid[] = {
            "86 18611281173", "8618611281175", "+86 18611281175", "+8618611281175", "18611281175", "01062153217" };//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ 

    @Before
    public void setUp() throws Exception {
        validPhoneCountIndicatorImpl = new ValidPhoneCountIndicatorImpl();
        IndicatorParameters createIndicatorParameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        TextParameters textParameters = IndicatorsFactory.eINSTANCE.createTextParameters();
        textParameters.setCountryCode(java.util.Locale.SIMPLIFIED_CHINESE.getCountry());
        createIndicatorParameters.setTextParameter(textParameters);
        validPhoneCountIndicatorImpl.setParameters(createIndicatorParameters);
    }

    @Test
    public void testHandle_1() {
        for (Object obj : data_valid) {
            this.validPhoneCountIndicatorImpl.handle(obj);

        }
        assertEquals(6, validPhoneCountIndicatorImpl.getValidPhoneNumCount().longValue());
    }

    @Test
    public void testHandle_2() {
        for (Object obj : data_invalid) {
            this.validPhoneCountIndicatorImpl.handle(obj);

        }
        assertEquals(0, validPhoneCountIndicatorImpl.getValidPhoneNumCount().longValue());
    }

}
