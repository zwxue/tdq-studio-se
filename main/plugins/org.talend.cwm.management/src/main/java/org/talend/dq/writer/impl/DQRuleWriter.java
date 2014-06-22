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
package org.talend.dq.writer.impl;

import org.apache.log4j.Logger;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.Item;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.writer.AElementPersistance;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class DQRuleWriter extends AElementPersistance {

    static Logger log = Logger.getLogger(DQRuleWriter.class);

    /**
     * DOC bZhou DQRuleWriter constructor comment.
     */
    DQRuleWriter() {
        super();
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

    @Override
    public ReturnCode save(Item item, boolean careDependency) {
        TDQBusinessRuleItem ruleItem = (TDQBusinessRuleItem) item;
        DQRule rule = ruleItem.getDqrule();
        // MOD yyi 2012-02-07 TDQ-4621:Update dependencies(connection) when careDependency is true.
        return careDependency ? saveWithDependencies(ruleItem, rule) : saveWithoutDependencies(ruleItem, rule);
    }

    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save();
    }
}
