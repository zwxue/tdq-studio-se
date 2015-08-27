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
package org.talend.dataquality.datamasking;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.GenerateSsnUk;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 20 août 2015 Detailled comment
 *
 */
public class GenerateSsnUkTest {

    private String output;

    private GenerateSsnUk gsuk = new GenerateSsnUk();

    @Before
    public void setUp() throws Exception {
        gsuk.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = gsuk.generateMaskedRow(null);
        assertEquals(output, "HH 080 752 722 B"); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gsuk.keepNull = true;
        output = gsuk.generateMaskedRow(null);
        assertEquals(output, null);
    }
}
