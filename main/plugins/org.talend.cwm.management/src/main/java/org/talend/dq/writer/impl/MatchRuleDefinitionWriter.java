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
package org.talend.dq.writer.impl;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.Item;
import org.talend.dataquality.properties.TDQMatchRuleItem;
import org.talend.dataquality.rules.BlockKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.writer.AElementPersistance;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * created by zshen on Aug 20, 2013 Detailled comment
 * 
 */
public class MatchRuleDefinitionWriter extends AElementPersistance {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#notifyResourceChanges()
     */
    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addDependencies(ModelElement element) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return FactoriesUtil.DQRULE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#save(org.talend.core.model.properties.Item, boolean)
     */
    @Override
    public ReturnCode save(Item item, boolean careDependency) {
        TDQMatchRuleItem matchRuleItem = (TDQMatchRuleItem) item;
        MatchRuleDefinition matchRuleDefinition = matchRuleItem.getMatchRule();
        return careDependency ? saveWithDependencies(matchRuleItem, matchRuleDefinition) : saveWithoutDependencies(matchRuleItem,
                matchRuleDefinition);
    }

    /**
     * copy the block/match keys from matchRule to ruleDefinition.
     * 
     * @param ruleDefinition: created one
     * @param matchRule: exported one from the match analysis
     */
    public void copy(MatchRuleDefinition ruleDefinition, MatchRuleDefinition matchRule) {

        // copy blocking keys
        ruleDefinition.getBlockKeys().clear();
        if (matchRule.getBlockKeys() != null && matchRule.getBlockKeys().size() > 0) {
            for (BlockKeyDefinition blockKey : matchRule.getBlockKeys()) {
                BlockKeyDefinition copy = EcoreUtil.copy(blockKey);
                // should not empty the column value when export it
                ruleDefinition.getBlockKeys().add(copy);
            }
        }
        // copy match keys in each match rules
        ruleDefinition.getMatchRules().clear();
        if (matchRule.getMatchRules() != null && matchRule.getMatchRules().size() > 0) {
            for (MatchRule oneMatchRule : matchRule.getMatchRules()) {
                ruleDefinition.getMatchRules().add(EObjectHelper.deepCopy(oneMatchRule));
            }
        }
    }

}
