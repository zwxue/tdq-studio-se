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
import org.talend.dataquality.datamasking.Functions.ReplaceSsnLong;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceSsnLongTest {

    private long output;

    private ReplaceSsnLong rsl = new ReplaceSsnLong();

    @Test
    public void testGood1() {
        long input = 123456789L;
        rsl.parameters = "2".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = rsl.generateMaskedRow(input);
        long l = 6789L;
        assertEquals(output, l);
    }

    @Test
    public void testGood2() {
        long input = 123456789012345L;
        rsl.parameters = "7".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = rsl.generateMaskedRow(input);
        long l = 12345L;
        assertEquals(output, l);
    }

    @Test
    public void testBad() {
        long input = 235698741L;
        rsl.parameters = "77".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = rsl.generateMaskedRow(input);
        assertEquals(output, 8741L);
    }

}
