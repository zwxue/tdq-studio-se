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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.talend.core.repository.model.repositoryObject.TdTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdViewRepositoryObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.database.ColumnFilterWizard;
import org.talend.repository.model.IRepositoryNode;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ColumnFilterAction extends Action {

    private static final int WIDTH = 300;

    private static final int HEIGHT = 150;

    private NamedColumnSet namedColumnSet;

    private IRepositoryNode node;
    public ColumnFilterAction() {
        super(DefaultMessagesImpl.getString("ColumnFilterAction.columnFilter")); //$NON-NLS-1$
    }

    public ColumnFilterAction(NamedColumnSet namedColumnSet) {
        this();
        this.namedColumnSet = namedColumnSet;
    }

    /**
     * DOC klliu ColumnFilterAction constructor comment.
     * 
     * @param node
     */
    public ColumnFilterAction(IRepositoryNode node) {
        this();
        this.node = node;
        if (node.getObject() instanceof TdTableRepositoryObject) {
            TdTableRepositoryObject tableObject = (TdTableRepositoryObject) node.getObject();
            this.namedColumnSet = tableObject.getTdTable();
        } else if (node.getObject() instanceof TdViewRepositoryObject) {
            TdViewRepositoryObject viewObject = (TdViewRepositoryObject) node.getObject();
            this.namedColumnSet = viewObject.getTdView();
        }
    }

    @Override
    public void run() {
        ColumnFilterWizard wizard = new ColumnFilterWizard(this.namedColumnSet);
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(WIDTH, HEIGHT);
        dialog.open();
        CorePlugin.getDefault().refreshDQView(node);
    }

}
