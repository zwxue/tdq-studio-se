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
package org.talend.dataprofiler.core.ui.views.provider;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

/**
 * DOC zshen class global comment. Test the method
 * org.talend.dataprofiler.core.ui.views.provider.ResourceViewLabelProvider#getFileCount
 */
public class GetFileCountTest {

    private static String[] filterExtensions = { "ana", "rep" };

    final private String anaFolderName = "Analyses";

    final private String repFolderName = "Reports";

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.views.provider.ResourceViewLabelProvider#getFileCount(org.eclipse.core.resources.IFolder, java.lang.String[])}
     * 
     */
    @Test
    public void testGetFileCount() {
        IFolder anaFolder = initFolder(anaFolderName);
        IFolder repFolder = initFolder(repFolderName);
        ResourceViewLabelProvider reViewLabelProvider = new ResourceViewLabelProvider();
        int AnalysisNum = reViewLabelProvider.getFileCount(anaFolder, filterExtensions);
        // System.out.println(AnalysisNum);
        assertEquals(AnalysisNum, 3);
        int reportNum = reViewLabelProvider.getFileCount(repFolder, filterExtensions);
        // System.out.println(reportNum);
        assertEquals(AnalysisNum, 3);
        assertEquals(reportNum, 3);
    }

    /**
     * 
     * DOC zshen Comment method "initFolder". init the folder which contain ana and rep files
     * 
     * @param folderName
     * @return
     */
    public IFolder initFolder(String folderName) {
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        IProject project = ws.getRoot().getProject("projects");
        if (!project.exists()) {
            try {
                project.create(null);
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // System.out.println(project);
        IFolder aa = project.getFolder(folderName);
        if (aa.exists()) {
        } else {
            try {
                project.open(null);
                aa.create(true, true, null);
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // for analyses
        if (anaFolderName.equals(folderName)) {
            IFolder subfolder1 = createFolder(aa, "subfolder1");
            IFolder subfolder2 = createFolder(aa, "subfolder2");
            createFile(aa, "a1.ana");
            createFile(subfolder1, "a2.ana");
            createFile(subfolder2, "a3.ana");
        }
        // for reports
        if (repFolderName.equals(folderName)) {
            IFolder subfolder1 = createFolder(aa, "subfolder1");
            IFolder subfolder2 = createFolder(aa, "subfolder2");
            createFile(aa, "a1.rep");
            createFile(subfolder1, "a2.rep");
            createFile(subfolder2, "a3.rep");
        }
        // System.out.println(aa.getRawLocationURI());
        return aa;
    }

    /**
     * 
     * DOC zshen Comment method "createFolder". create the subfolder under the parentFolder and named for folderName
     * 
     * @param parentFolder
     * @param folderName
     * @return
     */
    public IFolder createFolder(IFolder parentFolder, String folderName) {
        IFolder currFolder = parentFolder.getFolder(folderName);
        if (!currFolder.exists()) {
            try {
                currFolder.create(true, true, null);
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return currFolder;
    }

    /**
     * 
     * DOC zshen Comment method "createFile". create the file under the parentFolder and named for filName
     * 
     * @param parentFolder
     * @param filName
     * @return
     */
    public IFile createFile(IFolder parentFolder, String filName) {
        IFile file = parentFolder.getFile(filName);
        if (!file.exists()) {
            try {
                byte a1[] = new byte[0];
                file.create(new ByteArrayInputStream(a1), true, null);
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }
}
