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
package org.talend.dataprofiler.core.ui.wizard.indicator;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.wizard.Wizard;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
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

    private boolean isForExchange;

    public ExportUDIWizard(IFolder folder, boolean isForExchange) {
        this.folder = folder;
        this.isForExchange = isForExchange;
    }

    @Override
    public boolean performFinish() {

        String targetFile = page.getTargetFile();
        Object[] elements = page.getSelectedTree().getCheckedElements();

        List<IndicatorDefinition> seletedIndicators = new ArrayList<IndicatorDefinition>();
        for (Object element : elements) {
            if (element instanceof IFile) {
                IFile file = (IFile) element;
                if (FactoriesUtil.UDI.equalsIgnoreCase(file.getFileExtension())) {
                    seletedIndicators.add(UDIResourceFileHelper.getInstance().findUDI(file));
                }
            }
        }

        if ("".equals(targetFile)) { //$NON-NLS-1$
            MessageDialog.openError(getShell(), DefaultMessagesImpl.getString("ExportUDIWizard.Error"), DefaultMessagesImpl.getString("ExportUDIWizard.SpecifyValidResource")); //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        } else {
            File resource = new File(targetFile);

            if (isForExchange) {
                ExportFactory.export(resource, folder, seletedIndicators
                        .toArray(new IndicatorDefinition[seletedIndicators.size()]));

                for (Iterator iterator = seletedIndicators.iterator(); iterator.hasNext();) {
                    IndicatorDefinition id = (IndicatorDefinition) iterator.next();
                    File idFile = new File(resource, id.getName() + ".csv"); //$NON-NLS-1$
                    if (idFile.isFile() && idFile.exists()) {
                        try {
                            FilesUtils.zip(idFile, idFile.getPath() + ".zip"); //$NON-NLS-1$
                            idFile.delete();

                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }

                return true;
            } else {

                boolean isContinue = true;
                if (resource.exists()) {
                    isContinue = MessageDialogWithToggle.openConfirm(null, DefaultMessagesImpl
                            .getString("ExportPatternsWizard.waring"), //$NON-NLS-1$
                            DefaultMessagesImpl.getString("ExportPatternsWizard.fileAlreadyExist")); //$NON-NLS-1$
                }

                if (isContinue) {
                    ExportFactory.export(resource, folder, seletedIndicators.toArray(new IndicatorDefinition[seletedIndicators
                            .size()]));
                    return true;
                }

                return false;
            }
        }
    }

    @Override
    public void addPages() {
        page = new ExportUDIWizardPage(folder, isForExchange);
        addPage(page);
    }

}
