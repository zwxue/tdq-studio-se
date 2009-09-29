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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.analysis.parameters.SqlFileParameter;
import org.talend.top.repository.ProxyRepositoryManager;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class CreateSqlFileWizard extends Wizard {

    protected static Logger log = Logger.getLogger(CreateSqlFileWizard.class);

    private CreateSqlFileWizardPage mPage;

    private File sqlFile;

    private SqlFileParameter parameter;

    /**
     * DOC qzhang CreateSqlFileWizard constructor comment.
     * 
     * @param folder
     */
    public CreateSqlFileWizard(SqlFileParameter parameter) {
        this.parameter = parameter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        mPage = new CreateSqlFileWizardPage(parameter);
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
        String portableString = absolutePath.append(parameter.getFileName()).addFileExtension("sql").toPortableString(); //$NON-NLS-1$
        sqlFile = new File(portableString);
        try {
            sqlFile.createNewFile();
            ProxyRepositoryManager.getInstance().save();
            return true;
        } catch (IOException e) {
            log.error(e, e);
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

}
