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
package org.talend.dq.nodes;

import java.util.ArrayList;
import java.util.List;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.rules.DQRule;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class RuleRepNode extends DQRepositoryNode {

    private DQRule rule;

    public DQRule getRule() {
        return this.rule;
    }

    /**
     * DOC klliu RuleRepNOde constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public RuleRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        if (object != null && object.getProperty() != null) {
            Item item = object.getProperty().getItem();
            if (item != null && item instanceof TDQBusinessRuleItem) {
                this.rule = ((TDQBusinessRuleItem) item).getDqrule();
            }
        }
    }

    @Override
    public String getLabel() {
        if (this.getRule() != null && this.getRule().getName() != null) {
            return this.getRule().getName();
        }
        return super.getLabel();
    }

    @Override
    public boolean canExpandForDoubleClick() {
        return false;
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        // MOD klliu 2011-06-28 bug 22669
        return new ArrayList<IRepositoryNode>();
    }
}
