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

import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.ReplaceNumericLong;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceNumericLongTest {

    private long input = 123;

    private long output;

    private ReplaceNumericLong rnl = new ReplaceNumericLong();

    @Test
    public void testGood() {
        rnl.parameters = "6".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        rnl.integerParam = 6;
        output = rnl.generateMaskedRow(input);
        assertEquals(output, 666);
    }

    @Test
    public void testBad() {
        rnl.parameters = "10".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        rnl.integerParam = 10;
        output = rnl.generateMaskedRow(input);
        assertEquals(output, 888);
    }
}
