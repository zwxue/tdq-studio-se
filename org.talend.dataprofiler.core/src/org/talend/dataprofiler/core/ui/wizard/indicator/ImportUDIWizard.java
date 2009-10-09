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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.pattern.ImportFactory;
import org.talend.dataprofiler.core.ui.dialog.message.ImportInfoDialog;

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
        final List<String> information = ImportFactory.importIndicatorToStucture(file, folder, page.getSkip(), page.getRename());
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                Pattern p = Pattern.compile("File format of indicator \\\".*\\\" is invalid!");

                for (String info : information) {
                    Matcher m = p.matcher(info);
                    if (m.matches())
                        ImportInfoDialog
                                .openImportInformation(
                                        null,
                                        DefaultMessagesImpl.getString("ImportRemotePatternAction.ImportFinishWithWarning"), (String[]) information.toArray(new String[0]), MessageDialog.WARNING); //$NON-NLS-1$
                    break;
                }
            }
        });
        return true;
    }

}
