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

import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.ui.imex.model.EImexType;
import org.talend.dataprofiler.core.ui.imex.model.IImexWriter;
import org.talend.dataprofiler.core.ui.imex.model.ImportWriterFactory;
import org.talend.dataprofiler.core.ui.imex.model.ItemRecord;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ImportWizard extends Wizard {

    private ImportWizardPage importPage;

    private EImexType type;

    public ImportWizard() {
        this.importPage = new ImportWizardPage();
        setWindowTitle("Import Item");
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

        try {
            for (File file : files) {

                IImexWriter writer = ImportWriterFactory.create(type);
                writer.initPath(new ItemRecord(file), null);
                writer.write();
                writer.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Sets the type.
     * 
     * @param type the type to set
     */
    public void setType(EImexType type) {
        this.type = type;
    }

    /**
     * Getter for type.
     * 
     * @return the type
     */
    public EImexType getType() {
        return this.type;
    }
}
