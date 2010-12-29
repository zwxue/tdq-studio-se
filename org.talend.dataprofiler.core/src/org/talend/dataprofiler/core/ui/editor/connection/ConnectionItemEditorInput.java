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
package org.talend.dataprofiler.core.ui.editor.connection;

import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;

/**
 * 
 * DOC mzhao Connection item editor input
 */
public class ConnectionItemEditorInput extends AbstractItemEditorInput {

    private ConnectionItem connectionItem = null;

    public ConnectionItemEditorInput(Item connItem) {
        super(connItem);
        connectionItem = (ConnectionItem) connItem;
    }

    @Override
    public String getName() {
        return getPath() + connectionItem.getConnection().getName();
    }

    @Override
    public String getToolTipText() {
        return getPath() + connectionItem.getConnection().getName();
    }

    /**
     * 
     * DOC mzhao Get connection item.
     * 
     * @return
     */
    public ConnectionItem getConnectionItem() {
        return connectionItem;
    }


}
