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
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.StringUtils;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.resource.ResourceManager;

/**
 * DOC yyi class global comment. Detailled comment
 */
public class UpdateAnalysisWithMinLengthIndicator extends AbstractWorksapceUpdateTask {

    /**
     * DOC yyi UpdateAnalysisWithMinLengthIndicator constructor comment.
     */
    public UpdateAnalysisWithMinLengthIndicator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {

        Collection<IFile> analyseFiles = searchAllAnalysis(ResourceManager.getAnalysisFolder());

        for (IFile file : analyseFiles) {
            if (file.exists()) {
                File af = new File(file.getLocationURI());
                try {
                    String content = FileUtils.readFileToString(af, EMFUtil.ENCODING);
                    content = StringUtils.replace(content, "MinLengthIndicator xmi:id=\"", "minLengthIndicator xmi:id=\"");
                    content = StringUtils.replace(content, "</MinLengthIndicator>", "</minLengthIndicator>");
                    FileUtils.writeStringToFile(af, content, EMFUtil.ENCODING);
                } catch (IOException e) {
                    return false;
                }
            }

        }
        return true;
    }

    private Collection<IFile> searchAllAnalysis(IFolder folder) {
        Collection<IFile> analyses = new ArrayList<IFile>();
        try {
            for (IResource resource : folder.members()) {
                if (resource.getType() == IResource.FOLDER) {
                    searchAllAnalysis(folder.getFolder(resource.getName()));
                    continue;
                }
                IFile file = (IFile) resource;
                if (file.getFileExtension().equals(FactoriesUtil.ANA)) {
                    analyses.add(file);
                }
            }
        } catch (CoreException e) {
        }
        return analyses;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2010, 07, 06);
    }

}
