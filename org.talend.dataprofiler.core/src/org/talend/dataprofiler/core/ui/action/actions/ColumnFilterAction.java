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
import org.eclipse.jface.wizard.WizardDialog;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.database.ColumnFilterWizard;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ColumnFilterAction extends Action {

    private static final int WIDTH = 300;

    private static final int HEIGHT = 150;

    private NamedColumnSet namedColumnSet;

    public ColumnFilterAction() {
        super(DefaultMessagesImpl.getString("ColumnFilterAction.columnFilter")); //$NON-NLS-1$
    }

    public ColumnFilterAction(NamedColumnSet namedColumnSet) {
        this();
        this.namedColumnSet = namedColumnSet;
    }

    @Override
    public void run() {
        ColumnFilterWizard wizard = new ColumnFilterWizard(this.namedColumnSet);
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(WIDTH, HEIGHT);
        dialog.open();
        CorePlugin.getDefault().refreshDQView();
    }

}
