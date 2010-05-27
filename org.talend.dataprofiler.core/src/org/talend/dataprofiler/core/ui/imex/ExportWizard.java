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

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.ui.imex.model.EImexType;
import org.talend.dataprofiler.core.ui.imex.model.ExportWriterFactory;
import org.talend.dataprofiler.core.ui.imex.model.IImexWriter;
import org.talend.dataprofiler.core.ui.imex.model.ItemRecord;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ExportWizard extends Wizard {

    private static Logger log = Logger.getLogger(ExportWizard.class);

    private ExportWizardPage exportPage;

    private IImexWriter writer;

    /**
     * DOC bZhou ExportWizard constructor comment.
     */
    public ExportWizard(EImexType type) {
        this(type, null);
    }

    /**
     * DOC bZhou ExportWizard constructor comment.
     */
    public ExportWizard(EImexType type, String specifiedPath) {
        setWindowTitle("Export Item");

        this.writer = ExportWriterFactory.create(type);
        this.exportPage = new ExportWizardPage(writer, specifiedPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(exportPage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        final ItemRecord[] records = exportPage.getElements();

        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask("Export Item", records.length);

                try {
                    for (ItemRecord record : records) {

                        if (monitor.isCanceled()) {
                            break;
                        }

                        monitor.subTask("Exporting " + record.getElement().getName());

                        if (record.isValid()) {
                            log.info("Start exporting " + record.getFile().getAbsolutePath());
                            writer.write(record);
                        } else {
                            for (String error : record.getErrors()) {
                                log.error(error);
                            }
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
