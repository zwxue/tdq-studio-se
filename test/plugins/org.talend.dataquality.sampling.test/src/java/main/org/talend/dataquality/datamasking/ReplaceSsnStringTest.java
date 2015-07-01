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
import org.talend.dataquality.datamasking.Functions.ReplaceSsnString;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class ReplaceSsnStringTest {

    private String output;

    private ReplaceSsnString rss = new ReplaceSsnString();

    @Test
    public void testGood1() {
        String input = "123456789"; //$NON-NLS-1$
        rss.parameters = "X".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = rss.generateMaskedRow(input);
        assertEquals(output, "XXXXX6789"); //$NON-NLS-1$
    }

    @Test
    public void testGood2() {
        String input = "123456789012345"; //$NON-NLS-1$
        rss.parameters = "X".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = rss.generateMaskedRow(input);
        assertEquals(output, "XXXXXXXXXX12345"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        String input = "235698741"; //$NON-NLS-1$
        rss.parameters = "8X".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = rss.generateMaskedRow(input);
        assertEquals(output, "XXXXX8741"); //$NON-NLS-1$
    }

}
