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
package org.talend.dataprofiler.core.ui.editor.dqrules;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataquality.properties.TDQBusinessRuleItem;

/**
 * DOC klliu business rule editor input.
 */
public class BusinessRuleItemEditorInput extends AbstractItemEditorInput {

    // FIXME remove it.
    private IRepositoryViewObject viewObject = null;

    private TDQBusinessRuleItem item = null;

    /**
     * DOC klliu TDQBusinessRuleItemEditorInput constructor comment.
     * 
     * @param reposViewObj
     */
    public BusinessRuleItemEditorInput(Item item) {
        super(item);
        this.item = (TDQBusinessRuleItem) item;

    }

    @Override
    public String getName() {
        return getPath() + item.getDqrule().getName();
    }

    @Override
    public String getToolTipText() {
        return getPath() + item.getDqrule().getName();
    }

    public TDQBusinessRuleItem getTDQBusinessRuleItem() {
        return item;
    }

    public String getModelElementUuid() {
        if (this.item != null) {
            return ResourceHelper.getUUID(this.item.getDqrule());
        }
        return super.getModelElementUuid();
    }
}
