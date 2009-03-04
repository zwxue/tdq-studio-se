// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.talend.dataquality.rules.RulesFactory;
import org.talend.dataquality.rules.WhereRule;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class DqRuleBuilder {

    static Logger log = Logger.getLogger(DqRuleBuilder.class);

    private boolean initialized = false;

    private WhereRule whereRule;

    public boolean initializeDqRuleBuilder(String ruleName) {

        if (initialized) {
            log.warn("Pattern already initialized. ");
            return false;
        }

        this.whereRule = RulesFactory.eINSTANCE.createWhereRule();
        whereRule.setName(ruleName);

        return true;
    }

    public WhereRule getWhereRule() {
        return whereRule;
    }

}
