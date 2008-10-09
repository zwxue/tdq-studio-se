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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.wizard.Wizard;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dataquality.domain.pattern.Pattern;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ExportPatternsWizard extends Wizard {

    private IFolder folder;

    private ExportPatternsWizardPage page;

    /**
     * DOC zqin ExportPatternsWizard constructor comment.
     */
    public ExportPatternsWizard(IFolder folder) {
        this.folder = folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        String targetFile = page.getTargetFile();
        Object[] elements = page.getSelectedPatternsTree().getCheckedElements();

        List<Pattern> seletedPatterns = new ArrayList<Pattern>();
        for (Object element : elements) {
            if (element instanceof IFile) {
                IFile file = (IFile) element;
                if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.PATTERN)) {
                    seletedPatterns.add(PatternResourceFileHelper.getInstance().findPattern(file));
                }
            }
        }

        File file = new File(targetFile);

        boolean isContinue = true;

        if (file.exists()) {
            isContinue = MessageDialogWithToggle.openConfirm(null, "Warning",
                    "Warning: this file already exist, do you want to overwrite it?");
        }

        if (isContinue) {

            ExportFactory.exportToFile(seletedPatterns.toArray(new Pattern[seletedPatterns.size()]), file);
            return true;
        }

        return false;
    }

    @Override
    public void addPages() {
        page = new ExportPatternsWizardPage(folder);
        addPage(page);
    }

}
