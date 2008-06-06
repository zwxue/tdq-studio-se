// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;
import org.talend.dq.analysis.parameters.ConnectionParameter;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class CreateSqlFileWizard extends Wizard {

    private CreateSqlFileWizardPage mPage;

    private IFolder folder;

    private File sqlFile;

    /**
     * DOC qzhang CreateSqlFileWizard constructor comment.
     * 
     * @param folder
     */
    public CreateSqlFileWizard(IFolder folder) {
        this.folder = folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        mPage = new CreateSqlFileWizardPage();
        AbstractWizardPage.setConnectionParams(new ConnectionParameter());
        mPage.setTitle("New SQL File in repository");
        mPage.setDescription("Define the properties");
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
        IPath absolutePath = new Path(AbstractWizardPage.getConnectionParams().getFolderProvider().getFolder().getAbsolutePath());
        String portableString = absolutePath.append(AbstractWizardPage.getConnectionParams().getName()).addFileExtension("sql")
                .toPortableString();
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

}
