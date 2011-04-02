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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.commons.exception.PersistenceException;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataprofiler.core.ui.views.resources.RepositoryNodeDorpAdapterAssistant;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.SourceFileRepNode;
import org.talend.repository.model.RepositoryNode;

/**
 * rename tdq folder action.
 */
public class RenameTdqFolderAction extends Action {

    protected static Logger log = Logger.getLogger(RenameTdqFolderAction.class);

    private IFolder obj;

    private RepositoryNode node;

    /**
     * @param node a folder
     */
    public RenameTdqFolderAction(RepositoryNode node) {
        this.node = node;
        this.obj = WorkbenchUtils.getFolder(node);
        setText(DefaultMessagesImpl.getString("RenameTdqFolderAction.renameFolder")); //$NON-NLS-1$
    }

    @Override
    public void run() {
        InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(),
                DefaultMessagesImpl.getString("RenameTdqFolderAction.renameFolderName"), //$NON-NLS-1$
                DefaultMessagesImpl.getString("RenameTdqFolderAction.inputNewFolderName"), null, new IInputValidator() { //$NON-NLS-1$

                    public String isValid(String newText) {
                        return null;
                    }

                });
        if (dialog.open() == InputDialog.OK) {
            String value2 = dialog.getValue();
            try {
                // close opend editors
                List<SourceFileRepNode> sourceFileRepNodes = RepositoryNodeHelper.getSourceFileRepNodes(node, true);
                RepNodeUtils.closeModelElementEditor(sourceFileRepNodes, true);

                RepositoryNodeDorpAdapterAssistant dndAsistant = new RepositoryNodeDorpAdapterAssistant();
                dndAsistant.renameFolderRepNode(node, value2);

                // refresh the dq repository view
                if (node != null && node.getParent() != null) {
                    CorePlugin.getDefault().refreshDQView(node.getParent());
                }
            } catch (PersistenceException e) {
                log.error(e, e);
            }
        }
    }

}
