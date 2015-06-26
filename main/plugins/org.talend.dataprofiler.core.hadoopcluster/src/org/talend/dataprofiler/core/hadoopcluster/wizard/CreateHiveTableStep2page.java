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
package org.talend.dataprofiler.core.hadoopcluster.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.repository.hdfs.ui.HDFSSchemaForm;
import org.talend.repository.model.hdfs.HDFSConnection;

/**
 * created by yyin on 2015年4月28日 Detailled comment
 *
 */
public class CreateHiveTableStep2page extends WizardPage {

    private ConnectionItem connectionItem;

    private HDFSConnection temConnection;

    private HDFSSchemaForm schemaForm;

    public CreateHiveTableStep2page(ConnectionItem connectionItem, HDFSConnection temConnection) {
        super("CreateHiveTableStep2page"); //$NON-NLS-1$
        this.connectionItem = connectionItem;
        this.temConnection = temConnection;
    }

    public void createControl(final Composite parent) {
        schemaForm = new HDFSSchemaDQForm(parent, connectionItem, null, this, temConnection);
        schemaForm.setReadOnly(false);
        setControl(schemaForm);
        setPageComplete(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    @Override
    public boolean canFlipToNextPage() {
        return ((HDFSSchemaDQForm) schemaForm).canFlipToNext();
    }

}