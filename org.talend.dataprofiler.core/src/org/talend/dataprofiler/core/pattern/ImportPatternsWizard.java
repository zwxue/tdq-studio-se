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
package org.talend.dataprofiler.core.pattern;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.talend.dataprofiler.core.ui.dialog.message.ImportInfoDialog;
import org.talend.dataquality.domain.pattern.ExpressionType;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class ImportPatternsWizard extends Wizard {

    private IFolder folder;

    private ExpressionType type;

    private ImportPatternsWizardPage page;

    /**
     * DOC qzhang CreateSqlFileWizard constructor comment.
     * 
     * @param folder
     * @param type
     */
    public ImportPatternsWizard(IFolder folder, ExpressionType type) {
        this.folder = folder;
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        page = new ImportPatternsWizardPage();
        addPage(page);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        File file = new File(page.getSourceFile());

        final List<String> information = ImportFactory.importToStucture(file, folder, type, page.getSkip(), page.getRename());

        Display.getDefault().asyncExec(new Runnable() {

            public void run() {

                ImportInfoDialog.openImportInformation(null, "Import finish.", (String[]) information.toArray(new String[0]));
            }
        });

        return true;
    }
}
