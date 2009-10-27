// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.matching.date.pattern;

import java.io.File;

/**
 * 
 * @author Hallam mohamed amine
 * @date 17/08/2009
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        DatePatternRetriever patt = new DatePatternRetriever();
        File file = new File("PatternsNameAndRegularExpressions.txt");
        patt.initModel2Regex(file);
        patt.handle("2001-31-01 12:21:22");
        patt.handle("12 03 2001");
        patt.handle("12 03 2001");
        patt.handle("12 03 2001");
        patt.handle("Mon, 17 August 2009 2:22:12 GMT");
        patt.handle("08/17/2009 05:19:46");
        patt.handle("08/17/2009 5:19 AM");
        patt.handle("January 17");
        patt.handle("January 17 1998");
        patt.handle("1998 January");
        patt.showResults();
    }
}
