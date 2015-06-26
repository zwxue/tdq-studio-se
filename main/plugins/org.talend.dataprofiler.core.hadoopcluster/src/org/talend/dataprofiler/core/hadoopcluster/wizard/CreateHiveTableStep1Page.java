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

import org.eclipse.swt.widgets.Composite;
import org.talend.commons.i18n.internal.DefaultMessagesImpl;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.repository.hdfs.ui.HDFSFileSelectorForm;
import org.talend.repository.hdfs.ui.HDFSFileSelectorWizardPage;
import org.talend.repository.model.hdfs.HDFSConnection;

/**
 * created by yyin on 2015年5月18日 Detailled comment
 *
 */
public class CreateHiveTableStep1Page extends HDFSFileSelectorWizardPage {

    private ConnectionItem tconnectionItem;

    private HDFSConnection tempConnection;

    private HDFSFileSelectorDQForm dqForm;

    /**
     * DOC yyin CreateHiveTableStep1Page constructor comment.
     * 
     * @param connectionItem
     * @param isRepositoryObjectEditable
     * @param temConnection
     */
    public CreateHiveTableStep1Page(ConnectionItem connectionItem, boolean isRepositoryObjectEditable,
            HDFSConnection temConnection) {
        super(connectionItem, isRepositoryObjectEditable, temConnection);
        tconnectionItem = connectionItem;
        tempConnection = temConnection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.hdfs.ui.HDFSFileSelectorWizardPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        setPageComplete(false);
    }

    @Override
    protected HDFSFileSelectorForm createForm(final Composite parent) {
        dqForm = new HDFSFileSelectorDQForm(parent, tconnectionItem, tempConnection, this);
        return dqForm;
    }

    /*
     * If the form status is NOT ok, always use false to setPageComplete
     */
    @Override
    public void setPageComplete(boolean complete) {
        if (dqForm == null || dqForm.isStatusOk()) {
            super.setPageComplete(complete);
            setErrorMessage(null);
        } else {
            super.setPageComplete(false);
            this.setErrorMessage(DefaultMessagesImpl.getString("CreateHiveTableStep1Page.error")); //$NON-NLS-1$
        }

    }

}
