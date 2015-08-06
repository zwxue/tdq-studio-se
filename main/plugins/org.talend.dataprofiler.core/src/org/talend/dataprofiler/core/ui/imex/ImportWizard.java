// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.imex;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.imex.model.IImportWriter;
import org.talend.dataprofiler.core.ui.imex.model.ItemRecord;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ImportWizard extends Wizard {

    private static Logger log = Logger.getLogger(ImportWizard.class);

    protected ImportWizardPage importPage;

    public ImportWizard() {
        setWindowTitle("Import Item");//$NON-NLS-1$ 
        this.importPage = new ImportWizardPage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(importPage);
    }

    @Override
    public boolean performCancel() {
        final ItemRecord[] records = importPage.getElements();
        final IImportWriter writer = importPage.getWriter();

        try {
            writer.finish(records, null);
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }

        return super.performCancel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        // ADD xqliu TDQ-4284 2011-12-26
        if (ProxyRepositoryFactory.getInstance().isUserReadOnlyOnCurrentProject()) {
            return true;
        }
        // ~ TDQ-4284

        final ItemRecord[] records = importPage.getElements();

        final IImportWriter writer = importPage.getWriter();

        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Import Item", records.length);//$NON-NLS-1$ 

                writer.write(records, monitor);

                monitor.done();
            }
        };

        try {
            ProgressUI.popProgressDialog(op);
        } catch (Exception e) {
            log.error(e, e);
        }

        CorePlugin.getDefault().refreshWorkSpace();
        CorePlugin.getDefault().refreshDQView();

        return true;
    }
}
