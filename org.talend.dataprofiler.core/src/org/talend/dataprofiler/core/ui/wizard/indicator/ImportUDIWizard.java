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
package org.talend.dataprofiler.core.ui.wizard.indicator;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.pattern.ImportFactory;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ImportUDIWizard extends Wizard {

    private IFolder folder;

    private ImportUDIWizardPage page;

    public ImportUDIWizard(IFolder folder) {
        this.folder = folder;
    }

    @Override
    public void addPages() {
        page = new ImportUDIWizardPage();
        addPage(page);
    }

    @Override
    public boolean performFinish() {
        File file = new File(page.getSourceFile());
        ImportFactory.importIndicatorToStucture(file, folder, page.getSkip(), page.getRename());
        return true;
    }

}
