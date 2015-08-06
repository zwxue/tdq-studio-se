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
package org.talend.dataprofiler.core.sql;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.properties.TDQJrxmlItem;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class RenameJrxmlTemplateFileAction extends Action {

    protected static Logger log = Logger.getLogger(RenameSqlFileAction.class);

    private String newName;

    private String newFolderPath;

    private ArrayList<String> existNames;

    private IPath filePath;

    private IRepositoryNode jrxmlNode;

    private IRepositoryNode parentNode;

    private TDQJrxmlItem jrxmlItem;

    public RenameJrxmlTemplateFileAction(RepositoryNode node) {
        setText(DefaultMessagesImpl.getString("RenameJrxmlFileAction.renameJrxmlFile")); //$NON-NLS-1$
        this.filePath = WorkbenchUtils.getFilePath(node);
        this.parentNode = node.getParent();
        this.jrxmlItem = (TDQJrxmlItem) node.getObject().getProperty().getItem();
        this.jrxmlNode = node;
    }

    @Override
    public void run() {
        // TODO implement this method in V5.2.0
        MessageUI.openWarning(DefaultMessagesImpl.getString("JrxmlFileAction.forbiddenOperation")); //$NON-NLS-1$
    }
}
