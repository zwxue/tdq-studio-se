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
package org.talend.dq.dqrule;

import java.util.List;

import org.talend.dataquality.rules.DQRule;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class WhereRuleHandler {

    private static WhereRuleHandler instance;

    private WhereRuleHandler() {
    }

    /**
     * DOC xqliu Comment method "getInstance".
     * 
     * @return
     */
    public static WhereRuleHandler getInstance() {
        if (instance == null) {
            instance = new WhereRuleHandler();
        }
        return instance;
    }

    /**
     * DOC xqliu Comment method "getWhereRule".
     * 
     * @param name
     * @return
     */
    public WhereRule getWhereRule(String name) {
        WhereRule result = null;
        @SuppressWarnings("unchecked")
        List<DQRule> rules = (List<DQRule>) DQRuleResourceFileHelper.getInstance().getAllElement();
        for (DQRule rule : rules) {
            if (rule instanceof WhereRule && name.equals(rule.getName())) {
                result = (WhereRule) rule;
                break;
            }
        }
        return result;
    }
}
