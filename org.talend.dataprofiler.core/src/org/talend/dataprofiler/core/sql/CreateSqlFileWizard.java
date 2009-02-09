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
package org.talend.dataprofiler.core.sql;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dq.analysis.parameters.ConnectionParameter;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class CreateSqlFileWizard extends AbstractWizard {

    private CreateSqlFileWizardPage mPage;

    private File sqlFile;

    private ConnectionParameter parameter;

    /**
     * DOC qzhang CreateSqlFileWizard constructor comment.
     * 
     * @param folder
     */
    public CreateSqlFileWizard(ConnectionParameter parameter) {
        this.parameter = parameter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        mPage = new CreateSqlFileWizardPage();
        mPage.setTitle(DefaultMessagesImpl.getString("CreateSqlFileWizard.newSQLFile")); //$NON-NLS-1$
        mPage.setDescription(DefaultMessagesImpl.getString("CreateSqlFileWizard.define")); //$NON-NLS-1$
        mPage.setPageComplete(false);
        addPage(mPage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        IPath absolutePath = new Path(parameter.getFolderProvider().getFolder().getAbsolutePath());
        String portableString = absolutePath.append(parameter.getName()).addFileExtension("sql").toPortableString(); //$NON-NLS-1$
        sqlFile = new File(portableString);
        try {
            sqlFile.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Getter for sqlFile.
     * 
     * @return the sqlFile
     */
    public File getSqlFile() {
        return this.sqlFile;
    }

    @Override
    protected ConnectionParameter getConnectionParameter() {

        return parameter;
    }

}
