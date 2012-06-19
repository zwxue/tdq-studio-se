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
package org.talend.dataquality.standardization.main;

import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class creates the lucene index file to be used in the component.
 */
public final class HandleLuceneTest {

    private final static String filename = HandLuceneImplTest.filename;

    private final static String indexfolder = HandLuceneImplTest.indexfolder;

    /**
     * @param args
     */
    @Test
    public void testHandle() {
        // choose here to test the replace methods
        boolean testReplace = true;
        // choose here the appropriate input file.
        HandleLucene hl = new HandleLuceneImpl();
        hl.createIndex(filename, indexfolder);

        if (!testReplace) {
            return;
        }
        try {

            String res = hl.replaceName(indexfolder, "Philippe", false);//$NON-NLS-1$
            Assert.assertEquals("Philippe", res);
            try {
                String res1 = hl.replaceNameWithCountryInfo(indexfolder, "Philippe", "china", false);//$NON-NLS-1$ $NON-NLS-2$
                Assert.assertEquals("Philippe", res1);

                String res2 = hl.replaceNameWithGenderInfo(indexfolder, "Philippe", "0", false);//$NON-NLS-1$ $NON-NLS-2$
                Assert.assertEquals("Philippe", res2);
                String res3 = hl.replaceNameWithCountryGenderInfo(indexfolder, "Philippe", "china", "1", false);//$NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                Assert.assertEquals("Philippe", res3);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
