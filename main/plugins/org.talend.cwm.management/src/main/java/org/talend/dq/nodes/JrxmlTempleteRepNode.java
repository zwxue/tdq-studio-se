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
package org.talend.dq.nodes;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.properties.TDQJrxmlItem;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC klliu  class global comment. Detailled comment
 */
public class JrxmlTempleteRepNode extends DQRepositoryNode {

    private TDQJrxmlItem jrxmlItem;

    public TDQJrxmlItem getJrxmlItem() {
        return this.jrxmlItem;
    }

    /**
     * DOC klliu JrxmlTempleteRepNode constructor comment.
     * @param object
     * @param parent
     * @param type
     */
    public JrxmlTempleteRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        if (object != null && object.getProperty() != null) {
            Item item = object.getProperty().getItem();
            if (item != null && item instanceof TDQJrxmlItem) {
                this.jrxmlItem = (TDQJrxmlItem) item;
            }
        }
    }

    @Override
    public String getLabel() {
        if (this.getJrxmlItem() != null) {
            return this.getJrxmlItem().getName();
        }
        return super.getLabel();
    }

}
