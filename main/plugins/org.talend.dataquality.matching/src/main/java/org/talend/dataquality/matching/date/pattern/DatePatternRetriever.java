// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.talend.dataquality.matching.i18n.Messages;

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

    private static Logger logger = Logger.getLogger(DatePatternRetriever.class);

    // constructor
    public DatePatternRetriever() {

    }

    public void initModel2Regex(InputStream inStream) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer string = new StringTokenizer(line, "=\n");//$NON-NLS-1$
                while (string.hasMoreTokens()) {
                    String key = string.nextToken().replace("\"", "");//$NON-NLS-1$ //$NON-NLS-2$
                    String val = string.nextToken().replace("\"", "");//$NON-NLS-1$ //$NON-NLS-2$
                    modelMatchers.add(new ModelMatcher(key, val));
                }
            }
        } catch (IOException e) {
            logger.warn(Messages.getString("DatePatternRetriever.warn2"));//$NON-NLS-1$
        }
        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException e) {
                logger.warn("Error closing input stream");//$NON-NLS-1$
            }
        }
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                logger.warn("Error closing buffered reader");//$NON-NLS-1$
            }
        }
    }

    // initialization method of modelMatchers
    public void initModel2Regex(File patternFile) {
        try {
            initModel2Regex(new FileInputStream(patternFile));
        } catch (FileNotFoundException e) {
            logger.warn(Messages.getString("DatePatternRetriever.warn1"));//$NON-NLS-1$
        }
    }

    /**
     * @param expression
     */
    public void handle(String expression) {
        for (ModelMatcher patternMatcher : this.modelMatchers) {
            if (patternMatcher.matches(expression)) {
                patternMatcher.increment();
            }
        }
    }

    /**
     * method to show results on screen console.
     */
    void showResults() {
        this.getOrderedModelMatchers();
        for (ModelMatcher patternMatcher : this.modelMatchers) {
            if (patternMatcher.getScore() > 0) {
                logger.info(patternMatcher.getModel() + " : " + patternMatcher.getScore() + "\n");//$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }

    /**
     * sort pattern (ModelMatchers) according to their score.
     */
    private void getOrderedModelMatchers() {
        Collections.sort(this.modelMatchers);
    }

    /**
     * 
     * method "getRegex".
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
