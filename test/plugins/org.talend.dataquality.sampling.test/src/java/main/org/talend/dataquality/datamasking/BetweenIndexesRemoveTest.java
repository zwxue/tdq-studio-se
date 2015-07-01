package org.talend.dataquality.datamasking;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.BetweenIndexesRemove;

public class BetweenIndexesRemoveTest {

    private BetweenIndexesRemove bir = new BetweenIndexesRemove();

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    @Test
    public void testGood() {
        bir.parameters = "2, 4".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bir.generateMaskedRow(input);
        assertEquals("Se", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        bir.parameters = "-2, 8".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        bir.parameters = "1".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bir.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad2() {
        bir.parameters = "lk, df".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bir.generateMaskedRow(input);
        assertEquals(input, output);
    }

}
