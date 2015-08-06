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
package org.talend.dataquality.matching.date.pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.talend.dataquality.matching.i18n.Messages;

/**
 * @author Hallam mohamed amine
 * @date 17/08/2009
 */
public final class Main {

    private Main() {
    }

    private static Logger logger = Logger.getLogger(DatePatternRetriever.class);

    /**
     * @param fileDates : parse dates stored on external file
     */
    public static void parseFile(File fileDates, DatePatternRetriever patt) {
        try {
            FileReader fr = new FileReader(fileDates);
            BufferedReader br = new BufferedReader(fr);
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    patt.handle(line.replace("\"", ""));//$NON-NLS-1$ //$NON-NLS-2$
                }
            } finally {
                br.close();
            }
        } catch (FileNotFoundException e) {
            logger.warn(Messages.getString("DatePatternRetriever.warn1"));//$NON-NLS-1$
        } catch (IOException e) {
            logger.warn(Messages.getString("DatePatternRetriever.warn2"));//$NON-NLS-1$
        }
    }

    public static void main(String[] args) {

        DatePatternRetriever patt = new DatePatternRetriever();
        File file = new File("PatternsNameAndRegularExpressions.txt");//$NON-NLS-1$
        File filedates = new File("dates.txt");//$NON-NLS-1$
        patt.initModel2Regex(file);
        parseFile(filedates, patt);
        patt.showResults();

    }
}
