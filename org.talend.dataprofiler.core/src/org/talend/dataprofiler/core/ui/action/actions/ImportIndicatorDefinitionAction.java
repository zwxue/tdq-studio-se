// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.wizard.indicator.ImportIndicatorDefinitionWizard;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ImportIndicatorDefinitionAction extends Action {

    public ImportIndicatorDefinitionAction() {
        super("Import Indicators");
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.IMPORT));
    }

    @Override
    public void run() {
        ImportIndicatorDefinitionWizard wizard = new ImportIndicatorDefinitionWizard();
        WizardDialog dialog = new WizardDialog(null, wizard);
        wizard.setWindowTitle(getText());
        dialog.open();
    }
}
