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
package org.talend.dataprofiler.core.delete;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.ui.action.actions.handle.ActionHandleFactory;
import org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle;
import org.talend.dq.helper.PropertyHelper;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DeleteLogicalTest {

    @Test
    public void testLogilcalDelElements() throws Exception {
        // find the delete elements parent(folder).I only delete elements under Ananlysis folder now.
        IFolder folder = ResourceManager.getAnalysisFolder();
        IResource res[] = folder.members();
        IDeletionHandle handle = null;
        for (IResource re : res) {
            if (re instanceof IFile) {
                IFile file = (IFile) re;
                if (file.exists() && FactoriesUtil.isEmfFile(file.getFileExtension())) {
                    Property property = PropertyHelper.getProperty(file);
                    handle = ActionHandleFactory.createDeletionHandle(property);
                    boolean runStatus = handle.delete();
                    if (runStatus) {
                        System.out.println("Logical delete " + file.getName() + " successful!");
                    }
                }
            } else if (re instanceof IFolder) {
                IFolder subfolder = (IFolder) re;
                List<IFile> filsLs = new ArrayList<IFile>();
                getAllSubFiles(subfolder, filsLs);
                boolean hasDelFailing = false;
                for (IFile file : filsLs) {
                    if (file.exists() && FactoriesUtil.isEmfFile(file.getFileExtension())) {
                        Property property = PropertyHelper.getProperty(file);
                        handle = ActionHandleFactory.createDeletionHandle(property);
                        List<ModelElement> dependencies = handle.getDependencies();
                        if (dependencies != null && !dependencies.isEmpty()) {
                            hasDelFailing = true;
                            break;
                        }
                        if (handle.delete()) {
                            System.out.println("Logical delete " + file.getName() + " successful!");
                        } else {
                            hasDelFailing = true;
                        }
                    }
                }
                if (!hasDelFailing) {
                    // LogicalDeleteFileHandle.replaceInFile(LogicalDeleteFileHandle.folderType
                    // + subfolder.getFullPath().toOSString(), PluginConstant.EMPTY_STRING);
                    // LogicalDeleteFileHandle.saveElement(LogicalDeleteFileHandle.folderType,
                    // subfolder.getFullPath().toOSString());
                    System.out.println("Logical delete " + subfolder.getName() + " successful!");
                }

            }
        }
    }

    private void getAllSubFiles(IFolder folder, List<IFile> fileList) {
        IResource[] members = null;
        try {
            members = folder.members();
        } catch (CoreException e) {
            e.printStackTrace();
        }
        for (IResource res : members) {
            if (res.getType() == IResource.FILE) {
                IFile file = (IFile) res;
                if (!StringUtils.equals(file.getFileExtension(), FactoriesUtil.PROPERTIES_EXTENSION)) {
                    fileList.add(file);
                }
            } else if (res.getType() == IResource.FOLDER) {
                getAllSubFiles((IFolder) res, fileList);
            }
        }

    }
}
