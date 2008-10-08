// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.pattern;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.talend.dataprofiler.core.ImageLib;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ExportPatternsAction extends Action {

    private IFolder folder;

    /**
     * DOC zqin ExportPatternsAction constructor comment.
     */
    public ExportPatternsAction(IFolder folder) {
        setText("Export Patterns");
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.PATTERN_REG));
        this.folder = folder;
    }

    @Override
    public void run() {

        ExportPatternsWizard wizard = new ExportPatternsWizard(folder);
        WizardDialog dialog = new WizardDialog(null, wizard);
        wizard.setWindowTitle(getText());
        if (WizardDialog.OK == dialog.open()) {
            try {
                folder.refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

}
