// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.dbms;

import org.junit.Test;


/**
 * DOC scorreia  class global comment. Detailled comment
 */
public class MSSqlDbmsLanguageTest {

    
    private String charsToReplace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String replacementChars = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999";

    String[][] valuesNresults = { { "abc", "aaa" }, { "a1e", "a9a" }, { "dkmné", "aaaaa" } };

    /**
     * Test method for {@link org.talend.dq.dbms.MSSqlDbmsLanguage#getPatternFinderFunction(java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    public void testGetPatternFinderFunction() {
        MSSqlDbmsLanguage dbms = new MSSqlDbmsLanguage();
        String value = "toto";
        System.out.println(dbms.getPatternFinderFunction(value, charsToReplace, replacementChars));
    }

}
