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
package org.talend.dq.analysis.parameters;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class DQParserRulesParameter extends DQRulesParameter {

    private String parserRuleName;

    private String parserRuleType;

    private String parserRuleValue;

    public DQParserRulesParameter() {
        super();
    }

    public String getParserRuleName() {
        return this.parserRuleName;
    }

    public void setParserRuleName(String parserRuleName) {
        this.parserRuleName = parserRuleName;
    }

    public String getParserRuleType() {
        return this.parserRuleType;
    }

    public void setParserRuleType(String parserRuleType) {
        this.parserRuleType = parserRuleType;
    }

    public String getParserRuleValue() {
        return this.parserRuleValue;
    }

    public void setParserRuleValue(String parserRuleValue) {
        this.parserRuleValue = parserRuleValue;
    }

}
