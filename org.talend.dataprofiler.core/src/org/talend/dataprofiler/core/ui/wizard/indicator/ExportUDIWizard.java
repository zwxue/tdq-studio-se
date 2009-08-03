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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.pattern.ExportFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.utils.io.FilesUtils;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ExportUDIWizard extends Wizard {

    private static Logger log = Logger.getLogger(ExportUDIWizard.class);

    private IFolder folder;

    private ExportUDIWizardPage page;

    public ExportUDIWizard(IFolder folder) {
        this.folder = folder;
    }

    @Override
    public boolean performFinish() {
        String targetFile = page.getTargetFile();
        Object[] elements = page.getSelectedPatternsTree().getCheckedElements();

        List<IndicatorDefinition> seletedIndicators = new ArrayList<IndicatorDefinition>();
        for (Object element : elements) {
            if (element instanceof IFile) {
                IFile file = (IFile) element;
                if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.UDI)) {
                    seletedIndicators.add(UDIResourceFileHelper.getInstance().findUDI(file));
                }
            }
        }

        if ("".equals(targetFile)) {
            MessageDialog.openError(getShell(), "Error", "Please specify a valid resource!");
            return false;
        } else {
            File resource = new File(targetFile);
            ExportFactory.export(resource, seletedIndicators.toArray(new IndicatorDefinition[seletedIndicators.size()]));
            for (Iterator iterator = seletedIndicators.iterator(); iterator.hasNext();) {
                IndicatorDefinition indicator = (IndicatorDefinition) iterator.next();
                File indicatorFile = new File(resource, indicator.getName() + "." + FactoriesUtil.UDI);
                if (indicatorFile.isFile() && indicatorFile.exists()) {
                    try {
                        FilesUtils.zip(indicatorFile, indicatorFile.getPath() + ".zip");
                        indicatorFile.delete();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            return true;
        }
    }

    @Override
    public void addPages() {
        page = new ExportUDIWizardPage(folder);
        addPage(page);
    }

}
