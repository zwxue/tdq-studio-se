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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * 
 * @author Hallam Mohamed Amine
 * @date 11/08/2009
 */

public class DatePatternRetriever {

    // TODO put these mappings in a file so that it can be easily enriched
    private List<ModelMatcher> modelMatchers = new ArrayList<ModelMatcher>();

    private static Logger logger = Logger.getLogger(DatePatternRetriever.class);

    // constructor
    public DatePatternRetriever() {
        // TODO initialization should be done in a method
    }

    // initialization method of modelMatchers from external file with loading all patterns names and their regular
    // expressions
    public void initModel2Regex(File PatternFile) {
        try {
            FileReader fr = new FileReader(PatternFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    StringTokenizer string = new StringTokenizer(line, "=\n");
                    while (string.hasMoreTokens()) {
                        String key = string.nextToken().replace("\"", "");
                        String val = string.nextToken().replace("\"", "");
                        // System.out.print(key+"\n");
                        modelMatchers.add(new ModelMatcher(key, val));
                    }
                }
            } finally {
                // TODO should be closed even when an exception appears (use finally close)
                br.close();
            }
        } catch (FileNotFoundException e) {
            System.out.print("File not found");
        } catch (IOException e) {
            System.out.print("Problem when reading");
        }
    }

    // this method returns patterns
    public void handle(String expression) {
        // TODO this method should return an ordered list of objects which contain the model and the score

        for (ModelMatcher patternMatcher : this.modelMatchers) {
            if (patternMatcher.matches(expression)) {
                patternMatcher.increment();
            }
        }
    }

    public void showResults() {
        for (ModelMatcher patternMatcher : this.modelMatchers) {
            if (patternMatcher.getScore() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info(patternMatcher.getModel() + " : " + patternMatcher.getScore() + "\n");
                }
            }
        }
    }

}
