// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.util.regex.Pattern;

/**
 * 
 * @author Hallam Mohamed Amine
 * @date 11/08/2009
 */

@SuppressWarnings("unchecked")
public class ModelMatcher implements java.lang.Comparable {

    // model is the pattern like "MM DD YY"
    private String model;

    private int score;

    private String regex;

    public ModelMatcher(String model, String regex) {
        this.model = model;
        this.regex = regex;
        this.score = 0;
    }

    public String getRegex() {
        return this.regex;
    }

    public String getModel() {
        return this.model;
    }

    /**
     * @param expression
     * @return boolean true:match , false : don't match
     */
    public boolean matches(String expression) {
        // if the pattern match onetime , we increase his score
        if (Pattern.matches(this.getRegex(), expression)) {
            this.score++;
        }
        return Pattern.matches(this.getRegex(), expression);
    }

    public int getScore() {
        return this.score;
    }

    // comparison method used to sort modelMatchers
    public int compareTo(Object other) {
        if (this.getScore() > ((ModelMatcher) other).getScore())
            return -1;
        else if (this.getScore() == ((ModelMatcher) other).getScore())
            return 0;
        else {
            return 1;
        }
    }
}
