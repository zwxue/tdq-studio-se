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
package org.talend.dataprofiler.core.model.nodes.foldernode;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.talend.dataprofiler.core.ImageLib;

/**
 * DOC rli class global comment. Detailled comment
 */
public class RepGeneratedFolder extends AbstractFolderNode implements IWorkbenchAdapter {

    private static Logger log = Logger.getLogger(RepGeneratedFolder.class);

    private static final String GENERATE_REPORTS = "Generate Reports";

    private final IFile reportFile;

    /**
     * DOC rli RepGeneratedFolder constructor comment.
     * 
     * @param name
     */
    public RepGeneratedFolder(IFile reportFile) {
        super(GENERATE_REPORTS);
        this.reportFile = reportFile;
        this.loadChildren();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.model.nodes.foldernode.AbstractFolderNode#loadChildren()
     */
    @Override
    public void loadChildren() {
        IFolder reportContainer = (IFolder) reportFile.getParent();
        String fileName = reportFile.getName();
        String simpleName = null;
        int indexOf = fileName.indexOf(".");
        if (indexOf != -1) {
            simpleName = fileName.substring(0, indexOf);
        } else {
            log.error("The current report file name:" + reportFile.getFullPath() + " is a illegal name.");
            return;
        }
        IFolder currentRportFolder = reportContainer.getFolder(simpleName);
        if (!currentRportFolder.exists()) {
            return;
        }

        try {
            IResource[] members = currentRportFolder.members();
            this.setChildren(members);
        } catch (CoreException e) {
            e.printStackTrace();
        }

    }

    public IFile getReportFile() {
        return reportFile;
    }

    @Override
    public int getChildrenType() {
        return FILE_TYPE;
    }

    public Object[] getChildren(Object o) {
        return this.getChildren();
    }

    public ImageDescriptor getImageDescriptor(Object object) {
        return ImageLib.getImageDescriptor(ImageLib.FOLDERNODE_IMAGE);
    }

    public String getLabel(Object o) {
        return GENERATE_REPORTS;
    }

    public Object getParent(Object o) {
        return null;
    }

}
