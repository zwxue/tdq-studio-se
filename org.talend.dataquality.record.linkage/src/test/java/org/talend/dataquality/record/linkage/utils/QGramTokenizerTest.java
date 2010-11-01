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
package org.talend.dataquality.record.linkage.utils;

import java.util.List;

import org.junit.Test;


/**
 * DOC scorreia  class global comment. Detailled comment
 */
public class QGramTokenizerTest {

    private static final String INPUT = "DOC scorreia  class global comment. Detailled comment";

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.utils.QGramTokenizer#tokenizeToArrayList(java.lang.String, int)}.
     */
    @Test
    public void testTokenizeToArrayList() {
        QGramTokenizer tokenizer = new QGramTokenizer();
        List<String> tokenized = tokenizer.tokenizeToArrayList(INPUT, 3);
        for (String token : tokenized) {
            System.out.println(token);
        }
    }

}
