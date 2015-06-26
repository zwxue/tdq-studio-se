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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.repository.hdfs.ui.HDFSSchemaForm;
import org.talend.repository.model.hdfs.HDFSConnection;

/**
 * created by yyin on 2015年5月19日 Detailled comment
 *
 */
public class HDFSSchemaDQForm extends HDFSSchemaForm {

    private final WizardPage parentPage;

    private boolean canFlipToNext = false;

    /**
     * DOC yyin HDFSSchemaDQForm constructor comment.
     * 
     * @param parent
     * @param connectionItem
     * @param metadataTable
     * @param page
     * @param temConnection
     */
    public HDFSSchemaDQForm(Composite parent, ConnectionItem connectionItem, MetadataTable metadataTable, IWizardPage page,
            HDFSConnection temConnection) {
        super(parent, connectionItem, metadataTable, page, temConnection);
        parentPage = (WizardPage) page;
    }

    @Override
    protected void changeTableNavigatorStatus(boolean isEnabled) {
        super.changeTableNavigatorStatus(isEnabled);
        // judge if the current table has columns
        MetadataTable metadataTable2 = getMetadataTable();
        if (metadataTable2 == null || metadataTable2.getColumns() == null || metadataTable2.getColumns().size() < 1) {
            canFlipToNext = false;
        } else {
            canFlipToNext = true;
        }
        parentPage.getWizard().getContainer().updateButtons();
    }

    public boolean canFlipToNext() {
        return canFlipToNext;
    }
}
