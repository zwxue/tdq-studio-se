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
package org.talend.dataprofiler.core.ui.action.actions;

import org.eclipse.jface.action.Action;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionItemEditorInput;

/**
 * 
 * DOC mzhao Open TDQ items editor action.
 */
public class OpenItemEditorAction extends Action {

    private IRepositoryViewObject reposViewObj = null;

    public OpenItemEditorAction(IRepositoryViewObject reposViewObj) {
        super(DefaultMessagesImpl.getString("OpenIndicatorDefinitionAction.Open")); //$NON-NLS-1$
        this.reposViewObj = reposViewObj;
    }

    @Override
    public void run() {
        // Connection editor
        String key = reposViewObj.getRepositoryObjectType().getKey();
        if (ERepositoryObjectType.METADATA_CONNECTIONS.getKey().equals(key)
                || ERepositoryObjectType.METADATA_MDMCONNECTION.getKey().equals(key)) {
            ConnectionItemEditorInput connItemEditorInput = new ConnectionItemEditorInput(reposViewObj);
            CorePlugin.getDefault().openEditor(connItemEditorInput, ConnectionEditor.class.getName());
        }
    }
}
