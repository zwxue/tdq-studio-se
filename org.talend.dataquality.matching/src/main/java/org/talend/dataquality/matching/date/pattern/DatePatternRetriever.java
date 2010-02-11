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
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * 
 * @author Hallam Mohamed Amine
 * @date 11/08/2009
 */

public class DatePatternRetriever {

    private List<ModelMatcher> modelMatchers = new ArrayList<ModelMatcher>();

    public List<ModelMatcher> getModelMatchers() {
        return modelMatchers;
    }

    public void setModelMatchers(List<ModelMatcher> modelMatchers) {
        this.modelMatchers = modelMatchers;
    }

    private static Logger logger = Logger.getLogger(DatePatternRetriever.class);

    // constructor
    public DatePatternRetriever() {

    }

    // initialization method of modelMatchers
    public void initModel2Regex(File patternFile) {
        try {
            FileReader fr = new FileReader(patternFile);
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
                br.close();
            }
        } catch (FileNotFoundException e) {
            logger.warn("File not found");
        } catch (IOException e) {
            logger.warn("Problem when reading");
        }
    }

    /**
     * @param expression
     */
    public void handle(String expression) {
        for (ModelMatcher patternMatcher : this.modelMatchers) {
            if (patternMatcher.matches(expression)) {
                // patternMatcher.increment();
            }
        }
    }

    /**
     * method to show results on screen console
     */
    public void showResults() {
        this.getOrderedModelMatchers();
        for (ModelMatcher patternMatcher : this.modelMatchers) {
            if (patternMatcher.getScore() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info(patternMatcher.getModel() + " : " + patternMatcher.getScore() + "\n");
                }
            }
        }
    }

    // method witch sort ModelMatchers according their scores
    /**
     * sort pattern according to theirs score
     */
    @SuppressWarnings("unchecked")
    public void getOrderedModelMatchers() {
        Collections.sort(this.modelMatchers);
    }

    /**
     * 
     * DOC zshen Comment method "getRegex".
     * 
     * @param model the model of matcher.
     * @return if can find corresponding to matcher return it's the Regex of matcher else return null;
     */
    public String getRegex(String model) {
        for (ModelMatcher matcher : this.getModelMatchers()) {
            if (matcher.getModel().equals(model)) {
                return matcher.getRegex();
            }
        }
        return null;
    }
}
