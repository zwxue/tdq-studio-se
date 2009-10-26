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
import java.util.regex.Pattern;

/**
 * 
 * @author Hallam Mohamed Amine
 * @date 11/08/2009
 */

class ModelMatcher {

    // model is the pattern like "MM DD YY"
    private String model;

    private String regex;

    public ModelMatcher(String model, String regex) {
        this.model = model;
        this.regex = regex;
    }

    public String getRegex() {
        return this.regex;
    }

    // get pattern's name
    public String getModel() {
        return this.model;
    }

    public boolean matches(String expression) {
        return Pattern.matches(this.getRegex(), expression);
    }
}
