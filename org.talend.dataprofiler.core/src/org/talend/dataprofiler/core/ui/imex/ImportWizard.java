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
package org.talend.dataprofiler.core.ui.imex;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.ui.imex.model.EImexType;
import org.talend.dataprofiler.core.ui.imex.model.IImexWriter;
import org.talend.dataprofiler.core.ui.imex.model.ImportWriterFactory;
import org.talend.dataprofiler.core.ui.imex.model.ItemRecord;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ImportWizard extends Wizard {

    private static Logger log = Logger.getLogger(ImportWizard.class);

    private ImportWizardPage importPage;

    private IImexWriter writer;

    public ImportWizard(EImexType type) {
        setWindowTitle("Import Item");

        this.writer = ImportWriterFactory.create(type);
        this.importPage = new ImportWizardPage(writer);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        File[] files = importPage.getElements();

        final ItemRecord[] records = new ItemRecord[files.length];
        for (int i = 0; i < files.length; i++) {
            records[i] = new ItemRecord(files[i]);
        }

        ItemRecord[] invalidRecords = writer.populate(records);
        importPage.updateErrorList(invalidRecords);

        if (invalidRecords.length > 0) {
            importPage.removeInvalidRecords();
            return false;
        }

        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Import Item", records.length);

                try {
                    for (ItemRecord record : records) {

                        if (monitor.isCanceled()) {
                            break;
                        }

                        monitor.subTask("Importing " + record.getElement().getName());

                        if (record.isValid()) {
                            log.info("Start importing " + record.getFile().getAbsolutePath());
                            writer.initPath(record, null);
                            writer.write();
                        }

                        monitor.worked(1);
                    }

                    writer.finish(records);

                } catch (Exception e) {
                    log.error(e, e);
                }

                monitor.done();

            }
        };

        try {
            ProgressUI.popProgressDialog(op);
        } catch (Exception e) {
            log.error(e, e);
        }

        return true;
    }
}
