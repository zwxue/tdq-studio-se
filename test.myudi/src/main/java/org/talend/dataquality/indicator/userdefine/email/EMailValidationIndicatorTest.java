// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.indicator.userdefine.email;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * created by zhao on 2012-8-27 Detailled comment
 * 
 */
public class EMailValidationIndicatorTest {

    private EMailValidationIndicator emailValidationIndicator = null;

    private final static String[] emails = new String[] {
            "mzhao@talend.cn", "xxxmzhao@talend.com", "minglee.zhao@gmail.com", "paslor@126.com", "paslor@hotmail.com", "mzhao@Talxxx.com", "paslor@sina.com.cn", null, "", " ", "a@", "@b", "@hotmail.com" }; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ 

    /**
     * DOC zhao Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        emailValidationIndicator = new EMailValidationIndicator();
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.indicator.userdefine.email.EMailValidationIndicator#handle(java.lang.Object)}.
     */
    @Test
    public void testHandle() {
        emailValidationIndicator.reset();

        for (String email : emails) {
            emailValidationIndicator.handle(email);
        }
        // The total count
        Assert.assertEquals(13, emailValidationIndicator.getCount().longValue());
        // The matching count
        Assert.assertEquals(2, emailValidationIndicator.getMatchingValueCount().longValue());
    }

    @Test
    public void testIsAddressValid() {
        emailValidationIndicator.reset();
        boolean addressValid = true;
        try {
            addressValid = emailValidationIndicator.isAddressValid(emails[0]);
            Assert.assertFalse(addressValid);

            addressValid = emailValidationIndicator.isAddressValid(emails[1]);

            Assert.assertFalse(addressValid);
            addressValid = emailValidationIndicator.isAddressValid(emails[2]);
            Assert.assertTrue(addressValid);
            addressValid = emailValidationIndicator.isAddressValid(emails[3]);
            Assert.assertTrue(addressValid);
            addressValid = emailValidationIndicator.isAddressValid(emails[4]);
            Assert.assertFalse(addressValid);
            addressValid = emailValidationIndicator.isAddressValid(emails[5]);
            Assert.assertFalse(addressValid);
            addressValid = emailValidationIndicator.isAddressValid(emails[6]);
            Assert.assertFalse(addressValid);
            // addressValid = emailValidationIndicator.isAddressValid(emails[7]); //won't verify assert null here
            // Assert.assertFalse(addressValid);
            addressValid = emailValidationIndicator.isAddressValid(emails[8]);
            Assert.assertFalse(addressValid);
            addressValid = emailValidationIndicator.isAddressValid(emails[9]);
            Assert.assertFalse(addressValid);
            addressValid = emailValidationIndicator.isAddressValid(emails[10]);
            Assert.assertFalse(addressValid);
            addressValid = emailValidationIndicator.isAddressValid(emails[11]);
            Assert.assertFalse(addressValid);
            addressValid = emailValidationIndicator.isAddressValid(emails[12]);
            Assert.assertFalse(addressValid);
        } catch (Throwable e) {
            addressValid = false;
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
